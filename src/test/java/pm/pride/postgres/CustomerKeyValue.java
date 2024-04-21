package pm.pride.postgres;

import java.util.Map;

import pm.pride.basic.IdentifiedEntity;
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

    public RecordDescriptor getDescriptor() { return red; }


}
