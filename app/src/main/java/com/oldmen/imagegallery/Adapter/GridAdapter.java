package com.oldmen.imagegallery.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oldmen.imagegallery.GlideApp;
import com.oldmen.imagegallery.Interface.DownloadItemClickListener;
import com.oldmen.imagegallery.Interface.MainItemClickListener;
import com.oldmen.imagegallery.Model.Hit;
import com.oldmen.imagegallery.Model.ImageModel;
import com.oldmen.imagegallery.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MVP on 30.11.2017.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridHolder> {

    private Context mContext;
    private boolean isMainActivity;
    private ArrayList<ImageModel> mImgModel;
    private ArrayList<Hit> mHit;
    private MainItemClickListener mMainListener;
    private DownloadItemClickListener mDownloadListener;
    private String mFolderTitle;

    public GridAdapter(Context mContext, String mFolderTitle, ArrayList<ImageModel> mImgModel) {
        isMainActivity = true;
        this.mContext = mContext;
        this.mImgModel = mImgModel;
        this.mFolderTitle = mFolderTitle;
        if (mContext instanceof MainItemClickListener) mMainListener = (MainItemClickListener) mContext;
    }

    public GridAdapter(Context mContext, ArrayList<Hit> mHit) {
        isMainActivity = false;
        this.mContext = mContext;
        this.mHit = mHit;
        if(mContext instanceof DownloadItemClickListener)
        mDownloadListener = (DownloadItemClickListener) mContext;
    }

    @Override
    public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new GridHolder(view);
    }

    @Override
    public void onBindViewHolder(GridHolder holder, int position) {

        if (isMainActivity) {
            holder.bindView(mImgModel.get(position).getPath());
            holder.itemView.setOnClickListener(view -> mMainListener.onGridItemClicked(position, mFolderTitle, mImgModel));
            holder.itemView.setOnLongClickListener(view -> {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setTitle(mContext.getString(R.string.delete))
                        .setIcon(mContext.getDrawable(R.drawable.ic_action_delete))
                        .setMessage(String.format(mContext.getString(R.string.delete_image_dialog),
                                mImgModel.get(position).getTitle()))
                        .setPositiveButton(mContext.getString(R.string.delete),
                                (dialogInterface, i) -> {
                                    mMainListener.onDeleteImage(position, mFolderTitle);
                                    notifyDataSetChanged();
                                })
                        .setNegativeButton(mContext.getString(R.string.cancel), null)
                        .create().show();
                return false;
            });
        } else {
            holder.bindView(mHit.get(position).getmPreviewURL());
            holder.itemView.setOnClickListener(view -> mDownloadListener.onGridItemClicked(position, mHit));
        }
    }

    @Override
    public int getItemCount() {
        if (isMainActivity) return mImgModel.size();
        else return mHit.size();
    }

    class GridHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_grid_item)
        ImageView mImg;

        public GridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindView(String path) {
            if (isMainActivity) loadLocalImgByGlide(path);
            else loadWebImgByGlide(path);
        }

        private void loadLocalImgByGlide(String path) {
            GlideApp.with(mContext)
                    .load("file://" + path)
                    .override(156)
                    .into(mImg);
        }

        private void loadWebImgByGlide(String url) {
            GlideApp.with(mContext)
                    .load(url)
                    .override(156)
                    .into(mImg);
        }
    }
}
