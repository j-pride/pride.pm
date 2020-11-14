package pm.pride.util.generator;

import java.util.HashMap;

public class EntityInformation {

  private HashMap<String, RealName> methodNames = new HashMap<>();
  private HashMap<String, RealName> fieldNames = new HashMap<>();


  public RealName lookupMethod(String methodName) {
    return methodNames.get(methodName);
  }

  public void putMethod(RealName realMethodName) {
    methodNames.put(realMethodName.lowerCaseName, realMethodName);
  }

  public RealName lookupField(String fieldName) {
    return fieldNames.get(fieldName);
  }

  public void putField(RealName realFieldName) {
    fieldNames.put(realFieldName.lowerCaseName, realFieldName);
  }
}
