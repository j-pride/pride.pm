package pm.pride;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a condition field in an SQL WHERE clause.
 * This class encapsulates either a simple column name or an expression derived
 * from applying an SQL function to that column. It provides utilities to determine the
 * fully qualified name of the field based on a default table prefix.
 */
class WhereField {

  private String field;
  private WhereFunction function;

  WhereField(String field) {
    this.field = field;
  }

  WhereField(WhereFunction function) {
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
    List<String> qualifiedFields = new ArrayList<>();
    for (Object functionParameter : function.getParameters()) {
      assertFunctionParameterNotNull(functionParameter);
      String fieldInFunction = functionParameter.toString();
      if (defaultTablePrefix != null && !fieldInFunction.contains(".")) {
        qualifiedFields.add(defaultTablePrefix + "." + fieldInFunction);
      } else {
        qualifiedFields.add(fieldInFunction);
      }
    }
    return function.wrapInFunction(qualifiedFields);
  }

  private void assertFunctionParameterNotNull(Object functionParameter) {
    if (functionParameter == null) {
      throw new IllegalStateException("Function parameter must not be null, when it is used as field");
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
