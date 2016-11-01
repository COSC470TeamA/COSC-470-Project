package com.christopheramazurgmail.rtracker;

import com.christopheramazurgmail.rtracker.tesseract.OCRWrapper;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.FileDescriptor;
import java.io.IOException;

import static com.christopheramazurgmail.rtracker.R.drawable.default_image;
import static com.christopheramazurgmail.rtracker.R.drawable.test_1;

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
        /*
        initialize OCR Object with context and language.
        TODO: Replace Context with image handlers e.g. "load image" or "from photo"
        For now it just checks the local filepath for everything important.
        */

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
                intent.setType("image/png");      //png files
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
/*                ReceiptBridge bridge = new ReceiptBridge();
                Receipt receipt = bridge.makeReceipt(ocrResult);
                receipt = categorizationEngine.categorizeReceipt(receipt);
                OCRTextOutputField.setText(receipt.toString());*/
            }
        });
    }
    int currImg = 0;
public void handleImageViewClick(ImageView imageView) {
    OCRTextOutputField.setText("");
    switch  (currImg) {
        case 0:
            imageView.setImageResource(R.drawable.test_1);
            image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
            break;
        case 1:
            imageView.setImageResource(R.drawable.test_2);
            image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
            break;
        case 2:
            imageView.setImageResource(R.drawable.test_3);
            image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
            break;
        case 3:
            imageView.setImageResource(R.drawable.default_image);
            image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
            currImg = -1; // Go back to the start of the switch next time
            break;
    }
    currImg++;
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
                imageToProcess.setImageURI(imageUri);
                image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
