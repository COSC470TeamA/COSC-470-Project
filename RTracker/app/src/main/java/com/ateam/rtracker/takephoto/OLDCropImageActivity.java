/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*



package com.christopheramazurgmail.rtracker.takephoto;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.christopheramazurgmail.rtracker.R;
import com.christopheramazurgmail.rtracker.takephoto.gallery.IImage;
import com.christopheramazurgmail.rtracker.takephoto.gallery.IImageList;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


*/
/**
 * The activity can crop specific region of interest from an mImage.
 *//*

public class OLDCropImageActivity extends MonitoredActivity {
    private static final String TAG = "OLDCropImageActivity";

    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat =
            Bitmap.CompressFormat.JPEG; // only used with mSaveUri
    private int mOutputQuality = 100; // only used with mSaveUri and JPEG format
    private Uri mSaveUri = null;
    private boolean mSetWallpaper = false;
    private int mAspectX, mAspectY;
    private boolean mDoFaceDetection = false;
    private boolean mCircleCrop = false;
    private final Handler mHandler = new Handler();

    // These options specifiy the output mImage size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;

    boolean mWaitingToPick; // Whether we are wait the user to pick a face.
    boolean mSaving;  // Whether the "save" button is already clicked.

    private OLDCropImageView mImageView;
    private ContentResolver mContentResolver;

    private Bitmap mImage;
    RectangleOverlay mCrop;

    private IImageList mAllImages;
    private IImage mImage;

    private int mOutlineColor;
    private int mOutlineCircleColor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mContentResolver = getContentResolver();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        mImageView = (OLDCropImageView) findViewById(R.id.previewImage);

        // Work-around for devices incapable of using hardware-accelerated clipPath.
        // (android.view.GLES20Canvas.clipPath)
        //
        // See also:
        // - https://code.google.com/p/android/issues/detail?id=20474
        // - https://github.com/lvillani/android-cropimage/issues/20
        //
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 16) { // >= Gingerbread && < Jelly Bean
            mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        //fuck circle crop y even
        if (extras != null) {
            if (extras.getBoolean("circleCrop", false)) {
                mCircleCrop = false;
                mAspectX = 1;
                mAspectY = 1;
                mOutputFormat = Bitmap.CompressFormat.PNG;
            }
            mSaveUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
            if (mSaveUri != null) {
                String outputFormatString = extras.getString("outputFormat");
                if (outputFormatString != null) {
                    mOutputFormat = Bitmap.CompressFormat.valueOf(
                            outputFormatString);
                }
                mOutputQuality = extras.getInt("outputQuality", 100);
            } else {
                mSetWallpaper = extras.getBoolean("setWallpaper");
            }
            mImage = (Bitmap) extras.getParcelable("data");
            mAspectX = extras.getInt("aspectX");
            mAspectY = extras.getInt("aspectY");
            mOutputX = extras.getInt("outputX");
            mOutputY = extras.getInt("outputY");
            mOutlineColor = extras.getInt("outlineColor", RectangleOverlay.DEFAULT_OUTLINE_COLOR);
            mOutlineCircleColor = extras.getInt("outlineCircleColor", RectangleOverlay.DEFAULT_OUTLINE_CIRCLE_COLOR);
            mScale = extras.getBoolean("scale", true);
            mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
            mDoFaceDetection = extras.containsKey("noFaceDetection")
                    ? !extras.getBoolean("noFaceDetection")
                    : true;
        }
        //fuckin circles

        if (mImage == null) {
            Uri target = intent.getData();
            mAllImages = ImageManager.makeImageList(mContentResolver, target,
                    ImageManager.SORT_ASCENDING);
            mImage = mAllImages.getImageForUri(target);
            if (mImage != null) {
                // Don't read in really large bitmaps. Use the (big) thumbnail
                // instead.
                // TODO when saving the resulting bitmap use the
                // decode/crop/encode api so we don't lose any resolution.
                mImage = mImage.thumbBitmap(IImage.ROTATE_AS_NEEDED);
            }
        }

        if (mImage == null) {
            finish();
            return;
        }

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        startFaceDetection();
    }

    //TODO: Can probably delete this but not sure yet
    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mImage, true);

        Util.startBackgroundJob(this, null,
                getResources().getString(R.string.runningFaceDetection),
                new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                final Bitmap b = (mImage != null)
                        ? mImage.fullSizeBitmap(IImage.UNCONSTRAINED,
                        1024 * 1024)
                        : mImage;
                mHandler.post(new Runnable() {
                    public void run() {
                        if (b != mImage && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mImage.recycle();
                            mImage = b;
                        }
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    private void onSaveClicked() {
        if (mCrop == null) {
            return;
        }
        if (mSaving) return;
        mSaving = true;

        Bitmap croppedImage;
            Rect r = mCrop.getCropRect();

            int width = r.width();
            int height = r.height();

            // If we are circle cropping, we want alpha channel, which is the
            // third param here.
            croppedImage = Bitmap.createBitmap(width, height,
                    mCircleCrop
                    ? Bitmap.Config.ARGB_8888
                    : Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mImage, r, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mImage.recycle();

            // If the required dimension is specified, scale the mImage.
            if (mOutputX != 0 && mOutputY != 0 && mScale) {
                croppedImage = Util.transform(new Matrix(), croppedImage,
                        mOutputX, mOutputY, mScaleUp, Util.RECYCLE_INPUT);
            }
        }

        mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.mRectangleOverlays.clear();

        // Return the cropped mImage directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null && (myExtras.getParcelable("data") != null
                || myExtras.getBoolean("return-data"))) {
            Bundle extras = new Bundle();
            extras.putParcelable("data", croppedImage);
            setResult(RESULT_OK,
                    (new Intent()).setAction("inline-data").putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            final int msdId = mSetWallpaper
                    ? R.string.wallpaper
                    : R.string.savingImage;
            Util.startBackgroundJob(this, null,
                    getResources().getString(msdId),
                    new Runnable() {
                public void run() {
                    saveOutput(b);
                }
            }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {
        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                grantUriPermission("com.christopheramazurgmail.rtracker.takephoto", mSaveUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, mOutputQuality, outputStream);
                }
            } catch (IOException ex) {
                // TODO: report error to caller
                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
            } finally {
                Util.closeSilently(outputStream);
            }
            Bundle extras = new Bundle();
            setResult(RESULT_OK, new Intent(mSaveUri.toString())
                    .putExtras(extras));
        } else if (mSetWallpaper) {
            try {
                WallpaperManager.getInstance(this).setBitmap(croppedImage);
                setResult(RESULT_OK);
            } catch (IOException e) {
                Log.e(TAG, "Failed to set wallpaper.", e);
                setResult(RESULT_CANCELED);
            }
        } else  {
            //If filename exists, go to the directory and append a _number to the filename
            Bundle extras = new Bundle();
            extras.putString("rect", mCrop.getCropRect().toString());

            File oldPath = new File(mImage.getDataPath());
            File directory = new File(oldPath.getParent());

            int x = 0;
            String fileName = oldPath.getName();
            fileName = fileName.substring(0, fileName.lastIndexOf("."));

            // Try file-1.jpg, file-2.jpg, ... until we find a filename which
            // does not exist yet.
            while (true) {
                x += 1;
                String candidate = directory.toString()
                        + "/" + fileName + "-" + x + ".jpg";
                boolean exists = (new File(candidate)).exists();
                if (!exists) {
                    break;
                }
            }
            //add to the mImage manager
            try {
                int[] degree = new int[1];
                Uri newUri = ImageManager.addImage(
                        mContentResolver,
                        mImage.getTitle(),
                        mImage.getDateTaken(),
                        null,    // TODO this null is going to cause us to lose
                                 // the location (gps).
                        directory.toString(), fileName + "-" + x + ".jpg",
                        croppedImage, null,
                        degree);

                setResult(RESULT_OK, new Intent()
                        .setAction(newUri.toString())
                        .putExtras(extras));
            } catch (Exception ex) {
                // basically ignore this or put up
                // some ui saying we failed
                Log.e(TAG, "store mImage fail, continue anyway", ex);
            }
        }

        final Bitmap b = croppedImage;
        mHandler.post(new Runnable() {
            public void run() {
                mImageView.clear();
                b.recycle();
            }
        });

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mAllImages != null) {
            mAllImages.close();
        }
        super.onDestroy();
    }

    //don't need
    Runnable mRunFaceDetection = new Runnable() {
        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;

        //Default functionality
        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            RectangleOverlay hv = new RectangleOverlay(mImageView, mOutlineColor, mOutlineCircleColor);

            int width = mImage.getWidth();
            int height = mImage.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
                     mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();
            mHandler.post(new Runnable() {
                public void run() {
                        makeDefault();
                    mImageView.invalidate();
                    if (mImageView.mRectangleOverlays.size() == 1) {
                        mCrop = mImageView.mRectangleOverlays.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };
}

class OLDCropImageView extends ImageViewTouchBase {
    ArrayList<RectangleOverlay> mRectangleOverlays = new ArrayList<RectangleOverlay>();
    RectangleOverlay mMotionRectangleOverlay = null;
    float mLastX, mLastY;
    int mMotionEdge;

    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (RectangleOverlay hv : mRectangleOverlays) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnRectangleOverlay(hv);
                }
            }
        }
    }

    public OLDCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (RectangleOverlay hv : mRectangleOverlays) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (RectangleOverlay hv : mRectangleOverlays) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (RectangleOverlay hv : mRectangleOverlays) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay hv = mRectangleOverlays.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    // According to the event's position, change the focus to the first
    // hitting cropping rectangle.
    private void recomputeFocus(MotionEvent event) {
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay hv = mRectangleOverlays.get(i);
            hv.setFocus(false);
            hv.invalidate();
        }

        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            RectangleOverlay hv = mRectangleOverlays.get(i);
            int edge = hv.getHit(event.getX(), event.getY());
            if (edge != RectangleOverlay.GROW_NONE) {
                if (!hv.hasFocus()) {
                    hv.setFocus(true);
                    hv.invalidate();
                }
                break;
            }
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        OLDCropImageActivity OLDCropImageActivity = (OLDCropImageActivity) this.getContext();
        if (OLDCropImageActivity.mSaving) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (OLDCropImageActivity.mWaitingToPick) {
                    recomputeFocus(event);
                } else {
                    for (int i = 0; i < mRectangleOverlays.size(); i++) {
                        RectangleOverlay hv = mRectangleOverlays.get(i);
                        int edge = hv.getHit(event.getX(), event.getY());
                        if (edge != RectangleOverlay.GROW_NONE) {
                            mMotionEdge = edge;
                            mMotionRectangleOverlay = hv;
                            mLastX = event.getX();
                            mLastY = event.getY();
                            mMotionRectangleOverlay.setMode(
                                    (edge == RectangleOverlay.MOVE)
                                    ? RectangleOverlay.ModifyMode.Move
                                    : RectangleOverlay.ModifyMode.Grow);
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (OLDCropImageActivity.mWaitingToPick) {
                    for (int i = 0; i < mRectangleOverlays.size(); i++) {
                        RectangleOverlay hv = mRectangleOverlays.get(i);
                        if (hv.hasFocus()) {
                            OLDCropImageActivity.mCrop = hv;
                            for (int j = 0; j < mRectangleOverlays.size(); j++) {
                                if (j == i) {
                                    continue;
                                }
                                mRectangleOverlays.get(j).setHidden(true);
                            }
                            centerBasedOnRectangleOverlay(hv);
                            ((OLDCropImageActivity) this.getContext()).mWaitingToPick = false;
                            return true;
                        }
                    }
                } else if (mMotionRectangleOverlay != null) {
                    centerBasedOnRectangleOverlay(mMotionRectangleOverlay);
                    mMotionRectangleOverlay.setMode(
                            RectangleOverlay.ModifyMode.None);
                }
                mMotionRectangleOverlay = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (OLDCropImageActivity.mWaitingToPick) {
                    recomputeFocus(event);
                } else if (mMotionRectangleOverlay != null) {
                    mMotionRectangleOverlay.handleMotion(mMotionEdge,
                            event.getX() - mLastX,
                            event.getY() - mLastY);
                    mLastX = event.getX();
                    mLastY = event.getY();

                    if (true) {
                        // This section of code is optional. It has some user
                        // benefit in that moving the crop rectangle against
                        // the edge of the screen causes scrolling but it means
                        // that the crop rectangle is no longer fixed under
                        // the user's finger.
                        ensureVisible(mMotionRectangleOverlay);
                    }
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
    private void ensureVisible(RectangleOverlay hv) {
        Rect r = hv.mDrawRect;

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
    private void centerBasedOnRectangleOverlay(RectangleOverlay hv) {
        Rect drawRect = hv.mDrawRect;

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
            float [] coordinates = new float[] {hv.mCropRect.centerX(),
                                                hv.mCropRect.centerY()};
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mRectangleOverlays.size(); i++) {
            mRectangleOverlays.get(i).draw(canvas);
        }
    }

    public void add(RectangleOverlay hv) {
        mRectangleOverlays.add(hv);
        invalidate();
    }
}*/
