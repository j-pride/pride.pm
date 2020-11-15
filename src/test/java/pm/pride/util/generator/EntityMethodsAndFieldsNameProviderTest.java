package pm.pride.util.generator;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityMethodsAndFieldsNameProviderTest {
  static final String COLUMNNAME_WITH_DEFAULT_CAPITALISATION = "Customerkey";
  static final String COLUMNNAME_WITH_ENTITY_CAPITALISATION = "CustomerKey";
  static final String BOOLEAN_COLUMN = "Naturalperson";
  static final String EXPECTED_BOOLEAN_METHOD = "isNaturalPerson";
  static final String BOOLEAN_FIELD = "naturalperson";
  static final String EXPECTED_BOOLEAN_FIELD = "naturalPerson";

  static final String UNKNOWN_FIELD = "unknownfield";

  static final String NOT_EXISTING_CLASS = "org.voegtle.HelloTest";

  @Test public void checkGetter() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(TestEntity.class.getCanonicalName());
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkSetter() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(TestEntity.class.getCanonicalName());
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctMethodName = provider.lookupSetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("set"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkDoesNoHarmWithUnknownClass() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(NOT_EXISTING_CLASS);
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_DEFAULT_CAPITALISATION, correctMethodName);
  }

  @Test public void checkBooleanGetter() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(TestEntity.class.getCanonicalName());
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(BOOLEAN_COLUMN);
    assertEquals(EXPECTED_BOOLEAN_METHOD, correctMethodName);
  }

  @Test public void checkFields() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(TestEntity.class.getCanonicalName());
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(BOOLEAN_FIELD);
    assertEquals(EXPECTED_BOOLEAN_FIELD, correctFieldName);
  }

  @Test public void checkUnknownField() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(TestEntity.class.getCanonicalName());
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(UNKNOWN_FIELD);
    assertEquals(UNKNOWN_FIELD, correctFieldName);
  }

  @Test public void checkUnknownClass() {
    EntityMethodsAndFieldsScanner entityMethodsAndFieldsScanner = new EntityMethodsAndFieldsScanner();
    EntityMethodsAndFields entityInformation = entityMethodsAndFieldsScanner.scan(NOT_EXISTING_CLASS);
    EntityMethodsAndFieldsNameProvider provider = new EntityMethodsAndFieldsNameProvider(entityInformation);

    String correctFieldName = provider.lookupField(UNKNOWN_FIELD);
    assertEquals(UNKNOWN_FIELD, correctFieldName);
  }
}
