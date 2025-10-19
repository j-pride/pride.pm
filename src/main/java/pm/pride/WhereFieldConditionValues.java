package pm.pride;

import java.lang.reflect.Method;

/**
 * Represents condition values for a "WHERE" SQL clause, allowing for values or functions
 * associated with a condition to be encapsulated and processed.
 * This class provides support for:
 * - Handling raw values or an SQL function for conditions.
 * - Formatting the values or function in SQL-compatible representations.
 * - Retrieving the raw values or function parameters.
 */
class WhereFieldConditionValues {

  private static Object value(int index, Object[] values ) {
    if (values == null)
      return null;
    return values[index];
  }

  private static String formatValue(Object[] values, String operator, boolean withBinding, SQL.Formatter formatter) {
    switch (operator) {
      case WhereCondition.Operator.BETWEEN:
        return formatSingleValue(value0(values), withBinding, formatter) + " AND " + formatSingleValue(value(1, values), withBinding, formatter);
      case WhereCondition.Operator.IN:
      case WhereCondition.Operator.NOTIN:
        StringBuilder result = new StringBuilder("( ");
        for (int i = 0; i < values.length; i++) {
          result.append(formatSingleValue(value(i, values), withBinding, formatter));
          if (i < values.length - 1)
            result.append(", ");
        }
        return result + " )";
      default:
        return formatSingleValue(value0(values), withBinding, formatter);
    }
  }

  private static String formatSingleValue(Object value, boolean withBinding, SQL.Formatter formatter) {
    if (withBinding && value != null)
      return "?";
    if (formatter == null || (value instanceof SQL.Pre)) {
      return (value == null) ? "null" : value.toString();
    }
    return formatter.formatValue(value, null, false);
  }

  private static int bindSingleValue(Object value, SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam)
          throws ReflectiveOperationException {

    if(value == null) return nextParam;

    Method setter = PreparedStatementAccess.getAccessMethod(value.getClass());
    Object preparedValue = formatter.formatPreparedValue(value, setter.getParameterTypes()[1]);
    cns.setBindParameter(setter, nextParam, preparedValue);
    nextParam++;

    return nextParam;
  }

  private static Object value0(Object[] values) {
    return value(0, values);
  }
  private Object[] values;

  private SqlFunction function;

  WhereFieldConditionValues(Object... values) {
    this.values = values;
  }

  WhereFieldConditionValues(SqlFunction function) {
    this.function = function;
  }

  boolean hasNoValues() {
    if (function != null) {
      return function.getParameter() == null;
    } else {
      return values == null || values.length == 0 || values[0] == null;
    }
  }

  /**
   * Binds a value or a sequence of values to a prepared SQL statement using the specified formatter,
   * connection, and statement details. If the instance is associated with a function, it binds a single
   * value derived from the function's parameter; otherwise, it binds multiple values in sequence.
   *
   * @param formatter an instance of {@code SQL.Formatter} used to format values before binding.
   * @param cns an instance of {@code ConnectionAndStatement} that provides access to the database
   *            connection and the prepared statement where the values will be bound.
   * @param nextParam the index of the next parameter in the prepared statement where binding should
   *                  begin. Typically, this is incremented after each bound value.
   * @return the index of the next parameter after all values have been bound. If binding multiple values,
   *         this will be incremented accordingly.
   * @throws ReflectiveOperationException if an error occurs during reflection while binding values.
   */
  int bind(SQL.Formatter formatter, ConnectionAndStatement cns, int nextParam) throws ReflectiveOperationException {
    if (function != null) {
      return bindSingleValue(function.getParameter(), formatter, cns, nextParam);
    } else {
      for(Object aValue : values) {
        nextParam = bindSingleValue(aValue, formatter, cns, nextParam);
      }
      return nextParam;
    }
  }

  /**
   * Formats the value or function associated with the current instance based on the provided operator,
   * binding flag, and formatter. If a function is present, it delegates to {@code formatFunction}.
   * Otherwise, it formats the available values using the operator via the {@code formatValue} method.
   *
   * @param operator the SQL operator to apply for formatting. Can influence how values are formatted
   *                 (e.g., BETWEEN, IN, etc.). If null, an empty string is returned.
   * @param withBinding determines if binding should be applied. If true, certain values may be replaced
   *                    with binding placeholders (e.g., "?" for parameterized queries).
   * @param formatter a {@link SQL.Formatter} instance used to customize the formatting of values or operators
   *                  for SQL generation. May be null for default formatting behavior.
   * @return the formatted string representation of the value or function, or an empty string if the operator
   *         is null or no valid values/functions are available for formatting.
   */
  String formatValueOrFunction(String operator, boolean withBinding, SQL.Formatter formatter) {
    if (operator == null) {
      return "";
    } else if (function != null) {
      return formatFunction(withBinding, formatter);
    } else {
      return formatValue(values, operator, withBinding, formatter);
    }
  }

  private String formatFunction(boolean withBinding, SQL.Formatter formatter) {
    return function.wrapInFunction(formatSingleValue(function.getParameter(), withBinding, formatter));
  }

  Object rawValue() {
    if (values != null) {
      return value0(values);
    } else if (function != null) {
      return function.getParameter();
    }
    return null;
  }

}
