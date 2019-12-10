package com.yogi.alorineblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yogi.alorineblogapp.model.User;
import com.yogi.alorineblogapp.model.UserAPI;


import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    private TextInputLayout emailEditText, passwordEditText, phoneEditText, usernameEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        Button registerButton = findViewById(R.id.register_button_register);
        emailEditText = findViewById(R.id.email_register);
        usernameEditText = findViewById(R.id.username_register);
        passwordEditText = findViewById(R.id.password_register);
        phoneEditText = findViewById(R.id.phone_register);
        progressBar = findViewById(R.id.progress_bar_register);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {

                } else {
                    Toast.makeText(RegisterActivity.this, "User not logged in..", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(Objects.requireNonNull(emailEditText.getEditText()).getText().toString())
                        && !TextUtils.isEmpty(Objects.requireNonNull(passwordEditText.getEditText()).getText().toString())
                        && !TextUtils.isEmpty(Objects.requireNonNull(usernameEditText.getEditText()).getText().toString())
                        && !TextUtils.isEmpty(Objects.requireNonNull(phoneEditText.getEditText()).getText().toString())) {

                    String email = emailEditText.getEditText().getText().toString().trim();
                    String password = passwordEditText.getEditText().getText().toString().trim();
                    String username = usernameEditText.getEditText().getText().toString().trim();
                    String phone = phoneEditText.getEditText().getText().toString().trim();
                    createUserAccount(email, password, username, phone);
                    Log.d(TAG, "onClick: ");
                } else {
                    Toast.makeText(RegisterActivity.this, "Empty Fields not allowed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void createUserAccount(String email, String password, final String username, final String phone) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;
                                final String currentUserID = currentUser.getUid();
                                User userObj = User.getInstance();
                                userObj.setUserId(currentUserID);
                                userObj.setUsername(username);
                                userObj.setPhone(phone);
                                Log.d(TAG, "onComplete: " + userObj.getUsername());

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "onComplete: task successful"  );

                                                                    String username = Objects.requireNonNull(task.getResult())
                                                                            .getString("username");
                                                                    String phone = task.getResult().getString("phone");

                                                                    UserAPI userAPI = UserAPI.getInstance();
                                                                    userAPI.setPhone(phone);
                                                                    userAPI.setUserId(currentUserID);
                                                                    userAPI.setUsername(username);
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    Intent intent = new Intent(RegisterActivity.this, BlogActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: here");
                                            }
                                        });

                                Toast.makeText(RegisterActivity.this, "User Successfully created", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Toast.makeText(this, "Kindly fill all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
