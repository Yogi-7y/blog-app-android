package com.yogi.alorineblogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Blob;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.yogi.alorineblogapp.model.Blog;
import com.yogi.alorineblogapp.model.UserAPI;
import com.yogi.alorineblogapp.ui.BlogRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private List<Blog> blogList;
    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Blog");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        blogList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        collectionReference
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for(QueryDocumentSnapshot blogs : queryDocumentSnapshots) {
                                Blog blog = blogs.toObject(Blog.class);
                                blogList.add(blog);
                            }

                            blogRecyclerAdapter = new BlogRecyclerAdapter(HomeActivity.this, blogList);
                            recyclerView.setAdapter(blogRecyclerAdapter);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HomeActivity.this, "No Blogs Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                blogRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // Take user to add blog
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(HomeActivity.this, BlogActivity.class));

                }
                break;

            case R.id.action_sign_out:
                // User Sign out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

//        collectionReference
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            for(QueryDocumentSnapshot blogs : queryDocumentSnapshots) {
//                                Blog blog = blogs.toObject(Blog.class);
//                                blogList.add(blog);
//                            }
//
//                            blogRecyclerAdapter = new BlogRecyclerAdapter(HomeActivity.this, blogList);
//                            recyclerView.setAdapter(blogRecyclerAdapter);
//                            blogRecyclerAdapter.notifyDataSetChanged();
//                        } else {
//                            Toast.makeText(HomeActivity.this, "No Blogs Available", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//
//                    }
//                });
    }
}
