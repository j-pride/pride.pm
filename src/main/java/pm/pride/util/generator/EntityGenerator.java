/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *
 * Contributors:
 *     Jan Lessner, S&N AG
 *     Matthias Bartels, arvato direct services
 *******************************************************************************/
package pm.pride.util.generator;

/**
 * Generator class to generate entity types from database tables
 *
 * @author <a href="mailto:matthias.bertelsmann@bertelsmann.de">Matthias Bartels</a>
 */

import pm.pride.DatabaseFactory;
import pm.pride.RecordDescriptor;
import pm.pride.ResourceAccessor;
import pm.pride.ResourceAccessor.Config;
import pm.pride.ResourceAccessorJSE;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class EntityGenerator {

  // Types of generation work products
  public static final String HYBRID = "-h";
  public static final String BEAN = "-b";

  protected String className;
  protected String entityClassName;
  protected String baseClassName;
  protected String generationType;
  protected String[] tableNames;
  protected ClassLoader classLoader;
  protected List<TableColumn> flatTableColumns;
  protected TableDescription[] tableDesc;
  protected boolean generateAbstractClass;
  protected List<TableColumn> flatTableColumnList;
  protected ResourceAccessor resourceAccessor;
  protected String db;
  protected String dbType;

  public void postInit(String[] tableNames, String className,
      String generationType, String baseClassName)
      throws Exception {
    postInit(tableNames, className, generationType, baseClassName, ClassLoader.getSystemClassLoader());
  }

  public void postInit(String[] tableNames, String className,
      String generationType, String baseClassName, ClassLoader classLoader)
      throws Exception {
    this.generationType = generationType;
    this.tableNames = tableNames;
    this.className = className;
    this.baseClassName = baseClassName;
    this.entityClassName = generateDBA() ? generationType : className;
    this.classLoader = classLoader;
  }

  protected boolean generateBean() {
    return generationType.equals(BEAN);
  }

  protected boolean generateHybrid() {
    return generationType.equals(HYBRID);
  }

  protected boolean generateDBA() {
    return !generationType.equals(BEAN) && !generationType.equals(HYBRID);
  }

  protected void createResourceAccessor() throws Exception {
    if (System.getProperty(Config.DB) == null) {
      throw new IllegalArgumentException("Database URL must be defined by system property " + Config.DB);
    }
    resourceAccessor = ResourceAccessorJSE.fromSystemProps();
  }

  public RecordDescriptor extractRecordDescriptor(String className) throws SQLException {
    try {
      Class<?> c = Class.forName(className);
      Field f = c.getDeclaredField("red");
      f.setAccessible(true);
      return (RecordDescriptor)f.get(null);
    }
    catch(ClassNotFoundException cnfx) {
      throw new SQLException("Base class " + className + " not found.");
    }
    catch(NoSuchFieldException nsfx) {
      throw new SQLException("Base class " + className + " not suitable. Must have a static member of type RecordDescriptor");
    }
    catch(IllegalAccessException iax) {
      throw new SQLException("RecordDescriptor in base class " + className + " not accessible");
    }
  }

  /** Retrieve the fields being mapped by a specified class.
   * Retrieval is performed by reflection, assuming that the
   * class contains a static {@link RecordDescriptor} field "red"
   * which the field names can be retrieved from by
   * {@link RecordDescriptor#getFieldNames}. If you override the
   * template method {@link #writeRecordDescriptor} to create
   * record descriptor definitions differently, make sure to also
   * adapt this function accordingly.<br>
   * ATTENTION: This function tries to override standard security restrictions
   * to access the protected record descriptor member. This may cause
   * security exceptions.
   *
   * @param className The name of the class to retrieve the mapped fields from
   * @return The mapped fields
   */
  public Set<String> extractMappedFields(String className) throws SQLException {
    HashSet<String> fields = new HashSet<>();
    if (className != null) {
      RecordDescriptor desc = extractRecordDescriptor(className);
      String fieldsStr = desc.getFieldNames(null);
      StringTokenizer tokenizer = new StringTokenizer(fieldsStr, ",");
      while (tokenizer.hasMoreTokens())
        fields.add(tokenizer.nextToken());
    }
    return fields;
  }

  /** Retrieve the value type mapped by a specified class.
   * Retrieval is performed by reflection, assuming that the
   * class contains a static {@link RecordDescriptor} field "red"
   * which the value type name can be retrieved from by
   * {@link RecordDescriptor#getObjectType}.
   *
   * @param className The name of the class to retrieve the value type from
   * @return The name of the value type
   */
  public String extractBeanClass(String className) throws SQLException {
    RecordDescriptor desc = extractRecordDescriptor(className);
    return desc.getObjectType().getName();
  }

  /** Creates the entity type by successivly calling the
   * write... functions
   */
  public String create() throws Exception {
    createResourceAccessor();
    db = DatabaseFactory.getDatabase().getDBName();
    dbType = DatabaseFactory.getDatabase().getDBType();
    Connection con = getDBConnection();
    StringBuffer buffer = new StringBuffer();
    if (con != null) {
      tableDesc = getTableDescription(con);
      flatTableColumnList = flattenTableColumns(tableDesc);
      writePackage(tableDesc, className, baseClassName, generationType, buffer);
      writeHeader(tableDesc, className, baseClassName, generationType, buffer);
      writeConstants(tableDesc, className, baseClassName, generationType, buffer);
      writeRecordDescriptor(tableDesc, className, baseClassName, generationType, buffer);
      writeEntityReference(tableDesc, className, baseClassName, generationType, buffer);
      writeAttributes(tableDesc, className, baseClassName, generationType, buffer);
      writeGetMethods(tableDesc, className, baseClassName, generationType, buffer);
      writeSetMethods(tableDesc, className, baseClassName, generationType, buffer);
      writeFooter(tableDesc, className, baseClassName, generationType, buffer);
      con.rollback();
      con.close();
      con = null;
    } else {
      buffer.append("Aborting.... due to initialization problem");
    }
    return buffer.toString();
  }

  /** Opens up a database connection according to the
   * database driver, database name, user, and password specified by system properties
   */
  public Connection getDBConnection() throws Exception {
    return resourceAccessor.getConnection(db);
  }

  /** Generates a table description */
  public TableDescription[] getTableDescription(Connection con)
      throws SQLException {
    TableDescription[] tableDesc = new TableDescription[tableNames.length];
    for (int i = 0; i < tableNames.length; i++) {
      tableDesc[i] = new TableDescription(con, dbType, tableNames[i]);
      if (tableDesc[i].isPartial() || i > 0) {
        generateAbstractClass = true;
      }

    }
    return tableDesc;
  }

  /** Prints the package name to standard out */
  public void writePackage (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) {
    buffer.append(getPackage(className) + "\n");
    buffer.append("\n");
  }

  /** Prints the author tag to standard out, using the current
   * database user and database name as an identificatio
   */
  public void writeAuthor (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) throws Exception {
    String userName = System.getProperty("user.name");
    if (userName != null)
      buffer.append(" * @author " + userName + "\n");
    else
      buffer.append(" * @author " + resourceAccessor.getUserName(db) + "@" + db + "\n");
  }

  /** Determine the base class for the class to generate. When generating
   * a hybrid type, the base class is {@link pm.pride.MappedObject},
   * for adapters it is {@link pm.pride.ObjectAdapter} and for beans
   * it is nothing. If a base class is passed, it is takes as is. In case of
   * bean generation, the base class is assumed to specify the base adapter
   * class and the baes bean class is extracted by reflection.
   */
  protected String getBaseClassName(String baseClassName, String generationType)
      throws SQLException {
    if (baseClassName == null) {
      if (generateHybrid())
        return "MappedObject";
      else if (generateDBA())
        return "ObjectAdapter";
      else
        return null;
    }
    else {
      if (generateBean())
        return extractBeanClass(baseClassName);
      else
        return baseClassName;
    }
  }

  /** Prints the class header to standard out */
  public void writeHeader (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer)
      throws Exception {
    if (!generationType.equals(BEAN)) {
      buffer.append("import java.sql.SQLException;" + "\n");
      buffer.append("import pm.pride.*;" + "\n");
      buffer.append("\n");
    }
    buffer.append("/**" + "\n");
    writeAuthor(desc, className, baseClassName, generationType, buffer);
    buffer.append(" */" + "\n");
    if (generateAbstractClass) {
      buffer.append("abstract ");
    }
    buffer.append("public ");
    buffer.append("class " + getSimpleClassName(className) + " ");
    String actualBaseClassName = getBaseClassName(baseClassName, generationType);
    if (actualBaseClassName != null)
      buffer.append("extends " + actualBaseClassName + " ");
    if (baseClassName == null && !generateDBA())
      buffer.append("implements Cloneable, java.io.Serializable ");
    buffer.append("{\n");
  }

  public String toColumnConstant(TableColumn column) {
    return "COL_" + column.getNameUpper();
  }

  public void writeConstants (TableDescription[] desc, String className,
      String baseClassName, String generationType, StringBuffer buffer)
      throws SQLException {
    if (generationType.equals(BEAN))
      return;
    if (!generateAbstractClass)
      buffer.append("    public static final String TABLE = \"" + getCommaSeparatedTableNames() + "\";\n");
    Set<String> baseClassFields = extractMappedFields(baseClassName);
    for (TableColumn tableColumn: flatTableColumnList) {
      if (baseClassFields.contains(tableColumn.getName()))
        continue;
      buffer.append("    public static final String " + toColumnConstant(tableColumn) + " = \"");
      String qualifiedColumnName = (desc.length > 1) ?
          tableColumn.getTableName() + "." + tableColumn.getName() :
          tableColumn.getName();
      buffer.append(qualifiedColumnName + "\";\n");
    }
    buffer.append("\n");
  }

  /** Prints the record descriptor and its access function to standard out */
  public void writeRecordDescriptor (TableDescription[] desc, String className,
      String baseClassName, String generationType, StringBuffer buffer)
      throws SQLException {
    if (generateBean())
      return;

    Set<String> baseClassFields = extractMappedFields(baseClassName);
    buffer.append("    protected static final RecordDescriptor red =\n");
    buffer.append("        new RecordDescriptor(");
    buffer.append(getSimpleClassName(generationType.equals(HYBRID) ? className : generationType));
    buffer.append(".class, ");
    buffer.append(generateAbstractClass ? "null, " : "TABLE, ");
    buffer.append(baseClassName != null ? baseClassName + ".red" : "null");

    buffer.append(")");

    MethodNameResolver methodNameResolver = new MethodNameResolver();
    methodNameResolver.extractEntityMethods(generationType);

    for (TableColumn tableColumn: flatTableColumnList) {
      if (baseClassFields.remove(tableColumn.getName()))
        continue;
      StringBuffer sb = new StringBuffer();
      sb.append("\n            .row( ");
      sb.append(toColumnConstant(tableColumn));
      sb.append(", \"");
      sb.append(methodNameResolver.lookupGetter(tableColumn.getNameCamelCaseFirstUp()));
      sb.append("\", \"");
      sb.append(methodNameResolver.lookupSetter(tableColumn.getNameCamelCaseFirstUp()));
      sb.append("\" )");
      buffer.append(sb.toString());
    }

    writePrimaryKey(desc, className, baseClassName, generationType, buffer);

    buffer.append(";" + "\n\n");

    buffer.append("    public RecordDescriptor getDescriptor() { return red; }" + "\n");
    buffer.append("\n");
  }

  public void writeEntityReference(TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) {
    if (!generateDBA())
      return;

    buffer.append("    " + getSimpleClassName(className) + "(" +
        getSimpleClassName(generationType) + " entity) { ");
    buffer.append("super(entity);");
    buffer.append(" }\n\n");
  }

  private boolean hasPrimaryKey(TableDescription tdesc) {
    for(TableColumn current: tdesc.getColumnList()) {
      if (current.isPrimaryKeyField())
        return true;
    }
    return false;
  }

  /** Prints the primary key definition and its access function to standard out */
  public void writePrimaryKey (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) {
    if (generateBean())
      return;

    if (desc.length == 1) {
      if (hasPrimaryKey(desc[0])) {
        buffer.append("\n            .key( ");
        for(TableColumn current: desc[0].getColumnList()) {
          if (current.isPrimaryKeyField())
            buffer.append( toColumnConstant(current) + ", ");
        }
        buffer.deleteCharAt(buffer.length()-2);
        buffer.append(")");
      }
    }
  }

  /** Prints the data members to standard out */
  public void writeAttributes (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer)
      throws SQLException {
    if (generateDBA())
      return;

    Set<String> baseClassFields = extractMappedFields(baseClassName);

    for (TableColumn tableColumn: flatTableColumnList) {
      if (baseClassFields.contains(tableColumn.getName()))
        continue;
      buffer.append("    private " + tableColumn.getType() + " " + tableColumn.getNameCamelCaseFirstLow() + ";" + "\n");

    }

    buffer.append("\n");
  }

  /** Prints the geter methods for all members to standard out */
  public void writeGetMethods (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer)
      throws SQLException {
    if (generateDBA())
      return;

    Set<String> baseClassFields = extractMappedFields(baseClassName);

    buffer.append("    // Read access functions" + "\n");
    for (TableColumn tableColumn: flatTableColumnList) {
      if (baseClassFields.contains(tableColumn.getName()))
        continue;
      buffer.append("    public " + tableColumn.getType()  + " get"
          + tableColumn.getNameCamelCaseFirstUp() + "()   { return " + tableColumn.getNameCamelCaseFirstLow() + "; }" + "\n");

    }
    buffer.append("\n");
  }

  /** Prints the seter methods for all members to standard out */
  public void writeSetMethods (TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer)
      throws SQLException {
    if (generateDBA())
      return;

    Set<String> baseClassFields = extractMappedFields(baseClassName);

    buffer.append("    // Write access functions" + "\n");
    for (TableColumn tableColumn: flatTableColumnList) {
      if (baseClassFields.contains(tableColumn.getName()))
        continue;
      buffer.append("    public void set" + tableColumn.getNameCamelCaseFirstUp() + "(" + tableColumn.getType()
          + " " + tableColumn.getNameCamelCaseFirstLow() + ") { this." + tableColumn.getNameCamelCaseFirstLow() + " = " + tableColumn.getNameCamelCaseFirstLow() +
          "; }" + "\n");

    }
    buffer.append("\n");
  }

  public void writeToStringMethod(TableDescription[] desc, String className, String generationType, StringBuffer buffer) {
    if (!generationType.equals(BEAN) && !generationType.equals(HYBRID))
      return;
    // Something intelligent would be nice but is not yet implemented
  }

  public void writeClone(TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) {
    if (generateDBA())
      return;
    if (baseClassName != null)
      return;

    buffer.append("    public Object clone() throws CloneNotSupportedException {\n" +
        "        return super.clone();\n" +
        "    }\n" + "\n");
  }

  /** Prints a re-constructor to restore an existing records from its primary key fields
   * In case of a bean class, this is just a constructor with the primary key fields
   */
  public void writeReconstructor(TableDescription desc, String className,
      String baseClassName, StringBuffer buffer) {
    if (!desc.hasPrimaryKey() || generateDBA())
      return;
    buffer.append("\n    // Re-constructor\n");
    buffer.append("    public " + getSimpleClassName(className) + "(");
    for (TableColumn current: desc.getColumnList()) {
      if (current.isPrimaryKeyField())
        buffer.append((current.getType() != null? current.getType(): "Object") + " " + current.getName() + ", ");
    }
    buffer.delete(buffer.lastIndexOf(","), buffer.length());
    buffer.append(")");
    if (generateHybrid()) {
      buffer.append(" throws SQLException");
    }
    buffer.append(" {\n");
    if (baseClassName != null)
      buffer.append("        super(");
    for (TableColumn current: desc.getColumnList()) {
      if (current.isPrimaryKeyField()) {
        if (baseClassName == null)
          buffer.append("        set" + current.getNameCamelCaseFirstUp() + "(" + current.getName() + ");\n");
        else
          buffer.append(current.getName() + ", ");
      }
    }
    if (baseClassName != null) {
      buffer.delete(buffer.lastIndexOf(","), buffer.length());
      buffer.append(");\n");
    }
    if (generateHybrid() && baseClassName == null) {
      buffer.append("        findXE();\n");
    }
    buffer.append("    }\n");
  }

  /** Prints the footer to standard out, i.e. a reconstructor, a default constructor,
   * a toString method, a clone method, and the closing bracket
   */
  public void writeFooter(TableDescription[] desc, String className, String baseClassName,
      String generationType, StringBuffer buffer) {
    if (desc.length == 1) {
      if (desc[0].getColumnList().size() > 0)
        writeReconstructor(desc[0], className, baseClassName, buffer);
      buffer.append("\n");
      if (!generateDBA()) {
        buffer.append("    public " + getSimpleClassName(className) + "() {}\n\n");
      }
    }

    writeToStringMethod(desc, className, generationType, buffer);
    writeClone(desc, className, baseClassName, generationType, buffer);

    buffer.append("}" + "\n");
    buffer.append("\n" + "\n");
  }

  protected String getTableIdType(final TableDescription tabCols) {
    String idType;

    List<TableColumn> tableList = tabCols.getColumnList();
    if (tableList.size() > 0) {
      TableColumn current = (TableColumn) tableList.get(0);
      idType = current.getType();
    }
    else
      idType = "String";

    return idType;
  }


  protected String getPackage(String className) {
    String pack = "";
    if (className.indexOf(".") != -1)
      pack = "package " + className.substring(0, className.lastIndexOf(".")) + ";";
    return pack;
  }

  protected String getSimpleClassName(String className) {
    if (className.indexOf(".") != -1)
      return className.substring(className.lastIndexOf(".") + 1);
    else
      return className;
  }

  protected String getCommaSeparatedTableNames() {
    StringBuffer buf = new StringBuffer();
    for (TableDescription desc: this.tableDesc) {
      buf.append(",");
      buf.append(desc.getTableName());
    }
    return buf.toString().substring(1);
  }

  protected List<TableColumn> flattenTableColumns(TableDescription[] tableDescriptions) {
    List<String> usedColumnNames = new LinkedList<>();
    List<String> notUniqueColumnNames = new LinkedList<>();
    List<TableColumn> flatTableColumnList = new ArrayList<>();

    for (TableDescription tableDescription: tableDescriptions) {
      for (TableColumn tableColumn: tableDescription.getColumnList()) {
        flatTableColumnList.add(tableColumn);
        if (usedColumnNames.contains(tableColumn.getName())) {
          notUniqueColumnNames.add(tableColumn.getName());
        }
        else {
          usedColumnNames.add(tableColumn.getName());
        }
      }
    }
    for (TableColumn tableColumn: flatTableColumnList) {
      if (notUniqueColumnNames.contains(tableColumn.getName())) {
        tableColumn.makeUnique();
      }
    }
    return flatTableColumnList;
  }

  /**
   * put your documentation comment here
   * @param args
   * @exception SQLException
   */
  public EntityGenerator (String[] args) throws Exception {
    if (args.length < 1) {
      System.out.println("Usage: EntityGenerator tablename(s) [class] [beanclass | -b | -h] [baseclass]");
      System.exit(0);
    }
    String tableName = args[0];
    String className = null;
    String baseClassName = null;
    String generationType = HYBRID;
    String[] tableNames = null;
    if (tableName.indexOf(",") != -1) {
      StringTokenizer st = new StringTokenizer(tableName, ",");
      tableNames = new String[st.countTokens()];
      for (int i = 0; i < tableNames.length; i++) {
        tableNames[i] = st.nextToken();
      }
    }
    else {
      tableNames = new String[] {
          tableName
      };
    }
    if (args.length > 1)
      className = args[1];
    else {
      for (int i = 0; i < tableNames.length; i++) {
        if (className == null) {
          className = new String("");
        }
        className += tableNames[i];
      }
      className = className.substring(0,1).toUpperCase()+className.substring(1);
    }
    if (args.length > 2) {
      if (args[1].equals(BEAN))
        generationType = BEAN;
      else if (!args[1].equals(HYBRID))
        generationType = args[2];
    }
    if (args.length > 3)
      baseClassName = args[3];
    postInit(tableNames, className, generationType, baseClassName);
  }

  public void createAndPrint() throws Exception {
    System.out.println(create());
  }

  public static void main (String[] args) throws Exception {
    new EntityGenerator(args).createAndPrint();
  }

}
