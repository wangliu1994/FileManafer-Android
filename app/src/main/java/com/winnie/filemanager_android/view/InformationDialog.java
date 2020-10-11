package com.winnie.filemanager_android.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.winnie.filemanager_android.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : winnie
 * @date : 2019/1/10
 * @desc
 */
public class InformationDialog extends Dialog {
    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    private View.OnClickListener mOnClickListener;

    public InformationDialog(@NonNull Context context, String content) {
        super(context);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_infomation);
        ButterKnife.bind(this);

        mTvContent.setText(content);
        mBtnConfirm.setOnClickListener(v -> {
            dismiss();
            if(mOnClickListener != null) {
                mOnClickListener.onClick(v);
            }
        });
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }
}
