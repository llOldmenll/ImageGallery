package com.oldmen.imagegallery.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MVP on 01.12.2017.
 */

public class ImageModel implements Parcelable {

    private String path;
    private String title;
    private String date;
    private String size;

    public ImageModel(String path, String title, String date, String size) {
        this.path = path;
        this.title = title;
        this.date = date;
        this.size = size;
    }


    protected ImageModel(Parcel in) {
        path = in.readString();
        title = in.readString();
        date = in.readString();
        size = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(title);
        parcel.writeString(date);
        parcel.writeString(size);
    }
}
