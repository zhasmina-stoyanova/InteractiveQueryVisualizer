package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represents the attributes activity.
 * The associated screen is for selecting attributes
 * of the previously chosen lookup view.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class AttributesActivity extends AppCompatActivity {
    private List<Attribute> attributesList;
    private AttributesArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout resource
        setContentView(R.layout.activity_attribute);
        //sets the attributes list view
        ListView attributesListView = findViewById(R.id.attributes_list_view);
        attributesList = new ArrayList<>();

        setActionBarSubtitle();
        initializeAttributesList();

       /**
        * Initializing custom adapter with the list of attributes
        * that represents the name and the checkbox of each attribute
        * and the application context.
        */
        adapter = new AttributesArrayAdapter(attributesList, getApplicationContext());
        attributesListView.setAdapter(adapter);

        //on attribute checkbox click
        attributesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attribute currAttrsItem = attributesList.get(position);
                currAttrsItem.setSelected(!currAttrsItem.isSelected());
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Initializing attributes list with already chosen values,
     * if the user has been to this screen before,
     * or with new values requested from the web service.
     */
    private void initializeAttributesList(){
        //checks if the view hasn't been changed or initialized
        if (GlobalVariables.attributesList.size() > 0) {
            attributesList = GlobalVariables.attributesList;
        } else {
            String lookupView = GlobalVariables.lookupView;
            //requests lookupView attributes resource from the web service
            String request = Utils.getLookupViewAttributesRequest(lookupView);
            //replaces special symbols in the request
            String requestFormatted = Utils.replaceSpecialSymbols(request);
            //gets response from the web service
            String response = Utils.getResponse(requestFormatted);

            addResponseResultToList(response, attributesList);
        }
    }

    //sets subtitle for the action bar
    private void setActionBarSubtitle() {
        getSupportActionBar().setSubtitle(GlobalVariables.lookupView);
    }

    //sets the attributes list with the response values
    private void addResponseResultToList(String response, List<Attribute> attributesList) {
        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("name");
                String type = jsonobject.getString("type");
                //adds the name and type of each attribute from the response to the list
                attributesList.add(new Attribute(name, type));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //on table button click
    public void onTableBtn(View view) {
        setGlobalAttributesList(attributesList);
        //opens table page
        Intent intent = new Intent(AttributesActivity.this, TableActivity.class);
        startActivity(intent);
    }

    //on filter button click
    public void onFilterBtn(View view) {
        setGlobalAttributesList(attributesList);
        //opens filters page
        Intent intent = new Intent(AttributesActivity.this, FiltersActivity.class);
        startActivity(intent);
    }

    //on views button click
    public void onViewsBtn(View view) {
        setGlobalAttributesList(attributesList);
        //opens the attributes page
        Intent intent = new Intent(AttributesActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }

    //sets the global list of attribute names
    private void setGlobalAttributesList(List<Attribute> attributesList) {
        GlobalVariables.attributesList = attributesList;
    }
}

