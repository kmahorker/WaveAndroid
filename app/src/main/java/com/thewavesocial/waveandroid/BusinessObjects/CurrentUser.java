package com.thewavesocial.waveandroid.BusinessObjects;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


//Nothing changed
public final class CurrentUser {
    public static Context context;
    public static DummyUser dummy;
    public static User theUser;

    private CurrentUser() {
        context = null;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setContext(Context cont) {
        context = cont;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setTheUser(User theUser) {
        CurrentUser.theUser = theUser;
    }

    public static List<User> getUsersListObjects(List<Long> userIdList) {
        List<User> friendObjs = new ArrayList<User>();
        for (long id : userIdList) {
            friendObjs.add(getUserObject(id));
        }
        return friendObjs;
    }

    public static User getUserObject(long id) {
        switch ((int) id) {
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
                return dummy;
        }
    }

    public static List<Party> getPartyListObjects(List<Long> partyIdList) {
        List<Party> partyObjs = new ArrayList<Party>();
        for (long id : partyIdList) {
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }

    public static Party getPartyObject(long id) {
        switch ((int) id) {
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

    public static List<Long> getUserIDList(List<User> users ) {
        List<Long> list = new ArrayList<>();
        for ( User user : users )
            list.add(user.getUserID());
        return list;
    }

}
