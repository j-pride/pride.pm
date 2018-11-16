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
package pm.pride;


/**
 * Convenience derivation from ObjectAdapter, operating on a passed
 * value object.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class ValueObjectAdapter extends ObjectAdapter
{
	private Object entity;
	
	public Object getEntity() { return entity; }

	public ValueObjectAdapter(Object entity) {
		this.entity = entity;
	}
	
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/DatabaseRecord.java,v 1.7 2001/07/24 11:47:05 lessner Exp $";
}

