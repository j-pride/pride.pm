package de.mathema.pride;

abstract class WhereConditionPart {
	String chainOperator;
	boolean bind;
	
	@Override
	public String toString() {
		return toSQL(null);
	}
	
	public String toSQL(SQLFormatter formatter) {
		return chainOperator != null ? (chainOperator + " ") : "";
	}
	
	protected boolean requiresBinding() {
		return bind;
	}
	
	abstract protected int bind(SQLFormatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException;

}
