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
package de.mathema.pride;

import java.sql.SQLException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ExtensionDescriptor
{
    private ExtendedAttributeDescriptor[] attrDescriptors;
    private RecordDescriptor master;

    public ExtensionDescriptor(RecordDescriptor master, String[][] attributeMap)
	throws IllegalDescriptorException {
        this.master = master;
        attrDescriptors = new ExtendedAttributeDescriptor[attributeMap.length];
        for (int i = 0; i < attributeMap.length; i++)
            attrDescriptors[i] = new ExtendedAttributeDescriptor
                (master.getObjectType(), attributeMap[i], RecordDescriptor.ExtractionMode.AUTO);
    }

    public RecordDescriptor getMaster() { return master; }
    public String getContext() { return master.getContext(); }
    public String getTableName() { return master.getTableName(); }
    public Class getObjectType() { return master.getObjectType(); }
    public String getPrimaryKeyField() { return master.getPrimaryKeyField(); }
    public Object getPrimaryKey(Object obj)
	throws IllegalAccessException, InvocationTargetException {
	return master.getPrimaryKey(obj);
    }

    public AttributeDescriptor[] getAttributeDescriptors() { return attrDescriptors; }
    public AttributeDescriptor getAttributeDescriptor(String attrName) {
        for (int i = 0; i < attrDescriptors.length; i++)
            if (attrDescriptors[i].getFieldName().equals(attrName))
                return attrDescriptors[i];
        return null;
    }

    public void applyResult(Object obj, String attrName, String attrValue)
        throws SQLException, ReflectiveOperationException {
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (attrDescriptors[i].getFieldName().equals(attrName)) {
                attrDescriptors[i].applyResult(obj, attrValue);
                return;
            }
        }
        throw new SQLException("illegal attribute " + attrName + " for object type " + master.getTableName());
    }
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/ExtensionDescriptor.java,v 1.1 2001/06/25 07:50:07 lessner Exp $";
}

/* $Log: ExtensionDescriptor.java,v $
/* Revision 1.1  2001/06/25 07:50:07  lessner
/* Database framework extended by support for generic attributes
/*
 */
