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
package de.mathema.pride;

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

    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/TransactionEvent.java,v 1.1 2001/07/24 12:31:26 lessner Exp $";
}

/* $Log: TransactionEvent.java,v $
/* Revision 1.1  2001/07/24 12:31:26  lessner
/* Support for database transaction listeners added
/*
 */
