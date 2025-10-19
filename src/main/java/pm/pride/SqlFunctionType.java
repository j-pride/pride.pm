package pm.pride;

/**
 * Represents a collection of predefined SQL functions.
 * These functions encapsulate common SQL operations such as string
 * conversion or trimming.
 */
enum SqlFunctionType {

  UPPER("UPPER"),
  LOWER("LOWER"),
  RTRIM("RTRIM"),
  LTRIM("LTRIM"),
  TRIM("TRIM");

  private final String functionName;

  SqlFunctionType(String functionName) {
    this.functionName = functionName;
  }

  String getFunctionName() {
    return functionName;
  }
}
