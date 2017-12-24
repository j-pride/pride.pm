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
 * This exception is thrown during static initialization if a
 * {@link de.mathema.pride.RecordDescriptor} or an
 * {@link de.mathema.pride.AttributeDescriptor} have been miss-defined.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class IllegalDescriptorException extends RuntimeException
{
    public IllegalDescriptorException(String reason) { super(reason); }

    public final static String REVISION_ID = "$Header: ";
}
