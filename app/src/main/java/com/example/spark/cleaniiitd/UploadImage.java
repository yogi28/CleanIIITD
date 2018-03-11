package com.example.spark.cleaniiitd;

/**
 * Created by spark on 11/3/18.
 */

public class UploadImage {

    private String mName;
    private String mImageUrl;

    public UploadImage(){
        // Empty constructor
    }

    public UploadImage(String name, String url){
        if (name.trim().equals("")){
            name = "No Image";
        }
        mName = name;
        mImageUrl = url;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
