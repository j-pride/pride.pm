package pm.pride.basic;
import java.sql.Date;

import pm.pride.*;

public class Name extends MappedObject {
    private int id;
    private String firstname;
    private String lastname;
    private Date birthdate;

    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public Date getBirthDate() { return birthdate; }

    public void setId(int val) { id = val; }
    public void setFirstname(String val) { firstname = val; }
    public void setLastname(String val) { lastname = val; }
    public void setBirthDate(Date val) { birthdate = val; }

    public static RecordDescriptor red = new RecordDescriptor
	(Name.class, "name", null, new String[][] {
	    { "id", "getId", "setId" },
	    { "firstname", "getFirstname", "setFirstname" },
	    { "lastname", "getLastname", "setLastname" },
	    { "birthdate", "getBirthDate", "setBirthDate" }
	});
    public RecordDescriptor getDescriptor() { return red; }
}
