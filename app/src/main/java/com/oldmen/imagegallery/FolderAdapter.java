package com.oldmen.imagegallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by MVP on 01.12.2017.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderHolder> {

    private Context mContext;
    private ArrayList<String> mFolderTitles;
    private HashMap<String, ArrayList<ImageModel>> mImageData;

    public FolderAdapter(Context mContext, ArrayList<String> mFolderTitles, HashMap<String, ArrayList<ImageModel>> mImageData) {
        this.mContext = mContext;
        this.mFolderTitles = mFolderTitles;
        this.mImageData = mImageData;
    }

    @Override
    public FolderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.folder_item, parent, false);
        return new FolderHolder(view);
    }

    @Override
    public void onBindViewHolder(FolderHolder holder, int position) {
        holder.bindView(mFolderTitles.get(position), mImageData.get(mFolderTitles.get(position)));
    }

    @Override
    public int getItemCount() {
        return mFolderTitles.size();
    }

    class FolderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_front_folder_item)
        ImageView mImgFront;
        @BindView(R.id.img_back_left_folder_item)
        ImageView mImgBackLeft;
        @BindView(R.id.img_back_right_folder_item)
        ImageView mImgBackRight;
        @BindView(R.id.title_folder_item)
        TextView mTitle;
        @BindView(R.id.count_folder_item)
        TextView mCount;

        FolderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindView(String title, ArrayList<ImageModel> imgData) {
            int dataSize = imgData.size();
            mTitle.setText(title);
            mCount.setText(String.valueOf(imgData.size()));
            initImgByGlide(imgData.get(dataSize - 1).getPath(), mImgFront);

            if (imgData.size() > 2) {
                initImgByGlide(imgData.get(dataSize - 2).getPath(), mImgBackLeft);
                initImgByGlide(imgData.get(dataSize - 3).getPath(), mImgBackRight);
            } else if (imgData.size() > 1) {
                initImgByGlide(imgData.get(dataSize - 2).getPath(), mImgBackLeft);
                GlideApp.with(mContext).clear(mImgBackRight);
            } else {
                GlideApp.with(mContext).clear(mImgBackLeft);
                GlideApp.with(mContext).clear(mImgBackRight);
            }
            Log.i(TAG, "mImgFront: " + imgData.get(imgData.size() - 1).getPath());
        }

        private void initImgByGlide(String path, ImageView imgView) {
            GlideApp.with(mContext)
                    .load("file://" + path)
                    .override(150)
                    .centerCrop()
                    .placeholder(mContext.getResources().getDrawable(R.drawable.default_img_background))
                    .into(imgView);
        }
    }
}
