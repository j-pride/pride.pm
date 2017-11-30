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

import java.util.*;
import java.sql.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * This class holds meta information about access methods to
 * JDBC prepared statements. It is required for PriDE's generic way
 * of writing record fields to the database.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class PreparedStatementAccess
{
    private static Map<String, Method> preparedStatementAccessMethods = null;

    public static void putMethod(String methodName, Class<?> paramType)
        throws NoSuchMethodException {
        putMethod(methodName, paramType, paramType);
    }

    /** Adds a prepared statement access method for a specified type
     * @param methodName Name of the access method to register. The
     *   method is assumed to be applicable to the PreparedStatement class
     *   for access by integer index.
     * @param functionParamType Type of parameter to be passed to the 
     *   prepared statement access method denoted by parameter methodName.
     * @param valueType Type of value returned by a getter method of a
     *   source object. This parameter is required to express mappings like
     *   Float -> float. If it is null, the functionParamType is used.
     * @throws NoSuchMethodException if the specified method is not
     *   available for class java.sql.ResultSet
     */
    public static void putMethod(String methodName, Class<?> functionParamType, Class<?> valueType)
        throws NoSuchMethodException {
        if (preparedStatementAccessMethods == null)
            preparedStatementAccessMethods = new HashMap<String, Method>();
        Class<?>[] params = new Class[] { int.class, functionParamType };
        Method method = PreparedStatement.class.getMethod(methodName, params);
        preparedStatementAccessMethods.put(valueType.getName(), method);
    }
    
    /** Initialize the class by the default type mappings below.
     * As a difference to the corresponding function in {@link ResultSetAccess}
     * there is no mapping for java.util.Date as this type is not supported
     * as input for prepared statements parameters. As a work around you may
     * provide additional getter methods in you entity, providing a conversion
     * to either java.sql.Date or java.sql.Timestamp.
     * <ul>
     * <li>String -> setString
     * <li>Enum -> setString
     * <li>java.util.Date -> setDate
     * <li>java.sql.Date -> setDate
     * <li>java.sql.Timestamp -> setTimestamp
     * <li>Integer/int -> setInt
     * <li>Float/float -> setFloat
     * <li>Double/double -> setDouble
     * <li>Boolean/boolean -> setBoolean
     * <li>BigDecimal -> setBigDecimal
     * <li>Long/long -> setLong
     * <li>Short/short -> setShort
     * <li>byte[] -> setBytes
     * <li>Byte/byte -> setByte
     * <li>Blob -> setBlob
     * <li>Clob -> setClob
     * <li>Map -> setObject, for Postgres only
     * <li>Any array type -> setArray, for Postgres only
     * </ul>
     */
    public static void init() throws NoSuchMethodException {
        putMethod("setString", String.class);
        putMethod("setString", String.class, Enum.class);
        putMethod("setTimestamp", Timestamp.class);
        putMethod("setDate", java.sql.Date.class);
        putMethod("setDate", java.sql.Date.class, java.util.Date.class);
        putMethod("setInt", int.class, Integer.class);
        putMethod("setInt", int.class);
        putMethod("setFloat", float.class, Float.class);
        putMethod("setFloat", float.class);
        putMethod("setDouble", double.class, Double.class);
        putMethod("setDouble", double.class);
        putMethod("setBoolean", boolean.class, Boolean.class);
        putMethod("setBoolean", boolean.class);
        putMethod("setBigDecimal", BigDecimal.class);
        putMethod("setLong", long.class, Long.class);
        putMethod("setLong", long.class);
        putMethod("setShort", short.class, Short.class);
        putMethod("setShort", short.class);
        putMethod("setBytes", byte[].class);
		putMethod("setByte", byte.class, Byte.class);
		putMethod("setByte", byte.class);
        putMethod("setBlob", Blob.class);
        putMethod("setClob", Clob.class);
        putMethod("setObject", Object.class, Map.class);
        putMethod("setArray", Array.class);
    }

    /** Returns the ResultSet by-name access method to be used for extracting fields
     * of the type referred to by parameter <code>attributeTypeName</code>.
     */
    static Method getAccessMethod(Class<?> attributeType) throws NoSuchMethodException {
		if (preparedStatementAccessMethods == null)
	    	init();
		if (attributeType.isArray()) {
		    return (Method)preparedStatementAccessMethods.get(Array.class.getName());
		}
        Method result = (Method)preparedStatementAccessMethods.get(attributeType.getName());
        if (result == null && attributeType.isEnum())
            result = (Method)preparedStatementAccessMethods.get(Enum.class.getName());
        return result;
    }

    public final static String REVISION_ID = "$Header:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/PreparedStatementAccess.java-arc   1.1   23 Sep 2002 12:34:06   math19  $";
}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/PreparedStatementAccess.java-arc  $
 * 
 *    Rev 1.1   23 Sep 2002 12:34:06   math19
 * Support for java.util.Date for ResultSets added. putMethod() functions are now public to allow adding self-defined type mappings.
 * 
 *    Rev 1.0   28 Aug 2002 15:32:44   math19
 * Initial revision.
/* Revision 1.4  2001/08/14 12:55:22  lessner
/* Timestamp introduced.
/*
/* Revision 1.3  2001/08/07 11:13:53  meister
/* Deleted Debug info
/*
/* Revision 1.2  2001/08/07 11:01:50  meister
/* Added long and short
/*
/* Revision 1.1  2001/08/06 15:55:47  lessner
/* Minor refactoring
/*
 */
