package zs30.interactivequeryvisualizer;


public class AttributesListItem {

    private String attributeName;
    private boolean attributeChecked;

    public AttributesListItem(String attributeName) {
        this.attributeName = attributeName;
        this.attributeChecked = false;
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
}