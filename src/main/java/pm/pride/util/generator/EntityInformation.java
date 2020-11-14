package pm.pride.util.generator;

import java.util.HashMap;

public class EntityInformation {

  private HashMap<String, RealMethodName> methodNames = new HashMap<>();
  private HashMap<String, RealMethodName> attributeName = new HashMap<>();


  public RealMethodName lookupMethod(String methodName) {
    return methodNames.get(methodName);
  }

  public void put(RealMethodName realMethodName) {
    methodNames.put(realMethodName.lowerCaseName, realMethodName);
  }
}
