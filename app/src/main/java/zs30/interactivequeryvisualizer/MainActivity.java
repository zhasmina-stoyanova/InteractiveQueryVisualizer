package zs30.interactivequeryvisualizer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ListView list_view;
    private SimpleAdapter adapter;
    private SearchView searchView;
    SwitchCompat mainSwitchOnOffSw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());
       // getSupportActionBar().setCustomView(R.layout.switch_layout);
       // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM);

       // Switch button = findViewById(R.id.actionbar_switch);
       // button.setOnCheckedChangeListener(this);

        //SwitchCompat button = findViewById(R.id.switchButton);


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

    private boolean isChecked = false;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu.findItem(R.id.sort_by_name).setChecked(true);
        switch (item.getItemId()) {
            case R.id.switchButton:
                isChecked = !item.isChecked();
                //item.setTitle("some");
                if(isChecked){
                    item.setIcon(R.drawable.toggle_right);
                }else{
                    item.setIcon(R.drawable.toggle_left);
                }
                item.setChecked(isChecked);

                return true;
            default:
                item.setIcon(R.drawable.seek_thumb_disabled);
                return false;
      }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.graphics_switch) {
                View view = MenuItemCompat.getActionView(item);
                if (view != null) {
                    mainSwitchOnOffSw = view.findViewById(R.id.switchButton);
                    mainSwitchOnOffSw.setOnCheckedChangeListener(this);
                }
            }
        }

        return true;
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


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Toast.makeText(MainActivity.this, String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
    }
}
