package com.devdroid.dragan.draganapp.data.source.remote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.devdroid.dragan.draganapp.ApplicationConfig;

import org.json.JSONArray;

import java.util.HashMap;

public class RestComments implements Response.Listener<JSONArray>, Response.ErrorListener{

    private static final String TAG = "COMMENTS";

    private Context context;

    public interface RestCommentsListener {
        void onGetComments(JSONArray response);
        void onErrorLoadComments(ErrorCode errorCode);
    }
    private RestCommentsListener listener;

    public RestComments(Context context, RestCommentsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getCommentsForPost(int postId) {
        NetworkUtils utils = new NetworkUtils(context);
        if (utils.isNetworkAvailable()) {
            String url = RestConsts.URL + "comments";

            //Helper to build GET parameters link
            HashMap<String, String> params = new HashMap<>();
            params.put("postId", String.valueOf(postId));
            url += utils.builderGETParams(params);

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

            ApplicationConfig.getInstance().cancelPendingRequests(TAG);
            ApplicationConfig.getInstance().addToRequestQueue(request, TAG);
        } else {
            listener.onErrorLoadComments(ErrorCode.NO_NETWORK);
        }
    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        listener.onGetComments(jsonArray);
    }

    public void cancelRequest() {
        ApplicationConfig.getInstance().cancelPendingRequests(TAG);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        listener.onErrorLoadComments(ErrorCode.SERVER_ERROR);
    }

}
