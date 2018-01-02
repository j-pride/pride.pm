package basic;
import org.junit.Test;

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
public class DerivedRecordDescriptorTest extends AbstractPrideTest {

	@Override
    public void setUp() throws Exception {
        super.setUp();
        DatabaseFactory.getDatabase().sqlUpdate("DROP TABLE customer_pride_test");
		DatabaseFactory.getDatabase().sqlUpdate(
			"CREATE TABLE customer_pride_test "
				+ "(id int not null primary key,"
				+ "firstName varchar(50),"
				+ "lastName varchar(50),"
				+ "type varchar(10),"
				+ "hireDate date,"
				+ "street varchar(50),"
				+ "city varchar(50),"
				+ "active int)");
		DatabaseFactory.getDatabase().commit();        
		DerivedCustomer c = new DerivedCustomer(1,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
    }

	@Test
    public void testInsert() throws Exception {
		DerivedCustomer c = new DerivedCustomer(2,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer c2 = new DerivedCustomer(2);
		assertEquals("Hajo", c2.getFirstName());
		assertEquals("Klick", c2.getLastName());
		assertEquals("Maerchenwald", c2.getCity());
    }
    
	@Test
    public void testUpdate() throws Exception {
    
		DerivedCustomer c = new DerivedCustomer(1);
		assertEquals("Hajo", c.getFirstName());
		assertEquals("Maerchenwald", c.getCity());
		c.setFirstName("Casper");
		c.setLastName("Kopp");
		c.setStreet("Bei Schneewittchen");
		c.update(new String[] {"id", "city"});
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer[] ca = (DerivedCustomer[])c.query(new String [] { "id", "street" } ).toArray();
		assertEquals(ca.length, 1);
		assertEquals("Casper", ca[0].getFirstName());
		assertEquals("Kopp", ca[0].getLastName());        
		assertEquals("Bei Schneewittchen", ca[0].getStreet());
    }
    
	@Test
    public void testDelete() throws Exception {
        DerivedCustomer c = new DerivedCustomer();
		ResultIterator ri =	c.queryAll();
		int counter = 0;
		do {
			c.delete();
			counter++;
		} while (ri.next());
		DatabaseFactory.getDatabase().commit();
		assertEquals(1, counter);
		ri = c.queryAll();
		assertNull(ri);
    }
    
}