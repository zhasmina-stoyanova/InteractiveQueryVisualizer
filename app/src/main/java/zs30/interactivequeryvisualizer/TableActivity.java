package zs30.interactivequeryvisualizer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        String lookupView = ((GlobalVariables) getApplication()).getLookupView();
        String url;
        if (((GlobalVariables) getApplication()).getAttrsListItems().size() > 0) {
            List<AttributesListItem> attrsListItems = ((GlobalVariables) getApplication()).getAttrsListItems();
            StringBuilder attrsList = new StringBuilder();
            for(int i = 0; i < attrsListItems.size(); i++){
                if(attrsListItems.get(i).isAttributeChecked()) {
                    attrsList.append(attrsListItems.get(i).getAttributeName() + ",");
                }
            }

            attrsList = attrsList.deleteCharAt(attrsList.length() - 1);
            url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/select?attributes=" + attrsList;
        } else {
            url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView;
        }

        String response = "";
        HttpServiceRequest getRequest = new HttpServiceRequest();
        try {
            response = getRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        TableLayout tl = (TableLayout) findViewById(R.id.table);
        int count = 0;

        //get headers
        try {
            //read only the first row of pairs attributeName, attributeValue
            JSONArray jsonarray = new JSONArray(response);
            JSONObject jsonobject = jsonarray.getJSONObject(0);
            String name = jsonobject.getString("row");
            JSONArray jsonarray2 = new JSONArray(name);
            String[] headers = new String[jsonarray2.length()];
            for (int j = 0; j < jsonarray2.length(); j++) {
                JSONObject jsonobject2 = jsonarray2.getJSONObject(j);
                String attrName = jsonobject2.getString("attributeName");
                headers[j] = attrName;
            }

            TableRow trHeaders = new TableRow(this);
            trHeaders.setBackgroundColor(Color.parseColor("#1f3d7a"));
            trHeaders.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (String header : headers) {
                TextView cell11 = new TextView(this);

                cell11.setTextSize(16);
                cell11.setTypeface(null, Typeface.BOLD);
                cell11.setText(header);
                cell11.setPadding(5, 0, 15, 5);
                cell11.setTextColor(Color.WHITE);
                trHeaders.addView(cell11);
            }

            tl.addView(trHeaders, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //get values
        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("row");
                JSONArray jsonarray2 = new JSONArray(name);
                String[] attrs = new String[jsonarray2.length()];
                for (int j = 0; j < jsonarray2.length(); j++) {
                    JSONObject jsonobject2 = jsonarray2.getJSONObject(j);
                    String attrValue = jsonobject2.getString("attributeValue");
                    attrs[j] = attrValue;
                }

                TableRow tr = new TableRow(this);

                if (count % 2 != 0) {
                    tr.setBackgroundColor(Color.WHITE);
                } else {
                    tr.setBackgroundColor(Color.parseColor("#ebf0fa"));
                }

                tr.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));


                for (String attr : attrs) {
                    TextView cell11 = new TextView(this);

                    cell11.setText(attr);
                    cell11.setPadding(5, 0, 15, 5);
                    tr.addView(cell11);
                }
                tl.addView(tr, new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                count++;
            }
            // }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
