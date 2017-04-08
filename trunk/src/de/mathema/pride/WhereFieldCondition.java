package de.mathema.pride;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

class WhereFieldCondition extends WhereConditionPart {
	final String field;
	final String operator;
	final Object value;
	
	public WhereFieldCondition(String chainOperator, boolean bind, String field, String operator, Object value) {
		this.chainOperator = chainOperator;
		this.field = field;
		this.operator = operator;
		this.value = value;
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

	private String formatValue(SQLFormatter formatter) {
		if (operator == null)
			return "";
        if (operator.equals(WhereCondition.Operator.BETWEEN)) {
            return formatSingleValue(Array.get(value, 0), formatter) + " AND " + formatSingleValue(Array.get(value, 1), formatter);
        }
        else if (operator.equals(WhereCondition.Operator.IN) || operator.equals(WhereCondition.Operator.NOTIN)) {
        	String result = "( ";
            for (int i = 0; i < Array.getLength(value); i++) {
                result += formatSingleValue(Array.get(value, i), formatter);
                if (i < Array.getLength(value)-1)
                    result += ", ";
            }
            return result + " )";
        }
        else
            return formatSingleValue(Array.get(value, 0), formatter);
	}

	private String formatSingleValue(Object value, SQLFormatter formatter) {
		if (bind)
			return "?";
		return (formatter == null || (value instanceof SQLRaw)) ? value.toString() : formatter.formatValue(value);
	}

	private String formatOperator(SQLFormatter formatter) {
		if (operator == null)
			return "";
        if (formatter != null)
            return formatter.formatOperator(operator, value);
        return AbstractResourceAccessor.standardOperator(operator, Array.get(value, 0));
	}

	@Override
	protected int bind(SQLFormatter formatter, PreparedStatement stmt, int nextParam)
		throws ReflectiveOperationException {
		if (bind && operator != null) {
			Object preparedValue = formatter.formatPreparedValue(Array.get(value, 0));
			Method setter = PreparedStatementAccess.getAccessMethod(preparedValue.getClass());
			setter.invoke(stmt, nextParam, preparedValue);
			nextParam++;
		}
		return nextParam;
	}
	
}

