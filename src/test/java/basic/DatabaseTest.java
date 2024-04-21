package basic;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;
import pm.pride.RecordDescriptor;
import pm.pride.WhereCondition;
import pm.pride.Database.QueryScope;

import java.sql.SQLException;

public class DatabaseTest extends AbstractPrideTest  {

    public class CountDescriptor extends RecordDescriptor {

        private Long count; // The sum
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }

        // SumDescriptor defines itself as the sink for query results.
        public CountDescriptor(String tableName) {
            super(CountDescriptor.class, tableName, null,
                    new String[][]{ {null, "getCount", "setCount"} });
        }

        @Override
        public String getResultFields() { return "count(*)"; }
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(9);
    }

    @Test
    public void testQueryWithWhereConditionIsNull() throws SQLException {
        CountDescriptor countDesc =  new CountDescriptor(TEST_TABLE);
        DatabaseFactory.getDatabase().query(countDesc, QueryScope.First, countDesc, false, (WhereCondition) null);
    }
}
