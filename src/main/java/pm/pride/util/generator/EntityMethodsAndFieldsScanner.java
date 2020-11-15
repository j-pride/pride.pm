package pm.pride.util.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EntityMethodsAndFieldsScanner {
  EntityMethodsAndFields entityMethodsAndFields = new EntityMethodsAndFields();

  public EntityMethodsAndFields scan(String entityTypeName) {
    entityMethodsAndFields = new EntityMethodsAndFields();

    try {
      Class<?> entityType = Class.forName(entityTypeName);
      extractEntityMethods(entityType);
      extractEntityFields(entityType);
    }
    catch (ClassNotFoundException | ExceptionInInitializerError | NoClassDefFoundError e ) {
      // it is possible that the entity does not exist or is not constructable. In this case the entityInformation is empty
    }

    return entityMethodsAndFields;
  }

  private void extractEntityMethods(Class<?> entityType) {
    for (Method method : entityType.getMethods()) {
      entityMethodsAndFields.putMethod(method.getName());
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityMethods(superEntityType);
    }
  }

  private void extractEntityFields(Class<?> entityType) {
    for (Field field : entityType.getDeclaredFields()) {
      entityMethodsAndFields.putField(field.getName());
    }

    Class<?> superEntityType = entityType.getSuperclass();
    if (superEntityType != null && superEntityType != Object.class) {
      extractEntityFields(superEntityType);
    }

  }

}
