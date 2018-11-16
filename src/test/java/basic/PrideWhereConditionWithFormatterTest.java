package basic;

import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.SQL;
import pm.pride.WhereCondition;

import static pm.pride.WhereCondition.Operator.LIKE;

import java.sql.SQLException;

public class PrideWhereConditionWithFormatterTest extends AbstractPrideTest implements SQL.Formatter {
    @Override
    public String formatValue(Object rawValue) {
        if(rawValue instanceof String)
            rawValue = ((String) rawValue).replace('*', '%');

        return DatabaseFactory.getDatabase().formatValue(rawValue);
    }

    @Override
    public String formatOperator(String operator, Object rawValue) {
        return DatabaseFactory.getDatabase().formatOperator(operator, rawValue);
    }

    @Override
    public Object formatPreparedValue(Object rawValue) {
        if(rawValue instanceof String)
            rawValue = ((String) rawValue).replace('*', '%');

        return DatabaseFactory.getDatabase().formatPreparedValue(rawValue);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(1);
    }

    @Test
    public void testUseFormatterInWhereCondition() throws SQLException {
        WhereCondition whereCondition = new WhereCondition(this)
                .and("firstName", LIKE, "F*");

        assertEquals(1, new Customer().query(whereCondition).toArray(Customer.class).length);
    }

    @Test
    public void testUseFormatterInWhereConditionWithBinding() throws SQLException {
        WhereCondition whereCondition = new WhereCondition(this)
                .withBind()
                .and("firstName", LIKE, "F*");

        assertEquals(1, new Customer().query(whereCondition).toArray(Customer.class).length);
    }
}
