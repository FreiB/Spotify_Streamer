package com.nir.spotifystreamer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class Utility {

    //Based on a code snippet by the reviewer
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
