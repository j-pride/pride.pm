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
 * Convenience implementation of {@link DatabaseAdapterMixin}, assuming
 * that the entity to operate on is this object itself. This
 * base class is of interest for hybrid entities containing both
 * the actual data and the mapping information. This may be nice
 * for small projects to keep all related information in one class.
 * However, in larger projects it is recommended to separate the
 * mapping from the value objects as it is intended in base class
 * {@link ObjectAdapter}
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class MappedObject implements DatabaseAdapterMixin {
	@Override
	public Object getEntity() { return this; }
}
