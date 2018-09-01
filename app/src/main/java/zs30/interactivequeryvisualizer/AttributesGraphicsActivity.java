package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AttributesGraphicsActivity extends AppCompatActivity implements
        OnItemSelectedListener {
    private Spinner s2;
    private List<Attribute> attrsListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets the layout resource
        setContentView(R.layout.activity_attributes_graphics);

        setActionBarSubtitle();

        Spinner s1 = findViewById(R.id.spinnerAttribute1);
        s2 = findViewById(R.id.spinnerAttribute2);
        s1.setOnItemSelectedListener(this);
        s2.setOnItemSelectedListener(this);


        // TODO add date to categories

        //attributes list items
        attrsListItems = new ArrayList<>();

        //if the view hasn't been changed
        if (GlobalVariables.attributesList.size() > 0) {
            attrsListItems = GlobalVariables.attributesList;
        } else {
            String lookupView = GlobalVariables.lookupView;
            String urlToFormat = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";

            String url = Utils.replaceSpecialSymbols(urlToFormat);
            String response = Utils.getResponse(url);

            try {
                JSONArray jsonarray = new JSONArray(response);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String name = jsonobject.getString("name");
                    String type = jsonobject.getString("type");
                    attrsListItems.add(new Attribute(name, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<Attribute> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, attrsListItems);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);
    }

    //sets subtitle for the action bar
    private void setActionBarSubtitle() {
        getSupportActionBar().setSubtitle(GlobalVariables.lookupView);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()){
            case R.id.spinnerAttribute1:
                Attribute spinner1SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinnerAttribute1)).getSelectedItem();
                String typeAttr1 = spinner1SelectedItem.getType();
                String categoryAttr1 = Utils.dataTypeCategories.get(typeAttr1);
                initializeSpinner2(categoryAttr1);
                break;
            case R.id.spinnerAttribute2:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void initializeSpinner2(String categoryAttr1){
        List<Attribute> list = new ArrayList<>();
        for (int i = 0; i < attrsListItems.size(); i++) {
            String name = attrsListItems.get(i).getName();
            String type = attrsListItems.get(i).getType();
            String categoryAttr2 = Utils.dataTypeCategories.get(type);
            if (!categoryAttr1.equals(categoryAttr2)) {
                list.add(new Attribute(name, type));
            }
        }

        ArrayAdapter<Attribute> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2.notifyDataSetChanged();
        s2.setAdapter(adapter2);
    }

    //get the value of given spinner by id
    private String getSpinnerValue(int id) {
        Spinner spinner = findViewById(id);
        return spinner.getSelectedItem().toString();
    }

    //on filters button click
    public void onFilterBtn(View view) {
        setGlobalParametsers();
        //opens filter page
        Intent intent = new Intent(AttributesGraphicsActivity.this, FiltersActivity.class);
        startActivity(intent);
    }

    //on views button click
    public void onViewsBtn(View view) {
        setGlobalParametsers();
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }

    //on graphics button click
    public void onGraphicsBtn(View view) {
        setGlobalParametsers();
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, GraphicsActivity.class);
        startActivity(intent);
    }

    //on back button pressed
    @Override
    public void onBackPressed() {
        //sets the default graphics button value - off
        GlobalVariables.graphicsComponentOn = false;
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }

    private void setGlobalParametsers() {
        //get values of attribute1, attribute2 and graphics type spinners
        String graphicsAttribute1 = getSpinnerValue(R.id.spinnerAttribute1);
        String graphicsAttribute2 = getSpinnerValue(R.id.spinnerAttribute2);
        String graphicsType = getSpinnerValue(R.id.spinnerGraphicsType);

        Attribute spinner1SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinnerAttribute1)).getSelectedItem();
        String typeAttr1 = spinner1SelectedItem.getType();
        String graphicsAttribute1Type = Utils.dataTypeCategories.get(typeAttr1);

        Attribute spinner2SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinnerAttribute2)).getSelectedItem();
        String typeAttr2 = spinner2SelectedItem.getType();
        String graphicsAttribute2Type = Utils.dataTypeCategories.get(typeAttr2);

        //set global parameters
        GlobalVariables.graphicsAttribute1 = graphicsAttribute1;
        GlobalVariables.graphicsAttribute2 = graphicsAttribute2;
        GlobalVariables.graphicsAttribute1Type = graphicsAttribute1Type;
        GlobalVariables.graphicsAttribute2Type = graphicsAttribute2Type;
        GlobalVariables.graphicsType = graphicsType;
    }
}
