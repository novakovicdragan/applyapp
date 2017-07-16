package com.devdroid.dragan.draganapp.data.source;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.devdroid.dragan.draganapp.data.source.remote.ErrorCode;
import com.devdroid.dragan.draganapp.data.source.remote.RestComments;
import com.devdroid.dragan.draganapp.data.source.remote.RestPhotos;
import com.devdroid.dragan.draganapp.data.source.remote.RestUser;
import com.devdroid.dragan.draganapp.model.Comment;
import com.devdroid.dragan.draganapp.model.Photo;
import com.devdroid.dragan.draganapp.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Repository class layer which can help to easily use data from another source
public class DetailsRepository implements RestUser.RestUserListener, RestComments.RestCommentsListener, RestPhotos.RestPhotosListener{

    private static DetailsRepository INSTANCE = null;

    private RestUser restUser;
    private RestComments restComments;
    private RestPhotos restPhotos;

    // Declare interface for broadcasting events
    public interface DetailsListener {
        void onUserDataLoaded (User user);
        void onErrorLoadingUser(ErrorCode errorCode);
        void onCommentsLoaded (List<Comment> comments);
        void onErrorLoadingComments(ErrorCode errorCode);
        void onPhotosLoaded (List<Photo> photos);
        void onErrorLoadingPhotos(ErrorCode errorCode);
    }
    private DetailsListener listener;

    // Prevent direct instantiation of this class
    private DetailsRepository(Context context) {
        restUser = new RestUser(context, this);
        restComments = new RestComments(context, this);
        restPhotos = new RestPhotos(context, this);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param context application context
     * @return the {@link PostsRepository} instance
     */
    public static DetailsRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DetailsRepository(context);
        }
        return INSTANCE;
    }

    public void setRepositoryListener(DetailsListener listener) {
        this.listener = listener;
    }

    public void getUserData(int userId) {
        restUser.getUserInfo(userId);
    }

    public void getCommentsForPost(int postId) {
        restComments.getCommentsForPost(postId);
    }

    public void getPhotos() {
        restPhotos.getPhotos();
    }

    public void cancelRequests() {
        restUser.cancelRequest();
        restComments.cancelRequest();
        restPhotos.cancelRequest();
    }

    @Override
    public void onGetUserInfo(JSONObject response) {
        try {
            Log.wtf("GotUser", response.toString());

            User user = new User();
            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));
            user.setPhone(response.getString("phone"));
            user.setWebAddress(response.getString("website"));

            JSONObject obj = response.getJSONObject("address");
            user.setAddress(obj.getString("street") + ", " + obj.getString("city"));

            // use interface listener to broadcast received user data
            listener.onUserDataLoaded(user);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorLoadUser(ErrorCode errorCode) {
        listener.onErrorLoadingUser(errorCode);
    }

    @Override
    public void onGetComments(JSONArray response) {
        List<Comment> comments = new ArrayList<>();
        try {
            Log.wtf("GotComments", response.toString());

            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Comment comment = new Comment();
                comment.setCommentId(obj.getInt("id"));
                comment.setPostId(obj.getInt("postId"));
                comment.setTitle(obj.getString("name"));
                comment.setEmail(obj.getString("email"));
                comment.setBody(obj.getString("body"));

                comments.add(comment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // use interface listener to broadcast received comments data
        listener.onCommentsLoaded(comments);
    }

    @Override
    public void onErrorLoadComments(ErrorCode errorCode) {
        listener.onErrorLoadingComments(errorCode);
    }

    @Override
    public void onGetPhotos(JSONArray response) {
        List<Photo> photos = new ArrayList<>();
        try {
            Log.wtf("GotPhotos", response.toString());

            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                Photo photo = new Photo();
                photo.setPhotoId(obj.getInt("id"));
                photo.setAlbumId(obj.getInt("albumId"));
                photo.setTitle(obj.getString("title"));
                photo.setUrl(obj.getString("url"));
                photo.setThumbnailUrl(obj.getString("thumbnailUrl"));

                photos.add(photo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //// use interface listener to broadcast received photos data
        listener.onPhotosLoaded(photos);
    }

    @Override
    public void onErrorLoadPhotos(ErrorCode errorCode) {
        listener.onErrorLoadingPhotos(errorCode);
    }

}
