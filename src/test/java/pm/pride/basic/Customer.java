package pm.pride.basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/

import java.sql.SQLException;
import java.util.Date;

import pm.pride.RecordDescriptor;
import pm.pride.SQL;

/**
 * @author bart57
 *
 * Entity-Class that represents Customers in the database
 */
public class Customer extends IdentifiedEntity {

	public static String TABLE = AbstractPrideTest.TEST_TABLE;
	public static String COL_ID = "id";
	public static String COL_FIRSTNAME = "firstName";
	public static String COL_LASTNAME = "lastName";
	public static String COL_ACTIVE = "active";
	public static String COL_HIREDATE = "hireDate";
	// This column needs quotation which will be added in the test bootstrap. It is used
	// to ensure that PriDE is able to deal with it
	public static String COL_TYPE = "ty pe";
	
	private String firstName;
	private String lastName;
	private Boolean active;
	private Date hireDate;
  private CustomerType type;
	
	public Customer() {}
	
	public Customer(int id) throws SQLException {
		super(id);
		find();
	}
	
	public Customer(int id, String firstName, String lastName) throws SQLException {
		this(id, firstName, lastName, null, null);
	}

	public Customer(int id, String firstName, String lastName, Boolean active) throws SQLException {
		this(id, firstName, lastName, active, null);
	}
	
	public Customer(int id, String firstName, String lastName, Boolean active, Date hireDate) throws SQLException {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		this.hireDate = hireDate;
    this.type = CustomerType.standard;
		create();
	}

	protected static RecordDescriptor red;

	/** This is a very unusual lazy initialization of the record descriptor which
	 * is required as the unit tests of PriDE may be executed on multiple database
	 * types in a single run and the descriptor may need database-specific
	 * reinitialization. E.g. quotations may be different. */
	public RecordDescriptor getDescriptor() {
		if (red == null) {
			red = new RecordDescriptor(Customer.class, TABLE, IdentifiedEntity.red)
				.row(COL_FIRSTNAME, "getFirstName", "setFirstName")
				.row(COL_LASTNAME,  "getLastName",  "setLastName")
				.row(COL_HIREDATE,  "getHireDate",  "setHireDate")
				.row(COL_ACTIVE,    "getActive",    "setActive")
				.row(SQL.quote(COL_TYPE), "getType",    "setType");
		}
		return red;
	}

	public static void resetTestConfig() {
		red = null;
	}

	public String getFirstName() { return firstName; }
	public String getLastName()  { return lastName; }
	public Boolean getActive()    { return active; }
	public Date getHireDate()  { return hireDate;	}
    public CustomerType getType()  { return type; }

	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setLastName(String lastName)   { this.lastName = lastName; }
	public void setActive(Boolean active)      { this.active = active; }
	public void setHireDate(Date d)            { this.hireDate = d; }
    public void setType(CustomerType type)            { this.type = type; }
 
	public String toString() {
		return getId() + "/" + firstName + "/" + lastName + "/" + active + "/" + hireDate + "/" + type;
	}

	public void query(RecordDescriptor desc) {
		
	}
}
