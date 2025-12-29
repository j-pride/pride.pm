package pm.pride.basic;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pm.pride.*;

import java.util.Date;

import static pm.pride.basic.PrideRevisioningTest.assertRevisioned;
import static pm.pride.basic.PrideRevisioningTest.waitForRevisionTimestampChange;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled("Revisioning is deprecated")
@NeedsDBType(ResourceAccessor.DBType.ORACLE)
public class PrideRevisioningPreparedUpdateTest extends AbstractPrideTest {

    PrideRevisioningTest revisioningTest = new PrideRevisioningTest();

    @Test
    public void testExpectErrorForMissingUpdateFieldsBatchUpdate() throws Exception {
        assertThrows(BatchUpdateRevisioningException.class, () -> new PreparedUpdate(new String[]{"id"}, new String[]{"lastName"}, RevisionedCustomer.red));
    }

    @Test
    public void testExpectErrorForMissingKeyFieldsBatchUpdate() throws Exception {
        assertThrows(BatchUpdateRevisioningException.class, () -> new PreparedUpdate(new String[0], new String[]{"firstName", "lastName", "hireDate", "active", SQL.quote("ty pe")}, RevisionedCustomer.red));
    }

    @Test
    public void testExpectErrorForConflictingKeyFields() throws Exception {
        assertThrows(BatchUpdateRevisioningException.class, () -> new PreparedUpdate(new String[]{"id", "firstName"}, new String[]{"firstName", "hireDate", "lastName", "active", SQL.quote("ty pe")}, RevisionedCustomer.red));
    }

    @Test
    public void testRevisioningIntegrityComplied() throws Exception {
        new PreparedUpdate(new String[]{"id"}, new String[]{"firstName", "hireDate", "lastName", "active"}, RevisionedCustomer.red);
    }

    @Test
    public void testDisabledRevisioningIntegrityChecksForBatchUpdate() throws Exception {
        new PreparedUpdate(new String[]{"id"}, new String[]{"lastName"}, RevisionedCustomer.red, false);
    }

    @Test
    public void testPreparedUpdate() throws Exception {
        revisioningTest.testInsertRevisioned();
        waitForRevisionTimestampChange();
        RevisionedCustomer customer = new RevisionedCustomer(10);
        PreparedUpdate pu = new PreparedUpdate(RevisionedCustomer.red);
        customer.setLastName("Meyer");
        pu.execute(customer);
        DatabaseFactory.getDatabase().commit();
        assertRevisioned(10, "Sioned", "Meyer");
    }

    @Test
    public void testPreparedUpdateBatch() throws Exception {
        RevisionedCustomer c1 = new RevisionedCustomer(11, "Klaus", "Meyer", Boolean.TRUE, new Date());
        RevisionedCustomer c2 = new RevisionedCustomer(12, "Peter", "Moeller", Boolean.TRUE, new Date());
        DatabaseFactory.getDatabase().commit();
        waitForRevisionTimestampChange();
        PreparedUpdate pu = new PreparedUpdate(RevisionedCustomer.red);
        c1.setLastName("Bean");
        pu.addBatch(c1);
        c2.setLastName("Smith");
        pu.addBatch(c2);
        pu.executeBatch();
        DatabaseFactory.getDatabase().commit();
        assertRevisioned(11, "Meyer", "Bean");
        assertRevisioned(12, "Moeller", "Smith");
    }
}
