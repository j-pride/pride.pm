package de.mathema.pride;

import java.sql.PreparedStatement;

import de.mathema.pride.Database.ConnectionAndStatement;

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
