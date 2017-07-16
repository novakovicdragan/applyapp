package com.devdroid.dragan.draganapp.details;

import com.devdroid.dragan.draganapp.BaseView;
import com.devdroid.dragan.draganapp.model.Comment;
import com.devdroid.dragan.draganapp.model.Photo;
import com.devdroid.dragan.draganapp.model.User;

import java.util.List;

public interface DetailsContract {

    // view will implement this interface, so presenter can trigger events
    interface View extends BaseView<Presenter> {

        void showUserData(User user);

        void showComments(List<Comment> comments);

        void showPhotos(List<Photo> photos);

        void showLoadingError(String message, boolean tryAgain, int requestType);

        void setLoadingUserIndicator(boolean active);

        void setLoadingCommentsIndicator(boolean active);

        void setLoadingPhotosIndicator(boolean actvie);

    }

    //interface so view can trigger events to presenter
    interface Presenter {

        void start(int userId, int postId);

        void loadUserDetails(int userId);

        void loadCommentsForPost(int postId);

        void loadPhotos();

        void retryLoadRequest(int requestType);

        void onStopEvent();
    }

}
