package basic;
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
import java.sql.Date;
import java.sql.SQLException;

import pm.pride.RecordDescriptor;

/**
 * Same like {@link Customer} but defines function getAutoFields, which
 * is supposed to cause the firstName attribute never to be created.
 */
public class AutoCustomer extends Customer {

	public AutoCustomer() {}
	
	public AutoCustomer(int id) throws SQLException{
		super(id);
	}
	
	public AutoCustomer(String firstName, String lastName) throws SQLException {
		super(-1, firstName, lastName);
	}
	
	public AutoCustomer(int id, String firstName, String lastName, Boolean active, Date hireDate) throws SQLException {
		super(id, firstName, lastName, active, hireDate);
	}

	public String[] getAutoFields() { return new String[] {"id"}; }

}
