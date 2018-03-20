package org.cchao.localimageselect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shucc on 18/3/20.
 * cc@cchao.org
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {

    private List<String> data;

    private Context context;

    public PhotoAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        PhotoHolder holder = new PhotoHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        Glide.with(context)
                .load(data.get(position))
                .into(holder.imgPhoto);
    }

    @Override
    public int getItemCount() {
        if (null == data) {
            return 0;
        }
        return data.size();
    }

    class PhotoHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;

        public PhotoHolder(View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }
}
