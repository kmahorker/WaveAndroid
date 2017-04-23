package com.thewavesocial.waveandroid.BusinessObjects;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;


//Nothing changed
public final class CurrentUser {
    public static Activity context;
    public static DummyUser dummy;
    public static User theUser;

    private CurrentUser() {
        context = null;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setContext(Context cont) {
        context = (Activity) cont;
        dummy = new DummyUser(context);
        theUser = dummy;
    }

    public static void setTheUser(User theUser) {
        CurrentUser.theUser = theUser;
    }

    //Get list of user information
    public static List<User> getUsersListObjects(List<String> userIdList) {
        List<User> friendObjs = new ArrayList<>();
        for (String id : userIdList) {
            friendObjs.add(getUserObject(id));
        }
        return friendObjs;
    }

    //Get list of party information
    public static List<Party> getPartyListObjects(List<String> partyIdList) {
        List<Party> partyObjs = new ArrayList<>();
        for (String id : partyIdList) {
            partyObjs.add(getPartyObject(id));
        }
        return partyObjs;
    }

    //Get user information
    public static User getUserObject(String id) {
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

    //Get party information
    public static Party getPartyObject(String id) {
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

    //Get user information
    public static User server_getUserObject(String userID) {
        String url = context.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + DatabaseAccess.getTokenFromLocal(context).get("jwt");
        String result = null;
        try {
            result = new DatabaseAccess.HttpRequestTask(context, url, "GET", null).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> body = new HashMap<>();
        try {
            JSONObject main_json = new JSONObject(result);
            JSONObject data = main_json.getJSONObject("data");
            Iterator iterKey = data.keys();
            while (iterKey.hasNext()) {
                String key = (String) iterKey.next();
                body.put(key, data.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("CurUser_GetUserInfo", result);
        return constructUser(body);
    }

    //Get party information
    public static Party server_getPartyObject(String partyID) {
        String url = context.getString(R.string.server_url) + "events/" + partyID
                + "?access_token=" + DatabaseAccess.getTokenFromLocal(context).get("jwt");
        String result = null;
        try {
            result = new DatabaseAccess.HttpRequestTask(context, url, "GET", null).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        HashMap<String, Object> body = new HashMap<>();
        try {
            JSONObject main_json = new JSONObject(result);
            JSONObject data = main_json.getJSONObject("data");
            Iterator iterKey = data.keys();
            while (iterKey.hasNext()) {
                String key = (String) iterKey.next();
                body.put(key, data.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("CurUser_GetPartyInfo", result);
        return constructParty(body);
    }

    //Fill in all party information
    public static Party constructParty(HashMap<String, Object> info) {
        String partyID = null, name = null, startDate = null, startTime = null, endDate = null, endTime = null, address = null, str_isPublic = null, hostName = null;
        List hostingUsers = null, bouncingUsers = null, attendingUsers = null;
        Calendar startingDateTime = null, endingDateTime = null;
        MapAddress mapAddress = null;
        double price = 0;
        boolean isPublic = false;

        try {
            partyID = info.get("id") + "";
            name = info.get("name") + "";
            price = Double.parseDouble(info.get("price") + "");

            startingDateTime = Calendar.getInstance();
            startDate = info.get("start_date") + "";
            startTime = info.get("start_time") + "";
            startingDateTime.set(Calendar.YEAR, Integer.parseInt(startDate.substring(0, 4)));
            startingDateTime.set(Calendar.MONTH, Integer.parseInt(startDate.substring(5, 7)));
            startingDateTime.set(Calendar.DATE, Integer.parseInt(startDate.substring(8, 10)));
            startingDateTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0, 2)));
            startingDateTime.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3, 5)));

            endingDateTime = Calendar.getInstance();
            endDate = info.get("end_date") + "";
            endTime = info.get("end_time") + "";
            endingDateTime.set(Calendar.YEAR, Integer.parseInt(endDate.substring(0, 4)));
            endingDateTime.set(Calendar.MONTH, Integer.parseInt(endDate.substring(5, 7)));
            endingDateTime.set(Calendar.DATE, Integer.parseInt(endDate.substring(8, 10)));
            endingDateTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.substring(0, 2)));
            endingDateTime.set(Calendar.MINUTE, Integer.parseInt(endTime.substring(3, 5)));

            address = info.get("address") + ", " + info.get("city") + ", " + info.get("state");
            mapAddress = new MapAddress(address, null);
            hostingUsers = (ArrayList) info.get("hosting");
            bouncingUsers = (ArrayList) info.get("bouncing");
            attendingUsers = (ArrayList) info.get("attending");
            str_isPublic = (String) info.get("is_public");

            isPublic = false;
            if (Integer.parseInt(str_isPublic) == 1)
                isPublic = true;
        } catch (Exception e) {e.printStackTrace();}

        hostName = "";
        BitmapDrawable partyEmoji = new BitmapDrawable(); // TODO: 04/18/2017 Extract Image

        //Compose Party
        Party party = new Party(partyID, name, price, hostName, startingDateTime, endingDateTime,
                mapAddress, hostingUsers, bouncingUsers, attendingUsers, isPublic, partyEmoji);
        return party;
    }

    //Fill in all user information
    public static User constructUser(HashMap<String, Object> info) {
        String userID = null, firstName = null, lastName = null, email = null, college = null, gender = null, date = null;
        List bestFriends = null, followers = null, following = null, hosting = null, hosted = null,
                attending = null, attended = null, bounced = null;
        List notifications1 = new ArrayList(), notifications2 = new ArrayList();
        Calendar birthday = null;
        try {
            userID = info.get("id") + "";
            firstName = info.get("first_name") + "";
            lastName = info.get("last_name") + "";
            email = info.get("email") + "";
            college = info.get("college") + "";
            gender = info.get("gender") + "";

            date = info.get("birthday") + "";
            birthday = Calendar.getInstance();
            birthday.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
            birthday.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)));
            birthday.set(Calendar.DATE, Integer.parseInt(date.substring(8, 10)));

            bestFriends = (ArrayList) info.get("best_friends");
            followers = (ArrayList) info.get("followers");
            following = (ArrayList) info.get("following");
            hosting = (ArrayList) info.get("hosting");
            hosted = (ArrayList) info.get("hosted");
            attending = (ArrayList) info.get("attending");
            attended = (ArrayList) info.get("attended");
            bounced = (ArrayList) info.get("bounced");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List bestFriends = new ArrayList(), followers = new ArrayList(), following = new ArrayList(),
//                hosting = new ArrayList(), hosted = new ArrayList(), attending = new ArrayList(),
//                attended = new ArrayList(), bounced = new ArrayList();
//        List notifications1 = (ArrayList) info.get("notifications1");
//        List notifications2 = (ArrayList) info.get("notifications2");
//        String phone = (String) info.get("phone"); // TODO: 04/22/2017 Not provided
//        String password = (String) info.get("password"); // TODO: 04/22/2017 Not provided

        String phone = "", password = "";
        BitmapDrawable profilePic = new BitmapDrawable(); // TODO: 04/17/2017 Extract image
        MapAddress mapAddress = new MapAddress(); // TODO: 04/17/2017 what to store as address

        //Compose user
        User user = new User(userID, firstName, lastName, email, password, college, gender, phone, mapAddress, birthday,
                bestFriends, followers, following, hosting, attended, hosted, bounced, attending, notifications1,
                notifications2, profilePic);
        return user;
    }
}
