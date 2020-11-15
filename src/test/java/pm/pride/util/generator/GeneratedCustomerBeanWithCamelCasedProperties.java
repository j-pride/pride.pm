package pm.pride.util.generator;

/** This class represents an {@link EntityGenerator} output with manually fine-tuned attribute
 * and appropriate getter/setter method names. Running the generator based on this class causes
 * the manual modifications to appear in the new output as well. See appropriate tests in
 * {@link EntityGeneratorTest} */
public class GeneratedCustomerBeanWithCamelCasedProperties implements Cloneable, java.io.Serializable {
    private long id;
    private String type;
    private String lastName;
    private String firstname; // getter/setter use camel case, attribute does not
    private java.util.Date hiredate;
    private Long active;

    // Read access functions
    public long getId()   { return id; }
    public String getType()   { return type; }
    public String getLastName()   { return lastName; }
    public String getFirstName()   { return firstname; }
    public java.util.Date getHiredate()   { return hiredate; }
    public Long getActive()   { return active; }

    // Write access functions
    public void setId(long id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstname = firstName; }
    public void setHiredate(java.util.Date hiredate) { this.hiredate = hiredate; }
    public void setActive(Long active) { this.active = active; }

    // Re-constructor
    public GeneratedCustomerBeanWithCamelCasedProperties(long id) {
        setId(id);
    }

    public GeneratedCustomerBeanWithCamelCasedProperties() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
