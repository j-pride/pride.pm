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

import java.sql.*;
import java.util.Properties;

/**
 * Simple {@link ResourceAccessor} for J2SE environments.
 * This implementation stores database connections in a {@link ThreadLocal}
 * object, which garuantees fast connection access and multi-threading safety.
 * However, the connection is kept until the accessor is invoked with a different
 * database name. It is therefore recommended to work with multiple accessors
 * if you need to swith rapidly between multiple databases.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResourceAccessorJ2SE extends AbstractResourceAccessor
{
    //--------------- J D B C   c o n n e c t i o n   h a n d l i n g  -------------
    private final ThreadLocal dbConnection;
    private final String dbDriver;
    private String db;

    /** Return a JDBC database connection.
     * There is one unique connection returned per thread and kept allocated
     * until this function is invoked with a different database name or
     * function {@link #releaseConnection} is explicitely called. The
     * connection is optained from DriverManager.getConnection() passing
     * either user and password when specified in the constructor by
     * properties {@link ResourceAccessor.Config#USER} and {@link ResourceAccessor.Config#USER}
     * or otherwise passing the complete constructor Properties list as is.
     */
    public Connection getConnection(String db) throws SQLException {
    	if (!db.equals(this.db)) {
    		releaseConnection();
    		this.db = db;
    	}

    	// If connection is closed, force reconnection
        if (dbConnection.get() != null && ((Connection)dbConnection.get()).isClosed())
            dbConnection.set(null);

        if (dbConnection.get() == null) {
            Connection con;
		    if (dbUser == null || dbPassword == null) {
                con = (props != null) ?
                    DriverManager.getConnection(db, props) :
                    DriverManager.getConnection(db);
		    }
		    else
				con = DriverManager.getConnection(db, dbUser, dbPassword);
            setAutoCommit(con, false);
            dbConnection.set(con);
        }
        return (Connection)dbConnection.get();
    }

    /** Close the current thread's connection.
     * It is recommended to explicitely release connections if the associated
     * thread is about to terminate or sleep for a long time. JDBC's built-in
     * connection garbage collection is either not working at all or is not
     * this agile as it may be required.
     */
    public void releaseConnection() throws SQLException {
        if (dbConnection.get() != null) {
            Connection con = (Connection)dbConnection.get();
            con.close();
            dbConnection.set(null);
        }
    }

    /** Does nothing at all */
    public void releaseConnection(Connection con) throws SQLException {}


    //--------------- C o n s t r u c t o r  -------------
    
    /** Constructs a new ResourceAccessor for use in a J2SE environment
     * @param props The configuration for the resource accessor. See interface
     * {@link ResourceAccessor.Config} for available parameters. The property
     * {@link ResourceAccessor.Config#DRIVER} is mandatory
     */
    public ResourceAccessorJ2SE(Properties props)
    	throws Exception {
    	super(props);
		dbDriver = props.getProperty(Config.DRIVER);
		dbConnection = new ThreadLocal();
        if (dbDriver != null)
            Class.forName(dbDriver).newInstance();
	}

    public final static String REVISION_ID = "$Header: ";
}
