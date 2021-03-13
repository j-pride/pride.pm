package pm.pride.util.generator;

import basic.AbstractPrideTest;
import basic.SkipForDBType;
import org.junit.Test;
import pm.pride.DatabaseFactory;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;
import pm.pride.ResourceAccessor;

import static pm.pride.ResourceAccessor.DBType.*;

@SkipForDBType(value = { ResourceAccessor.DBType.MYSQL })
public class EntityGeneratorTest extends AbstractPrideTest {
    @Test
    public void testGenerateBean() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateBean";
        String generatedCode = generate(TEST_TABLE, "Customer_GenerateBean", EntityGenerator.BEAN);
        assertGeneratedFragments(generatedCode,
                CLASS_TO_GENERATE,
                "String getLastname",
                "void setLastname(String",
                "String lastname");
    }

    @Test
    public void testGenerateHybrid() throws Exception {
        String CLASS_TO_GENERATE = "Customer_GenerateHybrid";

        // Some databases provide the table name in capital letters
        String EXPECTED_OUTPUT_TABLE_NAME = isDBType(ORACLE, HSQL, DB2)
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
        String generatedCode = generate(TEST_TABLE, GeneratedCustomerBeanWithCamelCasedProperties.class.getName(), EntityGenerator.BEAN);
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
            ".row( COL_FIRSTNAME, \"getFIRSTname\",",
            "private String lastNAME;",
            "String getLastname",
            "void setLastname(String",
            "private String firstname;",
            "void setFIRSTname"
        );
    }

    @Test
    public void testGenerateHybridWithTypoInCasedRecordDescriptor() throws Exception {
        String generatedCode = generate(TEST_TABLE, GeneratedCustomerHybridWithTypoInRecordDescriptor.class.getName());
        assertGeneratedFragments(generatedCode,
                "private String lastname;",
                "String getLastname",
                "void setLastname(String",
                "private String firstname;",
                "void setFirstname"
        );
    }

    @Test
    public void testGenerateBeanWithBeanValidationAnnotations() throws Exception {
        String generatedCode = generate(TEST_TABLE, "Irrelevant", EntityGenerator.BEAN_WITH_BEANVALIDATION);
        assertBeanValidationAnnotationsPresent(generatedCode);
    }

    @Test
    public void testGenerateHybridWithBeanValidationAnnotations() throws Exception {
        String generatedCode = generate(TEST_TABLE, "Irrelevant", EntityGenerator.HYBRID_WITH_BEANVALIDATION);
        assertGeneratedFragments(generatedCode,
            "javax.validation.constraints.*",
            "@NotNull",
            "@Size(max=50)");
    }

    private void assertBeanValidationAnnotationsPresent(String generatedCode) {
        assertGeneratedFragments(generatedCode,
            "javax.validation.constraints.*",
            "@NotNull");
        //SQLite does not provide a reasonable information about the column size
        if (!isDBType(SQLITE)) {
            assertGeneratedFragments(generatedCode, "@Size(max=50)");

        }
    }

    /** Runs the entity generator based on the database configuration being provided
     * by the base class {@link AbstractPrideTest} */
    private String generate(String... args) throws Exception {
        EntityGenerator gen = new EntityGenerator(args) {
            @Override
            protected void createResourceAccessor() {
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
