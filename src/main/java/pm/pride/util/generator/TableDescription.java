/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride.util.generator;

/**
 * Meta information about a database table
 *
 * @author <a href="mailto:martin.haag@mathema.de">Martin Haag</a>
 * @version 1.0
 */

import java.sql.*;
import java.util.*;

public class TableDescription {

	public static final String COLUMN_LIST_START = "(";
	public static final String COLUMN_LIST_END = ")";
	public static final String COLUMN_LIST_SEPARATOR = ",";

	protected List<TableColumn> columnList;
	protected String tableName;
	protected boolean partial;

	protected List<TableColumn> extractColumns(DatabaseMetaData db_meta, String dbType, String tableName,
			List<String> columnsOfInterest) throws SQLException {
		ResultSet rset2 = db_meta.getColumns(null, // con.getCatalog (),
				null, // "*",
				tableName, "%");
		Set<TableColumn> result = new HashSet<>(0);
		while (rset2.next()) {
			String columnName = rset2.getString("COLUMN_NAME");
			int dataType = rset2.getInt("DATA_TYPE");
			int columnSize = rset2.getInt("COLUMN_SIZE"); // Timetamp: 11, Date:
															// 7
			int decimalDigits = rset2.getInt("DECIMAL_DIGITS");
			boolean nullable = rset2.getInt("NULLABLE") == ResultSetMetaData.columnNoNulls;
			if (columnsOfInterest == null || columnsOfInterest.remove(columnName)) {
				TableColumn tabColumn = new TableColumn(dbType, tableName, columnName, dataType, columnSize,
						decimalDigits, nullable);
				result.add(tabColumn);
			}
		}
		rset2.close();

		if (columnsOfInterest != null && columnsOfInterest.size() > 0) {
			throw new SQLException("Unknown columns in table " + tableName + ": " + columnsOfInterest);
		}
		return new ArrayList(result);
	}

	protected void markKeyColumns(DatabaseMetaData db_meta, String tableName, List<TableColumn> columns)
			throws SQLException {
		ResultSet rset2 = db_meta.getPrimaryKeys(null, // con.getCatalog (),
				null, // "*",
				tableName);
		while (rset2.next()) {
			String column_name = rset2.getString("COLUMN_NAME");

			Iterator<TableColumn> iter = columns.iterator();
			while (iter.hasNext()) {
				TableColumn tabColumn = (TableColumn) iter.next();
				if (tabColumn.columnName.equals(column_name)) {
					tabColumn.setPrimaryKeyField();
					break;
				}
			}
		}
		rset2.close();
	}

	protected void init(Connection con, String dbType, String tableName) throws SQLException {
		if (tableName.contains(COLUMN_LIST_START)) {
			this.tableName = extractColumnsFromTableName(con, dbType, tableName);
		} else {
			this.tableName = extractColumnsFromTableMetadata(con, dbType, tableName, null);
		}
	}

	private String extractColumnsFromTableName(Connection con, String dbType, String tableName) throws SQLException {
		partial = true;
		String columnNamesString = tableName.replaceFirst(".+\\" + COLUMN_LIST_START, "").replace(COLUMN_LIST_END, "")
				.replaceAll("\\s", "");
		String[] columnNames = columnNamesString.split(COLUMN_LIST_SEPARATOR);
		List<String> mutableColumnNameList = new ArrayList<>(Arrays.asList(columnNames));
		String cleanedTableName = tableName.replaceFirst("\\" + COLUMN_LIST_START + ".*\\" + COLUMN_LIST_END, "");
		return extractColumnsFromTableMetadata(con, dbType, cleanedTableName, mutableColumnNameList);
	}

	protected String extractColumnsFromTableMetadata(Connection con, String dbType, String tableName,
			List<String> columnsOfInterest) throws SQLException {
		DatabaseMetaData db_meta = con.getMetaData();
		String[] tbl_types = { "TABLE", "VIEW" };
		ResultSet rset1 = db_meta.getTables(null, // con.getCatalog (),
				null, // "*",
				tableName, tbl_types);
		if (rset1.next()) {
			tableName = rset1.getString("TABLE_NAME"); // Just in case there is
														// something to
														// normalize ;-)
			this.columnList = extractColumns(db_meta, dbType, tableName, columnsOfInterest);
			markKeyColumns(db_meta, tableName, columnList);
		}
		rset1.close();
		return tableName;
	}

	public TableDescription(Connection con, String dbType, String tableName) throws SQLException {
		init(con, dbType, tableName);
		if (columnList == null) {
			init(con, dbType, tableName.toUpperCase());
			if (columnList == null)
				throw new SQLException("Unknown table " + tableName);
		}
	}

	public boolean hasPrimaryKey() {
		for (TableColumn current : getColumnList()) {
			if (current.isPrimaryKeyField())
				return true;
		}
		return false;
	}

	/**
	 * Returns an enumerator for the table's columns in form of TableColumns
	 * objects
	 */
	public List<TableColumn> getColumnList() {
		return columnList;
	}

	/** Returns the plain table name */
	public String getTableName() {
		return tableName;
	}

	/** Returns the table name with its first letter capitalized */
	public String getTableNameFirstUpper() {
		return tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
	}

	public boolean isPartial() {
		return partial;
	}
}
