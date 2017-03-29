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
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public interface SQLFormatter
{
    public String formatValue(Object rawValue);

    public String formatOperator(String operator, Object rawValue);
    
    public Object formatPreparedValue(Object rawValue);

    public final static String REVISION_ID = "$Header:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/SQLFormatter.java-arc   1.0   06 Sep 2002 14:52:52   math19  $";
}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/SQLFormatter.java-arc  $
 * 
 *    Rev 1.0   06 Sep 2002 14:52:52   math19
 * Initial revision.
 * 
 *    Rev 1.1   Jul 25 2002 16:49:18   math19
 * Function formatOperator() added.
 */
