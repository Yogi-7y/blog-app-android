package com.yogi.alorineblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yogi.alorineblogapp.model.Blog;
import com.yogi.alorineblogapp.model.User;
import com.yogi.alorineblogapp.model.UserAPI;

import java.util.Date;

public class BlogActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = BlogActivity.class.getSimpleName();
    private static final int GALLERY_CODE = 1;
    private Button postButton;
    private TextInputLayout titleEditText, descriptionEditText;
    private ImageView addImage, backgroundImage;

    private String currentUserId, currentUsername;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference = db.collection("Blog");
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
//
//        Bundle bundle = getIntent().getExtras();
        final String username = User.getInstance().getUsername();
        Log.d(TAG, "onCreate: Username: " + username);
//        if (bundle != null) {
//            String username = bundle.getString("username");
//            String currentUserID = bundle.getString("currentUserId");
//            String phone = bundle.getString("phone");
//            Log.d(TAG, "onCreate: Username: " + username);
//        } else {
//            Log.d(TAG, "onCreate: Bundle is null");
//        }

        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        postButton = findViewById(R.id.post_button_blog);
        titleEditText = findViewById(R.id.title_blog);
        descriptionEditText = findViewById(R.id.description_blog);
        addImage = findViewById(R.id.background_image_icon_blog);
        backgroundImage = findViewById(R.id.background_image_blog);

        postButton.setOnClickListener(this);
        addImage.setOnClickListener(this);

        if (UserAPI.getInstance() != null) {
            currentUserId = UserAPI.getInstance().getUserId();
            currentUsername = UserAPI.getInstance().getUsername();
            Log.d(TAG, "onCreate: current user id: " + currentUserId);
            Log.d(TAG, "onCreate: current username: " + currentUsername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                } else {
                    Log.d(TAG, "onAuthStateChanged: User null.");
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_button_blog:
                Toast.makeText(this, "Uploading...", Toast.LENGTH_LONG).show();
                saveJournal();
                break;

            case R.id.background_image_icon_blog:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;

        }
    }

    private void saveJournal() {

        final String title = titleEditText.getEditText().getText().toString().trim();
        final String description = descriptionEditText.getEditText().getText().toString().trim();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageURI != null) {
            final StorageReference filePath = storageReference
                    .child("blog_images")
                    .child("my_image" + Timestamp.now().getSeconds());

            filePath.putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = imageURI.toString();
                                    Blog blog = new Blog(title, description, imageUrl,
                                            currentUserId, new Timestamp(new Date()), currentUsername);

                                    collectionReference.add(blog)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(BlogActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(BlogActivity.this, HomeActivity.class);
                                                    startActivity(intent);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: " + e.getMessage());
                                                }
                                            });
                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Toast.makeText(this, "Kindly provide all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageURI = data.getData();
                backgroundImage.setImageURI(imageURI);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
