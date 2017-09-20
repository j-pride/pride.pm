package de.mathema.pride;

import java.lang.reflect.Method;

class WhereFieldCondition extends WhereConditionPart {
	final String field;
	final String operator;
	final Object[] values;
	
	public WhereFieldCondition(String chainOperator, boolean bind, String field, String operator, Object... values) {
		this.chainOperator = chainOperator;
		this.field = field;
		this.operator = operator;
		this.values = values;
		this.bind = bind;
	}
	
	@Override
	public String toString() {
		return toSQL(null);
	}

	
	@Override
	public String toSQL(SQLFormatter formatter) {
		return super.toSQL(formatter) +
				field + " " + formatOperator(formatter) + " " + formatValue(formatter) + " ";
	}

	private Object value0() {
		if(values == null)
			return null;

		return values[0];
	}
	
	private Object value(int index) {
		return values[index];
	}
	
	private int numValues() {
		return values.length;
	}
	
	private String formatValue(SQLFormatter formatter) {
		if (operator == null)
			return "";
        if (operator.equals(WhereCondition.Operator.BETWEEN)) {
            return formatSingleValue(value0(), formatter) + " AND " + formatSingleValue(value(1), formatter);
        }
        else if (operator.equals(WhereCondition.Operator.IN) || operator.equals(WhereCondition.Operator.NOTIN)) {
        	String result = "( ";
            for (int i = 0; i < numValues(); i++) {
                result += formatSingleValue(value(i), formatter);
                if (i < numValues()-1)
                    result += ", ";
            }
            return result + " )";
        }
        else
            return formatSingleValue(value0(), formatter);
	}

	private String formatSingleValue(Object value, SQLFormatter formatter) {
		if (bind && value != null)
			return "?";
		if (formatter == null || (value instanceof SQLRaw)) {
			return (value == null) ? "null" : value.toString();
		}
		return formatter.formatValue(value);
	}

	private String formatOperator(SQLFormatter formatter) {
		if (operator == null)
			return "";
        if (formatter != null)
            return formatter.formatOperator(operator, value0());
        return AbstractResourceAccessor.standardOperator(operator, value0());
	}

	@Override
	protected int bind(SQLFormatter formatter, ConnectionAndStatement cns, int nextParam)
		throws ReflectiveOperationException {
		if (bind && operator != null && value0() != null) {
			Object preparedValue = formatter.formatPreparedValue(value0());
			Method setter = PreparedStatementAccess.getAccessMethod(preparedValue.getClass());
			cns.setBindParameter(setter, nextParam, preparedValue);
			nextParam++;
		}
		return nextParam;
	}
	
}

