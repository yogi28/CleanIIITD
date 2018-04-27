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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ck on 17-03-2018.
 */


public class Utilities {

    private static String dateString;

    public static String getUserKey(String email) {
        String[] key = email.split("@");
        return key[0];
    }

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
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);


    }

    public static String getTimeSlot(int slot) {
        switch (slot) {
            case 1:
                return "9:00 AM - 10:00 AM";
            case 2:
                return "1:00 PM - 2:00 PM";
            case 3:
                return "7:00 PM - 8:00 PM";
            default:
                return "Unknown Slot";
        }
    }

    public static String getDateString(long timeInMillis) {
        Date date = new Date(timeInMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        return simpleDateFormat.format(date);
    }

    public enum Status {
        EARLY, PENDING, DONE, LATE
    }

    public static ArrayList<Status> inWhichSlot() throws ParseException {
        Date date = new Date();
        String[] start_times = new String[]{"08:00", "12:00", "18:00"};
        String[] end_times = new String[]{"11:00", "15:00", "21:00"};

        SimpleDateFormat sdf = new SimpleDateFormat("HH:ss", Locale.ENGLISH);

        Date now = sdf.parse(sdf.format(date));

        ArrayList<Status> statuses = new ArrayList<>();

        for (int i = 0; i < start_times.length; i++) {
            Date d_start = sdf.parse(start_times[i]);
            Date d_end = sdf.parse(end_times[i]);
            if (now.before(d_start)) {
                statuses.add(Status.EARLY);
            } else if (d_start.before(now) && d_end.after(now)) {
                statuses.add(Status.PENDING);
            } else if (now.after(d_end)) {
                statuses.add(Status.LATE);
            }
        }
        return statuses;
    }


}
