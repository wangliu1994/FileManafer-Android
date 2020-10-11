package com.winnie.filemanager_android.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by winnie
 *
 * @date : 2020/10/11
 * @desc :
 */
class OkHttpClientProvider {
    private static OkHttpClient sClient;

    private OkHttpClientProvider() {
    }

    static OkHttpClient createOkHttpClient() {
        if (sClient == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpInterceptor());
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addNetworkInterceptor(httpLoggingInterceptor)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS);
            sClient = builder.build();
        }
        return sClient;
    }
}
