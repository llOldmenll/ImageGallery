package com.oldmen.imagegallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MVP on 30.11.2017.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridHolder> {

    private Context mContext;
    private ArrayList<ImageModel> mImgModel;

    public GridAdapter(Context mContext, ArrayList<ImageModel> mImgModel) {
        this.mContext = mContext;
        this.mImgModel = mImgModel;
    }

    @Override
    public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item, parent, false);
        return new GridHolder(view);
    }

    @Override
    public void onBindViewHolder(GridHolder holder, int position) {
        holder.bindView(mImgModel.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        return mImgModel.size();
    }

    class GridHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img_grid_item)
        ImageView mImg;

        public GridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bindView(String path){
            initImgByGlide(path);
        }

        private void initImgByGlide(String path) {
            GlideApp.with(mContext)
                    .load("file://" + path)
                    .override(156)
                    .into(mImg);
        }
    }
}
