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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable {
    boolean hasDuplicateData = false;
    private ListView list_view;
    ArrayAdapter<String> adapter;
    EditText search_text;
    final List<String> car_brands = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(this);
        thread.start();

        list_view = (ListView) findViewById(R.id.list_view);
        search_text = (EditText) findViewById(R.id.search_text);

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

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            adapter = new ArrayAdapter<String>(list_view.getContext(), R.layout.list_view_item, R.id.product_name, car_brands);
            list_view.setAdapter(adapter);
        }
    };

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void run() {
        System.out.println("Select Records Example by using the Prepared Statement!");
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection
                    ("jdbc:mysql://zs30.host.cs.st-andrews.ac.uk:3306/zs30_query_visualizer", "zs30", "F14VMP7v.d47wg");
            try {
                String sql = "SELECT name from lookup_views";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    car_brands.add(rs.getString(1));
                }
                ps.close();
                con.close();
            } catch (SQLException s) {
                s.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.sendEmptyMessage(0);
    }
}
