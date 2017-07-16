package com.devdroid.dragan.draganapp.posts;

import android.support.annotation.NonNull;

import com.devdroid.dragan.draganapp.data.source.PostsRepository;
import com.devdroid.dragan.draganapp.data.source.remote.ErrorCode;
import com.devdroid.dragan.draganapp.model.Post;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostsPresenter implements PostsContract.Presenter, PostsRepository.PostsListener {

    private final PostsRepository mPostsRepository;

    private WeakReference<PostsContract.View> mPostsView;

    private boolean mFirstLoad = true;
    private final int VISIBLE_TRASHOLD = 2;

    public PostsPresenter(@NonNull PostsRepository postsRepository, @NonNull PostsContract.View postsView) {
        mPostsRepository = checkNotNull(postsRepository, "postsRepository cannot be null");
        mPostsView = checkNotNull(new WeakReference(postsView), "postsView cannot be null!");

        mPostsView.get().setPresenter(this);

        mPostsRepository.setRepositoryListener(this);
    }

    @Override
    public void start() {
        loadPosts(false);
    }

    @Override
    public void loadPosts(boolean pagerReset) {
        // On first load we use mFirstLoad (default true) to make sure to reset pager counter
        loadPosts(pagerReset || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     *
     * @param pagerReset Will reset pager counter to first page
     * @param showLoadingUI True will display loader in UI
     */
    private void loadPosts(boolean pagerReset, boolean showLoadingUI) {
        if (showLoadingUI) {
            mPostsView.get().setLoadingIndicator(true);
        }
        mPostsRepository.getPosts(pagerReset);
    }

    @Override
    public void handleRecyclerScroll(int totalPostsCount, int lastVisiblePosition) {

        if (totalPostsCount <= (lastVisiblePosition + VISIBLE_TRASHOLD)) {
            // At this point we are close to end of loaded posts
            // Request next page to load for infinite scrolling effect
            loadPosts(false, false);
            mPostsView.get().showRecyclerBottomLoader(true);
        }
    }

    @Override
    public void cancelRequests() {
        mPostsRepository.cancelRequests();
    }

    @Override
    public void openPostDetails(@NonNull Post requestedPost) {
        checkNotNull(requestedPost, "requestedPost cannot be null!");
        mPostsView.get().showPostDetailsUi(requestedPost.getUserId(), requestedPost.getPostId());
    }

    @Override
    public void onPostsLoaded(List<Post> posts, int page) {
        mPostsView.get().setLoadingIndicator(false);
        if (page == 1) { // If user requested refresh posts, clear all posts and load new ones
            mPostsView.get().clearPostsList();
        }
        mPostsView.get().showRecyclerBottomLoader(false);
        mPostsView.get().showPosts(posts);
    }

    @Override
    public void onLoadError(ErrorCode errorCode) {
        //Notify view there was error on loading
        mPostsView.get().setLoadingIndicator(false);
        mPostsView.get().showRecyclerBottomLoader(false);
        String message = "";
        switch (errorCode) {
            case NO_NETWORK:
                message = "No internet connection";
                break;
            default:
                message = "neka greska";
                break;
        }
        mPostsView.get().showLoadingPostsError(message);
    }
}
