package zs30.interactivequeryvisualizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ListView list_view;
    private SimpleAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_view = findViewById(R.id.attrs_list_view);
        searchView = findViewById(R.id.searchView);

        String lookupView =((GlobalVariables) getApplication()).getLookupView();
        if(lookupView != ""){
           searchView.setQuery(lookupView, false);
        }

        //listener for the search view
        searchViewListener();

        String url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews";

        String response = "";
        HttpServiceRequest getRequest = new HttpServiceRequest();
        try {
            response = getRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        final ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                //values for the list view
                String name = jsonobject.getString("name");
                String description = jsonobject.getString("description");

                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("name", name);
                listItemMap.put("description", description);
                itemDataList.add(listItemMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //simple adapter
        adapter = new SimpleAdapter(this,itemDataList,R.layout.list_view,
                new String[]{"name","description"},new int[]{R.id.name,R.id.description});

        list_view.setAdapter(adapter);

        //list item on click listener
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String selectedName = itemDataList.get(index).get("name").toString();;
                searchView.setQuery(selectedName, false);
                ((GlobalVariables) getApplication()).setLookupView(selectedName);
                //clear the old attributes list
                ((GlobalVariables) getApplication()).getAttrsListItems().clear();
                //clear the where clause params
                ((GlobalVariables) getApplication()).getWhereClauseParams().clear();
            }
        });

        //search text on focus
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void searchViewListener() {
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                MainActivity.this.adapter.getFilter().filter(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.adapter.getFilter().filter(query);
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void onTableBtn(View view) {
        //opens table page
        Intent intent = new Intent(MainActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onFilterBtn(View view) {
        //opens filter page
        Intent intent = new Intent(MainActivity.this, FilterActivity.class);
        startActivity(intent);
    }

    public void onAttributeBtn(View view) {
        //opens attributes page
        Intent intent = new Intent(MainActivity.this, AttributesActivity.class);
        startActivity(intent);
    }
}
