/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Example code
 *******************************************************************************/
package servlet;
import java.sql.SQLException;

import de.mathema.pride.MappedObject;
import de.mathema.pride.RecordDescriptor;

/**
 * @author <a href="mailto:matthias.bartels@bertelsmann.de>Matthias Bartels</a>
 *
 * Entity-Class that represents Customers in the database
 */
public class Customer extends MappedObject implements Cloneable {

	private int id = 0;
	private String firstName = null;
	private String lastName = null;
	private Boolean active = null;
	
	
	public Customer() {}
	
	public Customer(int id) throws SQLException{
		this.id = id;
		find();
	}
	
	public Customer(int id, String firstName, String lastName) throws SQLException {
		this(id, firstName, lastName, null);
	}

	public Customer(int id, String firstName, String lastName, Boolean active) throws SQLException {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.active = active;
		create();
	}

	protected static RecordDescriptor red =
		new RecordDescriptor(
			Customer.class,
			"customer_pride_test",
			null,
			new String[][] {
				{ "id", "getId", "setId"},
				{ "firstName", "getFirstName", "setFirstName"},
				{ "lastName", "getLastName", "setLastName"},
				{ "active", "getActive", "setActive" }
			}
	);

	/**
	 * @see de.mathema.pride.MappedObject#getDesc()
	 */
	protected RecordDescriptor getDescriptor() { return red; }

	public String getFirstName() { return firstName; }
	public int getId() { return id; }
	public String getLastName() { return lastName; }
	public Boolean getActive() { return active; }

	public void setFirstName(String firstName) { this.firstName = firstName; }
	public void setId(int id) { this.id = id; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public void setActive(Boolean active) { this.active = active; }

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public String toString() {
		return id + "/" + firstName + "/" + lastName + "/" + active;
	}
}
