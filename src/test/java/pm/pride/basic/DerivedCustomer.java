package pm.pride.basic;
/*******************************************************************************
 * Copyright (c) 2001-2003 The PriDE team and MATHEMA Software Ltd.
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of an extended GNU Public License
 * (GPL) which accompanies this distribution, and is available at
 * http://pride.sourceforge.net/EGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software Ltd. - initial API and implementation
 *     Matthias Bartels, arvato direct services
 *******************************************************************************/
import java.sql.SQLException;

import pm.pride.RecordDescriptor;

import static pm.pride.basic.AbstractPrideTest.TEST_TABLE;

/**
 * @author Matthias Bartels
 *
 * Entity-Class that represents Customers in the database
 */
 public class DerivedCustomer extends Customer {
 
    private String street;
    private String city;
    
	public DerivedCustomer() {}
	
	public DerivedCustomer(int id) throws SQLException{
        setId(id);
        find();
    }
	
	public DerivedCustomer(int id, String firstName, String lastName, String street, String city) throws SQLException {
		this(id, firstName, lastName, street, city, null);
	}

	public DerivedCustomer(int id, String firstName, String lastName, String street, String city, Boolean active) throws SQLException {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setStreet(street);
        setCity(city);
        setActive(active);
		create();
	}    
	
	public String getStreet() { return street; }
    public String getCity() { return city; }
	
	public void setStreet(String street) { this.street = street; }
	public void setCity(String city) { this.city = city; }
	
	public String toString() {
		return getId() + "/" + getFirstName() + "/" + getLastName() + "/" + getStreet() + "/" + getCity() + "/" + getActive();
	}
	
	public RecordDescriptor getDescriptor() { return red; }
	
	protected static RecordDescriptor red =
		new RecordDescriptor(DerivedCustomer.class, TEST_TABLE, Customer.red)
			.row("street", "getStreet", "setStreet")
			.row("city", "getCity", "setCity");
 
 }
