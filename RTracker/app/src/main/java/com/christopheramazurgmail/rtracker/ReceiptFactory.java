package com.christopheramazurgmail.rtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.adapters.LongClickEditDialogListener;
import com.christopheramazurgmail.rtracker.adapters.ReceiptFactoryListAdapter;

import java.util.ArrayList;
import java.util.LinkedList;


public class ReceiptFactory extends AppCompatActivity {
    private CategorizationEngine categorizationEngine;
    private Receipt receipt;
    private ArrayList<Item> itemArrayList;
    private ReceiptFactoryListAdapter adapter;

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
        storeName.setOnLongClickListener(new LongClickEditDialogListener());

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
                    resetAllCategories((ArrayList<Item>)getIntent().getSerializableExtra("Items"));
                } else {
                    setAllCategories(adapterView.getItemAtPosition(i).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //populate items
        this.itemArrayList = (ArrayList<Item>)getIntent().getSerializableExtra("Items");
        initializeItems(this.itemArrayList);

        //populate total
        TextView totalPriceView = (TextView) findViewById(R.id.total_price);
        totalPriceView.setText(String.format("$%.2f", getTotal()));

        //update total on callback
        Message message = new Message();

        //allows text to be editable
        totalPriceView.setOnLongClickListener(new LongClickEditDialogListener());

        //set + action
        Button addItemButton = (Button) findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(new OnClickAddItemDialog());

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
        initializeItems(itemsList);
    }

    private void setAllCategories(String category) {
        ArrayList<Item> modifiedItems = new ArrayList<>();
        for(int i=0;i<adapter.getCount();i++){
            Item item = new Item();
            item.setDesc(((Item) adapter.getItem(i)).getDesc());
            item.setPrice(((Item) adapter.getItem(i)).getPriceD());
            item.setCat(category);
            modifiedItems.add(item);
        }

        initializeItems(modifiedItems);
    }

    private void initializeItems(ArrayList<Item> itemsList) {
        ListView listView = (ListView) findViewById(R.id.item_list);
        this.adapter = new ReceiptFactoryListAdapter(this,
                R.layout.content_item_receipt_factory, itemsList, categorizationEngine.getCategories());
        listView.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateTotal();
            }
        });

    }

    private ArrayList<Item> getModifiedReceiptItems() {
        ArrayList<Item> modifiedItems = new ArrayList<>();
        for(int i=0;i<adapter.getCount();i++){
            Item item = (Item) adapter.getItem(i);
            categorizationEngine.addToDictionary(item, item.getCat());
            modifiedItems.add(item);
        }
        return modifiedItems;
    }

    private double getTotal(){
        double totalPrice = 0.00;

        //add up total
        for(int i=0;i<adapter.getCount();i++){
            Item item = adapter.getItem(i);
            totalPrice += Double.parseDouble(item.getPrice());
        }

        return totalPrice;
    }

    private void applyChanges() {
        TextView storeName = (TextView) findViewById(R.id.store_name);
        this.receipt.setStore(storeName.getText().toString());
        this.receipt.items.setItems(getModifiedReceiptItems());

        MySQLiteHelper db = new MySQLiteHelper(this);
        db.insertReceiptObject(this.receipt);


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

    private void updateTotal() {
        TextView totalPriceView = (TextView) findViewById(R.id.total_price);
        totalPriceView.setText(String.format("$%.2f", getTotal()));
    }

    private class OnClickAddItemDialog implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Add Item");
            // Get the layout inflater
            LayoutInflater inflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View view = inflater.inflate(R.layout.add_item_dialog, null);
            builder.setView(view);
            //Display text of calling text view in edit field
            final EditText itemDescEditText = (EditText) view.findViewById(R.id.item_name_edit_text);
            final EditText itemPriceEditText = (EditText) view.findViewById(R.id.item_price_edit_text);

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    try {
                        String itemPrice = itemPriceEditText.getText().toString().replace("$", "");
                        double priceDouble = Double.parseDouble(itemPrice);
                        String itemDesc = itemDescEditText.getText().toString();
                        itemArrayList.add(new Item(itemDesc, priceDouble));
                        adapter.notifyDataSetChanged();
                        updateTotal();
                        dialog.dismiss();
                    } catch (NumberFormatException nfe) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Not a Valid Price", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            //Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}