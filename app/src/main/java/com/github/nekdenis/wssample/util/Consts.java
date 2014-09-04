package com.github.nekdenis.wssample.util;

import java.util.concurrent.TimeUnit;

public class Consts {

    public static final String SERVER_URL = "ws://mini-mdt.wheely.com?username=%1$s&password=%2$s";

    public static final long LOCATION_UPDATE_TIME = TimeUnit.MINUTES.toMillis(1);
    public static final long LOCATION_ACCURANCY = 300;

    public static final long DEFAULT_MAP_ZOOM = 10;

    public static final int MAPPOINTS_UPDATED_NOTIFICATION_ID = 11;
}
