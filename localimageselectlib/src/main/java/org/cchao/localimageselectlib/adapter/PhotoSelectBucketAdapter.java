package org.cchao.localimageselectlib.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cchao.localimageselectlib.PhotoSelectLoader;
import org.cchao.localimageselectlib.R;
import org.cchao.localimageselectlib.helper.ImageBucket;
import org.cchao.localimageselectlib.listener.OnItemClickListener;
import org.cchao.localimageselectlib.utils.ScreenUtils;

import java.util.List;

/**
 * Created by shucc on 18/3/20.
 * cc@cchao.org
 */
public class PhotoSelectBucketAdapter extends RecyclerView.Adapter<PhotoSelectBucketAdapter.PhotoSelectFolderHolder> {

    private String bucketCount;

    private List<ImageBucket> data;

    private Context context;

    private OnItemClickListener onItemClickListener;

    private int nowBucketPosition = 0;

    private ImageView nowBucketImage;

    public PhotoSelectBucketAdapter(List<ImageBucket> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PhotoSelectFolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        bucketCount = context.getString(R.string.activity_local_image_select_bucket_image_count);
        int size = ScreenUtils.width(context).px / 4;
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_image_bucket, parent, false);
        final PhotoSelectFolderHolder holder = new PhotoSelectFolderHolder(view);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.imgBucket.getLayoutParams();
        params.width = size;
        params.height = size;
        holder.imgBucket.setLayoutParams(params);
        if (null != onItemClickListener) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (position == nowBucketPosition) {
                        return;
                    }
                    ImageBucket preBucket = data.get(nowBucketPosition);
                    preBucket.setSelect(false);
                    data.set(nowBucketPosition, preBucket);
                    if (null != nowBucketImage) {
                        nowBucketImage.setImageResource(R.drawable.ic_photo_select_unselected);
                    }

                    ImageBucket nowBucket = data.get(position);
                    nowBucket.setSelect(true);
                    data.set(position, nowBucket);
                    nowBucketImage = holder.imgSelect;
                    nowBucketImage.setImageResource(R.drawable.ic_photo_select_selected);
                    nowBucketPosition = position;

                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoSelectFolderHolder holder, int position) {
        if (null == nowBucketImage && position == 0) {
            nowBucketImage = holder.imgSelect;
        }
        ImageBucket imageBucket = data.get(position);
        PhotoSelectLoader.getImageLoaderListener().load(context, holder.imgBucket, imageBucket.getImageList().get(0).getImagePath());
        holder.textBucket.setText(imageBucket.getBucketName());
        holder.textCount.setText(String.format(bucketCount, imageBucket.getCount()));
        holder.imgSelect.setImageResource(imageBucket.isSelect() ? R.drawable.ic_photo_select_selected : R.drawable.ic_photo_select_unselected);
    }

    @Override
    public int getItemCount() {
        if (null == data) {
            return 0;
        }
        return data.size();
    }

    class PhotoSelectFolderHolder extends RecyclerView.ViewHolder {

        ImageView imgBucket;

        TextView textBucket;

        TextView textCount;

        ImageView imgSelect;

        public PhotoSelectFolderHolder(View itemView) {
            super(itemView);
            imgBucket = itemView.findViewById(R.id.img_bucket);
            textBucket = itemView.findViewById(R.id.text_bucket);
            textCount = itemView.findViewById(R.id.text_count);
            imgSelect = itemView.findViewById(R.id.img_select);
        }
    }
}
