package de.mathema.pride;

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
    private String tableAlias;
    private RecordDescriptor base;
    private Map<String,String> columnNames = new HashMap<String,String>();
    private int columnCounter = 0;
    
    public JoinRecordDescriptor(RecordDescriptor baseDescriptor, String tableAlias) throws IllegalDescriptorException {
        super(baseDescriptor.getObjectType(),
                baseDescriptor.getTableName() + " " +  tableAlias,
                null, null);
        this.tableAlias = tableAlias;
        this.base = baseDescriptor;
        init();
    }

    private void init() {
        List<AttributeDescriptor> elements = new ArrayList<AttributeDescriptor>();
        extractAttributeDescriptors(elements, base);
                
        attrDescriptors = elements.toArray(new AttributeDescriptor[0]);
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
        AttributeDescriptor[] desc = red.attrDescriptors;
        elements.addAll(Arrays.asList(desc));
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
        names += getFieldNames(tableAlias, attrDescriptors, excludeAttrs);
        
        for (Join join : joins) {
            names += getFieldNames(join.getAlias(), join.getAttributeDescriptors(), excludeAttrs);
        }

        return trim(names);
    }
    
    private String getFieldNames(String alias, AttributeDescriptor[] attrDescriptors, String[] excludeAttrs) {
        String names = ""; 
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false)) {
                names += "," + alias + "." + attrDescriptors[i].getFieldName() + " as " + getColumnAlias(alias, attrDescriptors[i].getFieldName());
            }
        }
        return names;
    }   

    /**
     * Returns the total number of attributes being mapped by this
     * descriptor and all of its base descriptors. This function is
     * useful to skip non-existent parts of an outer join.
     * @return The number of mapped attributes
     */
    @Override
    public int totalAttributes() {
        int attribs = attrDescriptors.length +
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
        
        for (int i = 0; i < attrDescriptors.length; i++)
            position = record2object(obj, results, position, attrDescriptors[i]);
        
            for (Join join : joins) {
                position = record2child(join, obj, results, position);
            }
        
        return position;
    }
    
    private int record2child(Join join, Object obj, ResultSet results, int position) throws ReflectiveOperationException, SQLException {
        GetterSetterPair childAccess = join.getFieldAccess(obj);
        
        if (results.getObject(join.getPrimaryKeyFiled()) != null) {
            Object child = childAccess.get(obj);
            if (child == null) {
                Object parent = childAccess.getDirectOwner(obj);
                child = join.provideChild(parent);
                childAccess.set(obj, child);
            }
            AttributeDescriptor[] desc = join.getAttributeDescriptors();
            for (int i = 0; i < desc.length; i++) {
                position = record2object(child, results, position, desc[i]);
            }
        }
        else {
            if (childAccess.getDirectOwner(obj) != null)
                childAccess.set(obj, null);
            position += join.getAttributeDescriptors().length;
        }
        return position;
    }
    
    private String getColumnAlias(String tableAlias, String column) {
        String alias = tableAlias + "_" + column;
        String result = columnNames.get(alias);
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
        return result;
    }

    public class Join {
        private AttributeDescriptor[] attributeDescriptors;
        protected String alias;
        protected String joinOnExpression;
        private String propertyName;
        private GetterSetterPair propertyAccess;
        private Class objectType;
        private String primaryKeyField;
        protected RecordDescriptor joinDescriptor;
        private Constructor<?> childConstructor;
        protected Object lastParent, lastChild;
        
        public Join(RecordDescriptor joinDescriptor, String alias, String propertyName, String joinOnExpression) {
            this.propertyName = propertyName;
            this.joinOnExpression = joinOnExpression;
            this.alias = alias;
            this.joinDescriptor = joinDescriptor;

            List<AttributeDescriptor> elements = new ArrayList<AttributeDescriptor>();
            extractAttributeDescriptors(elements, joinDescriptor);

            this.attributeDescriptors = elements.toArray(new AttributeDescriptor[0]);
            this.objectType = joinDescriptor.getObjectType();
            this.primaryKeyField = getColumnAlias(alias, joinDescriptor.getPrimaryKeyField());;
        }
        
        public GetterSetterPair getFieldAccess(Object obj) {
            if (propertyAccess == null) {
                propertyAccess = new GetterSetterPair(obj.getClass(), propertyName);
            }
            return propertyAccess;
        }

        public AttributeDescriptor[] getAttributeDescriptors() {
            return attributeDescriptors;
        }

        public String getAlias() {
            return alias;
        }

        public String getJoinExpression() {
            return " join " + joinDescriptor.getTableName() + " " +  alias + " on " + joinOnExpression;
        }
        
        public String getPrimaryKeyFiled(){
            return primaryKeyField;
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
