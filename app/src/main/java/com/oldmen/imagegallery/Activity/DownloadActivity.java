package com.oldmen.imagegallery.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.oldmen.imagegallery.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DownloadActivity extends AppCompatActivity {

    @BindView(R.id.btn_back_press_toolbar_download)
    ImageButton mBtnBackPressToolbar;
    @BindView(R.id.toolbar_search_download)
    SearchView mToolbarSearch;
    @BindView(R.id.toolbar_download)
    Toolbar mToolbar;
    @BindView(R.id.progressbar_download)
    ProgressBar mProgressbar;
    @BindView(R.id.recycler_download)
    RecyclerView mRecycler;
    @BindView(R.id.fragment_container_download)
    FrameLayout mFragmentContainer;
    @BindView(R.id.ic_keyboard_download)
    ImageView mIcKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);


    }


}
