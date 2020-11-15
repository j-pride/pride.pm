package pm.pride.util.generator;

import java.sql.SQLException;
import pm.pride.*;

/**
 * @author benutzer1
 */
public class GeneratedCustomerHybridWithCamelCasedProperties extends MappedObject implements Cloneable, java.io.Serializable {
    public static final String TABLE = "CUSTOMER_PRIDE_TEST";
    public static final String COL_ID = "id";
    public static final String COL_TYPE = "type";
    public static final String COL_LASTNAME = "lastname";
    public static final String COL_FIRSTNAME = "firstname";
    public static final String COL_HIREDATE = "hiredate";
    public static final String COL_ACTIVE = "active";

    protected static final RecordDescriptor red =
            new RecordDescriptor(GeneratedCustomerHybridWithCamelCasedProperties.class, TABLE, null)
                    .row( COL_ID, "getId", "setId" )
                    .row( COL_TYPE, "getType", "setType" )
                    .row( COL_LASTNAME, "getLastname", "setLastname" )
                    .row( COL_FIRSTNAME, "getFIRSTname", "setFIRSTname" )
                    .row( COL_HIREDATE, "getHiredate", "setHiredate" )
                    .row( COL_ACTIVE, "getActive", "setActive" )
                    .key( COL_ID );

    public RecordDescriptor getDescriptor() { return red; }

    private long id;
    private String type;
    private String lastNAME;
    private String firstname;
    private java.util.Date hiredate;
    private Long active;

    // Read access functions
    public long getId()   { return id; }
    public String getType()   { return type; }
    public String getLastname()   { return lastNAME; }
    public String getFIRSTname()   { return firstname; }
    public java.util.Date getHiredate()   { return hiredate; }
    public Long getActive()   { return active; }

    // Write access functions
    public void setId(long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setLastname(String lastname) { this.lastNAME = lastname; }
    public void setFIRSTname(String firstname) { this.firstname = firstname; }
    public void setHiredate(java.util.Date hiredate) { this.hiredate = hiredate; }
    public void setActive(Long active) { this.active = active; }


    // Re-constructor
    public GeneratedCustomerHybridWithCamelCasedProperties(long id) throws SQLException {
        setId(id);
        findXE();
    }

    public GeneratedCustomerHybridWithCamelCasedProperties() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
