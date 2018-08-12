package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class AttributesGraphicsActivity extends AppCompatActivity implements
        OnItemSelectedListener {
    private Spinner s1, s2;
    private List<Attribute> attrsListItems;
    private ArrayAdapter<Attribute> adapter1;
    private ArrayAdapter<Attribute> adapter2;
    private Map<String, String> dataTypeCategories = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attributes_graphics);

        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());

        s1 = findViewById(R.id.spinner1);
        s2 = findViewById(R.id.spinner2);
        s1.setOnItemSelectedListener(this);
        s2.setOnItemSelectedListener(this);

        //categories - varchar, number, enum
        //string category
        dataTypeCategories.put("varchar", "string");
        dataTypeCategories.put("char", "string");
        dataTypeCategories.put("tinytext", "string");
        dataTypeCategories.put("text", "string");
        dataTypeCategories.put("mediumtext", "string");
        //very large text
        dataTypeCategories.put("longtext", "string");
        dataTypeCategories.put("binary", "string");
        dataTypeCategories.put("varbinary", "string");
        //enum category, for now it is in category string
        //if it is not a number it cannot be applied to teh barchart
        //because it needs float number
        dataTypeCategories.put("enum", "string");
        //numeric category
        dataTypeCategories.put("smallint", "number");
        dataTypeCategories.put("mediumint", "number");
        dataTypeCategories.put("bigint", "number");
        dataTypeCategories.put("int", "number");
        dataTypeCategories.put("double", "number");
        dataTypeCategories.put("decimal", "number");
        dataTypeCategories.put("tinyint", "number");
        // TODO add date to categories

        //attributes list items
        attrsListItems = new ArrayList<>();

        //if the view hasn't been changed
        if (((GlobalVariables) getApplication()).getAttributesList().size() > 0) {
            attrsListItems = ((GlobalVariables) getApplication()).getAttributesList();
        } else {
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
                    attrsListItems.add(new Attribute(name, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, attrsListItems);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(adapter1);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getId()){
            case R.id.spinner1:
                Attribute spinner1SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinner1)).getSelectedItem();

                String typeAttr1 = spinner1SelectedItem.getType();
                String categoryAttr1 = dataTypeCategories.get(typeAttr1);
                //st_firstSpinnerSelectedItem = firstList.get(position).getName();
                //st_firstSpinnerSelectedItemID = firstList.get(position).getFirstItemId();
                //setSecondSpinner(st_firstSpinnerSelectedItemID);
                initializeSpinner2(categoryAttr1);
                break;
            case R.id.spinner2:
                //st_secondSpinnerSelectedItem = secondList.get(position).getName();
                //st_secondSpinnerSelectedItemID = secondList.get(position).getId();
                break;
        }

    }

    public void initializeSpinner2(String categoryAttr1){
        List<Attribute> list = new ArrayList<>();
        for (int i = 0; i < attrsListItems.size(); i++) {
            String name = attrsListItems.get(i).getName();
            String type = attrsListItems.get(i).getType();
            String categoryAttr2 = dataTypeCategories.get(type);
            if (!categoryAttr1.equals(categoryAttr2)) {
                list.add(new Attribute(name, type));
            }
        }

        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2.notifyDataSetChanged();
        s2.setAdapter(adapter2);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    public String getSpinnerValue(int id) {
        Spinner spinner = findViewById(id);
        String value = spinner.getSelectedItem().toString();
        return value;
    }

    public void setGlobalGraphicsValues() {
        String graphicsXAxisAttr = getSpinnerValue(R.id.spinner1);
        String graphicsYAxisAttr = getSpinnerValue(R.id.spinner2);
        String graphicsType = getSpinnerValue(R.id.spinner3);

        Attribute spinner2SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinner2)).getSelectedItem();
        String typeAttr2 = spinner2SelectedItem.getType();
        String categoryAttr2 = dataTypeCategories.get(typeAttr2);

        Attribute spinner1SelectedItem = (Attribute) ((Spinner) findViewById(R.id.spinner1)).getSelectedItem();
        String typeAttr1 = spinner1SelectedItem.getType();
        String categoryAttr1 = dataTypeCategories.get(typeAttr1);

        ((GlobalVariables) getApplication()).setGraphicsXAxisAttr(graphicsXAxisAttr);
        ((GlobalVariables) getApplication()).setGraphicsYAxisAttr(graphicsYAxisAttr);
        ((GlobalVariables) getApplication()).setGraphicsXAxisAttrType(categoryAttr1);
        ((GlobalVariables) getApplication()).setGraphicsYAxisAttrType(categoryAttr2);
        ((GlobalVariables) getApplication()).setGraphicsType(graphicsType);
    }

    public void onFilterBtn(View view) {
        setGlobalGraphicsValues();
        //opens filter page
        Intent intent = new Intent(AttributesGraphicsActivity.this, FiltersActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        setGlobalGraphicsValues();
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }

    public void onGraphicsBtn(View view) {
        setGlobalGraphicsValues();
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, GraphicsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        ((GlobalVariables) getApplication()).setGraphicsBtnOn(false);
        //opens attributes page
        Intent intent = new Intent(AttributesGraphicsActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }
}
