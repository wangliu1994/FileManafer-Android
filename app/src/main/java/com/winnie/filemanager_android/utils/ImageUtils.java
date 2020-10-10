package com.winnie.filemanager_android.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Display;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.camera.BitmapLuminanceSource;
import com.uuzuche.lib_zxing.decoding.DecodeFormatManager;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author : winnie
 * @date : 2020/10/10
 * @desc
 */
public class ImageUtils {
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

    /**
     * 相机拍照之后返回--获取照片
     */
    public static String getImagePath(Context context, Uri uri) {
        String imagePath = null;

        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documnets".equals(uri.getAuthority())) {
                //判断uri是不是media格式
                String id = docId.split(":")[1];
                //是media格式的话将uri进行二次解析取出id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }

        return imagePath;
    }

    public static String getImagePath(Context context, Uri externalContentUri, String selection) {
        String imagePath = null;
        Cursor cursor = context.getContentResolver()
                .query(externalContentUri, null,
                        selection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                System.out.println("获取照片到返回" + imagePath);
            }
            cursor.close();
        }
        return imagePath;
    }

    /**
     * 解析二维码图片工具类
     * @param analyzeCallback
     */
    public static void analyzeBitmap(String path, CodeUtils.AnalyzeCallback analyzeCallback) {

        /**
         * 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM
         */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int sampleSize = (int) (options.outHeight / (float) 400);

        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        mBitmap = BitmapFactory.decodeFile(path, options);

        //处理图片旋转
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.setRotate(readPictureDegree(path));
        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);


        MultiFormatReader multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);//设置尽量识别
        // 设置继续的字符编码格式为UTF8
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
        // 设置解析配置参数
        multiFormatReader.setHints(hints);

        // 开始对图像资源解码
        Result rawResult = null;
        try {
            rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(mBitmap))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rawResult != null) {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeSuccess(mBitmap, rawResult.getText());
            }
        } else {
            if (analyzeCallback != null) {
                analyzeCallback.onAnalyzeFailed();
            }
        }
    }
}
