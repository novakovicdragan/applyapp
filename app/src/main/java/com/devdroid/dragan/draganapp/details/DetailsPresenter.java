package com.devdroid.dragan.draganapp.details;

import android.support.annotation.NonNull;
import android.util.Log;

import com.devdroid.dragan.draganapp.data.source.DetailsRepository;
import com.devdroid.dragan.draganapp.data.source.remote.ErrorCode;
import com.devdroid.dragan.draganapp.model.Comment;
import com.devdroid.dragan.draganapp.model.Photo;
import com.devdroid.dragan.draganapp.model.User;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsPresenter implements DetailsContract.Presenter, DetailsRepository.DetailsListener {

    private final DetailsRepository mDetailsRepository;

    private final DetailsContract.View mDetailsView;

    private final int REQUEST_USER = 1;
    private final int REQUEST_COMMENTS = 2;
    private final int REQUEST_PHOTOS = 3;

    private int userId;
    private int postId;

    public DetailsPresenter(@NonNull DetailsRepository detailsRepository, @NonNull DetailsContract.View detailsView) {
        // CheckNotNull for fast failing, easier to track back bug
        mDetailsRepository = checkNotNull(detailsRepository, "detailsRepository cannot be null");
        mDetailsView = checkNotNull(detailsView, "detailsView cannot be null!");

        // notify view to set its present to this
        mDetailsView.setPresenter(this);

        // Subscribe to repository interface
        mDetailsRepository.setRepositoryListener(this);
    }

    @Override
    public void start(int userId, int postId) {
        // Load first data when view is loaded
        if (userId > 0  && postId > 0) {
            this.userId = userId;
            this.postId = postId;
            loadUserDetails(userId);
            loadCommentsForPost(postId);
            loadPhotos();
        }
    }

    @Override
    public void loadUserDetails(int userId) {
        mDetailsRepository.getUserData(userId);
        mDetailsView.setLoadingUserIndicator(true);
    }

    @Override
    public void loadCommentsForPost(int postId) {
        mDetailsRepository.getCommentsForPost(postId);
        mDetailsView.setLoadingCommentsIndicator(true);
    }

    @Override
    public void loadPhotos() {
        mDetailsRepository.getPhotos();
        mDetailsView.setLoadingPhotosIndicator(true);
    }

    @Override
    public void onUserDataLoaded(User user) {
        user.setisSet(true);
        mDetailsView.showUserData(user);
    }

    @Override
    public void onErrorLoadingUser(ErrorCode errorCode) {
        processLoadingError(errorCode, REQUEST_USER);
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {
        mDetailsView.showComments(comments);
    }

    @Override
    public void onErrorLoadingComments(ErrorCode errorCode) {
        processLoadingError(errorCode, REQUEST_COMMENTS);
    }

    @Override
    public void onPhotosLoaded(List<Photo> photos) {
        mDetailsView.showPhotos(photos);
    }

    @Override
    public void onErrorLoadingPhotos(ErrorCode errorCode) {
        processLoadingError(errorCode, REQUEST_PHOTOS);
    }

    private void processLoadingError(ErrorCode errorCode, int requestType) {
        // Check error type from Enum ErrorCode and show SnackBar message accordingly
        String message;
        boolean tryAgain = false;
        switch (errorCode) {
            case NO_NETWORK:
                message = "Check network";
                break;
            case TIMEOUT:
                message = "Slow connection";
                tryAgain = true;
                break;
            case SERVER_ERROR:
                message = "Server error, try again later";
                break;
            default:
                message = "Error connecting";
                break;
        }
        //Notify view to show error message
        mDetailsView.showLoadingError(message, tryAgain, requestType);
    }

    @Override
    public void onStopEvent() {
        // view is entering onStop Lifecycle, cancel all pending requests
        mDetailsRepository.cancelRequests();
        mDetailsView.setLoadingUserIndicator(false);
        mDetailsView.setLoadingCommentsIndicator(false);
        mDetailsView.setLoadingPhotosIndicator(false);
    }

    @Override
    public void retryLoadRequest(int requestType) {
        //User has clicked on Retry button on SnackBar
        switch (requestType) {
            case REQUEST_USER:
                loadUserDetails(userId);
                break;
            case REQUEST_COMMENTS:
                loadCommentsForPost(postId);
                break;
            case REQUEST_PHOTOS:
                loadPhotos();
        }
    }
}
