package com.winnie.filemanager_android;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //圆通调用相机
    private final static int REQUEST_CODE_CAMERA_YT = 100000;
    //极兔调用相机
    private final static int REQUEST_CODE_CAMERA_JT= 200000;
    //圆通-type
    private final static int TYPE_YT = 1;
    //极兔-type
    private final static int TYPE_JT= 2;

    private Uri photoUri;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA_YT && resultCode == AppCompatActivity.RESULT_OK) {
            ContentResolver cr = getContentResolver();
            if (photoUri == null)
                return;
            //按 刚刚指定 的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径
            Cursor cursor = cr.query(photoUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(1);
                    //获得图片
                    Bitmap bp = getBitMapFromPath(path);
                    System.out.println("YT获取到相机返回");
                }
                cursor.close();
            }
            photoUri = null;
        }

        if (requestCode == REQUEST_CODE_CAMERA_JT && resultCode == AppCompatActivity.RESULT_OK) {
            ContentResolver cr = getContentResolver();
            if (photoUri == null)
                return;
            //按 刚刚指定 的那个文件名，查询数据库，获得更多的 照片信息，比如 图片的物理绝对路径
            Cursor cursor = cr.query(photoUri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    String path = cursor.getString(1);
                    //获得图片
                    Bitmap bp = getBitMapFromPath(path);
                    System.out.println("JT获取到相机返回");
                }
                cursor.close();
            }
            photoUri = null;
        }
    }

    @OnClick({R.id.ll_yuan_tong_layout, R.id.ll_ji_tu_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_yuan_tong_layout:
                OnOpenCamera(1);
                break;
            case R.id.ll_ji_tu_layout:
                OnOpenCamera(2);
                break;
            default:
                break;
        }
    }


    /**
     * 打开相机
     *
     * @param type 1：圆通 2：极兔
     */
    private void OnOpenCamera(int type) {
        //向  MediaStore.Images.Media.EXTERNAL_CONTENT_URI 插入一个数据，那么返回标识ID。
        //在完成拍照后，新的照片会以此处的photoUri命名. 其实就是指定了个文件名
        ContentValues values = new ContentValues();
        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //准备intent，并 指定 新 照片 的文件名（photoUri）
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
        //启动拍照的窗体。并注册 回调处理。
        int requestCode = 0;
        if(type == TYPE_YT){
            requestCode = REQUEST_CODE_CAMERA_YT;
        }
        if(type == TYPE_JT){
            requestCode = REQUEST_CODE_CAMERA_JT;
        }
        startActivityForResult(intent, requestCode);
    }


    /* 获得图片，并进行适当的 缩放。 图片太大的话，是无法展示的。 */
    private Bitmap getBitMapFromPath(String imageFilePath) {
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        int dw = currentDisplay.getWidth();
        int dh = currentDisplay.getHeight();
        // Load up the image's dimensions not the image itself
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageFilePath,
                bmpFactoryOptions);
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
                / (float) dh);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
                / (float) dw);

        // If both of the ratios are greater than 1,
        // one of the sides of the image is greater than the screen
        if (heightRatio > 1 && widthRatio > 1) {
            if (heightRatio > widthRatio) {
                // Height ratio is larger, scale according to it
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                // Width ratio is larger, scale according to it
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }
        // Decode it for real
        bmpFactoryOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);
        return bmp;
    }

}
