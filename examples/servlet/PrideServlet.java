/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software Ltd. - Release 2.0.3
 *******************************************************************************/
package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;

import de.mathema.pride.*;
import de.mathema.util.Singleton;

/**
 * Abstract base class for servlets, performing database operations
 * via PriDE. The class ensures that PriDE is initialized before usage
 * and any connections are safely released before termination. Derived
 * servlet classes should implement the {@link #doSafeGet} method.
 * 
 * @author <a href="mailto:matthias.bartels@bertelsmann.de>Matthias Bartels</a>
 */
public abstract class PrideServlet extends HttpServlet {

	/**
	 * Alternative exception listener, overriding the default behaviour
	 * of calling System.exit in case of a severe error condition.
	 * The listener below is also just a hack and should be replaced
	 * by something serious in a real project
	 */
	protected static class ServletExceptionListener implements ExceptionListener {
		public void process(Database db, Exception x) throws Exception { throw x; }
		public void processSevere(Database db, Exception x) {
			x.printStackTrace();
			throw new RuntimeException(x.getMessage());
		};
	}
	
	/**
	 * Singleton initializerfor PriDE. See the patterns section in
	 * the documentation for details
	 */
	protected final static Singleton prideInit =
		new Singleton() {
			protected Object createInstance() throws Exception {
				ResourceAccessor ra = new ResourceAccessorWeb(System.getProperties());
				DatabaseFactory.setDatabaseName("java:comp/env/sampledb");
				DatabaseFactory.setResourceAccessor(ra);
				DatabaseFactory.setExceptionListener(new ServletExceptionListener());
				return ra;
			}
    
		};

	/**
	 * Runs {@link Database#releaseConnection after having called {@link #doSafeGet}
	 * to ensure that any open PriDE connections are released before termination.
	 */	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		try { doSafeGet(request, response); }
		finally {
			try { DatabaseFactory.getDatabase().releaseConnection(); }
			catch(SQLException sqlx) {
				throw new ServletException(sqlx.getMessage());
			}
		}
	}

	/**
	 * Performs the PriDE initialization as specified in the
	 * {@link #prideInit} singleton initializer
	 */
	public void init() throws ServletException {
		super.init();
		try { prideInit.getInstance(); }
		catch (Exception e) { throw new ServletException(e); }
	}

	/**
	 * This function is called by this class' {@link #doGet} method and
	 * must be overridden by derived classes.
	 */
	protected abstract void doSafeGet(HttpServletRequest request,HttpServletResponse response)
		throws ServletException, IOException;
}
