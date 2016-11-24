package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.christopheramazurgmail.rtracker.R;


public class TakePhotoActivity extends Activity {

    Button takePicture;
    ImageView imagePreview;
    int CAMERA_PIC_REQUEST = 1;
    int REQUEST_CROP_PICTURE = 2;
    int REQUEST_CROP_STORE_NAME = 3;
    int REQUEST_CROP_ITEMS = 4;
    Uri imageUri, receiptStoreName, receiptItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        takePicture     = (Button)      findViewById(R.id.picture);

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
    }


    protected void startCrop(Uri imageToCrop){
        //Initialize CropImage intent with a 200x200 rectangle, outputting to imageToCrop URI
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, imageToCrop);
        //Todo: set in colors xml
        cropImage.setOutlineColor(0xFF03A9F4);
        //Don't need face detection for cropping - it was set on by default
        cropImage.setDoFaceDetection(false);
        //set the source image as imageToCrop
        cropImage.setSourceImage(imageToCrop);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
    }

    //TODO: Get both rectangles on one screen
    protected void cropReceiptStoreName(Uri imageToPrecision){
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 100, receiptStoreName);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(imageToPrecision);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
    }

    protected void cropReceiptItems(Uri imageToPrecision){
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 100, receiptItems);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(imageToPrecision);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
    }


    protected void startPrecisionRectangles(Uri imageToPrecision){
        cropReceiptStoreName(imageToPrecision);
        cropReceiptItems(imageToPrecision);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PIC_REQUEST) {
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);
                startCrop(imageUri);
            }

            else if (requestCode == REQUEST_CROP_PICTURE){
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);
                startPrecisionRectangles(imageUri);
            }

            else if (requestCode == REQUEST_CROP_STORE_NAME){
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);

            }

            else if (requestCode == REQUEST_CROP_ITEMS){
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri);
            }

        }
    }

}
