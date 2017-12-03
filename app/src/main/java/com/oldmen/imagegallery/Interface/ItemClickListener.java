package com.oldmen.imagegallery.Interface;

import android.widget.ImageView;

import com.oldmen.imagegallery.Model.ImageModel;

import java.util.ArrayList;

/**
 * Created by MVP on 02.12.2017.
 */

public interface ItemClickListener {
    void onFolderClicked(int position);
    void onGridItemClicked(int position, String folderTitle, ArrayList<ImageModel> mImgModel, ImageView imgView);
    void onDeleteImage(int position, String folder);
}
