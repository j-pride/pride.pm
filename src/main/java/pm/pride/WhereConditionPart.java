package pm.pride;

abstract class WhereConditionPart {
	String chainOperator;
	Boolean bind; // true = with bind variable, false = without, null = like default
	
	@Override
	public String toString() {
		return toSQL(null, null);
	}

	public String toSQLChainer(SQL.Formatter formatter) {
		return chainOperator != null ? (chainOperator + " ") : "";
	}

	public String toSQLIgnoreBindings(SQL.Formatter formatter, String defaultTablePrefix) {
		return toSQL(formatter, defaultTablePrefix, true);
	}

	public String toSQL(SQL.Formatter formatter, String defaultTablePrefix) {
		return toSQL(formatter, defaultTablePrefix, false);
	}

	protected abstract String toSQL(SQL.Formatter formatter, String defaultTablePrefix, boolean withBinding);

	protected boolean requiresBinding(SQL.Formatter formatter) {
		if (bind != null)
			return bind;
		if (formatter != null)
			return formatter.bindvarsByDefault();
		return false;
	}

	abstract protected int bind(SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException;
}
