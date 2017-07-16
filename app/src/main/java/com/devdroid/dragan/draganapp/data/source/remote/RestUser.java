package com.devdroid.dragan.draganapp.data.source.remote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.devdroid.dragan.draganapp.ApplicationConfig;

import org.json.JSONObject;

public class RestUser implements Response.Listener<JSONObject>, Response.ErrorListener {

    private static final String TAG = "USER";

    private Context context;

    public interface RestUserListener  {
        void onGetUserInfo(JSONObject response);
        void onErrorLoadUser(ErrorCode errorCode);
    }
    private RestUserListener listener;

    public RestUser(Context context, RestUserListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getUserInfo(int userId) {
        NetworkUtils utils = new NetworkUtils(context);
        if (utils.isNetworkAvailable()) {
            String url = RestConsts.URL + "users/" + userId;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

            ApplicationConfig.getInstance().cancelPendingRequests(TAG);
            ApplicationConfig.getInstance().addToRequestQueue(request, TAG);
        } else {
            listener.onErrorLoadUser(ErrorCode.NO_NETWORK);
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        listener.onGetUserInfo(jsonObject);
    }

    public void cancelRequest() {
        ApplicationConfig.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        listener.onErrorLoadUser(ErrorCode.SERVER_ERROR);
    }

}

