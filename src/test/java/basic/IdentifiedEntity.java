package basic;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;
import pm.pride.RevisionedRecordDescriptor;

/**
 * Base class for all entities being identified by an integer ID field.
 * In fact the class Customer os the only derivation in the JUnit test
 * suite. The only reason for this class is to test derivations of
 * record descriptors based on a somehow realistic example ;-)

 * @author jlessner
 */
public abstract class IdentifiedEntity extends MappedObject<IdentifiedEntity> implements Cloneable {
	private int id = 0;

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	protected IdentifiedEntity() {}

	protected IdentifiedEntity(int id) { this.id = id; }

	public RecordDescriptor getDescriptor() { return red; }

	protected static RecordDescriptor red =
		new RecordDescriptor(IdentifiedEntity.class, null, null, new String[][] {
			{ "id", "getId", "setId" },
		}
	);

}
