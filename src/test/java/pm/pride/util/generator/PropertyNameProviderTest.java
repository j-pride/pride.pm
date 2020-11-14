package pm.pride.util.generator;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyNameProviderTest {
  static final String COLUMNNAME_WITH_DEFAULT_CAPITALISATION = "Customerkey";
  static final String COLUMNNAME_WITH_ENTITY_CAPITALISATION = "CustomerKey";
  static final String BOOLEAN_COLUMN = "Naturalperson";
  static final String EXPECTED_BOOLEAN_METHOD = "isNaturalPerson";
  static final String BOOLEAN_FIELD = "naturalperson";
  static final String EXPECTED_BOOLEAN_FIELD = "naturalPerson";

  static final String UNKNOWN_FIELD = "unknownfield";

  static final String NOT_EXISTING_CLASS = "org.voegtle.HelloTest";

  @Test public void checkGetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkSetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctMethodName = provider.lookupSetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("set"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkDoesNoHarmWithUnknownClass() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(NOT_EXISTING_CLASS);
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_DEFAULT_CAPITALISATION, correctMethodName);
  }

  @Test public void checkBooleanGetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(BOOLEAN_COLUMN);
    assertEquals(EXPECTED_BOOLEAN_METHOD, correctMethodName);
  }

  @Test public void checkFields() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(BOOLEAN_FIELD);
    assertEquals(EXPECTED_BOOLEAN_FIELD, correctFieldName);
  }

  @Test public void checkUnknownField() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(UNKNOWN_FIELD);
    assertEquals(UNKNOWN_FIELD, correctFieldName);
  }

  @Test public void checkUnknownClass() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(NOT_EXISTING_CLASS);
    PropertyNameProvider provider = new PropertyNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(UNKNOWN_FIELD);
    assertEquals(UNKNOWN_FIELD, correctFieldName);
  }
}
