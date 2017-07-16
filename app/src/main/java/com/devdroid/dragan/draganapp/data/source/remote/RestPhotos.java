package com.devdroid.dragan.draganapp.data.source.remote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devdroid.dragan.draganapp.ApplicationConfig;

import org.json.JSONArray;

public class RestPhotos implements Response.Listener<JSONArray>, Response.ErrorListener{

    private static final String TAG = "PHOTOS";

    private Context context;

    public interface RestPhotosListener {
        void onGetPhotos(JSONArray response);
        void onErrorLoadPhotos(ErrorCode errorCode);
    }
    private RestPhotosListener listener;

    public RestPhotos(Context context, RestPhotosListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getPhotos() {
        NetworkUtils utils = new NetworkUtils(context);
        if (utils.isNetworkAvailable()) {
            String url = RestConsts.URL + "photos?_limit=20";

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

            ApplicationConfig.getInstance().cancelPendingRequests(TAG);
            ApplicationConfig.getInstance().addToRequestQueue(request, TAG);
        } else {
            listener.onErrorLoadPhotos(ErrorCode.NO_NETWORK);
        }
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        listener.onGetPhotos(jsonArray);
    }

    public void cancelRequest() {
        ApplicationConfig.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        listener.onErrorLoadPhotos(ErrorCode.SERVER_ERROR);
    }

}


