package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import static com.christopheramazurgmail.rtracker.R.id.item;

public class ReceiptFactory extends AppCompatActivity {
    private CategorizationEngine categorizationEngine;
    private Receipt receipt;

    public void start(Context context, Receipt receipt, CategorizationEngine categorizationEngine) {
        Intent intent = new Intent(context, ReceiptFactory.class);
        intent.putExtra("StoreName", receipt.getStore());
        intent.putExtra("Items", (ArrayList) receipt.items.getItems());
        intent.putExtra("CategorizationEngine", categorizationEngine);
        intent.putExtra("Receipt", receipt);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_factory);
        this.receipt = (Receipt) getIntent().getSerializableExtra("Receipt");
        this.categorizationEngine = (CategorizationEngine) getIntent().getSerializableExtra("CategorizationEngine");

        //populate store name
        TextView storeName = (TextView) findViewById(R.id.store_name);
        storeName.setText((String) getIntent().getSerializableExtra("StoreName"));

        //populate & initialize tag all spinner
        Spinner tagAllSpinner = (Spinner) findViewById(R.id.tag_all_spinner);
        String[] arraySpinner = populateSpinner(categorizationEngine.getCategories(), "Tag All");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        tagAllSpinner.setAdapter(spinnerAdapter);
        tagAllSpinner.setSelection(0, false);
        tagAllSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    resetAllCategories((ArrayList<Item>) getIntent().getSerializableExtra("Items"));
                } else {
                    setAllCategories(adapterView.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //populate items
        initializeItems((ArrayList<Item>) getIntent().getSerializableExtra("Items"));

        //populate total
        TextView totalPriceView = (TextView) findViewById(R.id.total_price);
        totalPriceView.setText(String.format("$%.2f", getTotal()));

        //set save actions
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
                finish();
            }
        });
    }

    private void resetAllCategories(ArrayList<Item> itemsList){
        ListView listView = (ListView) findViewById(R.id.item_list);

        ArrayList<Item> modifiedItems = new ArrayList<>();
        for(int i=0;i<listView.getCount();i++){
            Item item = (Item) listView.getItemAtPosition(i);
            item.setCat(null);
            modifiedItems.add(item);
        }

        initializeItems(modifiedItems);
    }

    private void setAllCategories(String category) {
        ListView listView = (ListView) findViewById(R.id.item_list);

        ArrayList<Item> modifiedItems = new ArrayList<>();
        for(int i=0;i<listView.getCount();i++){
            Item item = (Item) listView.getItemAtPosition(i);
            item.setCat(category);
            modifiedItems.add(item);
        }

        initializeItems(modifiedItems);
    }

    private void initializeItems(ArrayList<Item> itemsList) {
        ListView listView = (ListView) findViewById(R.id.item_list);
        ReceiptFactoryListAdapter adapter = new ReceiptFactoryListAdapter(this,
                R.layout.content_item_receipt_factory, itemsList, categorizationEngine.getCategories());
        listView.setAdapter(adapter);
    }

    private ArrayList<Item> getModifiedReceiptItems() {
        ListView listView = (ListView) findViewById(R.id.item_list);
        ArrayList<Item> modifiedItems = new ArrayList<>();
        for(int i=0;i<listView.getCount();i++){
            Item item = (Item) listView.getItemAtPosition(i);
            categorizationEngine.addToDictionary(item, item.getCat());
            modifiedItems.add(item);
        }
        return modifiedItems;
    }

    private double getTotal(){
        double totalPrice = 0.00;

        ListView listView = (ListView) findViewById(R.id.item_list);

        //add up total
        for(int i=0;i<listView.getCount();i++){
            Item item = (Item) listView.getItemAtPosition(i);
            totalPrice += Double.parseDouble(item.getPrice());
        }

        return totalPrice;
    }

    private void applyChanges() {
        this.receipt.items.setItems(getModifiedReceiptItems());
    }

    public Receipt getReceipt() {
        return this.receipt;
    }

    public static String[] populateSpinner(LinkedList<Category> categories, String defaultOption) {
        String[] arraySpinner = new String[categories.size() + 1];

        arraySpinner[0] = defaultOption;
        //set default selected category of none

        int index = 1;
        for (Category category : categories) {
            arraySpinner[index] = category.getName();
            index++;
        }

        return arraySpinner;
    }
}