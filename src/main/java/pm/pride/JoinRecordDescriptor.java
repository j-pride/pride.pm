package pm.pride;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinRecordDescriptor extends RecordDescriptor {
    public static final int MAX_ALIAS_NAME_LENGTH = 30;
    
    private List<Join> joins = new ArrayList<Join>();
    private RecordDescriptor base;
    private Map<String,String> columnNames = new HashMap<String,String>();
    private int columnCounter = 0;
    
    public JoinRecordDescriptor(RecordDescriptor baseDescriptor, String tableAlias) throws IllegalDescriptorException {
        super(baseDescriptor.getObjectType(), null,
                baseDescriptor.getTableName() + " " +  tableAlias,
                tableAlias, null);
        this.base = baseDescriptor;
        init();
    }

    private void init() {
        attrDescriptors = new ArrayList<AttributeDescriptor>();
        extractAttributeDescriptors(attrDescriptors, base);
    }

    public JoinRecordDescriptor join(RecordDescriptor joinDescriptor, String joinName, String propertyName, String joinOnExpression) {
        Join join = new Join(joinDescriptor, joinName, propertyName, joinOnExpression);
        joins.add(join);
        dbtable += join.getJoinExpression();
        return this;
    }
    
    public JoinRecordDescriptor join(RecordDescriptor joinDescriptor, String joinAndPropertyName, String joinOnExpression) {
        return join(joinDescriptor, joinAndPropertyName, joinAndPropertyName, joinOnExpression);
    }
    
    public JoinRecordDescriptor leftJoin(RecordDescriptor joinDescriptor, String joinName, String propertyName, String joinOnExpression) {
        Join join = new LeftOuterJoin(joinDescriptor, joinName, propertyName, joinOnExpression);
        joins.add(join);
        dbtable += join.getJoinExpression();
        return this;
    }

    public JoinRecordDescriptor leftJoin(RecordDescriptor joinDescriptor, String joinAndPropertyName, String joinOnExpression) {
        return leftJoin(joinDescriptor, joinAndPropertyName, joinAndPropertyName, joinOnExpression);
    }
    
    public JoinRecordDescriptor join(Join join) {
        joins.add(join);
        dbtable += join.getJoinExpression();
        return this;
    }


    private void extractAttributeDescriptors(List<AttributeDescriptor> elements, RecordDescriptor red) {
        elements.addAll(red.attrDescriptors);
        if (red.baseDescriptor != null) {
            extractAttributeDescriptors(elements, red.baseDescriptor);
        }
    }

    

    /** Returns a comma-seperated list of all attribute names
     * as required for an SQL insert operation
     */
    @Override
    public String getFieldNames(String[] excludeAttrs) {
        String names = (baseDescriptor != null) ? baseDescriptor.getFieldNames(excludeAttrs) : "";
        names += getFieldNames(dbtableAlias, attrDescriptors, excludeAttrs);
        
        for (Join join : joins) {
            names += getFieldNames(join.getAlias(), join.getAttributeDescriptors(), excludeAttrs);
        }

        return trim(names);
    }
    
    protected String getFieldName(String alias, AttributeDescriptor attrdesc) {
    	String fullFieldName = super.getFieldName(alias, attrdesc);
    	return fullFieldName  + " as " + getColumnAlias(alias, attrdesc.getFieldName());
    }

    /**
     * Returns the total number of attributes being mapped by this
     * descriptor and all of its base descriptors. This function is
     * useful to skip non-existent parts of an outer join.
     * @return The number of mapped attributes
     */
    @Override
    public int totalAttributes() {
        int attribs = attrDescriptors.size() +
           ((baseDescriptor != null) ? baseDescriptor.totalAttributes() : 0);
        //FIXME joins
        return attribs;
    }
    
    /** Extract result values from a result set according to the field mappings
     * The extraction is by default performed in auto-mode. i.e. first by-name
     * if possible and by-index otherwise. This is the only reliable way in all
     * JDBC drivers to support not only access of single tables but also joins
     * and alias fields. Joins turned out to cause some JDBC drivers to provide
     * no field names at all in the ResultSet. Alias names for function-based
     * fields turned out not to be provided in a reliable order.<br>
     * The index-based extraction requires function {RecordDescriptor#getResultFields}
     * to provide the attributes in the same order they are traversed by this
     * function.
     * @param obj The objet where to transfer the data to
     * @param results The result set to extract the data from
     * @param position The start index for data extraction
     * @return The next index for subsequent extractions or -1 to force
     *   extraction by name (see class {@link AttributeDescriptor} for details).
     */
    @Override
    public int record2object(Object obj, ResultSet results, int position)
        throws SQLException, ReflectiveOperationException {
        if (baseDescriptor != null)
            position = baseDescriptor.record2object(obj, results, position);
        
        for (AttributeDescriptor attrDesc: attrDescriptors)
            position = record2object(obj, results, position, attrDesc);
        
            for (Join join : joins) {
                position = record2child(join, obj, results, position);
            }
        
        return position;
    }
    
    private int record2child(Join join, Object obj, ResultSet results, int position) throws ReflectiveOperationException, SQLException {
        GetterSetterPair childAccess = join.getFieldAccess(obj);
        
        if (!isPrimaryKeyNull(join, results)) {
            Object child = childAccess.get(obj);
            if (child == null) {
                Object parent = childAccess.getDirectOwner(obj);
                child = join.provideChild(parent);
                childAccess.set(obj, child);
            }
            for (AttributeDescriptor attrDesc: join.getAttributeDescriptors()) {
                position = record2object(child, results, position, attrDesc);
            }
        }
        else {
            if (childAccess.getDirectOwner(obj) != null)
                childAccess.set(obj, null);
            position += join.getAttributeDescriptors().size();
        }
        return position;
    }
    
    private boolean isPrimaryKeyNull(Join join, ResultSet results) throws SQLException {
        for (String field: join.getPrimaryKeyFields()) {
            if (results.getObject(field) != null) {
                return false;
            }
		}
		return true;
	}

	private String[] getColumnAlias(String tableAlias, String[] columns) {
        String[] names = new String[columns.length];
        for (int i = 0; i < columns.length; i++) {
            names[i] = getColumnAlias(tableAlias, columns[i]);
		}
        return names;
    }
	
	private String getColumnAlias(String tableAlias, String column) {
        String alias = tableAlias + "_" + column;
        String result = columnNames.get(alias);
        if (result == null) {
            synchronized (columnNames) {
                result = columnNames.get(alias);
                if (result == null) {
                    result = (alias.length() > MAX_ALIAS_NAME_LENGTH) ? alias.substring(0, MAX_ALIAS_NAME_LENGTH) : alias;
                    if (columnNames.containsValue(result)) {
                        String suffix = Integer.toString(columnCounter++);
                        if (result.length() + suffix.length() > MAX_ALIAS_NAME_LENGTH)
                            result = result.substring(0, MAX_ALIAS_NAME_LENGTH - suffix.length());
                        result += suffix;    
                    }
                    columnNames.put(alias, result);
                }
            }
        }
        return result;
    }

    public class Join {
        private List<AttributeDescriptor> attributeDescriptors;
        protected String alias;
        protected String joinOnExpression;
        private String propertyName;
        private GetterSetterPair propertyAccess;
        private Class objectType;
        private String[] primaryKeyFields;
        protected RecordDescriptor joinDescriptor;
        private Constructor<?> childConstructor;
        protected Object lastParent, lastChild;
        
        public Join(RecordDescriptor joinDescriptor, String alias, String propertyName, String joinOnExpression) {
            this.propertyName = propertyName;
            this.joinOnExpression = joinOnExpression;
            this.alias = alias;
            this.joinDescriptor = joinDescriptor;

            this.attributeDescriptors = new ArrayList<AttributeDescriptor>();
            extractAttributeDescriptors(attributeDescriptors, joinDescriptor);

            this.objectType = joinDescriptor.getObjectType();
            this.primaryKeyFields = getColumnAlias(alias, joinDescriptor.getPrimaryKeyFields());;
        }
        
        public GetterSetterPair getFieldAccess(Object obj) {
            if (propertyAccess == null) {
                propertyAccess = new GetterSetterPair(obj.getClass(), propertyName);
            }
            return propertyAccess;
        }

        public List<AttributeDescriptor> getAttributeDescriptors() {
            return attributeDescriptors;
        }

        public String getAlias() {
            return alias;
        }

        public String getJoinExpression() {
            return " join " + joinDescriptor.getTableName() + " " +  alias + " on " + joinOnExpression;
        }
        
        public String[] getPrimaryKeyFields(){
            return primaryKeyFields;
        }
        
        public Object provideChild(Object parent) throws ReflectiveOperationException {
            if (parent != lastParent) {
                lastParent = parent;
                lastChild = createChild(parent);
            }
            return lastChild;
        }

        private Object createChild(Object parent) throws ReflectiveOperationException {
            if (childConstructor == null) {
                extractChildConstructor(parent);
            }
            return childConstructor.getParameterCount() == 0 ?
                    childConstructor.newInstance() :
                    childConstructor.newInstance(parent);
        }

        private void extractChildConstructor(Object parent) {
            Constructor<?>[] constructors = objectType.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parameters = constructor.getParameterTypes();
                if (parameters.length == 1 && parameters[0].isAssignableFrom(parent.getClass())) {
                    childConstructor = constructor;
                }
                else if (parameters.length == 0 && childConstructor == null) {
                    childConstructor = constructor;
                }
            }
            if (childConstructor == null)
                throw new RuntimeException("no matching constructor found");
        }
    }
    
    public class LeftOuterJoin extends Join {
        public LeftOuterJoin(RecordDescriptor joinDescriptor, String alias, String propertyName, String joinExpression) {
            super(joinDescriptor, alias, propertyName, joinExpression);
        }

        @Override
        public String getJoinExpression() {
            return " left join " + joinDescriptor.getTableName() + " " +  alias + " on " + joinOnExpression;
        }
    }
}
