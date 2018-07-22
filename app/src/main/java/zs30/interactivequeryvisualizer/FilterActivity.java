package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private List<AttributesListItem> attrsListItems;
    private Map<String, String> whereClauseParams = new HashMap<>();
    private List<EditText> stringsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //attributes list items
        attrsListItems = new ArrayList<>();

        //if the view hasn't been changed
        if (((GlobalVariables) getApplication()).getAttrsListItems().size() > 0) {
            attrsListItems = ((GlobalVariables) getApplication()).getAttrsListItems();
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
                    attrsListItems.add(new AttributesListItem(name, type));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //initialize where clause param map
        if (((GlobalVariables) getApplication()).getWhereClauseParams().size() > 0) {
            whereClauseParams = ((GlobalVariables) getApplication()).getWhereClauseParams();
        }

        //the 4 types of layouts
        LinearLayout layoutBoolean = findViewById(R.id.filter_string);

        for (int i = 0; i < attrsListItems.size(); i++) {
            String attrType = attrsListItems.get(i).getType();
            final String attrName = attrsListItems.get(i).getAttributeName();
            String lookupView = ((GlobalVariables) getApplication()).getLookupView();
            String url = "";
            if (attrType.equalsIgnoreCase("varchar")) {
                LinearLayout parent = new LinearLayout(this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);

                //attr name
                TextView attributeNameTextView = new TextView(this);
                attributeNameTextView.setText(attrName + ":");
                attributeNameTextView.setPadding(0, 0, 10, 0);
                attributeNameTextView.setTypeface(null, Typeface.BOLD);
                parent.addView(attributeNameTextView);

                final EditText attrValueEditText = new EditText(this);
                attrValueEditText.setTag(attrName);
                attrValueEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //if the map has values, gets those whose attributes has where clause values
                //and assigns it to the edit text field
                //if the attribute is not in the map, it does not have where clause value
                if(!whereClauseParams.isEmpty() && whereClauseParams.get(attrName) != null){
                    String whereClauseParamsValue = whereClauseParams.get(attrName);
                    String whereClauseParamsValueWithoutQuotations = whereClauseParamsValue.substring(1, whereClauseParamsValue.length()-1);
                    attrValueEditText.setText(whereClauseParamsValueWithoutQuotations);
                }
                parent.addView(attrValueEditText);
                stringsList.add(attrValueEditText);

                //on change edit text value
                attrValueEditText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        whereClauseParams.put(attrName, attrValueEditText.getText().toString());
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });

                //add the linear layout with view to teh boolean liner layout
                layoutBoolean.addView(parent);
            }
        }

        //spinner sort by
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSortByAttribute);
        spinner.setOnItemSelectedListener(this);
        //values
        List<String> selectedAttributes = new ArrayList<String>();

        for (int i = 0; i < attrsListItems.size(); i++) {
            if (attrsListItems.get(i).isAttributeChecked()) {
                selectedAttributes.add(attrsListItems.get(i).getAttributeName());
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectedAttributes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        //spinner order
        Spinner spinner2 = (Spinner) findViewById(R.id.spinnerOrder);
        spinner2.setOnItemSelectedListener(this);
        //values
        List<String> spinnerOrder = new ArrayList<String>();
        spinnerOrder.add("asc");
        spinnerOrder.add("desc");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerOrder);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter2);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spinnerSortByAttribute) {
            String item = parent.getItemAtPosition(position).toString();
            ((GlobalVariables) getApplication()).setSortByAttribute(item);
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        } else if (spinner.getId() == R.id.spinnerOrder) {
            String item = parent.getItemAtPosition(position).toString();
            ((GlobalVariables) getApplication()).setOrder(item);
            //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void onTableBtn(View view) {
        for(int i = 0; i < stringsList.size(); i++){
            //single quotation marks for the string values when querying the database
            String attr = stringsList.get(i).getTag().toString();
            String value = stringsList.get(i).getText().toString();
            if(!value.equals("")) {
                whereClauseParams.put(attr,"'" + value + "'");
            }
        }
        ((GlobalVariables) getApplication()).setWhereClauseParams(whereClauseParams);
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);
        //opens table page
        Intent intent = new Intent(FilterActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);
        //opens filter page
        Intent intent = new Intent(FilterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onAttributeBtn(View view) {
        ((GlobalVariables) getApplication()).setAttrsListItems(attrsListItems);
        //opens attributes page
        Intent intent = new Intent(FilterActivity.this, AttributesActivity.class);
        startActivity(intent);
    }
}

