package zs30.interactivequeryvisualizer;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceRequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String res = null;
        String line;
        try {
            String name = GlobalVariables.getUsername();
            String password = GlobalVariables.getPassword();
            String authString = name + ":" + password;

            byte[] data = authString.getBytes("UTF-8");
            String token = Base64.encodeToString(data, Base64.DEFAULT);

            URL myUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) myUrl.openConnection();

            String basicAuth = "Basic " + token;
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            //conn.setDoOutput(false);
            conn.connect();

            int status = conn.getResponseCode();

            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            isr.close();
            res = sb.toString();

        } catch (IOException e) {
            System.out.println(e);
            res = "401 Unauthorized";
        }
        return res;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}