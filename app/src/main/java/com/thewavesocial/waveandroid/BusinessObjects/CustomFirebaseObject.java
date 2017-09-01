package com.thewavesocial.waveandroid.BusinessObjects;

import com.google.firebase.database.Exclude;

/**
 * Created by kovlv on 8/31/2017.
 */

public abstract class CustomFirebaseObject {
    @Exclude
    private String id = null;

    @Exclude
    public String getId(){
        return this.id;
    }

    @Exclude
    public void setId(String id){
        this.id = id;
    }
}
