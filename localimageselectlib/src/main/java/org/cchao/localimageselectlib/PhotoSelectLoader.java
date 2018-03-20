package org.cchao.localimageselectlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class PhotoSelectLoader {

    private static LocalImageLoaderListener imageLoaderListener;

    public static void init(@NonNull LocalImageLoaderListener listener) {
        imageLoaderListener = listener;
    }

    public static LocalImageLoaderListener getImageLoaderListener() {
        return imageLoaderListener;
    }

    public interface LocalImageLoaderListener {
        void load(Context context, ImageView imageView, String imageUrl);
    }
}
