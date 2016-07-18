package ar.com.bestprice.buyitnow;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import ar.com.bestprice.buyitnow.dto.Item;

/**
 * Created by ivan on 17/07/16.
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter, ExpandableListAdapter {


    private final Map<Integer, PurchasesGroup> groups;
    public LayoutInflater inflater;
    public Activity activity;
    private final OnStartDragListener mDragStartListener = null;


    public RecyclerListAdapter(Activity act, Map<Integer, PurchasesGroup> groups) {

        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                //Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                //Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        //mItems.remove(position);
        notifyItemRemoved(position);
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

        itemDescription.setOnClickListener(new View.OnClickListener() {

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
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public long getCombinedChildId(long groupId, long childId) {
        return 0;
    }

    @Override
    public long getCombinedGroupId(long groupId) {
        return 0;
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
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {

    }

    public Object removeChild(int groupPosition, int childPosition) {

        return groups.get(groupPosition).children.remove(childPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        final ItemViewHolder itemView = (ItemViewHolder)holder;
        itemView.textView.setText(this.groups.get(position).getString());

            // Start a drag whenever the handle view it touched
        itemView.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    //mDragStartListener.onStartDrag(itemView);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final ImageView handleView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.text);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
