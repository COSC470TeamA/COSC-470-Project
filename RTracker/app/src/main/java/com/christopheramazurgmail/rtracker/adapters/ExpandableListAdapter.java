package com.christopheramazurgmail.rtracker.adapters;
        import java.util.List;
        import java.util.Map;

        import com.christopheramazurgmail.rtracker.R;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Typeface;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseExpandableListAdapter;
        import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> itemCollections;
    private List<String> items;

    public ExpandableListAdapter(Activity context, List<String> items,
                                 Map<String, List<String>> itemCollections) {
        this.context = context;
        this.itemCollections = itemCollections;
        this.items = items;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return itemCollections.get(items.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String child = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.report_item_group, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.category);

        item.setText(child);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return itemCollections.get(items.get(groupPosition)).size();
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
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}