package org.cchao.localimageselectlib;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class ImageLoader {

    private static ImageLoaderListener imageLoaderListener;

    public static void init(ImageLoaderListener listener) {
        imageLoaderListener = listener;
    }

    public static ImageLoaderListener getImageLoaderListener() {
        return imageLoaderListener;
    }

    public static interface ImageLoaderListener {
        void load(Context context, ImageView imageView, String imageUrl);
    }
}
