package pm.pride.util.generator;

import basic.AbstractPrideTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pm.pride.DatabaseFactory;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;
import pm.pride.ResourceAccessor;

import java.util.Random;

public class EntityGeneratorTest extends AbstractPrideTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testGenerateBean() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateBean";
        String generatedCode = generate(TEST_TABLE, "Customer_GenerateBean", "-b");
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                "String getLastname",
                "void setLastname(String",
                "String lastname");
    }

    @Test
    public void testGenerateHybrid() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateHybrid";

        String dbType = DatabaseFactory.getResourceAccessor().getDBType();
        String EXPECTED_OUTPUT_TABLE_NAME = dbType.equals(ResourceAccessor.DBType.ORACLE)
            ? TEST_TABLE.toUpperCase()
            : TEST_TABLE;

        String generatedCode = generate(TEST_TABLE, CLASS_TO_GENERATE);
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                MappedObject.class.getSimpleName(),
                EXPECTED_OUTPUT_TABLE_NAME,
                RecordDescriptor.class.getSimpleName(),
                "String getLastname",
                "void setLastname(String");
    }

    @Test
    public void testGenerateDBAWithCamelCasedPropertiesFromExistingBean() throws Exception {
        String generatedCode = generate(TEST_TABLE, "CustomerDBA", GeneratedCustomerBeanWithCamelCasedProperties.class.getName());
        assertGeneratedFragments(generatedCode,
                "getFirstName",
                "getLastName");
    }

    @Test
    public void testGenerateBeanWithCamelCasedPropertiesFromExistingBean() throws Exception {
        String generatedCode = generate(TEST_TABLE, GeneratedCustomerBeanWithCamelCasedProperties.class.getName(), "-b");
        assertGeneratedFragments(generatedCode,
                "private String lastName;",
                "String getLastName",
                "void setLastName(String",
                "private String firstname;",
                "void setFirstName"
                );
    }

    @Test
    public void testGenerateHybridWithCamelCasedPropertiesFromExistingHybrid() throws Exception {
        String generatedCode = generate(TEST_TABLE, GeneratedCustomerHybridWithCamelCasedProperties.class.getName());
        assertGeneratedFragments(generatedCode,
                "private String lastNAME;",
                "String getLastname",
                "void setLastname(String",
                "private String firstname;",
                "void setFIRSTname"
        );
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
