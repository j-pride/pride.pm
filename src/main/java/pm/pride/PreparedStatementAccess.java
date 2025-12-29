/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
     *   Float -&gt; float. If it is null, the functionParamType is used.
     * @throws NoSuchMethodException if the specified method is not
     *   available for class java.sql.ResultSet
     */
    public static void putMethod(String methodName, Class<?> functionParamType, Class<?> valueType)
        throws NoSuchMethodException {
        if (preparedStatementAccessMethods == null)
            preparedStatementAccessMethods = new HashMap<>();
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
     * <li>String -&gt; setString
     * <li>Enum -&gt; setString
     * <li>java.util.Date -&gt; setDate
     * <li>java.sql.Date -&gt; setDate
     * <li>java.sql.Timestamp -&gt; setTimestamp
     * <li>Integer/int -&gt; setInt
     * <li>Float/float -&gt; setFloat
     * <li>Double/double -&gt; setDouble
     * <li>Boolean/boolean -&gt; setBoolean
     * <li>BigDecimal -&gt; setBigDecimal
     * <li>Long/long -&gt; setLong
     * <li>Short/short -&gt; setShort
     * <li>byte[] -&gt; setBytes
     * <li>Byte/byte -&gt; setByte
     * <li>Blob -&gt; setBlob
     * <li>Clob -&gt; setClob
     * <li>Map -&gt; setObject, for Postgres only
     * <li>Any array type -&gt; setArray, for Postgres only
     * </ul>
     */
    public static void init() throws NoSuchMethodException {
        putMethod("setString", String.class);
        putMethod("setString", String.class, Enum.class);
        putMethod("setDate", java.sql.Date.class, java.util.Date.class);
        putMethod("setDate", java.sql.Date.class);
        putMethod("setDate", java.sql.Date.class, LocalDate.class);
        putMethod("setTimestamp", Timestamp.class);
        putMethod("setTimestamp", Timestamp.class, LocalDateTime.class);
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
        putMethod("setArray", Array.class);
        putMethod("setSQLXML", SQLXML.class);
    }

    /** Returns the ResultSet by-name access method to be used for extracting fields
     * of the type referred to by parameter <code>attributeTypeName</code>.
     */
    static Method getAccessMethod(Class<?> attributeType) throws NoSuchMethodException {
		if (preparedStatementAccessMethods == null)
	    	init();
		if (attributeType != byte[].class && attributeType.isArray()) {
		    return (Method)preparedStatementAccessMethods.get(Array.class.getName());
		}
        Method result = (Method)preparedStatementAccessMethods.get(attributeType.getName());
        if (result == null && attributeType.isEnum())
            result = (Method)preparedStatementAccessMethods.get(Enum.class.getName());
        return result;
    }
}
