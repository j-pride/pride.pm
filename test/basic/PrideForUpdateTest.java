package basic;

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.SQLFormatter;
import de.mathema.pride.WhereCondition;
import org.junit.Test;

import java.sql.SQLException;

import static de.mathema.pride.WhereCondition.Operator.EQUAL;
import static de.mathema.pride.WhereCondition.Operator.UNEQUAL;

public class PrideForUpdateTest extends AbstractPrideTest implements SQLFormatter {

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

        assertEquals("( firstName IS NOT NULL ) FOR UPDATE", expression.toSQL(this));
        assertNotNull(new Customer().query(expression));
    }

    @Override
    public String formatValue(Object rawValue) {
        return DatabaseFactory.getDatabase().formatValue(rawValue);
    }

    @Override
    public String formatOperator(String operator, Object rawValue) {
        if (operator.equals(EQUAL)) {
            return (rawValue == null) ? "IS" : operator;
        }
        if (operator.equals(UNEQUAL)) {
            return (rawValue == null) ? "IS NOT" : operator;
        }
        return operator;
    }

    @Override
    public Object formatPreparedValue(Object rawValue) {
        return DatabaseFactory.getDatabase().formatPreparedValue(rawValue);
    }
}
