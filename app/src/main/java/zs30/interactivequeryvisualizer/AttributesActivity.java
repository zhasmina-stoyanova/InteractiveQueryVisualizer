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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * The class represents the attributes activity.
 * The associated screen is for selecting attributes
 * of the previously chosen lookup view.
 *
 * @version 1.0 August 2018
 * @author Zhasmina Stoyanova
 */
public class AttributesActivity extends AppCompatActivity {
    private List<Attribute> attributesList;
    private AttributesArrayAdapter adapter;
    private ListView attributesListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute);
        attributesListView = findViewById(R.id.attributes_list_view);
        attributesList = new ArrayList<>();

        //sets subtitle to the action bar
        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());

        //checks if the view hasn't been changed
        if (((GlobalVariables) getApplication()).getAttributesList().size() > 0) {
            attributesList = ((GlobalVariables) getApplication()).getAttributesList();
        } else {
            String lookupView = ((GlobalVariables) getApplication()).getLookupView();

            String urlToFormat = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";

            String url = Utils.replaceSpecialSymbolsUrl(urlToFormat);
            String response = Utils.getResponse(url);

            try {
                JSONArray jsonarray = new JSONArray(response);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String name = jsonobject.getString("name");
                    String type = jsonobject.getString("type");
                    //adds all the values from the response to teh llist
                    attributesList.add(new Attribute(name, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new AttributesArrayAdapter(attributesList, getApplicationContext());
        attributesListView.setAdapter(adapter);

        attributesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Attribute currAttrsItem = attributesList.get(position);
                currAttrsItem.setSelected(!currAttrsItem.isSelected());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void onTableBtn(View view) {
        ((GlobalVariables) getApplication()).setAttributesList(attributesList);
        //opens table page
        Intent intent = new Intent(AttributesActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onFilterBtn(View view) {
        ((GlobalVariables) getApplication()).setAttributesList(attributesList);
        //opens filter page
        Intent intent = new Intent(AttributesActivity.this, FiltersActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        ((GlobalVariables) getApplication()).setAttributesList(attributesList);
        //opens the attributes page
        Intent intent = new Intent(AttributesActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }
}

