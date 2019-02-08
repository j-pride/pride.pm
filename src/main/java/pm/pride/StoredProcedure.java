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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;

/**
 * This class simplifies calling stored procedures from Java code.
 * Derived types just have to declare the procedure's parameters
 * as public members. Input parameters must be declared final,
 * output parameters as non-final members. In/out parameters are
 * not supported because I didn't find an elegant way to do yet.
 * The procedure is called by running the {@link #execute} method.
 * E.g. the following class would run the procedure <tt>myproc</tt>
 * with two input Strings and one output integer:
 * <pre>
 *   public class myproc extends StoredProcedure {
 *
 *     public final String p1;
 *     public final String p2;
 *     public int p3;
 *
 *     public myproc(String p1, String p2) {
 *       this.p1 = p1;
 *       this.p2 = p2;
 *     }
 *   }
 * </pre>
 * The SQL types of the parameters are derived from the member's
 * Java types. Currently there are the following types supported:
 * <ul>
 * <li>String -&gt; VARCHAR
 * <li>BigDecimal -&gt; DECIMAL
 * <li>int -&gt; INTEGER
 * <li>Integer -&gt; INTEGER
 * <li>long -&gt; BIGINT
 * <li>Long -&gt; BIGINT
 * <li>java.sql.Date -&gt; DATE
 * <li>java.sql.Timestamp -&gt; TIMESTAMP
 * <li>java.util.Date -&gt; TIMESTAMP
 * </ul>
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public abstract class StoredProcedure {

    protected Database db;

    /** Returns a database required by formatting and logging functions. If
     * the {@link #execute} method was called with a {@link Database} object.
     * This object is returned, otherwise DatabaseFactory.getDatabase()
     */
    public Database getDatabase() {
        return (db != null) ? db : DatabaseFactory.getDatabase();
    }
    
    /** Returns true if the execution of the stored procedure should be logged.
     * The function returns getDatabase().isLogging() by default and can be
     * overridden in derived classes if required.
     */
    public boolean isLogging() { return getDatabase().isLogging(); }
    
    /** Called by the execution method when logging is enabled
     * (see {@link #isLogging}) to write an execution string to
     * log media. This function calls getDatabase().sqlLog() by
     * default and can be overridden in derived classes if required.
     */
    public void log(String operation) {
        getDatabase().sqlLog(operation);
    }

    /** Format a value as string. This is only used for logging purposes in this class */
    public String format(Object value, Class<?> targetType) {
        return getDatabase().formatValue(value, targetType);
    }
    
    /** Returns the name of the stored procedure to call.
     * This is by default the name of the derived class
     * (without package).
     */
    protected String getName() {
        String result = getClass().getName();
        int dot = result.lastIndexOf('.');
        return (dot == -1) ? result : result.substring(dot+1);
    }

    /** Returns the number of parameters for the SP call.
     * This is extracted from counting all non-static, public
     * data members of the class.
     */
    protected int getNumParams() {
        int num = 0;
        Field[] fields = getClass().getFields();
        for (int f = 0; f < fields.length; f++) {
            int modifiers = fields[f].getModifiers();
            if (!Modifier.isStatic(modifiers) &&
                !Modifier.isPrivate(modifiers))
                num++;
        }
        return num;
    }

    /** Sets an input values for an SP call
     * @param stmt The callable statement, representing the SP to call
     * @param field The data member holding the value to set
     * @param index The parameter's index in the statement's signature,
     *   beginning with index 1.
     */
    protected String setInParam(CallableStatement stmt, Field field, int index)
        throws IllegalDescriptorException, SQLException {
        try {
            Object value = field.get(this);
            Class type = field.getType();
            if (value == null)
                stmt.setNull(index, getSQLType(type));
            else if (type == String.class)
                stmt.setString(index, (String)value);
            else if (type == BigDecimal.class)
                stmt.setBigDecimal(index, (BigDecimal)value);
            else if (type == int.class || type == Integer.class)
                stmt.setInt(index, ((Integer)value).intValue());
            else if (type == long.class || type == Long.class)
                stmt.setLong(index, ((Long)value).longValue());
            else if (type == Date.class)
                stmt.setDate(index, (Date)value);
            else if (type == Timestamp.class)
                stmt.setTimestamp(index, (Timestamp)value);
            else if (type == java.util.Date.class)
                stmt.setTimestamp(index, new Timestamp(((java.util.Date)value).getTime()));
            else
                throw new IllegalDescriptorException("unsupported type " + type);
            return isLogging() ? format(value, type) + " " : null;
        }
        catch(IllegalAccessException iax) {
            throw new IllegalDescriptorException("illegal access on " + field.getName());
        }
    }

    /** Returns the SQL type equivalent to the passed Java type */
    protected int getSQLType(Class type) throws IllegalDescriptorException {
        if (type == String.class)
            return java.sql.Types.VARCHAR;
        if (type == BigDecimal.class)
            return java.sql.Types.DECIMAL;
        if (type == int.class || type == Integer.class)
            return java.sql.Types.INTEGER;
        if (type == long.class || type == Long.class)
            return java.sql.Types.BIGINT;
        if (type == Date.class)
            return java.sql.Types.DATE;
        if (type == Timestamp.class)
            return java.sql.Types.TIMESTAMP;
        if (type == java.util.Date.class)
            return java.sql.Types.TIMESTAMP;
        throw new IllegalDescriptorException("unsupported type " + type);
    }

    /** Sets an output value types for an SP call
     * @param stmt The callable statement, representing the SP to call
     * @param field The data member to derive the type from.
     * @param index The parameter's index in the statement's signature,
     *   beginning with index 1.
     */
    protected String setOutParamType(CallableStatement stmt, Field field, int index)
        throws IllegalDescriptorException, SQLException {
        Class type = field.getType();
        stmt.registerOutParameter(index, getSQLType(type));
        return isLogging() ? "? " : null;
    }

    protected String setParams(CallableStatement stmt)
        throws IllegalDescriptorException, SQLException {
        StringBuffer log = new StringBuffer();
        Field[] fields = getClass().getFields();
        int paramIndex = 1;
        for (int f = 0; f < fields.length; f++) {
            int modifiers = fields[f].getModifiers();
            if (!Modifier.isStatic(modifiers) &&
                !Modifier.isPrivate(modifiers)) {
                if (Modifier.isFinal(modifiers))
                    log.append(setInParam(stmt, fields[f], paramIndex));
                else
                    log.append(setOutParamType(stmt, fields[f], paramIndex));
                paramIndex++;
            }
        }
        return log.toString();
    }
    
    protected String getOutParam(CallableStatement stmt, Field field, int index)
        throws IllegalDescriptorException, SQLException {
        try {
            Class type = field.getType();
            if (type == String.class)
                field.set(this, stmt.getString(index));
            else if (type == BigDecimal.class)
                field.set(this, stmt.getBigDecimal(index));
            else if (type == int.class)
                field.setInt(this, stmt.getInt(index));
            else if (type == Integer.class)
                field.set(this, new Integer(stmt.getInt(index)));
            else if (type == long.class)
                field.setLong(this, stmt.getLong(index));
            else if (type == Long.class)
                field.set(this, new Long(stmt.getLong(index)));
            else if (type == Date.class)
                field.set(this, stmt.getDate(index));
            else if (type == Timestamp.class)
                field.set(this, stmt.getTimestamp(index));
            else if (type == java.util.Date.class)
                field.set(this, stmt.getTimestamp(index));
            else
                throw new IllegalDescriptorException("unsupported type " + type);
            if (stmt.wasNull() && type!=int.class && type!=long.class)
                field.set(this, null);
            return isLogging() ? format(field.get(this), field.getType()) + " " : null;
        }
        catch(IllegalAccessException iax) {
            throw new IllegalDescriptorException("illegal access on " + field.getName());
        }
    }
    
    protected String getOutParams(CallableStatement stmt)
        throws IllegalDescriptorException, SQLException {
        StringBuffer log = new StringBuffer();
        Field[] fields = getClass().getFields();
        int paramIndex = 1;
        for (int f = 0; f < fields.length; f++) {
            int modifiers = fields[f].getModifiers();
            if (!Modifier.isStatic(modifiers) &&
                !Modifier.isPrivate(modifiers)) {
                if (!Modifier.isFinal(modifiers))
                    log.append(getOutParam(stmt, fields[f], paramIndex));
                else if (isLogging()) {
                    try { log.append(format(fields[f].get(this), fields[f].getType()) + " "); }
                    catch(IllegalAccessException iax) {
                        throw new IllegalDescriptorException("illegal access on " + fields[f].getName());
                    }
                }
                paramIndex++;
            }
        }
        return log.toString();
    }

    protected String assembleCallString() {
        StringBuffer assembly = new StringBuffer("{call ");
        assembly.append(getName());
        assembly.append("(");
        int numParams = getNumParams();
        for (int p = 0; p < numParams; p++)
            assembly.append("?,");
        if (numParams != 0)
            assembly.deleteCharAt(assembly.length()-1);
        assembly.append(")}");
        return assembly.toString();
    }

    /** Executes the stored procedure represented by this object on the passed database
     * connection. If logging is enabled, ths functions writes an appropriate string to
     * the log media.
     * @throws SQLException if the stored procedure call failed
     * @throws IllegalDescriptorException if the stored procedure
     *   class is somehow malformed.
     */
    public void execute(Connection con)
        throws SQLException, IllegalDescriptorException {
        CallableStatement stmt = null;
        String logString = null;
        try {
            String callString = assembleCallString();
            stmt = con.prepareCall(callString);
            logString = setParams(stmt);
            stmt.executeUpdate();
            logString = getOutParams(stmt);
            if (isLogging()) {
                /* This is somewhat tricky: If the SP runs successfully, there is a
                 * log string assembled in getOutParams() including both input and
                 * output parameters. Otherwise, the log string assembled by setParams()
                 * is logged in the finally block which includes at least the input
                 * parameters
                 */
                log(getName() + "(" + logString + ")");
                logString = null;
            }
        }
        finally {
            if (isLogging() && logString != null)
                log(getName() + "(" + logString + ")");
            if (stmt != null)
                stmt.close();
        }
    }

    /** Executes the stored procedure represented by this object on the passed database.
     * If logging is enabled, the functions writes an appropriate string to the log media.
     * @throws SQLException if the stored procedure call failed
     * @throws IllegalDescriptorException if the stored procedure
     *   class is somehow malformed.
     */
    public void execute(Database db)
        throws SQLException, IllegalDescriptorException {
        this.db = db;
        Connection con = null;
        try {
            con = getDatabase().getConnection();
            execute(con);
        }
        finally {
            if (con != null)
                con.close();
        }
    }
    
    public final static String REVISION_ID = "$Header:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/StoredProcedure.java-arc   1.1   23 Sep 2002 14:08:52   math19  $";
}
