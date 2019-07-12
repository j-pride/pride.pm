package pm.pride.util.generator;

import pm.pride.Database;

public class PreparedStatementLogger {
	private final String statementContent;
	private final Database database;
	private StringBuffer logBuffer;
	private int logPointer = 0;
	
	public PreparedStatementLogger(Database database, String statementContent) {
    	this.database = database;
		this.statementContent = statementContent;
		reset();
	}

	public void reset() {
		if (database.isLogging()) {
			logPointer = 0;
			logBuffer = new StringBuffer();
			scrollLogToNextBinding();
		}
	}
	
	public void logBindingAndScroll(Object boundValue, int parameterIndex, Class<?> targetType) {
		if (database.isLogging()) {
			logBuffer.append(database.formatValue(boundValue, targetType, true));
			scrollLogToNextBinding();
		}
	}

	private void scrollLogToNextBinding() {
		if (database.isLogging()) {
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

}
