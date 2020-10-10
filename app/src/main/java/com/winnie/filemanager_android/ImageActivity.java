package com.winnie.filemanager_android;

import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.winnie.filemanager_android.common.Constant;
import com.winnie.filemanager_android.common.ImageUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author : winnie
 * @date : 2020/10/10
 * @desc
 */
public class ImageActivity extends AppCompatActivity {

    @BindView(R.id.image_view)
    PhotoView imageView;

    private String mPhotoPath;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_image);
        ButterKnife.bind(this);

        mPhotoPath = getIntent().getStringExtra(Constant.KEY_PATH);
        mType = getIntent().getIntExtra(Constant.KEY_TYPE, -1);

        if (mPhotoPath == null) {
            return;
        }
        imageView.setImageBitmap(ImageUtils.getBitMapFromPath(this, mPhotoPath));
    }

    @OnClick(R.id.ic_back)
    public void onClick() {
        finish();
    }
}
