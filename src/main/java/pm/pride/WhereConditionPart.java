package pm.pride;

abstract class WhereConditionPart {
	String chainOperator;
	boolean bind;
	
	@Override
	public String toString() {
		return toSQL(null);
	}

	public String toSQLChainer(SQL.Formatter formatter) {
		return chainOperator != null ? (chainOperator + " ") : "";
	}

	public String toSQLIgnoreBindings(SQL.Formatter formatter) {
		return toSQL(formatter, true);
	}

	public String toSQL(SQL.Formatter formatter) {
		return toSQL(formatter, false);
	}

	protected abstract String toSQL(SQL.Formatter formatter, boolean withBinding);

	protected boolean requiresBinding() {
		return bind;
	}

	abstract protected int bind(SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException;
}
