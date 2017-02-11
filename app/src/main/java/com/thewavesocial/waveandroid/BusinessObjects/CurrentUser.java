package com.thewavesocial.waveandroid.BusinessObjects;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;


//Nothing changed
public final class CurrentUser
{
    public static Context context;  //= null;
    public static DummyUser dummy; // = new DummyUser(context);
    public static User theUser; //= dummy; //replace with getCurrentUser from databaseAccess class

    private CurrentUser(){
        context = null;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setContext(Context cont){
        context = cont;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    //string n = CurrentUser.theUser.getFirstName();

    /*public static User getTheUser()
    {
        return theUser;
    }
    public static void setTheUser(User theUser)
    {
        CurrentUser.theUser = theUser;
    }*/

    public static List<User> getFriendsListObjects(List<Long> userIdList){
        List<User> friendObjs = new ArrayList<User>();
        for(long id : userIdList){
            friendObjs.add(getFriendObject(id));
        }
        return friendObjs;
    }

    public static User getFriendObject(long id)
    {
        switch ((int) id)
        {
            case 1:
                return dummy.getFriend1();
            case 2:
                return dummy.getFriend2();
            case 3:
                return dummy.getFriend3();
            case 4:
                return dummy.getFriend4();
            case 5:
                return dummy.getFriend5();
            default:
                return dummy.getFriend1();
        }
    }

    public static List<Party> getPartyListObjects(List<Long> partyIdList){
        List<Party> partyObjs = new ArrayList<Party>();
        for(long id: partyIdList){
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }
    public static Party getPartyObject(long id)
    {
        switch ((int) id)
        {
            case 1:
                return dummy.getParty1();
            case 2:
                return dummy.getParty2();
            case 3:
                return dummy.getParty3();
            case 4:
                return dummy.getParty4();
            case 5:
                return dummy.getParty5();
            default:
                return dummy.getParty1();
        }
    }
}
