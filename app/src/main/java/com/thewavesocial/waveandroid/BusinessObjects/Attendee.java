package com.thewavesocial.waveandroid.BusinessObjects;

/**
 * Created by kmaho on 5/27/2017.
 */

public class Attendee {
    private String userId;
    private String status;

    public Attendee(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
