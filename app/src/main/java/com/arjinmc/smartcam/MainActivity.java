package com.arjinmc.smartcam;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.recyclerviewdecoration.RecyclerViewItemDecoration;
import com.arjinmc.smartcam.core.SmartCamConfig;
import com.arjinmc.smartcam.core.SmartCamUtils;
import com.arjinmc.smartcam.core.file.SmartCamFileUtils;
import com.arjinmc.smartcam.permission.PermissionAssistant;
import com.arjinmc.smartcam.ui.SmartCamActivity;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String[] mPermissions = new String[]{
            Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE};
    private Integer[] mTitles = new Integer[]{
            R.string.default_ui
            , R.string.prieview_from_new_object
            , R.string.prieview_from_xml};

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_list);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this, android.R.color.darker_gray))
                .thickness(2).create());
        RecyclerViewStyleHelper.toLinearLayout(mRecyclerView, RecyclerView.VERTICAL);
        mAdapter = new RecyclerViewAdapter<>(this, Arrays.asList(mTitles)
                , R.layout.item_main_list, new RecyclerViewSingleTypeProcessor<Integer>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, int position, final Integer integer) {
                ((TextView) holder.itemView).setText(getString(integer));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!SmartCamUtils.hasCamera(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Your phone has no camera!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        switch (integer) {
                            case R.string.default_ui:
                                startActivity(SmartCamActivity.class);
                                break;
                            case R.string.prieview_from_new_object:
                                startActivity(PreviewFromNewActivity.class);
                                break;
                            case R.string.prieview_from_xml:
                                startActivity(PreviewFromXMLActivity.class);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!PermissionAssistant.isGrantedAllPermissions(this, mPermissions)) {
            PermissionAssistant.requestPermissions(this, mPermissions, false);
        } else {
            initDir();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionAssistant.isGrantedAllPermissions(this, mPermissions)) {
            //create file dir for test
            initDir();
        }
    }

    private void initDir() {
        File file = new File(SmartCamFileUtils.getExternalStoreageDir() + File.separator + SmartCamConfig.getRootDirName());
        if (!file.exists() || !file.isDirectory()) {
            file.mkdir();
        }
        SmartCamConfig.setRootDirPath(file.getAbsolutePath());
    }

    private void startActivity(Class clz) {
        startActivity(new Intent(this, clz));
    }
}
