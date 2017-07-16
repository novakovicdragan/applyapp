package com.devdroid.dragan.draganapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // interface listener so we can send event when item is clicked
    public interface PostsListener {
        void onClickPostListener (Post post);
    }
    private final PostsListener listener;
    private List<Post> list_posts;

    private boolean flagBottomLoader = false;

    private final int VIEW_POST = 1;
    private final int VIEW_LOADER = 0;

    public PostsAdapter(PostsListener listener) {
        this.listener = listener;
        list_posts = new ArrayList<>();
    }

    //Set flag of Bottom loader, and notify adapter about its state
    public void setBottomLoaderStatus(boolean active) {
        flagBottomLoader = active;
        this.notifyDataSetChanged();
    }

    // add all posts at once and notify adapter
    public void addPostsList(List<Post> posts) {
        flagBottomLoader = false;
        list_posts.addAll(posts);
        this.notifyDataSetChanged();
    }

    public void clearPostsList() {
        flagBottomLoader = false;
        list_posts.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list_posts.size() + (flagBottomLoader ? 1 : 0);
    }

    public class ViewHolderPost extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txvTitle;
        TextView txvBody;

        public ViewHolderPost(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txvTitle = (TextView) itemView.findViewById(R.id.postTitle);
            txvBody = (TextView) itemView.findViewById(R.id.postBody);
        }

        @Override
        public void onClick(View v) {
            listener.onClickPostListener(list_posts.get(getAdapterPosition()));
        }
    }

    public static class ViewHolderProgress extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ViewHolderProgress(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < (list_posts.size())) {
            return VIEW_POST;
        } else {
            return VIEW_LOADER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_POST) {
            return new ViewHolderPost(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
        } else {
            return new ViewHolderProgress(LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ViewHolderPost) {
            Post post = list_posts.get(position);
            ViewHolderPost holder = (ViewHolderPost) viewHolder;

            holder.txvTitle.setText(post.getTitle());
            holder.txvBody.setText(post.getBody());
        }
    }
}
