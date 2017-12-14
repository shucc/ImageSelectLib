package org.cchao.localimageselect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.cchao.localimageselectlib.PhotoSelectActivity;
import org.cchao.localimageselectlib.PhotoSelectLoader;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String fileProviderName = "org.cchao.localimageselect.fileProvider";
    private final String imageFolderName = "ceshi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PhotoSelectLoader.init(new PhotoSelectLoader.LocalImageLoaderListener() {
            @Override
            public void load(Context context, ImageView imageView, String imageUrl) {
                Glide.with(context)
                        .load(new File(imageUrl))
                        .into(imageView);
            }
        }, fileProviderName, imageFolderName);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoSelectActivity.launch(MainActivity.this, 3, 300);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoSelectActivity.launch(MainActivity.this, 4, 300, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            List<String> images = (List<String>) data.getSerializableExtra(PhotoSelectActivity.KEY_LOCAL_IMAGE_SELECT);
            if (null != images && images.size() > 0) {
                for (int i = 0; i < images.size(); i++) {
                    Log.d("MainActivity", images.get(i));
                }
            }
        }
    }
}
