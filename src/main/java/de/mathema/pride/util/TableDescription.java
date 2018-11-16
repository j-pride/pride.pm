/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package de.mathema.pride.util;

/**
 * Meta information about a database table
 *
 * @author <a href="mailto:martin.haag@mathema.de">Martin Haag</a>
 * @version 1.0
 */

import java.sql.*;
import java.util.*;

public class TableDescription {

    protected Vector<TableColumn> tableList;
    protected String tableName;

	protected Vector<TableColumn> getColumns(DatabaseMetaData db_meta, String tableName) throws SQLException {
		ResultSet rset2 = db_meta.getColumns (null, //con.getCatalog (),
											  null, //"*",
											  tableName, "%");
		Vector<TableColumn> result = new Vector<>(0);
		while (rset2.next ()) {
		  TableColumn tabColumn =
			  new TableColumn(rset2.getString ("COLUMN_NAME"),
							   rset2.getInt ("DATA_TYPE"),
                               rset2.getInt ("DECIMAL_DIGITS"),
							   (rset2.getInt ("NULLABLE")== ResultSetMetaData.columnNoNulls));
		  result.addElement(tabColumn);
		}
		rset2.close ();

		return result;
	}

	protected void markKeyColumns(DatabaseMetaData db_meta, String tableName, Vector<TableColumn> columns) throws SQLException {
		ResultSet rset2 = db_meta.getPrimaryKeys(null, //con.getCatalog (),
								                 null, //"*",
								                 tableName);
		while (rset2.next ()) {
			String column_name = rset2.getString ("COLUMN_NAME");
	
			Iterator<TableColumn> iter = columns.iterator();
			while(iter.hasNext()) {
			  TableColumn tabColumn = (TableColumn)iter.next();
			  if (tabColumn.columnName.equals(column_name)) {
				  tabColumn.setPrimaryKeyField();
				  break;
			  }
			}
		}
		rset2.close ();
	}

	protected void init(Connection con, String tableName) throws SQLException {
		this.tableName = tableName;

		DatabaseMetaData db_meta = con.getMetaData ();
		String[] tbl_types = { "TABLE", "VIEW" };
		ResultSet rset1 = db_meta.getTables (null, //con.getCatalog (),
											 null, //"*",
											 tableName,
											 tbl_types);

		while (rset1.next ()) {
		  String tbl_name = rset1.getString ("TABLE_NAME");
		  this.tableName = tbl_name; // Just in case there is something to normalize ;-)
		  this.tableList = getColumns(db_meta, tbl_name);
		  markKeyColumns(db_meta, tbl_name, tableList);
		}
		rset1.close ();
	}
	
    public TableDescription(Connection con, String tableName) throws SQLException {
		init(con, tableName);
		if (tableList == null) {
			init(con, tableName.toUpperCase());
			if (tableList == null)
				throw new SQLException("Unknown table " + tableName);
		}
    }

    public boolean hasPrimaryKey() {
        for (TableColumn current: getList()) {
            if (current.isPrimaryKeyField())
                return true;
        }
        return false;
    }
    
    /** Returns an enumerator for the table's columns in form of TableColumns objects */
    public Vector<TableColumn> getList() { return tableList; }

    /** Returns the plain table name */
    public String getTableName() { return tableName; }

    /** Returns the table name with its first letter capitalized */
    public String getTableNameFirstUpper() {
        return tableName.substring(0,1).toUpperCase() + tableName.substring(1);
    }
}
