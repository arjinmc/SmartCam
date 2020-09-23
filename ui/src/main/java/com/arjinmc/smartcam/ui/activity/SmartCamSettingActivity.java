package com.arjinmc.smartcam.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arjinmc.smartcam.ui.R;

/**
 * Setting for complex camera view
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private ConstraintLayout mClRatio;
    private TextView mTvRatio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smartcam_act_setting);
        initView();
        initListener();
        initData();
    }


    private void initView() {
        mIvBack = findViewById(R.id.smartcam_iv_back);

        mClRatio = findViewById(R.id.smartcam_cl_ratio);
        mTvRatio = findViewById(R.id.smartcam_tv_ratio);

    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mClRatio.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.smartcam_iv_back) {
            finish();
        }
        if (v.getId() == R.id.smartcam_cl_ratio) {
            //todo show list of camera support radio
        }
    }
}
