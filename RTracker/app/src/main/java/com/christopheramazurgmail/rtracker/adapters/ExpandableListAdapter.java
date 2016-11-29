package com.christopheramazurgmail.rtracker.adapters;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Set;

        import com.christopheramazurgmail.rtracker.Item;
        import com.christopheramazurgmail.rtracker.ItemGroup;
        import com.christopheramazurgmail.rtracker.R;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Typeface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.TextView;

/**
 * Created by haunter on 26/10/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, ItemGroup> allCategories;
    private List<String> items;

    public ExpandableListAdapter(Activity context, List<String> items,
                                 Map<String, ItemGroup> allCategories) {
        this.context = context;
        this.allCategories = allCategories;
        this.items = items;
    }

    public void setNewItems(List<String> items, Map<String, ItemGroup> allCategories) {
        this.items = items;
        this.allCategories = allCategories;
        notifyDataSetChanged();
    }

    public Object getChild(int groupPosition, int childPosition) {
        // Category names get group position
        String categoryName = items.get(groupPosition);
        ItemGroup category = allCategories.get(categoryName);
        category.get(childPosition);
        return allCategories.get(items.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Item childObject = (Item) getChild(groupPosition, childPosition);
        String childDesc = childObject.getDesc();
        String childPrice = childObject.getPrice();
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.report_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.item);
        TextView priceText = (TextView) convertView.findViewById(R.id.priceText);

        item.setText(childDesc);
        priceText.setText(childPrice);

        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        String categoryName = items.get(groupPosition);

        ItemGroup category = allCategories.get(categoryName);
        if (category == null) return 0;
        int categorySize = category.size();

        return categorySize;
    }

    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    public int getGroupCount() {
        return items.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        String itemName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.report_item_group,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.category);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(itemName);

        ItemGroup cat = allCategories.get(itemName);
        if (cat != null) {
            List<Item> category = cat.getItems();
            double runningTotal = 0; // @TODO make categories know about items instead of these stupid hacks

            for (Item indItem : category) {
                runningTotal += indItem.getPriceD();
            }

            TextView totalPriceText = (TextView) convertView.findViewById(R.id.totalPriceText);
            totalPriceText.setTypeface(null, Typeface.BOLD);
            totalPriceText.setText(String.format("%.2f", runningTotal));
        }
        return convertView;
    }



    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}