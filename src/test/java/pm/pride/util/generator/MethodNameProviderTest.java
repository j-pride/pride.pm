package pm.pride.util.generator;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodNameProviderTest {
  static final String COLUMNNAME_WITH_DEFAULT_CAPITALISATION = "Customerkey";
  static final String COLUMNNAME_WITH_ENTITY_CAPITALISATION = "CustomerKey";
  static final String BOOLEAN_COLUMN = "Naturalperson";
  static final String EXPECTED_BOOLEAN_COLUMNNAME = "isNaturalPerson";

  static final String NOT_EXISTING_CLASS = "org.voegtle.HelloTest";

  @Test public void checkGetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    MethodNameProvider provider = new MethodNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkSetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    MethodNameProvider provider = new MethodNameProvider(entityInformation);

    String correctMethodName = provider.lookupSetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("set"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkDoesNoHarmWithUnknownClass() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(NOT_EXISTING_CLASS);
    MethodNameProvider provider = new MethodNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_DEFAULT_CAPITALISATION, correctMethodName);
  }

  @Test public void checkBooleanGetter() {
    EntityScanner entityScanner = new EntityScanner();
    EntityInformation entityInformation = entityScanner.scan(TestEntity.class.getCanonicalName());
    MethodNameProvider provider = new MethodNameProvider(entityInformation);

    String correctMethodName = provider.lookupGetter(BOOLEAN_COLUMN);
    assertEquals(EXPECTED_BOOLEAN_COLUMNNAME, correctMethodName);
  }
}
