/*******************************************************************************
 * Copyright (c) 2001-2003 The PriDE team and MATHEMA Software Ltd.
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of an extended GNU Public License
 * (GPL) which accompanies this distribution, and is available at
 * http://pride.sourceforge.net/EGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software Ltd. - initial API and implementation
 *******************************************************************************/
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import basic.Name;
import pm.pride.*;

public class simple {

    public static void main(String[] args) throws Exception {
	/*
	ResourceAccessorJ2SE accessor = new ResourceAccessorJ2SE 
	    ("sun.jdbc.odbc.JdbcOdbcDriver", "sa", "", "sql.log");
	DatabaseFactory.setResourceAccessor(accessor);
	DatabaseFactory.setDatabaseName("jdbc:odbc:chayyamdb");
	*/
	ResourceAccessorJ2SE accessor =
		new ResourceAccessorJ2SE(System.getProperties());
	DatabaseFactory.setResourceAccessor(accessor);
	DatabaseFactory.setDatabaseName("jdbc:mysql://localhost/test");


	Database db = DatabaseFactory.getDatabase();
	Connection con = db.getConnection();

	Name name = new Name();
	name.setId(1);
	name.find();
	System.out.println(name.getId() + " " +
			   name.getFirstname() + " " +
			   name.getLastname());
	System.out.println(new Date());

        PreparedOperation pop = new PreparedInsert(name.getDescriptor());
        name.setId(11);
        pop.execute(name);
        db.commit();

        pop = new PreparedUpdate(new String[] { "id" }, name.getDescriptor());
        name.setLastname("egal");
        pop.execute(name);
        db.commit();

        /*

	Calendar cal = Calendar.getInstance();
	cal.set(1967, 6, 12);
	java.sql.Date birthDate = new java.sql.Date(cal.getTime().getTime());
	for (int i = 0; i < 80000; i++) {
	    name.setId(i);
	    name.setFirstname("Jan");
	    name.setLastname("Lessner");
	    name.setBirthDate(birthDate);
	    name.create();
	}
	db.commit();
	System.out.println(new Date());

	for (int i = 0; i < 80000; i++) {
	    name.setId(i + 80000);
	    name.setFirstname("Jan");
	    name.setLastname("Lessner");
	    name.setBirthDate(birthDate);

	    db.sqlUpdate("insert into name (id, firstname, lastname, birthdate) values (" + 
			 name.getId() + ", " +
			 "'" + name.getFirstname() + "'," +
			 "'" + name.getLastname() + "'," +
			 "'" + name.getBirthDate() + "'" +
			 ")");
	}
	db.commit();
	System.out.println(new Date());

	db.sqlUpdate("delete from name");
	db.commit();
	System.out.println(new Date());

        */
    }

}
