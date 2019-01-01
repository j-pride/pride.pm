package inherit;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author jlessner
 */
public class DerivedCustomer extends inherit.AbstractHybrid {
    public static final String TABLE = "CUSTOMER";
    public static final String COL_NAME = "name";
    public static final String COL_FIRST_NAME = "first_name";

    protected static final RecordDescriptor red = new RecordDescriptor
        (DerivedCustomer.class, TABLE, inherit.AbstractHybrid.red, new String[][] {
            { COL_NAME,   "getName",   "setName" },
            { COL_FIRST_NAME,   "getFirstName",   "setFirstName" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { COL_ID };
    public String[] getKeyFields() { return keyFields; }

    private String name;
    private String firstName;

    // Read access functions
    public String getName()   { return name; }
    public String getFirstName()   { return firstName; }

    // Write access functions
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }


    // Re-constructor
    public DerivedCustomer(int id) throws SQLException {
        super(id);
    }

    public DerivedCustomer() {}

}

