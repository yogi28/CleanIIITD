package com.example.spark.cleaniiitd.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.UploadImage;
import com.example.spark.cleaniiitd.adapters.ImageAdapter;
import com.example.spark.cleaniiitd.utilities.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckListActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 1;
    private Button uploadButton;
    private TextView washroomText;
    private ProgressBar mProgressBar;
    //    private LinearLayout uploadImageLayout;
    private RecyclerView mRecyclerView;
    private ImageAdapter imageAdapter;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ArrayList<Uri> allImagesUri = new ArrayList<>();
    private ArrayList<String> imagePaths;
    private String TAG = CheckListActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        Intent superviseTimeIntent = getIntent();
        String supervisionTime = superviseTimeIntent.getStringExtra("superviseTime");
        String washroomId = superviseTimeIntent.getStringExtra("washroom_id");
        String job_date = superviseTimeIntent.getStringExtra("date");
        imagePaths = new ArrayList<>();


        uploadButton = findViewById(R.id.uploadButton);
        washroomText = findViewById(R.id.washroom_id);
        washroomText.setText(washroomId + ", " + job_date + '\n' + supervisionTime);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView = findViewById(R.id.image_list);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        addImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dispatchTakePictureIntent();
////                openCamera();
//            }
//        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onSuccess");
                uploadImage();
            }
        });

        imageAdapter = new ImageAdapter(this);
        imageAdapter.setOnItemClickListener(new ImageAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (view instanceof ImageView) {
                    imageAdapter.deleteImage(position);
                } else if (view instanceof ViewGroup) {
                    dispatchTakePictureIntent();
                }
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        mRecyclerView.setAdapter(imageAdapter);
    }

//    private void openCamera() {
//        Log.d(TAG, "openCamera");
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, REQUEST_CAMERA);
//    }

    /* REFERENCE: Android Developers: https://developer.android.com/training/camera/photobasics.html */

    String mCurrentPhotoPath;

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "CW_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Nothing
                Log.d(TAG, "New Func: " + "Error Occurred while creating file!");
            }
            // Continue only if the File was successfully created
            Uri photoURI;
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        this.getPackageName() + ".provider",
                        photoFile);
                allImagesUri.add(photoURI);
                imagePaths.add(mCurrentPhotoPath);
                Log.d(TAG, "New Func: " + photoFile.getAbsolutePath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
//            Bitmap bm = (Bitmap) data.getExtras().get("data");
//            Log.d(TAG, "Result from camera received " + bm.getConfig());
//        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            imageAdapter.updateImageList(allImagesUri, imagePaths);


//            imageAdapter.updateImageList(bitmapList);
//            Picasso.with(this).load(imageUri).into(newImage);
//            grantUriPermission("com.example.spark.cleaniiitd", imageUri, 0);
//            uploadedImages.setImageURI(imageUri);
        }
    }

    private String getImageExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        mProgressBar.setVisibility(View.VISIBLE);
        final Activity activity = this;
        if (!allImagesUri.isEmpty()) {
            for (final Uri imageUri : allImagesUri) {
                Log.d(TAG, imageUri.toString());
                final String filename = imageUri.getPathSegments().get(imageUri.getPathSegments().size() - 1);
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getImageExtension(imageUri));
                fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 5000);
                        Log.d(TAG, "onSuccess");
                        Toast.makeText(activity, "Upload Successful", Toast.LENGTH_SHORT).show();
                        UploadImage uploadImage = new UploadImage(filename, taskSnapshot.getDownloadUrl().toString());
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(uploadImage);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure");
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "onProgress");
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                mProgressBar.setProgress((int) progress);
                            }
                        });
            }
        } else {
            Toast.makeText(this, "No image Selected!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}