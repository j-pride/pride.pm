/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import java.sql.SQLException;

/**
 * Entity class to access the table of generic attributes
 *
 * @author <a href="mailto:jan.lessner@acoreus.de">Jan Lessner</a>
 */
public class Attribute extends MappedObject
{
    private static String[] primaryKey = new String[] { "object_type", "object", "name" };
    public String[] getKeyFields() { return primaryKey; }
    
    protected static RecordDescriptor red = new RecordDescriptor
        (Attribute.class, "attribute", null, new String[][] {
            { "object",      "getObject",     "setObject"     },
            { "object_type", "getObjectType", "setObjectType" },
            { "name",        "getName",       "setName"       },
            { "seq_no",      "getSeqNo",      "setSeqNo"      },
            { "value",       "getValue",      "setValue"      },
        });
    public RecordDescriptor getDescriptor() { return red; }

    private String objectType;
    private String object;
    private String name;
    private int seqNo;
    private String value;

    public String getObjectType() { return objectType; }
    public String getObject()     { return object;     }
    public String getName()       { return name;       }
    public int    getSeqNo()      { return seqNo;      }
    public String getValue()      { return value;      }

    public void setObjectType(String val) { objectType = val; }
    public void setObject    (String val) { object     = val; }
    public void setName      (String val) { name       = val; }
    public void setSeqNo     (int val)    { seqNo      = val; }
    public void setValue     (String val) { value      = val; }

    public Attribute(String objectType, String object, String attr)
        throws SQLException {
        this.objectType = objectType;
        this.object = object;
        this.name = attr;
        find();
    }

    public Attribute(String objectType, String object, String attr, String value)
        throws SQLException {
        this.objectType = objectType;
        this.object = object;
        this.name = attr;
        this.value = value;
        this.seqNo = 0;
    }
    
}
