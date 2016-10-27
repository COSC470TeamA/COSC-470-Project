package com.christopheramazurgmail.rtracker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.christopheramazurgmail.rtracker.adapters.ExpandableListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class TopReportActivity extends Activity {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> itemCategory;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_report);

        createGroupList();

        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, itemCategory);
        expListView.setAdapter(expListAdapter);


        expListView.setOnChildClickListener(new OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        groupList.add("Gas");
        groupList.add("Clothes");
        groupList.add("Food");
        groupList.add("Electronics");
        groupList.add("Booze");
    }

    private void createCollection() {
        // preparing collection(child)
        String[] gasItems = { "Gas", "Gas",
                "Gas" };
        String[] clothingItems = { "Pants", "Golf Pants", "Skirt" };
        String[] foodItems = { "Bread", "Milk",
                "Cheese", "Coffee" };
        String[] electronicItems = { "Headphones", "Keyboard" };
        String[] boozeItems = { "Beer", "Moonshine", "Cognac" };

        itemCategory = new LinkedHashMap<String, List<String>>();

        for (String cat : groupList) {
            if (cat.equals("Gas")) {
                loadChild(gasItems);
            } else if (cat.equals("Clothes"))
                loadChild(clothingItems);
            else if (cat.equals("Food"))
                loadChild(foodItems);
            else if (cat.equals("Electronics"))
                loadChild(electronicItems);
            else if (cat.equals("Booze"))
                loadChild(boozeItems);


            itemCategory.put(cat, childList);
        }
    }

    private void loadChild(String[] items) {
        childList = new ArrayList<String>();
        for (String item : items)
            childList.add(item);
    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
