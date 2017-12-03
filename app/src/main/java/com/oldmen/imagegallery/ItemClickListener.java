package com.oldmen.imagegallery;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by MVP on 02.12.2017.
 */

public interface ItemClickListener {
    void onFolderClicked(int position);
    void onGridItemClicked(int position, String folderTitle, ArrayList<ImageModel> mImgModel, ImageView imgView);
    void onDeleteImage(int position, String folder);
}
