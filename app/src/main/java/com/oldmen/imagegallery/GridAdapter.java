package com.oldmen.imagegallery;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MVP on 30.11.2017.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridHolder> {


    @Override
    public GridHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(GridHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class GridHolder extends RecyclerView.ViewHolder{

        public GridHolder(View itemView) {
            super(itemView);
        }
    }
}
