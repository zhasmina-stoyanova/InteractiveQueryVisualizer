package zs30.interactivequeryvisualizer;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AttributesActivity extends AppCompatActivity {
    private List<AttributesListItem> attrsListItems;
    private AttributesListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute);
        ListView attrsListView = findViewById(R.id.attrs_list_view);
        attrsListItems = new ArrayList<>();

        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());

        //if the view hasn't been changed
            if (((GlobalVariables) getApplication()).getAttrsListItems().size() > 0) {
                attrsListItems = ((GlobalVariables) getApplication()).getAttrsListItems();
            } else {
            //connection freeze
            //String url = "http://10.0.2.2:8080/InteractiveQueryVisualizerWS/webapi/myresource";
            //connection refused
            //String url = "http://127.0.0.1:8080/InteractiveQueryVisualizerWS/webapi/myresource";
            //connection refused
            //String url = "http://136.168.42.16:8080/InteractiveQueryVisualizerWS/webapi/myresource";
            //no host
            //String url = "http://138.251.218.96:8080/InteractiveQueryVisualizerWS/webapi/myresource";
            //connection refused
            //String url = "http://localhost:8080/InteractiveQueryVisualizerWS/webapi/myresource";

            //activate tethering on phone(Samsung) or Hotspot -> USB Network and sharing(Lenovo)
            //see the ip of the connected network(ethernet adapter 2, for example) - ipconfig, not the network and sharing center connection
            //samsung
            //String ip = "192.168.42.16";
            //lenovo
            //String ip = "192.168.42.113";
            String lookupView = ((GlobalVariables) getApplication()).getLookupView();
            String url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";

            String response = "";
            HttpServiceRequest getRequest = new HttpServiceRequest();
            try {
                response = getRequest.execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            try {
                JSONArray jsonarray = new JSONArray(response);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String name = jsonobject.getString("name");
                    String type = jsonobject.getString("type");
                    attrsListItems.add(new AttributesListItem(name, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new AttributesListViewAdapter(attrsListItems, getApplicationContext());
        attrsListView.setAdapter(adapter);

        attrsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttributesListItem currAttrsItem = attrsListItems.get(position);
                currAttrsItem.setAttributeChecked(!currAttrsItem.isAttributeChecked());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void onTableBtn(View view) {
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);


        //opens table page
        Intent intent = new Intent(AttributesActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onFilterBtn(View view) {
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);

        //opens filter page
        Intent intent = new Intent(AttributesActivity.this, FilterActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);

        //opens attributes page
        Intent intent = new Intent(AttributesActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

