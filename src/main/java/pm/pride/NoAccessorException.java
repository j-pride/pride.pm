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
 * This expection is thrown when trying to instantiate a database
 * without having defined a valid {@link ResourceAccessor}. If you
 * are using {@link DatabaseFactory#getDatabase()} for instantiation,
 * make shure to have initialized the factory propperly by calling
 * {@link DatabaseFactory#setResourceAccessor}.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class NoAccessorException extends Exception
{
    public NoAccessorException(String reason) { super(reason); }
    public NoAccessorException() {}

    public final static String REVISION_ID = "$Header: ";
}
