package basic;
import pm.pride.JoinRecordDescriptor;
import pm.pride.RecordDescriptor;
import pm.pride.SQL;

/**
 * Extension of {@link Customer} representing a customer and fragments of 
 * another joined customer. The class uses the same artificial join condition
 * like {@link JoinedCustomer} but doesn't join a complete customer. This is
 * a different pattern which does *not* make use of the {@link JoinRecordDescriptor}
 */
public class JoinedCustomerFragments extends Customer {
	public static final String HUSBAND_ALIAS = "husband";
	public static final String WIFE_ALIAS = "wife";
	public static final String TABLE_JOIN = SQL.build(
			"@customer @husband join @customer @wife on " +
			"@wife.@lastName = @husband.@lastName and @wife.@id > @husband.@id",
			TABLE, HUSBAND_ALIAS, WIFE_ALIAS,
			COL_LASTNAME, COL_ID);

	private int wifeId;
	private String wifeFirstName;
	
    public int getWifeId() { return wifeId; }
	public void setWifeId(int wifeId) { this.wifeId = wifeId; }
	public String getWifeFirstName() { return wifeFirstName; }
	public void setWifeFirstName(String wifeFirstName) { this.wifeFirstName = wifeFirstName; }

	public static RecordDescriptor red = new RecordDescriptor
			(JoinedCustomerFragments.class, null, TABLE_JOIN, HUSBAND_ALIAS, Customer.red)
    	.from(WIFE_ALIAS)
    	.row(COL_FIRSTNAME, "getWifeFirstName", "setWifeFirstName")
    	.row(COL_ID, "getWifeId", "setWifeId");
    		
    public RecordDescriptor getDescriptor() { return red; }
}
