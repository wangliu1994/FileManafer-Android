package com.winnie.filemanager_android.net;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpInterceptor implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.i("HttpLogInfo", message);
    }
}
