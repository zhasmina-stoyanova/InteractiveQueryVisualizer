package zs30.interactivequeryvisualizer;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class GlobalVariables extends Application {
    private  String lookupView;
    private  List<AttributesListItem> attrsListItems = new ArrayList<AttributesListItem>();
    public static final String IP_MOBILE_DEVICE = "192.168.42.16";

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

//example for call from other activities
    //((GlobalVariables) getApplication()).setViewName("56");
    //String viewName = ((GlobalVariables) getApplication()).getViewName();
}
