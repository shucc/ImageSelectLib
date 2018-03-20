package org.cchao.localimageselect;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
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

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

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
                openPhotoSelect(3);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPhotoSelect(9);
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

    private void openPhotoSelect(final int maxSize) {
        List<PermissionItem> permissionItems = new ArrayList<>();
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间", R.drawable.permission_ic_storage));
        HiPermission.create(this)
                .title("权限申请")
                .permissions(permissionItems)
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, getTheme()))
                .msg("为了您能正常使用选取图片功能，需要以下权限")
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {
                        PhotoSelectActivity.launch(MainActivity.this, maxSize, 300);
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
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
