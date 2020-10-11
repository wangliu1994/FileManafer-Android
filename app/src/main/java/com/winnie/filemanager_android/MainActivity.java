package com.winnie.filemanager_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.winnie.filemanager_android.base.BaseActivity;
import com.winnie.filemanager_android.common.Constant;
import com.winnie.filemanager_android.common.Result;
import com.winnie.filemanager_android.model.FileUploadResDTO;
import com.winnie.filemanager_android.net.ApiClient;
import com.winnie.filemanager_android.view.InformationDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    @BindView(R.id.action_image)
    ImageView actionImage;
    @BindView(R.id.ll_yuan_tong_layout)
    LinearLayout llYuanTongLayout;
    @BindView(R.id.ll_ji_tu_layout)
    LinearLayout llJiTuLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({
            R.id.ll_yuan_tong_layout,
            R.id.ll_ji_tu_layout,
            R.id.action_image,
            R.id.ic_setting
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_yuan_tong_layout:
                syncPhotos(1);
                break;
            case R.id.ll_ji_tu_layout:
                syncPhotos(2);
                break;
            case R.id.action_image:
                Toast.makeText(MainActivity.this, "欢迎使用面单归档助手", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ic_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            default:
                break;
        }
    }


    /**
     * 启动同步面单页面
     *
     * @param type 1：圆通 2：极兔
     */
    private void syncPhotos(int type) {
        testServer(type);
    }

    private void testServer(int type) {
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
                            InformationDialog dialog = new InformationDialog(MainActivity.this, content);
                            dialog.show();
                            return;
                        }
                        Intent intent = new Intent(MainActivity.this, FormActivity.class);
                        intent.putExtra(Constant.KEY_TYPE, type);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("HttpLogInfo", e.getMessage());
                        String content = "连接不上电脑，请检查电脑是否启动";
                        InformationDialog dialog = new InformationDialog(MainActivity.this, content);
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
