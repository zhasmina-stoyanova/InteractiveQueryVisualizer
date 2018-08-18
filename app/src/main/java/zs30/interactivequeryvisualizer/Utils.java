package zs30.interactivequeryvisualizer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Utils {
    //public static String url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";
    //request to the web service to check the username and password against the database, when the user logs in
    private static String authenticationRequest = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/authentication";

    public static String getLookupViewsRequest() {
        return lookupViewsRequest;
    }

    public static void setLookupViewsRequest(String lookupViewsRequest) {
        Utils.lookupViewsRequest = lookupViewsRequest;
    }

    private static String lookupViewsRequest = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews";

    public static String getResponse(String url) {
        String response = "";
        HttpServiceRequest getRequest = new HttpServiceRequest();
        try {
            response = getRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static String getAuthenticationRequest() {
        return authenticationRequest;
    }

    public static void setAuthenticationRequest(String authenticationRequest) {
        Utils.authenticationRequest = authenticationRequest;
    }

    public static String replaceSpecialSymbolsUrl(String urlToFormat){
        Map<String, String> specialSymbols = new HashMap<>();
        specialSymbols.put("'", "%27");
        specialSymbols.put(" ", "%20");
        //replace all special symbols in the url
        StringBuilder strToReplace = new StringBuilder(urlToFormat);
        for (Map.Entry<String, String> entry : specialSymbols.entrySet()) {
            String url = "";
            if (strToReplace.toString().contains(entry.getKey())) {
                url = strToReplace.toString().replace(entry.getKey(), entry.getValue());
                //clear stringBuilder
                strToReplace.setLength(0);
                strToReplace.append(url);
            }
        }

        return strToReplace.toString();
    }
}
