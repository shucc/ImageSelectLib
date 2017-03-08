package org.cchao.localimageselectlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.cchao.localimageselectlib.helper.ImageItem;

import java.util.List;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class LocalPhotoAdapter extends RecyclerView.Adapter<LocalPhotoAdapter.LocalImageHolder> {

    private final int SELECED_COLOR_FILTER = 0x77000000;

    private List<ImageItem> imageItems;

    private Context context;

    //是否显示拍照
    private boolean needShowCamera;

    private ImageLocalItemOnclickListener imageLocalItemOnclickListener;

    public LocalPhotoAdapter(List<ImageItem> imageItems, boolean needShowCamera) {
        this.imageItems = imageItems;
        this.needShowCamera = needShowCamera;
    }

    public void setImageLocalItemOnclickListener(ImageLocalItemOnclickListener imageLocalItemOnclickListener) {
        this.imageLocalItemOnclickListener = imageLocalItemOnclickListener;
    }

    @Override
    public LocalImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LocalImageHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_local_image, parent, false));
    }

    @Override
    public void onBindViewHolder(LocalImageHolder holder, final int position) {
        final ImageItem imageItem = imageItems.get(position);
        if (null == imageItem) {
            holder.imgLocal.setVisibility(View.GONE);
            holder.imgSelect.setVisibility(View.GONE);
            holder.imgCamera.setVisibility(View.VISIBLE);
        } else {
            holder.imgLocal.setVisibility(View.VISIBLE);
            holder.imgSelect.setVisibility(View.VISIBLE);
            holder.imgCamera.setVisibility(View.GONE);
            if (imageItem.isSelect()) {
                holder.imgSelect.setImageResource(R.drawable.ic_picture_selected);
                holder.imgLocal.setColorFilter(SELECED_COLOR_FILTER);
            } else {
                holder.imgSelect.setImageResource(R.drawable.ic_picture_unselected);
                holder.imgLocal.setColorFilter(null);
            }
            LocalImageLoader.getImageLoaderListener().load(context, holder.imgLocal, imageItem.getImagePath());
        }
        if (imageLocalItemOnclickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == imageItem) {
                        imageLocalItemOnclickListener.onItemCameraClick();
                    } else {
                        imageLocalItemOnclickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (imageItems == null) {
            return 0;
        }
        return imageItems.size();
    }

    protected class LocalImageHolder extends RecyclerView.ViewHolder {

        ImageView imgLocal;

        ImageView imgSelect;

        ImageView imgCamera;

        public LocalImageHolder(View itemView) {
            super(itemView);
            imgLocal = (ImageView) itemView.findViewById(R.id.img_local);
            imgSelect = (ImageView) itemView.findViewById(R.id.img_select);
            imgCamera = (ImageView) itemView.findViewById(R.id.img_camera);
        }
    }

    public static interface ImageLocalItemOnclickListener {
        void onItemClick(View view, int position);

        void onItemCameraClick();
    }
}
