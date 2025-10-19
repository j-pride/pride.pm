package pm.pride;

/**
 * Represents an SQL function and its parameter.
 * This class provides the ability to encapsulate SQL function calls with
 * their respective function name and parameter, allowing integration with
 * database operations.
 */
public class SqlFunction {

  String function;
  Object parameter;

  public static SqlFunction function(String functionName, Object value) {
    return new SqlFunction(functionName, value);
  }

  /**
   * Constructs an SQL "UPPER" function that converts the provided value to uppercase in SQL execution.
   *
   * @param value The value to be converted to uppercase. This can typically be a column name or a literal value.
   * @return An instance of {@code SqlFunction} encapsulating the SQL "UPPER" function.
   */
  public static SqlFunction upper(String value) {
    return function(SqlFunctionType.UPPER.getFunctionName(), value);
  }

  /**
   * Constructs an SQL "LOWER" function that converts the provided value to lowercase in SQL execution.
   *
   * @param value The value to be converted to lowercase. This can typically be a column name
   *              or a literal value.
   * @return An instance of {@code SqlFunction} encapsulating the SQL "LOWER" function.
   */
  public static SqlFunction lower(String value) {
    return function(SqlFunctionType.LOWER.getFunctionName(), value);
  }

  /**
   * Removes leading whitespace or specified characters from a given value.
   *
   * @param value the input object to be trimmed; it is expected to be of a type
   *              compatible with the SQL function requirements, such as a String
   *              or column reference.
   * @return an SqlFunction representing the*/
  public static SqlFunction ltrim(String value) {
    return function(SqlFunctionType.LTRIM.getFunctionName(), value);
  }

  /**
   * Trims the given value by applying the SQL TRIM function.
   *
   * @param value the value to be trimmed; should typically be a string
   *              or a valid expression supported by SQL functions.
   * @return the {@code SqlFunction} that represents the SQL TRIM function
   *         applied to the given value.
   */
  public static SqlFunction trim(Object value) {
    return function(SqlFunctionType.TRIM.getFunctionName(), value);
  }

  /**
   * Removes trailing spaces or specified characters from the right end of a given value.
   *
   * @param value The object representing the value to be trimmed on the right.
   * @return An instance of SqlFunction representing the RTRIM function applied to the given*/
  public SqlFunction rtrim(String value) {
    return SqlFunction.function(SqlFunctionType.RTRIM.getFunctionName(), value);
  }

  SqlFunction() {
  }

  SqlFunction(String function, Object parameter) {
    this.function = function;
    this.parameter = parameter;
  }

  SqlFunction(SqlFunctionType functionType, Object parameter) {
    this(functionType.getFunctionName(), parameter);
  }

  String getFunction() {
    return function;
  }

  Object getParameter() {
    return parameter;
  }

  String wrapInFunction(String formattedParameter) {
    return function + "(" + formattedParameter + ")";
  }

}
