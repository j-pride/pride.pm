package pm.pride;

abstract class WhereConditionPart {
	String chainOperator;
	boolean bind;
	
	@Override
	public String toString() {
		return toSQL(null);
	}

	public String toSQLChainer(SQLFormatter formatter) {
		return chainOperator != null ? (chainOperator + " ") : "";
	}

	public String toSQLIgnoreBindings(SQLFormatter formatter) {
		return toSQL(formatter, true);
	}

	public String toSQL(SQLFormatter formatter) {
		return toSQL(formatter, false);
	}

	protected abstract String toSQL(SQLFormatter formatter, boolean withBinding);

	protected boolean requiresBinding() {
		return bind;
	}

	abstract protected int bind(SQLFormatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException;
}
