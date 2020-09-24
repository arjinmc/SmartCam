package com.arjinmc.smartcam.core;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.camera1.Camera1Preview;
import com.arjinmc.smartcam.core.camera1.Camera1Wrapper;
import com.arjinmc.smartcam.core.camera2.Camera2Preview;
import com.arjinmc.smartcam.core.camera2.Camera2Wrapper;
import com.arjinmc.smartcam.core.model.CameraAspectRatio;
import com.arjinmc.smartcam.core.model.CameraManualFocusParams;
import com.arjinmc.smartcam.core.model.CameraSize;
import com.arjinmc.smartcam.core.model.CameraVersion;
import com.arjinmc.smartcam.core.view.CameraManualFocusView;

/**
 * SmartCamPreview
 * Preview for SmartCam
 * Created by Eminem Lo on 2019-10-14.
 * email: arjinmc@hotmail.com
 */
public class SmartCamPreview extends FrameLayout {

    private int mCurrentCameraVersion;
    private SmartCam mSmartCam;
    private Camera1Preview mCamera1Preview;
    private Camera2Preview mCamera2Preview;
    private View mCaptureAnimationView;
    private CameraManualFocusView mCameraManualFocusView;
    private OnManualFocusListener mOnManualFocusListener;
    private OnCaptureAnimationLister mOnCaptureAnimationLister;
    private float mTouchX, mTouchY;
    private ValueAnimator mCaptureAnimation;
    private boolean isPlayingCaptureAnimation = false;

    public SmartCamPreview(@NonNull Context context) {
        super(context);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartCamPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCamera(SmartCam smartCam) {
        init(smartCam);
    }

    private void init(SmartCam smartCam) {

        if (smartCam == null) {
            return;
        }

        mSmartCam = smartCam;

        if (getChildCount() != 0) {
            removeAllViews();
        }

        mOnManualFocusListener = new OnManualFocusListener() {
            @Override
            public void requestFocus(float x, float y) {
                showManualFocusView(x, y);
            }

            @Override
            public void cancelFocus() {
                hideManualFocusView();
            }

            @Override
            public Rect getFocusRegion() {
                if (isUsedCamera2()) {
                    return getFocusRectForCamera2();
                }
                return getFocusRectForCamera1();
            }
        };

        mOnCaptureAnimationLister = new OnCaptureAnimationLister() {
            @Override
            public void onPlay() {
                playCaptureAnimation();
            }

            @Override
            public void onStop() {
                stopCaptureAnimation();
            }
        };

        mCurrentCameraVersion = mSmartCam.getCameraVersion();

        if (isUsedCamera2()) {
            mCamera2Preview = new Camera2Preview(getContext(), (Camera2Wrapper) mSmartCam.getCameraWrapper());
            mCamera2Preview.setOnCaptureAnimationListener(mOnCaptureAnimationLister);
            addView(mCamera2Preview);
        } else {
            mCamera1Preview = new Camera1Preview(getContext(), (Camera1Wrapper) mSmartCam.getCameraWrapper());
            mCamera1Preview.setOnCaptureAnimationListener(mOnCaptureAnimationLister);
            addView(mCamera1Preview);
        }

        if (SmartCamConfig.getInstance().isUseManualFocus()) {
            mCameraManualFocusView = new CameraManualFocusView(getContext());
            mCameraManualFocusView.setVisibility(View.GONE);
            addView(mCameraManualFocusView, new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if (isUsedCamera2()) {
                mCamera2Preview.setOnManualFocusListener(mOnManualFocusListener);
            } else {
                mCamera1Preview.setOnManualFocusListener(mOnManualFocusListener);
            }
        }

        if (mSmartCam.getPreviewRatio() != null) {
            int width = getMeasuredWidth();
            CameraAspectRatio ratio = new CameraAspectRatio();
            ratio.parse(mSmartCam.getPreviewRatio());
            if (ratio.isValid()) {
                getPreview().setLayoutParams(new FrameLayout.LayoutParams(width, width * ratio.getX() / ratio.getY()));
            }
        }
    }

    /**
     * hide manual focus view
     */
    public void hideManualFocusView() {
        if (mCameraManualFocusView != null && mCameraManualFocusView.getVisibility() == View.VISIBLE) {
            mCameraManualFocusView.setVisibility(View.GONE);
        }
    }

    /**
     * show manual focus view
     *
     * @param x
     * @param y
     */
    public void showManualFocusView(float x, float y) {

        if (mCameraManualFocusView != null) {

            //check border
            CameraManualFocusParams cameraManualFocusParams = mCameraManualFocusView.getCameraManualFocusParams();
            if (cameraManualFocusParams == null) {
                return;
            }

            boolean canShow = true;
            if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE) {
                if (x - cameraManualFocusParams.getRadius() < 0
                        || x + cameraManualFocusParams.getRadius() > getMeasuredWidth()
                        || y - cameraManualFocusParams.getRadius() < 0
                        || y + cameraManualFocusParams.getRadius() > getMeasuredHeight()) {

                    canShow = false;
                }
            } else if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_SQUARE) {
                CameraSize shapeSize = cameraManualFocusParams.getSize();
                if (shapeSize != null) {
                    if (x - shapeSize.getWidth() / 2 < 0
                            || x + shapeSize.getWidth() / 2 > getMeasuredWidth()
                            || y - shapeSize.getHeight() / 2 < 0
                            || y + shapeSize.getHeight() / 2 > getMeasuredHeight()) {
                        canShow = false;
                    }
                }
            }

            if (canShow) {
                mTouchX = x;
                mTouchY = y;
                mCameraManualFocusView.setTouchPoint(x, y);
                if (mCameraManualFocusView.getVisibility() != View.VISIBLE) {
                    mCameraManualFocusView.setVisibility(View.VISIBLE);
                }
            } else {
                mTouchX = -1;
                mTouchY = -1;
                if (mCameraManualFocusView.getVisibility() == View.VISIBLE) {
                    mCameraManualFocusView.setVisibility(View.GONE);
                }
            }
        }
    }

    public Rect getFocusRectForCamera2() {
        if (mCameraManualFocusView == null
                || mCameraManualFocusView.getVisibility() != View.VISIBLE
                || mCameraManualFocusView.getCameraManualFocusParams() == null) {
            return null;
        }
        CameraManualFocusParams cameraManualFocusParams = mCameraManualFocusView.getCameraManualFocusParams();
        Rect rect = new Rect();
        if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE) {
            rect.left = (int) (mTouchX - cameraManualFocusParams.getRadius());
            rect.top = (int) (mTouchY - cameraManualFocusParams.getRadius());
            rect.right = (int) (mTouchX + cameraManualFocusParams.getRadius());
            rect.bottom = (int) (mTouchY + cameraManualFocusParams.getRadius());
//            SmartCamLog.e("touch rect", rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom);
            return rect;
        } else if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_SQUARE) {
            rect.left = (int) (mTouchX - cameraManualFocusParams.getSize().getWidth() / 2);
            rect.top = (int) (mTouchY - cameraManualFocusParams.getSize().getHeight() / 2);
            rect.right = (int) (mTouchX + cameraManualFocusParams.getSize().getWidth() / 2);
            rect.bottom = (int) (mTouchY + cameraManualFocusParams.getSize().getHeight() / 2);
//            SmartCamLog.e("touch rect",rect.left+","+rect.top+","+rect.right+","+rect.bottom);
            return rect;
        }
        return null;
    }

    private Rect getFocusRectForCamera1() {

        if (mCameraManualFocusView == null) {
            return null;
        }
        CameraManualFocusParams cameraManualFocusParams = mCameraManualFocusView.getCameraManualFocusParams();
        int areaWidth = 0, areaHeight = 0;
        if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE) {
            areaWidth = (int) cameraManualFocusParams.getRadius();
            areaHeight = areaWidth;
        } else if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_SQUARE) {
            areaWidth = cameraManualFocusParams.getSize().getWidth();
            areaHeight = cameraManualFocusParams.getSize().getHeight();
        }
        if (areaWidth != 0 && areaHeight != 0) {
            int left = coverAreaCoordinateForCamera1(mTouchY / getMeasuredHeight(), areaWidth);
            int top = coverAreaCoordinateForCamera1((getMeasuredWidth() - mTouchX) / getMeasuredWidth(), areaHeight);
            return new Rect(left, top, left + areaWidth, top + areaHeight);
        }
        return null;
    }

    /**
     * cover area coordinate for camera1
     *
     * @param point
     * @param focusAreaSize
     * @return
     */
    private static int coverAreaCoordinateForCamera1(float point, int focusAreaSize) {
        int result;
        int touchCoordinateInCameraReper = Float.valueOf(point * 2000 - 1000).intValue();
        if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize > 1000) {
            if (touchCoordinateInCameraReper > 0) {
                result = 1000 - focusAreaSize;
            } else {
                result = -1000 + focusAreaSize;
            }
        } else {
            result = touchCoordinateInCameraReper - focusAreaSize / 2;
        }
        return result;
    }


    private void playCaptureAnimation() {

        if (isPlayingCaptureAnimation) {
            return;
        }
        if (mCaptureAnimation == null) {
            mCaptureAnimation = ValueAnimator.ofInt(0, 100).setDuration(
                    SmartCamConfig.getInstance().getCaptureAnimationDuration());
            mCaptureAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    //set camera preview alpha
                    mCaptureAnimationView.setAlpha(value / 100f);
                }
            });
            mCaptureAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    removeView(mCaptureAnimationView);
                    isPlayingCaptureAnimation = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    removeView(mCaptureAnimationView);
                    isPlayingCaptureAnimation = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            mCaptureAnimationView = new View(getContext());
            mCaptureAnimationView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            mCaptureAnimationView.setBackgroundColor(Color.BLACK);
        }
        mCaptureAnimation.start();
        addView(mCaptureAnimationView);
        isPlayingCaptureAnimation = true;

    }

    private void stopCaptureAnimation() {

        mCaptureAnimation.cancel();
    }

    /**
     * keep screen on
     *
     * @param screenOn
     */
    @Override
    public void setKeepScreenOn(boolean screenOn) {
        super.setKeepScreenOn(screenOn);
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.getHolder().setKeepScreenOn(screenOn);
        }
    }

    public void resume() {
        init(mSmartCam);
    }

    public void pause() {
        if (isUsedCamera1()) {
            mCamera1Preview.destroy();
        } else if (isUsedCamera2()) {
            mCamera2Preview.destroy();
        }
    }

    public void release() {
        if (mCaptureAnimation != null) {
            mCaptureAnimation.cancel();
            mCaptureAnimation = null;
        }
        pause();
    }

    public void restart() {
        pause();
        resume();
    }

    public void startPreview() {
        if (isUsedCamera1()) {
            mCamera1Preview.startPreview();
        } else if (isUsedCamera2()) {
            mCamera2Preview.startPreview();
        }
    }

    public void stopPreview() {
        if (isUsedCamera1()) {
            mCamera1Preview.stopPreview();
        } else if (isUsedCamera2()) {
            mCamera2Preview.stopPreview();
        }
    }

    private View getPreview() {
        if (isUsedCamera2()) {
            return mCamera2Preview;
        }
        return mCamera1Preview;
    }

    private boolean isUsedCamera1() {
        return mCurrentCameraVersion == CameraVersion.VERSION_1;
    }

    private boolean isUsedCamera2() {
        return mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public interface OnManualFocusListener {
        void requestFocus(float x, float y);

        void cancelFocus();

        Rect getFocusRegion();
    }

    /**
     * animation listener
     */
    public interface OnCaptureAnimationLister {

        void onPlay();

        void onStop();
    }
}


