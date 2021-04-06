package com.winnie.filemanager_android;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.winnie.filemanager_android.common.Constant;
import com.winnie.filemanager_android.common.Result;
import com.winnie.filemanager_android.net.ApiClient;
import com.winnie.filemanager_android.view.InformationDialog;
import com.winnie.filemanager_android.view.WaitingDialog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class BaseApplication extends Application {

    private static BaseApplication mContext;
    private WaitingDialog mWaitingDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ZXingLibrary.initDisplayOpinion(this);
    }

    public static BaseApplication getContext() {
        return mContext;
    }

    public void showWaitingDialog() {
        if (mWaitingDialog == null) {
            mWaitingDialog = new WaitingDialog(this);
        } else if (mWaitingDialog.isShowing()) {
            return;
        }

        mWaitingDialog.show();
    }

    public void hideWaitingDialog() {
        if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
            mWaitingDialog.dismiss();
        }
    }

    public void testServer(int type) {
        ApiClient
                .getService()
                .testServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<Result<Boolean>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showWaitingDialog();
                    }

                    @Override
                    public void onNext(Response<Result<Boolean>> response) {
                        if (response == null || response.body() == null) {
                            String content = "连接不上电脑，请检查电脑是否启动";
                            InformationDialog dialog = new InformationDialog(BaseApplication.this, content);
                            dialog.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("HttpLogInfo", e.getMessage());
                        String content = "连接不上电脑，请检查电脑是否启动";
                        InformationDialog dialog = new InformationDialog(BaseApplication.this, content);
                        dialog.show();
                        hideWaitingDialog();
                    }

                    @Override
                    public void onComplete() {
                        hideWaitingDialog();
                    }
                });
    }
}
