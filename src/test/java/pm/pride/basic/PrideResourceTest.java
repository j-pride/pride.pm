package pm.pride.basic;

import java.sql.Connection;


import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;

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
public class PrideResourceTest extends AbstractPrideTest {

	@Test
	public void testReconnect() throws Exception {
		Connection con = DatabaseFactory.getDatabase().getConnection();
		con.close();
		// Following (senseless) statement must not fail though this thread's
		// connection was closed in the line above.
		DatabaseFactory.getDatabase().sqlUpdate("update " + TEST_TABLE + " set id = 5 where 1=0");
	}

}
