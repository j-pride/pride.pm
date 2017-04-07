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
 * This is just a helper class, preventing a string value
 * from getting formatted when passed to any expression builder
 * function of {@link WhereCondition}. E.g. use this type to
 * express an equality check between two database fields like
 * this:<p>
 * <tt>exp.and("field1", new SQLRaw("field2"))</tt>
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class SQLRaw
{
    protected String fieldname;
    
    public SQLRaw(String fieldname) { this.fieldname = fieldname; }

    public String toString() { return fieldname; }
    
    public final static String REVISION_ID = "$Header:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/SQLRaw.java-arc   1.0   06 Sep 2002 14:52:52   math19  $";
}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/SQLRaw.java-arc  $
 * 
 *    Rev 1.0   06 Sep 2002 14:52:52   math19
 * Initial revision.
 * 
 *    Rev 1.1   Jul 25 2002 14:51:06   math19
 * Javadocs added.
 */
