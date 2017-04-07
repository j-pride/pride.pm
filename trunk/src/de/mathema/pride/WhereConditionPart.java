package de.mathema.pride;

import java.sql.PreparedStatement;

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
	
	abstract protected int bind(SQLFormatter formatter, PreparedStatement stmt, int nextParam) throws ReflectiveOperationException;

}
