package zs30.interactivequeryvisualizer;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class TableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        TableLayout tl = (TableLayout) findViewById(R.id.table);
        int count = 0;

        //headers values
        String[] headers = new String[6];
        headers[0] = "Header 1";
        headers[1] = "Header 2";
        headers[2] = "Header 3";
        headers[3] = "Header 4";
        headers[4] = "Header 5";
        headers[5] = "Header 6";

        TableRow trHeaders = new TableRow(this);
        trHeaders.setBackgroundColor(Color.parseColor("#1f3d7a"));
        trHeaders.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        for (String header : headers) {
            TextView cell11 = new TextView(this);

            cell11.setTextSize(16);
            cell11.setTypeface(null, Typeface.BOLD);
            cell11.setText(header);
            cell11.setPadding(5, 0, 15, 5);
            cell11.setTextColor(Color.WHITE);
            trHeaders.addView(cell11);
        }

        tl.addView(trHeaders, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        //attribute values
        for (int i = 0; i < 66; i++) {
            String[] attrs = new String[6];
            attrs[0] = "10/25/22";
            attrs[1] = "45456.00";
            attrs[2] = "value";
            attrs[3] = "some long text here";
            attrs[4] = "some very long text here and now back then very long";
            attrs[5] = "some very long text here and now back then very long";

            TableRow tr = new TableRow(this);

            if (count % 2 != 0) {
                tr.setBackgroundColor(Color.WHITE);
            } else {
                tr.setBackgroundColor(Color.parseColor("#ebf0fa"));
            }

            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));


            for (String attr : attrs) {
                TextView cell11 = new TextView(this);

                cell11.setText(attr);
                cell11.setPadding(5, 0, 15, 5);
                tr.addView(cell11);
            }

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            count++;
        }
    }
}
