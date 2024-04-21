/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import java.util.EventObject;

/**
 * Event type passed in event functions of the {@link TransactionListener}
 * interface. The emiting database is passed as event source object.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class TransactionEvent extends EventObject
{
    public TransactionEvent(Database db) { super(db); }

}
