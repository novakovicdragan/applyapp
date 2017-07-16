package com.devdroid.dragan.draganapp.posts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.adapter.PostsAdapter;
import com.devdroid.dragan.draganapp.model.Post;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PostsFragment extends Fragment implements PostsContract.View, PostsAdapter.PostsListener{

    private PostsContract.Presenter mPresenter;

    private RecyclerView postsRecycler;
    private PostsAdapter postsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Interface to notify registered listeners about user clicking on some post item
    public interface PostsListener {
        void onClickedPostListener(int userId, int postId);
    }

    PostsListener listener;

    public PostsFragment() {
        // Requires empty public constructor
    }

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (PostsListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (postsAdapter == null) {
            postsAdapter = new PostsAdapter(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.posts_fragment, container, false);

        postsRecycler = (RecyclerView) v.findViewById(R.id.recyclerPosts);
        postsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        postsRecycler.setAdapter(postsAdapter);

        // Set up progress indicator
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        // set swipeRefreshListener
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadPosts(true); // load posts with page reset
            }
        });

        /*
        Send scroll event to presenter. When user reaches end of list (almost) then
        request new page for infinite scrolling effect
         */
        postsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalPostsCount = ((LinearLayoutManager) recyclerView.getLayoutManager()).getItemCount();
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                mPresenter.handleRecyclerScroll(totalPostsCount, lastVisibleItemPosition);
                Log.wtf("PostsFragment", "Inside addOnScrollListener()");
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.cancelRequests();
    }

    @Override
    public void setPresenter(@NonNull PostsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showPostDetailsUi(int userId, int postId) {
        listener.onClickedPostListener(userId, postId);
    }

    @Override
    public void showPosts(List<Post> posts) {
        postsAdapter.addPostsList(posts);
    }

    @Override
    public void onClickPostListener(Post post) {
        mPresenter.openPostDetails(post);
    }

    @Override
    public void showRecyclerBottomLoader(boolean showLoader) {
        postsAdapter.setBottomLoaderStatus(showLoader);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void clearPostsList() {
        postsAdapter.clearPostsList();
    }

    @Override
    public void showLoadingPostsError(String message) {
        showMessage(message);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }


}
