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

        //Variables for the database tests
        ArrayList<String> receipts = new ArrayList<>();
        ArrayList<String> itemNames = new ArrayList<>();
        Item testItem1 = new Item("Potatoes", 23.45);
        Item testItem2 = new Item("Pizza", 18.33);
        Receipt rec = new Receipt();
        rec.add(testItem1);
        rec.add(testItem2);

        //Uncomment this line if you need a fresh database
        //Tables are populated with some basic information upon creation
        
        //db.danTestUpgrade();

        //These are just some tests to see if the database is functioning properly
        //All of them output to the terminal
        //These can be commented out
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
        System.out.println("Testing the getAllItemNames method with ID red699: ");
        itemNames = db.getAllItemNamesOnReceipt("red699");
        for (String listing : itemNames) {
            System.out.println(listing);
        }
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
