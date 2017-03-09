package org.cchao.localimageselectlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class LocalImageLoader {

    private static LocalImageLoaderListener imageLoaderListener;

    private static String fileProviderName;

    private static String imageFolderName;

    public static void init(@NonNull LocalImageLoaderListener listener
            , @NonNull String fileProvider, @NonNull String imageFolder) {
        imageLoaderListener = listener;
        fileProviderName = fileProvider;
        imageFolderName = imageFolder;
    }

    public static LocalImageLoaderListener getImageLoaderListener() {
        return imageLoaderListener;
    }

    public static String getFileProviderName() {
        return fileProviderName;
    }

    public static String getImageFolderName() {
        return imageFolderName;
    }

    public static interface LocalImageLoaderListener {
        void load(Context context, ImageView imageView, String imageUrl);
    }
}
