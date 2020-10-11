package com.winnie.filemanager_android.net;

import android.content.Context;
import android.content.SharedPreferences;

import com.winnie.filemanager_android.BaseApplication;
import com.winnie.filemanager_android.common.Constant;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class ApiClient {
    private static String sHost;

    private static ApiService sApiService;

    private ApiClient() {
    }

    public static ApiService getService() {
        if (sApiService == null) {
            if (sHost == null) {
                SharedPreferences sp = BaseApplication.getContext()
                        .getSharedPreferences(Constant.SYSTEM_CONFIG_KEY, Context.MODE_PRIVATE);
                String host = sp.getString(Constant.SP_FILE_SERVER_HOST, "192.168.0.8:12000");
                sHost = "http://" + host;
            }
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(sHost)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(OkHttpClientProvider.createOkHttpClient())
                    .build();
            sApiService = retrofit.create(ApiService.class);
        }
        return sApiService;
    }

    public static void reset() {
        sHost = null;
        sApiService = null;
    }
}
