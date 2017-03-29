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

import java.sql.SQLException;

/**
 * This interface is to be implemented by types which want to be
 * informed about completion or cancelation of transactions.
 * TransactionListeners can be registered at a database object
 * by method {@link Database#addListener Database.addListener}.
 * TransactionListeners should only be used in J2SE environments.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public interface TransactionListener
{
    /** Function to be called on successful completion of a transaction.
     * The listeners' commit functions are called <i>before</i> the
     * actual commitment is performed. If any listener throws an Exception
     * the commitment will be ommited.
     */
    public void commit(TransactionEvent e) throws SQLException;

    /** Function to be called on transaction abortion
     * The listeners' rollback functions are called <i>before</i> the
     * actual rollback is performed. If any listener throws an Exception
     * the rollback will be ommited.
     */
    public void rollback(TransactionEvent e) throws SQLException;
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/TransactionListener.java,v 1.1 2001/07/24 12:31:26 lessner Exp $";
}

/* $Log: TransactionListener.java,v $
/* Revision 1.1  2001/07/24 12:31:26  lessner
/* Support for database transaction listeners added
/*
 */
