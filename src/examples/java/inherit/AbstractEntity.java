package inherit;

/**
 * @author jlessner
 */
abstract public class AbstractEntity implements Cloneable, java.io.Serializable {
    private int id;

    // Read access functions
    public int getId()   { return id; }

    // Write access functions
    public void setId(int id) { this.id = id; }


    // Re-constructor
    public AbstractEntity(int id) {
        setId(id);
    }

    public AbstractEntity() {}

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
