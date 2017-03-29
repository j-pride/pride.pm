package basic;
/*******************************************************************************
 * Copyright (c) 2001-2003 The PriDE team and MATHEMA Software Ltd.
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of an extended GNU Public License
 * (GPL) which accompanies this distribution, and is available at
 * http://pride.sourceforge.net/EGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software Ltd. - initial API and implementation
 *     Matthias Bartels, arvato direct services GmbH
 *******************************************************************************/
import de.mathema.pride.*;
import junit.framework.TestCase;
import junit.framework.Assert;

/**
 * @author Matthias Bartels
 *
 * Testclass that tests an inheritance hierarchy of RecordDescriptors.
 */
public class DerivedRecordDescriptorTest extends TestCase {

	public DerivedRecordDescriptorTest(String name) {
		super(name);
	}

    public void setUp() throws Exception {
        super.setUp();
		ResourceAccessor ra =
			new ResourceAccessorJ2SE(System.getProperties());
		DatabaseFactory.setResourceAccessor(ra);
		DatabaseFactory.setDatabaseName(System.getProperties().getProperty("pride.db"));
		try {
			DatabaseFactory.getDatabase().sqlUpdate(
				"DROP TABLE CUSTOMER_PRIDE_TEST");
		} catch (Exception e) {
			//e.printStackTrace();
		}
		DatabaseFactory.getDatabase().sqlUpdate(
			"CREATE TABLE CUSTOMER_PRIDE_TEST "
				+ "(id int not null primary key,"
				+ "firstName varchar(50),"
				+ "lastName varchar(50),"
				+ "street varchar(50),"
				+ "city varchar(50),"
				+ "active int)");
		DatabaseFactory.getDatabase().commit();        
		DerivedCustomer c = new DerivedCustomer(1,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
    }
    
    public void testInsert() throws Exception {
		DerivedCustomer c = new DerivedCustomer(2,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer c2 = new DerivedCustomer(2);
		Assert.assertEquals("Hajo", c2.getFirstName());
		Assert.assertEquals("Klick", c2.getLastName());
		Assert.assertEquals("Maerchenwald", c2.getCity());
    }
    
    public void testUpdate() throws Exception {
    
		DerivedCustomer c = new DerivedCustomer(1);
		Assert.assertEquals("Hajo", c.getFirstName());
		Assert.assertEquals("Maerchenwald", c.getCity());
		c.setFirstName("Casper");
		c.setLastName("Kopp");
		c.setStreet("Bei Schneewittchen");
		c.update(new String[] {"id", "city"});
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer[] ca = (DerivedCustomer[])c.query(new String [] { "id", "street" } ).toArray();
		Assert.assertEquals(ca.length, 1);
		Assert.assertEquals("Casper", ca[0].getFirstName());
		Assert.assertEquals("Kopp", ca[0].getLastName());        
		Assert.assertEquals("Bei Schneewittchen", ca[0].getStreet());
    }
    
    public void testDelete() throws Exception {
        DerivedCustomer c = new DerivedCustomer();
		ResultIterator ri =	c.queryAll();
		int counter = 0;
		do {
			c.delete();
			counter++;
		} while (ri.next());
		DatabaseFactory.getDatabase().commit();
		Assert.assertEquals(1, counter);
		ri = null;
		try {
			ri = c.queryAll();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof NoResultsException);	
		}
		Assert.assertNull(ri);
    }
    
    
    public void tearDown() throws Exception {
		super.tearDown();
		DatabaseFactory.getDatabase().sqlUpdate(
			"DROP TABLE CUSTOMER_PRIDE_TEST");
		DatabaseFactory.getDatabase().commit();    
    }
}