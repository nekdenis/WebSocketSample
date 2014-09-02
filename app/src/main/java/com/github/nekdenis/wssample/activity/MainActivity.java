package com.github.nekdenis.wssample.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.nekdenis.wssample.R;
import com.github.nekdenis.wssample.fragment.LoginFragment;
import com.github.nekdenis.wssample.service.PushService;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import util.Settings;

public class MainActivity extends FragmentActivity implements LoginFragment.LoginManager {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;
    private MenuItem logoutItem;
    private SocketServiceConnection socketServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        socketServiceConnection = new SocketServiceConnection();
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socketService != null) {
            socketService.detachListener();
        }
        unbindService(socketServiceConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        logoutItem = menu.findItem(R.id.action_logout);
        logoutItem.setVisible(Settings.isLoginSuccessfull(this));
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                supportInvalidateOptionsMenu();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void logout() {
        Settings.putUserLogin(MainActivity.this, "");
        Settings.putUserPassword(MainActivity.this, "");
        showLoginFragment();
    }

    @Override
    protected void onPause() {
        if (socketService != null) {
            socketService.detachListener();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (socketService != null) {
            socketService.attachListener(new SocketServiceMessageListener());
        }
    }

    private void initView() {
        if (Settings.isLoginSuccessfull(this)) {
            startSocketService();
        }else {
            showLoginFragment();
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
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map_fragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onLoginSuccessful() {
        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
        supportInvalidateOptionsMenu();
        Fragment loginFragment = getSupportFragmentManager().findFragmentByTag(LoginFragment.TAG);
        getSupportFragmentManager().beginTransaction().remove(loginFragment).commit();
        startSocketService();
    }

    private void startSocketService() {
        bindService(PushService.startIntent(getApplicationContext()), socketServiceConnection, BIND_IMPORTANT);
        getApplicationContext().startService(PushService.startIntent(getApplicationContext()));
    }

    private PushService socketService;

    private class SocketServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketService = ((PushService.Binder) service).getService();
            socketService.onStartCommand(null, 0, 0);
            socketService.attachListener(new SocketServiceMessageListener());
        }
    }

    private class SocketServiceMessageListener implements PushService.ServiceMessageListener {

        @Override
        public void onMapPointsResponse() {
            //TODO:reload loader
        }
    }
}
