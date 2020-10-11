package com.winnie.filemanager_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.winnie.filemanager_android.base.BaseActivity;
import com.winnie.filemanager_android.common.Constant;
import com.winnie.filemanager_android.net.ApiClient;
import com.winnie.filemanager_android.view.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {

    @BindView(R.id.edit_IP)
    ClearEditText mEditIP;
    @BindView(R.id.edit_Port)
    ClearEditText mEditPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getHost();
    }

    @OnClick({R.id.ic_back, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_back:
                finish();
                break;
            case R.id.btn_confirm:
                saveHost();
                break;
        }
    }

    private void saveHost() {
        if (mEditIP.getText() == null || mEditPort.getText() == null) {
            Toast.makeText(this, "请输入IP和端口号", Toast.LENGTH_SHORT).show();
            return;
        }
        String ip = mEditIP.getText().toString();
        String port = mEditPort.getText().toString();
        String host = ip + ":" + port;

        SharedPreferences sp = getSharedPreferences(Constant.SYSTEM_CONFIG_KEY, Context.MODE_PRIVATE);
        sp.edit().putString(Constant.SP_FILE_SERVER_HOST, host).apply();
        ApiClient.reset();
        finish();
    }

    private void getHost() {
        SharedPreferences sp = getSharedPreferences(Constant.SYSTEM_CONFIG_KEY, Context.MODE_PRIVATE);
        String host = sp.getString(Constant.SP_FILE_SERVER_HOST, BuildConfig.HOST);
        if (host == null) {
            return;
        }
        String[] hostStr = host.split(":");
        if (hostStr.length > 1) {
            mEditIP.setText(hostStr[0]);
            mEditPort.setText(hostStr[1]);
        }
    }
}
