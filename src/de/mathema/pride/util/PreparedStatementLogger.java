package de.mathema.pride.util;

import de.mathema.pride.Database;

public class PreparedStatementLogger {
	private final StringBuffer logBuffer;
	private int logPointer = 0;
	private String statementContent;
	private Database database;
	
	public PreparedStatementLogger(Database database, String statementContent) {
    	this.database = database;
		this.statementContent = statementContent;
		logBuffer = new StringBuffer();
    	scrollLogToNextBinding();
	}

	public void logBindingAndScroll(Object boundValue, int parameterIndex) {
		logBuffer.append(database.formatValue(boundValue));
		scrollLogToNextBinding();
	}

	private void scrollLogToNextBinding() {
		int nextBinding = statementContent.indexOf("?", logPointer);
		if (nextBinding == -1) {
			logBuffer.append(statementContent.substring(logPointer));
			database.sqlLog(logBuffer.toString());
		}
		else {
			logBuffer.append(statementContent.substring(logPointer, nextBinding+1));
			logPointer = nextBinding+1;
		}
	}

}
