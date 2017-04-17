package com.thewavesocial.waveandroid.BusinessObjects;

/**
 * Created by kmaho on 3/27/2017.
 */

public class BestFriend {
    String name;
    String phoneNumber;

    public BestFriend(){
        name = "";
        phoneNumber = "0";
    }

    public BestFriend(String name, String num){
        this.name = name;
        phoneNumber = num;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
