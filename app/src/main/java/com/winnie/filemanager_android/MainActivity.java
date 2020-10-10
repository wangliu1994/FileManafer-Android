package com.winnie.filemanager_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.winnie.filemanager_android.common.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
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

        actionImage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            startActivity(intent);
        });
    }


    @OnClick({R.id.ll_yuan_tong_layout, R.id.ll_ji_tu_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_yuan_tong_layout:
                syncPhotos(1);
                break;
            case R.id.ll_ji_tu_layout:
                syncPhotos(2);
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
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(Constant.KEY_TYPE, type);
        startActivity(intent);
    }
}
