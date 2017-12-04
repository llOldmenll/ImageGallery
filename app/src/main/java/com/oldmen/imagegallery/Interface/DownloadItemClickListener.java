package com.oldmen.imagegallery.Interface;

import com.oldmen.imagegallery.Model.Hit;

import java.util.ArrayList;

/**
 * Created by MVP on 04.12.2017.
 */

public interface DownloadItemClickListener {

    void onGridItemClicked(int position, ArrayList<Hit> mHits);
}
