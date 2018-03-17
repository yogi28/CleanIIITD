package com.example.spark.cleaniiitd;

/**
 * Created by spark on 11/3/18.
 */

public class UploadImage {

    private String name;
    private String url;

    public UploadImage() {
        // Empty constructor
    }

    public UploadImage(String name, String url) {
        if (name.trim().equals("")) {
            name = "No Image";
        }
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
