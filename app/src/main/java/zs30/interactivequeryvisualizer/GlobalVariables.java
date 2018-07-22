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


    //example for call from other activities
    //((GlobalVariables) getApplication()).setViewName("56");
    //String viewName = ((GlobalVariables) getApplication()).getViewName();
}
