/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package de.mathema.pride;

import java.util.HashMap;
import java.util.Map;

import de.mathema.util.Singleton;

/**
 * Factory class to instantiate {@link Database} objects. All
 * operations refer to a working context which can be switched
 * by the setContext function. Contexts are used to identify
 * multiple database access schemes, defined by a database name,
 * a resource accessor, and an exception listener. This allows
 * e.g. to serve multiple databases in an application or
 * different types of connections to one database.
 * {@link RecordDescriptor}s may get passed a context identified
 * to express an association of an entity type to a particular
 * database access scheme. There is one {@link Database} object
 * created by context. There is always at least one context
 * available by default which is identified by DEFAULT_CONTEXT.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class DatabaseFactory
{
    public static final String DEFAULT_CONTEXT = "default";

    /** This class represents a working context, made up from
     * a database name, a resource accessor, and an exception
     * listener. A context is identified by name.
     */
    private static class Context {
        private static Map contextMap = new HashMap();
        public static Context getContext(String name) { return (Context)contextMap.get(name); }
        
        private final Singleton _singleton = new Singleton() {
                @Override
                protected Object createInstance() {
                    return new Database(dbname, accessor, exlistener, true);
                }
            };
        public String name;
        public String dbname;
        public ResourceAccessor accessor;
        public ExceptionListener exlistener = new ExceptionListener() {
                @Override
                public void process(Database db, Exception x) throws Exception { throw x; }
                @Override
                public RuntimeException processSevere(Database db, Exception x) {
                    x.printStackTrace();
                    throw new RuntimeException("Severe error in database operation detected by PriDE. " +
                            "The application state may be inconsistent. Immediate shut down is recommended!" + 
                            "Caught " + x.getClass().getName() + ": " + x.getMessage(), x);
                }
            };

        public Context(String name) {
            this.name = name;
            contextMap.put(name, this);
        }

        public Database getDatabase() {
            try { return (Database)_singleton.getInstance(); }
            catch(Exception x) {
                exlistener.processSevere(null, x);
                return null;
            }
        }
    }

    private static Context currentContext = new Context(DEFAULT_CONTEXT);


    /** Sets the current context's {@link ResourceAccessor}
     * to be used for {@link Database} instantiation in
     * {@link DatabaseFactory#getDatabase getDatabase}.
     */
    public static void setResourceAccessor(ResourceAccessor val) {
        currentContext.accessor = val;
    }

    /** Returns the current context's {@link ResourceAccessor} */
    public static ResourceAccessor getResourceAccessor() {
    return currentContext.accessor;
    }

    /** Sets the database name for the current context
     * to be used for {@link Database} instantiation in
     * {@link DatabaseFactory#getDatabase getDatabase}.
     */
    public static void setDatabaseName(String dbname) {
        currentContext.dbname = dbname;
    }

    /** Sets the {@link ExceptionListener} for the current context
     * to be used for {@link Database} instantiation in
     * {@link DatabaseFactory#getDatabase getDatabase}.
     */
    public static void setExceptionListener(ExceptionListener el) {
        currentContext.exlistener = el;
    }

    /** Returns the {@link Database} instance of the current context */
    public static Database getDatabase() { return currentContext.getDatabase(); }

    /** Returns the {@link Database} instance of the specified context.
     * @param contextName The context to refer to with <null> identifying
     * the current context.
     */
    public static Database getDatabase(String contextName) {
        if (contextName == null)
            return getDatabase();
        Context context = Context.getContext(contextName);
        return (context != null) ? context.getDatabase() : null;
    }

    /** Makes the specified context the current one and returns the context's
     * {@link Database} instance. If the context doesn't exist yet, it is
     * created and the function returns null. <null> identifies the current
     * context and thus causes no context switch.
     */
    public static Database setContext(String name) {
        if (name == null)
            return getDatabase();
        Context context = Context.getContext(name);
        if (context != null) {
            currentContext = context;
            return context.getDatabase();
        }
        else {
            currentContext = new Context(name);
            return null;
        }
    }

    public static void addContext(String name, String dbName, ResourceAccessor accessor) {
        Context context = Context.getContext(name);
        if (context == null) {
            context = new Context(name);
            context.dbname = dbName;
            context.accessor = accessor;
        }
    }
    
    /** Returns the name of the current database context */
    public static String getContext() { return currentContext.name; }

    private DatabaseFactory() { }

    public final static String REVISION_ID = "$Header: /framework/pride/src/de/mathema/pride/DatabaseFactory.java 3     3.02.06 13:41 Less02 $";
}

/* $Log: /framework/pride/src/de/mathema/pride/DatabaseFactory.java $
 * 
 * 3     3.02.06 13:41 Less02
 * Upgrade to PriDE 2.3.2
/* Revision 1.2  2001/07/31 18:09:28  lessner
/* Job monitor supports interactive toggling of SQL logging
/*
/* Revision 1.1.1.1  2001/06/19 09:20:24  lessner
/* XBCSetup Project Directory
/*
/* Revision 1.1.1.1  2001/06/19 08:19:08  lessner
/* XBCSetup Project Directory
/*
 */
