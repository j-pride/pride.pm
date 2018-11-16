package pm.pride;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedOperationI {
	public Database getDatabase();
	public PreparedStatement getStatement();
	public void setBindParameter(Method setter, int parameterIndex, Object preparedValue) throws ReflectiveOperationException;
	public void setBindParameterNull( int parameterIndex, int columnType) throws SQLException;
}
