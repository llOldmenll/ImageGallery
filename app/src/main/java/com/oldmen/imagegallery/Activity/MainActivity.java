package com.oldmen.imagegallery.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oldmen.imagegallery.Adapter.FolderAdapter;
import com.oldmen.imagegallery.Fragment.GridFragment;
import com.oldmen.imagegallery.Fragment.PagerFragment;
import com.oldmen.imagegallery.Interface.FragmentChangeListener;
import com.oldmen.imagegallery.Interface.MainItemClickListener;
import com.oldmen.imagegallery.Model.ImageModel;
import com.oldmen.imagegallery.R;
import com.oldmen.imagegallery.Utils.Constants;
import com.oldmen.imagegallery.Utils.InternetConnectionCheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainItemClickListener,
        FragmentManager.OnBackStackChangedListener, PagerFragment.PagerFragmentListener,
        FragmentChangeListener {

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
    @BindView(R.id.fab_download_main)
    FloatingActionButton mFabDownload;

    private ArrayList<String> mFolderTitle = new ArrayList<>();
    private HashMap<String, ArrayList<ImageModel>> mImageData = new HashMap<>();
    private FolderAdapter mFolderAdapter;
    private GridFragment mGridFragment;
    private PagerFragment mPagerFragment;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.CAMERA_REQUEST_CODE:
                mFolderRecycler.setVisibility(View.INVISIBLE);
                getImageData();
                break;
            case Constants.DOWNLOAD_ACTIVITY_REQUEST_CODE:
                getImageData();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mFolderAdapter = new FolderAdapter(this, mFolderTitle, mImageData);
        mFolderRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFolderRecycler.setAdapter(mFolderAdapter);
        initToolbar();
        initFab();
        getImageData();
        if(mFolderTitle.size() > 0) mProgressbar.setVisibility(View.INVISIBLE);
    }

    private void initToolbar() {
        mBtnBackPress.setOnClickListener(view -> onBackPressed());
        mBtnCamera.setOnClickListener(view -> openCamera());
    }

    private void initFab() {
        mFolderRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFabDownload.getVisibility() == View.VISIBLE) {
                    mFabDownload.hide();
                } else if (dy < 0 && mFabDownload.getVisibility() != View.VISIBLE) {
                    mFabDownload.show();
                }
            }
        });

        mFabDownload.setOnClickListener(view -> {
            if (InternetConnectionCheck.checkConnection(getApplicationContext())) {
                startActivityForResult(new Intent(MainActivity.this, DownloadActivity.class),
                        Constants.DOWNLOAD_ACTIVITY_REQUEST_CODE);
            } else {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder.setTitle(getString(R.string.download_image))
                        .setMessage(getString(R.string.internet_unavailable))
                        .setPositiveButton(getString(R.string.ok), null)
                        .create().show();
            }
        });

    }

    private void getImageData() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mProgressbar.setVisibility(View.VISIBLE);
            mFolderRecycler.setVisibility(View.INVISIBLE);
            mFolderTitle.clear();
            mImageData.clear();

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

            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                mProgressbar.setVisibility(View.GONE);
                mFolderRecycler.setVisibility(View.VISIBLE);
                mFolderAdapter.notifyDataSetChanged();
            } else if (mPagerFragment != null) {
                Intent intent = new Intent(Constants.FILTER_PAGER_RECEIVER);
                sendBroadcast(intent);
            }
        }
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

    private void openCamera() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
            getSupportFragmentManager().popBackStack();

        startActivityForResult(new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA),
                Constants.CAMERA_REQUEST_CODE);
    }

    private void deleteImage(int position, String folder) {
        File imgFile = new File(mImageData.get(folder).get(position).getPath());
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            if (imgFile.exists()) {
                boolean isDelete = imgFile.delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imgFile)));
                if (isDelete) {
                    if (mGridFragment != null) {
                        Intent intent = new Intent(Constants.FILTER_GRID_RECEIVER);
                        intent.putExtra(Constants.EXTRAS_IMAGE_POSITION, position);
                        sendBroadcast(intent);
                    }
                }

            }
        }
    }

    @Override
    public void onFolderClicked(int position) {
        mGridFragment = GridFragment.newInstance(mFolderTitle.get(position),
                mImageData.get(mFolderTitle.get(position)));
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.animator.enter_from_right,
                        R.animator.exit_to_left,
                        R.animator.enter_from_left,
                        R.animator.exit_to_right)
                .replace(R.id.fragment_container_main, mGridFragment)
                .addToBackStack(Constants.FRAGMENT_GRID_TAG)
                .commit();
    }

    @Override
    public void onGridItemClicked(int position, String folderTitle, ArrayList<ImageModel> mImgModel) {
        mPagerFragment = PagerFragment.newInstance(position, folderTitle, mImgModel);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_main, mPagerFragment)
                .addToBackStack(Constants.FRAGMENT_PAGER_TAG)
                .commit();
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mFolderRecycler.setVisibility(View.VISIBLE);
            mBtnBackPress.setVisibility(View.GONE);
            mToolbarTitle.setText(getString(R.string.toolbar_title));
            mFabDownload.show();
            mFolderAdapter.notifyDataSetChanged();
            mPagerFragment = null;
            mGridFragment = null;
        } else {
            mFabDownload.hide();
            mProgressbar.setVisibility(View.INVISIBLE);
            mBtnBackPress.setVisibility(View.VISIBLE);
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) mPagerFragment = null;
        }

    }

    @Override
    public void onImageClicked(boolean mIsFooterHidden) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (mIsFooterHidden) {
            mToolbar.animate()
                    .translationY(-mToolbar.getHeight())
                    .setDuration(200);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.color_black));
        } else {
            mToolbar.animate().translationY(0).setDuration(200);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primaryDarkColor));
        }
    }

    @Override
    public void onImageRename(int position, String folderName, String newFileName) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            File img = new File(mImageData.get(folderName).get(position).getPath());
            String imgName = img.getName();
            String pathFolder = mImageData.get(folderName).get(position).getPath().replace(imgName, "");
            String fileType = imgName.substring(imgName.indexOf("."), imgName.length());

            boolean rename = false;
            if (img.exists()) {
                String newName = newFileName.trim() + fileType.trim();
                File fileName = new File(pathFolder, newName);
                rename = img.renameTo(fileName);
                if (rename) {
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(img)));
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(fileName)));
                    MediaScannerConnection.scanFile(getApplicationContext(),
                            new String[]{fileName.getAbsolutePath()}, null,
                            (s, uri) -> {
                                getImageData();
                                if (mPagerFragment != null) {
                                    Intent intent = new Intent(Constants.FILTER_PAGER_RECEIVER);
                                    intent.putExtra(Constants.EXTRAS_NEW_IMAGE_NAME, fileName.getName());
                                    intent.putExtra(Constants.EXTRAS_IMAGE_POSITION, position);
                                    sendBroadcast(intent);
                                }
                            });
                }
            }

            Log.i("Item Rename", "onImageRename: File With New Name - " + img.getName() + " " + rename);
        }

    }

    @Override
    public void onDeleteImage(int position, String folder) {
        deleteImage(position, folder);
    }

    @Override
    public void onFragmentChanged(String tbTitle) {
        mToolbarTitle.setText(tbTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFolderAdapter != null) mFolderAdapter.notifyDataSetChanged();
        int permissionCheckRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheckRead != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        onBackStackChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            getImageData();
            int permissionCheckWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }
}
