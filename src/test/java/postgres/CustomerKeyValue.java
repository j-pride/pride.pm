package postgres;

import java.sql.SQLException;
import java.util.Map;

import basic.Customer;
import basic.IdentifiedEntity;
import pm.pride.RecordDescriptor;

public class CustomerKeyValue extends IdentifiedEntity {
    Map<String, String> contacts;
    
    public Map<String, String> getContacts() { return contacts; }
    public void setContacts(Map<String, String> contacts) { this.contacts = contacts; }

    public CustomerKeyValue(int id) { super(id); }

    protected static RecordDescriptor red =
            new RecordDescriptor(CustomerKeyValue.class, PostgresKeyValueTest.KEY_VALUE_TEST_TABLE,
                    IdentifiedEntity.red, new String[][] {
                { "contacts", "getContacts", "setContacts"}
            }
        );

    protected RecordDescriptor getDescriptor() { return red; }


}
