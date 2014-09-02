package com.github.nekdenis.wssample.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;

import com.github.nekdenis.wssample.R;
import com.github.nekdenis.wssample.activity.MainActivity;
import com.github.nekdenis.wssample.network.AsyncTaskCallback;
import com.github.nekdenis.wssample.network.ParseMapPointsAsyncTask;
import com.github.nekdenis.wssample.provider.mappoint.MappointColumns;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import util.Consts;
import util.SLog;
import util.Settings;


public class PushService extends Service {

    private static final String TAG = PushService.class.getSimpleName();

    public static final String ACTION_PING = "edu.ku.eecs780.ACTION_PING";
    public static final String ACTION_CONNECT = "edu.ku.eecs780.ACTION_CONNECT";
    public static final String ACTION_SHUT_DOWN = "edu.edu.eecs780.ACTION_SHUT_DOWN";

    private WebSocketConnection connection;
    private final IBinder mBinder = new Binder();
    private boolean shutDown = false;
    private ServiceMessageListener mListener;
    private Handler mHandler;
    private WakeLock connectionWakeLock;
    private boolean isConnecting;
    private ParseMapPointsAsyncTask parseTask;

    public static Intent startIntent(Context context) {
        Intent i = new Intent(context, PushService.class);
        i.setAction(ACTION_CONNECT);
        return i;
    }

    public static Intent pingIntent(Context context) {
        Intent i = new Intent(context, PushService.class);
        i.setAction(ACTION_PING);
        return i;
    }

    public static Intent closeIntent(Context context) {
        Intent i = new Intent(context, PushService.class);
        i.setAction(ACTION_SHUT_DOWN);
        return i;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        SLog.d(TAG, "Creating Service " + this.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SLog.d(TAG, "Destroying Service " + this.toString());
        if (connection != null && connection.isConnected()) connection.disconnect();
        if (parseTask != null) {
            parseTask.detachCallback();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SocketServiceLock");
        wakelock.acquire();
        SLog.d(TAG, "onStartCommand");
        if (intent != null) {
            SLog.d(TAG, intent.toUri(0));
        }
        shutDown = false;
        if (connection == null || (!connection.isConnected() && !isConnecting)) {
            connectionWakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SocketService_clientLock");
            connection = new WebSocketConnection();
            try {
                isConnecting = true;
                connection.connect(String.format(Consts.SERVER_URL, Settings.getUserLogin(this), Settings.getUserPassword(this)), new WebSocketHandlerImpl());
                //wake lock will be released in connection listener;
            } catch (WebSocketException e) {
                SLog.e(TAG, e.getMessage());
                if (connectionWakeLock != null && connectionWakeLock.isHeld()) {
                    connectionWakeLock.release();
                }
            }
        } else if (intent != null) {
            if (ACTION_SHUT_DOWN.equals(intent.getAction())) {
                shutDown = true;
                if (connection.isConnected()) connection.disconnect();
            }
        }
        wakelock.release();
        return START_STICKY;
    }

    public class Binder extends android.os.Binder {

        public PushService getService() {
            return PushService.this;
        }
    }

    public synchronized void attachListener(ServiceMessageListener listener) {
        mListener = listener;
    }

    public synchronized void detachListener() {
        mListener = null;
    }

    public synchronized void sendLocation(double lat, double lon) {
        if (connection != null && connection.isConnected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("lon", lon);
                json.put("lat", lat);
            } catch (JSONException e) {
                SLog.e(TAG, e.getMessage());
            }
            String message = json.toString();
            SLog.d(TAG, "trying to send: " + message);

            connection.sendTextMessage(message);
        }
    }

    public synchronized boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private void handleMessage(String message, WakeLock wakelock) {
        parseTask = new ParseMapPointsAsyncTask(message);
        parseTask.attachCallback(new ParseMapPointsCallback(wakelock));
        parseTask.execute();
    }

    private class WebSocketHandlerImpl extends WebSocketHandler {
        @Override
        public void onOpen() {
            isConnecting = false;
            SLog.d(TAG, "Connected to websocket");
            if (connectionWakeLock != null && connectionWakeLock.isHeld()) {
                connectionWakeLock.release();
            }
            sendLocation(55.373703d, 37.474764d);
        }

        @Override
        public void onClose(int code, String reason) {
            isConnecting = false;
            SLog.d(TAG, String.format("Disconnected! Code: %d Reason: %s", code, reason));
            if (!shutDown) {
                startService(startIntent(PushService.this));
            } else {
                stopSelf();
            }
            if (connectionWakeLock != null && connectionWakeLock.isHeld()) {
                connectionWakeLock.release();
            }
        }

        @Override
        public void onTextMessage(String response) {
            WakeLock wakelock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SocketServiceLock");
            wakelock.acquire();
            SLog.d(TAG, "recieved: " + response);
            handleMessage(response, wakelock);

        }
    }

    private class ParseMapPointsCallback implements AsyncTaskCallback<List<ContentValues>> {

        private WakeLock wakeLock;

        public ParseMapPointsCallback(WakeLock wakelock) {
            this.wakeLock = wakelock;
        }

        @Override
        public void onPostExecute(final List<ContentValues> result) {
            parseTask = null;
            getContentResolver().bulkInsert(MappointColumns.CONTENT_URI, result.toArray(new ContentValues[result.size()]));
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mListener != null) {
                        mListener.onMapPointsResponse();
                    } else {
                        sendNotification(result);
                    }
                }
            });
            wakeLock.release();
        }

        @Override
        public void onDetach() {
            wakeLock.release();
        }
    }

    private void sendNotification(List<ContentValues> response) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.location_update_notification, response.size()))
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setOngoing(false)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Consts.MAPPOINTS_UPDATED_NOTIFICATION_ID, builder.build());
    }

    public interface ServiceMessageListener {
        public void onMapPointsResponse();
    }
}