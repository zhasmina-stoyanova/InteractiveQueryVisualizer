package zs30.interactivequeryvisualizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class GraphicsActivity extends AppCompatActivity {
    private Map<String, String> whereClauseParams = new HashMap<>();
    private Map<String, String> specialSymbolsURL = new HashMap<>();
    private ArrayList<BarEntry> entries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<String>();
    ArrayList<Entry> entriesLineGraph = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());
        String graphicsXAxisAttr = ((GlobalVariables) getApplication()).getGraphicsXAxisAttr();
        String graphicsYAxisAttr = ((GlobalVariables) getApplication()).getGraphicsYAxisAttr();
        String graphicsXAxisAttrType = ((GlobalVariables) getApplication()).getGraphicsXAxisAttrType();
        String graphicsYAxisAttrType = ((GlobalVariables) getApplication()).getGraphicsYAxisAttrType();
        String graphicsType = ((GlobalVariables) getApplication()).getGraphicsType();

        // To make vertical bar chart, initialize graph id this way
        BarChart barChart = (BarChart) findViewById(R.id.chart);
        //barChart.getXAxis().setLabelsToSkip(0);

        LineChart lineGraph = (LineChart) findViewById(R.id.line_graph);
        //lineGraph.getXAxis().setLabelsToSkip(0);

        //show bar chart or line graph
        if (graphicsType.equals("Bar Chart")) {
            lineGraph.setVisibility(View.GONE);
        } else if (graphicsType.equals("Line Graph")) {
            barChart.setVisibility(View.GONE);
        }

        specialSymbolsURL.put("'", "%27");
        specialSymbolsURL.put(" ", "%20");

        String lookupView = ((GlobalVariables) getApplication()).getLookupView();
        String urlToFormat;

        if (((GlobalVariables) getApplication()).getAttrsListItems().size() > 0) {
            List<AttributesListItem> attrsListItems = ((GlobalVariables) getApplication()).getAttrsListItems();
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
                if (attrsListItems.get(i).isAttributeChecked()) {
                    String attrName = attrsListItems.get(i).getAttributeName();
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
        for (Map.Entry<String, String> entry : specialSymbolsURL.entrySet()) {
            String url = "";
            if (strToReplace.toString().contains(entry.getKey())) {
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
            response = getRequest.execute(url, toString()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //get values
        try {
            int indexEntry = 0;

            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("row");
                JSONArray jsonarray2 = new JSONArray(name);
                String[] attrs = new String[2];
                for (int j = 0; j < jsonarray2.length(); j++) {
                    JSONObject jsonobject2 = jsonarray2.getJSONObject(j);
                    String attrName = jsonobject2.getString("attributeName");
                    String attrValue = jsonobject2.getString("attributeValue");

                    if (attrName.equals(graphicsXAxisAttr) || attrName.equals(graphicsYAxisAttr)) {

                        if (attrName.equals(graphicsXAxisAttr)) {
                            if (graphicsXAxisAttrType.equals("number")) {
                                //bar chart ot line graph graphics type is chosen
                                if (graphicsType.equals("Bar Chart")) {
                                    entries.add(new BarEntry(Float.parseFloat(attrValue), indexEntry));
                                } else if (graphicsType.equals("Line Graph")) {
                                    entriesLineGraph.add(new Entry(Float.parseFloat(attrValue), indexEntry));
                                }
                                indexEntry++;
                            } else {
                                labels.add(attrValue);
                            }
                        } else {
                            if (graphicsYAxisAttrType.equals("number")) {
                                //bar chart ot line graph graphics type is chosen
                                if (graphicsType.equals("Bar Chart")) {
                                    entries.add(new BarEntry(Float.parseFloat(attrValue), indexEntry));
                                } else if (graphicsType.equals("Line Graph")) {
                                    entriesLineGraph.add(new Entry(Float.parseFloat(attrValue), indexEntry));
                                }
                                indexEntry++;
                            } else {
                                labels.add(attrValue);
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //bar chart data
        BarDataSet dataset = new BarDataSet(entries, "entries");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(labels, dataset);
        barChart.setData(data);
        barChart.setDescription(((GlobalVariables) getApplication()).getLookupView());

        //line graph data
        LineDataSet datasetLineGraph = new LineDataSet(entriesLineGraph, "entries");
        datasetLineGraph.setColors(ColorTemplate.COLORFUL_COLORS);

        LineData dataLineGraph = new LineData(labels, datasetLineGraph);
        lineGraph.setData(dataLineGraph);
        lineGraph.setDescription(((GlobalVariables) getApplication()).getLookupView()); // set the description

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
