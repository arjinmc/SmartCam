package com.arjinmc.smartcam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arjinmc.smartcam.R;
import com.arjinmc.smartcam.SmartCamSPManager;
import com.arjinmc.smartcam.SmartCamUIConstants;
import com.arjinmc.smartcam.wiget.SmartCamRadioDialog;

import java.util.ArrayList;

/**
 * Setting for complex camera view
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mIvBack;
    private ConstraintLayout mClRatio;
    private TextView mTvRatio;
    private ArrayList<String> mRatioList;
    private SmartCamRadioDialog mRadioDialog;

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

        mRadioDialog = new SmartCamRadioDialog();
    }

    private void initListener() {
        mIvBack.setOnClickListener(this);
        mClRatio.setOnClickListener(this);

        mRadioDialog.setOnSelectedListener(new SmartCamRadioDialog.OnSelectedListener() {
            @Override
            public void onSelected(String ratio) {
                mTvRatio.setText(ratio);
            }
        });
    }

    private void initData() {

        Intent intent = getIntent();
        mRatioList = intent.getStringArrayListExtra(SmartCamUIConstants.BUNDLE_RATIO_LIST);
        mRadioDialog.setData(mRatioList);

        mTvRatio.setText(SmartCamSPManager.getInstance(this).getRatio());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.smartcam_iv_back) {
            finish();
        }
        if (v.getId() == R.id.smartcam_cl_ratio) {
            mRadioDialog.show(getSupportFragmentManager());
            return;
        }
    }
}
