package org.cchao.localimageselectlib.util;

import android.content.Context;
import android.os.Environment;

import org.cchao.localimageselectlib.PhotoSelectLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by shucc on 17/3/9.
 * cc@cchao.org
 */
public class FileUtil {

    public static File createTempFile(Context context) {
        File file = null;
        File storageDir;
        String storagePath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                storageDir = new File(storagePath + File.separator + PhotoSelectLoader.getImageFolderName());
            } else {
                storagePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                storageDir = new File(storagePath);
            }
            storageDir.mkdirs();
            file = File.createTempFile(timeStamp, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
