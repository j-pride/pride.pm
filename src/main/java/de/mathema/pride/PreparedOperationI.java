package de.mathema.pride;

import java.sql.PreparedStatement;

public interface PreparedOperationI {
	public Database getDatabase();
	public PreparedStatement getStatement();
}
