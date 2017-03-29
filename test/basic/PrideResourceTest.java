package basic;

import java.sql.Connection;

import de.mathema.pride.DatabaseFactory;

/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/

/**
 * JUnit test class performing some basic tests of PriDE's simple
 * resource and transaction management in non-managed environments
 */
public class PrideResourceTest extends PrideBaseTest {

	public PrideResourceTest(String name)	{
		super(name);
	}
	
	public void testReconnect() throws Exception {
		Connection con = DatabaseFactory.getDatabase().getConnection();
		con.close();
		// Following (senseless) statement must not fail though this thread's
		// connection was closed in the line above.
		DatabaseFactory.getDatabase().sqlUpdate("update " + PrideBaseTest.TEST_TABLE + " set id = 5 where 1=0");
	}

}
