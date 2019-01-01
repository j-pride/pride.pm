package inherit;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author jlessner
 */
abstract public class AbstractAdapter extends ObjectAdapter {
    public static final String COL_ID = "id";

    protected static final RecordDescriptor red = new RecordDescriptor
        (AbstractEntity.class, null, null, new String[][] {
            { COL_ID,   "getId",   "setId" },
        });

    public RecordDescriptor getDescriptor() { return red; }

    private static String[] keyFields = new String[] { COL_ID };
    public String[] getKeyFields() { return keyFields; }

    AbstractAdapter(AbstractEntity entity) { super(entity); }


}
