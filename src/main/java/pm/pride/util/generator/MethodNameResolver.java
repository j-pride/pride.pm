package pm.pride.util.generator;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The entity generator generates the getter and setter from the database column names.
 * These names sometimes use different capitalisation than the getter and setter in the entities. This class returns the
 * methodname of the entity in the capitalisation used in the entity.
 */
public class MethodNameResolver {
  private class RealMethodName {

    String lowerCaseName;
    String realName;
    public RealMethodName(String realName) {
      this.lowerCaseName = realName.toLowerCase();
      this.realName = realName;
    }

  }
  private HashMap<String, RealMethodName> methodNames = new HashMap<>();
  /**
   * return the getter name with the capitalisation used in the entity.
   */
  public String lookupGetter(String columnName) {
    MethodNameFactory nameCandidate = new MethodNameFactory(columnName);
    RealMethodName realMethodName = methodNames.get(nameCandidate.lowerGet());
    if (realMethodName == null) {
      realMethodName = methodNames.get(nameCandidate.lowerIs());
    }
    return realMethodName != null ? realMethodName.realName : nameCandidate.get();
  }

  public String lookupSetter(String columnName) {
    MethodNameFactory nameCandidate = new MethodNameFactory(columnName);
    RealMethodName realMethodName = methodNames.get(nameCandidate.lowerSet());

    return realMethodName != null ? realMethodName.realName : nameCandidate.set();
  }

  public void extractEntityMethods(String entityTypeName) {
    try {
      methodNames = new HashMap<>();
      Class<?> entityType = Class.forName(entityTypeName);
      extractEntityMethods(entityType);
    } catch (ClassNotFoundException e) {
      // it is possible that the entity does not exist. In this case the method list is empty
    }
  }

  private void extractEntityMethods(Class<?> entityType) {
    for (Method method : entityType.getMethods()) {
      RealMethodName realMethodName = new RealMethodName(method.getName());
      methodNames.put(realMethodName.lowerCaseName, realMethodName);
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityMethods(superEntityType);
    }
  }
}
