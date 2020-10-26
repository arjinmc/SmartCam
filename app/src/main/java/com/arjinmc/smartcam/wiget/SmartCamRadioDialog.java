package com.arjinmc.smartcam.wiget;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.arjinmc.expandrecyclerview.adapter.RecyclerViewAdapter;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewSingleTypeProcessor;
import com.arjinmc.expandrecyclerview.adapter.RecyclerViewViewHolder;
import com.arjinmc.expandrecyclerview.style.RecyclerViewStyleHelper;
import com.arjinmc.recyclerviewdecoration.RecyclerViewLinearItemDecoration;
import com.arjinmc.smartcam.R;
import com.arjinmc.smartcam.SmartCamSPManager;
import com.arjinmc.smartcam.SmartCamUIConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for camera radio
 * Created by Eminem Lo on 19/10/2020.
 * email: arjinmc@hotmail.com
 */
public class SmartCamRadioDialog extends BaseSelectorDialogFragment {

    private RecyclerView mRvRatio;
    private RecyclerViewAdapter mRatioAdapter;
    private List<String> mRatioList;
    private int mCurrentPosition;

    private OnSelectedListener mOnSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.smartcam_dialog_ratio, null);
        return view;
    }

    @Override
    protected void intView(View view) {

        mRvRatio = view.findViewById(R.id.smartcam_rv_ratio);
        RecyclerViewStyleHelper.toLinearLayout(mRvRatio, RecyclerView.VERTICAL);
        mRvRatio.addItemDecoration(new RecyclerViewLinearItemDecoration.Builder(getActivity())
                .color(Color.BLACK)
                .thickness(2)
                .create());

        mRatioAdapter = new RecyclerViewAdapter(getActivity(), mRatioList
                , R.layout.smartcam_dialog_ratio_item
                , new RecyclerViewSingleTypeProcessor<String>() {
            @Override
            public void onBindViewHolder(RecyclerViewViewHolder holder, int position, String ratio) {
                TextView tvRatio = holder.getView(R.id.smartcam_tv_ratio);
                ImageView imCheck = holder.getView(R.id.smartcam_iv_check);
                tvRatio.setText(ratio);
                if (mCurrentPosition == position) {
                    imCheck.setVisibility(View.VISIBLE);
                } else {
                    imCheck.setVisibility(View.INVISIBLE);
                }
            }
        });

        mRvRatio.setAdapter(mRatioAdapter);
    }

    @Override
    protected void initData() {
        initCurrentSelectedType();
    }

    @Override
    protected void initListener() {
        mRatioAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                mCurrentPosition = position;
                mRatioAdapter.notifyDataSetChanged();
                SmartCamSPManager.getInstance(getActivity()).setRatio(mRatioList.get(position));
                if (mOnSelectedListener != null) {
                    mOnSelectedListener.onSelected(mRatioList.get(position));
                }
                dismiss();
            }
        });
    }

    public void setData(List<String> ratioList) {
        mRatioList = ratioList;
        if (mRatioList == null) {
            mRatioList = new ArrayList<>();
        }
        mRatioList.add(0, SmartCamUIConstants.RATIO_FIX_WINDOW);
        initCurrentSelectedType();
        if (mRatioAdapter != null) {
            mRatioAdapter.notifyDataChanged(mRatioList);
        }
    }

    private void initCurrentSelectedType() {
        if (getActivity() == null) {
            return;
        }
        String ratio = SmartCamSPManager.getInstance(getActivity()).getRatio();
        if (TextUtils.isEmpty(ratio)) {
            mCurrentPosition = 0;
            return;
        }
        if (mRatioList != null && !mRatioList.isEmpty()) {
            int ratioSize = mRatioList.size();
            for (int i = 0; i < ratioSize; i++) {
                if (ratio.equals(mRatioList.get(i))) {
                    mCurrentPosition = i;
                    break;
                }
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(String ratio);
    }


}
