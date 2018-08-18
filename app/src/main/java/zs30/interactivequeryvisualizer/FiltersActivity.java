package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FiltersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private List<Attribute> attrsListItems;
    private Map<String, String> whereClauseParams = new HashMap<>();
    private List<EditText> stringsList = new ArrayList<>();
    private List<RadioGroup> booleansList = new ArrayList<>();
    private List<RangeSeekBar> numberRangeList = new ArrayList<>();
    private Map<String, String> dataTypeCategories = new HashMap<>();
    private Button buttonAtrrsGraphics;
    private Button buttonGraphics;
    private Button buttonAttrsTable;
    private Button buttonTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

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

        //datatype categories
        //single values categories
        dataTypeCategories.put("varchar", "single");
        dataTypeCategories.put("char", "single");
        dataTypeCategories.put("tinytext", "single");
        dataTypeCategories.put("text", "single");
        dataTypeCategories.put("mediumtext", "single");
        //very large text
        dataTypeCategories.put("longtext", "single");
        dataTypeCategories.put("binary", "single");
        dataTypeCategories.put("varbinary", "single");
        //must be different category
        dataTypeCategories.put("enum", "single");
        dataTypeCategories.put("smallint", "single");
        dataTypeCategories.put("mediumint", "single");
        dataTypeCategories.put("bigint", "single");
        dataTypeCategories.put("int", "single");
        //numeric range
        dataTypeCategories.put("double", "numeric_range");
        dataTypeCategories.put("decimal", "numeric_range");
        // TODO add date to categories

        getSupportActionBar().setSubtitle(((GlobalVariables) getApplication()).getLookupView());

        //attributes list items
        attrsListItems = new ArrayList<>();

        //if the view hasn't been changed
        if (((GlobalVariables) getApplication()).getAttributesList().size() > 0) {
            attrsListItems = ((GlobalVariables) getApplication()).getAttributesList();
        } else {
            String lookupView = ((GlobalVariables) getApplication()).getLookupView();
            String urlToFormat = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/attributes";

            String url = Utils.replaceSpecialSymbolsUrl(urlToFormat);
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

        //initialize where clause param map
        if (((GlobalVariables) getApplication()).getWhereClauseParams().size() > 0) {
            whereClauseParams = ((GlobalVariables) getApplication()).getWhereClauseParams();
        }

        //the 4 types of layouts
        LinearLayout layoutString = findViewById(R.id.filter_string);
        LinearLayout layoutBoolean = findViewById(R.id.filter_boolean);
        LinearLayout layoutNumber = findViewById(R.id.filter_number);

        for (int i = 0; i < attrsListItems.size(); i++) {
            String attrType = attrsListItems.get(i).getType();
            final String attrName = attrsListItems.get(i).getName();
            String lookupView = ((GlobalVariables) getApplication()).getLookupView();
            String url = "";

            String category = dataTypeCategories.get(attrType) != null ? dataTypeCategories.get(attrType) : attrType;

            if (category.equalsIgnoreCase("single")) {
                TextView panelLabel = findViewById(R.id.text_string);
                panelLabel.setVisibility(View.VISIBLE);
                LinearLayout parent = new LinearLayout(this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);
                parent.setPadding(16, 16, 16, 0);

                //interactive hint for edit text
                TextInputLayout titleWrapper = new TextInputLayout(this);
                titleWrapper.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                titleWrapper.setHint(attrName);
                parent.addView(titleWrapper);

                final EditText attrValueEditText = new EditText(this);
                attrValueEditText.setTag(attrName);
                attrValueEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                //if the map has values, gets those whose attributes that have where clause values
                //and assigns it to the edit text field
                //if the attribute is not in the map, it does not have where clause value
                if (!whereClauseParams.isEmpty() && whereClauseParams.get(attrName) != null) {
                    String whereClauseParamsValue = whereClauseParams.get(attrName);
                    String whereClauseParamsValueWithoutQuotations = whereClauseParamsValue.substring(1, whereClauseParamsValue.length() - 1);
                    attrValueEditText.setText(whereClauseParamsValueWithoutQuotations);
                }
                titleWrapper.addView(attrValueEditText);
                stringsList.add(attrValueEditText);

                //on change edit text value
                attrValueEditText.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        whereClauseParams.put(attrName, attrValueEditText.getText().toString());
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

                //add the linear layout with view to the string liner layout
                layoutString.addView(parent);
            }
            if (attrType.equalsIgnoreCase("tinyint")) {
                TextView panelLabel = findViewById(R.id.text_boolean);
                panelLabel.setVisibility(View.VISIBLE);
                //min and max values for the attribute
                String min = "";
                String max = "";
                url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/extremes?attribute=" + attrName;
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
                    JSONObject jsonobject = new JSONObject(response);
                    min = jsonobject.getString("min");
                    max = jsonobject.getString("max");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayout parent = new LinearLayout(this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.HORIZONTAL);
                parent.setPadding(16, 16, 16, 0);

                //initialize radio group
                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.HORIZONTAL);
                rg.setTag(attrName);

                //attr name
                TextView attributeNameTextView = new TextView(this);
                attributeNameTextView.setText(attrName + ":");
                attributeNameTextView.setPadding(0, 0, 10, 0);
                attributeNameTextView.setTypeface(null, Typeface.BOLD);
                rg.addView(attributeNameTextView);

                //radio button for all values
                RadioButton rb_all = new RadioButton(this);
                rb_all.setTag("all");
                rb_all.setText("All");

                rg.addView(rb_all);

                //radio button that contains the min value of the attribute
                RadioButton rb_min = new RadioButton(this);
                rb_min.setText(min);

                rg.addView(rb_min);

                //radio button that contains the min value of the attribute
                RadioButton rb_max = new RadioButton(this);
                rb_max.setText(max);

                rg.addView(rb_max);

                //if the map has values, gets those whose attributes that hava where clause values
                //and assigns it to the radio buttons field
                //if the attribute is not in the map, it does not have where clause value set the
                if (!whereClauseParams.isEmpty() && whereClauseParams.get(attrName) != null) {
                    String whereClauseParamsValue = whereClauseParams.get(attrName);

                    //check which is teh selected value
                    if (whereClauseParamsValue.equals(min)) {
                        rb_min.setChecked(true);
                    } else {
                        rb_max.setChecked(true);
                    }
                } else {
                    rb_all.setChecked(true);
                }

                parent.addView(rg);

                booleansList.add(rg);

                //add the linear layout with view to the boolean liner layout
                layoutBoolean.addView(parent);

            } else if (category.equalsIgnoreCase("numeric_range")) {
                TextView panelLabel = findViewById(R.id.text_number);
                panelLabel.setVisibility(View.VISIBLE);
                String min = "";
                String max = "";
                url = "http://" + GlobalVariables.IP_MOBILE_DEVICE + ":8080/InteractiveQueryVisualizerWS/webapi/lookupviews/" + lookupView + "/extremes?attribute=" + attrName;
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
                    JSONObject jsonobject = new JSONObject(response);
                    min = jsonobject.getString("min");
                    max = jsonobject.getString("max");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LinearLayout parent = new LinearLayout(this);
                parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                parent.setOrientation(LinearLayout.VERTICAL);
                parent.setPadding(16, 16, 16, 0);
                //parent.setBackgroundColor(Color.parseColor("#EAEAEA"));

                //attr name
                TextView attributeNameTextView = new TextView(this);
                attributeNameTextView.setText(attrName + ":");
                attributeNameTextView.setPadding(0, 0, 10, 0);
                attributeNameTextView.setTypeface(null, Typeface.BOLD);

                double minDouble = Double.parseDouble(min);
                double maxDouble = Double.parseDouble(max);

                final RangeSeekBar seekBar = new RangeSeekBar(this);
                seekBar.setRangeValues(minDouble, maxDouble);
                seekBar.setTextAboveThumbsColor(Color.parseColor("#000000"));
                seekBar.setTag(attrName);

                //table layout for min max labels and values
                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                TableLayout tableLayout = new TableLayout(this);

                TableRow tableRow = new TableRow(this);
                LinearLayout.LayoutParams tableRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                tableRow.setLayoutParams(tableRowParams);

                //min text
                TextView minLabel = new TextView(this);
                minLabel.setText("min");
                minLabel.setPadding(0, 0, 10, 0);
                minLabel.setTypeface(null, Typeface.BOLD);
                ;
                minLabel.setLayoutParams(textViewParam);
                minLabel.setGravity(Gravity.LEFT);

                tableRow.addView(minLabel);

                //max text
                TextView maxLabel = new TextView(this);
                maxLabel.setText("max");
                maxLabel.setPadding(0, 0, 10, 0);
                maxLabel.setTypeface(null, Typeface.BOLD);
                maxLabel.setGravity(Gravity.RIGHT);
                maxLabel.setLayoutParams(textViewParam);

                tableRow.addView(maxLabel);

                tableLayout.addView(tableRow);

                TableRow tableRow2 = new TableRow(this);
                tableRow2.setLayoutParams(tableRowParams);

                //min value
                final TextView minValue = new TextView(this);
                minValue.setText(min);
                minValue.setLayoutParams(textViewParam);
                minValue.setGravity(Gravity.LEFT);

                tableRow2.addView(minValue);

                //max value
                final TextView maxValue = new TextView(this);
                maxValue.setText(max);
                maxValue.setGravity(Gravity.RIGHT);
                maxValue.setLayoutParams(textViewParam);

                tableRow2.addView(maxValue);

                tableLayout.addView(tableRow2);

                //add attributes to parent
                parent.addView(attributeNameTextView);
                parent.addView(seekBar);
                parent.addView(tableLayout);

                layoutNumber.addView(parent);

                //if the map has values, gets those whose attributes that have where clause values
                //and assigns it to the min and max values in range number seekbar
                //if the attribute is not in the map, it does not have where clause value set
                if (!whereClauseParams.isEmpty() && whereClauseParams.get(attrName) != null) {
                    String whereClauseParamsValue = whereClauseParams.get(attrName);
                    //the values of min and max are concatenated by ;
                    String[] parts = whereClauseParamsValue.split(";");
                    String minValueWhereClause = parts[0];
                    String maxValueWhereClause = parts[1];
                    minValue.setText(minValueWhereClause);
                    maxValue.setText(maxValueWhereClause);
                    seekBar.setSelected(true);
                    seekBar.setSelectedMinValue(Double.parseDouble(minValueWhereClause));
                    seekBar.setSelectedMaxValue(Double.parseDouble(maxValueWhereClause));
                }

                //on slide seekbar
                seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Double>() {

                    @Override
                    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Double minValueSeekBar, Double maxValueSeekBar) {
                        minValue.setText("" + minValueSeekBar);
                        maxValue.setText("" + maxValueSeekBar);
                    }
                });

                numberRangeList.add(seekBar);
            }
        }

        //spinner sort by
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSortByAttribute);
        spinner.setOnItemSelectedListener(this);
        //values
        List<String> selectedAttributes = new ArrayList<String>();

        for (int i = 0; i < attrsListItems.size(); i++) {
            if (attrsListItems.get(i).isSelected()) {
                selectedAttributes.add(attrsListItems.get(i).getName());
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
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
    }

    public void onTableBtn(View view) {
        setGlobalWhereClauseParamsAndAttributesList();
        //opens table page
        Intent intent = new Intent(FiltersActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        setGlobalWhereClauseParamsAndAttributesList();
        //opens filter page
        Intent intent = new Intent(FiltersActivity.this, LookupViewActivity.class);
        startActivity(intent);
    }

    public void onAttributeBtn(View view) {
        setGlobalWhereClauseParamsAndAttributesList();
        //opens attributes page
        Intent intent = new Intent(FiltersActivity.this, AttributesActivity.class);
        startActivity(intent);
    }

    public void onAttributeGraphicsBtn(View view) {
        setGlobalWhereClauseParamsAndAttributesList();
        //opens graphics attributes page
        Intent intent = new Intent(FiltersActivity.this, AttributesGraphicsActivity.class);
        startActivity(intent);
    }

    public void onGraphicsBtn(View view) {
        setGlobalWhereClauseParamsAndAttributesList();
        //opens graphics page
        Intent intent = new Intent(FiltersActivity.this, GraphicsActivity.class);
        startActivity(intent);
    }

    public void setGlobalWhereClauseParamsAndAttributesList() {
        //get values for strings
        for (int i = 0; i < stringsList.size(); i++) {
            //single quotation marks for the string values when querying the database
            String attr = stringsList.get(i).getTag().toString();
            String value = stringsList.get(i).getText().toString();
            if (!value.equals("")) {
                whereClauseParams.put(attr, "'" + value + "'");
            } else {
                //if we have attribute and the value is set after the first time to ""
                whereClauseParams.remove(attr);
            }
        }

        //get values for booleans
        for (int i = 0; i < booleansList.size(); i++) {
            String attr = booleansList.get(i).getTag().toString();
            int checkedRadioBtnId = booleansList.get(i).getCheckedRadioButtonId();

            RadioButton selectedRadioButton = (RadioButton) findViewById(checkedRadioBtnId);
            String value = selectedRadioButton.getText().toString();

            if (!value.equals("") && !value.equals("All")) {
                whereClauseParams.put(attr, value);
            }
        }

        //get values for number range
        for (int i = 0; i < numberRangeList.size(); i++) {
            String attr = numberRangeList.get(i).getTag().toString();
            Number minValue = numberRangeList.get(i).getSelectedMinValue();
            Number maxValue = numberRangeList.get(i).getSelectedMaxValue();

            //concatenate min and max values with ;
            String value = minValue + ";" + maxValue;

            if (!value.equals("")) {
                whereClauseParams.put(attr, value);
            }
        }

        ((GlobalVariables) getApplication()).setWhereClauseParams(whereClauseParams);
        ((GlobalVariables) getApplication()).setAttributesList(attrsListItems);
    }
}

