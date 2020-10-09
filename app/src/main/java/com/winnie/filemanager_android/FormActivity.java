package com.winnie.filemanager_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormActivity extends AppCompatActivity {

    @BindView(R.id.action_image)
    ImageView actionImage;
    @BindView(R.id.tv_number_content)
    ClearEditText tvNumberContent;
    @BindView(R.id.tv_date_content)
    TextView tvDateContent;

    private DatePickerDialog datePickerDialog;

    private String mPhotoPath;
    private int mType;
    private Long mDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        ButterKnife.bind(this);

        mPhotoPath = getIntent().getStringExtra(Constant.KEY_PATH);
        mType = getIntent().getIntExtra(Constant.KEY_TYPE, -1);

        actionImage.setImageBitmap(getBitMapFromPath(mPhotoPath));
        tvNumberContent.setText("未扫描快递单号");
        initDate( System.currentTimeMillis());
    }

    @OnClick({R.id.ic_back, R.id.ll_date, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ic_back:
                finish();
                break;
            case R.id.ll_date:
                chooseDate();
                break;
            case R.id.btn_confirm:
                Toast.makeText(this, "确定提交吗？", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void chooseDate() {
        if (datePickerDialog == null) {
            datePickerDialog = new DatePickerDialog(this, mDate);
        }
        datePickerDialog.setSelectListener(this::initDate);
        datePickerDialog.show();
    }

    private void initDate(long date){
        mDate = date;
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date(date));
        tvDateContent.setText(dateString);
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
