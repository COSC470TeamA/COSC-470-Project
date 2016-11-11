package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
        initializeItems(getIntent().getSerializableExtra("Items"));
        TextView storeName = (TextView) findViewById(R.id.store_name);
        storeName.setText((String) getIntent().getSerializableExtra("StoreName"));

        ListView listView = (ListView) findViewById(R.id.item_list);
        double totalPrice = 0.00;

        //add up total
        for(int i=0;i<listView.getCount();i++){
            Item item = (Item) listView.getItemAtPosition(i);
            totalPrice += Double.parseDouble(item.getPrice());
        }

        TextView totalPriceView = (TextView) findViewById(R.id.total_price);
        totalPriceView.setText(String.format("$%.2f", totalPrice));

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
                finish();
            }
        });
    }

    private void initializeItems(Serializable items) {
        ArrayList<Item> itemsList = (ArrayList<Item>) items;

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
            modifiedItems.add(item);
        }
        return modifiedItems;
    }

    private void applyChanges() {
        this.receipt.items.setItems(getModifiedReceiptItems());
    }

    public Receipt getReceipt() {
        return this.receipt;
    }

}
