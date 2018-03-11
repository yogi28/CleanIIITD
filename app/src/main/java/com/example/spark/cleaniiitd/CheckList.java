package com.example.spark.cleaniiitd;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class CheckList extends AppCompatActivity {

    public static final int REQUEST_CAMERA = 1;
    private Button uploadButton;
    private Button captureImageButton;
    private ImageView uploadedImages;
    private Uri imageUri;
    private ProgressBar mProgressBar;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        Intent superviseTimeIntent = getIntent();
        String superviseTime = superviseTimeIntent.getStringExtra("superviseTime");

        uploadButton = findViewById(R.id.uploadButton);
        captureImageButton = findViewById(R.id.captureImageButton);
        uploadedImages = findViewById(R.id.uploadedImageView);
        mProgressBar = findViewById(R.id.progressBar);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("IMAGE", "onSuccess");
                uploadImage();

            }
        });

        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    private void openCamera()
    {
        Log.d("IMAGE", "openCamera");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data!=null && data.getData()!=null)
        {
            String test = "TEST";
            imageUri = data.getData();
            if (imageUri!=null){
                test = "TESTING";
            }
            Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
            Picasso.with(this).load(imageUri).into(uploadedImages);
//            grantUriPermission("com.example.spark.cleaniiitd", imageUri, 0);
//            uploadedImages.setImageURI(imageUri);
        }
    }

    private String getImageExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final Activity activity = this;
        if (imageUri!=null){
            StorageReference fileRefernce = mStorageRef.child(System.currentTimeMillis()
            +"."+getImageExtension(imageUri));
            fileRefernce.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(0);
                        }
                    }, 5000);
                    Log.d("IMAGE", "onSuccess");
                    Toast.makeText(activity, "Upload Successful", Toast.LENGTH_SHORT).show();
                    UploadImage uploadImage = new UploadImage("IMAGE_1", taskSnapshot.getDownloadUrl().toString());
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(uploadImage);

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("IMAGE", "onFailure");
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            })
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("IMAGE", "onProgress");
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mProgressBar.setProgress((int) progress);
                }
            });
        }else {
            Toast.makeText(this, "No image Selected!", Toast.LENGTH_SHORT).show();
        }
    }
}
