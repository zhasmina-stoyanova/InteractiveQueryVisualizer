package zs30.interactivequeryvisualizer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AttributesActivity extends AppCompatActivity {
    private List<AttributesListItem> attrsListItems;
    private AttributesListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribute);
        ListView attrsListView = findViewById(R.id.attrs_list_view);
        attrsListItems = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            attrsListItems.add(new AttributesListItem("Attribute " + i));
        }

        adapter = new AttributesListViewAdapter(attrsListItems, getApplicationContext());
        attrsListView.setAdapter(adapter);

        attrsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttributesListItem currAttrsItem = attrsListItems.get(position);
                currAttrsItem.setAttributeChecked(!currAttrsItem.isAttributeChecked());
                adapter.notifyDataSetChanged();
            }
        });
    }
}