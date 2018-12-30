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
package pm.pride;

/**
 * Convenience baseclass, providing a set of ready-to-use standard
 * methods for interaction between the database and data entity
 * objects. This is a derivation from DatabaseAdapter assuming that
 * derived types provide functions to access the type's
 * record descriptor, primary key definition and operation value object
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class ObjectAdapter implements DatabaseAdapterMixin {
    private Object entity;
    
	/** Returns the value object the adapter is operating on */
    public Object getEntity() { return entity; }
    
    protected ObjectAdapter(Object entity) { this.entity = entity; }

}
