package basic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import pm.pride.RecordDescriptor;
import pm.pride.RevisionedRecordDescriptor;

public class CustomerRevision extends Customer {

    protected static RecordDescriptor redToReadFromRevisioningTable =
            new RecordDescriptor(CustomerRevision.class, AbstractPrideTest.REVISIONING_TEST_TABLE, IdentifiedEntity.red, new String[][]{
                    {"firstName", "getFirstName", "setFirstName"},
                    {"lastName", "getLastName", "setLastName"},
                    {"hireDate", "getHireDate", "setHireDate"},
                    {"active", "getActive", "setActive"},
                    {RevisionedRecordDescriptor.COLUMN_REVISION_TIMESTAMP, "getRevisionedTimestamp", "setRevisionedTimestamp"}
            }
            );
    private Timestamp revisionedTimestamp;

    public CustomerRevision() {
    }

    public CustomerRevision(int id) throws SQLException {
        super(id);
    }

    public CustomerRevision(int id, String firstName, String lastName) throws SQLException {
        super(id, firstName, lastName);
    }

    public CustomerRevision(int id, String firstName, String lastName, Boolean active) throws SQLException {
        super(id, firstName, lastName, active);
    }

    public CustomerRevision(int id, String firstName, String lastName, Boolean active, Date hireDate) throws SQLException {
        super(id, firstName, lastName, active, hireDate);
    }

    @Override
    protected RecordDescriptor getDescriptor() {
        return redToReadFromRevisioningTable;
    }

    public Timestamp getRevisionedTimestamp() {
        return revisionedTimestamp;
    }

    public void setRevisionedTimestamp(Timestamp revisionedTimestamp) {
        this.revisionedTimestamp = revisionedTimestamp;
    }
}
