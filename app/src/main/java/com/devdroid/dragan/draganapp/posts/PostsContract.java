package com.devdroid.dragan.draganapp.posts;

import android.support.annotation.NonNull;

import com.devdroid.dragan.draganapp.BasePresenter;
import com.devdroid.dragan.draganapp.BaseView;
import com.devdroid.dragan.draganapp.model.Post;

import java.lang.ref.WeakReference;
import java.util.List;

// This is contract between view and presenter
public interface PostsContract {

    interface View extends BaseView<Presenter> {

        void showPosts(List<Post> posts);

        void showLoadingPostsError(String message);

        void showPostDetailsUi(int userId, int postId);

        void setLoadingIndicator(boolean active);

        void showRecyclerBottomLoader(boolean showLoader);

        void clearPostsList();

    }

    interface Presenter extends BasePresenter {

        void loadPosts(boolean pageReset);

        void openPostDetails(@NonNull Post requestedPost);

        void handleRecyclerScroll(int totalPostsCount, int lastVisibleItemPosition);

        void cancelRequests();
    }




}
