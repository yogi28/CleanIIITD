package com.example.spark.cleaniiitd.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.RectF;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.CleanWiseApp;
import com.example.spark.cleaniiitd.R;
import com.example.spark.cleaniiitd.pojo.Job;
import com.example.spark.cleaniiitd.pojo.Record;
import com.example.spark.cleaniiitd.pojo.Supervisor;
import com.example.spark.cleaniiitd.pojo.UploadImage;
import com.example.spark.cleaniiitd.adapters.ImageAdapter;
import com.example.spark.cleaniiitd.utilities.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.internal.Util;

public class CheckListActivity extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 1;
    private Button uploadButton;
    private TextView washroomText;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private ImageAdapter imageAdapter;
    private ProgressDialog progressDialog;

    private CleanWiseApp application;
    private Supervisor supervisor;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mJobsRef;
    private DatabaseReference mRecordRef;
    private ArrayList<Uri> allImagesUri = new ArrayList<>();
    private ArrayList<String> imagePaths;
    private String TAG = CheckListActivity.class.getSimpleName();
    private String currDate;
    private String mCurrentPhotoPath;

    private int slot;
    private String washroomId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        Intent superviseTimeIntent = getIntent();
        String supervisionTime = superviseTimeIntent.getStringExtra("superviseTime");
        washroomId = superviseTimeIntent.getStringExtra("washroom_id");
        currDate = superviseTimeIntent.getStringExtra("date");
        slot = superviseTimeIntent.getIntExtra("slot", 0);
        imagePaths = new ArrayList<>();

        application = CleanWiseApp.getInstance();

        uploadButton = findViewById(R.id.uploadButton);
        washroomText = findViewById(R.id.washroom_id);
        washroomText.setText(washroomId + ", " + currDate + '\n' + supervisionTime);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView = findViewById(R.id.image_list);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mJobsRef = application.getFirebaseDatabaseInstance().getReference("jobs");
        mRecordRef = application.getFirebaseDatabaseInstance().getReference("records");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    private void showProgressDialog(String msg, int progress) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        if (progress != -1)
            progressDialog.setProgress(progress);
        progressDialog.show();
    }

    private void setProgressDialogProgress(int progress) {
        progressDialog.setMessage("Uploading Image...  " + progress + "%");
    }

    private void hideProgressDialog() {
        progressDialog.hide();
    }


    /* REFERENCE: Android Developers: https://developer.android.com/training/camera/photobasics.html */

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
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
        showProgressDialog("Uploading Image", 0);
        mProgressBar.setVisibility(View.VISIBLE);
        final Activity activity = this;
        if (!allImagesUri.isEmpty()) {
            final Job job = new Job();
            job.setSlot(slot);
            job.setWashroomId(washroomId);
            job.setSupervisorId(application.getAppUser(null).getId());
            final ArrayList<String> imageUrls = new ArrayList<>();

            for (Uri imageUri : allImagesUri) {
                Log.d(TAG, imageUri.toString());
//                String filename = imageUri.getPathSegments().get(imageUri.getPathSegments().size() - 1);
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getImageExtension(imageUri));
                fileReference.putFile(imageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, String.valueOf(taskSnapshot.getBytesTransferred()));
                        double ratio = ((float) taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        setProgressDialogProgress((int) (ratio * 100));
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        imageUrls.add(taskSnapshot.getDownloadUrl().toString());
                        if (imageUrls.size() == allImagesUri.size()) {
                            job.setImages(imageUrls);
                            supervisor = application.getAppUser(null);
                            Log.d(TAG, supervisor.getId() + " : here");
                            mRecordRef.child(currDate).child(washroomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.d(TAG, dataSnapshot.toString());
                                    if (dataSnapshot.hasChild(String.valueOf(slot))) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            if (ds.getKey().equals(String.valueOf(slot))) {
                                                Record record = ds.getValue(Record.class);
                                                Log.d(TAG, supervisor.getId() + " : " + record.getSupervisorId());
                                                if (record != null && record.getSupervisorId().equals(supervisor.getId())) {
                                                    String jobId = record.getJobId();
                                                    Log.d(TAG, jobId);
                                                    job.setId(jobId);
                                                    record.setJobId(job.getId());
                                                    record.setStatus(Utilities.Status.DONE);
                                                    record.setSupervisorId(job.getSupervisorId());
                                                    mJobsRef.child(jobId).setValue(job).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d(TAG, "Submitted");
                                                        }
                                                    });
                                                    mJobsRef.child(jobId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                                    mRecordRef.child(currDate).child(job.getWashroomId()).child(String.valueOf(slot)).setValue(record);
                                                } else {
                                                    Toast.makeText(CheckListActivity.this, "Already Registered! Not authorized to edit this entry.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                    } else {
                                        String jobId = mJobsRef.push().getKey();
                                        job.setId(jobId);

                                        Record record = new Record();
                                        record.setJobId(job.getId());
                                        record.setStatus(Utilities.Status.DONE);
                                        record.setSupervisorId(job.getSupervisorId());

                                        mJobsRef.child(jobId).setValue(job);
                                        mJobsRef.child(jobId).child("timestamp").setValue(ServerValue.TIMESTAMP);
                                        mRecordRef.child(currDate).child(job.getWashroomId()).child(String.valueOf(slot)).setValue(record);

                                        //TODO: Add this job id to users past records
                                        if (supervisor != null) {
                                            supervisor.addJob(job.getId());
                                            application.getAppUser(supervisor);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    hideProgressDialog();
                                }
                            });

//                            mProgressBar.setVisibility(View.INVISIBLE);
                            hideProgressDialog();
                            Toast.makeText(CheckListActivity.this, "Job Added!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        }, 5000);
//                        Log.d(TAG, "onSuccess");
//                        Toast.makeText(activity, "Upload Successful", Toast.LENGTH_SHORT).show();
//                        UploadImage uploadImage = new UploadImage(filename, taskSnapshot.getDownloadUrl().toString());
//                        String uploadId = mDatabaseRef.push().getKey();
//                        mDatabaseRef.child(uploadId).setValue(uploadImage);
//                        mProgressBar.setVisibility(View.INVISIBLE);
//                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure");
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onProgress");
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                    }
                });
            }

        } else {
            Toast.makeText(this, "No image Added!", Toast.LENGTH_SHORT).show();
            hideProgressDialog();
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