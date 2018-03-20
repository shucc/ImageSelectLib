package org.cchao.localimageselectlib.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.cchao.localimageselectlib.PhotoSelectLoader;
import org.cchao.localimageselectlib.R;
import org.cchao.localimageselectlib.helper.ImageItem;
import org.cchao.localimageselectlib.listener.OnItemClickListener;
import org.cchao.localimageselectlib.utils.ScreenUtils;

import java.util.List;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class PhotoSelectAdapter extends RecyclerView.Adapter<PhotoSelectAdapter.LocalImageHolder> {

    private List<ImageItem> data;

    private int maxSize;

    //当前已选中图片数目
    private int selectCount = 0;

    private Context context;

    private OnItemClickListener onItemClickListener;

    public PhotoSelectAdapter(List<ImageItem> data, int maxSize) {
        this.data = data;
        this.maxSize = maxSize;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public LocalImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_image, parent, false);
        final LocalImageHolder holder = new LocalImageHolder(view);
        int itemHeight = (ScreenUtils.width(context).px - 16) / 3;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imgLocal.getLayoutParams();
        params.height = itemHeight;
        holder.imgLocal.setLayoutParams(params);
        if (null != onItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = holder.getAdapterPosition();
                    ImageItem imageItem = data.get(position);
                    if (imageItem.isSelect()) {
                        selectCount--;
                    } else {
                        if (selectCount >= maxSize) {
                            Toast.makeText(context, String.format(context.getString(R.string.activity_local_image_select_image_enough)
                                    , maxSize), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectCount++;
                    }
                    imageItem.setSelect(!imageItem.isSelect());
                    changeSelect(holder, imageItem.isSelect());
                    data.set(position, imageItem);
                    onItemClickListener.onItemClick(view, position);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(LocalImageHolder holder, final int position) {
        final ImageItem imageItem = data.get(position);
        changeSelect(holder, imageItem.isSelect());
        PhotoSelectLoader.getImageLoaderListener().load(context, holder.imgLocal, imageItem.getImagePath());
    }

    private void changeSelect(LocalImageHolder holder, boolean isSelected) {
        if (isSelected) {
            holder.imgSelect.setImageResource(R.drawable.ic_photo_select_selected);
            holder.imgLocal.setColorFilter(0x77000000);
        } else {
            holder.imgSelect.setImageResource(R.drawable.ic_photo_select_unselected);
            holder.imgLocal.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    public int getSelectCount() {
        return selectCount;
    }

    class LocalImageHolder extends RecyclerView.ViewHolder {

        ImageView imgLocal;

        ImageView imgSelect;

        LocalImageHolder(View itemView) {
            super(itemView);
            imgLocal = itemView.findViewById(R.id.img_local);
            imgSelect = itemView.findViewById(R.id.img_select);
        }
    }
}
