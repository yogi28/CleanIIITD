package com.example.spark.cleaniiitd.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ck on 17-03-2018.
 */

public class Utilities {

    public static String getFilename(String name) {
        return (getAppDirectory().getAbsolutePath() + "/" + name + ".jpg");
    }

    public static String saveBitmap(Bitmap bmp, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            Log.d("Utilities", "Bitmap Saved: " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filename;
    }

    public static File getAppDirectory() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "CleanWise/Uploads");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getAppThumbnailDir() {
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), ("CleanWise/Uploads/.thumbs"));
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String getThumbAddress(String name) {
        return getAppThumbnailDir().getAbsolutePath() + "/" + name;
    }

    public static String saveThumbnail(Bitmap bm, String name) {
        FileOutputStream out = null;
        String filename = getThumbAddress(name) + ".jpg";
        Log.i("thumb", filename);
        try {
            out = new FileOutputStream(filename);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filename;
    }

    public static String getFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Cleanwise_" + timeStamp;
        return imageFileName;
    }


    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }


    public static void setPic(ImageView mImageView, String mCurrentPhotoPath, int h, int w) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        targetH = h;
        targetW = w;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;


        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);


    }





}
