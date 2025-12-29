/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import static pm.pride.RevisionedRecordDescriptor.FLAG_IS_NOT_REVISIONED;
import static pm.pride.RevisionedRecordDescriptor.FLAG_IS_REVISIONED;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;


/** Description of a mapping between a database record field and a JAVA class member.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
class AttributeDescriptor implements WhereCondition.Operator, RecordDescriptor.ExtractionMode
{
  public static final String AS_KEYWORD = " as ";
  private static final int FIELDNAME = 0;
  private static final int GETMETHOD = 1;
  private static final int SETMETHOD = 2;
  private static final int FIELDTYPE = 3;
  public static final int REVISIONINGFLAG = 4;

  protected String databaseFieldName;
  protected int databaseColumnType;

  /** Method to fetch data from a database result set by name */
  protected Method databaseGetByNameMethod;

  /** Method to fetch data from a database result set by index */
  protected Method databaseGetByIndexMethod;

  /** Method to store data in a database prepared statement */
  protected Method databaseSetMethod;

  /** Specifies the ResultSet data extraction mode. See class
   * {@link RecordDescriptor} for details.
   */
  protected int extractionMode;

  /**
   * Specifies if the column is used for revisioning
   */
  protected boolean revisioning;

  /** True if the attribute represented by this descriptor is of a primitive type */
  protected boolean isPrimitive;

  protected GetterSetterPair fieldAccess;

  /** Create a mapping descriptor for a single database field
   * @param objectType JAVA class the database field is mapped to
   * @param attrInfo mapping detail info in form of 3 or 4 strings.
   *  <OL>
   *  <LI>Name of the database field. If the field is ommited, query
   *    result data is accessed by index rather than by name. This
   *    should only be used for very special cases (e.g. if the result
   *    data doesn't represent database records) and works for read
   *    access only!!
   *  <LI>Getter method to read the value from a JAVA object
   *  <LI>Setter method to write the value to a JAVA object
   *  <LI>Optionally the type identifier of the database field. If the array
   *   contains only 3 strings, the database field type is derived from the
   *   return type of the getter method.
   *  </OL>
   */
  public AttributeDescriptor(Class<?> objectType, int extractionMode, String[] attrInfo)
      throws IllegalDescriptorException {
    this.extractionMode = extractionMode;
    databaseFieldName = attrInfo[FIELDNAME];
    revisioning = (attrInfo.length > REVISIONINGFLAG) ?
        ! FLAG_IS_NOT_REVISIONED.equals(attrInfo[REVISIONINGFLAG]) : true;

    fieldAccess = new GetterSetterPair(objectType, attrInfo[GETMETHOD], attrInfo[SETMETHOD]);

    Class<?> attributeType = null;

    try {
      attributeType = (attrInfo.length > FIELDTYPE && attrInfo[FIELDTYPE] != null) ?
          Class.forName(attrInfo[FIELDTYPE]) : fieldAccess.type();
      isPrimitive = attributeType.isPrimitive();
      databaseGetByIndexMethod =
          ResultSetAccess.getIndexAccessMethod(attributeType);
      databaseGetByNameMethod =
          ResultSetAccess.getAccessMethod(attributeType);
      databaseSetMethod =
          PreparedStatementAccess.getAccessMethod(attributeType);
    }
    catch(ClassNotFoundException x) {
      throw new IllegalDescriptorException("Unknown class " + attrInfo[FIELDTYPE]);
    }
    catch(NoSuchMethodException x) {
      x.printStackTrace();
      throw new IllegalDescriptorException("Error in method retrieval for " +
          attributeType.getName() + "/" +
          attrInfo[GETMETHOD] + ": " + x.toString());
    }
  }

  /**
   * Copy constructor
   * @param ad The descriptor which to copy data from
   * @param alias An optional alias name to use for database access.
   *    This is of interest if this descriptor is supposed to be used
   *    in an aggregation of multiple record descriptors for accessing
   *    full table joins. May be null;
   */
  public AttributeDescriptor(AttributeDescriptor ad, String alias) {
    databaseFieldName = ad.databaseFieldName;
    if (alias != null)
      databaseFieldName = alias + "." + databaseFieldName;
    databaseColumnType = ad.databaseColumnType;
    databaseGetByNameMethod = ad.databaseGetByNameMethod;
    databaseGetByIndexMethod = ad.databaseGetByIndexMethod;
    databaseSetMethod = ad.databaseSetMethod;
    fieldAccess = ad.fieldAccess;
    extractionMode = ad.extractionMode;
    isPrimitive = ad.isPrimitive;
    revisioning = ad.revisioning;
  }

  /** Returns the database field name */
  public String getFieldName() { return databaseFieldName; }

  /** Allows to use the column names of the primary RecordDescriptor's of a JoinRecordDescriptor
   * in a query by example resp. find operation without providing a table alias prefix. This is
   * of interest for entity compositions - the entity base class' query functions should still work */
  public String getFieldName(String defaultAliasPrefix) {
    String fieldName = getFieldName();
    if (defaultAliasPrefix != null && !fieldName.contains(".") && !fieldName.contains(AS_KEYWORD)) {
      fieldName = defaultAliasPrefix + "." + fieldName;
    }
    return fieldName;
  }

  /** Retrieve the column's SQL type, required for setting NULL values
   * in prepared statements. The retrieval is performed lazy as it may
   * take some time and is not always required.
   * @param con the database connection to fetch meta data from
   * @param table the database table to fetch the column type from.
   *   The function assumes that the type is equal in all tables making
   *   use of the same attribute descriptor (e.g. all tables of a
   *   descriptor derivation hierarchy).
   * @param database
   * @throws SQLException if meta data retrieval failed
   */
  public int getColumnType(Connection con, String table, Database database) throws SQLException {
    if (databaseColumnType == Types.NULL) {
      // For the sake of compatibility, always retrieve meta data with the exact table and column names
      // first, then with capitalized names and afterwards (for Postgres) with lower-cased names
      try {
        databaseColumnType = selectDataType(con, table, database, databaseFieldName);
      }
      catch(SQLException sqlx1) {
        try {
          databaseColumnType = selectDataType(con, table.toUpperCase(), database, databaseFieldName.toUpperCase());
        }
        catch(SQLException sqlx2) {
          databaseColumnType = selectDataType(con, table.toLowerCase(), database, databaseFieldName.toLowerCase());
        }
      }
    }
    return databaseColumnType;
  }

  protected static String toCommonIdentifier(Database database, String identifier) {
    return identifier
        .replace(database.getIdentifierQuotation(), "")
        .replace(" ", "");
  }

  protected int selectDataType(Connection con, String table, Database database, String column) throws SQLException {
    int result;
    DatabaseMetaData meta = con.getMetaData();
    column = toCommonIdentifier(database, column).toUpperCase();
    try (ResultSet rs = meta.getColumns
        (null, null, table, null)) {
      while(rs.next()) {
        String foundColumn = rs.getString("COLUMN_NAME");
        if (toCommonIdentifier(database, foundColumn).toUpperCase().equals(column)) {
          result = rs.getInt("DATA_TYPE");
          return result;
        }
      }
      throw new SQLException("Can't retrieve meta data for column " + column + " in table " + table);
    }
  }

  /** Returns the object's value for this attribute */
  public Object getValue(Object obj) throws ReflectiveOperationException {
    return fieldAccess.get(obj);
  }

  /** Returns an update expression of the form "&lt;name&gt; = &lt;value&gt;"
   * by extracting the pased object's value for this descriptor's
   * attribute.
   */
  public String getUpdateValue(Object obj, Database db)
      throws ReflectiveOperationException, SQLException {
    return getFieldName() + " = " + getCreationValue(obj, db);
  }

  /** Returns a constraint expression of the form "&lt;name&gt; = &lt;value&gt;"
   * if the objects's value for this descriptor's attribute defers
   * from null, "&lt;name&gt; IS NULL" otherwise
   */
  @Deprecated
  public String getWhereValue(Object obj, Database db, boolean byLike)
      throws ReflectiveOperationException {
    String strval, operator;
    Object val;
    if (obj != null) {
      val = getValue(obj);
      strval = db.formatValue(val, databaseSetMethod.getParameterTypes()[1], false);
    }
    else
      val = strval = getCreationValue(obj, db);
    operator = db.formatOperator(byLike ? LIKE : EQUAL, val);
    return getFieldName() + " " + operator + " " + strval;
  }

  public WhereFieldCondition assembleWhereValue(Object obj, String defaultAliasPrefix, boolean byLike, Boolean withBind)
      throws ReflectiveOperationException {
    Object val = getValue(obj);
    String operator = byLike ? LIKE : EQUAL;
    String fieldName = getFieldName(defaultAliasPrefix);
    return new WhereFieldCondition(null, withBind, new WhereField(fieldName), operator, new WhereValues(val));
  }

  /** Allows to use the column names of the primary RecordDescriptor's of a JoinRecordDescriptor
   * in a query by example resp. find operation without providing a table alias prefix. This is
   * of interest for entity compositions - the entity base class' query functions should still work */
  public boolean matches(String defaultAliasPrefix, String dbfield) {
    if (databaseFieldName.equals(dbfield))
      return true;
    if (defaultAliasPrefix != null && !dbfield.contains(".")) {
      dbfield = defaultAliasPrefix + "." + dbfield;
      return databaseFieldName.equals(dbfield);
    }
    return false;
  }

  /** Fetch a value from a database result set according to this
   * descriptor's extraction mode. If mode is ExtractionMode.AUTO or NAME
   * extract the field value by name. If the extraction causes an InvocationTargetExtraction
   * and the extraction mode is AUTO, the descriptor is switched to INDEX mode and an
   * extraction by index is immediatly attempted.
   * @param results The ResultSet to extract data from
   * @param position The index of the field to extract. Ignored in NAME mode.
   *   Passsing -1 forces extraction by name.
   * @return The extracted value
   */
  protected Object record2object(String tableAlias, ResultSet results, int position)
      throws InvocationTargetException, IllegalAccessException {
    if (position < 0 || extractionMode == AUTO || extractionMode == NAME) {
      try {
        String lookupFieldName = getFieldName(tableAlias);
        return databaseGetByNameMethod.invoke(results, lookupFieldName);
      }
      catch(InvocationTargetException itx) {
        if (extractionMode == NAME || position < 0)
          throw itx;
        else
          extractionMode = INDEX;
      }
    }
    return databaseGetByIndexMethod.invoke(results, new Object[] {new Integer(position)});
  }

  /** Fetch a value from a database result set and copy it into an object
   * according to this attribute descriptor
   */
  protected void record2object(String tableAlias, Object obj, Database db, ResultSet results, int position)
      throws SQLException, ReflectiveOperationException {
    Object dbValue = record2object(tableAlias, results, position);
    if (results.wasNull()) {
      dbValue = null;
    }
    dbValue = db.unformatValue(dbValue, fieldAccess.typeFromSetter());
    if (dbValue == null && isPrimitive)
      throw new SQLException("Attempt to assign NULL from " + databaseFieldName + " to a primitive");
    fieldAccess.set(obj, dbValue);
  }

  /** Returns a string representation of the passed object's value for
   * this descriptor's attribute. If obj is null, returns "?", required
   * for prepared statements.
   */
  public String getCreationValue(Object obj, Database db)
      throws ReflectiveOperationException {
    if (fieldAccess.isConstantGetValue()) {
      return (String) getValue(obj);
    }
    else if (obj != null) {
      return db.formatValue(getValue(obj), databaseSetMethod.getParameterTypes()[1], false);
    }
    else {
      return "?";
    }
  }

  /** Writes an attribute of the passed value object to a {@link PreparedOperation}
   * @param obj the value object to read from
   * @param pop the {@link PreparedOperation} to write to
   * @param table the name of the database table, the operation refers to.
   *   This is only required in case of NULL attribute values, which require
   *   to retrieve the column type first. This is a nasty detail in the JDBC
   *   API which we must take care of. Some databases allow just to set type
   *   NULL in this case, but especially DB2 and Oracle do not. See method
   *   {@link PreparedStatement#setNull} for further details.
   * @param position index of the parameter in the prepared statement to write
   * @return next parameter index (i.e. passed value + 1)
   */
  public int getParameter(Object obj, PreparedOperationI pop, String table, int position)
      throws ReflectiveOperationException, SQLException {
    if (databaseSetMethod == null)
      throw new IllegalAccessException
          ("No prepared statement writer for " + databaseFieldName);
    Object attrValue = getValue(obj);
    if (fieldAccess.isConstantGetValue()) {
      return position + 1;
    }
    else if (attrValue != null) {
      attrValue = wrapArrayTypedValue(pop.getStatement().getConnection(), attrValue);
      pop.setBindParameter(databaseSetMethod, position, attrValue);
    }
    else {
      int columnType = getColumnType
          (pop.getStatement().getConnection(), pop.getDatabase().getPhysicalTableName(table), pop.getDatabase());
      pop.setBindParameterNull(position, columnType);
    }
    return position+1;
  }

  protected Object wrapArrayTypedValue(Connection connection, Object attrValue) throws SQLException {
    if (!(attrValue instanceof byte[]) && attrValue.getClass().isArray()) {
      attrValue = connection.createArrayOf("text", (Object[])attrValue);
    }
    return attrValue;
  }

  public String[] getRawAttributeMap() {
    return new String[] {
        databaseFieldName,
        fieldAccess.getterName(),
        fieldAccess.setterName(),
        null,
        revisioning ? FLAG_IS_REVISIONED : FLAG_IS_NOT_REVISIONED};
  }

}
