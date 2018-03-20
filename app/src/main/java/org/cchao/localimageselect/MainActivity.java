package org.cchao.localimageselect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.cchao.localimageselectlib.PhotoSelectActivity;
import org.cchao.localimageselectlib.PhotoSelectLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvImage;

    private PhotoAdapter adapter;

    private List<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvImage = findViewById(R.id.rv_image);
        data = new ArrayList<>();
        showData();
        PhotoSelectLoader.init(new PhotoSelectLoader.LocalImageLoaderListener() {
            @Override
            public void load(Context context, ImageView imageView, String imageUrl) {
                Glide.with(context)
                        .load(new File(imageUrl))
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(imageView);
            }
        });

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoSelectActivity.launch(MainActivity.this, 3, 300);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoSelectActivity.launch(MainActivity.this, 9, 300);
            }
        });
    }

    private void showData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
            return;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvImage.setLayoutManager(gridLayoutManager);
        adapter = new PhotoAdapter(data);
        rvImage.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == 300) {
            List<String> images = intent.getStringArrayListExtra(PhotoSelectActivity.KEY_LOCAL_IMAGE_SELECT);
            data.clear();
            data.addAll(images);
            showData();
        }
    }
}
