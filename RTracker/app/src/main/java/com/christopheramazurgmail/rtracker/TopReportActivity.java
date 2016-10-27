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

/**
 * Created by haunter on 26/10/16.
 */
public class TopReportActivity extends Activity {

    List<String> categoryNames;
    List<Item> childList;
    Map<String, List<Item>> allCategories;
    ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_report);

        createGroupList();

        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, categoryNames, allCategories);
        expListView.setAdapter(expListAdapter);


        expListView.setOnChildClickListener(new OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
//                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
//                        .show();

                return true;
            }
        });
    }

    /**
     * TEST DATA
     *
     * Create the category names.
     */
    private void createGroupList() {
        categoryNames = new ArrayList<String>();
        categoryNames.add("Gas");
        categoryNames.add("Clothes");
        categoryNames.add("Food");
        categoryNames.add("Electronics");
        categoryNames.add("Booze");
    }

    /**
     * TEST DATA
     *
     * Create item lists and attach them to category names.
     */
    private void createCollection() {
        // preparing collection(child)
        ItemBuilder itemBuilder = new ItemBuilder();

        ArrayList<Item> gasItems = itemBuilder.build("Gas", "20.50", "Gas", "35.75", "Gas", "18.99");
        ArrayList<Item> clothingItems = itemBuilder.build("Pants", "14.50", "Golf Pants", "45.29", "Hat", "9.99");
        ArrayList<Item> foodItems = itemBuilder.build("Bread", "20.50", "Milk", "35.75", "Cheese", "18.99");
        ArrayList<Item> electronicItems = itemBuilder.build("Headphones", "20.50", "Keyboard", "35.75");
        ArrayList<Item> boozeItems = itemBuilder.build("Beer", "20.50", "Moonshine", "35.75", "Cognac", "18.99");


        allCategories = new LinkedHashMap<String, List<Item>>();

        for (String cat : categoryNames) {
            // Put the list of items in the childList
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

                // Attach the category name to the item list
                // and store it in allCategories
                allCategories.put(cat, childList);
            }
        }


    private void loadChild(String[] items) {
        childList = new ArrayList<Item>();
        for (String item : items)
            childList.add(new Item(item));
    }

    /** Populates the list of children (items)
     * from an array of items.
     * @param items
     */
    private void loadChild(Item[] items) {
        childList = new ArrayList<Item>();
        for (Item item : items)
            childList.add(item);
    }
    private void loadChild(ArrayList<Item> items) {
        childList = items;
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
