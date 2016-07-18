package ar.com.bestprice.buyitnow;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import ar.com.bestprice.buyitnow.dto.Item;


public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private final Map<Integer, PurchasesGroup> groups;
    public LayoutInflater inflater;
    public Activity activity;


    public MyExpandableListAdapter(Activity act, Map<Integer, PurchasesGroup> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Item children = (Item) getChild(groupPosition, childPosition);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }

        TextView itemDescription = (TextView) convertView.findViewById(R.id.listrow_item_description);
        itemDescription.setText(children.getDescription());

        int icon = Category.MERCADERIA.getIcon();
        if (children.getCategory() != null) {
                icon = children.getCategory().getIcon();
        }

        itemDescription.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);

        itemDescription.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Category:" + children.getCategory().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        itemDescription.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeChild(groupPosition, childPosition);
                return false;
            }
        });

        TextView itemPrice = (TextView) convertView.findViewById(R.id.item_price);
        itemPrice.setText(String.format("$%.2f", children.getPrice()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }

        RelativeLayout relativeLayout = (RelativeLayout) ((LinearLayout)convertView).getChildAt(0);

        CheckedTextView checkedTextView = (CheckedTextView) relativeLayout.getChildAt(0);
        TextView amountPerMonth = (TextView) relativeLayout.getChildAt(1);
        TextView differencePerMonth = (TextView) relativeLayout.getChildAt(3);

        PurchasesGroup purchasesByMonth = (PurchasesGroup) getGroup(groupPosition);
        PurchasesGroup previousPurchasesByMonth = (PurchasesGroup) getGroup( ( (groupPosition > 0) ? (groupPosition - 1) : 0) );

        if (purchasesByMonth.getPurchasesTotalPrice() > previousPurchasesByMonth.getPurchasesTotalPrice()) {
            differencePerMonth.setBackgroundColor(Color.rgb(139,00,00)); //RED
        } else {
            differencePerMonth.setBackgroundColor(Color.rgb(34,139,34)); //GREEN
        }

        checkedTextView.setText(purchasesByMonth.getString());


        float diff = purchasesByMonth.getPurchasesTotalPrice() - previousPurchasesByMonth.getPurchasesTotalPrice();

        diff = (diff * 100) / previousPurchasesByMonth.getPurchasesTotalPrice();

        differencePerMonth.setText(String.format("%+.2f%%",diff));
        amountPerMonth.setText(String.format("$%.2f",purchasesByMonth.getPurchasesTotalPrice()));

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public Object removeChild(int groupPosition, int childPosition) {

        return groups.get(groupPosition).children.remove(childPosition);
    }
}
