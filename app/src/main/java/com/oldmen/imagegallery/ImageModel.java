package com.oldmen.imagegallery;

/**
 * Created by MVP on 01.12.2017.
 */

public class ImageModel {

    private String path;
    private String title;
    private String date;

    public ImageModel(String path, String title, String date) {
        this.path = path;
        this.title = title;
        this.date = date;
    }

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
}
