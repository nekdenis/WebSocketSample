package com.github.nekdenis.wssample.provider.mappoint;

import java.util.Date;

import android.content.ContentResolver;
import android.net.Uri;

import com.github.nekdenis.wssample.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code mappoint} table.
 */
public class MappointContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return MappointColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, MappointSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public MappointContentValues putServerId(long value) {
        mContentValues.put(MappointColumns.SERVER_ID, value);
        return this;
    }



    public MappointContentValues putLat(double value) {
        mContentValues.put(MappointColumns.LAT, value);
        return this;
    }



    public MappointContentValues putLon(double value) {
        mContentValues.put(MappointColumns.LON, value);
        return this;
    }



    public MappointContentValues putAddedDate(Long value) {
        mContentValues.put(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointContentValues putAddedDateNull() {
        mContentValues.putNull(MappointColumns.ADDED_DATE);
        return this;
    }

}
