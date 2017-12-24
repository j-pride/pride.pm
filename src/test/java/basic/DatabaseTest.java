package basic;

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.RecordDescriptor;
import de.mathema.pride.WhereCondition;
import org.junit.Test;

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
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(9);
    }

    @Test
    public void testQueryWithWherConditionIsNull() throws SQLException {
        CountDescriptor countDesc =  new CountDescriptor("Customer");
        DatabaseFactory.getDatabase().query((WhereCondition) null, countDesc, countDesc, false);
    }
}
