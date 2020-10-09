package com.winnie.filemanager_android;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    //圆通调用相机
    private final static int REQUEST_CODE_CAMERA_YT = 10000;
    //圆通申请权限
    private final static int REQUEST_PERMISSION_YT = 10009;

    //极兔调用相机
    private final static int REQUEST_CODE_CAMERA_JT = 20000;
    //极兔申请权限
    private final static int REQUEST_PERMISSION_JT = 20009;

    //圆通-type
    private final static int TYPE_YT = 1;
    //极兔-type
    private final static int TYPE_JT = 2;

    private Uri photoUri;

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
                    actionImage.setImageBitmap(bp);
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
                    actionImage.setImageBitmap(bp);
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
                takePhotos(1);
                break;
            case R.id.ll_ji_tu_layout:
                takePhotos(2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_YT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                openCamera(REQUEST_CODE_CAMERA_YT);
            } else {
                Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_PERMISSION_JT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                openCamera(REQUEST_CODE_CAMERA_JT);
            } else {
                Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 打开相机
     *
     * @param type 1：圆通 2：极兔
     */
    private void takePhotos(int type) {
        if (checkPermission(type)) {
            //有权限，直接拍照
            int requestCode = 0;
            if (type == TYPE_YT) {
                requestCode = REQUEST_CODE_CAMERA_YT;
            }
            if (type == TYPE_JT) {
                requestCode = REQUEST_CODE_CAMERA_JT;
            }
            openCamera(requestCode);
        }
    }

    /**
     * 检查权限
     *
     * @param type  1：圆通 2：极兔
     * @return 有权限
     */
    private boolean checkPermission(int type) {
        boolean cameraPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        if (cameraPermission && storagePermission) {
            return true;
        }
        int requestCode = 0;
        if (type == TYPE_YT) {
            requestCode = REQUEST_PERMISSION_YT;
        }
        if (type == TYPE_JT) {
            requestCode = REQUEST_PERMISSION_JT;
        }
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);

        return false;
    }

    /**
     * 打开相机
     */
    private void openCamera(int requestCode) {
        //向  MediaStore.Images.Media.EXTERNAL_CONTENT_URI 插入一个数据，那么返回标识ID。
        //在完成拍照后，新的照片会以此处的photoUri命名. 其实就是指定了个文件名
        ContentValues values = new ContentValues();
        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //准备intent，并 指定 新 照片 的文件名（photoUri）
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        //启动拍照的窗体。并注册 回调处理。
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
