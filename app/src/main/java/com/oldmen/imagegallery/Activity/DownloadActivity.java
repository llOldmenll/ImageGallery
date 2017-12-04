package com.oldmen.imagegallery.Activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.oldmen.imagegallery.Adapter.GridAdapter;
import com.oldmen.imagegallery.Api.RetrofitClient;
import com.oldmen.imagegallery.Fragment.PagerFragment;
import com.oldmen.imagegallery.Interface.DownloadItemClickListener;
import com.oldmen.imagegallery.Interface.FragmentChangeListener;
import com.oldmen.imagegallery.Model.Hit;
import com.oldmen.imagegallery.Model.PixabayModel;
import com.oldmen.imagegallery.R;
import com.oldmen.imagegallery.Utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadActivity extends AppCompatActivity implements FragmentChangeListener,
        FragmentManager.OnBackStackChangedListener, PagerFragment.PagerFragmentListener,
        DownloadItemClickListener{

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
    @BindView(R.id.toolbar_title_main)
    TextView mToolbarTitle;

    private GridAdapter mGridAdapter;
    private ArrayList<Hit> mHitList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState != null && savedInstanceState.containsKey(Constants.EXTRAS_HIT_LIST))
            mHitList = savedInstanceState.getParcelableArrayList(Constants.EXTRAS_HIT_LIST);

        initToolbar();
        initRecycler();
        if (mHitList.size() > 0) {
            searchStateEnd();
        } else {
            mRecycler.setVisibility(View.INVISIBLE);
            mIcKeyboard.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void initToolbar() {
        mBtnBackPressToolbar.setOnClickListener(view -> onBackPressed());
        mToolbarSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("Search View", "onQueryTextSubmit: " + query);
                findImgByTag(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initRecycler() {
        mGridAdapter = new GridAdapter(this, mHitList);
        mRecycler.setLayoutManager(new GridLayoutManager(this,
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 4 : 7));
        mRecycler.setAdapter(mGridAdapter);
    }

    private void searchStateStart() {
        mRecycler.setVisibility(View.INVISIBLE);
        mIcKeyboard.setVisibility(View.INVISIBLE);
        mProgressbar.setVisibility(View.VISIBLE);
    }

    private void searchStateEnd() {
        mRecycler.setVisibility(View.VISIBLE);
        mIcKeyboard.setVisibility(View.INVISIBLE);
        mProgressbar.setVisibility(View.INVISIBLE);
        mGridAdapter.notifyDataSetChanged();
        mToolbarSearch.clearFocus();
    }

    private void findImgByTag(String tag) {
        searchStateStart();
        Call<PixabayModel> call = RetrofitClient.getApiService(this).findImg(Constants.PIXABAY_KEY, tag);
        call.enqueue(new Callback<PixabayModel>() {
            @Override
            public void onResponse(@NonNull Call<PixabayModel> call, @NonNull Response<PixabayModel> response) {
                if (response.body() != null && response.body().getmHits() != null) {
                    mHitList = response.body().getmHits();
                    for (Hit hit : mHitList) {
                        Log.i("Find img by Tag", "onResponse: " + hit.getmPreviewURL());
                    }
                    initRecycler();
                    searchStateEnd();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PixabayModel> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.EXTRAS_HIT_LIST, mHitList);
    }

    @Override
    public void onFragmentChanged(String tbTitle) {
        if (tbTitle != null)
            mToolbarTitle.setText(tbTitle);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mToolbarTitle.setVisibility(View.GONE);
            mToolbarSearch.setVisibility(View.VISIBLE);
        } else {
            mToolbarTitle.setVisibility(View.VISIBLE);
            mToolbarSearch.setVisibility(View.GONE);
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

    }

    @Override
    public void onGridItemClicked(int position, ArrayList<Hit> mHits) {
        PagerFragment pagerFragment = PagerFragment.newInstance(position, mHits);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_download, pagerFragment)
                .addToBackStack(Constants.FRAGMENT_PAGER_TAG)
                .commit();
    }
}
