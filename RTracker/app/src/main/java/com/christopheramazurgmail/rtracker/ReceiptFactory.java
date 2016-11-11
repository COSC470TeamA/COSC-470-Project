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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ReceiptFactory extends AppCompatActivity {
    private CategorizationEngine categorizationEngine;
    private static Context thisContext;

    public static void start(Context context, String storeName, List<Item> items, CategorizationEngine categorizationEngine) {
        thisContext = context;
        Intent intent = new Intent(context, ReceiptFactory.class);
        intent.putExtra("StoreName", storeName);
        intent.putExtra("Items", (ArrayList) items);
        intent.putExtra("CategorizationEngine", categorizationEngine);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_factory);

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
    }

    private void initializeItems(Serializable items) {
        ArrayList<Item> itemsList = (ArrayList<Item>) items;

        ListView listView = (ListView) findViewById(R.id.item_list);
        ReceiptFactoryListAdapter adapter = new ReceiptFactoryListAdapter(this,
                R.layout.content_item_receipt_factory, itemsList, categorizationEngine.getCategories());
        listView.setAdapter(adapter);
    }
}
