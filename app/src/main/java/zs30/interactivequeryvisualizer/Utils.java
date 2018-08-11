package zs30.interactivequeryvisualizer;

import java.util.concurrent.ExecutionException;

public class Utils {
    //public static String url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";
    //request to the web service to check the username and password against the database, when the user logs in
    private static String authenticationRequest = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/authentication";

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
}
