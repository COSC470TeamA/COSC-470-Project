package com.christopheramazurgmail.rtracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.christopheramazurgmail.rtracker.takephoto.TakePhotoActivity;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;


public class MainActivity extends AppCompatActivity {

    MySQLiteHelper db;
    MainFeedListAdapter adapter;
    DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Invokes the database
        db = new MySQLiteHelper(getApplicationContext());

        //Uncomment this to reset and test the database
        //db.danTestUpgrade();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<FeedItem> feedItemArrayList = getFeedContent();

        ListView listView = (ListView) findViewById(R.id.feed_list_view);
        this.adapter = new MainFeedListAdapter(this,
                R.layout.content_main_feed_item, feedItemArrayList);
        listView.setAdapter(adapter);

        //OCR activity
        FloatingActionButton OCRB = (FloatingActionButton)   findViewById(R.id.demo_button);
        OCRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OCRActivity.class);
                startActivity(intent);
            }
        });

        //Take Photo activity
        FloatingActionButton takePhotoButton = (FloatingActionButton)   findViewById(R.id.take_photo_button);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TakePhotoActivity.class);
                startActivity(intent);
            }
        });

        //Report Activity
        FloatingActionButton fab = (FloatingActionButton)   findViewById(R.id.report_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TopReportActivity.class);
                startActivity(intent);
            }
        });

        //Closes database connection on termination
        db.closeDB();
    }

    private ArrayList<FeedItem> getFeedContent() {
        ArrayList<FeedItem> feedContent = new ArrayList<>();

        ArrayList<Receipt> receipts = db.getAllReceipts();
        Date refDate = new Date();
        String[] days = getDaysOfWeek(refDate, Calendar.getInstance().getFirstDayOfWeek());

        int totalNumberReceiptsForWeek = 0;
        double totalValueReceiptsForWeek = 0.0;

        for(Receipt receipt : receipts){
            String date = outputFormatter.format(receipt.getDateCreated());
            if (Arrays.asList(days).contains(date)) {
                double receiptTotal = 0.0;

                for(Item item : receipt.getItemList()) {
                    receiptTotal += item.getPriceD();
                }
                totalNumberReceiptsForWeek++;
                totalValueReceiptsForWeek+=receiptTotal;
            }
        }

        //# of receipts per week in db
        feedContent.add(new FeedItem("Receipt Total", "Total number of receipts this week is " + totalNumberReceiptsForWeek));
        //$ worth of receipts in db per week
        feedContent.add(new FeedItem("Receipt Value", String.format("Total value of receipts is $%.2f", totalValueReceiptsForWeek)));
        //recent receipt
        feedContent.add(new FeedItem(R.drawable.s3print, "Recent Receipt", "Recent receipt image processed"));

        return feedContent;
    }

    private String[] getDaysOfWeek(Date refDate, int firstDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(refDate);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        String[] daysOfWeek = new String[7];
        for (int i = 0; i < 7; i++) {
            daysOfWeek[i] = outputFormatter.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return daysOfWeek;
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
