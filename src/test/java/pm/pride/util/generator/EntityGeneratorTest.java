package pm.pride.util.generator;

import basic.AbstractPrideTest;
import org.junit.Test;
import pm.pride.DatabaseFactory;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;

public class EntityGeneratorTest extends AbstractPrideTest {
    @Test
    public void testGenerateBean() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateBean";
        String generatedCode = generate(TEST_TABLE, "Customer_GenerateBean", "-b");
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                "String getLastname");
    }

    @Test
    public void testGenerateHyprid() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateHybrid";
        String generatedCode = generate(TEST_TABLE, CLASS_TO_GENERATE);
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                MappedObject.class.getSimpleName(),
                TEST_TABLE,
                RecordDescriptor.class.getSimpleName(),
                "String getLastname");
    }

    @Test
    public void testGenerateDBAWithCamelCasedPropertiesFromExsistingBean() throws Exception {
        String generatedCode = generate(TEST_TABLE, "CustomerDBA", GeneratedCustomerBeanWithCamelCasedProperties.class.getName());
        assertGeneratedFragments(generatedCode,
                "getFirstName",
                "getLastName");
    }

    /** Runs the entity generator based on the database configuration being provided
     * by the base class {@link AbstractPrideTest} */
    private String generate(String... args) throws Exception {
        EntityGenerator gen = new EntityGenerator(args) {
            @Override
            protected void createResourceAccessor() throws Exception {
                resourceAccessor = DatabaseFactory.getResourceAccessor();
            }
        };
        String generatedCode = gen.create();
        System.out.println(generatedCode);
        return generatedCode;
    }

    private void assertGeneratedFragments(String generatedCode, String... expectedFragments) {
        for (String fragment: expectedFragments) {
            assertTrue("Expected fragment '" + fragment + "' not found in " + generatedCode,
                    generatedCode.contains(fragment));
        }
    }

}
