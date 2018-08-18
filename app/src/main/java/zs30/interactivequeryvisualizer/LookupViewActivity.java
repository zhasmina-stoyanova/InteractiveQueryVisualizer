package zs30.interactivequeryvisualizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
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

public class LookupViewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ListView list_view;
    private ArrayAdapter<String> adapter;
    private SwitchCompat switchGraphics;
    private Button buttonAtrrsGraphics;
    private Button buttonGraphics;
    private Button buttonAttrsTable;
    private Button buttonTable;
    private EditText inputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup_view);

        buttonAtrrsGraphics = findViewById(R.id.button_attr_graphics);
        buttonGraphics = findViewById(R.id.button_graphics);
        buttonAttrsTable = findViewById(R.id.button_attr_table);
        buttonTable = findViewById(R.id.button_table);

        list_view = findViewById(R.id.list_view);
        inputSearch = findViewById(R.id.inputSearch);

        ((GlobalVariables) getApplication()).setLookupView("");

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

        //requests lookupview resource from the web service
        String request = Utils.getLookupViewsRequest();
        String response = Utils.getResponse(request);

        final ArrayList<String> itemDataList = new ArrayList<>();

        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                //values for the list view
                String name = jsonobject.getString("name");
                itemDataList.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.name, itemDataList);
        list_view.setAdapter(adapter);

        //filtering
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                LookupViewActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        //list item on click listener
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String selectedName = itemDataList.get(index);
                inputSearch.setText(selectedName);
               // searchView.setQuery(selectedName, false);
                ((GlobalVariables) getApplication()).setLookupView(selectedName);
                //clear the old attributes list
                ((GlobalVariables) getApplication()).getAttributesList().clear();
                //clear the where clause params
                ((GlobalVariables) getApplication()).getWhereClauseParams().clear();
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

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean areRequiredFieldsFilled() {
        String selectedLookupView = ((GlobalVariables) getApplication()).getLookupView();
        if (selectedLookupView == null || selectedLookupView.equals("")) {
            //searchView.clearFocus();
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
