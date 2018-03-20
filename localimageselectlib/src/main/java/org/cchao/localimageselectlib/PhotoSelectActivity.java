package org.cchao.localimageselectlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cchao.localimageselectlib.adapter.PhotoSelectAdapter;
import org.cchao.localimageselectlib.adapter.PhotoSelectBucketAdapter;
import org.cchao.localimageselectlib.helper.ImageBucket;
import org.cchao.localimageselectlib.helper.ImageItem;
import org.cchao.localimageselectlib.helper.ImagesHelper;
import org.cchao.localimageselectlib.listener.OnItemClickListener;
import org.cchao.localimageselectlib.utils.ScreenUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shucc on 17/3/3.
 * cc@cchao.org
 */
public class PhotoSelectActivity extends AppCompatActivity {

    public static final String KEY_LOCAL_IMAGE_SELECT = "key_local_image_select";
    private static final String KEY_IMAGE_MAX_SIZE = "key_image_max_size";
    private static final String KEY_RESULT_CODE = "key_result_code";

    private RecyclerView rvImage;
    private Button btnComplete;
    private TextView textBucket;
    private TextView textNowCount;
    private TextView textMaxCount;
    private View viewBucket;
    private RecyclerView rvBucket;

    private List<ImageItem> data;

    private List<ImageBucket> bucketData;

    private PhotoSelectAdapter adapter;

    private PhotoSelectBucketAdapter bucketAdapter;

    //可以选择的图片数目
    private int selectMaxSize = 9;

    private int imageResultCode;

    private FindLocalPhotosTask findLocalPhotosTask;

    private int rvBucketHeight;
    private ObjectAnimator showBucketSelectAnim;
    private ObjectAnimator hideBucketSelectAnim;

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

        rvImage = findViewById(R.id.rv_image);
        btnComplete = findViewById(R.id.btn_complete);
        textBucket = findViewById(R.id.text_bucket);
        textNowCount = findViewById(R.id.text_now_count);
        textMaxCount = findViewById(R.id.text_max_count);
        viewBucket = findViewById(R.id.view_bucket);
        rvBucket = findViewById(R.id.rv_bucket);

        initData();
    }

    private void initData() {
        selectMaxSize = getIntent().getIntExtra(KEY_IMAGE_MAX_SIZE, 0);
        imageResultCode = getIntent().getIntExtra(KEY_RESULT_CODE, 100);
        data = new ArrayList<>();

        textMaxCount.setText(String.format(getString(R.string.activity_local_image_select_max_size), selectMaxSize));
        textNowCount.setText(String.valueOf(0));

        int screenHeight = ScreenUtils.height(this).px;
        int screenWidth = ScreenUtils.width(this).px;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewBucket.getLayoutParams();
        layoutParams.height = (screenWidth - 16) / 3;
        viewBucket.setLayoutParams(layoutParams);
        rvBucketHeight = screenHeight - (screenWidth - 16) / 3;

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectConfirm();
            }
        });
        textBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.GONE == rvBucket.getVisibility()) {
                    showBucketSelect();
                } else {
                    hideBucketSelect();
                }
            }
        });
        viewBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideBucketSelect();
            }
        });
        findLocalPhotosTask = new FindLocalPhotosTask();
        findLocalPhotosTask.execute(false);
    }

    /**
     * 展示当前选中文件夹所有图片
     */
    private void showData() {
        if (null != adapter) {
            adapter.notifyDataSetChanged();
            return;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        rvImage.setLayoutManager(gridLayoutManager);
        rvImage.addItemDecoration(new ItemDecorationAlbumColumns(8, 3));
        adapter = new PhotoSelectAdapter(data, selectMaxSize);
        rvImage.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                textNowCount.setText(String.valueOf(adapter.getSelectCount()));
            }
        });
    }

    /**
     * 展示各文件夹图片信息
     */
    private void showBucketData() {
        if (null != bucketAdapter) {
            bucketAdapter.notifyDataSetChanged();
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvBucket.setLayoutManager(linearLayoutManager);
        bucketAdapter = new PhotoSelectBucketAdapter(bucketData);
        rvBucket.setAdapter(bucketAdapter);
        bucketAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageBucket nowBucket = bucketData.get(position);
                data.clear();
                data.addAll(nowBucket.getImageList());
                textBucket.setText(nowBucket.getBucketName());
                adapter.notifyDataSetChanged();
                hideBucketSelect();
            }
        });
    }

    /**
     * 选取成功确认
     */
    private void selectConfirm() {
        ArrayList<String> images = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            ImageItem imageItem = data.get(i);
            if (imageItem.isSelect()) {
                images.add(imageItem.getImagePath());
            }
        }
        Intent intent = new Intent();
        intent.putStringArrayListExtra(KEY_LOCAL_IMAGE_SELECT, images);
        setResult(imageResultCode, intent);
        finish();
    }

    private void showBucketSelect() {
        rvBucket.setVisibility(View.VISIBLE);
        if (null == showBucketSelectAnim) {
            showBucketSelectAnim = ObjectAnimator
                    .ofFloat(rvBucket, View.TRANSLATION_Y, rvBucketHeight, 0)
                    .setDuration(400);
            showBucketSelectAnim.setInterpolator(new LinearInterpolator());
            showBucketSelectAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rvBucket.setVisibility(View.VISIBLE);
                    viewBucket.setVisibility(View.VISIBLE);
                }
            });
        }
        showBucketSelectAnim.start();
    }

    private void hideBucketSelect() {
        viewBucket.setVisibility(View.INVISIBLE);
        if (null == hideBucketSelectAnim) {
            hideBucketSelectAnim = ObjectAnimator
                    .ofFloat(rvBucket, View.TRANSLATION_Y, 0, rvBucketHeight)
                    .setDuration(400);
            hideBucketSelectAnim.setInterpolator(new LinearInterpolator());
            hideBucketSelectAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    rvBucket.setVisibility(View.GONE);
                }
            });
        }
        hideBucketSelectAnim.start();
    }

    @Override
    public void onBackPressed() {
        if (View.VISIBLE == rvBucket.getVisibility()) {
            hideBucketSelect();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (findLocalPhotosTask != null && !findLocalPhotosTask.isCancelled()) {
            findLocalPhotosTask.cancel(true);
        }
        if (null != showBucketSelectAnim) {
            showBucketSelectAnim.cancel();
        }
        if (null != hideBucketSelectAnim) {
            hideBucketSelectAnim.cancel();
        }
    }

    private class FindLocalPhotosTask extends AsyncTask<Boolean, Integer, List<ImageItem>> {

        @Override
        protected void onPostExecute(List<ImageItem> imageItems) {
            textBucket.setText(bucketData.get(0).getBucketName());
            showData();
            showBucketData();
        }

        @Override
        protected List<ImageItem> doInBackground(Boolean... params) {
            ImagesHelper imagesHelper;
            imagesHelper = new ImagesHelper();
            imagesHelper.init(PhotoSelectActivity.this);
            List<ImageBucket> imageBuckets = imagesHelper.getImagesBucketList(false);

            ImageBucket allBuckets = new ImageBucket();
            allBuckets.setBucketName(getString(R.string.activity_local_image_select_all));
            allBuckets.setSelect(true);
            List<ImageItem> allItems = new ArrayList<>();
            for (ImageBucket imageBucket : imageBuckets) {
                allItems.addAll(imageBucket.getImageList());
                allBuckets.setCount(allBuckets.getCount() + imageBucket.getImageList().size());
            }
            allBuckets.setImageList(allItems);
            bucketData = new ArrayList<>();
            bucketData.add(allBuckets);
            bucketData.addAll(imageBuckets);

            data.addAll(bucketData.get(0).getImageList());
            return data;
        }
    }
}
