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
 * Simple {@link ResourceAccessor} for J2EE environments.
 * This implementation fetches database connections from a
 * {@link DataSource} and assumes that connections are pooled
 * by the application server.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResourceAccessorJEE extends AbstractResourceAccessor
{
	protected final static String LOCAL_JNDI_CONTEXT = "java:comp/env";
	
	//--------------- J D B C   c o n n e c t i o n   h a n d l i n g  -------------
	protected DataSource globalDataSource = null;

	/** Return a JDBC database connection.
	 * The passed database name is interpreted as a JNDI name used to
	 * lookup a {@link DataSource} to get a connection from. If the JNDI
	 * name starts with java:comp/env, the lookup is performed in the
	 * current EJBs local environment with every single call. Otherwise
	 * the lookup is performed only once and the DataSource is stored as
	 * a member variable for reuse in subsequent calls of <code>getConnection</code>.
	 * If the application performs many simple database interactions, it
	 * is strongly recommended to use global JNDI names for the sake of
	 * performance.
	 */
	public Connection getConnection(String db)
		throws SQLException, NamingException {
		DataSource ds;
		if (globalDataSource == null || db.startsWith(LOCAL_JNDI_CONTEXT)) {
			Context ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(db);
			if (!db.startsWith(LOCAL_JNDI_CONTEXT))
				globalDataSource = ds;
		}
		else
			ds = globalDataSource;
		return (dbUser == null || dbPassword == null) ?
			ds.getConnection() : ds.getConnection(dbUser, dbPassword);
	}

	/** Does nothing at all, we rely on the connection handling
	 * of the application server
	 */
	public void releaseConnection() throws SQLException { }

	/** Calls the connection's close function which is supposed to
	 * cause a fast logical releasement.
	 */
	public void releaseConnection(Connection con) throws SQLException {
		con.close();
	}


	//--------------- C o n s t r u c t o r  -------------

	/** Constructs a new ResourceAccessor for use in a J2SE environment
	 * @param props The configuration for the resource accessor. See interface
	 * {@link ResourceAccessor.Config} for available parameters.
	 */
	public ResourceAccessorJEE(Properties props)
		throws Exception {
		super(props);
	}

    public final static String REVISION_ID = "$Header: ";
}
