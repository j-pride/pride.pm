/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 *******************************************************************************/
package pm.pride.util.generator;

import java.sql.*;

import pm.pride.ResourceAccessor;

/**
 * Meta information about a database table column
 */
public class TableColumn {

	protected String dbType;
    protected String tableName;
    protected String columnName;
    protected String uniqueColumnName;
    protected int columnType;
    protected int columnSize;
    protected int decimalDigits;
    protected boolean nullsForbidden;
    protected boolean primaryKeyField;
    
    public TableColumn(String dbType, String tableName, String columnName, int type, int columnSize, int decimalDigits, boolean nullsForbidden) {
    	this.dbType = dbType;
    	this.tableName = tableName;
        this.columnName = columnName;
        this.uniqueColumnName = columnName;
        this.columnType = type;
        this.columnSize = columnSize;
        this.decimalDigits = decimalDigits;
        this.nullsForbidden = nullsForbidden;
    }

	public void setPrimaryKeyField() { primaryKeyField = true; }

	public void makeUnique() {
		this.uniqueColumnName = this.tableName + "_" + this.columnName;
	}

	/** @return true if this column is a primary key field */
	public boolean isPrimaryKeyField() { return primaryKeyField; }

    /**
     * @return column name in lower case and doesnt remove character underscore
     */
    public String getName() { return columnName.toLowerCase(); }

    public String getUniqueName() { return uniqueColumnName.toLowerCase(); }

    /**
     * @return capitalized column name without underscores. Characters following
     * directly after underscores are printed in uppercase.
     */
    public String getNameCamelCaseFirstUp() {
        StringBuffer newString = new StringBuffer();
        boolean nextUpper = true;
        for (int i=0; i < uniqueColumnName.length(); i++) {
            if ( uniqueColumnName.substring(i,i + 1).equals("_") ) {
                nextUpper = true;
            }
            else {
                if ( nextUpper ) {
                    newString.append(uniqueColumnName.substring(i,i + 1).toUpperCase());
                    nextUpper = false;
                }
                else
                    newString.append(uniqueColumnName.substring(i,i + 1).toLowerCase());
            }
        }
        return newString.toString();
    }
    
    /**
     * @return column name. Like getName2 but prints the first letter in lowercase.
     */
    public String getNameCamelCaseFirstLow() {
    	String firstUp = getNameCamelCaseFirstUp();
    	String head = firstUp.substring(0, 1);
    	return head.toLowerCase() + firstUp.substring(1);
    }
    
    public String getNameUpper() {
    	return getUniqueName().toUpperCase();
    }
    
    /**
     * @return String with JAVA type of the column. If the column doesn't allow
     * null values, the functions returns primitive types, otherwise it returns
     * object types.
     */
    public String getType() {
        String returnType = null;
        
        switch (columnType) {
        case Types.VARCHAR  :
        case Types.NVARCHAR  :
            returnType = "String";
            break;
        case Types.CHAR     :
            returnType = "String";
            break;
        case Types.SMALLINT :
        case Types.TINYINT  :
            returnType = nullsForbidden ? "short" : "Short";
            break;
        case Types.NUMERIC  :
        case Types.DECIMAL  :
            if (decimalDigits > 0)
                returnType = "java.math.BigDecimal";
            else
                returnType = nullsForbidden ? "long" : "Long";
            break;
        case Types.INTEGER  :
            returnType = nullsForbidden ? "int" : "Integer";
            break;
        case Types.REAL     :
            returnType = nullsForbidden ? "float" : "Float";
            break;
        case Types.FLOAT    :
            returnType = nullsForbidden ? "double" : "Double";
            break;
        case Types.DOUBLE   :
            returnType = nullsForbidden ? "double" : "Double";
            break;
        case Types.BIGINT   :
            returnType = nullsForbidden ? "long" : "Long";
            break;
        case Types.DATE     :
            returnType = "java.util.Date";
            break;
        case Types.BOOLEAN  :
            returnType = nullsForbidden ? "boolean" : "Boolean";
            break;
        case Types.CLOB     :
            returnType = "Clob";
            break;
        case Types.TIMESTAMP:
            returnType = "java.sql.Timestamp";
            /** Special issue with Oracle: DATE columns are reported to have type TIMESTAMP
             * via the JDBC meta data. A time stamp with a short column size is a date.
             */
        	if (ResourceAccessor.DBType.ORACLE.equals(dbType) && columnSize < 11) {
                returnType = "java.util.Date";
        	}
            break;
        default:
        	returnType = "Object";
        }
        
        return returnType;
    }

	public String getTableName() {
		return tableName;
	}
}
