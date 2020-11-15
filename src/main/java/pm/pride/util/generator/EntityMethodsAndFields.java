package pm.pride.util.generator;

import java.util.HashMap;

public class EntityMethodsAndFields {

  private HashMap<String, String> methodNames = new HashMap<>();
  private HashMap<String, String> fieldNames = new HashMap<>();

  public String lookupRealMethod(String methodNameAnyCamelizing) {
    return methodNames.get(methodNameAnyCamelizing.toLowerCase());
  }

  public void putMethod(String realMethodName) {
    methodNames.put(realMethodName.toLowerCase(), realMethodName);
  }

  public String lookupRealField(String fieldNameAnyCamelizing) {
    return fieldNames.get(fieldNameAnyCamelizing.toLowerCase());
  }

  public void putField(String realFieldName) {
    fieldNames.put(realFieldName.toLowerCase(), realFieldName);
  }

}
