package com.christopheramazurgmail.rtracker.takephoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * Created by Bre on 2016-11-29.
 * Updated by Chris slightly later : >
 */

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

        if ((Math.abs(zoom - getScale()) / zoom) > .2) {
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
