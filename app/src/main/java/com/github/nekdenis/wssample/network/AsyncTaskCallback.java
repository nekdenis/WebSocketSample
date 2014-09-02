package com.github.nekdenis.wssample.network;


/**
 * base interface for background callbacks that should be executed in UI
 */
public interface AsyncTaskCallback<T> {
    void onPostExecute(T result);
    void onDetach();
}
