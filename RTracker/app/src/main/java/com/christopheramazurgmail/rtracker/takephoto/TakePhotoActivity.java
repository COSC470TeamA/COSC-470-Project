package com.christopheramazurgmail.rtracker.takephoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.tesseract.OCRActivity;

import java.io.InputStream;

/**
 * Created by Chris Mazur on 11/27/2016.
 */

public class TakePhotoActivity extends Activity {

    private final Handler mHandler = new Handler();
    public static final String STORENAME = "storeName";
    public static final String ITEMS = "items";
    public static final String CROPPED_IMAGE = "croppedImage";
    //TODO: test string delete after
    public static final String IMAGEURI = "ImageURI";
    Button save, takePhoto, discard;
    CropImageView mImageView;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_CROP_PICTURE = 2;
    int REQUEST_CROP_STORE_NAME = 3;
    int REQUEST_CROP_ITEMS = 4;
    Uri imageUri, croppedImage, receiptStoreName, receiptItems;
    Bitmap mBitmap;

    RectangleOverlay mCrop;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        Intent i = this.getIntent();
        Bundle extras = i.getExtras();

        mImageView = (CropImageView) findViewById(R.id.previewImage);

        if (extras != null) {
            //we get an mBitmap from gallery that we want to crop
            if (extras.containsKey("imageFromFile")) {
                imageUri = extras.getParcelable("imageFromFile");
                startCrop();
            }
            else takePicture();
        } else takePicture();
    }

    protected void takePicture(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    protected void startCrop() {
        Toast t = Toast.makeText(this,
                "Crop the mBitmap so that the receipt is the focus",
                Toast.LENGTH_SHORT);
        t.show();

        Intent intent = new Intent(getApplicationContext(), CropImageActivity.class);
        startActivity(intent);

       /* Matrix mImageMatrix = mImageView.getImageMatrix();
        mHandler.post(new Runnable() {
            public void run() {

                rectangleOverlaySetup();
                mImageView.invalidate();
                if (mImageView.mRectangleOverlays.size() == 1) {
                    mCrop = mImageView.mRectangleOverlays.get(0);
                    mCrop.setFocus(true);
                }
            }
        });*/
    }


    private void rectangleOverlaySetup() {
        RectangleOverlay ro = new RectangleOverlay(mImageView, RectangleOverlay.DEFAULT_OUTLINE_COLOR, RectangleOverlay.DEFAULT_OUTLINE_CIRCLE_COLOR);
        InputStream iStream;
        Matrix mImageMatrix1 = mImageView.getImageMatrix();

        try{
            iStream = getContentResolver().openInputStream(imageUri);
            mBitmap = BitmapFactory.decodeStream(iStream);
            mImageView.setImageBitmap(mBitmap);
            iStream.close();
        } catch(Exception e){}

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        Rect imageRect = new Rect(0, 0, width, height);

        // make the default size about 4/5 of the width or height
        int cropWidth = Math.min(width, height) * 4 / 5;
        int cropHeight = cropWidth;
        int x = (width - cropWidth) / 2;
        int y = (height - cropHeight) / 2;

        RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
        ro.setup(mImageMatrix1, imageRect, cropRect, false, true);
        mImageView.add(ro);
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //Original mBitmap from camera
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                mImageView.setImageURI(imageUri);
                startCrop();
            } else if (requestCode == REQUEST_CROP_PICTURE) {
                croppedImage = data.getData();
                Bitmap b = data.getExtras().getParcelable("return-data");
                mImageView.setImageURI(croppedImage);
                Intent i = this.getIntent();
                i.putExtra(IMAGEURI, croppedImage);
                //cropReceiptStoreName(croppedImage);
                Intent OCR = new Intent(getApplicationContext(), OCRActivity.class);
                startActivity(OCR);
            }
        }
    }
}


