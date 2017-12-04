package com.oldmen.imagegallery.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by MVP on 04.12.2017.
 */

public class PixabayModel {

    @SerializedName("hits")
    @Expose
    private ArrayList<Hit> mHits;

    public ArrayList<Hit> getmHits() {
        return mHits;
    }

    public void setmHits(ArrayList<Hit> mHits) {
        this.mHits = mHits;
    }
}
