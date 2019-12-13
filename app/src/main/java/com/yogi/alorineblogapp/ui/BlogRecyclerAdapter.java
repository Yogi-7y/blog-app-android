package com.yogi.alorineblogapp.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yogi.alorineblogapp.R;
import com.yogi.alorineblogapp.model.Blog;

import java.util.List;



public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    private static final String TAG = BlogRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<Blog> blogList;

    public BlogRecyclerAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.blog_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecyclerAdapter.ViewHolder holder, int position) {
        Blog blog = blogList.get(position);
        String imageUrl;

        holder.title.setText(blog.getTitle());
        holder.description.setText(blog.getDescription());
//        holder.name.setText(blog.getUsername());

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(blog.getTimestamp().getSeconds() * 1000);
        holder.timeStamp.setText(timeAgo);

        imageUrl = blog.getImageUri();
        Log.d(TAG, "onBindViewHolder: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, description, timeStamp, name;
        public ImageView imageView;
        String userId, username;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            context = context;

            title = itemView.findViewById(R.id.blog_title_list);
            description = itemView.findViewById(R.id.blog_description_list);
            timeStamp = itemView.findViewById(R.id.blog_time_stamp_list);
            imageView = itemView.findViewById(R.id.blog_image_list);

        }
    }
}
