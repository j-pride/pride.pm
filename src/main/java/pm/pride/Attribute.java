/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
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
    protected RecordDescriptor getDescriptor() { return red; }

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
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/Attribute.java,v 1.3 2001/07/18 17:44:19 lessner Exp $";
}

/* $Log: Attribute.java,v $
/* Revision 1.3  2001/07/18 17:44:19  lessner
/* Deletion was buggy.
/*
/* Revision 1.2  2001/07/13 07:27:45  lessner
/* delete() method added
/*
/* Revision 1.1  2001/06/25 07:50:07  lessner
/* Database framework extended by support for generic attributes
/*
/* Revision 1.1  2001/06/22 10:33:25  lessner
/* Number generator classes introduced
/*
 */
