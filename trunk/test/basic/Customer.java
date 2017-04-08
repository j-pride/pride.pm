package basic;
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
import java.sql.Date;

import de.mathema.pride.MappedObject;
import de.mathema.pride.RecordDescriptor;
import de.mathema.pride.ResultIterator;

/**
 * @author bart57
 *
 * Entity-Class that represents Customers in the database
 */
public class Customer extends IdentifiedEntity {

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

	protected static RecordDescriptor red =
		new RecordDescriptor(Customer.class, AbstractPrideTest.TEST_TABLE, IdentifiedEntity.red, new String[][] {
			{ "firstName", "getFirstName", "setFirstName"},
			{ "lastName",  "getLastName",  "setLastName"},
			{ "hireDate",  "getHireDate",  "setHireDate"},
			{ "active",    "getActive",    "setActive" },
            { "type",    "getType",    "setType" }
		}
	);

	/**
	 * @see de.mathema.pride.MappedObject#getDesc()
	 */
	protected RecordDescriptor getDescriptor() { return red; }

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
 
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public String toString() {
		return getId() + "/" + firstName + "/" + lastName + "/" + active + "/" + hireDate + "/" + type;
	}
}
