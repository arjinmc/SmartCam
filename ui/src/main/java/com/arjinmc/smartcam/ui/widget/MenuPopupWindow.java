package com.arjinmc.smartcam.ui.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.recyclerviewdecoration.RecyclerViewGridSpaceItemDecoration;
import com.arjinmc.smartcam.ui.R;

import java.util.Arrays;

/**
 * Menu layout as PopupWindow
 * Created by Eminem Lo on 23/9/2020.
 * email: arjinmc@hotmail.com
 */
public class MenuPopupWindow extends PopupWindow {

    private Context mContext;

    private RecyclerView mRvOption;
    private RecyclerViewAdapter mOptionAdapter;

    private Integer[] mMenus = new Integer[]{
            R.string.smartcam_ui_setting_ratio
            , R.string.smartcam_ui_setting_timer
    };

    public MenuPopupWindow(Context context) {
        mContext = context;
        if (mContext == null) {
            return;
        }
        init();

    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.smartcam_pop_menu, null);
        mRvOption = view.findViewById(R.id.rv_menu_option);
        RecyclerViewStyleHelper.toGridView(mRvOption, 4);
        mRvOption.addItemDecoration(new RecyclerViewGridSpaceItemDecoration.Builder(mContext)
                .margin(1)
                .create());
        mOptionAdapter = new RecyclerViewAdapter<>(mContext, Arrays.asList(mMenus)
                , R.layout.smartcam_pop_menu_item, new RecyclerViewSingleTypeProcessor<Integer>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, int position, Integer optionResId) {
                ImageView ivIcon = holder.getView(R.id.smartcam_iv_menu_icon);
                TextView tvOption = holder.getView(R.id.smartcam_tv_menu_title);

                tvOption.setText(optionResId);
            }
        });

        mOptionAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int optionResId = mMenus[position];
                if (optionResId == R.string.smartcam_ui_setting_ratio) {
                }
                if (optionResId == R.string.smartcam_ui_setting_timer) {

                }
                dismiss();
            }
        });

        mRvOption.setAdapter(mOptionAdapter);

        view.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(0, 0);
        setWidth(view.getMeasuredWidth());
        setHeight(view.getMeasuredHeight());
        setContentView(view);
        setOutsideTouchable(true);

    }

    public void show(View parent) {
        if (isShowing()) {
            return;
        }
        showAtLocation(parent, Gravity.CENTER_HORIZONTAL, 0, 0);
    }
}
