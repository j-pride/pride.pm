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

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ExtendedAttributeDescriptor extends AttributeDescriptor
{
    private static Map conversionMethods = null;

    protected static void setConversionMethods() throws NoSuchMethodException {
	conversionMethods = new HashMap();
	Class[] params = new Class[] { String.class };
	conversionMethods.put
	    (Integer.class.getName(), Integer.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    ("int", Integer.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    (Float.class.getName(), Float.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    ("float", Float.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    (Double.class.getName(), Double.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    ("double", Double.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    (Boolean.class.getName(), Boolean.class.getDeclaredMethod("valueOf", params));
	conversionMethods.put
	    ("boolean", Boolean.class.getDeclaredMethod("valueOf", params));
    }

    protected static Method getConversionMethod(Class destinationType)
	throws NoSuchMethodException {
        if (conversionMethods == null)
            setConversionMethods();
        return (Method)conversionMethods.get(destinationType.getName());
    }
    
    protected Method conversionMethod = null;
    
    public ExtendedAttributeDescriptor(Class objectType, String[] attrInfo, int extractionMode)
		throws IllegalDescriptorException {
		super(objectType, attrInfo, extractionMode);
		try {
		    Class setMethodParamType = setMethod.getParameterTypes()[0];
		    if (setMethodParamType != String.class) {
			conversionMethod = getConversionMethod(setMethodParamType);
			if (conversionMethod == null)
			    throw new IllegalDescriptorException
				("Mapping from string to " + setMethodParamType.getName() + " not implemented");
		    }
		}
		catch(NoSuchMethodException x) {
		    throw new IllegalDescriptorException("Error in static initialization: " + x.toString());
		}
    }

    public void applyResult(Object obj, String stringValue)
	throws IllegalAccessException, InvocationTargetException {
	Object value = (conversionMethod != null) ?
	    conversionMethod.invoke(null, new Object[] { stringValue }) : stringValue;
	setMethod.invoke(obj, new Object[] { value });
    }
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/ExtendedAttributeDescriptor.java,v 1.3 2001/06/25 13:31:42 lessner Exp $";
}

/* $Log: ExtendedAttributeDescriptor.java,v $
/* Revision 1.3  2001/06/25 13:31:42  lessner
/* Added support for boolean attributes.
/*
/* Revision 1.2  2001/06/25 09:38:25  lessner
/* Double support added.
/*
/* Revision 1.1  2001/06/25 07:50:07  lessner
/* Database framework extended by support for generic attributes
/*
 */
