package com.thewavesocial.waveandroid.BusinessObjects;

/**
 * Created by kmaho on 3/27/2017.
 */

public class BestFriend {
    String name;
    int phoneNumber;

    public BestFriend(){
        name = "";
        phoneNumber = 0;
    }

    public BestFriend(String name, int num){
        this.name = name;
        phoneNumber = num;
    }

    public BestFriend(String name, String numString){
        this.name = name;
        String str = numString.replaceAll("[^\\d]", "");
        phoneNumber = Integer.parseInt(str);
    }
}
