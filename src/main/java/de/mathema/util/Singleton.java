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
package de.mathema.util;

/**
 * Klasse fuer Implementierung des Singleton-Patterns.
 * <br>Benutzung z.B. mit einer anonymen Klasse:<br>
 * <code>
 * class MyClass {
 * ...
 *   private static final Singleton _singleton = new Singleton() {
 *     protected Object createInstance() {
 *       return new MyClass();
 *     }
 *   };
 * ...
 *   public static MyClass getInstance() {
 *     return (MyClass)_singleton.getInstance();
 *   }
 * }
 * </code>
 * @author Uwe Wardenbach (uwe.wardenbach@gmx.de)
 */
public abstract class Singleton
{
    protected Object _singleInstance;

    private boolean creating = false;
    
/**
 * Eine Klasse die einen Singleton benutzen will, ueberlaedt diese Methode
 * @return description
 */
    protected abstract Object createInstance() throws Exception;

/**
 * implementiert threadsafe, aber effizient den Zugriff auf die
 * Singleton-Instanz und bei Bedarf deren Erzeugung ueber
 * <code>createInstance()</code><br>
 * Synchronisationsmechanismen werden nur benutzt, wenn der Singleton noch
 * nicht erzeugt ist
 * @return  Referenz auf das mit createInstance() erzeugte Objekt
 */
    public final Object getInstance() throws Exception {

/*
 * benutzt das Pattern double checked locking (D.Schmidt), um den overhead
 * durch Synchronisation auf den benoetigten Fall zu reduzieren
 */
        if ( _singleInstance == null )
            synchronized (this) {
                if ( _singleInstance == null ) {
                    if(creating)
                        throw new RuntimeException("recursive creation");
                    creating = true;
                    _singleInstance = createInstance();
                    creating = false;
                }
            }
        return _singleInstance;
    } // getInstance()
}

/*
 * $Log: Singleton.java,v $
 */
