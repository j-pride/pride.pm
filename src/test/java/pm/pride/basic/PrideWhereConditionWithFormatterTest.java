package pm.pride.basic;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;
import pm.pride.SQL;
import pm.pride.WhereCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pm.pride.WhereCondition.Operator.LIKE;

import java.sql.SQLException;

public class PrideWhereConditionWithFormatterTest extends AbstractPrideTest implements SQL.Formatter {
    @Override
    public String formatValue(Object rawValue, Class<?> targetType, boolean forLogging) {
        if(rawValue instanceof String)
            rawValue = ((String) rawValue).replace('*', '%');

        return DatabaseFactory.getDatabase().formatValue(rawValue, targetType, forLogging);
    }

    @Override
    public Object formatPreparedValue(Object rawValue, Class<?> targetType) {
        if(rawValue instanceof String)
            rawValue = ((String) rawValue).replace('*', '%');

        return DatabaseFactory.getDatabase().formatPreparedValue(rawValue, targetType);
    }

    @Override
    public String formatOperator(String operator, Object rawValue) {
        return DatabaseFactory.getDatabase().formatOperator(operator, rawValue);
    }

    @Override
	public boolean bindvarsByDefault() {
    	return DatabaseFactory.getDatabase().bindvarsByDefault();
	}

	@Override
    @BeforeEach
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
                .bindvarsOn()
                .and("firstName", LIKE, "F*");

        assertEquals(1, new Customer().query(whereCondition).toArray(Customer.class).length);
    }
}
