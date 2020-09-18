package com.arjinmc.smartcam.core;

import android.animation.ValueAnimator;
import android.content.Context;
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
    private CameraManualFocusView mCameraManualFocusView;
    private OnManualFocusListener mOnManualFocusListener;
    private OnCaptureAnimationLister mOnCaptureAnimationLister;
    private float mTouchX, mTouchY;
    private ValueAnimator mCaptureAnimation;

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
                return getFocusRect();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCurrentCameraVersion = CameraVersion.VERSION_2;
        } else {
            mCurrentCameraVersion = CameraVersion.VERSION_1;
        }
        if (DebugConfig.useV1) {
            mCurrentCameraVersion = CameraVersion.VERSION_1;
        }

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
    }

    /**
     * set fixed size for preview
     *
     * @param width
     * @param height
     */
    public void setFixedSize(int width, int height) {
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.getHolder().setFixedSize(width, height);
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

    public Rect getFocusRect() {
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
            return rect;
        } else if (cameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_SQUARE) {
            rect.left = (int) (mTouchX - cameraManualFocusParams.getSize().getWidth() / 2);
            rect.top = (int) (mTouchY - cameraManualFocusParams.getSize().getHeight() / 2);
            rect.right = (int) (mTouchX + cameraManualFocusParams.getSize().getWidth() / 2);
            rect.bottom = (int) (mTouchY + cameraManualFocusParams.getSize().getHeight() / 2);
            return rect;
        }
        return null;
    }

    private void playCaptureAnimation() {

        if (mCaptureAnimation == null) {
            mCaptureAnimation = ValueAnimator.ofInt(0, 100).setDuration(
                    SmartCamConfig.getInstance().getCaptureAnimationDuration());
            mCaptureAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    //set camera preview alpha
                    getChildAt(0).setAlpha(value / 100f);
                }
            });
        }
        mCaptureAnimation.start();
    }

    private void stopCaptureAnimation() {

        mCaptureAnimation.cancel();
        //set camera preview alpha
        getChildAt(0).setAlpha(1);
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
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.destroy();
        } else if (mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    public void startPreview() {
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.startPreview();
        } else if (mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCamera2Preview.startPreview();
        }
    }

    public void stopPreview() {
        if (mCurrentCameraVersion == CameraVersion.VERSION_1) {
            mCamera1Preview.stopPreview();
        } else if (mCurrentCameraVersion == CameraVersion.VERSION_2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCamera2Preview.stopPreview();
        }
    }

    private boolean isUsedCamera2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && mCurrentCameraVersion == CameraVersion.VERSION_2;
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


