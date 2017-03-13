package cn.contactbook.utils;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by dell on 2017/2/20.
 */

public class PhotoHelper {
    public static final int REQUEST_LOAD_PHOTO_PICKED = 101;//打开相册的请求码
    public static final int REQUEST_LOAD_PHOTO_CAMERA = 102;//打开相册拍照的请求码
    public static final int REQUEST_PHOTO_CROP = 103;//对图片进行裁剪的请求码
    public static final String APPPATH = Environment.getExternalStorageDirectory() + File.separator;//文件保存的根路径

    /**
     * 从已有图库中获取相片
     *
     * @param context     上下文对象
     * @param requestCode 请求码
     */
    public static void selectMyPhotoFormGallery(Activity context, int requestCode) {
        Intent _intent = new Intent(Intent.ACTION_PICK);
        _intent.setType("image/*");
        context.startActivityForResult(_intent, requestCode);
    }

    /*
     * 根据当前的时间进行生成文件名
     */
    private static String generateFileName() {
        return String.valueOf(System.currentTimeMillis()) + ".jpg";
    }

    /**
     * 获取图库中照片的绝对路径
     *
     * @param fileName
     * @return
     */
    private static String pathForNewCameraPhoto(String fileName) {
        return pathForNewCameraPhoto(null, fileName);
    }

    private static String pathForNewCameraPhoto(String dir, String fileName) {
        if (dir == null) {
            dir = APPPATH + "Android/data/cn.contactbook";
        }

        File file = new File(dir);
        if (!file.exists()) {
            file.mkdir();
        }

        File iFile = new File(file.getAbsolutePath(), fileName);
        if (!iFile.exists())
            return iFile.getAbsolutePath();
        else
            return null;
    }

    /**
     * 裁剪图片
     *
     * @param context     上下文对象
     * @param pPhotoUri   原图片的uri路径
     * @param pResultCode 裁剪的请求码
     * @param isXY        是否设置1：1的裁剪框比例
     * @return 裁剪后的图片路径
     */
    public static String doCropPhoto(Activity context, Uri pPhotoUri,
                                     int pResultCode, boolean isXY) {
        String mTmpCameraFilePath = pathForNewCameraPhoto(generateFileName());
        Uri mCurrentPhotoFileUri = Uri.fromFile(new File(mTmpCameraFilePath));
        Intent _intent = new Intent("com.android.camera.action.CROP");
        _intent.setDataAndType(pPhotoUri, "image/*");
        _intent.putExtra("crop", "true");
        // 裁剪框的比例 1：1
        if (isXY) {
            _intent.putExtra("aspectX", 1);
            _intent.putExtra("aspectY", 1);
        }

        // 裁剪后输出图片的尺寸大小
        _intent.putExtra("outputX", 300);
        _intent.putExtra("outputY", 300);
        _intent.putExtra("outputFormat", "png");// 图片格式
        _intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoFileUri);
        context.startActivityForResult(_intent, pResultCode);
        return mTmpCameraFilePath;
    }
}