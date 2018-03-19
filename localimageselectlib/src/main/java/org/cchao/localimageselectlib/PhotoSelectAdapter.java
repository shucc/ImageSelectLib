package org.cchao.localimageselectlib;

import android.content.Context;
import android.support.annotation.NonNull;
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
public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectAdapter.LocalImageHolder> {

    private final int SELECTED_COLOR_FILTER = 0x77000000;

    private List<ImageItem> imageItems;

    private Context context;

    private ImageLocalItemOnclickListener imageLocalItemOnclickListener;

    PhotoSelectAdapter(List<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    void setImageLocalItemOnclickListener(ImageLocalItemOnclickListener imageLocalItemOnclickListener) {
        this.imageLocalItemOnclickListener = imageLocalItemOnclickListener;
    }

    @Override
    public LocalImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_image, parent, false);
        final LocalImageHolder localImageHolder = new LocalImageHolder(view);
        if (null != imageLocalItemOnclickListener) {
            localImageHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageItem imageItem = imageItems.get(localImageHolder.getAdapterPosition());
                    if (null == imageItem) {
                        imageLocalItemOnclickListener.onItemCameraClick();
                    } else {
                        imageLocalItemOnclickListener.onItemClick(view, localImageHolder.getAdapterPosition());
                    }
                }
            });
        }
        return localImageHolder;
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
                holder.imgSelect.setImageResource(R.drawable.ic_photo_select_selected);
                holder.imgLocal.setColorFilter(SELECTED_COLOR_FILTER);
            } else {
                holder.imgSelect.setImageResource(R.drawable.ic_photo_select_unselected);
                holder.imgLocal.setColorFilter(null);
            }
            PhotoSelectLoader.getImageLoaderListener().load(context, holder.imgLocal, imageItem.getImagePath());
        }
    }

    @Override
    public int getItemCount() {
        if (imageItems == null) {
            return 0;
        }
        return imageItems.size();
    }

    class LocalImageHolder extends RecyclerView.ViewHolder {

        ImageView imgLocal;

        ImageView imgSelect;

        ImageView imgCamera;

        LocalImageHolder(View itemView) {
            super(itemView);
            imgLocal = itemView.findViewById(R.id.img_local);
            imgSelect = itemView.findViewById(R.id.img_select);
            imgCamera = itemView.findViewById(R.id.img_camera);
        }
    }

    public interface ImageLocalItemOnclickListener {
        void onItemClick(View view, int position);

        void onItemCameraClick();
    }
}
