package com.thewavesocial.waveandroid.BusinessObjects;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


//Nothing changed
public final class CurrentUser
{
    public static Context context;
    public static DummyUser dummy;
    public static User theUser;

    private CurrentUser()
    {
        context = null;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setContext(Context cont)
    {
        context = cont;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setTheUser( User theUser )
    {
        CurrentUser.theUser = theUser;
    }

    public static List<User> getUsersListObjects(List<String> userIdList)
    {
        List<User> friendObjs = new ArrayList<User>();
        for(String id : userIdList){
            friendObjs.add(getUserObject(id));
        }
        return friendObjs;
    }

    public static User getUserObject(String id)
    {
        if ( id.equals("1") )
            return dummy.getFriend1();
        else if ( id.equals("2") )
            return dummy.getFriend2();
        else if ( id.equals("3") )
            return dummy.getFriend3();
        else if ( id.equals("4") )
            return dummy.getFriend4();
        else if ( id.equals("5") )
            return dummy.getFriend5();
        else
            return dummy.getFriend1();
    }

    public static List<Party> getPartyListObjects(List<String> partyIdList)
    {
        List<Party> partyObjs = new ArrayList<Party>();
        for(String id: partyIdList){
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }

    public static Party getPartyObject(String id)
    {
        if ( id.equals("1") )
            return dummy.getParty1();
        else if ( id.equals("2") )
            return dummy.getParty2();
        else if ( id.equals("3") )
            return dummy.getParty3();
        else if ( id.equals("4") )
            return dummy.getParty4();
        else if ( id.equals("5") )
            return dummy.getParty5();
        else
            return dummy.getParty1();
    }
}
