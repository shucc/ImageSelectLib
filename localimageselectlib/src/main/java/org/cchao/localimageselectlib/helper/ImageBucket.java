package org.cchao.localimageselectlib.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 16/12/12.
 * cc@cchao.org
 */
public class ImageBucket {

    private int count = 0;

    private String bucketName;

    private List<ImageItem> imageList;

    private boolean select = false;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<ImageItem> getImageList() {
        return imageList;
    }

    public void setImageList(List<ImageItem> imageList) {
        this.imageList = imageList;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public boolean isSelect() {
        return select;
    }

    public void addItem(ImageItem imageItem) {
        if (null == imageList) {
            imageList = new ArrayList<>();
        }
        imageList.add(imageItem);
    }
}
