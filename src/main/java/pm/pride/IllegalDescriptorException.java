/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

/**
 * This exception is thrown during static initialization if a
 * {@link pm.pride.RecordDescriptor} or an
 * {@link pm.pride.AttributeDescriptor} have been miss-defined.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class IllegalDescriptorException extends RuntimeException
{
    public IllegalDescriptorException(String reason) { super(reason); }

    public final static String REVISION_ID = "$Header: ";
}
