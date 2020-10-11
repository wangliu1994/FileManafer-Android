package com.winnie.filemanager_android;

import android.app.Application;
import android.content.Context;

import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class BaseApplication extends Application {

    private static BaseApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        ZXingLibrary.initDisplayOpinion(this);
    }

    public static Context getContext() {
        return mContext;
    }
}
