package com.winnie.filemanager_android;

import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;

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
            mPhotoPath = "/storage/emulated/0/Pictures/1602309669776.jpg";
        }
        imageView.setImageBitmap(ImageUtils.getBitMapFromPath(this, mPhotoPath));
    }

    @OnClick(R.id.ic_back)
    public void onClick() {
        finish();
    }
}
