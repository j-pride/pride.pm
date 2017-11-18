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
	
	protected String toSQL(SQLFormatter formatter, boolean ignoreBindings) {
		boolean withBinding = ignoreBindings ? false : bind;
		return toSQLChainer(formatter) +
				field + " " +
				formatOperator(operator, values, formatter) + " " +
				formatValue(values, operator, withBinding, formatter) + " ";
	}

	private static Object value0(Object[] values) {
		return value(0, values);
	}
	
	private static Object value(int index, Object[] values ) {
		if (values == null)
			return null;

		return values[index];
	}
	
	private static int numValues(Object[] values) {
		return values.length;
	}
	
	private static String formatValue(Object[] values, String operator, boolean withBinding, SQLFormatter formatter) {
		if (operator == null)
			return "";
		switch (operator) {
			case WhereCondition.Operator.BETWEEN:
				return formatSingleValue(value0(values), withBinding, formatter) + " AND " + formatSingleValue(value(1, values), withBinding, formatter);
			case WhereCondition.Operator.IN:
			case WhereCondition.Operator.NOTIN:
				StringBuilder result = new StringBuilder("( ");
				for (int i = 0; i < numValues(values); i++) {
					result.append(formatSingleValue(value(i, values), withBinding, formatter));
					if (i < numValues(values) - 1)
						result.append(", ");
				}
				return result + " )";
			default:
				return formatSingleValue(value0(values), withBinding, formatter);
		}
	}

	private static String formatSingleValue(Object value, boolean withBinding, SQLFormatter formatter) {
		if (withBinding && value != null)
			return "?";
		if (formatter == null || (value instanceof SQLRaw)) {
			return (value == null) ? "null" : value.toString();
		}
		return formatter.formatValue(value);
	}

	private static String formatOperator(String operator, Object[] values, SQLFormatter formatter) {
		if (operator == null)
			return "";
        if (formatter != null)
            return formatter.formatOperator(operator, value0(values));
        return AbstractResourceAccessor.standardOperator(operator, value0(values));
	}

	@Override
	protected int bind(SQLFormatter formatter, ConnectionAndStatement cns, int nextParam)
		throws ReflectiveOperationException {
		if (bind && operator != null && values != null) {
			for(Object aValue : values) {
				nextParam = bindSingleValue(aValue, formatter, cns, nextParam);
			}
		}
		return nextParam;
	}

	private static int bindSingleValue(Object value, SQLFormatter formatter, ConnectionAndStatement cns, int nextParam)
		throws ReflectiveOperationException {

		if(value == null) return nextParam;

		Object preparedValue = formatter.formatPreparedValue(value);
		Method setter = PreparedStatementAccess.getAccessMethod(preparedValue.getClass());
		cns.setBindParameter(setter, nextParam, preparedValue);
		nextParam++;

		return nextParam;
	}
}

