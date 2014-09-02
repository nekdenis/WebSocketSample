package com.github.nekdenis.wssample.provider.mappoint;

import java.util.HashSet;
import java.util.Set;

import android.net.Uri;
import android.provider.BaseColumns;

import com.github.nekdenis.wssample.provider.GeneratedProvider;

/**
 * Columns for the {@code mappoint} table.
 */
public class MappointColumns implements BaseColumns {
    public static final String TABLE_NAME = "mappoint";
    public static final Uri CONTENT_URI = Uri.parse(GeneratedProvider.CONTENT_URI_BASE + "/" + TABLE_NAME);

    public static final String _ID = BaseColumns._ID;
    public static final String SERVER_ID = "server_id";
    public static final String LAT = "lat";
    public static final String LONG = "long";
    public static final String ADDED_DATE = "added_date";

    public static final String DEFAULT_ORDER = TABLE_NAME + "." +_ID;

    // @formatter:off
    public static final String[] FULL_PROJECTION = new String[] {
            TABLE_NAME + "." + _ID + " AS " + BaseColumns._ID,
            TABLE_NAME + "." + SERVER_ID,
            TABLE_NAME + "." + LAT,
            TABLE_NAME + "." + LONG,
            TABLE_NAME + "." + ADDED_DATE
    };
    // @formatter:on

    private static final Set<String> ALL_COLUMNS = new HashSet<String>();
    static {
        ALL_COLUMNS.add(_ID);
        ALL_COLUMNS.add(SERVER_ID);
        ALL_COLUMNS.add(LAT);
        ALL_COLUMNS.add(LONG);
        ALL_COLUMNS.add(ADDED_DATE);
    }

    public static boolean hasColumns(String[] projection) {
        if (projection == null) return true;
        for (String c : projection) {
            if (ALL_COLUMNS.contains(c)) return true;
        }
        return false;
    }
}
