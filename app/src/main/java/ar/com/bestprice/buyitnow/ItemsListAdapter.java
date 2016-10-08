package ar.com.bestprice.buyitnow;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import ar.com.bestprice.buyitnow.dto.Item;

/**
 * Created by ivan on 19/09/16.
 */
public class ItemsListAdapter extends ArrayAdapter<Item>{

    Activity activity = null;

    public ItemsListAdapter(android.content.Context context, List<Item> items, Activity activity) {
        super(context, 0, items);
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);

        View view = activity.getLayoutInflater().inflate(R.layout.listrow_details, null);

        int icon = Category.MERCADERIA.getIcon();
        if (item.getCategory() != null) {
            icon = item.getCategory().getIcon();
        }
        TextView text = (TextView) view.findViewById(R.id.listrow_item_description);


        text.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
        TextView price = (TextView) view.findViewById(R.id.item_price);

        price.setText(String.format("$%.2f", item.getPrice()));


        text.setText(item.getDescription());
        return view;
    }

}
