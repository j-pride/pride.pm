package pm.pride;

/**
 * Represents a condition field in a SQL WHERE clause.
 * This class encapsulates either a simple column field or a field derived
 * from the use of an SQL function. It provides utilities to determine the
 * fully qualified name of the field based on a default table prefix.
 */
class WhereFieldConditionField {

  private String field;
  private SqlFunction function;

  WhereFieldConditionField(String field) {
    this.field = field;
  }

  WhereFieldConditionField(SqlFunction function) {
    this.function = function;
  }

  String determineQualifiedField(String defaultTablePrefix) {
    if (field != null) {
      return determineQualifiedFieldForFieldValue(defaultTablePrefix);
    } else if (function != null) {
      return determineQualifiedFieldForFunction(defaultTablePrefix);
    } else {
      throw new IllegalStateException("No field or function specified");
    }
  }

  private String determineQualifiedFieldForFunction(String defaultTablePrefix) {
    Object functionParameter = function.getParameter();
    assertFunctionParameterNotNull(functionParameter);
    assertFunctionParameterIsAString(functionParameter);
    String fieldInFunction = (String) functionParameter;
    if (defaultTablePrefix != null && !fieldInFunction.contains(".")) {
      return function.wrapInFunction(defaultTablePrefix + "." + fieldInFunction);
    } else {
      return function.wrapInFunction(fieldInFunction);
    }
  }

  private void assertFunctionParameterNotNull(Object functionParameter) {
    if (functionParameter == null) {
      throw new IllegalStateException("Function parameter must not be null, when it is used as field");
    }
  }

  private void assertFunctionParameterIsAString(Object functionParameter) {
    if (!(functionParameter instanceof String)) {
      throw new IllegalStateException("Function parameter must not be string describing the database field, when it is used as field");
    }
  }

  private String determineQualifiedFieldForFieldValue(String defaultTablePrefix) {
    if (defaultTablePrefix != null && !field.contains(".")) {
      return defaultTablePrefix + "." + field;
    } else {
      return field;
    }
  }

}
