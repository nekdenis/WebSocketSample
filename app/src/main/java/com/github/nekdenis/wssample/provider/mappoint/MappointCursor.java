package com.github.nekdenis.wssample.provider.mappoint;

import java.util.Date;

import android.database.Cursor;

import com.github.nekdenis.wssample.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code mappoint} table.
 */
public class MappointCursor extends AbstractCursor {
    public MappointCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Get the {@code server_id} value.
     */
    public long getServerId() {
        return getLongOrNull(MappointColumns.SERVER_ID);
    }

    /**
     * Get the {@code lat} value.
     */
    public double getLat() {
        return getDoubleOrNull(MappointColumns.LAT);
    }

    /**
     * Get the {@code long} value.
     */
    public double getLong() {
        return getDoubleOrNull(MappointColumns.LONG);
    }

    /**
     * Get the {@code added_date} value.
     * Can be {@code null}.
     */
    public Long getAddedDate() {
        return getLongOrNull(MappointColumns.ADDED_DATE);
    }
}
