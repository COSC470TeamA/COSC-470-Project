package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;


public class TakePhotoActivity extends Activity {

    Button OCRActivity;
    Button takePicture;
    ImageButton cropPicture;
    ImageView imagePreview;
    int CAMERA_PIC_REQUEST = 1;
    int CAMERA_CROP_REQUEST = 2;
    //int CAMERA_QUICK_PIC_REQUEST = 3; - for taking a quick picture with no confirmation stuff
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        OCRActivity     = (Button)      findViewById(R.id.OCRActivity);
        takePicture     = (Button)      findViewById(R.id.picture);
        cropPicture     = (ImageButton) findViewById(R.id.crop_image);
        imagePreview    = (ImageView)   findViewById(R.id.previewImage);
        imagePreview.setVisibility(View.INVISIBLE);

        //Begin the activity in take-picture mode
        //if (settings.startinpicturemodesetting) {}
        if (savedInstanceState == null) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        }

        //Button for taking pictures
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
                }
            }
        });
        //Button for cropping
        cropPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_CROP_REQUEST);
                }
            }
        });

        //Button for launching OCR
        OCRActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagePreview.getVisibility() == imagePreview.VISIBLE) {
                    Intent OCRIntent = new Intent(getApplicationContext(), OCRActivity.class);
                    OCRIntent.putExtra("ImageURI", imageUri);
                    startActivity(OCRIntent);
                }

                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "No Image!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_PIC_REQUEST) {
                //Todo: Encapsulate
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);
                if (imagePreview.getVisibility() == View.INVISIBLE)
                    imagePreview.setVisibility(View.VISIBLE);

            }

            else if (requestCode == CAMERA_CROP_REQUEST){
                //do a crop
            }
        }
    }

}
