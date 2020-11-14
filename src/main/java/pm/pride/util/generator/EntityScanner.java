package pm.pride.util.generator;

import java.lang.reflect.Method;
import java.util.HashMap;

public class EntityScanner {
  EntityInformation entityInformation = new EntityInformation();

  public EntityInformation scan(String entityTypeName) {
    entityInformation = new EntityInformation();

    try {
      Class<?> entityType = Class.forName(entityTypeName);
      extractEntityMethods(entityType);

    } catch (ClassNotFoundException e) {
      // it is possible that the entity does not exist. In this case the entityInformation is empty
    }

    return entityInformation;
  }

  private void extractEntityMethods(Class<?> entityType) {
    for (Method method : entityType.getMethods()) {
      RealMethodName realMethodName = new RealMethodName(method.getName());
      entityInformation.put(realMethodName);
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityMethods(superEntityType);
    }
  }

}
