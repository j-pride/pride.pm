package pm.pride.util.generator;

/**
 * The entity generator generates the getter and setter from the database column names.
 * These names sometimes use different capitalisation than the getter and setter in the entities. This class returns the
 * methodname of the entity in the capitalisation used in the entity.
 */
public class MethodNameProvider {

  private EntityInformation entityInformation;

  public MethodNameProvider(EntityInformation entityInformation) {
    this.entityInformation = entityInformation;
  }

  /**
   * return the getter name with the capitalisation used in the entity.
   */
  public String lookupGetter(String columnName) {
    MethodNameFactory nameCandidate = new MethodNameFactory(columnName);
    RealMethodName realMethodName = entityInformation.lookupMethod(nameCandidate.lowerGet());
    if (realMethodName == null) {
      realMethodName = entityInformation.lookupMethod(nameCandidate.lowerIs());
    }
    return realMethodName != null ? realMethodName.realName : nameCandidate.get();
  }

  public String lookupSetter(String columnName) {
    MethodNameFactory nameCandidate = new MethodNameFactory(columnName);
    RealMethodName realMethodName = entityInformation.lookupMethod(nameCandidate.lowerSet());

    return realMethodName != null ? realMethodName.realName : nameCandidate.set();
  }

}
