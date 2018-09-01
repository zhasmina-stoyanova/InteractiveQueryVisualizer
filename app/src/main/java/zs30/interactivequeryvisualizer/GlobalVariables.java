package zs30.interactivequeryvisualizer;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The class contains global parameters.
 * It saves their values when going from one screen to
 * another, when a lookup view is already chosen.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class GlobalVariables extends Application {
    public static String lookupView;
    public static String sortByAttribute;
    //asc or desc
    public static String order;
    public static List<Attribute> attributesList = new ArrayList<>();
    public static final String IP_MOBILE_DEVICE = "192.168.42.16";
    public static Map<String, String> whereClauseParams = new HashMap<>();
    //flag if the graphics mode is turned on
    public static boolean graphicsComponentOn = false;
    public static String graphicsAttribute1;
    public static String graphicsAttribute2;
    public static String graphicsAttribute1Type;
    public static String graphicsAttribute2Type;
    public static String graphicsType;
    public static String username;
    public static String password;
}
