package com.winnie.filemanager_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.Display;

import java.io.IOException;

/**
 * @author : winnie
 * @date : 2020/10/10
 * @desc
 */
public class BitmapUtils {
    /**
     * 获得图片，并进行适当的 缩放。 图片太大的话，是无法展示的。
     */
    public static Bitmap getBitMapFromPath(Activity activity, String imageFilePath) {
        Display currentDisplay = activity.getWindowManager().getDefaultDisplay();
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
//        if (heightRatio > 1 && widthRatio > 1) {
//            if (heightRatio > widthRatio) {
//                // Height ratio is larger, scale according to it
//                bmpFactoryOptions.inSampleSize = heightRatio;
//            } else {
//                // Width ratio is larger, scale according to it
//                bmpFactoryOptions.inSampleSize = widthRatio;
//            }
//        }
        // Decode it for real
        bmpFactoryOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(imageFilePath, bmpFactoryOptions);

        //处理图片旋转
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(readPictureDegree(imageFilePath));
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        return bmp;
    }


    public static int readPictureDegree(String imageFilePath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imageFilePath);
            int orientation =
                    exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
