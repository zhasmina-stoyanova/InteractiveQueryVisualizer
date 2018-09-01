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

/**
 * The class represents the graphics activity.
 * The associated screen is for displaying the
 * data in the form of bar chart or line graph.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class GraphicsActivity extends AppCompatActivity {
    private Map<String, String> whereClauseParams = new HashMap<>();
    private ArrayList<BarEntry> entries = new ArrayList<>();
    private ArrayList<String> labels = new ArrayList<String>();
    private ArrayList<Entry> entriesLineGraph = new ArrayList<>();
    private BarChart barChart;
    private LineChart lineGraph;
    private String graphicsAttribute1;
    private String graphicsAttribute2;
    private String graphicsAttribute1Type;
    private String graphicsAttribute2Type;
    private String graphicsType;
    private String lookupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout resource
        setContentView(R.layout.activity_graphics);

        //referring objects to UI components
        barChart = findViewById(R.id.chart);
        lineGraph = findViewById(R.id.line_graph);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarSubtitle();

        loadGlobalParameters();

        //set the visibility of the UI component depending on the previously chosen data type
        if (graphicsType.equals("Bar Chart")) {
            lineGraph.setVisibility(View.GONE);
        } else if (graphicsType.equals("Line Graph")) {
            barChart.setVisibility(View.GONE);
        }

        String urlToFormat;

        //if the user has been to the filters screen the attributesList will not be empty
        if (GlobalVariables.attributesList.size() > 0) {
            List<Attribute> attributeList = (GlobalVariables.attributesList);
            //lists of attributes
            StringBuilder attrsList = new StringBuilder();

            //list of where clauses params
            boolean hasWhereClause = false;
            StringBuilder whereClauseList = null;
            //check if there are where clause params
            if (GlobalVariables.whereClauseParams.size() > 0) {
                whereClauseList = new StringBuilder();
                hasWhereClause = true;
                whereClauseParams = GlobalVariables.whereClauseParams;
            }

            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).isSelected()) {
                    String attrName = attributeList.get(i).getName();
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
            String sortByAttribute = GlobalVariables.sortByAttribute;
            String order = GlobalVariables.order;
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

        String url = Utils.replaceSpecialSymbols(urlToFormat);
        String response = Utils.getResponse(url);

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

                    if (attrName.equals(graphicsAttribute1) || attrName.equals(graphicsAttribute2)) {
                        if (attrName.equals(graphicsAttribute1)) {
                            if (graphicsAttribute1Type.equals("number")) {
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
                            if (graphicsAttribute2Type.equals("number")) {
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

        visualizeBarChartData();
        visualizeLineGraphData();
    }

    //if the user has chosen these parameters
    //from previous screens
    private void loadGlobalParameters(){
        graphicsAttribute1 = GlobalVariables.graphicsAttribute1;
        graphicsAttribute2 = GlobalVariables.graphicsAttribute2;
        graphicsAttribute1Type = GlobalVariables.graphicsAttribute1Type;;
        graphicsAttribute2Type = GlobalVariables.graphicsAttribute2Type;
        graphicsType = GlobalVariables.graphicsType;
        lookupView = GlobalVariables.lookupView;
    }

    //sets subtitle for the action bar
    private void setActionBarSubtitle() {
        getSupportActionBar().setSubtitle(GlobalVariables.lookupView);
    }

    //display data through bar chart
    public void visualizeBarChartData(){
        //bar chart data
        BarDataSet dataset = new BarDataSet(entries, "entries");
        //each entry gets colors
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(labels, dataset);
        barChart.setData(data);
        //a description located on the right bottom corner of the chart
        barChart.setDescription(GlobalVariables.lookupView);
    }

    //display data through line graph
    public void visualizeLineGraphData(){
        //line graph data
        LineDataSet datasetLineGraph = new LineDataSet(entriesLineGraph, "entries");
        //each entry gets colors
        datasetLineGraph.setColors(ColorTemplate.COLORFUL_COLORS);

        LineData dataLineGraph = new LineData(labels, datasetLineGraph);
        lineGraph.setData(dataLineGraph);
        //a description located on the right bottom corner of the graph
        lineGraph.setDescription(GlobalVariables.lookupView);
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
}
