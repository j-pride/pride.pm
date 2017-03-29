package basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import de.mathema.pride.MappedObject;
import de.mathema.pride.RecordDescriptor;

/**
 * Base class for all entities being identified by an integer ID field.
 * In fact the class Customer os the only derivation in the JUnit test
 * suite. The only reason for this class is to test derivations of
 * record descriptors based on a somehow realistic example ;-)

 * @author jlessner
 */
public abstract class IdentifiedEntity extends MappedObject implements Cloneable {
	private int id = 0;

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	protected IdentifiedEntity() {}

	protected IdentifiedEntity(int id) { this.id = id; }

	protected RecordDescriptor getDescriptor() { return red; }

	protected static RecordDescriptor red =
		new RecordDescriptor(IdentifiedEntity.class, null, null, new String[][] {
			{ "id", "getId", "setId"},
		}
	);

}
