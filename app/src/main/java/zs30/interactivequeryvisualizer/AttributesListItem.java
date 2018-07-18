package zs30.interactivequeryvisualizer;


public class AttributesListItem {

    private String attributeName;
    private boolean attributeChecked;
    private String type;

    public AttributesListItem(String attributeName, String type) {
        this.setAttributeName(attributeName);
        this.setAttributeChecked(true);
        this.setType(type);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public boolean isAttributeChecked() {
        return attributeChecked;
    }

    public void setAttributeChecked(boolean attributeChecked) {
        this.attributeChecked = attributeChecked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}