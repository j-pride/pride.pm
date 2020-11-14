package pm.pride.util.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EntityScanner {
  EntityInformation entityInformation = new EntityInformation();

  public EntityInformation scan(String entityTypeName) {
    entityInformation = new EntityInformation();

    try {
      Class<?> entityType = Class.forName(entityTypeName);
      extractEntityMethods(entityType);
      extractEntityFields(entityType);

    } catch (ClassNotFoundException e) {
      // it is possible that the entity does not exist. In this case the entityInformation is empty
    }

    return entityInformation;
  }

  private void extractEntityMethods(Class<?> entityType) {
    for (Method method : entityType.getMethods()) {
      RealName realMethodName = new RealName(method.getName());
      entityInformation.putMethod(realMethodName);
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityMethods(superEntityType);
    }
  }

  private void extractEntityFields(Class<?> entityType) {
    for (Field field : entityType.getDeclaredFields()) {
      RealName realFieldName = new RealName(field.getName());
      entityInformation.putField(realFieldName);
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityFields(superEntityType);
    }

  }

}
