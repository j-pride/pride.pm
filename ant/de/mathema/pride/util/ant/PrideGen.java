/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Ant Task
 *******************************************************************************/
package de.mathema.pride.util.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import de.mathema.pride.util.CreateTableTemplate;

/**
 * The one-and-only Ant task for PriDE's entity generator
 * 
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class PrideGen extends Task {
    
    public static final String MODE_HYBRID = "hybrid";
    public static final String MODE_BEAN = "bean";
    public static final String MODE_ADAPTER = "adapter";

    private String driver;
    private String url;
    private String user;
    private String password;
    private String table;
    private String clazz;
    private String baseClass;
    private String beanClass;
    private String sourceBase = ".";
    private boolean createPackagePath = true;
    private String mode = MODE_HYBRID;
    private Vector tables = new Vector();
    private Path classpath;
    private ClassLoader classLoader = this.getClass().getClassLoader();
    
    public void setClass(String string) { clazz = string; }
    public void setBaseClass(String string) { baseClass = string; }
    public void setBeanClass(String string) { beanClass = string; }
    public void setDriver(String string) { driver = string; }
    public void setPassword(String string) { password = string; }
    public void setTable(String string) { table = string; }
    public void setUrl(String string) { url = string; }
    public void setUser(String string) { user = string; }
    public void setMode(String string) { mode = string; }
    public void setSourceBase(String string) { sourceBase = string; }
    public void setCreatePackagePath(boolean bool) { createPackagePath = bool; }

    public Path createClasspath() {
        classpath = new Path(getProject());
        return classpath;
    }
    
    public Table createTable() {
        Table table = new Table();
        tables.add(table);
        return table;
    }
    
    public class Table {
        private String name;
        public String getName() { return name; }
        public void setName(String string) { name = string; }
    }

    protected void checkClass(String className, String classType) throws BuildException {
        if (className != null) {
            try { Class.forName(className, true, classLoader); }
            catch(ClassNotFoundException cnfx) {
                throw new BuildException(classType + " class " + className + " not found. Make sure the class is already compiled and accessible on the classpath!");
            }
        }
    }
    
    protected void checkParameters() throws BuildException {
        if (classpath != null)
            classLoader = new AntClassLoader(getProject(), classpath, true);
        if (table != null)
            createTable().setName(table);
        if (tables.size() == 0)
            throw new BuildException("Either the 'table' attribute or at least one table element must be set");
        if (clazz == null) {
            if (tables.size() == 1)
                clazz = ((Table)tables.get(0)).getName();
            else
                throw new BuildException("Attribute 'class' is mandatory when generating for multiple tables");
        }
        if (driver == null)
            throw new BuildException("Mandatory 'driver' attribute is missing");
        try { Class.forName(driver, true, classLoader); }
        catch(ClassNotFoundException cnfx) {
            throw new BuildException("Driver class " + driver + " not accessible");
        }
        if (url == null)
            throw new BuildException("Mandatory 'url' attribute is missing");
        if (mode.length() == 0 ||
            (!MODE_HYBRID.startsWith(mode) && !MODE_BEAN.startsWith(mode) && !MODE_ADAPTER.startsWith(mode)))
            throw new BuildException("Illegal mode '" + mode + "'.\nSupported values are '" + MODE_HYBRID
                + "', '" + MODE_BEAN + "', and '" + MODE_ADAPTER + "'");
        if (MODE_ADAPTER.startsWith(mode) && beanClass == null)
            throw new BuildException("Attribute 'beanClass' is mandatory for generating an adapter");
        checkClass(baseClass, "base");
        checkClass(beanClass, "bean");
        if (!new File(sourceBase).isDirectory())
            throw new BuildException("sourceBase '" + sourceBase + "' is not a valid directory");
    }
    
    protected String[] assembleTableList() {
        String[] result = new String[tables.size()];
        Iterator iter = tables.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            Table table = (Table)iter.next();
            result[i] = table.getName();
        }
        return result;
    }
    
    protected String assembleGenerationType() {
        if (MODE_ADAPTER.startsWith(mode))
            return beanClass;
        else
            return "-" + mode.substring(0, 1);
    }
    
    protected PrintStream createOutstream(String className) throws FileNotFoundException {
        File dir = new File(sourceBase);
        int pathcut = className.lastIndexOf(".");
        if (pathcut != -1) {
            String packageName = className.substring(0, pathcut);
            dir = new File(sourceBase + "/" + packageName.replace('.', '/'));
            className = className.substring(pathcut+1);
        }
        dir.mkdirs();
        if (!dir.isDirectory())
            throw new FileNotFoundException("Can't find or create directory " + dir.getPath());
        FileOutputStream fos = new FileOutputStream(dir.getPath() + "/" + className + ".java");
        return new PrintStream(fos);
    }
    
    public void execute() throws BuildException {
        checkParameters();
        String[] tableList = assembleTableList();
        String generationType = assembleGenerationType();
        try {
            CreateTableTemplate generator = new CreateTableTemplate
                (driver, url, user, password, tableList, clazz, generationType, baseClass, classLoader);
            String result = generator.create();
            PrintStream outstream = createOutstream(clazz);
            outstream.print(result);
            outstream.close();
        }
        catch(Exception x) {
            throw new BuildException("Generator error: " + x.getMessage());
        }
    }

}
