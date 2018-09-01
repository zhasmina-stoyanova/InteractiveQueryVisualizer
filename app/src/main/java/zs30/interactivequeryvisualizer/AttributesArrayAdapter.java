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

/**
 * Custom adapter representing the name
 * and the checkbox of each attribute.
 *
 * @author Zhasmina Stoyanova
 * @version 1.0 August 2018
 */
public class AttributesArrayAdapter extends ArrayAdapter<Attribute> {

    private final List<Attribute> attributesList;

    /**
     * Initializes a new instance of the custom array adapter class
     * by passing the list of attributes, the layout used
     * for them, and the application context.
     */
    public AttributesArrayAdapter(List<Attribute> attributesList, Context context) {
        super(context, R.layout.list_view_with_checkbox, attributesList);
        this.attributesList = attributesList;
    }

    //returns the size of the attributes list
    @Override
    public int getCount() {
        return attributesList.size();
    }

    //get item from the attributes list by its position
    @Override
    public Attribute getItem(int position) {
        return attributesList.get(position);
    }

    /**
     * populates the ViewHolder and stores it inside the tag field of the Layout
     * for immediate access
     */
    @NonNull
    @Override
    public View getView(int position, View resultView, @NonNull ViewGroup parent) {
        ViewHolder listViewHolder;

        //when the resultView is not null only updating its contents, otherwise inflating a new row layout.
        if (resultView == null) {
            //inflate the layout
            resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_with_checkbox, parent, false);

            //creates new instance of the ViewHolder class
            listViewHolder = new ViewHolder();
            listViewHolder.textView = resultView.findViewById(R.id.textView);
            listViewHolder.checkBox = resultView.findViewById(R.id.checkBox);

            resultView.setTag(listViewHolder);
        } else {
            listViewHolder = (ViewHolder) resultView.getTag();
        }

        //sets the attributes of the ViewHolder instance
        listViewHolder.textView.setText(attributesList.get(position).getName());
        listViewHolder.checkBox.setChecked(attributesList.get(position).isSelected());

        return resultView;
    }

    static class ViewHolder {
        TextView textView;
        CheckBox checkBox;
    }
}