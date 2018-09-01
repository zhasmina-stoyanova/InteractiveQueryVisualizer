package zs30.interactivequeryvisualizer;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The class represents the lookupView activity.
 * The associated screen is for selecting a lookupview
 * and choosing display type for it(if not using the default one).
 * There are options that redirect the user to refine the data by selecting
 * attributes or filters of the chosen view.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class LookupViewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private ArrayAdapter<String> adapter;
    private SwitchCompat switchGraphics;
    private EditText inputSearch;
    private List<String> lookupViewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout resource
        setContentView(R.layout.activity_lookup_view);
        lookupViewsList = new ArrayList<>();

        //refering objects to UI components
        ListView list_view = findViewById(R.id.list_view);
        inputSearch = findViewById(R.id.inputSearch);

        //clears the lookup view every time the activity loads
        GlobalVariables.lookupView = "";

        //when the user returns to the screen the graphics is off
        GlobalVariables.graphicsComponentOn = false;

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //requests lookupview resource from the web service
        String request = Utils.getLookupViewsRequest();
        //replaces special symbols in the request
        String requestFormatted = Utils.replaceSpecialSymbols(request);
        //gets response from the web service
        String response = Utils.getResponse(requestFormatted);

        addResponseResultToList(response, lookupViewsList);

        /**
         * Initializing an array adapter with the list of lookup views
         * that represents the name and the checkbox of each attribute
         * and the application context.
         */
        adapter = new ArrayAdapter<>(this, R.layout.list_view, R.id.name, lookupViewsList);
        list_view.setAdapter(adapter);

        //listener when text changes in the search field
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // triggers the filter when the user changes the text
                LookupViewActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        //listens when an item in the lookupViewsList is being clicked on
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                String selectedName = lookupViewsList.get(index);
                inputSearch.setText(selectedName);
                GlobalVariables.lookupView = selectedName;
                //clears the old attributes list
                GlobalVariables.attributesList.clear();
                //clears the where clause params
                GlobalVariables.whereClauseParams.clear();
            }
        });
    }

    //sets the lookupviews list with the response values
    private void addResponseResultToList(String response, List<String> lookupViewsList) {
        try {
            JSONArray jsonarray = new JSONArray(response);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                //adds the name of each lookupview from the response to the list
                String name = jsonobject.getString("name");
                lookupViewsList.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switchComponent:
                boolean isGraphicsModeOn = !item.isChecked();
                //setting up the icon depending whether the graphics mode is on or off
                if (isGraphicsModeOn) {
                    item.setIcon(R.drawable.toggle_right);
                } else {
                    item.setIcon(R.drawable.toggle_left);
                }
                item.setChecked(isGraphicsModeOn);
                return true;
            default:
                item.setIcon(R.drawable.toggle_left);
                return false;
        }
    }

    //inflate the menu with the switch component to turn on the graphics mode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getItemId() == R.id.graphics_switch) {
                View view = MenuItemCompat.getActionView(item);
                if (view != null) {
                    switchGraphics = view.findViewById(R.id.switchComponent);
                    switchGraphics.setOnCheckedChangeListener(this);
                }
            }
        }
        return true;
    }

    //checks if the required lookup view is chosen
    private boolean areRequiredFieldsFilled() {
        String selectedLookupView = GlobalVariables.lookupView;
        if (selectedLookupView == null || selectedLookupView.equals("")) {
            Toast.makeText(this, "Please, choose a view!", Toast.LENGTH_SHORT).show();
            switchGraphics.setChecked(false);
            return false;
        }
        return true;
    }

    //on table button click
    public void onTableBtn(View view) {
        if (areRequiredFieldsFilled()) {
            //opens table page
            Intent intent = new Intent(LookupViewActivity.this, TableActivity.class);
            startActivity(intent);
        }
    }

    //on filters button click
    public void onFilterBtn(View view) {
        if (areRequiredFieldsFilled()) {
            //opens filter page
            Intent intent = new Intent(LookupViewActivity.this, FiltersActivity.class);
            startActivity(intent);
        }
    }
    //on attributes button click
    public void onAttributeBtn(View view) {
        if (areRequiredFieldsFilled()) {
            //opens attributes page
            Intent intent = new Intent(LookupViewActivity.this, AttributesActivity.class);
            startActivity(intent);
        }
    }

    //on switching to the graphics mode
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            GlobalVariables.graphicsComponentOn = true;
            //opens graphics attributes page
            if (areRequiredFieldsFilled()) {
                Intent intent = new Intent(LookupViewActivity.this, AttributesGraphicsActivity.class);
                startActivity(intent);
            }
        }
    }
}
