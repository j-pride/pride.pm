package basic;

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.SQLFormatter;
import de.mathema.pride.WhereCondition;
import org.junit.Test;

import java.sql.SQLException;

import static de.mathema.pride.WhereCondition.Operator.LIKE;

public class PrideWhereConditionWithFormatterTest extends AbstractPrideTest implements SQLFormatter {
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
