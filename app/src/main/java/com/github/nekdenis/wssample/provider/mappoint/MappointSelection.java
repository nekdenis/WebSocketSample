package com.github.nekdenis.wssample.provider.mappoint;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.github.nekdenis.wssample.provider.base.AbstractSelection;

/**
 * Selection for the {@code mappoint} table.
 */
public class MappointSelection extends AbstractSelection<MappointSelection> {
    @Override
    public Uri uri() {
        return MappointColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @param sortOrder How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort
     *            order, which may be unordered.
     * @return A {@code MappointCursor} object, which is positioned before the first entry, or null.
     */
    public MappointCursor query(ContentResolver contentResolver, String[] projection, String sortOrder) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), sortOrder);
        if (cursor == null) return null;
        return new MappointCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null}.
     */
    public MappointCursor query(ContentResolver contentResolver, String[] projection) {
        return query(contentResolver, projection, null);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, projection, null, null}.
     */
    public MappointCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null, null);
    }


    public MappointSelection id(long... value) {
        addEquals(MappointColumns._ID, toObjectArray(value));
        return this;
    }


    public MappointSelection serverId(long... value) {
        addEquals(MappointColumns.SERVER_ID, toObjectArray(value));
        return this;
    }

    public MappointSelection serverIdNot(long... value) {
        addNotEquals(MappointColumns.SERVER_ID, toObjectArray(value));
        return this;
    }

    public MappointSelection serverIdGt(long value) {
        addGreaterThan(MappointColumns.SERVER_ID, value);
        return this;
    }

    public MappointSelection serverIdGtEq(long value) {
        addGreaterThanOrEquals(MappointColumns.SERVER_ID, value);
        return this;
    }

    public MappointSelection serverIdLt(long value) {
        addLessThan(MappointColumns.SERVER_ID, value);
        return this;
    }

    public MappointSelection serverIdLtEq(long value) {
        addLessThanOrEquals(MappointColumns.SERVER_ID, value);
        return this;
    }

    public MappointSelection lat(double... value) {
        addEquals(MappointColumns.LAT, toObjectArray(value));
        return this;
    }

    public MappointSelection latNot(double... value) {
        addNotEquals(MappointColumns.LAT, toObjectArray(value));
        return this;
    }

    public MappointSelection latGt(double value) {
        addGreaterThan(MappointColumns.LAT, value);
        return this;
    }

    public MappointSelection latGtEq(double value) {
        addGreaterThanOrEquals(MappointColumns.LAT, value);
        return this;
    }

    public MappointSelection latLt(double value) {
        addLessThan(MappointColumns.LAT, value);
        return this;
    }

    public MappointSelection latLtEq(double value) {
        addLessThanOrEquals(MappointColumns.LAT, value);
        return this;
    }

    public MappointSelection long(double... value) {
        addEquals(MappointColumns.LONG, toObjectArray(value));
        return this;
    }

    public MappointSelection longNot(double... value) {
        addNotEquals(MappointColumns.LONG, toObjectArray(value));
        return this;
    }

    public MappointSelection longGt(double value) {
        addGreaterThan(MappointColumns.LONG, value);
        return this;
    }

    public MappointSelection longGtEq(double value) {
        addGreaterThanOrEquals(MappointColumns.LONG, value);
        return this;
    }

    public MappointSelection longLt(double value) {
        addLessThan(MappointColumns.LONG, value);
        return this;
    }

    public MappointSelection longLtEq(double value) {
        addLessThanOrEquals(MappointColumns.LONG, value);
        return this;
    }

    public MappointSelection addedDate(Long... value) {
        addEquals(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointSelection addedDateNot(Long... value) {
        addNotEquals(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointSelection addedDateGt(long value) {
        addGreaterThan(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointSelection addedDateGtEq(long value) {
        addGreaterThanOrEquals(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointSelection addedDateLt(long value) {
        addLessThan(MappointColumns.ADDED_DATE, value);
        return this;
    }

    public MappointSelection addedDateLtEq(long value) {
        addLessThanOrEquals(MappointColumns.ADDED_DATE, value);
        return this;
    }
}
