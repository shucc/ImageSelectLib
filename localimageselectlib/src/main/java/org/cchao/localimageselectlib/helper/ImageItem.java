package org.cchao.localimageselectlib.helper;

/**
 * Created by chenchao on 16/12/12.
 * cc@cchao.org
 */
public class ImageItem {

    private String id;
    private String orientation;
    private String imagePath;
    private String thumnbailPath;

    private boolean select = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    public String getThumnbailPath() {
        return thumnbailPath;
    }

    public void setThumnbailPath(String thumnbailPath) {
        this.thumnbailPath = thumnbailPath;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
