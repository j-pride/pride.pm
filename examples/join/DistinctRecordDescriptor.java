/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Example code
 *******************************************************************************/
package join;

import de.mathema.pride.*;

/**
 * RecordDescriptor for the definition of distinct joins
 * 
 * @author <a href="mailto:matthias.bartels@bertelsmann.de>Matthias Bartels</a>
 */
public class DistinctRecordDescriptor extends RecordDescriptor {

  public DistinctRecordDescriptor
      (Class objectType, String dbContext, String dbtable,
       RecordDescriptor baseDescriptor, String[][] attributeMap)
      throws IllegalDescriptorException {
      super(objectType, dbContext, dbtable, baseDescriptor, attributeMap);
  }

  public DistinctRecordDescriptor
      (Class objectType, String dbtable,
       RecordDescriptor baseDescriptor, String[][] attributeMap)
      throws IllegalDescriptorException {
      super(objectType, dbtable, baseDescriptor, attributeMap);
  }

  /** Return the comma-separated list of result fields, preceeded
   * by the keyword DISTINCT
   */
  protected String getResultFields() { return "DISTINCT " + super.getResultFields(); }

}
