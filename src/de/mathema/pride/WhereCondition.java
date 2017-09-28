package de.mathema.pride;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * where (age < 18 or age > 64) and status = '5' and blubb in (1, 17, 99)
 * 
 * @author LESS02
 *
 */
public class WhereCondition extends WhereConditionPart {
    public static interface Operator {
        public String EQUAL = "=";
        public String UNEQUAL = "<>";
        public String LESS = "<";
        public String GREATER = ">";
        public String LESSEQUAL = "<=";
        public String GREATEREQUAL = ">=";
        public String LIKE = "LIKE";
        public String BETWEEN = "BETWEEN";
        public String IN = "IN";
        public String NOTIN = "NOT IN";
    }
    
	public static interface ChainOperator {
		public String AND = "AND";
		public String OR = "OR";
	}
	
    public static interface Direction {
    	public String ASC = "ASC";
    	public String DESC = "DESC";
    }

    protected static boolean bindDefault;
    
    public static void setBindDefault(boolean bind) {
    	bindDefault = bind;
    }

    protected WhereCondition parent;
    protected SQLFormatter formatter;
    protected List<WhereConditionPart> parts = new ArrayList<WhereConditionPart>();
    protected String orderBy;
    protected String groupBy;
    protected boolean forUpdate;

    /** Create a new empty WhereCondition */
    public WhereCondition() {
    	this(null, null, null, null);
    }

    /** Create a new empty WhereCondition including an individual SQLFormatter
     * @param formatter A formatter object used to format SQL values and operators. This is only of interest
     *   if the conditions needs an individual formatting which is not already covered by the {@link ResourceAccessor}
     *   in use which is responsible for things like SQL syntax dialects. The formatter passed in here is only
     *   reasonable for application-level aspects of SQL interpretation.
     */
    public WhereCondition(SQLFormatter formatter) {
    	this(null, formatter, null, null);
    }

    public WhereCondition(String initialExpression) {
    	this(null, null, null, initialExpression);
    }

    public WhereCondition(SQLFormatter formatter, String initialExpression) {
    	this(null, formatter, null, initialExpression);
    }

    public WhereCondition(WhereCondition parent) {
    	this(null, null, null, null);
    }
    
    public WhereCondition(WhereCondition parent, SQLFormatter formatter, String chainOperator, String initialExpression) {
    	this.bind = bindDefault;
    	this.chainOperator = chainOperator;
    	this.parent = parent;
    	this.formatter = formatter;
    	if (parent != null)
    		this.bind = parent.bind;
    	if (initialExpression != null)
    		and(initialExpression);
	}

	public WhereCondition withBind() { return withBind(true); }

    public WhereCondition withoutBind() { return withBind(false); }

    public WhereCondition withBind(boolean bind) {
    	this.bind = bind;
    	return this;
    }

    protected String chainIfNotEmpty(String chainOperator) {
		return parts.size() > 0 ? chainOperator : null;
    }

	protected WhereCondition chain(WhereConditionPart part) {
		parts.add(part);
		return this;
	}

	public WhereCondition chain(String chainOperator, WhereConditionPart subcondition) {
		subcondition.chainOperator = chainIfNotEmpty(chainOperator);
		return chain(subcondition);
	}

	protected WhereCondition chain(String chainOperator, boolean skipOnNullValue, String field, String operator, Object... values) {
		if (skipOnNullValue && values[0] == null)
			return this;
		chainOperator = chainIfNotEmpty(chainOperator);
		WhereFieldCondition subcondition = new WhereFieldCondition(chainOperator, bind, field, operator, values);
		return chain(subcondition);
	}

	protected WhereCondition chain(String chainOperation) {
		chainOperation = chainIfNotEmpty(chainOperation);
		WhereCondition subcondition = new WhereCondition(this, formatter, chainOperation, null);
		chain(subcondition);
		return subcondition;
	}
	
	public WhereCondition and(String field, String operator, Object... values) {
		return chain(ChainOperator.AND, false, field, operator, values);
	}

	public WhereCondition andNotNull(String field, String operator, Object... values) {
		return chain(ChainOperator.AND, true, field, operator, values);
	}

	public WhereCondition and(String field, Object value) {
		return and(field, Operator.EQUAL, value);
	}

	public WhereCondition andNotNull(String field, Object value) {
		return andNotNull(field, Operator.EQUAL, value);
	}

	public WhereCondition and(String formattedSubcondition) {
    	if(formattedSubcondition == null || formattedSubcondition.isEmpty())
    		return this;
		return and(formattedSubcondition, null, (Object[])null);
	}

	public WhereCondition and(WhereConditionPart subcondition) {
		return chain(ChainOperator.AND, subcondition);
	}

	public WhereCondition and() {
		return chain(ChainOperator.AND);
	}
	
	public WhereCondition or(String field, String operator, Object... values) {
		return chain(ChainOperator.OR, false, field, operator, values);
	}

	public WhereCondition orNotNull(String field, String operator, Object... values) {
		return chain(ChainOperator.OR, true, field, operator, values);
	}

	public WhereCondition or(String field, Object value) {
		return or(field, Operator.EQUAL, value);
	}

	public WhereCondition orNotNull(String field, Object value) {
		return orNotNull(field, Operator.EQUAL, value);
	}

	public WhereCondition or(String formattedSubcondition) {
		return or(formattedSubcondition, null, (Object[])null);
	}

	public WhereCondition or(WhereConditionPart subcondition) {
		return chain(ChainOperator.OR, subcondition);
	}

	public WhereCondition or() {
		return chain(ChainOperator.OR);
	}
	
	public WhereCondition _() {
		return parent;
	}

	public WhereCondition orderBy(String field, String direction) {
		if (parent != null)
			throw new IllegalArgumentException("subexpression must not include an order clause");
		if (orderBy != null)
			orderBy += ", ";
		else
			orderBy = "";
		orderBy += field + " " + direction;
		return this;
	}
	
	public WhereCondition orderBy(String field) {
		return orderBy(field, "");
	}
	
	public WhereCondition groupBy(String field) {
		if (parent != null)
			throw new IllegalArgumentException("subexpression must not include an order clause");
		if (groupBy != null)
			groupBy += ", ";
		else
			groupBy = "";
		groupBy += field;
		return this;
	}

	public WhereCondition forUpdate() {
		forUpdate = true;
		return this;
	}

	public WhereCondition groupBy(String... fields) {
		for (String field: fields)
			groupBy(field);
		return this;
	}
	
	@Override
	public String toString() {
		return toSQL(formatter);
	}

	@Override
	public String toSQL(SQLFormatter formatter) {
		String s = super.toSQL(formatter) + "( ";
		if (parts.size() == 0) {
			s += "1=1 ";
		}
		else {
			for (WhereConditionPart part: parts) {
				s += part.toSQL(formatter);
			}
		}
		s += ") ";
		if (groupBy != null)
			s += " GROUP BY " + groupBy;
		if (orderBy != null)
			s += " ORDER BY " + orderBy;
		if(forUpdate)
			s += "FOR UPDATE";
		return s;
	}

	protected void bind(SQLFormatter formatter, ConnectionAndStatement cns) throws ReflectiveOperationException {
		bind(formatter, cns, 1);
	}
	
	@Override
	protected int bind(SQLFormatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException {
		for (WhereConditionPart part: parts) {
			nextParam = part.bind(formatter, cns, nextParam);
		}
		return nextParam;
	}

	@Override
	protected boolean requiresBinding() {
		if (bind)
			return true;
		for (WhereConditionPart part: parts) {
			if (part.requiresBinding())
				return true;
		}
		return false;
	}

}
