package com.devdroid.dragan.draganapp.data.source;

import android.content.Context;
import android.util.Log;

import com.devdroid.dragan.draganapp.data.source.remote.ErrorCode;
import com.devdroid.dragan.draganapp.data.source.remote.RestPosts;
import com.devdroid.dragan.draganapp.model.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Repository class layer which can help to easily use data from another source
public class PostsRepository implements RestPosts.RestPostsListener {

    private static PostsRepository INSTANCE = null;

    private RestPosts restPosts;

    private int postsPageCounter;

    // Declare interface for broadcasting events
    public interface PostsListener {
        void onPostsLoaded (List<Post> posts, int pageCounter);
        void onLoadError (ErrorCode errorCode);
    }
    private PostsListener listener;

    // Prevent direct instantiation of this class
    private PostsRepository(Context context) {
        restPosts = new RestPosts(context, this);
        postsPageCounter = 1;
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param context application context
     * @return the {@link PostsRepository} instance
     */
    public static PostsRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PostsRepository(context);
        }
        return INSTANCE;
    }

    public void setRepositoryListener(PostsListener listener) {
        this.listener = listener;
    }

    public void getPosts(boolean pagerReset) {
        if (pagerReset) {
            postsPageCounter = 1;
        }
        restPosts.getPosts(postsPageCounter);
    }

    public void cancelRequests() {
        restPosts.cancelRequest();
    }

    @Override
    public void onGetPosts(JSONArray response) {
        postsPageCounter++;
        List<Post> posts = new ArrayList<>();
        try {
            Log.wtf("onGetPosts", response.toString());

            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Post post = new Post();
                post.setPostId(obj.getInt("id"));
                post.setUserId(obj.getInt("userId"));
                post.setTitle(obj.getString("title"));
                post.setBody(obj.getString("body"));

                posts.add(post);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //broadcast received posts data, and current page we are on
        listener.onPostsLoaded(posts, postsPageCounter - 1);
    }

    @Override
    public void onErrorLoadPosts(ErrorCode errorCode) {
        listener.onLoadError(errorCode);
    }




}
