package com.devdroid.dragan.draganapp.details;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.adapter.DetailsAdapter;
import com.devdroid.dragan.draganapp.model.Comment;
import com.devdroid.dragan.draganapp.model.Photo;
import com.devdroid.dragan.draganapp.model.User;
import com.devdroid.dragan.draganapp.posts.PostsActivity;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsFragment extends Fragment implements DetailsContract.View, DetailsCommunicator {

    private DetailsContract.Presenter mPresenter;

    private RecyclerView detailsRecycler;
    private DetailsAdapter detailsAdapter;

    private int userId = 0;
    private int postId = 0;

    public DetailsFragment() {
        // Requires empty public constructor
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    public static DetailsFragment newInstance(int userId, int postId) {
        Bundle arguments = new Bundle();
        arguments.putInt(DetailsActivity.EXTRA_USER_ID, userId);
        arguments.putInt(DetailsActivity.EXTRA_POST_ID, postId);
        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // check if fragment is loaded in PostsActivity, then we will need to implement communicator
        // so DetailsFragment can get information when post is clicked
        if (getActivity() instanceof PostsActivity) {
            ((PostsActivity) context).mDetailsCommunicator = this;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (detailsAdapter == null) {
            detailsAdapter = new DetailsAdapter(getContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.details_fragment, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userId = bundle.getInt(DetailsActivity.EXTRA_USER_ID, 0);
            postId = bundle.getInt(DetailsActivity.EXTRA_POST_ID, 0);
        }

        detailsRecycler = (RecyclerView) v.findViewById(R.id.recyclerDetails);

        // Check if fragment is loaded inside PostsActivity.
        // True means there are two fragments sharing screen and we easily set number of spans from integers.xml value
        final int gridSpan;
        if (getActivity() instanceof PostsActivity) {
            gridSpan = getResources().getInteger(R.integer.grid_photos_spans_mix);
        } else {
            gridSpan = getResources().getInteger(R.integer.grid_photos_spans_single);
        }
        GridLayoutManager gridLM = new GridLayoutManager(getContext(), gridSpan);
        gridLM.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (detailsAdapter.getItemViewType(position) == detailsAdapter.VIEW_PHOTO) {
                    return 1;
                } else {
                    return gridSpan;
                }
            }
        });
        detailsRecycler.setLayoutManager(gridLM);
        detailsRecycler.setAdapter(detailsAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start(userId, postId);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Notify presenter that view is entering onStop lifecycle
        mPresenter.onStopEvent();
    }

    @Override
    public void setPresenter(@NonNull DetailsContract.Presenter presenter) {
        // checkNotNull for fast failing, easier to track bug
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showUserData(User user) {
        // notify view there is new data for user
        detailsAdapter.setUser(user);
    }

    @Override
    public void showComments(List<Comment> comments) {
        detailsAdapter.addCommentsList(comments);
    }

    @Override
    public void showPhotos(List<Photo> photos) {
        detailsAdapter.addPhotosList(photos);
    }

    @Override
    public void showLoadingError(String message, boolean tryAgain, final int requestType) {
        // there was error with loading data. Show snacbar message with Retry button
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.retryLoadRequest(requestType);
                    }
                })
                .show();
    }

    @Override
    public void onClickPostListener(int userId, int postId) {
        // Listener function, user has clicked on post, notify presenter about new post details
        mPresenter.start(userId, postId);
    }

    @Override
    public void setLoadingUserIndicator(boolean active) {
        // Trigger from presenter, need to set loading indicator
        detailsAdapter.setLoadingUserIndicator(active);
    }

    @Override
    public void setLoadingCommentsIndicator(boolean active) {
        // Trigger from presenter, need to set loading indicator
        detailsAdapter.setLoadingCommentsIndicator(active);
    }

    @Override
    public void setLoadingPhotosIndicator(boolean actvie) {
        // Trigger from presenter, need to set loading indicator
        detailsAdapter.setLoadingPhotosIndicator(actvie);
    }
}
