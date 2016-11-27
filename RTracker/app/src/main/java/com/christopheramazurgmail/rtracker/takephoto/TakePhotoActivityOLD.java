package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;


public class TakePhotoActivityOLD extends Activity {

    public static final String STORENAME = "storeName";
    public static final String ITEMS = "items";
    public static final String CROPPED_IMAGE = "croppedImage";
    Button save, takePhoto, discard;
    CropImageView mImageView;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_CROP_PICTURE = 2;
    int REQUEST_CROP_STORE_NAME = 3;
    int REQUEST_CROP_ITEMS = 4;
    Uri imageUri, croppedImage, receiptStoreName, receiptItems;
    Intent i = this.getIntent();
    CropImageIntentBuilder cropImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        Bundle extras = getIntent().getExtras();
        mImageView = (CropImageView) findViewById(R.id.previewImage);

        if (extras != null) {
            //we get an mBitmap from gallery that we want to crop
            if (extras.containsKey("imageFromFile")) {
                imageUri = extras.getParcelable("imageFromFile");
                startCrop(imageUri);
            }
        }

        takePicture();
    }


    protected void takePicture(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    protected void startCrop(Uri imageToCrop) {

        Toast t = Toast.makeText(this,
                "Crop the mBitmap so that the receipt is the focus",
                Toast.LENGTH_SHORT);
        t.show();

        //Initialize CropImageActivity intent with a 200w x 400h rectangle, outputting to the passed mBitmap's URI
        //TODO: Figure out how to make rectangles not auto-scale w x h
        cropImage = new CropImageIntentBuilder(200, 400, croppedImage);
        //Todo: set in colors xml
        cropImage.setOutlineColor(0xFF03A9F4);
        //Don't need face detection for cropping - it was set on by default
        cropImage.setDoFaceDetection(false);
        //set the source mBitmap as imageToCrop
        cropImage.setSourceImage(imageToCrop);
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
    }

    //TODO: Get both rectangles on one screen
    protected void cropReceiptStoreName(Uri imageToPrecision) {

        Toast t = Toast.makeText(this,
                "Select the Store Name",
                Toast.LENGTH_SHORT);
        t.show();

        cropImage.setSourceImage(croppedImage);
        cropImage.outputX = 200;
        cropImage.outputY = 100;
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_STORE_NAME);
    }

    protected void cropReceiptItems(Uri imageToPrecision) {

        Toast t = Toast.makeText(this,
                "Select the Items",
                Toast.LENGTH_SHORT);
        t.show();

        cropImage.setSourceImage(croppedImage);
        cropImage.outputX = 200;
        cropImage.outputY = 200;
        startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_ITEMS);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //Original mBitmap from camera
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                mImageView.setImageURI(imageUri);
                startCrop(imageUri);
            } else if (requestCode == REQUEST_CROP_PICTURE) {
                Bundle extras = data.getExtras();
                croppedImage = extras.getParcelable("data");
                mImageView.setImageURI(croppedImage);
                i.putExtra(CROPPED_IMAGE, croppedImage);
                cropReceiptStoreName(croppedImage);
            } else if (requestCode == REQUEST_CROP_STORE_NAME) {
                Bundle extras = data.getExtras();
                receiptStoreName = extras.getParcelable("data");
                i.putExtra(STORENAME, receiptStoreName);
                cropReceiptItems(croppedImage);
            } else if (requestCode == REQUEST_CROP_ITEMS) {
                Bundle extras = data.getExtras();
                receiptItems = extras.getParcelable("data");
                i.putExtra(ITEMS, receiptItems);
                Intent OCR = new Intent(getApplicationContext(), OCRActivity.class);
                startActivity(OCR);

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
