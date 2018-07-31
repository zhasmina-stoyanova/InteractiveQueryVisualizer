package zs30.interactivequeryvisualizer;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalVariables extends Application {
    private  String lookupView;
    private String sortByAttribute;
    private String order;
    private  List<AttributesListItem> attrsListItems = new ArrayList<AttributesListItem>();
    public static final String IP_MOBILE_DEVICE = "192.168.42.16";
    private Map<String, String> whereClauseParams = new HashMap<>();
    private boolean graphicsBtnOn = false;
    private String graphicsXAxisAttr;
    private String graphicsYAxisAttr;
    private String graphicsXAxisAttrType;
    private String graphicsYAxisAttrType;
    private String graphicsType;

    public String getLookupView() {
        return lookupView;
    }

    public void setLookupView(String lookupView) {
        this.lookupView = lookupView;
    }

    public List<AttributesListItem> getAttrsListItems() {
        return attrsListItems;
    }

    public void setAttrsListItems(List<AttributesListItem> attrsListItems) {
        this.attrsListItems = attrsListItems;
    }

    public String getSortByAttribute() {
        return sortByAttribute;
    }

    public void setSortByAttribute(String sortByAttribute) {
        this.sortByAttribute = sortByAttribute;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, String> getWhereClauseParams() {
        return whereClauseParams;
    }

    public void setWhereClauseParams(Map<String, String> whereClauseParams) {
        this.whereClauseParams = whereClauseParams;
    }

    public boolean isGraphicsBtnOn() {
        return graphicsBtnOn;
    }

    public void setGraphicsBtnOn(boolean graphicsBtnOn) {
        this.graphicsBtnOn = graphicsBtnOn;
    }

    public String getGraphicsXAxisAttr() {
        return graphicsXAxisAttr;
    }

    public void setGraphicsXAxisAttr(String graphicsXAxisAttr) {
        this.graphicsXAxisAttr = graphicsXAxisAttr;
    }

    public String getGraphicsYAxisAttr() {
        return graphicsYAxisAttr;
    }

    public void setGraphicsYAxisAttr(String graphicsYAxisAttr) {
        this.graphicsYAxisAttr = graphicsYAxisAttr;
    }

    public String getGraphicsType() {
        return graphicsType;
    }

    public void setGraphicsType(String graphicsType) {
        this.graphicsType = graphicsType;
    }

    public String getGraphicsXAxisAttrType() {
        return graphicsXAxisAttrType;
    }

    public void setGraphicsXAxisAttrType(String graphicsXAxisAttrType) {
        this.graphicsXAxisAttrType = graphicsXAxisAttrType;
    }

    public String getGraphicsYAxisAttrType() {
        return graphicsYAxisAttrType;
    }

    public void setGraphicsYAxisAttrType(String graphicsYAxisAttrType) {
        this.graphicsYAxisAttrType = graphicsYAxisAttrType;
    }

    //example for call from other activities
    //((GlobalVariables) getApplication()).setViewName("56");
    //String viewName = ((GlobalVariables) getApplication()).getViewName();
}
