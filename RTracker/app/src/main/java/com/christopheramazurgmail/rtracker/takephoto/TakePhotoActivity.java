package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;


public class TakePhotoActivity extends Activity {

    public static final String STORENAME = "storename";
    public static final String ITEMS = "items";
    Button save, takePhoto, discard;
    CropImageView mImageView;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_CROP_PICTURE = 2;
    int REQUEST_CROP_STORE_NAME = 3;
    int REQUEST_CROP_ITEMS = 4;
    Uri imageUri, croppedImage, receiptStoreName, receiptItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        Bundle extras = getIntent().getExtras();
        mImageView = (CropImageView) findViewById(R.id.previewImage);

        if (extras != null) {
            //we get an image from gallery that we want to crop
            if (extras.containsKey("imageFromFile")) {
                imageUri = extras.getParcelable("imageFromFile");
                startCrop(imageUri);
            }
        }

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    protected void startCrop(Uri imageToCrop) {
        //Initialize CropImageActivity intent with a 200w x 400h rectangle, outputting to the passed image's URI
        //TODO: Figure out how to make rectangles not auto-scale w x h
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 400, imageToCrop);
        //Todo: set in colors xml
        cropImage.setOutlineColor(0xFF03A9F4);
        //Don't need face detection for cropping - it was set on by default
        cropImage.setDoFaceDetection(false);
        //set the source image as imageToCrop
        cropImage.setSourceImage(imageToCrop);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
    }

    //TODO: Get both rectangles on one screen
    protected void cropReceiptStoreName(Uri imageToPrecision) {
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 100, imageToPrecision);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(croppedImage);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_STORE_NAME);
    }

    protected void cropReceiptItems(Uri imageToPrecision) {
        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 400, imageToPrecision);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(croppedImage);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_ITEMS);
    }


    protected void startPrecisionRectangles(Uri imageToPrecision) {
        cropReceiptStoreName(imageToPrecision);
        cropReceiptItems(imageToPrecision);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //Original image from camera
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                mImageView.setImageURI(imageUri);
                startCrop(imageUri);

            } else if (requestCode == REQUEST_CROP_PICTURE) {
                croppedImage = data.getData();
                mImageView.setImageURI(croppedImage);
                Toast t = Toast.makeText(this,
                        "Image cropped and set!",
                        Toast.LENGTH_SHORT);
                t.show();

                startPrecisionRectangles(croppedImage);

                Intent i = this.getIntent();
                if (receiptStoreName != null && receiptItems != null) {
                    i.putExtra(STORENAME, receiptStoreName);
                    i.putExtra(ITEMS, receiptItems);
                }

            } else if (requestCode == REQUEST_CROP_STORE_NAME) {
                receiptStoreName = data.getData();

            } else if (requestCode == REQUEST_CROP_ITEMS) {
                receiptItems = data.getData();

            } else {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Toast t = Toast.makeText(this,
                            "Something went wrong taking a photo",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                if (requestCode == REQUEST_CROP_PICTURE) {
                    Toast t = Toast.makeText(this,
                            "Something went wrong while cropping the original photo",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                if (requestCode == REQUEST_CROP_STORE_NAME) {
                    Toast t = Toast.makeText(this,
                            "Something went wrong selecting the Store Name",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
                if (requestCode == REQUEST_CROP_ITEMS) {
                    Toast t = Toast.makeText(this,
                            "Something went wrong selecting the Items",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }


        }
    }

}
