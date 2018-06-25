package zs30.interactivequeryvisualizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class AttributesListViewAdapter extends ArrayAdapter<AttributesListItem> {

    private List<AttributesListItem> attributesList;

    static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }

    public AttributesListViewAdapter(List<AttributesListItem> attributesList, Context context) {
        super(context, R.layout.list_view_checkbox, attributesList);
        this.attributesList = attributesList;
    }

    @Override
    public int getCount() {
        return attributesList.size();
    }

    @Override
    public AttributesListItem getItem(int position) {
        return attributesList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View resultView, @NonNull ViewGroup parent) {
        ViewHolder listViewHolder;
        if (resultView == null) {
            listViewHolder = new ViewHolder();
            resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_checkbox, parent, false);

            listViewHolder.textView = resultView.findViewById(R.id.textView);
            listViewHolder.checkBox = resultView.findViewById(R.id.checkBox);

            resultView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) resultView.getTag();
        }

        listViewHolder.textView.setText(attributesList.get(position).getAttributeName());
        listViewHolder.checkBox.setChecked(attributesList.get(position).isAttributeChecked());

        return resultView;
    }
}