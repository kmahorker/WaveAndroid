package com.thewavesocial.waveandroid.DatabaseObjects;

import java.util.HashMap;

/**
 * Created by Wei-Tung on 04/25/2017.
 */

public class RequestComponents {
    public String url;
    public String endpoint;
    public HashMap<String, String> body;

    public RequestComponents(String url, String endpoint, HashMap<String, String> body) {
        this.url = url;
        this.endpoint = endpoint;
        this.body = body;
    }
}
