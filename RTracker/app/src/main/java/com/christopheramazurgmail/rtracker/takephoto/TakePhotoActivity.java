package com.christopheramazurgmail.rtracker.takephoto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static android.view.View.INVISIBLE;

/**
 * Created by Chris Mazur on 11/27/2016.
 */

public class TakePhotoActivity extends MonitoredActivity {

    private final Handler mHandler = new Handler();
    public static final String STORENAME = "storeName";
    public static final String ITEMS = "items";
    public static final String CROPPED_IMAGE = "croppedImage";
    //TODO: test string delete after
    public static final String IMAGEURI = "ImageURI";
    private String TAG = "TakePhotoActivity", filePath, filePathOld;
    CropImageView mImageView;
    int mAspectX = 1;
    int mAspectY = 1;
    int REQUEST_IMAGE_CAPTURE = 1;
    int REQUEST_CROP_PICTURE = 2;
    int REQUEST_CROP_STORE_NAME = 3;
    int REQUEST_CROP_ITEMS = 4;
    Boolean CROPPING_BASE = true, CROPPING_STORE_NAME = false, CROPPING_ITEMS = false;
    Boolean mSaving = false;
    RectangleOverlay mCrop;
    Uri imageUri, receiptStoreName, receiptItems;
    Bitmap mImage = null, croppedImage = null;
    Button mSave, mDiscard, mTakePhoto;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        mImageView = (CropImageView) findViewById(R.id.previewImage);
        mSave = (Button) findViewById(R.id.save);
        mDiscard= (Button) findViewById(R.id.discard);
        mTakePhoto= (Button) findViewById(R.id.takePhoto);

        mSave.setVisibility(INVISIBLE);
        mDiscard.setVisibility(INVISIBLE);
        mTakePhoto.setVisibility(INVISIBLE);

        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 16) { // >= Gingerbread && < Jelly Bean
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        Intent i = this.getIntent();
        Bundle extras = i.getExtras();


        findViewById(R.id.discard).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });

        findViewById(R.id.save).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        onSaveClicked();
                    }
                });

        findViewById(R.id.takePhoto).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        takePicture();
                    }
                });


        if (extras != null) {
            //we get a URI from gallery that we want to crop
            if (extras.containsKey("imageFromFile")) {
                imageUri = extras.getParcelable("imageFromFile");
                startCrop();
            } else takePicture();
        } else takePicture();
    }

    protected void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    protected void startCrop() {
        mSave.setVisibility(View.VISIBLE);
        mDiscard.setVisibility(View.VISIBLE);
        Toast t = Toast.makeText(this,
                "Crop the Bitmap so that the receipt is the focus",
                Toast.LENGTH_SHORT);
        t.show();
        startrectangleOverlay();
    }


    public void startrectangleOverlay() {
        InputStream imageStream;
        //Get the base image from URI to Bitmap
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            mImage = BitmapFactory.decodeStream(imageStream);
            imageStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        //Rotate and set the image base
        mImageView.setImageRotateBitmapResetBase(new RotateBitmap(mImage, 90), true);

        //mutex waits for latch
        startBackgroundJob(this, null,
                "Beginning Crop",
                new Runnable() {
                    public void run() {
                        final CountDownLatch latch = new CountDownLatch(1);
                        final Bitmap b = mImage;

                        mHandler.post(new Runnable() {
                            public void run() {
                                //If we've modified the image, update the image base
                                if (b != mImage && b != null) {
                                    mImageView.setImageBitmapResetBase(b, false);
                                    mImage.recycle();
                                    mImage = b;
                                }

                                mImageView.center(true, true);
                                latch.countDown();
                            }
                        });

                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        mRunRectangleOverlay.run();
                    }
                }, mHandler);
    }


    Runnable mRunRectangleOverlay = new Runnable() {
        @SuppressWarnings("hiding")
        Matrix mImageMatrix;

        // Create the rectangle
        private void makeDefaultRectangle() {
            RectangleOverlay ro = new RectangleOverlay(mImageView, RectangleOverlay.DEFAULT_OUTLINE_COLOR, RectangleOverlay.DEFAULT_OUTLINE_COLOR);

            int height = mImage.getHeight();
            int width = mImage.getWidth();
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;
            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;


            Rect imageRect = new Rect(0, 0, width, height);
            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);

            ro.setup(mImageMatrix, imageRect, cropRect, false, false);
            mImageView.add(ro);
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            mHandler.post(new Runnable() {
                public void run() {
                    makeDefaultRectangle();
                    mImageView.invalidate();
                    if (mImageView.mRectangleOverlays.size() == 1) {
                        mCrop = mImageView.mRectangleOverlays.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };

    private void onSaveClicked() {
        if (mCrop == null) return;
        if (mSaving) return;
        mSaving = true;

        //Get the dimensions of the rectangle representing the selected area
        Rect r = mCrop.getCropRect();
        int width = r.width();
        int height = r.height();
        Rect dstRect = new Rect(0,0, width, height);

        //Create a bitmap large enough to hold the selection
        croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        //Set the bitmap on the Canvas
        Canvas canvas = new Canvas(croppedImage);

        //Splash the desired portion of the image onto the bitmap
        canvas.drawBitmap(mImage, r, dstRect, null);

        // Release bitmap memory as soon as possible
        mImageView.clear();
        mImage.recycle();

        mImageView.center(true, true);
        mImageView.mRectangleOverlays.clear();
        mImageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, 90), true);

        mSave.setVisibility(View.INVISIBLE);
        mDiscard.setVisibility(View.INVISIBLE);
        mTakePhoto.setVisibility(View.VISIBLE);

        this.finishActivity(REQUEST_CROP_PICTURE);

/*        //Append CROPPED to new image and save it. We can keep both or delete as needed later.
        filePathOld = filePath;
        filePath = "CROPPED_" + filePath;
        new File(filePath);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(filePath); //here is set your file path where you want to save or also here you can set file object directly

            croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mSaving = false;*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //Original imageURI from camera
            //At this point, the image is saved to the device under mediastore's ACTION_IMAGE_CAPTURE,
            //which basically just points to DIRECTORY_PICTURES - which is fine.
            //Images are stored in appended-date-format YYYYMMDD
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                filePath = imageUri.getPath();
                startCrop();
            }
            else if (requestCode == REQUEST_CROP_PICTURE){

            }
        }
    }

    public static void startBackgroundJob(MonitoredActivity activity,
                                          String title, String message, Runnable job, Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        ProgressDialog dialog = ProgressDialog.show(
                activity, title, message, true, false);
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }
}

class BackgroundJob
        extends MonitoredActivity.LifeCycleAdapter implements Runnable {

    private final MonitoredActivity mActivity;
    private final ProgressDialog mDialog;
    private final Runnable mJob;
    private final Handler mHandler;
    private final Runnable mCleanupRunner = new Runnable() {
        public void run() {
            mActivity.removeLifeCycleListener(BackgroundJob.this);
            if (mDialog.getWindow() != null) mDialog.dismiss();
        }
    };

    public BackgroundJob(MonitoredActivity activity, Runnable job,
                         ProgressDialog dialog, Handler handler) {
        mActivity = activity;
        mDialog = dialog;
        mJob = job;
        mActivity.addLifeCycleListener(this);
        mHandler = handler;
    }

    public void run() {
        try {
            mJob.run();
        } finally {
            mHandler.post(mCleanupRunner);
        }
    }


    @Override
    public void onActivityDestroyed(MonitoredActivity activity) {
        // We get here only when the onDestroyed being called before
        // the mCleanupRunner. So, run it now and remove it from the queue
        mCleanupRunner.run();
        mHandler.removeCallbacks(mCleanupRunner);
    }

    @Override
    public void onActivityStopped(MonitoredActivity activity) {
        mDialog.hide();
    }

    @Override
    public void onActivityStarted(MonitoredActivity activity) {
        mDialog.show();
    }
}