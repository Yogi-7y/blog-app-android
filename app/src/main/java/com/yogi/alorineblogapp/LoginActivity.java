package com.yogi.alorineblogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.yogi.alorineblogapp.model.UserAPI;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button loginButton, registerButton;
    private TextInputLayout emailEditText, passwordEditText;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email_text_input_layout);
        passwordEditText = findViewById(R.id.password_text_input_layout);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button_login);
        progressBar = findViewById(R.id.progress_bar_login);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(emailEditText.getEditText().getText().toString().trim(),
                        passwordEditText.getEditText().getText().toString().trim());
            }
        });

    }

    private void loginUser(String email, String password) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                final String currentUserId = user.getUid();

                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                }
                                                assert queryDocumentSnapshots != null;
                                                if(!queryDocumentSnapshots.isEmpty()) {
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        UserAPI userAPI = UserAPI.getInstance();
                                                        userAPI.setUsername(snapshot.getString("username"));
                                                        userAPI.setUserId(currentUserId);

                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                                    finish();
                                                }
                                            }
                                        });
                                
                            } else {
                                Log.d(TAG, "onComplete: task failed" );
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        } else {
            Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
        }
    }
}
