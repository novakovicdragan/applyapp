package com.devdroid.dragan.draganapp.posts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.data.source.DetailsRepository;
import com.devdroid.dragan.draganapp.data.source.PostsRepository;
import com.devdroid.dragan.draganapp.details.DetailsActivity;
import com.devdroid.dragan.draganapp.details.DetailsCommunicator;
import com.devdroid.dragan.draganapp.details.DetailsFragment;
import com.devdroid.dragan.draganapp.details.DetailsPresenter;
import com.devdroid.dragan.draganapp.util.ActivityUtils;

public class PostsActivity extends AppCompatActivity implements PostsFragment.PostsListener {

    public DetailsCommunicator mDetailsCommunicator;

    private View detailsFragmentContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posts_activity);

        Log.wtf("PostsActivity", "inside onCreate");

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PostsFragment postsFragment = (PostsFragment) getSupportFragmentManager().findFragmentById(R.id.containerPosts);
        if (savedInstanceState == null) {
            // Activity is not being recreated so we need to initialize posts fragment
            if (postsFragment == null) {
                // Create the fragment
                postsFragment = PostsFragment.newInstance();
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), postsFragment, R.id.containerPosts);
            }


        }

        //Create new presenter for postsFragment view
        new PostsPresenter(PostsRepository.getInstance(getApplicationContext()), postsFragment);


        // Check to see if containerDetails exists. If true means we are in landscape mode
        detailsFragmentContainer = findViewById(R.id.containerDetails);
        if (detailsFragmentContainer != null) {

            DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.containerDetails);
            if (detailsFragment == null) {
                detailsFragment = DetailsFragment.newInstance();
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), detailsFragment, R.id.containerDetails);

            // Create presenter for details view
            new DetailsPresenter(DetailsRepository.getInstance(getApplicationContext()), detailsFragment);
        } else {
            // containerDetails doesn't exists, check if there is loaded fragment pending and remove it
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.containerDetails);
            if (fragment != null) {
                ActivityUtils.removeFragmentFromActivity(getSupportFragmentManager(), fragment);
            }
        }
    }

    @Override
    public void onClickedPostListener(int userId, int postId) {
        // check if exists detailsFragmentContainer
        // True means there are two fragments on screen and need to notify detailsFragment through mDetailscommunicator
        if (detailsFragmentContainer != null) {
            mDetailsCommunicator.onClickPostListener(userId, postId);
        } else {
            // Send clicked post data to DetailsActivity
            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
            intent.putExtra(DetailsActivity.EXTRA_USER_ID, userId);
            intent.putExtra(DetailsActivity.EXTRA_POST_ID, postId);
            startActivity(intent);
        }
    }
}
