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

/**
 * This is a derivation from {@link StoredProcedure} to call functions rather than procedures.
 * The difference is that a function returns a value. The first public non-final member of
 * derived classes will receive the function's return value. For general information about
 * how to call stored procedures, see the Javadoc of the base class {@link StoredProcedure}
 * 
 * @author less02
 */
public abstract class StoredFunction extends StoredProcedure {

    @Override
    protected String assembleCallString() {
        return super.assembleCallString().replace("{", "{? = ");
    }

    @Override
    protected int getNumParams() {
        return super.getNumParams() - 1;
    }

    
}
