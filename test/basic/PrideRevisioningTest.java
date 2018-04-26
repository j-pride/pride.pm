package basic;

import de.mathema.pride.*;
import org.junit.Test;

import java.util.Date;

public class PrideRevisioningTest extends AbstractPrideTest {

    @Test
    public void testInsertRevisioned() throws Exception {
        RevisionedCustomer c = new RevisionedCustomer(10, "Revi", "Sioned", true, new Date());
        DatabaseFactory.getDatabase().commit();
        assertRevisioned(10, "Sioned");
    }

    @Test
    public void testUpdateRevisioned() throws Exception {
        RevisionedCustomer c = new RevisionedCustomer(11, "Klaus", "Meyer", Boolean.TRUE, new Date());
        DatabaseFactory.getDatabase().commit();
        RevisionedCustomer c2 = new RevisionedCustomer(11);
        c2.setLastName("Mueller");
        c2.update();
        waitForRevisionTimestampChange();
        c2.setLastName("Schmidt");
        c2.update();
        DatabaseFactory.getDatabase().commit();
        assertRevisioned(11, "Meyer", "Mueller", "Schmidt");
    }

    static void assertRevisioned(long id, String... changedLastNames) throws Exception {
        WhereCondition where = new WhereCondition()
                .and("id", id)
                .orderBy(RevisionedRecordDescriptor.COLUMN_REVISION_TIMESTAMP, WhereCondition.Direction.ASC);
        CustomerRevision[] customerRevisions = (CustomerRevision[]) new CustomerRevision().query(where).toArray();
        assertNotNull("No revisioned entries found", customerRevisions);
        assertEquals("Expected number does not match actual revisions.", changedLastNames.length, customerRevisions.length);
        for (int i = 0; i < changedLastNames.length; i++) {
            assertEquals(changedLastNames[i], customerRevisions[i].getLastName());
            assertEquals(id, customerRevisions[i].getId());
        }
    }

    static void waitForRevisionTimestampChange() throws InterruptedException {
        Thread.sleep(1);
    }

}
