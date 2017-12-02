package com.oldmen.imagegallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemClickListener,
        FragmentManager.OnBackStackChangedListener, PagerFragment.PagerFragmentListener {

    @BindView(R.id.btn_back_press_toolbar_main)
    ImageButton mBtnBackPress;
    @BindView(R.id.toolbar_title_main)
    TextView mToolbarTitle;
    @BindView(R.id.folder_recycler_main)
    RecyclerView mFolderRecycler;
    @BindView(R.id.progressbar_main)
    ProgressBar mProgressbar;
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.btn_camera_toolbar_main)
    ImageButton mBtnCamera;

    private Handler handler;
    private ArrayList<String> mFolderTitle = new ArrayList<>();
    private HashMap<String, ArrayList<ImageModel>> mImageData = new HashMap<>();
    private FolderAdapter mFolderAdapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.CAMERA_REQUEST_CODE) {
            mFolderRecycler.setVisibility(View.INVISIBLE);
            getImageData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mProgressbar.setVisibility(View.VISIBLE);
        mFolderRecycler.setVisibility(View.INVISIBLE);
        initToolbar();
        initHandler();
        getImageData();
    }

    private void initToolbar() {
        mBtnBackPress.setOnClickListener(view -> onBackPressed());
        mBtnCamera.setOnClickListener(view -> openCamera());
    }

    private void initHandler() {
        handler = new Handler(message -> {
            switch (message.what) {
                case Constants.IMAGES_DATA_THREAD:
                    mFolderAdapter = new FolderAdapter(this, mFolderTitle, mImageData);
                    mFolderRecycler.setLayoutManager(new LinearLayoutManager(this));
                    mFolderRecycler.setAdapter(mFolderAdapter);
                    mProgressbar.setVisibility(View.GONE);
                    mFolderRecycler.setVisibility(View.VISIBLE);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void getImageData() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            getImageData();
        } else {
            new ImagesDataThread().start();
        }
    }

    private void openCamera() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
            getSupportFragmentManager().popBackStack();

        startActivityForResult(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA),
                Constants.CAMERA_REQUEST_CODE);
    }

    @Override
    public void onFolderClicked(int position) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main,
                        GridFragment.newInstance(mImageData.get(mFolderTitle.get(position))))
                .addToBackStack(Constants.FRAGMENT_GRID_TAG)
                .commit();
    }

    @Override
    public void onGridItemClicked(int position, ArrayList<ImageModel> mImgModel) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main,
                        PagerFragment.newInstance(position, mImgModel))
                .addToBackStack(Constants.FRAGMENT_PAGER_TAG)
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mBtnBackPress.setVisibility(View.GONE);
        } else {
            mBtnBackPress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onImageClicked(boolean mIsFooterHidden) {
        if (mIsFooterHidden)
            mToolbar.animate().translationY(-mToolbar.getHeight()).setDuration(200);
        else
            mToolbar.animate().translationY(0).setDuration(200);
    }

    private class ImagesDataThread extends Thread {
        @Override
        public void run() {
            mFolderTitle.clear();
            mImageData.clear();
            mFolderTitle.add(getString(R.string.all_title));
            mImageData.put(getString(R.string.all_title), new ArrayList<>());

            String[] projection = new String[]{
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                    MediaStore.Images.Media.SIZE};

            loadFromStorage(managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, null, null, null));

//            loadFromStorage(managedQuery(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
//                    projection, null, null, null));

            Log.i("ListingImages", "getImagesData: mFolderTitle length" + mFolderTitle.size());
            Log.i("ListingImages", "getImagesData: mImageData length" + mImageData.size());
            handler.sendEmptyMessage(Constants.IMAGES_DATA_THREAD);
        }

        private void loadFromStorage(Cursor cur) {
            if (cur.moveToFirst()) {
                String title;
                String imgUri;
                String bucket;
                String date;
                String size;

                do {
                    bucket = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    date = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                    imgUri = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.DATA));
                    title = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    size = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.SIZE));
                    mImageData.get(getString(R.string.all_title)).add(new ImageModel(imgUri, title, date, size));

                    Log.i("ListingImages", " bucket=" + bucket
                            + "\ndate_taken=" + date + "\nDATA=" + imgUri + "\nImage title=" + title);

                    if (mFolderTitle.contains(bucket)) {
                        mImageData.get(bucket).add(new ImageModel(imgUri, title, date, size));
                    } else {
                        ArrayList<ImageModel> imgList = new ArrayList<>();
                        imgList.add(new ImageModel(imgUri, title, date, size));
                        mFolderTitle.add(bucket);
                        mImageData.put(bucket, imgList);
                    }
                } while (cur.moveToNext());
            }
        }
    }
}
