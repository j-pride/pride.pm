package pm.pride.util.generator;

public class MethodNameFactory {
  private String columnName;

  public MethodNameFactory(String columnName) {
    this.columnName = columnName;
  }

  String lowerGet() {
    return get().toLowerCase();
  }

  String get() {
    return "get" + columnName;
  }

  String lowerIs() {
    return is().toLowerCase();
  }

  String is() {
    return "is" + columnName;
  }

  String lowerSet() {
    return set().toLowerCase();
  }

  String set() {
    return "set" + columnName;
  }


}
