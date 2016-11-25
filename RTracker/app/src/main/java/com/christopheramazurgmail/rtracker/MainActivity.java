package com.christopheramazurgmail.rtracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.christopheramazurgmail.rtracker.takephoto.TakePhotoActivity;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;


public class MainActivity extends AppCompatActivity {

    MySQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Invokes the database
        db = new MySQLiteHelper(getApplicationContext());

        //Uncomment this to reset and test the database

        /*
        //Variables for the database tests
        ArrayList<String> receipts = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<String> storeNames = new ArrayList<>();
        ArrayList<String> storeReceiptNames = new ArrayList<>();
        Item testItem1 = new Item("Potatoes", 23.45);
        Item testItem2 = new Item("Pizza", 18.33);
        Receipt rec = new Receipt("Wal-Mart");
        rec.add(testItem1);
        rec.add(testItem2);

        db.danTestUpgrade();

        System.out.println("Testing the getCat methods: " + db.getCatID(1) + " " + db.getCatName(1));
        System.out.println("Testing the getUser methods: " + db.getUser(1));
        System.out.println("Testing the getItem methods: " + db.getItemID("red681", "Coke")
                + " " + db.getItemName("red681", "Coke") + " " + db.getItemPrice("red681", "Coke"));
        System.out.println("Testing the getAllReceiptID method: ");
        receipts = db.getAllReceiptID();
        for (String item : receipts) {
            System.out.println(item);
        }

        System.out.println("Building a receipt");
        Receipt receipt = new Receipt();
        receipt = db.getReceiptObject("red681");
        System.out.println("Inserting a receipt with ID red699");
        db.insertReceiptObject("red699", rec);
        System.out.println("Testing the getAllItemsInTable method: ");
        itemNames = db.getAllItemsInTable();
        for (String listing : itemNames) {
            System.out.println(listing);
        }

        System.out.println("Deleting a receipt with ID red699");
        db.deleteReceipt("red699");
        System.out.println("Testing the getAllItemsInTable method: ");
        itemNames = db.getAllItemsInTable();
        for (String listing : itemNames) {
            System.out.println(listing);
        }

        System.out.println("Testing the getAllStoresInTable method: ");
        storeNames = db.getAllStoresInTable();
        for (String listing2 : storeNames) {
            System.out.println(listing2);
        }

        System.out.println("Testing the getAllStoreReceiptsInTable method: ");
        storeReceiptNames = db.getAllStoresReceiptsInTable();
        for (String listing3 : storeReceiptNames) {
            System.out.println(listing3);
        }

        */
        //End of database tests

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //OCR activity
        FloatingActionButton OCRB = (FloatingActionButton)   findViewById(R.id.OCRFAB);
        OCRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), OCRActivity.class);
                startActivity(intent);
            }
        });

        //Take Photo activity
        FloatingActionButton takePhotoButton = (FloatingActionButton)   findViewById(R.id.takePhotoFAB);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
                startActivity(intent);
            }
        });

        //Report Activity
        FloatingActionButton fab = (FloatingActionButton)   findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), TopReportActivity.class);
                startActivity(intent);
            }
        });

        //Closes database connection on termination
        db.closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
