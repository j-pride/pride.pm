package pm.pride;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an SQL function and its parameter used in a WHERE condition.
 * This class provides the ability to encapsulate SQL function calls with
 * their respective function name and parameter, allowing integration with
 * database operations.
 * <p>
 * The inner interface {@link Type} provides a set of typical SQL functions,
 * but {@link WhereFunction} is not limited to those. The constructor method
 * {@link #function(String, Object...)} allows the creation of any kind of
 * function-based expressions.
 */
public class WhereFunction {
  public interface Type {
    String UPPER = "UPPER";
    String LOWER = "LOWER";
    String RTRIM = "RTRIM";
    String LTRIM = "LTRIM";
    String TRIM = "TRIM";
    String LENGTH = "LENGTH";
    String CONCAT = "CONCAT";
    String SUBSTR = "SUBSTR";
  }

  String function;
  Object[] parameters;

  public static WhereFunction function(String functionName, Object... parameters) {
    return new WhereFunction(functionName, parameters);
  }

  /**
   * Constructs an SQL "UPPER" function that converts the provided value to uppercase in SQL execution.
   *
   * @param value The value to be converted to uppercase. This can typically be a column name or a literal value.
   * @return An instance of {@code SqlFunction} encapsulating the SQL "UPPER" function.
   */
  public static WhereFunction upper(String value) {
    return function(Type.UPPER, value);
  }

  /** Like {@link #upper(String)} but applying SQL function LOWER */
  public static WhereFunction lower(String value) {
    return function(Type.LOWER, value);
  }

  /** Like {@link #upper(String)} but applying SQL function LTRIM */
  public static WhereFunction ltrim(String value) {
    return function(Type.LTRIM, value);
  }

  /** Like {@link #upper(String)} but applying SQL function TRIM */
  public static WhereFunction trim(Object value) {
    return function(Type.TRIM, value);
  }

  /** Like {@link #upper(String)} but applying SQL function RTRIM */
  public static WhereFunction rtrim(String value) { return function(Type.RTRIM, value); }

  /** Like {@link #upper(String)} but applying SQL function LEN */
  public static WhereFunction length(String value) { return function(Type.LENGTH, value); }

  public static WhereFunction concat(String... values) { return function(Type.CONCAT, values); }

  public static WhereFunction substr(String value, int position) { return function(Type.SUBSTR, value, position); }

  public static WhereFunction substr(String value, int position, int substringLength) {
    return function(Type.SUBSTR, value, position, substringLength);
  }

  WhereFunction() {
  }

  WhereFunction(String function, Object... parameters) {
    this.function = function;
    this.parameters = parameters;
  }

  Object[] getParameters() {
    return parameters;
  }

  public boolean hasNoValues() {
    return parameters == null || parameters.length == 0;
  }

  String wrapInFunction(List<String> formattedParameters) {
    return function
      + "("
      + formattedParameters.stream().collect(Collectors.joining(","))
      + ")";
  }

}
