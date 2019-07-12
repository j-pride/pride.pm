/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

/**
 * The exception listener interface must be implemented by types
 * which want to get informed about unexpected exceptions within
 * PriDE API operations. Every {@link Database} objects has the
 * ExceptionListener associated which is set as default listener
 * in the {@link DatabaseFactory}. You can register your own
 * ExceptinListener by calling
 * {@link DatabaseFactory#setExceptionListener
 *  setDefaultExceptionListener}.
 *  
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public interface ExceptionListener
{
    /** Process an exception which caused the current operation to fail
     * but does not compromise the system integrity.
     * @param db The database object the operation of which caused
     *    the exception. This might be null, if the exception occured
     *    in the {@link DatabaseFactory} or during static initialization
     * @param x The exception to report
     * @throws Exception The passed exception x, if no reasonable handling
     *    can be applied.
     */
    public void process(Database db, Exception x) throws Exception;

    /** Process an exception which compromise the system integrity.
     * In the current version, PriDE always calls this function.
     * @param db The database object the operation of which caused
     *    the exception. This might be null, if the exception occured
     *    in the {@link DatabaseFactory} or during static initialization
     * @param x The exception to report
     * @throws RuntimeException if any. In general, the
     *    function is supposed not to return at all but to shut down
     *    the application safely.
     */
    public RuntimeException processSevere(Database db, Exception x) throws RuntimeException;
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/TransactionListener.java,v 1.1 2001/07/24 12:31:26 lessner Exp $";
}
