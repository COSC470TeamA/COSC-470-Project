package com.christopheramazurgmail.rtracker.takephoto;

import android.content.Context;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.christopheramazurgmail.rtracker.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
    private String TAG = "TakePhotoActivity";
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


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 16) { // >= Gingerbread && < Jelly Bean
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        Intent i = this.getIntent();
        Bundle extras = i.getExtras();
        mImageView = (CropImageView) findViewById(R.id.previewImage);

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
            Util.startBackgroundJob(this, null,
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
            int height = mImageView.getMeasuredHeight();
            int width = mImageView.getMeasuredWidth();
            Rect imageRect = new Rect(0, 0, width, height);
            RectF cropRect = new RectF((width*(1/4)), (height*(1/4)), (width/2), (height/2));
            ro.setup(mImageMatrix, imageRect, cropRect, false, true);
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

        Rect r = mCrop.getCropRect();
        int width = r.height();
        int height = r.width();

        croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(croppedImage);
        Rect dstRect = new Rect(0, 0, mImageView.getWidth(), mImageView.getHeight());
        canvas.drawBitmap(mImage, r, dstRect, null);

        // Release bitmap memory as soon as possible
        mImageView.clear();
        mImage.recycle();

        mImageView.center(true, true);
        mImageView.mRectangleOverlays.clear();
        mImageView.setImageRotateBitmapResetBase(new RotateBitmap(croppedImage, 90), true);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //Original imageURI from camera
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageUri = data.getData();
                startCrop();
            } else if (requestCode == REQUEST_CROP_PICTURE) {
/*                croppedImage = data.getData();
                Bitmap b = data.getExtras().getParcelable("return-data");
                mImageView.setImageURI(croppedImage);
                Intent i = this.getIntent();
                i.putExtra(IMAGEURI, croppedImage);
                //cropReceiptStoreName(croppedImage);
                Intent OCR = new Intent(getApplicationContext(), OCRActivity.class);
                startActivity(OCR);*/
            }
        }
    }
}

class CropImageView extends ImageViewTouchBase {
    ArrayList<RectangleOverlay> mRectangleOverlays = new ArrayList<RectangleOverlay>();
    RectangleOverlay mMotionRectangleOverlay = null;
    float mLastX, mLastY;
    int mMotionEdge;

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (RectangleOverlay ro : mRectangleOverlays) {
                ro.mMatrix.set(getImageMatrix());
                ro.invalidate();
                if (ro.mIsFocused) {
                    centerBasedOnRectangleOverlay(ro);
                }
            }
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (RectangleOverlay ro : mRectangleOverlays) {
            ro.mMatrix.set(getImageMatrix());
            ro.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (RectangleOverlay ro : mRectangleOverlays) {
            ro.mMatrix.set(getImageMatrix());
            ro.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (RectangleOverlay ro : mRectangleOverlays) {
            ro.mMatrix.set(getImageMatrix());
            ro.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay ro = mRectangleOverlays.get(i);
            ro.mMatrix.postTranslate(deltaX, deltaY);
            ro.invalidate();
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) {
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay ro = mRectangleOverlays.get(i);
            ro.setFocus(false);
            ro.invalidate();
        }

        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay ro = mRectangleOverlays.get(i);
            int edge = ro.getHit(event.getX(), event.getY());
            if (edge != RectangleOverlay.GROW_NONE) {
                if (!ro.hasFocus()) {
                    ro.setFocus(true);
                    ro.invalidate();
                }
                break;
            }
        }
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        TakePhotoActivity takePhoto = (TakePhotoActivity) this.getContext();

        if (takePhoto.mSaving) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                recomputeFocus(event);
                for (int i = 0; i < mRectangleOverlays.size(); i++) {
                    RectangleOverlay ro = mRectangleOverlays.get(i);
                    int edge = ro.getHit(event.getX(), event.getY());
                    if (edge != RectangleOverlay.GROW_NONE) {
                        mMotionEdge = edge;
                        mMotionRectangleOverlay = ro;
                        mLastX = event.getX();
                        mLastY = event.getY();
                        mMotionRectangleOverlay.setMode(
                                (edge == RectangleOverlay.MOVE)
                                        ? RectangleOverlay.ModifyMode.Move
                                        : RectangleOverlay.ModifyMode.Grow);
                        break;

                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                recomputeFocus(event);
                for (int i = 0; i < mRectangleOverlays.size(); i++) {
                    RectangleOverlay ro = mRectangleOverlays.get(i);
                    if (ro.hasFocus()) {
                        takePhoto.mCrop = ro;
                        for (int j = 0; j < mRectangleOverlays.size(); j++) {
                            if (j == i) {
                                continue;
                            }
                            mRectangleOverlays.get(j).setHidden(true);
                        }
                        centerBasedOnRectangleOverlay(ro);
                        return true;
                    }

                }
                if (mMotionRectangleOverlay != null) {
                    centerBasedOnRectangleOverlay(mMotionRectangleOverlay);
                    mMotionRectangleOverlay.setMode(
                            RectangleOverlay.ModifyMode.None);
                }
                mMotionRectangleOverlay = null;
                break;

            case MotionEvent.ACTION_MOVE:
                recomputeFocus(event);
                if (mMotionRectangleOverlay != null) {
                    mMotionRectangleOverlay.handleMotion(mMotionEdge,
                            event.getX() - mLastX,
                            event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();
                    ensureVisible(mMotionRectangleOverlay);
                }
                break;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                center(true, true);
                break;
            case MotionEvent.ACTION_MOVE:
                // if we're not zoomed then there's no point in even allowing
                // the user to move the mImage around.  This call to center puts
                // it back to the normalized location (with false meaning don't
                // animate).
                if (getScale() == 1F) {
                    center(true, true);
                }
                break;
        }

        return true;
    }

    // Pan the displayed mImage to make sure the cropping rectangle is visible.
    private void ensureVisible(RectangleOverlay ro) {
        Rect r = ro.mDrawRect;

        int panDeltaX1 = Math.max(0, this.getLeft() - r.left);
        int panDeltaX2 = Math.min(0, this.getRight() - r.right);

        int panDeltaY1 = Math.max(0, this.getTop() - r.top);
        int panDeltaY2 = Math.min(0, this.getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnRectangleOverlay(RectangleOverlay ro) {
        Rect drawRect = ro.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[]{ro.mCropRect.centerX(),
                    ro.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(ro);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            mRectangleOverlays.get(i).draw(canvas);
        }
    }

    public void add(RectangleOverlay ro) {
        mRectangleOverlays.add(ro);
        invalidate();
    }
}
