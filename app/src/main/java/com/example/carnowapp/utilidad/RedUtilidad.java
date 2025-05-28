package com.example.carnowapp.utilidad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class RedUtilidad {
    public static boolean hayConexionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
