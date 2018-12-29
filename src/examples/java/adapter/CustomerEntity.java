package adapter;

/**
 * @author jlessner
 */
public class CustomerEntity implements Cloneable, java.io.Serializable {
    private long id;
    private String name;
    private String firstName;

    // Read access functions
    public long getId()   { return id; }
    public String getName()   { return name; }
    public String getFirstName()   { return firstName; }

    // Write access functions
    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }


    // Reconstructor
    public CustomerEntity(long id) {
        setId(id);
    }

    public CustomerEntity() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
