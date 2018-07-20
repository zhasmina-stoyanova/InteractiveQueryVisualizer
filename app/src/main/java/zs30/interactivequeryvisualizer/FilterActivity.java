package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private List<AttributesListItem> attrsListItems;
    private RangeSeekBar seekBarInteger, seekBarDouble;
    private TextView minTextInt, maxtextInt, minTextDouble, maxTextDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        //seekbar for integers and double values
        seekBarInteger = findViewById(R.id.seekbar);
        minTextInt = findViewById(R.id.seekValuemin);
        maxtextInt = findViewById(R.id.seekValuemax);
        seekBarDouble = findViewById(R.id.seekbarDouble);
        minTextDouble = findViewById(R.id.seekValueminDouble);
        maxTextDouble = findViewById(R.id.seekValuemaxDouble);

        seekBarInteger.setRangeValues(0, 30);
        seekBarInteger.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                minTextInt.setText("Min Value " + minValue);
                maxtextInt.setText("Max value " + maxValue);
            }
        });

        seekBarDouble.setRangeValues(0.0, 100.0);
        seekBarDouble.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValue, Double maxValue) {
                minTextDouble.setText("Min Value " + minValue);
                maxTextDouble.setText("Max value " + maxValue);
            }
        });

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

        //spinner selected attributes
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

        //spinner selected attributes
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

