package org.cchao.localimageselectlib;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.cchao.localimageselectlib.helper.ImageItem;
import org.cchao.localimageselectlib.helper.LocalImagesUri;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class PhotoSelectActivity extends AppCompatActivity {

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

    private PhotoSelectAdapter imageAdapter;

    //可以选择的图片数目
    private int selectMaxSize = 9;

    private int imageResultCode;

    private FindLocalPhotosTask findLocalPhotosTask;

    public static void launch(Activity activity, int maxSize, int resultCode) {
        Intent starter = new Intent(activity, PhotoSelectActivity.class);
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

        recyclerView = findViewById(R.id.recyclerView);
        btnComplete = findViewById(R.id.btn_complete);
        textMaxSize = findViewById(R.id.text_max_image_size);
        textNumber = findViewById(R.id.text_number);
        textDefault = findViewById(R.id.text_default);

        initData();
    }

    private void initData() {
        selectMaxSize = getIntent().getIntExtra(KEY_IMAGE_MAX_SIZE, 0);
        imageResultCode = getIntent().getIntExtra(KEY_RESULT_CODE, 100);
        imageLocal = new ArrayList<>();

        textDefault.setText(String.format(getString(R.string.activity_local_image_select_max_size), selectMaxSize));
        textNumber.setText(String.valueOf(0));

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectConfirm();
            }
        });

        showLocalImages();
    }

    public void showLocalImages() {
        findLocalPhotosTask = new FindLocalPhotosTask();
        findLocalPhotosTask.execute(false);
    }

    private void showData() {
        if (null != imageAdapter) {
            imageAdapter.notifyDataSetChanged();
            return;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(PhotoSelectActivity.this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new ItemDecorationAlbumColumns(10, 3));
        imageAdapter = new PhotoSelectAdapter(imageLocal, selectMaxSize);
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.setImageLocalItemOnclickListener(new PhotoSelectAdapter.ImageLocalItemOnclickListener() {
            @Override
            public void onItemClick(View view, int position) {
                textNumber.setText(String.valueOf(imageAdapter.getSelectCount()));
            }
        });
    }

    /**
     * 选取成功确认
     */
    private void selectConfirm() {
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < imageLocal.size(); i++) {
            ImageItem imageItem = imageLocal.get(i);
            if (imageItem.isSelect()) {
                images.add(imageItem.getImagePath());
            }
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_LOCAL_IMAGE_SELECT, (Serializable) images);
        setResult(imageResultCode, intent);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (findLocalPhotosTask != null && !findLocalPhotosTask.isCancelled()) {
            findLocalPhotosTask.cancel(true);
        }
    }

    private class FindLocalPhotosTask extends AsyncTask<Boolean, Integer, List<ImageItem>> {

        @Override
        protected void onPostExecute(List<ImageItem> imageItems) {
            textMaxSize.setText(String.format(getResources().getString(R.string.activity_local_image_select_all_size), imageItems.size()));
            showData();
        }

        @Override
        protected List<ImageItem> doInBackground(Boolean... params) {
            imageLocal.addAll(LocalImagesUri.getLocalImagesUri(PhotoSelectActivity.this));
            return imageLocal;
        }
    }
}
