package zs30.interactivequeryvisualizer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class TableActivity extends AppCompatActivity {
    private Map<String, String> whereClauseParams = new HashMap<>();
    private Map<String, String> specialSymbolsURL = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());

        specialSymbolsURL.put("'", "%27");
        specialSymbolsURL.put(" ", "%20");

        String lookupView = ((GlobalVariables) getApplication()).getLookupView();
        String urlToFormat;

        if (((GlobalVariables) getApplication()).getAttributesList().size() > 0) {
            List<Attribute> attrsListItems = ((GlobalVariables) getApplication()).getAttributesList();
            //lists of attributes
            StringBuilder attrsList = new StringBuilder();

            //list of where clauses params
            boolean hasWhereClause = false;
            StringBuilder whereClauseList = null;
            //check if there are where clause params
            if (((GlobalVariables) getApplication()).getWhereClauseParams().size() > 0) {
                whereClauseList = new StringBuilder();
                hasWhereClause = true;
                whereClauseParams = ((GlobalVariables) getApplication()).getWhereClauseParams();
            }


            for (int i = 0; i < attrsListItems.size(); i++) {
                if (attrsListItems.get(i).isSelected()) {
                    String attrName = attrsListItems.get(i).getName();
                    attrsList.append(attrName + ",");

                    //if the map has values, gets those whose attributes has where clause values
                    //and assigns it to the edit text field
                    //if the attribute is not in the map, it does not have where clause value
                    if (!whereClauseParams.isEmpty() && whereClauseParams.get(attrName) != null) {
                        String whereClauseParamsValue = whereClauseParams.get(attrName);
                        whereClauseList.append(attrName + "=" + whereClauseParamsValue + ",");
                    }
                }
            }
            //delete the last symbol
            if (hasWhereClause) {
                whereClauseList = whereClauseList.deleteCharAt(whereClauseList.length() - 1);
            }
            attrsList = attrsList.deleteCharAt(attrsList.length() - 1);


            //check if order by and sort filter has been set
            String orderBy = "";
            String sortByAttribute = ((GlobalVariables) getApplication()).getSortByAttribute();
            String order = ((GlobalVariables) getApplication()).getOrder();
            if (sortByAttribute != null) {
                orderBy = "&orderBy=" + sortByAttribute + ":" + order;
            }

            //adds where clause if exists
            String where = "";
            if (hasWhereClause) {
                where = "&where=" + whereClauseList.toString();
            }

            urlToFormat = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/select?attributes=" + attrsList + where + orderBy;
            //urlToFormat = "http://192.168.42.16:8080/InteractiveQueryVisualizerWS/webapi/lookupviews/film_list/select?attributes=FID,title,description,category,price,length,rating,actors&where=title=%27AFFAIR%20PREJUDICE%27";
        } else {
            urlToFormat = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView;
        }

        //replace all special symbols in the url
        StringBuilder strToReplace = new StringBuilder(urlToFormat.toString());
        for (Map.Entry<String, String> entry : specialSymbolsURL.entrySet())
        {
            String url = "";
            if(strToReplace.toString().contains(entry.getKey())){
                url = strToReplace.toString().replace(entry.getKey(), entry.getValue());
                //clear stringBuilder
                strToReplace.setLength(0);
                strToReplace.append(url);
            }
        }

        String url = strToReplace.toString();

        String response = "";
        HttpServiceRequest getRequest = new HttpServiceRequest();
        try {
            response = getRequest.execute(url,toString()).get();
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
            trHeaders.setBackgroundColor(Color.parseColor("#0066AF"));
            trHeaders.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (String header : headers) {
                TextView cell11 = new TextView(this);
                cell11.setTextSize(17);
                cell11.setTypeface(null, Typeface.BOLD);
                cell11.setText(header);
                cell11.setPadding(5, 5, 15, 5);
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
                    tr.setBackgroundColor(Color.parseColor("#EFEFEF"));
                }

                tr.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.FILL_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));


                for (String attr : attrs) {
                    TextView cell11 = new TextView(this);

                    cell11.setText(attr);
                    cell11.setTextSize(16);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // the back button in the action bar
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
}
