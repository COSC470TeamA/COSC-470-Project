package com.christopheramazurgmail.rtracker;

import com.christopheramazurgmail.rtracker.tesseract.OCRWrapper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import static com.christopheramazurgmail.rtracker.R.id.OCRTextOutputField;

/**
 * Created by Chris Mazur on 26/10/31.
 * Provides a view for selecting and processing stored images
 *
 *
 */
public class OCRActivity extends Activity {

    FloatingActionButton processImageButton;
    FloatingActionButton selectImageButton;
    OCRWrapper OCR;
    Bitmap image;
    TextView OCRTextOutputField;
    ImageView imageToProcess;
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

        //set up the view objects
        //TODO: remove this later
        imageToProcess = (ImageView) findViewById(R.id.OCRImageInput);
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
                } catch (android.content.ActivityNotFoundException ex) {
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
                //ReceiptBridge bridge = new ReceiptBridge();
                //Receipt receipt = bridge.makeReceipt(ocrResult);
                //OCRTextOutputField.setText(receipt.toString());
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
                try {
                    imageToProcess.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri));
                    image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
