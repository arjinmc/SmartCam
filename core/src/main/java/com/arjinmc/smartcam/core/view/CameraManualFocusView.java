package com.arjinmc.smartcam.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.model.CameraManualFocusParams;

/**
 * Manual Focus View  for camera preview
 * Created by Eminem Lo on 11/9/2020.
 * email: arjinmc@hotmail.com
 */
public class CameraManualFocusView extends View {

    private Paint mPaint;
    private CameraManualFocusParams mCameraManualFocusParams;
    private float mTouchX, mTouchY;

    public CameraManualFocusView(Context context) {
        super(context);
        init();
    }

    public CameraManualFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraManualFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraManualFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCameraManualFocusParams == null) {
            return;
        }
        if (mCameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_CIRCLE) {
            canvas.drawCircle(mTouchX
                    , mTouchY
                    , mCameraManualFocusParams.getRadius()
                    , mPaint);
        } else if (mCameraManualFocusParams.getShape() == CameraManualFocusParams.CAMERA_MANUAL_FOCUS_SHAPE_SQUARE) {
            canvas.drawRect(mTouchX - mCameraManualFocusParams.getSize().getWidth() / 2
                    , mTouchY - mCameraManualFocusParams.getSize().getHeight() / 2
                    , mTouchX + mCameraManualFocusParams.getSize().getWidth() / 2
                    , mTouchY + mCameraManualFocusParams.getSize().getHeight() / 2
                    , mPaint);
        }
    }

    private void init() {

        mCameraManualFocusParams = SmartCamConfig.getInstance().getCameraManualFocusParams();
        if (mCameraManualFocusParams == null) {
            mCameraManualFocusParams = new CameraManualFocusParams();
        } else if (mCameraManualFocusParams.getSize() == null) {
            throw new IllegalArgumentException("CameraManualFocusParams size must not be null！");
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCameraManualFocusParams.getBorderWidth());
        mPaint.setColor(mCameraManualFocusParams.getColor());
    }

    /**
     * get current CameraManualFocusParams
     *
     * @return
     */
    public CameraManualFocusParams getCameraManualFocusParams() {
        return mCameraManualFocusParams;
    }

    /**
     * set touch point that user has touched
     *
     * @param x
     * @param y
     */
    public void setTouchPoint(float x, float y) {
        mTouchX = x;
        mTouchY = y;

        postInvalidate();
    }
}
