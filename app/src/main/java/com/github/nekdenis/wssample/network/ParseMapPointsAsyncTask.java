package com.github.nekdenis.wssample.network;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.github.nekdenis.wssample.provider.mappoint.MappointContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.SLog;

public class ParseMapPointsAsyncTask extends AsyncTask<Void, Void, List<ContentValues>> {

    private static final String TAG = ParseMapPointsAsyncTask.class.getSimpleName();
    private String response;
    private AsyncTaskCallback<List<ContentValues>> callback;

    public ParseMapPointsAsyncTask(String response) {
        this.response = response;
    }

    public void attachCallback(AsyncTaskCallback<List<ContentValues>> callback) {
        this.callback = callback;
    }

    public void detachCallback() {
        this.callback = null;
    }

    @Override
    protected List<ContentValues> doInBackground(Void... params) {
        List<ContentValues> result = new ArrayList<ContentValues>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; jsonArray.length() > i; i++) {
                JSONObject item = jsonArray.optJSONObject(i);
                MappointContentValues parsedPoint = new MappointContentValues()
                        .putServerId(item.optInt("id"))
                        .putAddedDate(System.currentTimeMillis())
                        .putLat(item.optDouble("lat"))
                        .putLon(item.optDouble("lon"));
                result.add(parsedPoint.values());
            }
        } catch (JSONException e) {
            SLog.e(TAG, e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(List<ContentValues> mapPoints) {
        if (callback != null) {
            callback.onPostExecute(mapPoints);
        }
    }
}