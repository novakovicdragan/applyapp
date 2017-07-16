package com.devdroid.dragan.draganapp.data.source.remote;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

class NetworkUtils {

    private Context context;

    NetworkUtils(Context context) {
        this.context = context;
    }

    boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Log.d("Exception NetworkInfo", "Handled NetworkInfo exception");
            return false;
        }
    }

    String builderGETParams(Map<String, String> params) {
        String url = "?";
        boolean isFirst = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                if (!isFirst) {
                    url += "&" + key + "=" + URLEncoder.encode(value, "UTF-8");
                } else {
                    url += key + "=" + URLEncoder.encode(value, "UTF-8");
                    isFirst = false;
                }
            } catch (UnsupportedEncodingException e) {
                Log.wtf("URL EncodeError", e.toString());
            }
        }

        return url;
    }
}
