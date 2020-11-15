package pm.pride.util.generator;

import basic.AbstractPrideTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pm.pride.DatabaseFactory;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;
import pm.pride.ResourceAccessor;

import java.util.Arrays;
import java.util.Random;

import static pm.pride.ResourceAccessor.DBType.*;

public class EntityGeneratorTest extends AbstractPrideTest {
    private String TABLE_TO_GENERATE_FOR = TEST_TABLE;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // Some databases provide the table name in capital letters
        if (isDBType(ORACLE, HSQL, MYSQL)) {
            TABLE_TO_GENERATE_FOR = TABLE_TO_GENERATE_FOR.toUpperCase();
        }
    }

    @Test
    public void testGenerateBean() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateBean";
        String generatedCode = generate(TABLE_TO_GENERATE_FOR, "Customer_GenerateBean", "-b");
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                "String getLastname",
                "void setLastname(String",
                "String lastname");
    }

    @Test
    public void testGenerateHybrid() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateHybrid";

        String generatedCode = generate(TABLE_TO_GENERATE_FOR, CLASS_TO_GENERATE);
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                MappedObject.class.getSimpleName(),
                TABLE_TO_GENERATE_FOR,
                RecordDescriptor.class.getSimpleName(),
                "String getLastname",
                "void setLastname(String");
    }

    @Test
    public void testGenerateDBAWithCamelCasedPropertiesFromExistingBean() throws Exception {
        String generatedCode = generate(TABLE_TO_GENERATE_FOR, "CustomerDBA", GeneratedCustomerBeanWithCamelCasedProperties.class.getName());
        assertGeneratedFragments(generatedCode,
                "getFirstName",
                "getLastName");
    }

    @Test
    public void testGenerateBeanWithCamelCasedPropertiesFromExistingBean() throws Exception {
        String generatedCode = generate(TABLE_TO_GENERATE_FOR, GeneratedCustomerBeanWithCamelCasedProperties.class.getName(), "-b");
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
        String generatedCode = generate(TABLE_TO_GENERATE_FOR, GeneratedCustomerHybridWithCamelCasedProperties.class.getName());
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
