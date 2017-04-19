package com.thewavesocial.waveandroid.BusinessObjects;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


//Nothing changed
public final class CurrentUser
{
    public static Activity context;
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
        context = (Activity) cont;
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

    public static Party constructParty(HashMap<String, Object> info) {
        String partyID = (String) info.get("id");
        String name = (String) info.get("name");
        double price = Double.parseDouble((String) info.get("price"));
        String hostName = "";
        Calendar startingDateTime = Calendar.getInstance();
        String startDate =  (String) info.get("start_date");
        startingDateTime.set(Calendar.YEAR, Integer.parseInt(startDate.substring(0, 4)));
        startingDateTime.set(Calendar.MONTH, Integer.parseInt(startDate.substring(5, 7)));
        startingDateTime.set(Calendar.DATE, Integer.parseInt(startDate.substring(8)));
        String startTime = (String) info.get("start_time");
        startingDateTime.set(Calendar.HOUR, Integer.parseInt(startTime.substring(0, 2)));
        startingDateTime.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3)));

        Calendar endingDateTime = Calendar.getInstance();
        String endDate =  (String) info.get("end_date");
        endingDateTime.set(Calendar.YEAR, Integer.parseInt(endDate.substring(0, 4)));
        endingDateTime.set(Calendar.MONTH, Integer.parseInt(endDate.substring(5, 7)));
        endingDateTime.set(Calendar.DATE, Integer.parseInt(endDate.substring(8)));
        String endTime = (String) info.get("end_time");
        startingDateTime.set(Calendar.HOUR, Integer.parseInt(endTime.substring(0, 2)));
        startingDateTime.set(Calendar.MINUTE, Integer.parseInt(endTime.substring(3)));

        MapAddress mapAddress = new MapAddress((String) info.get("address"), null);
        List hostingUsers = (ArrayList) info.get("hosting");
        List bouncingUsers = (ArrayList) info.get("bouncing");
        List attendingUsers = (ArrayList) info.get("attending");
        boolean isPublic = Boolean.getBoolean((String) info.get("is_public"));
        BitmapDrawable partyEmoji = new BitmapDrawable(); // TODO: 04/18/2017 Extract Image

        Party party = new Party(partyID, name, price, hostName, startingDateTime, endingDateTime,
                mapAddress, hostingUsers, bouncingUsers, attendingUsers, isPublic, partyEmoji);
        return party;
    }

    //Fill in all user information
    public static User constructUser(HashMap<String, Object> info) {
        String userID = (String) info.get("id");
        String firstName = (String) info.get("first_name");
        String lastName = (String) info.get("last_name");
        String email = (String) info.get("email");
        String password = (String) info.get("password");
        String college = (String) info.get("college");
        String gender = (String) info.get("gender");
        String phone = (String) info.get("phone");
        MapAddress mapAddress = new MapAddress(); // TODO: 04/17/2017 what to store as address
        Calendar birthday = Calendar.getInstance();
        String strB = (String) info.get("birthday");
        birthday.set(Calendar.YEAR, Integer.parseInt(strB.substring(0, 4)));
        birthday.set(Calendar.MONTH, Integer.parseInt(strB.substring(5, 7)));
        birthday.set(Calendar.DATE, Integer.parseInt(strB.substring(8)));

        List bestFriends = (ArrayList) info.get("best_friends");
        List followers = (ArrayList) info.get("followers");
        List following = (ArrayList) info.get("following");
        List hosting = (ArrayList) info.get("hosting");
        List attended = (ArrayList) info.get("attended");
        List hosted = (ArrayList) info.get("hosted");
        List bounced = (ArrayList) info.get("bounced");
        List attending = (ArrayList) info.get("attending");
        List notifications1 = (ArrayList) info.get("notifications1");
        List notifications2 = (ArrayList) info.get("notifications2");

        BitmapDrawable profilePic = new BitmapDrawable(); // TODO: 04/17/2017 Extract image

        //Compose user
        User user = new User(userID, firstName, lastName, email, password, college, gender, phone, mapAddress, birthday,
                bestFriends, followers, following, hosting, attended, hosted, bounced, attending, notifications1,
                notifications2, profilePic);
        return user;
    }

    //Get user information
    private void server_getUserInfo(String userID) {
        String url = context.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + DatabaseAccess.getTokenFromLocal(context).get("jwt");
        String result = null;
        try {
            result = new DatabaseAccess.HttpRequestTask(context, url, "GET", null).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("CurUser_GetUserInfo", result);
    }
}
