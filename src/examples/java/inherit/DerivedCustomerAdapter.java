package inherit;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author jlessner
 */
public class DerivedCustomerAdapter extends inherit.AbstractAdapter {
    public static final String TABLE = "CUSTOMER";
    public static final String COL_NAME = "name";
    public static final String COL_FIRST_NAME = "first_name";

    protected static final RecordDescriptor red = new RecordDescriptor
        (DerivedCustomerEntity.class, TABLE, inherit.AbstractAdapter.red, new String[][] {
            { COL_NAME,   "getName",   "setName" },
            { COL_FIRST_NAME,   "getFirstName",   "setFirstName" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { COL_ID };
    public String[] getKeyFields() { return keyFields; }

    DerivedCustomerAdapter(DerivedCustomerEntity entity) { super(entity); }


}
