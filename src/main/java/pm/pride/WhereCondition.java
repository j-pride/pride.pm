package pm.pride;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class for simplified assembly of where conditions. The values can
 * individually by bound to variables, causing PriDE to execute queries based
 * in this where condition as a prepared statement.
 * <p>
 * (age &lt; 18 or age &gt; 64) and status = '5' and foo in (1, 17, 99)
 * 
 * @author LESS02
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

    protected WhereCondition parent;
    protected SQL.Formatter formatter;
    protected List<WhereConditionPart> parts = new ArrayList<WhereConditionPart>();
    protected String orderBy;
    protected String groupBy;
    protected boolean forUpdate;

    /** Create a new empty WhereCondition */
    public WhereCondition() {
    	this(null, null, null, null);
    }

    /** Create a new empty WhereCondition including an individual SQL.Formatter
     * @param formatter A formatter object used to format SQL values and operators. This is only of interest
     *   if the conditions needs an individual formatting which is not already covered by the {@link ResourceAccessor}
     *   in use which is responsible for things like SQL syntax dialects. The formatter passed in here is only
     *   reasonable for application-level aspects of SQL interpretation.
     */
    public WhereCondition(SQL.Formatter formatter) {
    	this(null, formatter, null, null);
    }

    public WhereCondition(String initialExpression) {
    	this(null, null, null, initialExpression);
    }

    public WhereCondition(SQL.Formatter formatter, String initialExpression) {
    	this(null, formatter, null, initialExpression);
    }

    public WhereCondition(WhereCondition parent) {
    	this(null, null, null, null);
    }
    
    public WhereCondition(WhereCondition parent, SQL.Formatter formatter, String chainOperator, String initialExpression) {
    	this.chainOperator = chainOperator;
    	this.parent = parent;
    	this.formatter = formatter;
    	if (parent != null)
    		this.bind = parent.bind;
    	if (initialExpression != null)
    		and(initialExpression);
	}

	public WhereCondition(String field, Object value) {
    	this(null, null, null, null);
    	and(field, value);
	}
	
	public WhereCondition(String field, String operator, Object... values) {
    	this(null, null, null, null);
    	and(field, operator, values);
	}

	public WhereCondition(SqlFunction functionField, Object value) {
    	this(null, null, null, null);
    	and(functionField, value);
	}
	
	public WhereCondition(SqlFunction functionField, String operator, Object... values) {
    	this(null, null, null, null);
    	and(functionField, operator, values);
	}
	
	public WhereCondition(String field, SqlFunction functionValue) {
    	this(null, null, null, null);
    	and(field, functionValue);
	}
	
	public WhereCondition(String field, String operator, SqlFunction functionValue) {
    	this(null, null, null, null);
    	and(field, operator, functionValue);
	}
	
	public WhereCondition(SqlFunction functionField, SqlFunction functionValue) {
    	this(null, null, null, null);
    	and(functionField, functionValue);
	}
	
	public WhereCondition(SqlFunction functionField, String operator, SqlFunction functionValue) {
    	this(null, null, null, null);
    	and(functionField, operator, functionValue);
	}
	
	public WhereCondition bindvarsOn() { return bindvars(true); }

    public WhereCondition bindvarsOff() { return bindvars(false); }

    public WhereCondition bindvarsDefault() { return bindvars(null); }

    public WhereCondition bindvars(Boolean bind) {
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
    return chain(chainOperator, skipOnNullValue, operator, new WhereFieldConditionField(field), new WhereFieldConditionValues(values));
  }

  protected WhereCondition chain(String chainOperator, boolean skipOnNullValue, WhereFieldConditionField field, String operator, Object... values) {
    return chain(chainOperator, skipOnNullValue, operator, field, new WhereFieldConditionValues(values));
  }

  protected WhereCondition chain(String chainOperator, boolean skipOnNullValue, String field, String operator, WhereFieldConditionValues values) {
    return chain(chainOperator, skipOnNullValue, operator, new WhereFieldConditionField(field), values);
  }

  private WhereCondition chain(String chainOperator, boolean skipOnNullValue, String operator, WhereFieldConditionField field, WhereFieldConditionValues values) {
    if (skipOnNullValue && (values == null || values.hasNoValues())) {
      return this;
    }
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

	/**
	 * Adds a field sub condition which is AND-concatenated with what is already present in this WhereCondition. If the WhereCondition
	 *   is empty, the method has nothing to concatenate. It therefore doesn't matter if you start the condition assembly with and... methods
	 *   or or... methods.
	 * @param field The field to operate on
	 * @param operator The operator to apply. Supported values are listed in sub-interface {@link Operator}. Operator {@link Operator#EQUAL}
	 *    combines with a null value will assemble to "IS NULL", operator {@link Operator#UNEQUAL} to "IS NOT NULL".
	 * @param values The value to apply. In case of operator {@link Operator#BETWEEN}, the method expects exactly two values. In case of operator
	 *    {@link Operator#IN}, the values have an unlimited length. All other operators expect a simgle value.
	 * @return  This WhereCondition itself to allow condition assembly in a fluent way.
	 */
	public WhereCondition and(String field, String operator, Object... values) {
		return chain(ChainOperator.AND, false, field, operator, values);
	}
  
	/**
	 * Like {@link #and(String, String, Object...)} but only adds the sub condition if the first value is not null.
	 */
	public WhereCondition andNN(String field, String operator, Object... values) {
		return chain(ChainOperator.AND, true, field, operator, values);
	}

	/**
	 * Like {@link #and(String, String, Object...)} with {@link Operator#EQUAL}
	 */
	public WhereCondition and(String field, Object value) {
		return and(field, Operator.EQUAL, value);
	}

	/**
	 * Like {@link #andNN(String, String, Object...)} with {@link Operator#EQUAL}
	 */
	public WhereCondition andNN(String field, Object value) {
		return andNN(field, Operator.EQUAL, value);
	}

	/**
	 * Adds the passed pre-formatted sub condition to this WhereCondition. This is a kind of 'last resort' for complicated situations.
	 * Keep in mind that the sub condition is plain SQL text which therefore cannot support bind variables.
	 * @return This WhereCondition itself to allow condition assembly in a fluent way.
	 */
	public WhereCondition and(String formattedSubcondition) {
    	if(formattedSubcondition == null || formattedSubcondition.isEmpty())
    		return this;
		return and(formattedSubcondition, null, (Object[])null);
	}

	public WhereCondition and(WhereConditionPart subcondition) {
		return chain(ChainOperator.AND, subcondition);
	}

	/**
	 * Initiates a sub condition which is AND-concatenated with what is already present in this WhereCondition.
	 * @return The sub condition. You can switch back to the parent condition by closing the sub condition using
	 *    method {@link #bracketClose()}.
	 */
	public WhereCondition and() {
		return chain(ChainOperator.AND);
	}
	
	/**
	 * Like {@link #and(String, String, Object...)} but performs an OR-concatenation.
	 */
	public WhereCondition or(String field, String operator, Object... values) {
		return chain(ChainOperator.OR, false, field, operator, values);
	}

	/**
	 * Like {@link #andNN(String, String, Object...)} but performs an OR-concatenation.
	 */
	public WhereCondition orNotNull(String field, String operator, Object... values) {
		return chain(ChainOperator.OR, true, field, operator, values);
	}

	/**
	 * Like {@link #and(String, Object)} but performs an OR-concatenation.
	 */
	public WhereCondition or(String field, Object value) {
		return or(field, Operator.EQUAL, value);
	}

	/**
	 * Like {@link #andNN(String, Object)} but performs an OR-concatenation.
	 */
	public WhereCondition orNotNull(String field, Object value) {
		return orNotNull(field, Operator.EQUAL, value);
	}

	/**
	 * Like {@link #and(String)} but performs an OR-concatenation.
	 */
	public WhereCondition or(String formattedSubcondition) {
		return or(formattedSubcondition, null, (Object[])null);
	}

	public WhereCondition or(WhereConditionPart subcondition) {
		return chain(ChainOperator.OR, subcondition);
	}

	/**
	 * Like {@link #and()} but performs an OR-concatenation for the sub condition.
	 */
	public WhereCondition or() {
		return chain(ChainOperator.OR);
	}

  /**
   * ANDs a condition where the left-hand side is an SQL function applied to a field and the right-hand side
   * consists of plain value(s).
   * <p>
   * This behaves like {@link #and(String, String, Object...)} but allows using an {@link SqlFunction}
   * as the field expression, e.g. {@code and(upper("last_name"), Operator.EQUAL, "DOE")}.
   *
   * @param functionField the {@link SqlFunction} representing the field expression (e.g. {@link SqlFunction#upper(String)}).
   *                      The function's parameter must be the field name to which the function is applied.
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}, {@link Operator#LIKE}, {@link Operator#BETWEEN}.
   * @param values the value(s) to compare against, same semantics as in {@link #and(String, String, Object...)}.
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition and(SqlFunction functionField, String operator, Object... values) {
    return chain(ChainOperator.AND, false,
      new WhereFieldConditionField(functionField), operator, values);
  }
  
  /**
   * Like {@link #and(SqlFunction, String, Object...)} but performs an OR-concatenation.
   *
   * @param functionField the {@link SqlFunction} representing the left-hand field expression
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}
   * @param values the value(s) to compare against
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition or(SqlFunction functionField, String operator, Object... values) {
    return chain(ChainOperator.OR, false,
      new WhereFieldConditionField(functionField), operator, values);
  }

  /**
   * ANDs a condition where the left-hand side is a plain field and the right-hand side is an SQL function value.
   * <p>
   * This behaves like {@link #and(String, String, Object...)} but allows using an {@link SqlFunction}
   * as the value expression, e.g. {@code and("name", Operator.EQUAL, upper(?))} with a bound parameter.
   *
   * @param field the database field name on the left-hand side
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}, {@link Operator#LIKE}
   * @param functionValue the {@link SqlFunction} producing the value for comparison
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition and(String field, String operator, SqlFunction functionValue) {
    return chain(ChainOperator.AND, false,
      field, operator, new WhereFieldConditionValues(functionValue));
  }

  /**
   * Like {@link #and(String, String, SqlFunction)} but performs an OR-concatenation.
   *
   * @param field the database field name on the left-hand side
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}
   * @param functionValue the {@link SqlFunction} producing the value for comparison
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition or(String field, String operator, SqlFunction functionValue) {
    return chain(ChainOperator.OR, false,
      field, operator, new WhereFieldConditionValues(functionValue));
  }

  /**
   * ANDs a condition where both sides are SQL function expressions.
   * <p>
   * This is the function-to-function counterpart of {@link #and(String, String, Object...)} allowing
   * you to write conditions like {@code and(upper("last_name"), Operator.EQUAL, trim(?))}.
   *
   * @param functionField the {@link SqlFunction} to be used as field expression on the left-hand side
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}
   * @param functionValue the {@link SqlFunction} providing the right-hand side value expression
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition and(SqlFunction functionField, String operator, SqlFunction functionValue) {
    return chain(ChainOperator.AND, false,
      operator,
      new WhereFieldConditionField(functionField),
      new WhereFieldConditionValues(functionValue));
  }

  /**
   * Like {@link #and(SqlFunction, String, SqlFunction)} but performs an OR-concatenation.
   *
   * @param functionField the {@link SqlFunction} to be used as the left-hand field expression
   * @param operator the comparison operator, e.g. {@link Operator#EQUAL}
   * @param functionValue the {@link SqlFunction} providing the right-hand side value expression
   * @return this {@link WhereCondition} to allow fluent chaining
   */
  public WhereCondition or(SqlFunction functionField, String operator, SqlFunction functionValue) {
    return chain(ChainOperator.OR, false,
      operator,
      new WhereFieldConditionField(functionField),
      new WhereFieldConditionValues(functionValue));
  }

  /**
   * Like {@link #and(SqlFunction, String, Object...)} with {@link Operator#EQUAL}
   */
  public WhereCondition and(SqlFunction functionField, Object value) {
    return and(functionField, Operator.EQUAL, value);
  }

  /**
   * Like {@link #and(String, String, SqlFunction)} with {@link Operator#EQUAL}
   */
  public WhereCondition and(String field, SqlFunction functionValue) {
    return and(field, Operator.EQUAL, functionValue);
  }

  /**
   * Like {@link #and(SqlFunction, String, SqlFunction)} with {@link Operator#EQUAL}
   */
  public WhereCondition and(SqlFunction functionField, SqlFunction functionValue) {
    return and(functionField, Operator.EQUAL, functionValue);
  }

  /**
   * Like {@link #or(SqlFunction, String, Object...)} with {@link Operator#EQUAL}
   */
  public WhereCondition or(SqlFunction functionField, Object value) {
    return or(functionField, Operator.EQUAL, value);
  }

  /**
   * Like {@link #or(String, String, SqlFunction)} with {@link Operator#EQUAL}
   */
  public WhereCondition or(String field, SqlFunction functionValue) {
    return or(field, Operator.EQUAL, functionValue);
  }

  /**
   * Like {@link #or(SqlFunction, String, SqlFunction)} with {@link Operator#EQUAL}
   */
  public WhereCondition or(SqlFunction functionField, SqlFunction functionValue) {
    return or(functionField, Operator.EQUAL, functionValue);
  }
	
	public WhereCondition bracketClose() {
		return parent;
	}

	/**
	 * Adds an order by clause to this WhereCondition. You may call this method several times, producing a comma-separated concatenation of
	 * all ordering constraints. E.g.
	 * <p>
	 * <pre>where.orderBy("last_name", ASC).orderBy("first_name", ASC);</pre>
	 * <p>
	 * Causes an SQL ordering clause like
	 * <p>
	 * <pre>order by last_name ASC, first_name DESC</pre>
	 * <p>
	 * @param field The field to order the output by
	 * @param direction The direction, usually either {@link Direction#ASC} or {@link Direction#DESC}
	 * @return This WhereCondition itself to allow condition assembly in a fluent way
	 */
	public WhereCondition orderBy(String field, String direction) {
		if (parent != null)
			throw new IllegalArgumentException("sub expression must not include an order clause");
		if (orderBy != null)
			orderBy += ", ";
		else
			orderBy = "";
		orderBy += field + " " + direction;
		return this;
	}

	/**
	 * Like {@link #orderBy(String, String)} without explicitly specifying an order direction which in standard SQL databases means ascending order
	 */
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
		return toSQL(formatter, null);
	}

	@Override
	protected String toSQL(SQL.Formatter formatter, String defaultTablePrefix, boolean ignoreBindings) {
		String s = toSQLChainer(formatter) + "( ";
		if (parts.size() == 0) {
			s += "1=1 ";
		}
		else {
			for (WhereConditionPart part: parts) {
				s += part.toSQL(formatter, defaultTablePrefix, ignoreBindings);
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

	protected void bind(SQL.Formatter inheritedFormatter, ConnectionAndStatement cns) throws ReflectiveOperationException {
		bind(formatter != null ? formatter : inheritedFormatter, cns, 1);
	}
	
	@Override
	protected int bind(SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException {
		for (WhereConditionPart part: parts) {
			nextParam = part.bind(formatter, cns, nextParam);
		}
		return nextParam;
	}

	@Override
	protected boolean requiresBinding(SQL.Formatter formatter) {
		if (super.requiresBinding(formatter))
			return true;
		for (WhereConditionPart part: parts) {
			if (part.requiresBinding(formatter))
				return true;
		}
		return false;
	}

}
