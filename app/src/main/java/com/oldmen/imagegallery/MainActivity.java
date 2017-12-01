package com.oldmen.imagegallery;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_back_press_toolbar_main)
    ImageButton mBtnBackPress;
    @BindView(R.id.toolbar_title_main)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar_search_main)
    SearchView mToolbarSearch;
    @BindView(R.id.folder_recycler_main)
    RecyclerView mFolderRecycler;

    private ArrayList<String> mFolderTitle = new ArrayList<>();
    private HashMap<String, ArrayList<ImageModel>> mImageData = new HashMap<>();
    private FolderAdapter mFolderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        getImagesData();
        mFolderAdapter = new FolderAdapter(this, mFolderTitle, mImageData);
        mFolderRecycler.setLayoutManager(new LinearLayoutManager(this));
        mFolderRecycler.setAdapter(mFolderAdapter);
    }

    private void getImagesData() {
        mFolderTitle.clear();
        mImageData.clear();
        mFolderTitle.add(getString(R.string.all_title));
        mImageData.put(getString(R.string.all_title), new ArrayList<>());

        String[] projection = new String[]{
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };

        Uri storageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cur = managedQuery(storageUri, projection, null, null, null);

        Log.i("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String title;
            String imgUri;
            String bucket;
            String date;

            do {
                bucket = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                date = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                imgUri = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.DATA));
                title = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

                Log.i("ListingImages", " bucket=" + bucket
                        + "\ndate_taken=" + date + "\nDATA=" + imgUri
                        + "\nImage title=" + title);

                mImageData.get(getString(R.string.all_title)).add(new ImageModel(imgUri, title, date));

                if (mFolderTitle.contains(bucket)) {
                    mImageData.get(bucket).add(new ImageModel(imgUri, title, date));
                } else {
                    ArrayList<ImageModel> imgList = new ArrayList<>();
                    imgList.add(new ImageModel(imgUri, title, date));
                    mFolderTitle.add(bucket);
                    mImageData.put(bucket, imgList);
                }

            } while (cur.moveToNext());
        }

        Log.i("ListingImages", "getImagesData: mFolderTitle length" + mFolderTitle.size());
        Log.i("ListingImages", "getImagesData: mImageData length" + mImageData.size());
    }

}
