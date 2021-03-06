package com.github.nekdenis.wssample.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.nekdenis.wssample.service.SocketService;

import com.github.nekdenis.wssample.util.SLog;
import com.github.nekdenis.wssample.util.Settings;

/**
 * Network state change receiver that manage Socket service lifecycle
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkReceiver.class.getSimpleName();
@Override
public void onReceive(Context context, Intent intent) {
    ConnectivityManager conn =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = conn.getActiveNetworkInfo();
        //do not touch service if user are not logged in
        if(Settings.isLoginSuccessfull(context)) {
            //start service on internet connected
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                SLog.d(TAG, "connected");
                context.startService(SocketService.startIntent(context.getApplicationContext()));
            }
            //log other states
            else if (networkInfo != null) {
                NetworkInfo.DetailedState state = networkInfo.getDetailedState();
                SLog.d(TAG, state.name());
            }
            //stop service if no internet
            else {
                SLog.d(TAG, "lost connection");
                context.startService(SocketService.closeIntent(context));
            }
        }
	}
}