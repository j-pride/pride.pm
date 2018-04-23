package basic;

import de.mathema.pride.RecordDescriptor;
import de.mathema.pride.RevisionedRecordDescriptor;

import java.sql.SQLException;
import java.util.Date;

public class RevisionedCustomer extends Customer {

    protected static RevisionedRecordDescriptor red =
            new RevisionedRecordDescriptor(Customer.class, AbstractPrideTest.TEST_TABLE, AbstractPrideTest.REVISIONING_TEST_TABLE, IdentifiedEntity.red, new String[][]{
                    {"firstName", "getFirstName", "setFirstName"},
                    {"lastName", "getLastName", "setLastName"},
                    {"hireDate", "getHireDate", "setHireDate"},
                    {"active", "getActive", "setActive"},
                    {"type", "getType", "setType"}
            }
            );

    public RevisionedCustomer() {
    }

    public RevisionedCustomer(int id) throws SQLException {
        super(id);
    }

    public RevisionedCustomer(int id, String firstName, String lastName) throws SQLException {
        super(id, firstName, lastName);
    }

    public RevisionedCustomer(int id, String firstName, String lastName, Boolean active) throws SQLException {
        super(id, firstName, lastName, active);
    }

    public RevisionedCustomer(int id, String firstName, String lastName, Boolean active, Date hireDate) throws SQLException {
        super(id, firstName, lastName, active, hireDate);
    }

    @Override
    protected RecordDescriptor getDescriptor() {
        return red;
    }

}
