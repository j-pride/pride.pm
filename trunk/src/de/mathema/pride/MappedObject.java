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


/**
 * Convenience derivation from {@link ObjectAdapter}, assuming
 * that the entity to operate on is this object itself. This
 * base class is of interest for hybrid entities containing both
 * the actual data and the mapping information. This may be nice
 * for small projects to keep all related information in one class.
 * However, in larger projects it is recommended to separate the
 * mapping from the value objects as it is intended in base class
 * {@link ValueObjectAdapter}
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class MappedObject extends ObjectAdapter
{
	public Object getEntity() { return this; }
	
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/DatabaseRecord.java,v 1.7 2001/07/24 11:47:05 lessner Exp $";
}

/* $Log: MappedRecord.java,v $
 */
