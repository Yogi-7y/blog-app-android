package com.yogi.alorineblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.primitives.Chars;
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
import java.util.Objects;

public class BlogActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = BlogActivity.class.getSimpleName();
    private static final int GALLERY_CODE = 1;
    private Button postButton;
    private TextInputLayout titleEditText, descriptionEditText;
    private ImageView addImage, backgroundImage;
    private Spinner categorySpinner;
    private ArrayAdapter<CharSequence> adapter;
    String selectedCategory;

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
        categorySpinner = findViewById(R.id.spinner);

        postButton.setOnClickListener(this);
        addImage.setOnClickListener(this);

        adapter = ArrayAdapter.createFromResource(this, R.array.categories, R.layout.spinner_text_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: " + selectedCategory);
                Toast.makeText(BlogActivity.this, "" + selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        final String title = Objects.requireNonNull(titleEditText.getEditText()).getText().toString().trim();
        final String description = Objects.requireNonNull(descriptionEditText.getEditText()).getText().toString().trim();

        if (validateTitle() & validateDescription() & imageURI != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_LONG).show();
            final StorageReference filePath = storageReference
                    .child("blog_images")
                    .child("my_image" + Timestamp.now().getSeconds());

            filePath.putFile(imageURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                            Toast.makeText(BlogActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
//
//                            Blog blog = new Blog(title, description, taskSnapshot.getUploadSessionUri().toString(), currentUserId,
//                                    new Timestamp(new Date()), currentUsername, selectedCategory);
//
//                            collectionReference.add(blog)
//                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                                        @Override
//                                        public void onSuccess(DocumentReference documentReference) {
//                                            Toast.makeText(BlogActivity.this, "Data Successfully Added", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(BlogActivity.this, "OnFailuer Occured", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Blog blog = new Blog(title, description, imageUrl,
                                            currentUserId, new Timestamp(new Date()), currentUsername, selectedCategory);
                                    Log.d(TAG, "onSuccess: Selected Category" + selectedCategory);
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

    private boolean validateTitle() {

        String titleInput = Objects.requireNonNull(titleEditText.getEditText()).getText().toString().trim();

        if (titleInput.isEmpty()) {
            titleEditText.setError("Field can't be empty");
            return false;
        } else {
            titleEditText.setError(null);
            return true;
        }
    }

    private boolean validateDescription() {

        String descriptionInput = Objects.requireNonNull(descriptionEditText.getEditText()).getText().toString().trim();
        Log.d(TAG, "validateDescription: length " + descriptionInput.length());
        if (descriptionInput.isEmpty()) {
            descriptionEditText.setError("Field can't be empty");
            return false;
        } else if (descriptionInput.length() > 10) {
            descriptionEditText.setError("Max limit is 1000 characters");
            return false;
        } else {
            descriptionEditText.setError(null);
            return true;
        }
    }
}
