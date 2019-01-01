package inherit;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author jlessner
 */
abstract public class AbstractHybrid extends MappedObject implements Cloneable, java.io.Serializable {
    public static final String COL_ID = "id";

    protected static final RecordDescriptor red = new RecordDescriptor
        (AbstractHybrid.class, null, null, new String[][] {
            { COL_ID,   "getId",   "setId" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { COL_ID };
    public String[] getKeyFields() { return keyFields; }

    private int id;

    // Read access functions
    public int getId()   { return id; }

    // Write access functions
    public void setId(int id) { this.id = id; }


    // Re-constructor
    public AbstractHybrid(int id) throws SQLException {
        setId(id);
        findx();
    }

    public AbstractHybrid() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
