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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.*;
import javax.sql.DataSource;

/**
 * {@link ResourceAccessor} for servlet engines.
 * This implementation is somehow a mixture of what is done by {@link ResourceAccessorJ2EE}
 * and {@link ResourceAccessorJ2SE}. It fetches database connections from a
 * {@link DataSource} but keeps it alive in a ThreadLocal until it is explicitely
 * released.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResourceAccessorWeb extends ResourceAccessorJ2EE
{
    //--------------- J D B C   c o n n e c t i o n   h a n d l i n g  -------------
	private final ThreadLocal dbConnection;
	private String db;

    /** Return a JDBC database connection.
     * The passed database name is interpreted as a JNDI name used to
     * lookup a {@link DataSource} to get a connection from. If the JNDI
     * name doesn't start with "java:comp/env", it is extended accordingly.
	 * The lookup is performed only once and the DataSource is stored as
     * a member variable for reuse in subsequent calls of <code>getConnection</code>.
     */
    public Connection getConnection(String db)
        throws SQLException, NamingException {

		if (!db.equals(this.db)) {
			releaseConnection();
			globalDataSource = null;
			this.db = db;
		}

		if (dbConnection.get() == null) {
	        Connection con;
	        if (!db.startsWith(LOCAL_JNDI_CONTEXT))
	          	db = LOCAL_JNDI_CONTEXT + "/" + db;
	        if (globalDataSource == null) {
	            Context ctx = new InitialContext();
	            globalDataSource = (DataSource)ctx.lookup(db);
	        }
	        con = (dbUser == null || dbPassword == null) ?
	            globalDataSource.getConnection() :
	            globalDataSource.getConnection(dbUser, dbPassword);
			setAutoCommit(con, false);
			dbConnection.set(con);            
		}
		return (Connection)dbConnection.get(); 
    }

	/** Close the current thread's connection.
	 * This function must be explicitely called at the top-level of the call
	 * chain before the servlet end up its job. This is the same thing like in
	 * J2SE environments.
	 */
	public void releaseConnection() throws SQLException {
		if (dbConnection.get() != null) {
			Connection con = (Connection)dbConnection.get();
			con.close();
			dbConnection.set(null);
		}
	}

	/** Does nothing at all. Although working with a data source and 
	 * a pooled connection, the servlet engine by default does not perform
	 * any transaction management and would place the connection back into
	 * the pool and abort the current work. So we keep it alive.
	 */
	public void releaseConnection(Connection con) throws SQLException {}

    //--------------- C o n s t r u c t o r  -------------

    /** Constructs a new ResourceAccessor for use in a J2SE environment
     * @param props The configuration for the resource accessor. See interface
     * {@link ResourceAccessor.Config} for available parameters.
     */
    public ResourceAccessorWeb(Properties props)
        throws Exception {
        super(props);
		dbConnection = new ThreadLocal();
    }

    public final static String REVISION_ID = "$Header: ";
}
