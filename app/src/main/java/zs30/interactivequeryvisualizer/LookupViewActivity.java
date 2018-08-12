package zs30.interactivequeryvisualizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LookupViewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ListView list_view;
    private SimpleAdapter adapter;
    private SearchView searchView;
    private SwitchCompat switchGraphics;
    private Button buttonAtrrsGraphics;
    private Button buttonGraphics;
    private Button buttonAttrsTable;
    private Button buttonTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_view);

        buttonAtrrsGraphics = findViewById(R.id.button_attr_graphics);
        buttonGraphics = findViewById(R.id.button_graphics);
        buttonAttrsTable = findViewById(R.id.button_attr_table);
        buttonTable = findViewById(R.id.button_table);

        //check if graphics is on
        //if on hide attr table and table buttons
        //and show attr graphics and graphics buttons
        if (((GlobalVariables) getApplication()).isGraphicsBtnOn()) {
            buttonAtrrsGraphics.setVisibility(View.VISIBLE);
            buttonGraphics.setVisibility(View.VISIBLE);
            buttonAttrsTable.setVisibility(View.GONE);
            buttonTable.setVisibility(View.GONE);
        } else {
            buttonAtrrsGraphics.setVisibility(View.GONE);
            buttonGraphics.setVisibility(View.GONE);
            buttonAttrsTable.setVisibility(View.VISIBLE);
            buttonTable.setVisibility(View.VISIBLE);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        list_view = findViewById(R.id.attrs_list_view);
        searchView = findViewById(R.id.searchView);

        String lookupView = ((GlobalVariables) getApplication()).getLookupView();
        if (lookupView != "") {
            searchView.setQuery(lookupView, false);
        }

        //listener for the search view
        searchViewListener();

        //requests lookupview resource from the web service
        String request = Utils.getLookupViewsRequest();
        String response = Utils.getResponse(request);

        final ArrayList<Map<String, Object>> itemDataList = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                //values for the list view
                String name = jsonobject.getString("name");
                String description = jsonobject.getString("description");

                Map<String, Object> listItemMap = new HashMap<String, Object>();
                listItemMap.put("name", name);
                listItemMap.put("description", description);
                itemDataList.add(listItemMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //simple adapter
        adapter = new SimpleAdapter(this, itemDataList, R.layout.list_view,
                new String[]{"name", "description"}, new int[]{R.id.name, R.id.description});

        list_view.setAdapter(adapter);

        //list item on click listener
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String selectedName = itemDataList.get(index).get("name").toString();
                searchView.setQuery(selectedName, false);
                ((GlobalVariables) getApplication()).setLookupView(selectedName);
                //clear the old attributes list
                ((GlobalVariables) getApplication()).getAttributesList().clear();
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
        switch (item.getItemId()) {
            case R.id.switchButton:
                isChecked = !item.isChecked();
                if (isChecked) {
                    item.setIcon(R.drawable.toggle_right);
                } else {
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
                    switchGraphics = view.findViewById(R.id.switchButton);
                    switchGraphics.setOnCheckedChangeListener(this);
                }
            }
        }
        return true;
    }

    private void searchViewListener() {
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {
                LookupViewActivity.this.adapter.getFilter().filter(newText);
                ((GlobalVariables) getApplication()).setLookupView(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                LookupViewActivity.this.adapter.getFilter().filter(query);
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

    private boolean areRequiredFieldsFilled() {
        String selectedLookupView = ((GlobalVariables) getApplication()).getLookupView();
        if (selectedLookupView == null || selectedLookupView.equals("")) {
            searchView.clearFocus();
            Toast.makeText(this, "Please, choose a view!", Toast.LENGTH_SHORT).show();
            switchGraphics.setChecked(false);
            return false;
        }
        return true;
    }

    public void onTableBtn(View view) {
        if(areRequiredFieldsFilled()) {
            //opens table page
            Intent intent = new Intent(LookupViewActivity.this, TableActivity.class);
            startActivity(intent);
        }
    }

    public void onFilterBtn(View view) {
        if(areRequiredFieldsFilled()) {
            //opens filter page
            Intent intent = new Intent(LookupViewActivity.this, FiltersActivity.class);
            startActivity(intent);
        }
    }

    public void onAttributeBtn(View view) {
        if(areRequiredFieldsFilled()) {
            //opens attributes page
            Intent intent = new Intent(LookupViewActivity.this, AttributesActivity.class);
            startActivity(intent);
        }
    }

    public void onAttributeGraphicsBtn(View view) {
        if(areRequiredFieldsFilled()) {
            //opens attributes page
            Intent intent = new Intent(LookupViewActivity.this, AttributesGraphicsActivity.class);
            startActivity(intent);
        }
    }

    public void onGraphicsBtn(View view) {
        if(areRequiredFieldsFilled()) {
            //opens table page
            Intent intent = new Intent(LookupViewActivity.this, GraphicsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            ((GlobalVariables) getApplication()).setGraphicsBtnOn(true);
            //opens attributes page
            if(areRequiredFieldsFilled()) {
                Intent intent = new Intent(LookupViewActivity.this, AttributesGraphicsActivity.class);
                startActivity(intent);
            }
        }
    }
}
