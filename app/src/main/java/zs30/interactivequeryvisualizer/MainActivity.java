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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ListView list_view;
    private ArrayAdapter<String> adapter;
    private final List<String> car_brands = new ArrayList<>();
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

        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("name");
                car_brands.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(list_view.getContext(), R.layout.list_view, R.id.product_name, car_brands);
        list_view.setAdapter(adapter);

        //search text on focus
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // OnItemClickListener for list_view
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                searchView.setQuery(selectedItem, false);
                ((GlobalVariables) getApplication()).setLookupView(selectedItem);
                //clear the old attributes list
                ((GlobalVariables) getApplication()).getAttrsListItems().clear();
                //clear the where clause params
                ((GlobalVariables) getApplication()).getWhereClauseParams().clear();
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
