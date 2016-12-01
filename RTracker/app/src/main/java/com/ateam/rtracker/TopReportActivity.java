package com.ateam.rtracker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.ateam.rtracker.adapters.ExpandableListAdapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

/**
 * Created by haunter on 26/10/16.
 * Top Report Activity is the top-most view of information about the categories.
 * Item Categories are the parent collapsible views in the List View.
 * Items are the child view in the List View.
 */
public class TopReportActivity extends AppCompatActivity {

    ArrayList<String> headerNames;
    ItemGroup childList;
    Map<String, ItemGroup> expListViewMap;
    ExpandableListView expListView;
    ExpandableListAdapter expListAdapter;
    Spinner searchBySpinner, orderBySpinner;

    Dictionary dictionary;
    MySQLiteHelper db = new MySQLiteHelper(this);

    // TODO remove test stuff when we can implement live DB calls
    Receipt testReceipt = new Receipt("Store Name AAA", "test1", "5.1", "test2", "8.2", "test3", "3.3", "xaguette", "9.99", "roast ox", "93.13");
    Receipt testReceipt2 = new Receipt("Store Name Bab", "best1", "11.1", "best2", "22.2", "best3", "3.3");
    Receipt testReceipt3 = new Receipt("Store Name Carlop", "vest1", "1.19", "vest2", "24.2", "vest3", "8.54");

    ArrayList<Receipt> allReceipts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_report);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dictionary = new Dictionary(this);
        createGroupList();

        createCollection();


        // Find the expandable list view
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expListAdapter = new ExpandableListAdapter(this, headerNames, expListViewMap);
        expListView.setAdapter(expListAdapter);

        // Find the search by spinner
        final String[] searchByValues = {"Category", "Receipt"};
        searchBySpinner = (Spinner) findViewById(R.id.searchBySpinner);
        ArrayAdapter<String> searchByAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchByValues);
        searchBySpinner.setAdapter(searchByAdapter);
        searchBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch (position) {
                case 0:
                    handleSortByCategory();
                break;
                case 1:

                    handleSortByReceipt();
                break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Find the order by spinner
        final String[] orderByValues = {"Most Recent", "Price"};
        orderBySpinner = (Spinner) findViewById(R.id.orderBySpinner);
        ArrayAdapter<String> orderByAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, orderByValues);
        orderBySpinner.setAdapter(orderByAdapter);
        orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                orderItems(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        //TODO remove this test stuff -- tests category separators
        for (Item i : testReceipt.getItemList()) {
            i.setCat("Booze");
        }
    }


    /**
     * Displays the Categories and their Items in the report.
     *
     * Clears all the current data and adds it back with the
     * category names as headers and the items as children.
     */
    public void handleSortByCategory() {

        expListViewMap.clear();
        headerNames.clear();
        allReceipts.clear();

        allReceipts = db.getAllReceipts();
        // TODO remove this test crap and replace with DB calls
        allReceipts.add(testReceipt);
        allReceipts.add(testReceipt2);
        allReceipts.add(testReceipt3);

        headerNames.addAll(dictionary.getCategoryNames());
        headerNames.add("Uncategorized");

        for (String catName : headerNames) {
            expListViewMap.put(catName, new ItemGroup());
        }

        for (Receipt rec : allReceipts) {
            for (Item item : rec.getItemList()) {
                if (item.getCat() == null) {
                    item.setCat("Uncategorized");
                }
                expListViewMap.get(item.getCat()).add(item);
            }
        }

        // Removes categories with no items
        Iterator it = expListViewMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ItemGroup> i = (Map.Entry) it.next();
            if (i.getValue().isEmpty()) {
                headerNames.remove(i.getKey());
                it.remove();
            }

        }


        expListAdapter.notifyDataSetChanged();
    }

    /**
     * Dispalys the Receipts and their Items in the report.
     *
     * Clears all the current data and adds it back with the
     * store name as headers and the items as children.
     */
    public void handleSortByReceipt() {
        expListViewMap.clear();
        headerNames.clear();
        allReceipts.clear();

        // TODO remove this test crap
        allReceipts.add(testReceipt);

        db.insertReceiptObject(testReceipt, testReceipt2, testReceipt3);

        ArrayList<Receipt> r = db.getAllReceipts();

        for (Receipt receipt : r) {
            expListViewMap.put(receipt.getStore(), receipt.getItems());
            headerNames.add(receipt.getStore());
        }

        expListAdapter.notifyDataSetChanged();
        orderItems();

    }

    /**
     * Orders all child items by whatever is specified in the order by spinner
     */
    public void orderItems() {
        int selectedPosition = orderBySpinner.getSelectedItemPosition();
        orderItems(selectedPosition);
    }
    public void orderItems(int selectedPosition) {
        switch (selectedPosition) {
            case 0:
                handleOrderByRecent();
                break;
            case 1:
                handleOrderByPrice();
                break;
        }
    }

    /**
     * Reorganizes the list items to be sorted by date
     */
    public void handleOrderByRecent() {

        // @TODO sort parents by receipt date
        // @TODO if category view, sort children by receipt date
        allReceipts = db.getAllReceipts();
        // @TODO REMOVE
        System.out.println(searchBySpinner.getSelectedItemPosition());
        if (searchBySpinner.getSelectedItemPosition() == 1) { // IF RECEIPT HEADERS
System.out.println("ATTEMPTING TO SORT BY DATE");
            ItemGroup parentItemGroup = new ItemGroup();

            for (Receipt rec : allReceipts) {
                parentItemGroup.add(new Item(rec.getStore(), (Long) rec.getDateCreated().getTime()));
            }
            // Put the headers into a fake item group to sort it by price
            parentItemGroup.sortByPrice();
            headerNames.clear();
            headerNames.addAll(parentItemGroup.getItemNames());

        }

        expListAdapter.notifyDataSetChanged();
    }

    /**
     * Reorganizes the parents and children to be sorted by price
     */
    public void handleOrderByPrice() {
        // Sort either the category names or the receipt names by price
        for (String parentName : expListViewMap.keySet()) {
            // Sort the children by price
            ItemGroup itemGroup = expListViewMap.get(parentName);
            itemGroup.sortByPrice();

        }

        ItemGroup parentItemGroup = new ItemGroup();

        for (Map.Entry entry : expListViewMap.entrySet()) {
            ItemGroup i = (ItemGroup) entry.getValue();
            parentItemGroup.add(new Item((String) entry.getKey(), i.getTotalPrice()));
        }
        // Put the headers into a fake item group to sort it by price
        parentItemGroup.sortByPrice();
        headerNames.clear();
        headerNames.addAll(parentItemGroup.getItemNames());
        expListAdapter.notifyDataSetChanged();
    }

    /**
     * Create the category names.
     */
    private void createGroupList() {
        headerNames = new ArrayList<>();

        headerNames.addAll(dictionary.getCategoryNames());
    }

    /**
     * TEST DATA
     *
     * Create item lists and attach them to category names.
     */
    private void createCollection() {
        // preparing collection(child)
        ItemBuilder itemBuilder = new ItemBuilder();

        ItemGroup gasItems = itemBuilder.build("Gas", "20.50", "Gas", "35.75", "Gas", "18.99");
        ItemGroup clothingItems = itemBuilder.build("Pants", "14.50", "Golf Pants", "45.29", "Hat", "9.99");
        ItemGroup foodItems = itemBuilder.build("Bread", "20.50", "Milk", "35.75", "Cheese", "18.99");
        ItemGroup electronicItems = itemBuilder.build("Headphones", "20.50", "Keyboard", "35.75");
        ItemGroup boozeItems = itemBuilder.build("Beer", "20.50", "Moonshine", "35.75", "Cognac", "18.99");


        expListViewMap = new TreeMap<String, ItemGroup>();

        // @TODO This switch is not necessary.
        for (String cat : headerNames) {
            // Put the list of items in the childList
            if (cat.equals("Gas"))
                loadChild(gasItems);
             else if (cat.equals("Clothes"))
                loadChild(clothingItems);
            else if (cat.equals("Food"))
                loadChild(foodItems);
            else if (cat.equals("Electronics"))
                loadChild(electronicItems);
            else if (cat.equals("Booze"))
                loadChild(boozeItems);

                // Attach the category name to the item list
                // and store it in expListViewMap
                expListViewMap.put(cat, childList);
            }
        }

    /** Populates the list of children (items)
     * from an array of items.
     * @param items
     */
    private void loadChild(ItemGroup items) {

        childList = items;
    }



    ////////////////////////////////////////////////////////////////

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
