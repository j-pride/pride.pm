package pm.pride;

class WhereFieldCondition extends WhereConditionPart {

	final WhereField field;
	final String operator;
	final WhereValues values;
	
	public WhereFieldCondition(String chainOperator, Boolean bind, WhereField field, String operator, WhereValues values) {
		this.chainOperator = chainOperator;
		this.field = field;
		this.operator = operator;
		this.values = values;
		this.bind = bind;
	}
	
	protected String toSQL(SQL.Formatter formatter, String defaultTablePrefix, boolean ignoreBindings) {
		boolean withBinding = ignoreBindings ? false : requiresBinding(formatter);
		return toSQLChainer(formatter) +
				field.determineQualifiedField(defaultTablePrefix) + " " +
				formatOperator(operator,values, formatter) + " " +
				values.formatValueOrFunction(operator, withBinding, formatter) + " ";
	}

  private static String formatOperator(String operator, WhereValues values, SQL.Formatter formatter) {
    if (operator == null) {
      return "";
    }
    Object rawValue = values.rawValue();
    if (formatter != null) {
      return formatter.formatOperator(operator, rawValue);
    }
    return AbstractResourceAccessor.standardOperator(operator, rawValue);
  }

	@Override
	protected int bind(SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam)
		throws ReflectiveOperationException {
		if (requiresBinding(formatter) && operator != null && values != null) {
      nextParam = values.bind(formatter, cns, nextParam);
		}
		return nextParam;
	}


}

