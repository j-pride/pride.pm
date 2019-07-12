/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
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
