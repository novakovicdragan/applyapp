package com.devdroid.dragan.draganapp.data.source.remote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devdroid.dragan.draganapp.ApplicationConfig;

import org.json.JSONArray;

import java.util.HashMap;

public class RestPosts implements Response.Listener<JSONArray>, Response.ErrorListener{

    private static final String TAG = "POSTS";

    private Context context;

    public interface RestPostsListener {
        void onGetPosts(JSONArray response);
        void onErrorLoadPosts(ErrorCode errorCode);
    }
    private RestPostsListener listener;

    public RestPosts(Context context, RestPostsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getPosts(int page) {
        NetworkUtils utils = new NetworkUtils(context);
        if (utils.isNetworkAvailable()) {
            String url = RestConsts.URL + "posts";

            //Helper to
            HashMap<String, String> params = new HashMap<>();
            params.put("_page", String.valueOf(page));
            url += utils.builderGETParams(params);

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

            ApplicationConfig.getInstance().cancelPendingRequests(TAG);
            ApplicationConfig.getInstance().addToRequestQueue(request, TAG);
        } else {
            listener.onErrorLoadPosts(ErrorCode.NO_NETWORK);
        }
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        listener.onGetPosts(jsonArray);
    }

    public void cancelRequest() {
        ApplicationConfig.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        listener.onErrorLoadPosts(ErrorCode.SERVER_ERROR);
    }

}

