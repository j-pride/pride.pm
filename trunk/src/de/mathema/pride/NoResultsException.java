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
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class NoResultsException extends SQLException
{
    public NoResultsException(String reason) { super(reason); }
    public NoResultsException() { }

    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/general/error/NoResultsException.java,v 1.1 2001/06/25 16:23:42 lessner Exp $";
}

/* $Log: NoResultsException.java,v $
/* Revision 1.1  2001/06/25 16:23:42  lessner
/* *** empty log message ***
/*
 */
