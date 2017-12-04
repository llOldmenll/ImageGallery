package com.oldmen.imagegallery.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MVP on 04.12.2017.
 */

public class Hit implements Parcelable{
    @SerializedName("tags")
    @Expose
    private String mTags;
    @SerializedName("previewURL")
    @Expose
    private String mPreviewURL;
    @SerializedName("webformatURL")
    @Expose
    private String mWebformatURL;

    protected Hit(Parcel in) {
        mTags = in.readString();
        mPreviewURL = in.readString();
        mWebformatURL = in.readString();
    }

    public static final Creator<Hit> CREATOR = new Creator<Hit>() {
        @Override
        public Hit createFromParcel(Parcel in) {
            return new Hit(in);
        }

        @Override
        public Hit[] newArray(int size) {
            return new Hit[size];
        }
    };

    public String getmTags() {
        return mTags;
    }

    public void setmTags(String mTags) {
        this.mTags = mTags;
    }

    public String getmPreviewURL() {
        return mPreviewURL;
    }

    public void setmPreviewURL(String mPreviewURL) {
        this.mPreviewURL = mPreviewURL;
    }

    public String getmWebformatURL() {
        return mWebformatURL;
    }

    public void setmWebformatURL(String mWebformatURL) {
        this.mWebformatURL = mWebformatURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTags);
        parcel.writeString(mPreviewURL);
        parcel.writeString(mWebformatURL);
    }
}
