package org.cchao.localimageselectlib;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 16/12/12.
 * cc@cchao.org
 */
public class LocalImagesUri {

    protected static List<ImageItem> getLocalImagesUri(Context context) {

        ImagesHelper mImagesHelper;
        List<ImageBucket> mImageBucket = new ArrayList<ImageBucket>();
        List<ImageItem> mImageItem = new ArrayList<ImageItem>();

        mImagesHelper = new ImagesHelper();
        mImagesHelper.init(context);
        mImageBucket = mImagesHelper.getImagesBucketList(false);
        if (mImageBucket.isEmpty()) {
            mImageBucket.clear();
        }
        if (mImageItem.isEmpty()) {
            mImageItem.clear();
        }
        for (ImageBucket imageBucket : mImageBucket) {
            mImageItem.addAll(imageBucket.imageList);
        }
        return mImageItem;
    }
}