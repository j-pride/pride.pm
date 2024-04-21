/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

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
    void commit(TransactionEvent e) throws SQLException;

    /** Function to be called on transaction abortion
     * The listeners' rollback functions are called <i>before</i> the
     * actual rollback is performed. If any listener throws an Exception
     * the rollback will be ommited.
     */
    void rollback(TransactionEvent e) throws SQLException;
}

