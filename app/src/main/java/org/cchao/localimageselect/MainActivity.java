package org.cchao.localimageselect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.cchao.localimageselectlib.ImageLoader;
import org.cchao.localimageselectlib.LocalIPhotoSelectActivity;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoader.init(new ImageLoader.ImageLoaderListener() {
            @Override
            public void load(Context context, ImageView imageView, String imageUrl) {
                Glide.with(context)
                        .load(new File(imageUrl))
                        .into(imageView);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocalIPhotoSelectActivity.launch(MainActivity.this, 3, 300);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "onActivityResult: " + requestCode + "-->" + resultCode);
        if (resultCode == 300) {
            List<String> images = (List<String>) data.getSerializableExtra(LocalIPhotoSelectActivity.KEY_LOCAL_IMAGE_SELECT);
            if (null != images && images.size() > 0) {
                for (int i = 0; i < images.size(); i++) {
                    Log.d("MainActivity", images.get(i));
                }
            }
        }
    }
}
