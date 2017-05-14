package com.thewavesocial.waveandroid.BusinessObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.DatabaseObjects.RequestComponents;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

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

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.getTokenFromLocal;


//Nothing changed
public final class CurrentUser {
    public static Activity context;
    public static User theUser = new User();

    private CurrentUser() {
        context = null;
        getUserObject("10", new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                theUser = result;
            }
        });
    }

    //Initialize user object
    public static void setContext(Activity cont, final OnResultReadyListener<Boolean> delegate) {
        context = (Activity) cont;
        getUserObject(getTokenFromLocal(cont).get("id"), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                theUser = result;
                if ( result != null )
                    delegate.onResultReady(true);
                else
                    delegate.onResultReady(false);
            }
        });
    }

    //Set main user object
    public static void setTheUser(User theUser) {
        CurrentUser.theUser = theUser;
    }

    //Get list of user information from server
    public static void getUsersListObjects(List<String> userIdList, final OnResultReadyListener<List<User>> delegate) {
        RequestComponents[] comps = new RequestComponents[userIdList.size()];
        for ( int i = 0; i < comps.length; i++ ) {
            String url = context.getString(R.string.server_url) + "users/" + userIdList.get(i)
                    + "?access_token=" + getTokenFromLocal(context).get("jwt");
            comps[i] = new RequestComponents(url, "GET", null);
        }
        new DatabaseAccess.HttpRequestTask(context, comps, new OnResultReadyListener<ArrayList<String>>(){
            @Override
            public void onResultReady(ArrayList<String> result) {
                List<User> friends = new ArrayList<>();
                for ( int i = 0; i < result.size(); i++ ) {
                    HashMap<String, String> body = new HashMap<>();
                    try {
                        JSONObject main_json = new JSONObject(result.get(i));
                        JSONObject data = main_json.getJSONObject("data");
                        Iterator iterKey = data.keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getString(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("CurUser_GetUserInfo", result.get(0));
                    User user = constructUser(body);
                    //testing purpose
                    ArrayList<String> followers = new ArrayList<>();
                    followers.add("11");
                    followers.add("12");
                    followers.add("13");
                    followers.add("14");
                    followers.add("15");
                    user.setFollowers(followers);
                    user.setFollowing(followers);
                    friends.add(user);
                }
                delegate.onResultReady(friends);
            }
        }).execute();
    }

    //Get list of party information from server
    public static void getPartyListObjects(List<String> partyIdList, final OnResultReadyListener<List<Party>> delegate) {
        RequestComponents[] comps = new RequestComponents[partyIdList.size()];
        for ( int i = 0; i < comps.length; i++ ) {
            String url = context.getString(R.string.server_url) + "users/" + partyIdList.get(i)
                    + "?access_token=" + getTokenFromLocal(context).get("jwt");
            comps[i] = new RequestComponents(url, "GET", null);
        }
        new DatabaseAccess.HttpRequestTask(context, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<Party> parties = new ArrayList<>();
                for ( int i = 0; i < result.size(); i++ ) {
                    HashMap<String, String> body = new HashMap<>();
                    try {
                        JSONObject main_json = new JSONObject(result.get(0));
                        JSONObject data = main_json.getJSONObject("data");
                        Iterator iterKey = data.keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getString(key));
                        }
                    } catch (JSONException e) {e.printStackTrace();}

                    Log.d("CurUser_GetPartyInfo", result.get(0));
                    parties.add(constructParty(body));
                }
                delegate.onResultReady(parties);
            }}).execute();
    }

    //Get user information
    public static void getUserObject(String userID, final OnResultReadyListener<User> delegate) {
        String url = context.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + getTokenFromLocal(context).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(context, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>(){
            @Override
            public void onResultReady(ArrayList<String> result) {
                HashMap<String, String> body = new HashMap<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONObject data = main_json.getJSONObject("data");
                    Iterator iterKey = data.keys();
                    while (iterKey.hasNext()) {
                        String key = (String) iterKey.next();
                        body.put(key, data.getString(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CurUser_GetUserInfo", result.get(0));
                User user = constructUser(body);

                //testing purpose
                ArrayList<String> followers = new ArrayList<>();
                followers.add("11");
                followers.add("12");
                followers.add("13");
                followers.add("14");
                followers.add("15");
                user.setFollowers(followers);
                user.setFollowing(followers);

                delegate.onResultReady(user);
            }
        }).execute();
    }

    //Get party information from server
    public static void getPartyObject(String partyID, final OnResultReadyListener<Party> delegate) {
        String url = context.getString(R.string.server_url) + "events/" + partyID
                + "?access_token=" + getTokenFromLocal(context).get("jwt");

        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(context, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                HashMap<String, String> body = new HashMap<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONObject data = main_json.getJSONObject("data");
                    Iterator iterKey = data.keys();
                    while (iterKey.hasNext()) {
                        String key = (String) iterKey.next();
                        body.put(key, data.getString(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CurUser_GetPartyInfo", result.get(0));
                delegate.onResultReady(constructParty(body));
            }}).execute();
    }

    //Get user following from server
    public static void getUserFollowing(String userID, final OnResultReadyListener<List<User>> delegate) {
        String url = context.getString(R.string.server_url) + "users/" + userID
                + "/followings?access_token=" + getTokenFromLocal(context).get("jwt");

        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(context, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<User> followings = new ArrayList<>();
                try {
                    JSONArray list = new JSONArray(result.get(0));
                    for ( int i = 0; i < list.length(); i++ ) {

                        HashMap<String, String> body = new HashMap<>();
                        JSONObject main_json = list.getJSONObject(i);
                        JSONObject data = main_json.getJSONObject("data");
                        Iterator iterKey = data.keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getString(key));
                        }
                        followings.add(constructUser(body));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                delegate.onResultReady(followings);
            }
        }).execute();
    }

    //Fill in all party information locally
    private static Party constructParty(HashMap<String, String> info) {
        String partyID = "", name = "", emoji = "", startDateTime = "", duration = "", address = "", str_isPublic = "", hostName = "",
        min_age = "", max_age = "";
        List hostingUsers = new ArrayList(), bouncingUsers = new ArrayList(), attendingUsers = new ArrayList();
        Calendar startingDateTimeCalendar = Calendar.getInstance(), endingDateTimeCalendar = Calendar.getInstance();
        MapAddress mapAddress = new MapAddress();
        double price = 0;
        boolean isPublic = false;
        int minAge = 0, maxAge = 0;

        try {
            partyID = info.get("id");
            name = info.get("name");
            emoji = info.get("emoji");
            if ( info.get("price") != null )
                price = Double.parseDouble(info.get("price") + "");

            startDateTime = info.get("start_time_stamp");
            duration = info.get("duration");
            //startTime = info.get("start_time") + "";
            if ( !startDateTime.equals("null") ) {
                startingDateTimeCalendar = UtilityClass.unixToCalendar(Long.parseLong(startDateTime));
                if(!duration.equals("null")){
                    endingDateTimeCalendar = UtilityClass.unixToCalendar(Long.parseLong(startDateTime) + Long.parseLong(duration));
                }
//                startingDateTimeCalendar.set(Calendar.YEAR, Integer.parseInt(startDateTime.substring(0, 4)));
//                startingDateTimeCalendar.set(Calendar.MONTH, Integer.parseInt(startDateTime.substring(5, 7)));
//                startingDateTimeCalendar.set(Calendar.DATE, Integer.parseInt(startDateTime.substring(8, 10)));
            }
//            if ( !startTime.equals("null") ) {
//                startingDateTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime.substring(0, 2)));
//                startingDateTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(3, 5)));
//            }

//            endDate = info.get("end_date") + "";
//            endTime = info.get("end_time") + "";


//            if ( !endDate.equals("null") ) {
//                endingDateTimeCalendar.set(Calendar.YEAR, Integer.parseInt(endDate.substring(0, 4)));
//                endingDateTimeCalendar.set(Calendar.MONTH, Integer.parseInt(endDate.substring(5, 7)));
//                endingDateTimeCalendar.set(Calendar.DATE, Integer.parseInt(endDate.substring(8, 10)));
//            }
//            if ( !endTime.equals("null") ) {
//                endingDateTimeCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime.substring(0, 2)));
//                endingDateTimeCalendar.set(Calendar.MINUTE, Integer.parseInt(endTime.substring(3, 5)));
//            }

            address = info.get("address");
            mapAddress.setAddress_string(address);
            if ( !info.get("lat").equals("null") && !info.get("lng").equals("null") ) {
                mapAddress.setAddress_latlng(new LatLng(Integer.parseInt(info.get("lat")), Integer.parseInt(info.get("lng"))));
            }

            str_isPublic = info.get("is_public") + "";
            if ( !str_isPublic.equals("null") && Integer.parseInt(str_isPublic) == 1 ) {
                isPublic = true;
            }

            hostName = info.get("host_name");
            min_age = info.get("min_age");
            max_age = info.get("max_age");
            if(!min_age.equals("null") && !max_age.equals("null")){
                minAge = Integer.parseInt(min_age);
                maxAge = Integer.parseInt(max_age);
            }
        } catch (Exception e) {e.printStackTrace();}


//        hostingUsers = (ArrayList) info.get("hosting");
//        bouncingUsers = (ArrayList) info.get("bouncing");
//        attendingUsers = (ArrayList) info.get("attending");
//        hostName = "";
//        String partyEmoji = "";
//        int minAge = 17, maxAge = 40;


        //Compose Party
        Party party = new Party(partyID, name, price, hostName, startingDateTimeCalendar, endingDateTimeCalendar,
                mapAddress, hostingUsers, bouncingUsers, attendingUsers, isPublic, emoji, minAge, maxAge);
        return party;
    }

    //Fill in all user information locally
    private static User constructUser(HashMap<String, String> info) {
        String userID = "", firstName = "", lastName = "", email = "", college = "", gender = "", date = "", profilePic = "";
        List bestFriends = new ArrayList(), followers = new ArrayList(), following = new ArrayList(),
                hosting = new ArrayList(), hosted = new ArrayList(), attending = new ArrayList(),
                attended = new ArrayList(), bounced = new ArrayList();
        List notifications1 = new ArrayList(), notifications2 = new ArrayList();
        Calendar birthday = Calendar.getInstance();
        try {
            userID = info.get("id");
            firstName = info.get("first_name");
            lastName = info.get("last_name");
            email = info.get("email");
            college = info.get("college");
            gender = info.get("gender");
            profilePic = info.get("image_path");

            date = info.get("birthday");
            if ( !date.equals("null") ) {
                birthday.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
                birthday.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)));
                birthday.set(Calendar.DATE, Integer.parseInt(date.substring(8, 10)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        bestFriends = (ArrayList) info.get("best_friends");
//        followers = (ArrayList) info.get("followers");
//        following = (ArrayList) info.get("following");
//        hosting = (ArrayList) info.get("hosting");
//        hosted = (ArrayList) info.get("hosted");
//        attending = (ArrayList) info.get("attending");
//        attended = (ArrayList) info.get("attended");
//        bounced = (ArrayList) info.get("bounced");

//        List bestFriends = new ArrayList(), followers = new ArrayList(), following = new ArrayList(),
//                hosting = new ArrayList(), hosted = new ArrayList(), attending = new ArrayList(),
//                attended = new ArrayList(), bounced = new ArrayList();
//        List notifications1 = (ArrayList) info.get("notifications1");
//        List notifications2 = (ArrayList) info.get("notifications2");
//        String phone = (String) info.get("phone"); // TODO: 04/22/2017 Not provided
//        String password = (String) info.get("password"); // TODO: 04/22/2017 Not provided

        String phone = "", password = "";
        MapAddress mapAddress = new MapAddress(); // TODO: 04/17/2017 what to store as address

        //Compose user
        User user = new User(userID, firstName, lastName, email, password, college, gender, phone, mapAddress, birthday,
                bestFriends, followers, following, hosting, attended, hosted, bounced, attending, notifications1,
                notifications2, "https://cdn.pixabay.com/photo/2017/02/17/20/05/donald-2075124_960_720.png");
        return user;
    }

    //Delete party from server
    public static void deleteParty(String partyID, final OnResultReadyListener<String> delegate) {
        RequestComponents comps[] = new RequestComponents[1];
        String url = context.getString(R.string.server_url) + "events/" + partyID + "?access_token="
                + getTokenFromLocal(context).get("jwt");
        String result = null;
        comps[0] = new RequestComponents(url, "DELETE", null);
        new DatabaseAccess.HttpRequestTask(context, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getJSONObject("status") + "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CurUser_DeleteEvent", result.get(0));
                delegate.onResultReady(status);
            }
        }).execute();
        Log.d("Delete Party", result);
    }

    public static Party createParty( String name,
                                     String emoji,
                                     double price,
                                     String hostName,
                                     String address,
                                     double lat,
                                     double lng,
                                     boolean isPublic,
                                     double startTimeStamp,
                                     double duration,
                                     int minAge,
                                     int maxAge,

                                     final OnResultReadyListener<String> delegate){
        RequestComponents[] comps = new RequestComponents[1];
        final Party[] event_id = {null};
        String url = "https://api.theplugsocial.com/v1/events?access_token=" + getTokenFromLocal(context).get("jwt");
        HashMap<String, String> event_info = new HashMap();
        event_info.put("name", name);
        event_info.put("emoji", emoji);
        event_info.put("price", price + "");
        event_info.put("address", address);
        event_info.put("lat", lat + "");
        event_info.put("lng", lng + "");
        event_info.put("is_public", isPublic ? "1" : "0");
        event_info.put("start_time_stamp", startTimeStamp + "");
        event_info.put("duration", duration + "");
        event_info.put("min_age", minAge + "");
        event_info.put("max_age", maxAge + "");
        event_info.put("host_name", hostName);

        String result = null;

        comps[0] = new RequestComponents(url, "POST", event_info);
        new DatabaseAccess.HttpRequestTask(context, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getJSONObject("status") + "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("CurUser_CreateEvent", result.get(0));
                delegate.onResultReady(status);
            }
        }).execute();
        return event_id[0];
    }


    //
//    //Get user information
//    public static User getUserObject(String id) {
//        if ( id.equals("1") )
//            return dummy.getFriend1();
//        else if ( id.equals("2") )
//            return dummy.getFriend2();
//        else if ( id.equals("3") )
//            return dummy.getFriend3();
//        else if ( id.equals("4") )
//            return dummy.getFriend4();
//        else if ( id.equals("5") )
//            return dummy.getFriend5();
//        else
//            return dummy.getFriend1();
//    }
//
//    //Get party information
//    public static Party getPartyObject(String id) {
//        if ( id.equals("1") )
//            return dummy.getParty1();
//        else if ( id.equals("2") )
//            return dummy.getParty2();
//        else if ( id.equals("3") )
//            return dummy.getParty3();
//        else if ( id.equals("4") )
//            return dummy.getParty4();
//        else if ( id.equals("5") )
//            return dummy.getParty5();
//        else
//            return dummy.getParty1();
//    }
}
