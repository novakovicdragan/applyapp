package com.devdroid.dragan.draganapp.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.devdroid.dragan.draganapp.R;
import com.devdroid.dragan.draganapp.data.source.DetailsRepository;
import com.devdroid.dragan.draganapp.util.ActivityUtils;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "POST_ID";
    public static final String EXTRA_USER_ID = "USER_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        // check if application is rebuilding
        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            int postId = bundle.getInt(EXTRA_POST_ID, 0);
            int userId = bundle.getInt(EXTRA_USER_ID, 0);

            if (detailsFragment == null) {
                // Create the fragment
                detailsFragment = DetailsFragment.newInstance(userId, postId);
                ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), detailsFragment, R.id.contentFrame);
            }

        }

        // Create the presenter for details view
        new DetailsPresenter(DetailsRepository.getInstance(getApplicationContext()), detailsFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
