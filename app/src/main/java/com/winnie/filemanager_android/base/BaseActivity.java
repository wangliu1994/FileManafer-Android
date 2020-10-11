package com.winnie.filemanager_android.base;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.winnie.filemanager_android.view.WaitingDialog;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private WaitingDialog mWaitingDialog;

    public void showWaitingDialog() {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(this);
        } else if (mWaitingDialog.isShowing()) {
            return;
        }

        mWaitingDialog.show();
    }

    public void hideWaitingDialog() {
        if (isFinishing()) {
            return;
        }
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
    }
}
