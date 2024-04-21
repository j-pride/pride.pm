/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import java.util.*;
import java.sql.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * This class holds meta information about access methods to
 * JDBC result sets. It is required for PriDE's generic way
 * of fetching record fields from an SQL query result.
 * By default, the class is initialized by its {@link #init()}
 * method on the first call of {@link #getAccessMethod(Class)} or
 * {@link #getIndexAccessMethod(Class)} which in turn are called
 * by the constructors of class {@link RecordDescriptor}.
 * However, you might be required to change the default
 * mappings according to your driver's abilities. E.g.
 * the default MySQL JDBC driver turned out not to support
 * making timestamps from ordinary date strings.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResultSetAccess
{
    private static Map resultSetAccessMethods = null;
    private static Map resultSetAccessMethodsByIndex = null;

    /** Adds a result set access method for a specified type
     * @param methodName Name of the access method to register. The
     *   method is assumed to be applicable to the ResultSet class
     *   for both access by name and access by index.
     * @param valueType Name of the type to be accessed by the method
     *   denoted by parameter methodName.
     * @throws NoSuchMethodException if the specified method is not
     *   available for class java.sql.ResultSet
     */
    public static void putMethod(String methodName, Class valueType)
        throws NoSuchMethodException {

        if (resultSetAccessMethods == null)
            resultSetAccessMethods = new HashMap();
        Method byNameMethod = findResultSetAccessMethod(methodName, String.class);
        resultSetAccessMethods.put(valueType.getName(), byNameMethod);

        if (resultSetAccessMethodsByIndex == null)
            resultSetAccessMethodsByIndex = new HashMap();
        Method byIndexMethod = findResultSetAccessMethod(methodName, int.class);
        resultSetAccessMethodsByIndex.put(valueType.getName(), byIndexMethod);
    }

    protected static Method findResultSetAccessMethod(String methodName, Class<?> addressType)
        throws NoSuchMethodException {
        Class<?>[] params = new Class<?>[] { addressType };
        return ResultSet.class.getMethod(methodName, params);
    }
    
    /** Initialize the class by the following default type mappings:
     * <ul>
     * <li>String -&gt; getString
     * <li>Enum -&gt; getString
     * <li>java.util.Date -&gt; getDate
     * <li>java.sql.Date -&gt; getDate
     * <li>java.sql.Timestamp -&gt; getTimestamp
     * <li>Integer/int -&gt; getInt
     * <li>Float/float -&gt; getFloat
     * <li>Double/double -&gt; getDouble
     * <li>Boolean/boolean -&gt; getBoolean
     * <li>BigDecimal -&gt; getBigDecimal
     * <li>Long/long -&gt; getLong
     * <li>Short/short -&gt; getShort
     * <li>Byte/byte -&gt; getByte
     * <li>byte[] -&gt; getBytes
     * <li>Blob -&gt; getBlob
     * <li>Clob -&gt; getClob
     * <li>Map&lt;String, String&gt; -&gt; getObject, for Postgres only
     * <li>Any array type -&gt; getObject, for Postgres only
     * </ul>
     */
    public static void init() throws NoSuchMethodException {
        putMethod("getString", String.class);
        putMethod("getString", Enum.class);
		putMethod("getDate", java.util.Date.class);
        putMethod("getDate", java.sql.Date.class);
        putMethod("getTimestamp", java.sql.Timestamp.class);
        putMethod("getInt", Integer.class);
        putMethod("getInt", int.class);
        putMethod("getFloat", Float.class);
        putMethod("getFloat", float.class);
        putMethod("getDouble", Double.class);
        putMethod("getDouble", double.class);
        putMethod("getBoolean", Boolean.class);
        putMethod("getBoolean", boolean.class);
        putMethod("getBigDecimal", BigDecimal.class);
        putMethod("getLong", Long.class);
        putMethod("getLong", long.class);
        putMethod("getShort", Short.class);
        putMethod("getShort", short.class);
        putMethod("getBytes", byte[].class);
		putMethod("getByte", Byte.class);
		putMethod("getByte", byte.class);
        putMethod("getBlob", Blob.class);
        putMethod("getClob", Clob.class);
        putMethod("getObject", Map.class);
        putMethod("getObject", Array.class);
        putMethod("getSQLXML", SQLXML.class);
    }

    static Method getAccessMethod(Map source, Class attributeType)  throws NoSuchMethodException {
        if (attributeType != byte[].class && attributeType.isArray())
            attributeType = Array.class;
        Method result = (Method)source.get(attributeType.getName());
        if (result == null && attributeType.isEnum())
            result = (Method)source.get(Enum.class.getName());
        if (result == null)
            throw new NoSuchMethodException
                ("No mapping for attribute type " + attributeType);
        return result;
        
    }
    
    /** Returns the ResultSet by-name access method to be used for extracting fields
     * of the type referred to by parameter <code>attributeTypeName</code>.
     */
    static Method getAccessMethod(Class attributeType) throws NoSuchMethodException {
        if (resultSetAccessMethods == null)
            init();
        return getAccessMethod(resultSetAccessMethods, attributeType);
    }

    /** Returns the ResultSet by-index access method to be used for extracting fields
     * of the type referred to by parameter <code>attributeTypeName</code>.
     */
    static Method getIndexAccessMethod(Class attributeType) throws NoSuchMethodException {
        if (resultSetAccessMethodsByIndex == null)
            init();
        return getAccessMethod(resultSetAccessMethodsByIndex, attributeType);
    }

}
