package com.arjinmc.smartcam.ui.widget;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * Bottom view dialog fragment
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class BaseSelectorDialogFragment extends DialogFragment {

    private static final String TAG = "BaseSelectorDialogFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    protected void init(View view) {
        intView(view);
        initListener();
        initData();
    }

    protected void intView(View view) {

    }

    protected void initListener() {

    }

    protected void initData() {

    }

    public void show(FragmentManager fragmentManager) {
        if (fragmentManager == null) {
            return;
        }
//        if (!isHidden()) {
//            return;
//        }
//        if (isAdded()) {
//            dismiss();
//        }
        show(fragmentManager.beginTransaction(), TAG);
        Log.e("show", "show");
    }
}
