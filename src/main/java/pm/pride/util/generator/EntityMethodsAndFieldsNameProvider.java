package pm.pride.util.generator;

/** The entity generator generates the getter and setter from the database column names.
 * These names sometimes use different capitalisation than the getter and setter in the
 * entities. This class returns method and field names for an entity to generate according
 * to the capitalisation from an already existing entity. */
public class EntityMethodsAndFieldsNameProvider {

  private final EntityMethodsAndFields entityMethodsAndFields;

  public EntityMethodsAndFieldsNameProvider(EntityMethodsAndFields entityMethodsAndFields) {
    this.entityMethodsAndFields = entityMethodsAndFields;
  }

  /**
   * return the getter name with the capitalisation used in the entity.
   */
  public String lookupGetter(String columnName) {
    String getterCandidate = columnName2Getter(columnName);

    String realMethodName = entityMethodsAndFields.lookupRealMethod(getterCandidate);
    if (realMethodName == null) {
      realMethodName = entityMethodsAndFields.lookupRealMethod(columnName2BooleanGetter(columnName));
    }
    return realMethodName != null ? realMethodName : getterCandidate;
  }

  public String lookupSetter(String columnName) {
    String setterCandidate = columnName2Setter(columnName);
    String realMethodName = entityMethodsAndFields.lookupRealMethod(setterCandidate);
    return realMethodName != null ? realMethodName : setterCandidate;
  }

  public String lookupField(String columnName) {
    String realFieldName = entityMethodsAndFields.lookupRealField(columnName);
    return realFieldName != null ? realFieldName : columnName;
  }

  String columnName2Getter(String columnName) {
    return "get" + columnName;
  }

  String columnName2BooleanGetter(String columnName) {
    return "is" + columnName;
  }

  String columnName2Setter(String columnName) { return "set" + columnName; }

}
