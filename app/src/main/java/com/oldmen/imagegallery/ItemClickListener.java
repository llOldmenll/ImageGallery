package com.oldmen.imagegallery;

import java.util.ArrayList;

/**
 * Created by MVP on 02.12.2017.
 */

public interface ItemClickListener {
    void onFolderClicked(int position);
    void onGridItemClicked(int position, ArrayList<ImageModel> mImgModel);
}
