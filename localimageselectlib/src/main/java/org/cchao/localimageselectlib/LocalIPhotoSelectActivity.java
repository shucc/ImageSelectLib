package org.cchao.localimageselectlib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class LocalIPhotoSelectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public static final String KEY_LOCAL_IMAGE_SELECT = "key_local_image_select";

    private static final int RC_LOCAL_IMAGE_PERM = 100;

    private static final String KEY_IMAGE_MAX_SIZE = "key_image_max_size";
    private static final String KEY_RESULT_CODE = "key_result_code";

    private RecyclerView recyclerView;
    private Button btnComplete;
    private TextView textMaxSize;
    private TextView textNumber;
    private TextView textDefault;

    private List<ImageItem> imageLocal;

    private LocalPhotoAdapter imageAdapter;

    //可以选择的图片数目
    private int selectMaxSize = 9;

    //当前已选中图片数目
    private int selectCount = 0;

    private int resultCode;

    public static void launch(Activity activity, int maxSize, int resultCode) {
        Intent starter = new Intent(activity, LocalIPhotoSelectActivity.class);
        starter.putExtra(KEY_IMAGE_MAX_SIZE, maxSize);
        starter.putExtra(KEY_RESULT_CODE, resultCode);
        activity.startActivityForResult(starter, 300);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_image_select);

        if (LocalImageLoader.getImageLoaderListener() == null) {
            finish();
            throw new NullPointerException("ImageLoader is null!");
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnComplete = (Button) findViewById(R.id.btn_complete);
        textMaxSize = (TextView) findViewById(R.id.text_max_image_size);
        textNumber = (TextView) findViewById(R.id.text_number);
        textDefault = (TextView) findViewById(R.id.text_default);

        initData();
    }

    private void initData() {
        selectMaxSize = getIntent().getIntExtra(KEY_IMAGE_MAX_SIZE, 0);
        resultCode = getIntent().getIntExtra(KEY_RESULT_CODE, 100);
        imageLocal = new ArrayList<>();

        textDefault.setText("/".concat(String.valueOf(selectMaxSize).concat("张")));
        textNumber.setText(String.valueOf(selectCount));

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> images = new ArrayList<String>();
                for (int i = 0; i < imageLocal.size(); i++) {
                    ImageItem imageItem = imageLocal.get(i);
                    if (imageItem.isSelect()) {
                        images.add(imageItem.getImagePath());
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(KEY_LOCAL_IMAGE_SELECT, (Serializable) images);
                setResult(resultCode, intent);
                finish();
            }
        });

        showLocalImages();
    }

    @AfterPermissionGranted(RC_LOCAL_IMAGE_PERM)
    public void showLocalImages() {
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, RC_LOCAL_IMAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            Observable.create(new Observable.OnSubscribe<List<ImageItem>>() {
                @Override
                public void call(Subscriber<? super List<ImageItem>> subscriber) {
                    imageLocal = LocalImagesUri.getLocalImagesUri(LocalIPhotoSelectActivity.this);
                    subscriber.onNext(imageLocal);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<ImageItem>>() {
                        @Override
                        public void call(List<ImageItem> imageItems) {
                            textMaxSize.setText(String.format(getResources().getString(R.string.activity_local_image_select_local_max_size), imageItems.size()));
                            showData();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
        }
    }

    private void showData() {
        if (imageAdapter == null) {
            GridLayoutManager gridLayouManager = new GridLayoutManager(LocalIPhotoSelectActivity.this, 3);
            gridLayouManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayouManager);
            imageAdapter = new LocalPhotoAdapter(imageLocal);
            recyclerView.setAdapter(imageAdapter);
            imageAdapter.setImageLocalItemOnclickListener(new LocalPhotoAdapter.ImageLocalItemOnclickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ImageItem imageItem = imageLocal.get(position);
                    if (imageItem.isSelect()) {
                        selectCount--;
                    } else {
                        if (selectCount >= selectMaxSize) {
                            return;
                        }
                        selectCount++;
                    }
                    imageItem.setSelect(!imageItem.isSelect());
                    imageLocal.set(position, imageItem);
                    imageAdapter.notifyItemChanged(position);
                    textNumber.setText(String.valueOf(selectCount));
                }
            });
        } else {
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int i, List<String> list) {

    }

    @Override
    public void onPermissionsDenied(int i, List<String> list) {
        new AppSettingsDialog.Builder(this)
                .setRequestCode(RC_LOCAL_IMAGE_PERM)
                .build()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCAL_IMAGE_PERM) {
            if (resultCode == RESULT_OK) {
                showLocalImages();
            } else {
                finish();
            }
        }
    }
}
