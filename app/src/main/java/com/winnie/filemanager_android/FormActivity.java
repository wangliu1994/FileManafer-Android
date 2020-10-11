package com.winnie.filemanager_android;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.winnie.filemanager_android.base.BaseActivity;
import com.winnie.filemanager_android.common.Constant;
import com.winnie.filemanager_android.common.ImageUtils;
import com.winnie.filemanager_android.common.Result;
import com.winnie.filemanager_android.model.FileUploadResDTO;
import com.winnie.filemanager_android.net.ApiClient;
import com.winnie.filemanager_android.view.ClearEditText;
import com.winnie.filemanager_android.view.DatePickerDialog;
import com.winnie.filemanager_android.view.InformationDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class FormActivity extends BaseActivity {
    @BindView(R.id.action_image)
    ImageView mActionImage;
    @BindView(R.id.tv_number_content)
    ClearEditText mTvNumberContent;
    @BindView(R.id.tv_date_content)
    TextView mTvDateContent;
    @BindView(R.id.title_bar)
    TextView mTitleBar;
    @BindView(R.id.btn_confirm)
    Button mBtnConfirm;

    //调用相机
    private final static int REQUEST_CODE_CAMERA = 10000;
    //调用相册
    private final static int REQUEST_CODE_ALBUM = 20000;
    //申请权限
    private final static int REQUEST_PERMISSION = 9999;


    private DatePickerDialog mDatePickerDialog;

    private Uri photoUri;

    private String mPhotoPath;
    private int mType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);

        mType = getIntent().getIntExtra(Constant.KEY_TYPE, -1);
        if (mType == Constant.TYPE_YT) {
            mTitleBar.setText("圆通面单归档");
        } else if (mType == Constant.TYPE_JT) {
            mTitleBar.setText("极兔面单归档");
        } else {
            Toast.makeText(this, "快递类型不支持", Toast.LENGTH_LONG).show();
            finish();
        }
        initDate(System.currentTimeMillis());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) {
                mPhotoPath = ImageUtils.getImagePath(this, photoUri);
                photoUri = null;
                onGetPhoto();
            }

            if (requestCode == REQUEST_CODE_ALBUM) {
                if (data != null) {
                    Uri address = data.getData();
                    mPhotoPath = ImageUtils.getImagePath(this, address);
                    onGetPhoto();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                openCamera();
            } else {
                Toast.makeText(this, "权限不足", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick({
            R.id.ic_back,
            R.id.ll_date,
            R.id.btn_confirm,
            R.id.action_image,
            R.id.ll_choose_camera,
            R.id.ll_choose_photo
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_back:
                finish();
                break;
            case R.id.ll_choose_camera:
                //相机拍照
                useCamera();
                break;
            case R.id.ll_choose_photo:
                //相册选图
                useAlbum();
                break;
            case R.id.action_image:
                //大图预览
                Intent intent = new Intent(FormActivity.this, ImageActivity.class);
                intent.putExtra(Constant.KEY_PATH, mPhotoPath);
                intent.putExtra(Constant.KEY_TYPE, mType);
                startActivity(intent);
                break;
            case R.id.ll_date:
                //选择到件日期
                if (mDatePickerDialog == null) {
                    mDatePickerDialog = new DatePickerDialog(this, null);
                }
                mDatePickerDialog.setSelectListener(this::initDate);
                mDatePickerDialog.show();
                break;
            case R.id.btn_confirm:
                uploadFile();
                break;
            default:
                break;
        }
    }

    private void initDate(long date) {
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(date));
        mTvDateContent.setText(dateString);
    }

    /**
     * 打开相机
     */
    private void useCamera() {
        if (checkPermission()) {
            //有权限，直接拍照
            openCamera();
        }
    }

    /**
     * 打开相册
     */
    private void useAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_ALBUM);
    }

    /**
     * 检查权限
     *
     * @return 有权限
     */
    private boolean checkPermission() {
        boolean cameraPermission = ContextCompat.checkSelfPermission(FormActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storagePermission = ContextCompat.checkSelfPermission(FormActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (cameraPermission && storagePermission) {
            return true;
        }
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);

        return false;
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        //向  MediaStore.Images.Media.EXTERNAL_CONTENT_URI 插入一个数据，那么返回标识ID。
        //在完成拍照后，新的照片会以此处的photoUri命名. 其实就是指定了个文件名
        ContentValues values = new ContentValues();
        photoUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //准备intent，并 指定 新 照片 的文件名（photoUri）
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        //启动拍照的窗体。并注册 回调处理。
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private void onGetPhoto() {
        if (mPhotoPath == null) {
            Toast.makeText(this, "没有获取到图片，请稍后重试", Toast.LENGTH_LONG).show();
        }

        mActionImage.setImageBitmap(ImageUtils.getBitMapFromPath(this, mPhotoPath));
        ImageUtils.analyzeBitmap(mPhotoPath, new CodeUtils.AnalyzeCallback() {
            @Override
            public void onAnalyzeSuccess(Bitmap mBitmap, String result) {

                Vibrator vibrator;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    vibrator.vibrate(200L);
                }

                mTvNumberContent.setText(result);
            }

            @Override
            public void onAnalyzeFailed() {
                mTvNumberContent.setText("");
                Toast.makeText(FormActivity.this, "解析二维码失败，请稍后重试", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 上传文件
     */
    private void uploadFile() {
        String number = null;
        if (mTvNumberContent.getText() != null) {
            number = mTvNumberContent.getText().toString();
        }

        Long date = System.currentTimeMillis();
        if (mDatePickerDialog != null) {
            date = mDatePickerDialog.getCurrentTime();
        }
        if (mPhotoPath != null && mPhotoPath.length() > 0) {
            Map<String, RequestBody> map = new HashMap<>(4);

            File file = new File(mPhotoPath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            map.put("file\"; filename=\"" + file.getName(), requestFile);
            ApiClient
                    .getService()
                    .uploadFile(map, mType, number, date)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<Result<FileUploadResDTO>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            showWaitingDialog();
                        }

                        @Override
                        public void onNext(Response<Result<FileUploadResDTO>> response) {
                            if (response == null || response.body() == null) {
                                Toast.makeText(
                                        FormActivity.this,
                                        "文件上传失败", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }

                            Result<FileUploadResDTO> result = response.body();
                            if (result.getData() == null) {
                                Toast.makeText(FormActivity.this,
                                        "文件上传失败:" + result.getErrorMessage(),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }

                            String resultPath = result.getData().getFilePath();
                            String content = "文件已成功上传至：" + resultPath;
                            InformationDialog dialog = new InformationDialog(FormActivity.this, content);
                            dialog.setOnClickListener(v -> {
                                mActionImage.setImageResource(R.drawable.bg_index);
                                mTvNumberContent.setText("");
                                initDate(System.currentTimeMillis());
                            });
                            dialog.show();

                            Toast.makeText(FormActivity.this,
                                    resultPath, Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(FormActivity.this,
                                    "文件上传失败:", Toast.LENGTH_SHORT)
                                    .show();
                            hideWaitingDialog();
                        }

                        @Override
                        public void onComplete() {
                            hideWaitingDialog();
                        }
                    });
        }

    }
}
