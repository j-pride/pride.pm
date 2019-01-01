package inherit;

/**
 * @author jlessner
 */
public class DerivedCustomerEntity extends inherit.AbstractEntity {
    private String name;
    private String firstName;

    // Read access functions
    public String getName()   { return name; }
    public String getFirstName()   { return firstName; }

    // Write access functions
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; }


    // Re-constructor
    public DerivedCustomerEntity(int id) {
        super(id);
    }

    public DerivedCustomerEntity() {}

}
