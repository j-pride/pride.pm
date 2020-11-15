package basic;

import pm.pride.JoinRecordDescriptor;
import pm.pride.MappedObject;
import pm.pride.RecordDescriptor;

import static basic.AbstractPrideTest.TEST_TABLE;

public class CustomerFragments extends MappedObject implements Cloneable {

	String lastName;
	String husbandFirstName;
	String wifeFirstName;
	
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getHusbandFirstName() { return husbandFirstName; }
	public void setHusbandFirstName(String husbandFirstName) { this.husbandFirstName = husbandFirstName; }
	public String getWifeFirstName() { return wifeFirstName; }
	public void setWifeFirstName(String wifeFirstName) { this.wifeFirstName = wifeFirstName; }

	public static RecordDescriptor red =
    	new JoinRecordDescriptor(CustomerFragments.class, TEST_TABLE, "husband")
    		  .row("lastName", "getLastName", "setLastName")
    		  .row("firstName", "getHusbandFirstName", "setHusbandFirstName")
    	.join(TEST_TABLE, "wife", "wife.lastName = husband.lastName and wife.firstName != husband.firstName")
    		  .row("firstName", "getWifeFirstName", "setWifeFirstName");
    		
    public RecordDescriptor getDescriptor() { return red; }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
    	return lastName + ": " + husbandFirstName + " / " + wifeFirstName;
    }
}
