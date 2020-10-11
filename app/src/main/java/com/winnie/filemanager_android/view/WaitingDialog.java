package com.winnie.filemanager_android.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.winnie.filemanager_android.R;

public class WaitingDialog extends Dialog {
    public WaitingDialog(Context context) {
        super(context, R.style.WaitingDialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_waiting);
    }
}
