package org.cchao.localimageselectlib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class LocalPhotoAdapter extends RecyclerView.Adapter<LocalPhotoAdapter.LocalImageHolder> {

    private final int SELECED_COLOR_FILTER = 0x77000000;

    private List<ImageItem> imageItems;

    private Context context;

    private ImageLocalItemOnclickListener imageLocalItemOnclickListener;

    public LocalPhotoAdapter(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
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
        ImageItem imageItem = imageItems.get(position);
        if (imageItem.isSelect()) {
            holder.imgSelect.setImageResource(R.drawable.ic_picture_selected);
            holder.imgLocal.setColorFilter(SELECED_COLOR_FILTER);
        } else {
            holder.imgSelect.setImageResource(R.drawable.ic_picture_unselected);
            holder.imgLocal.setColorFilter(null);
        }
        ImageLoader.getImageLoaderListener().load(context, holder.imgLocal, imageItem.getImagePath());
        if (imageLocalItemOnclickListener != null) {
            holder.imgLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageLocalItemOnclickListener.onItemClick(view, position);
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

        public LocalImageHolder(View itemView) {
            super(itemView);
            imgLocal = (ImageView) itemView.findViewById(R.id.img_local);
            imgSelect = (ImageView) itemView.findViewById(R.id.img_select);
        }
    }

    public static interface ImageLocalItemOnclickListener {
        void onItemClick(View view, int position);
    }
}
