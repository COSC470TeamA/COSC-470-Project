package com.christopheramazurgmail.rtracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.christopheramazurgmail.rtracker.tesseract.OCRWrapper;

import java.io.File;

/**
 * Created by Chris Mazur on 10/31/2016.
 *
 * Simple photo activity - creates an image file appended with datestamp
 *
 */

public class TakePhotoActivity extends Activity {

    protected Button takePhotoButton;
    protected ImageView currentPhoto;
    protected TextView noImageTextField;
    protected String dataPath;
    protected boolean photoTaken;
    protected Context context;
    protected Time t;
    protected File dataFile;
    protected static final String PHOTO_TAKEN = "photo_taken";
    OCRWrapper OCR;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        OCR = new OCRWrapper(this, "eng");
        currentPhoto = ( ImageView ) findViewById( R.id.currentCapturedImage );
        noImageTextField = ( TextView ) findViewById( R.id.noImageTextField );
        takePhotoButton = ( Button ) findViewById( R.id.takePhotoButton);
        context = this;
        t = new Time();

        dataPath = context.getFilesDir() + "/Receipt Tracker/";

        //take a picture
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCameraActivity();
            }
        });
    }





    protected void startCameraActivity() {

        //check filepath; create new file
        checkPhotoDataFile(new File(dataPath), this);
        //gimme a uri! But not a youry
        Uri outputFileUri = Uri.fromFile(dataFile);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);
    }


    protected void onPhotoTaken() {
        photoTaken = true;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap bitmap = BitmapFactory.decodeFile(dataPath, options );
        currentPhoto.setImageBitmap(bitmap);

        noImageTextField.setText(OCR.processImage(bitmap));

    }

    private void checkPhotoDataFile(File dir, Context context) {
        if (!dir.exists() && dir.mkdirs()) {
            //made directory
        }
      /*  if (dir.exists()) {
            //append unique tag to path for file
            t.setToNow();
            String dataFilePath = dataPath + "Image_" + t.toString().substring(0,8) + ".png";
            dataFile = new File(dataFilePath);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == -1) noImageTextField.setText("Photo Capture Cancelled");
        else if (resultCode == 0) onPhotoTaken();
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        outState.putBoolean( TakePhotoActivity.PHOTO_TAKEN, photoTaken);
    }
    @Override
    protected void onRestoreInstanceState( Bundle savedInstanceState)
    {
        Log.i( "MakeMachine", "onRestoreInstanceState()");
        if( savedInstanceState.getBoolean( TakePhotoActivity.PHOTO_TAKEN ) ) {
            onPhotoTaken();
        }
    }
}







