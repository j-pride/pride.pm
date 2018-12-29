package adapter;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author jlessner
 */
public class CustomerAdapter extends ObjectAdapter<CustomerEntity> {
    public static final String TABLE = "CUSTOMER";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_FIRST_NAME = "first_name";

    protected static final RecordDescriptor red = new RecordDescriptor
        (CustomerEntity.class, TABLE, null, new String[][] {
            { COL_ID,   "getId",   "setId" },
            { COL_NAME,   "getName",   "setName" },
            { COL_FIRST_NAME,   "getFirstName",   "setFirstName" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { COL_ID };
    public String[] getKeyFields() { return keyFields; }

    CustomerAdapter(CustomerEntity entity) { super(entity); }


}
