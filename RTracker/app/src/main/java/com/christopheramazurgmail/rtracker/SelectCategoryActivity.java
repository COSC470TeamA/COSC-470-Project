package com.christopheramazurgmail.rtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;


public class SelectCategoryActivity extends AppCompatActivity {
    CategorizationEngine categorizationEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        this.categorizationEngine = (CategorizationEngine) getIntent().getSerializableExtra("CategorizationEngine");
        String[] arraySpinner = new String[categorizationEngine.getCategories().size()];

        int i = 0;
        for (Category category : categorizationEngine.getCategories()) {
            arraySpinner[i] = category.getName();
            i++;
        }

        final Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        categorySpinner.setAdapter(adapter);

        TextView itemText =  (TextView) findViewById(R.id.itemDesc);
        itemText.setText(categorizationEngine.getUncategorizedItems().getFirst().getDesc());

        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        categorizationEngine.addToDictionary(categorizationEngine.getUncategorizedItems().getFirst(), categorySpinner.getSelectedItem().toString());
                        finish();
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
        });


    }

}
