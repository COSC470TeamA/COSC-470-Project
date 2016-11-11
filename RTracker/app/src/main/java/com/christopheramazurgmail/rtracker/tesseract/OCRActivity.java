package com.christopheramazurgmail.rtracker.tesseract;

import com.christopheramazurgmail.rtracker.CategorizationEngine;
import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.Receipt;
import com.christopheramazurgmail.rtracker.ReceiptBridge;
import com.christopheramazurgmail.rtracker.ReceiptFactory;
import com.christopheramazurgmail.rtracker.SelectCategoryActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ScrollView;

import java.io.InputStream;

/**
 * Created by Chris Mazur on 26/10/31.
 * Provides a view for selecting and processing stored images
 */
public class OCRActivity extends Activity {

    FloatingActionButton processImageButton;
    FloatingActionButton selectImageButton;
    OCRWrapper OCR;
    Bitmap image;

    TextView OCRTextOutputField;
    ImageView imageToProcess;
    CategorizationEngine categorizationEngine;
    ReceiptFactory receiptFactory = new ReceiptFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        OCR = new OCRWrapper(this, "eng");
        categorizationEngine = new CategorizationEngine(this);

        //set up the view objects
        //TODO: remove this later
        imageToProcess = (ImageView) findViewById(R.id.OCRImageInput);
        imageToProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleImageViewClick(imageToProcess);
            }
        });
        OCRTextOutputField = (TextView) findViewById(R.id.OCRTextOutputField);
        selectImageButton = (FloatingActionButton) findViewById(R.id.selectImageButton);
        processImageButton = (FloatingActionButton) findViewById(R.id.processImageButton);
        image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();

        //Give user image selection on button click
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");      //any files for now
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    //start the intended activity; if result pass 1 to onActivityResult
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });


        //Assign image process prompt to button click

        processImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ocrResult = OCR.processImage(image);
                OCRTextOutputField.setText(ocrResult);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked an image
                // The Intent data Uri identifies which image was selected.
                Uri imageUri = data.getData();
                InputStream imageStream = null;
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);

                    image = BitmapFactory.decodeStream(imageStream);
                    imageToProcess.setImageBitmap(image);
                    imageStream.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    int currImg = 0;

    public void handleImageViewClick(ImageView imageView) {
        int[] testImageArray = {R.drawable.test_1, R.drawable.test_2};

        OCRTextOutputField.setText(OCR.processImage(image));

        if (currImg == testImageArray.length) {
            currImg = 0;
        }

        Receipt receipt = processImage(testImageArray[currImg], imageView);
        currImg++;

        OCRTextOutputField.setText(receipt.toString());
    }

    private Receipt processImage(int imageID, ImageView imageView) {
        ReceiptBridge bridge = new ReceiptBridge();
        imageView.setImageResource(imageID);
        image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();

        //make receipt object
        Receipt receipt = bridge.makeReceipt(OCR.processImage(image));

        //categorize receipt
        receipt = categorizationEngine.categorizeReceipt(receipt);

        //set output
        OCRTextOutputField.setText(receipt.toString());

        //display receipt in factory
        receiptFactory.start(this, receipt, categorizationEngine);

        return receipt;
    }
}
