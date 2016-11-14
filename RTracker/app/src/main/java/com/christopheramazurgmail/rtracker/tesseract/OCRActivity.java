package com.christopheramazurgmail.rtracker.tesseract;


import com.christopheramazurgmail.rtracker.CategorizationEngine;
import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.Receipt;
import com.christopheramazurgmail.rtracker.ReceiptBridge;
import com.christopheramazurgmail.rtracker.ReceiptFactory;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


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
    Uri imageUriFromCamera;
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
        imageToProcess = (ImageView) findViewById(R.id.OCRImageInput);
        Bundle extras = getIntent().getExtras();

            //Case: Context from Take Photo Activity
        if (extras != null) {
            if (extras.containsKey("ImageURI")) {
                System.out.println("Has Image URI");
                try {
                    imageUriFromCamera = extras.getParcelable("ImageURI");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                pictureFromCamera();
            }
        }
            image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();

        //set up the view objects
        //TODO: remove this later
        imageToProcess = (ImageView) findViewById(R.id.OCRImageInput);

        // Set the Click Listener for the Image View

        imageToProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleImageViewClick(imageToProcess);
            }
        });

        OCRTextOutputField = (TextView) findViewById(R.id.OCRTextOutputField);
        selectImageButton = (FloatingActionButton) findViewById(R.id.selectImageButton);
        processImageButton = (FloatingActionButton) findViewById(R.id.processImageButton);

        //Give user image selection on button click
        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");      //any files for now
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    //start the get_content action; if result pass 1 to onActivityResult
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
                //String ocrResult = OCR.processImage(image);
                //OCRTextOutputField.setText(ocrResult);
                OCRTextOutputField.setText("");

                processImage();

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
                pictureFromGallery(data);
            }
        }
    }


    /**
     * Sets the Image View to display the specified drawable.
     *
     * @param drawable
     */
    public void putImageInCarousel(int drawable) {
        Uri imageUri = drawableToUri(drawable);
        InputStream imageStream;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            image = BitmapFactory.decodeStream(imageStream);
            imageToProcess.setImageBitmap(image);
            imageStream.close();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    int currImg = 0;
    int[] testImageArray = {R.drawable.s6print, R.drawable.s3print, R.drawable.s3, R.drawable.s2,
            R.drawable.s4, R.drawable.s5, R.drawable.s1,
            R.drawable.test_1};

    private void pictureFromGallery(Intent data){
        Uri imageUriFromGallery;

        imageUriFromGallery = data.getData();

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(imageUriFromGallery);
            image = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        image = rotateImage(image);
        imageToProcess.setImageBitmap(image);
    }

    private void pictureFromCamera(){
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(imageUriFromCamera);
            image = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        image = rotateImage(image);
        imageToProcess.setImageBitmap(image);
    }

    private Bitmap rotateImage(Bitmap toRotate){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(toRotate, 0, 0, toRotate.getWidth(), toRotate.getHeight(), matrix, true);
    }


    /**
     * Invoked on click of the Image View.
     *
     * @param imageView
     */
    public void handleImageViewClick(ImageView imageView) {
        currImg++;
        if (currImg == testImageArray.length) {
            currImg = 0;
        }
        // Put a new image in the Image View
        putImageInCarousel(testImageArray[currImg]);

        //OCRTextOutputField.setText(OCR.processImage(image));


        //Receipt receipt = processImage(testImageArray[currImg], imageView);


        //OCRTextOutputField.setText(receipt.toString());


    }


    /**
     * Looks up the URI of an Android drawable.
     *
     * @param drawable
     * @return
     */
    public Uri drawableToUri(int drawable) {
        Uri imageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(drawable)
                + '/' + getResources().getResourceTypeName(drawable)
                + '/' + getResources().getResourceEntryName(drawable));

        return imageUri;
    }

    private Receipt processImage(int imageID) {
        ReceiptBridge bridge = new ReceiptBridge();

        //imageToProcess.setImageBitmap(decodeSampledBitmapFromResource(getResources(), imageID, 200, 300));


        //imageView.setImageResource(imageID);
        image = ((BitmapDrawable) imageToProcess.getDrawable()).getBitmap();

        // Get the output of the OCR
        String OCROutput = OCR.processImage(image);

        // Show the output onscreen
        OCRTextOutputField.setText(OCROutput);

        // Make receipt object
        Receipt receipt = bridge.makeReceipt(OCROutput);

        // Categorize receipt
        receipt = categorizationEngine.categorizeReceipt(receipt);

        // OCRTextOutputField.setText(receipt.toString());
        //OCRTextOutputField.setText(OCRTextOutputField.getText() + "\n\n" + receipt.toString());
        OCRTextOutputField.append("\n\nReceipt:\n\n" + receipt.toString());

        // Display receipt in factory
        receiptFactory.start(this, receipt, categorizationEngine);


        System.out.println(OCRTextOutputField.getText());


        return receipt;
    }

    public void processImage() {
        processImage(testImageArray[currImg]);
    }

    /**
     * From https://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}

