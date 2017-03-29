/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Matthias Bartels, arvato direct services - Release 2.0.3
 *******************************************************************************/
package servlet;

import java.io.*;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.http.HttpServlet;

import de.mathema.pride.*;
import de.mathema.pride.ResourceAccessor;

/**
 * @author <a href="mailto:matthias.bartels@bertelsmann.de>Matthias Bartels</a>
 *
 */
public class ExampleServlet extends PrideServlet implements SingleThreadModel {

    /**
     * Actual worker method. See {@link PrideServlet} why we implement
     * {@link PrideServlet#doSafeGet} rather than {@link HttpServlet#doGet}
     * This method creates a customer record in the database according to
     * the input from the HTML form in index.html
     */
    protected void doSafeGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html");
        try {
                     
            int id = Integer.parseInt(request.getParameter("id"));    
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            boolean active = Boolean.getBoolean(request.getParameter("active"));
            
            Customer customer = new Customer(id, firstName, lastName, new Boolean(active));
            DatabaseFactory.getDatabase().commit();
            pw.write("<html><body>Customer <b>" + id + "</b> created!</body></html>");

			// The following call is not really required because it will be done
			// in the PrideServlet base class. The line was only included for those
			// who just copy this example and throw the base class out without thinking ;-)
			DatabaseFactory.getDatabase().releaseConnection();
        }
        catch (Exception e) {
            e.printStackTrace(pw);
        }
    }

	/**
	 * The following method ensures PriDE initialization and is not really
	 * required because it is already done in the {@link PrideServlet} base class.
	 * The method was only included for those who just copy this example class and
	 * throw the base class out without thinking ;-)
     */
    public void init() throws ServletException {
		super.init();
		try { prideInit.getInstance(); }
		catch (Exception e) { throw new ServletException(e); }
	}

}
