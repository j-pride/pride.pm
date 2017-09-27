package basic;

import de.mathema.pride.WhereCondition;
import org.junit.Test;

import java.sql.SQLException;

import static de.mathema.pride.WhereCondition.Operator.UNEQUAL;

public class PrideForUpdateTest extends AbstractPrideTest{

    @Override
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(1);
    }

    @Test
    public void testWhereConditionWithForUpdate() throws SQLException {
        WhereCondition expression = new WhereCondition()
                .and("firstName", UNEQUAL, null)
                .forUpdate();

        assertEquals("( firstName IS NOT null ) FOR UPDATE", expression.toString());
        assertNotNull(new Customer().query(expression));
    }
}
