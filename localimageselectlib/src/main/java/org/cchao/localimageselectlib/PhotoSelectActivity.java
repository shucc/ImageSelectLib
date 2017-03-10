package org.cchao.localimageselectlib;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.cchao.localimageselectlib.helper.ImageItem;
import org.cchao.localimageselectlib.helper.LocalImagesUri;
import org.cchao.localimageselectlib.util.FileUtil;

import java.io.File;
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
public class PhotoSelectActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private final String TAG = getClass().getName();

    public static final String KEY_LOCAL_IMAGE_SELECT = "key_local_image_select";

    private static final int RC_LOCAL_IMAGE_PERM = 100;
    private final int TAKE_PHOTO = 200;

    private static final String KEY_IMAGE_MAX_SIZE = "key_image_max_size";
    private static final String KEY_RESULT_CODE = "key_result_code";
    private static final String KEY_NEED_CAMERA = "keed_need_camera";

    private final String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private RecyclerView recyclerView;
    private Button btnComplete;
    private TextView textMaxSize;
    private TextView textNumber;
    private TextView textDefault;

    private List<ImageItem> imageLocal;

    private PhotoSelectAdapter imageAdapter;

    //可以选择的图片数目
    private int selectMaxSize = 9;

    //是否显示拍照
    private boolean needShowCamera = false;

    //当前已选中图片数目
    private int selectCount = 0;

    private int imageResultCode;

    private File cameraFile;

    public static void launch(Activity activity, int maxSize, int resultCode) {
        Intent starter = new Intent(activity, PhotoSelectActivity.class);
        starter.putExtra(KEY_IMAGE_MAX_SIZE, maxSize);
        starter.putExtra(KEY_RESULT_CODE, resultCode);
        activity.startActivityForResult(starter, 300);
    }

    public static void launch(Activity activity, int maxSize, int resultCode, boolean needCamera) {
        Intent starter = new Intent(activity, PhotoSelectActivity.class);
        starter.putExtra(KEY_NEED_CAMERA, needCamera);
        starter.putExtra(KEY_IMAGE_MAX_SIZE, maxSize);
        starter.putExtra(KEY_RESULT_CODE, resultCode);
        activity.startActivityForResult(starter, 300);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_image_select);

        if (PhotoSelectLoader.getImageLoaderListener() == null) {
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
        needShowCamera = getIntent().getBooleanExtra(KEY_NEED_CAMERA, false);
        imageResultCode = getIntent().getIntExtra(KEY_RESULT_CODE, 100);
        imageLocal = new ArrayList<>();

        textDefault.setText("/".concat(String.valueOf(selectMaxSize).concat("张")));
        textNumber.setText(String.valueOf(selectCount));

        btnComplete.setOnClickListener((v) -> selectConfirm());

        showLocalImages();
    }

    @AfterPermissionGranted(RC_LOCAL_IMAGE_PERM)
    public void showLocalImages() {
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, RC_LOCAL_IMAGE_PERM, permissions);
        } else {
            Observable.create((Subscriber<? super List<ImageItem>> subscriber) -> {
                if (needShowCamera) {
                    imageLocal.add(null);
                }
                imageLocal.addAll(LocalImagesUri.getLocalImagesUri(PhotoSelectActivity.this));
                subscriber.onNext(imageLocal);
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(imageItems -> {
                        textMaxSize.setText(String.format(getResources().getString(R.string.activity_local_image_select_local_max_size), imageItems.size()));
                        showData();
                    }, throwable -> Log.d(TAG, throwable.getMessage()));
        }
    }

    private void showData() {
        if (imageAdapter == null) {
            GridLayoutManager gridLayouManager = new GridLayoutManager(PhotoSelectActivity.this, 3);
            gridLayouManager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(gridLayouManager);
            recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(10, 3));
            imageAdapter = new PhotoSelectAdapter(imageLocal);
            recyclerView.setAdapter(imageAdapter);
            imageAdapter.setImageLocalItemOnclickListener(new PhotoSelectAdapter.ImageLocalItemOnclickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    ImageItem imageItem = imageLocal.get(position);
                    if (imageItem.isSelect()) {
                        selectCount--;
                    } else {
                        if (selectCount >= selectMaxSize) {
                            Toast.makeText(PhotoSelectActivity.this, String.format(getString(R.string.activity_local_image_select_image_enough)
                                    , selectMaxSize), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        selectCount++;
                    }
                    imageItem.setSelect(!imageItem.isSelect());
                    imageLocal.set(position, imageItem);
                    imageAdapter.notifyItemChanged(position);
                    textNumber.setText(String.valueOf(selectCount));
                }

                @Override
                public void onItemCameraClick() {
                    if (selectCount >= selectMaxSize) {
                        Toast.makeText(PhotoSelectActivity.this, String.format(getString(R.string.activity_local_image_select_image_enough)
                                , selectMaxSize), Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        openCamera();
                    }
                }
            });
        } else {
            imageAdapter.notifyDataSetChanged();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            cameraFile = FileUtil.createTempFile(this);
            if (cameraFile != null) {
                Uri uri = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this, PhotoSelectLoader.getFileProviderName(), cameraFile);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    uri = Uri.fromFile(cameraFile);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        }
    }

    /**
     * 选取成功确认
     */
    private void selectConfirm() {
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < imageLocal.size(); i++) {
            ImageItem imageItem = imageLocal.get(i);
            if (null != imageItem && imageItem.isSelect()) {
                images.add(imageItem.getImagePath());
            }
        }
        if (needShowCamera && null != cameraFile) {
            images.add(cameraFile.getPath());
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_LOCAL_IMAGE_SELECT, (Serializable) images);
        setResult(imageResultCode, intent);
        finish();
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
                .setRationale(R.string.activity_local_image_select_perm)
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
        if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
            selectConfirm();
        }
    }
}
