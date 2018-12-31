package modify;

import java.sql.SQLException;
import java.util.List;

import pm.pride.*;

/**
 * The code here is completely intentionally redundant to keep from coupling the quick start
 * example to this very special auto-increment example which may not of interest for every padawan.
 * 
 * @author jlessner
 */
public class AutoIncCustomer extends MappedObject implements Cloneable, java.io.Serializable {
    public static final String TABLE = "CUSTOMER";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_FIRST_NAME = "first_name";

    protected static final RecordDescriptor red = new RecordDescriptor
        (AutoIncCustomer.class, "CUSTOMER", null, new String[][] {
            { COL_ID,   "getId",   "setId" },
            { COL_NAME,   "getName",   "setName" },
            { COL_FIRST_NAME,   "getFirstName",   "setFirstName" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { "id" };
    public String[] getKeyFields() { return keyFields; }
    public String[] getAutoFields() { return keyFields; }

    private long id;
    private String name;
    private String firstName;

    // Read access functions
    public long getId()   { return id; }
    public String getName()   { return name; }
    public String getFirstName()   { return firstName; }

    // Write access functions
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }


    // Re-constructor
    public AutoIncCustomer(long id) throws SQLException {
        setId(id);
        findx();
    }

    public AutoIncCustomer() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
    	return id + ": " + name + ", " + firstName;    	
    }
    
}

