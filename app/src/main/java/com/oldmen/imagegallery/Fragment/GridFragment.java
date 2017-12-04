package com.oldmen.imagegallery.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oldmen.imagegallery.Adapter.GridAdapter;
import com.oldmen.imagegallery.Interface.FragmentChangeListener;
import com.oldmen.imagegallery.Model.ImageModel;
import com.oldmen.imagegallery.R;
import com.oldmen.imagegallery.Utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class GridFragment extends Fragment {

    @BindView(R.id.recycler_grid_fragment)
    RecyclerView mRecyclerGrid;

    private Context mContext;
    private Unbinder unbinder;
    private ArrayList<ImageModel> mImgModel;
    private GridAdapter mGridAdapter;
    private String mFolderTitle;
    private FragmentChangeListener mFragmentListener;

    public GridFragment() {
    }

    public static GridFragment newInstance(String folderTitle, ArrayList<ImageModel> mImgModel) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_GRID, mImgModel);
        args.putString(Constants.ARGUMENT_CURRENT_FOLDER_TITLE, folderTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext.registerReceiver(new GridReceiver(), new IntentFilter(Constants.FILTER_GRID_RECEIVER));
        if (getArguments() != null) {
            mImgModel = getArguments().getParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_GRID);
            mFolderTitle = getArguments().getString(Constants.ARGUMENT_CURRENT_FOLDER_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mImgModel != null) {
            mGridAdapter = new GridAdapter(mContext, mFolderTitle, mImgModel);
            mRecyclerGrid.setLayoutManager(new GridLayoutManager(mContext,
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 4 : 7));
            mRecyclerGrid.setAdapter(mGridAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(context instanceof FragmentChangeListener)
            mFragmentListener = (FragmentChangeListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGridAdapter != null)
            mGridAdapter.notifyDataSetChanged();
        if(mFragmentListener != null)
            mFragmentListener.onFragmentChanged(mFolderTitle);
    }

    public class GridReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                mImgModel.remove(intent.getIntExtra(Constants.EXTRAS_IMAGE_POSITION, 0));
                mGridAdapter.notifyDataSetChanged();
            }
        }
    }
}
