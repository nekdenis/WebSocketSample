package com.github.nekdenis.wssample.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nekdenis.wssample.R;
import com.github.nekdenis.wssample.fragment.LoginFragment;
import com.github.nekdenis.wssample.provider.mappoint.MappointColumns;
import com.github.nekdenis.wssample.provider.mappoint.MappointCursor;
import com.github.nekdenis.wssample.service.SocketService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import util.Consts;
import util.Settings;

/**
 * Activity with map
 */
public class MainActivity extends FragmentActivity implements LoginFragment.LoginManager {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int MAPPOINTS_LOADER_ID = 0;
    //map from MapFragment may be not initialized if user have not installed GooglePlayServices
    private GoogleMap map;
    //service for controlling web socket
    private SocketService socketService;
    //service connection/disconnection listener
    private SocketServiceConnection socketServiceConnection;
    //listener that receives messages from service
    private SocketServiceMessageListener serviceMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        socketServiceConnection = new SocketServiceConnection();
        serviceMessageListener = new SocketServiceMessageListener();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                //invalidate to hide button after logout
                supportInvalidateOptionsMenu();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onPause() {
        if (socketService != null) {
            //disable interaction with UI from service
            socketService.detachListener();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (socketService != null) {
            //enable interaction with UI from service
            socketService.attachListener(serviceMessageListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(socketServiceConnection);
    }

    private void initView() {
        //if user not logged in authorization form will be opened
        if (Settings.isLoginSuccessfull(this)) {
            startSocketService();
        } else {
            showLoginFragment();
        }
    }

    private void logout() {
        Settings.putUserLogin(MainActivity.this, "");
        Settings.putUserPassword(MainActivity.this, "");
        showLoginFragment();
        if (socketService != null) {
            //initialize closing process in service
            socketService.startService(SocketService.closeIntent(this));
        }
    }

    private void showLoginFragment() {
        Fragment loginFragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_login_container, loginFragment, LoginFragment.TAG).commit();
    }

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map_fragment)).getMap();
            if (map != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        map.setMyLocationEnabled(true);
        //load data after map successfully initialized
        getSupportLoaderManager().initLoader(MAPPOINTS_LOADER_ID, null, new MapPointLoaderCallback());
    }

    @Override
    public void onLoginSuccessful() {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        //update button in ActionBar
        supportInvalidateOptionsMenu();
        Fragment loginFragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        //close login fragment
        getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();
        startSocketService();
    }

    private void startSocketService() {
        bindService(SocketService.startIntent(getApplicationContext()), socketServiceConnection, BIND_IMPORTANT);
        getApplicationContext().startService(SocketService.startIntent(getApplicationContext()));
    }

    /**
     * Service connection/disconnection listener
     */
    private class SocketServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketService = ((SocketService.Binder) service).getService();
            socketService.onStartCommand(null, 0, 0);
            socketService.attachListener(new SocketServiceMessageListener());
        }
    }

    /**
     * Listener that receives messages from Service if activity is visible
     */
    private class SocketServiceMessageListener implements SocketService.ServiceMessageListener {

        @Override
        public void onMapPointsResponse() {
            //Loader automatically update content so we do not need this callback. I've keep it just for sample
        }
    }

    /**
     * Loader callback that creates CursorLoader for MappointCursor
     */
    private class MapPointLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(MainActivity.this, MappointColumns.CONTENT_URI, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (map != null) {
                //clear map and add all markers from database
                map.clear();
                MappointCursor mappointCursor = new MappointCursor(data);
                for (mappointCursor.moveToFirst(); !mappointCursor.isAfterLast(); mappointCursor.moveToNext()) {
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(mappointCursor.getLat(), mappointCursor.getLon()))
                            .title(String.valueOf(mappointCursor.getServerId())));
                    if (mappointCursor.isLast()) {
                        //show last marker in center of the map
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mappointCursor.getLat(), mappointCursor.getLon()),
                                Consts.DEFAULT_MAP_ZOOM));
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
