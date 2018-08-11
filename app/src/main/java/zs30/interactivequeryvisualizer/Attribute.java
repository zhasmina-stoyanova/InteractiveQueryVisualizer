package zs30.interactivequeryvisualizer;

/**
 * The class represents an attribute in the database.
 * It contains methods for setting and getting the name and type of each
 * attribute and a flag that indicated whether the attribute is selected or not.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class Attribute {
    private String name;
    //flag if the attribute is selected or deselected.
    private boolean isSelected;
    private String type;

    /**
     * Initializes a new instance of the class by passing the attribute name and type
     *
     * @param name the name of the attribute in a table
     * @param type the data type of the attribute in a table
     */

    public Attribute(String name, String type) {
        this.setName(name);
        this.setSelected(true);
        this.setType(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Used when choosing graphics attribute
     * from the spinner.
     *
     * @return the name of the attribute
     */
    @Override
    public String toString() {
        return this.name;
    }
}