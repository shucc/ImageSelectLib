package org.cchao.localimageselectlib.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 16/12/12.
 * cc@cchao.org
 */
public class LocalImagesUri {

    public static List<ImageItem> getLocalImagesUri(Context context) {
        ImagesHelper imagesHelper;
        List<ImageItem> imageItems = new ArrayList<>();
        imagesHelper = new ImagesHelper();
        imagesHelper.init(context);
        List<ImageBucket> imageBuckets = imagesHelper.getImagesBucketList(false);
        if (null != imageBuckets && imageBuckets.size() > 0) {
            for (ImageBucket imageBucket : imageBuckets) {
                imageItems.addAll(imageBucket.imageList);
            }
        }
        return imageItems;
    }
}