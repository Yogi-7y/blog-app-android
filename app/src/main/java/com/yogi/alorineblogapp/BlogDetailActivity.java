package com.yogi.alorineblogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yogi.alorineblogapp.model.Blog;

public class BlogDetailActivity extends AppCompatActivity {

    private static final String TAG = BlogDetailActivity.class.getSimpleName();
    private Blog blog;
    private ImageView blogImageView;
    private TextView titleTextView, nameTextView, timeStampTextView, descriptionTextView;

    String title, timeStamp, username, description, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        image = intent.getStringExtra("imageUri");
        timeStamp = intent.getStringExtra("timeStamp");
        title = intent.getStringExtra("title");
        username = intent.getStringExtra("username");

        nameTextView = findViewById(R.id.name_blog_detail);
        titleTextView = findViewById(R.id.title_blog_detail);
        descriptionTextView = findViewById(R.id.description_blog_detail);
        timeStampTextView = findViewById(R.id.time_stamp_blog_detail);
        blogImageView = findViewById(R.id.image_blog_detail);

        nameTextView.setText(username);
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        timeStampTextView.setText(timeStamp);

        Glide.with(getApplicationContext())
                .load(image)
                .into(blogImageView);

    }
}


















