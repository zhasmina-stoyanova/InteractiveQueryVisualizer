package zs30.interactivequeryvisualizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;

public class MainActivity extends AppCompatActivity {

    private ListView list_view;
    ArrayAdapter<String> adapter;
    EditText search_text;

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_view = (ListView) findViewById(R.id.list_view);
        search_text = (EditText) findViewById(R.id.search_text);

        // Example data
        String car_brands[] = {"Ford", "Chevrolet", "Kia Motors", "Mazda For", "Tesla", "Infinity Fe", "Lancia", "Mitsubishi", "Peugeot", "Jaguar"};

        // adding values to list_view
        adapter = new ArrayAdapter<String>(this, R.layout.list_view_item, R.id.product_name, car_brands);
        list_view.setAdapter(adapter);

        //set rule when the search text is on focus and when not
        search_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                search_text.setText(selectedItem);
            }
        });

        //search text
        search_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // on text changed
                MainActivity.this.adapter.getFilter().filter(cs);
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
    }


}
