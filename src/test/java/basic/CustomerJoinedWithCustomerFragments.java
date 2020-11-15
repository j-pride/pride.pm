package basic;
import pm.pride.JoinRecordDescriptor;
import pm.pride.RecordDescriptor;
import pm.pride.SQL;

import static basic.AbstractPrideTest.TEST_TABLE;

/**
 * Extension of {@link Customer} representing a customer and fragments of 
 * another joined customer. The class uses the same artificial join condition
 * like {@link CustomerJoinedWithCustomer} but doesn't join a complete customer. This is
 * a different pattern which does *not* make use of the {@link JoinRecordDescriptor}
 */
public class CustomerJoinedWithCustomerFragments extends Customer {
	public static final String HUSBAND_ALIAS = "husband";
	public static final String WIFE_ALIAS = "wife";

	private int wifeId;
	private String wifeFirstName;
	
    public int getWifeId() { return wifeId; }
	public void setWifeId(int wifeId) { this.wifeId = wifeId; }
	public String getWifeFirstName() { return wifeFirstName; }
	public void setWifeFirstName(String wifeFirstName) { this.wifeFirstName = wifeFirstName; }

	public static RecordDescriptor red = new JoinRecordDescriptor
		(CustomerJoinedWithCustomerFragments.class, Customer.red, "husband")
			.join(TEST_TABLE, "wife", "wife.lastName = husband.lastName and wife.id > husband.id")
	    		.row(COL_FIRSTNAME, "getWifeFirstName", "setWifeFirstName")
	    		.row(COL_ID, "getWifeId", "setWifeId");
			

	@Override
    public RecordDescriptor getDescriptor() { return red; }
	
	@Override
	public String toString() {
		return super.toString() + "/" + wifeFirstName + "/" + wifeId;
	}
}
