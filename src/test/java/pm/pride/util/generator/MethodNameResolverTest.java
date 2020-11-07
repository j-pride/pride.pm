package pm.pride.util.generator;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MethodNameResolverTest {
  static final String COLUMNNAME_WITH_DEFAULT_CAPITALISATION = "Customerkey";
  static final String COLUMNNAME_WITH_ENTITY_CAPITALISATION = "CustomerKey";
  static final String BOOLEAN_COLUMN = "Naturalperson";
  static final String EXPECTED_BOOLEAN_COLUMNNAME = "isNaturalPerson";

  static final String NOT_EXISTING_CLASS = "org.voegtle.HelloTest";

  @Test public void checkGetter() {
    MethodNameResolver provider = new MethodNameResolver();
    provider.extractEntityMethods(TestEntity.class.getCanonicalName());
    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkSetter() {
    MethodNameResolver provider = new MethodNameResolver();
    provider.extractEntityMethods(TestEntity.class.getCanonicalName());
    String correctMethodName = provider.lookupSetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("set"+ COLUMNNAME_WITH_ENTITY_CAPITALISATION, correctMethodName);
  }

  @Test public void checkDoesNoHarmWithUnknownClass() {
    MethodNameResolver provider = new MethodNameResolver();
    provider.extractEntityMethods(NOT_EXISTING_CLASS);
    String correctMethodName = provider.lookupGetter(COLUMNNAME_WITH_DEFAULT_CAPITALISATION);
    assertEquals("get"+ COLUMNNAME_WITH_DEFAULT_CAPITALISATION, correctMethodName);
  }

  @Test public void checkBooleanGetter() {
    MethodNameResolver provider = new MethodNameResolver();
    provider.extractEntityMethods(TestEntity.class.getCanonicalName());
    String correctMethodName = provider.lookupGetter(BOOLEAN_COLUMN);
    assertEquals(EXPECTED_BOOLEAN_COLUMNNAME, correctMethodName);
  }
}
