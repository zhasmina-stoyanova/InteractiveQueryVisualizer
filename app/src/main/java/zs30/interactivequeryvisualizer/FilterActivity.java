package zs30.interactivequeryvisualizer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
    }

    public void onTableBtn(View view) {
        //opens table page
        Intent intent = new Intent(FilterActivity.this, TableActivity.class);
        startActivity(intent);
    }

    public void onViewsBtn(View view) {
        //opens filter page
        Intent intent = new Intent(FilterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onAttributeBtn(View view) {
        //opens attributes page
        Intent intent = new Intent(FilterActivity.this, AttributesActivity.class);
        startActivity(intent);
    }
}

