package ar.com.bestprice.buyitnow;

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

    public ItemsListAdapter(android.content.Context context, List<Item> items) {
        super(context, 0, items);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Item item = getItem(position);
        return null;
    }

}
