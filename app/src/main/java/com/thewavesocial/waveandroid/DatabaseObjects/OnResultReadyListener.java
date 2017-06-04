package com.thewavesocial.waveandroid.DatabaseObjects;

//Call back interface for http requests
public interface OnResultReadyListener<T> {
    void onResultReady(T result);
}
