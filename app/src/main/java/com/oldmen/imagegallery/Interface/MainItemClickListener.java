package com.oldmen.imagegallery.Interface;

import com.oldmen.imagegallery.Model.ImageModel;

import java.util.ArrayList;

/**
 * Created by MVP on 02.12.2017.
 */

public interface MainItemClickListener {
    void onFolderClicked(int position);
    void onGridItemClicked(int position, String folderTitle, ArrayList<ImageModel> mImgModel);
    void onDeleteImage(int position, String folder);
}
