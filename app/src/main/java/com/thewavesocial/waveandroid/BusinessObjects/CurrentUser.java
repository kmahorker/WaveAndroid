package com.thewavesocial.waveandroid.BusinessObjects;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.DatabaseObjects.RequestComponents;
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
import java.util.Map;
import java.util.TreeMap;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.getTokenFromLocal;


//Nothing changed
public final class CurrentUser {
    public static Activity mainActivity;
    public static User theUser = new User();

    private CurrentUser() {
        mainActivity = null;
        server_getUserObject("10", new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                theUser = result;
            }
        });
    }

    /**Initialize user object*/
    public static void setContext(Activity activity, final OnResultReadyListener<Boolean> delegate) {
        mainActivity = activity;
        server_getUserObject(getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(User result) {
                theUser = result;
                server_getEventsOfUser(theUser.getUserID(), new OnResultReadyListener<HashMap<String, ArrayList<String>>>() {
                    @Override
                    public void onResultReady(HashMap<String, ArrayList<String>> result) {
                        theUser.getAttending().addAll(result.get("attending"));
                        theUser.getHosting().addAll(result.get("hosting"));
                        theUser.getBouncing().addAll(result.get("bouncing"));
                        theUser.getGoing().addAll(result.get("going"));
                        server_getBestFriends(getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<List<BestFriend>>() {
                            @Override
                            public void onResultReady(List<BestFriend> result) {
                                theUser.setBestFriends( new ArrayList<BestFriend>() );
                                if ( result != null )
                                    theUser.getBestFriends().addAll(result);

                                if ( delegate == null )
                                    return;
                                if ( theUser.getUserID() != null )
                                    delegate.onResultReady(true);
                                else
                                    delegate.onResultReady(false);
                            }
                        });
                    }
                });
            }
        });
    }

    /**Set main user object*/
    public static void setTheUser(User theUser) {
        CurrentUser.theUser = theUser;
    }

//------------------------------------------------------------------------------------POST Requests

    /**Create new party in server*/
    public static void server_createNewParty(String name,
                                             String emoji,
                                             double price,
                                             String address,
                                             double lat,
                                             double lng,
                                             boolean isPublic,
                                             double startTimeStamp,
                                             double endingTimeStamp,
                                             int minAge,
                                             int maxAge,
                                             final OnResultReadyListener<String> delegate){
        RequestComponents[] comps = new RequestComponents[1];
        String url = "https://api.theplugsocial.com/v1/events?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> event_info = new HashMap();
        event_info.put("name", name);
        event_info.put("emoji", emoji);
        event_info.put("price", price + "");
        event_info.put("address", address);
        event_info.put("lat", lat + "");
        event_info.put("lng", lng + "");
        event_info.put("is_public", isPublic ? "1" : "0");
        event_info.put("start_timestamp", startTimeStamp + "");
        event_info.put("end_timestamp", endingTimeStamp + "");
        event_info.put("min_age", minAge + "");
        event_info.put("max_age", maxAge + "");

        comps[0] = new RequestComponents(url, "POST", event_info);
        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    String status =  main_json.getString("status");

                    if(!status.equals("error")) {
                        JSONObject data_json = main_json.getJSONObject("data");
                        String event_id = data_json.getString("insertId");
                        status += "," + event_id;
                        if ( delegate != null )
                            delegate.onResultReady(status);
                    }

                } catch (JSONException e) {e.printStackTrace();}
                Log.d("CreateNewParty", result.get(0));
            }
        }).execute();
    }

    /**Create new user in server*/
    public static void server_createNewUser(String first_name,
                                            String last_name,
                                            String email,
                                            String college,
                                            String password,
                                            String fb_id,
                                            String gender,
                                            String birthday,
                                            final OnResultReadyListener<String> delegate) {
        RequestComponents[] comps = new RequestComponents[1];
        String url = "https://api.theplugsocial.com/v1/users";
        HashMap<String, String> event_info = new HashMap();
        event_info.put("first_name", first_name);
        event_info.put("last_name", last_name);
        event_info.put("email", email);
        event_info.put("college", college);
        event_info.put("password", password);
        event_info.put("fb_id", fb_id);
        event_info.put("gender", gender);
        event_info.put("birthday", birthday);

        comps[0] = new RequestComponents(url, "POST", event_info);
        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    String status =  main_json.getString("status");

                    if(!status.equals("error")) {
                        JSONObject data_json = main_json.getJSONObject("data");
                        String event_id = data_json.getString("insertId");
                        status += "," + event_id;

                        if ( delegate != null )
                            delegate.onResultReady(status);
                    }

                } catch (JSONException e) {e.printStackTrace();}
                Log.d("CreateNewUser", result.get(0));
            }
        }).execute();
    }

    /**Parameter action must be either "POST" or "DELETE"*/
    public static void server_manageUserForParty(String userID, String eventID, String relationship, String action, final OnResultReadyListener<String> delegate){
        RequestComponents[] comps = new RequestComponents[1];
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/users/" + userID + "/?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> about = new HashMap();
        about.put("relationship", relationship);

        if (action.equals("POST"))
            comps[0] = new RequestComponents(url, "POST", about);
        else if (action.equals("DELETE"))
            comps[0] = new RequestComponents(url, "DELETE", about);
        else
            Log.d("Action", "Illegal passed action argument: " + action);

        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("ManageUserForParty", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /**Update user in server*/
    public static void server_updateUser(String userID, HashMap<String, String> body, final OnResultReadyListener<String> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents[] comps = new RequestComponents[1];
        comps[0] = new RequestComponents(url, "POST", body);

        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {e.printStackTrace();}

                Log.d("UpdateUser", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /**Update party in server*/
    public static void server_updateParty(String partyID, HashMap<String, String> body, final OnResultReadyListener<String> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents[] comps = new RequestComponents[1];
        comps[0] = new RequestComponents(url, "POST", body);

        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {e.printStackTrace();}

                Log.d("UpdateParty", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /**Follow a user*/
    public static void server_followUser(String userID, String targetID, final OnResultReadyListener<String> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followings/" + targetID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "POST", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {e.printStackTrace();}

                Log.d("Follow User", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /**Add Best Friend on server*/
    public static void server_addBestFriend(String name, String number, String userId, final OnResultReadyListener<String> delegate){
        String url =  mainActivity.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("contact", number);
        RequestComponents comp = new RequestComponents(url, "POST", body);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                try{
                    JSONObject main_json = new JSONObject(result.get(0));
                    String status =  main_json.getString("status");
                    if ( delegate != null )
                        delegate.onResultReady(status);
                }catch(JSONException e){
                    e.printStackTrace();
                }
                Log.d("addBestFriend", result.get(0) + "");
            }
        }).execute();
    }

    /** Invite user to event. Return either "success" or "error" */
    public static void server_inviteUserToEvent(String userID, String eventID, final OnResultReadyListener<String> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "POST", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("InviteUserToParty", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /** Create new notification. Return either "success" or "error" */
    public static void server_createNotification(String userID, String message, String type, final OnResultReadyListener<String> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/notifications?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();
        body.put("message", message);
        body.put("type", type);
        RequestComponents comp = new RequestComponents(url, "POST", body);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CreateNotification", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(status);
            }
        }).execute();

    }

//-------------------------------------------------------------------------------------GET Requests

    /**Get list of user information from server*/
    public static void server_getUsersListObjects(List<String> userIdList, final OnResultReadyListener<List<User>> delegate) {
        RequestComponents[] comps = new RequestComponents[userIdList.size()];
        for ( int i = 0; i < comps.length; i++ ) {
            String url = mainActivity.getString(R.string.server_url) + "users/" + userIdList.get(i)
                    + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
            comps[i] = new RequestComponents(url, "GET", null);
        }
        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>(){
            @Override
            public void onResultReady(ArrayList<String> result) {
                final List<User> friends = new ArrayList<>();
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

                    Log.d("CurUser_GetUserInfo", result.get(i));
                    User user = constructUser(body);
                    friends.add(user);
                }

                //Light-weight threads management
                class ThreadManager{
                    private int max, completes;
                    public ThreadManager(int max){
                        this.max = max;
                    }
                    void completeThreads(){
                        completes++;
                        if ( completes >= max && delegate != null )
                            delegate.onResultReady(friends);
                    }
                }
                final ThreadManager threadManager = new ThreadManager(friends.size()*2);

                for(final User user : friends){
                    CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                        @Override
                        public void onResultReady(List<String> result) {
                            if (result != null)
                                user.getFollowing().addAll(result);
                            threadManager.completeThreads();
                        }
                    });
                    CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                        @Override
                        public void onResultReady(List<String> result) {
                            if (result != null)
                                user.getFollowers().addAll(result);
                            threadManager.completeThreads();
                        }
                    });
                }
            }
        }).execute();
    }

    /**Get list of party information from server*/
    public static void server_getPartyListObjects(List<String> partyIdList, final OnResultReadyListener<List<Party>> delegate) {
        RequestComponents[] comps = new RequestComponents[partyIdList.size()];
        for ( int i = 0; i < partyIdList.size(); i++ ) {
            String url = mainActivity.getString(R.string.server_url) + "events/" + partyIdList.get(i)
                    + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
            comps[i] = new RequestComponents(url, "GET", null);
        }
        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                Map<Long, Party> raw_parties = new TreeMap<>();
                for ( int i = 0; i < result.size(); i++ ) {
                    long date_key = 0;
                    HashMap<String, String> body = new HashMap<>();
                    try {
                        JSONObject main_json = new JSONObject(result.get(i));
                        JSONObject data = main_json.getJSONObject("data");
                        Iterator iterKey = data.keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getString(key));
                        }
                        date_key = Long.parseLong(data.getString("start_timestamp"));
                    } catch (JSONException e) {e.printStackTrace();}

                    Log.d("CurUser_GetPartyInfo", result.get(i));
                    while (raw_parties.keySet().contains(date_key))
                        date_key += 1;

                    raw_parties.put(date_key, constructParty(body));
                }
                ArrayList<Party> parties = new ArrayList<>();
                for ( Long key : raw_parties.keySet() ) {
                    parties.add(raw_parties.get(key));
                }

                if ( delegate != null )
                    delegate.onResultReady(parties);
            }}).execute();
    }

    /**Get user information*/
    public static void server_getUserObject(final String userID, final OnResultReadyListener<User> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>(){
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
                final User user = constructUser(body);
                server_getUserFollowing(userID, new OnResultReadyListener<List<String>>() {
                    @Override
                    public void onResultReady(List<String> result) {
                        user.getFollowing().addAll(result);
                        server_getUserFollowers(userID, new OnResultReadyListener<List<String>>() {
                            @Override
                            public void onResultReady(List<String> result) {
                                user.getFollowers().addAll(result);
                                if ( delegate != null )
                                    delegate.onResultReady(user);
                            }
                        });
                    }
                });
        }}).execute();
    }

    /**Get party information from server*/
    public static void server_getPartyObject(String partyID, final OnResultReadyListener<Party> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");

        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
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
                if ( delegate != null )
                    delegate.onResultReady(constructParty(body));
            }}).execute();
    }

    /**Get user following from server*/
    public static void server_getUserFollowers(String userID, final OnResultReadyListener<List<String>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followers?access_token=" + getTokenFromLocal(mainActivity).get("jwt");

        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<String> followers = new ArrayList<>();
                try {
                    JSONObject list = new JSONObject(result.get(0));
                    JSONArray main_json = list.getJSONArray("data");
                    for ( int i = 0; i < main_json.length(); i++ ) {
                        JSONObject data = main_json.getJSONObject(i);
                        String userID = data.getString("id");
                        followers.add(userID);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ( delegate != null )
                    delegate.onResultReady(followers);
            }
        }).execute();
    }

    /**Get user following from server*/
    public static void server_getUserFollowing(String userID, final OnResultReadyListener<List<String>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followings?access_token=" + getTokenFromLocal(mainActivity).get("jwt");

        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<String> followings = new ArrayList<>();
                try {
                    JSONObject list = new JSONObject(result.get(0));
                    JSONArray main_json = list.getJSONArray("data");
                    for ( int i = 0; i < main_json.length(); i++ ) {
                        JSONObject data = main_json.getJSONObject(i);
                        String userID = data.getString("id");
                        followings.add(userID);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("User Following", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(followings);
            }
        }).execute();
    }

    /**Get User's events from server*/
    public static void server_getEventsOfUser(String userID, final OnResultReadyListener<HashMap<String,ArrayList<String>>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "/events?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<String> attending = new ArrayList<>();
                ArrayList<String> going = new ArrayList<>();
                ArrayList<String> hosting = new ArrayList<>();
                ArrayList<String> bouncing = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        if ( data.getJSONObject(i).getString("relationship").equals("attending"))
                            attending.add(data.getJSONObject(i).getString("event_id"));
                        else if ( data.getJSONObject(i).getString("relationship").equals("hosting"))
                            hosting.add(data.getJSONObject(i).getString("event_id"));
                        else if ( data.getJSONObject(i).getString("relationship").equals("bouncing"))
                            bouncing.add(data.getJSONObject(i).getString("event_id"));
                        else if ( data.getJSONObject(i).getString("relationship").equals("going"))
                            going.add(data.getJSONObject(i).getString("event_id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HashMap<String, ArrayList<String>> parties = new HashMap();
                parties.put("attending", attending);
                parties.put("hosting", hosting);
                parties.put("bouncing", bouncing);
                parties.put("going", going);
                Log.d("Get User Events", result.get(0));
                if ( delegate != null )
                    delegate.onResultReady(parties);
            }
        }).execute();
    }

    /**Get User's events from server*/
    public static void server_getUsersOfEvent(final String eventID, final OnResultReadyListener<HashMap<String,ArrayList<User>>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/users?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<User> attending = new ArrayList<>();
                ArrayList<User> going = new ArrayList<>();
                ArrayList<User> hosting = new ArrayList<>();
                ArrayList<User> bouncing = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        if ( data.getJSONObject(i).getString("relationship").equals("attending"))
                            attending.add(constructUser(body));
                        else if ( data.getJSONObject(i).getString("relationship").equals("hosting"))
                            hosting.add(constructUser(body));
                        else if ( data.getJSONObject(i).getString("relationship").equals("bouncing"))
                            bouncing.add(constructUser(body));
                        else if ( data.getJSONObject(i).getString("relationship").equals("going"))
                            going.add(constructUser(body));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Parties will have: attending, hosting, bouncing, going, and inviting
                final HashMap<String, ArrayList<User>> users = new HashMap();
                users.put("attending", attending);
                users.put("hosting", hosting);
                users.put("bouncing", bouncing);
                users.put("going", going);
                users.put("inviting", new ArrayList<User>());
                server_getInvitesOfEvent(eventID, new OnResultReadyListener<ArrayList<User>>() {
                    @Override
                    public void onResultReady(ArrayList<User> result) {
                        if ( result != null )
                            users.get("inviting").addAll(result);

                        //Light-weight threads management
                        class ThreadManager{
                            private int max, completes;
                            public ThreadManager(int max){
                                this.max = max;
                            }
                            void completeThreads(){
                                completes++;
                                Log.d("Threads", "Completed: " + completes + ", Max" + max);
                                if ( completes >= max && delegate != null )
                                    delegate.onResultReady(users);
                            }
                        }
                        final ThreadManager threadManager = new ThreadManager((users.get("attending").size()+
                                                                    users.get("hosting").size()+
                                                                    users.get("bouncing").size()+
                                                                    users.get("going").size()+
                                                                    users.get("inviting").size())*2);

                        //Get following/followers
                        for(final User user : users.get("attending")){
                            CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowing().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                            CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowers().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                        }
                        for(final User user : users.get("hosting")){
                            CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowing().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                            CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowers().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                        }
                        for(final User user : users.get("bouncing")){
                            CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowing().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                            CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowers().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                        }
                        for(final User user : users.get("going")){
                            CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowing().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                            CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowers().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                        }
                        for(final User user : users.get("inviting")){
                            CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowing().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                            CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                                @Override
                                public void onResultReady(List<String> result) {
                                    if (result != null)
                                        user.getFollowers().addAll(result);
                                    threadManager.completeThreads();
                                }
                            });
                        }
                    }
                });

                Log.d("Get User Events", result.get(0));
            }
        }).execute();
    }

    /**Get Best Friend from server*/
    public static void server_getBestFriends(String userId, final OnResultReadyListener<List<BestFriend>> delegate ){
        String url =  mainActivity.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                List<BestFriend> bestFriends = new ArrayList<BestFriend>();
                try {
                    JSONObject list = new JSONObject(result.get(0));
                    JSONArray main_json = list.getJSONArray("data");
                    String name = "", number = "";
                    for ( int i = 0; i < main_json.length(); i++ ) {
                        JSONObject data = main_json.getJSONObject(i);
                        name = data.getString("name");
                        number = data.getString("contact");
                        bestFriends.add(new BestFriend(name, number));
                    }
                    if(delegate != null) {
                        delegate.onResultReady(bestFriends);
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
                Log.d("getBestFriends", result.get(0) + "");
            }
        }).execute();
    }

    /**Get events in specified distance*/
    public static void server_getEventsInDistance(String minLat, String maxLat, String minLng, String maxLng, final OnResultReadyListener<ArrayList<Party>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/find-by-coordinate?min_lat=" + minLat
                + "&max_lat=" + maxLat + "&min_lng=" + minLng + "&max_lng=" + maxLng
                + "&access_token=" + DatabaseAccess.getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<Party> parties = new ArrayList<>();
                try {
                    JSONObject json_result = new JSONObject(result.get(0));
                    JSONArray data = json_result.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        parties.add(constructParty(body));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("Get Events In Distance", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(parties);
            }
        }).execute();
    }

    /**Get event's invited users from server*/
    public static void server_getInvitesOfEvent(String eventID, final OnResultReadyListener<ArrayList<User>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID
                + "/invites?access_token=" + DatabaseAccess.getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<User> invites = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        invites.add(constructUser(body));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Get Invites of Event", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(invites);
            }
        }).execute();
    }

    /** Get Notification by UserID */
    public static void server_getNotificationsOfUser(String userID, final OnResultReadyListener<ArrayList<Notification>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/notifications?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<Notification> notifications = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        notifications.add(constructNotification(body));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Get Notifications", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(notifications);
            }
        }).execute();
    }

    /**Get events by keyword*/
    public static void server_getEventsByKeyword(String keyword, final OnResultReadyListener<ArrayList<Party>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/find-by-keyword?keyword=" + keyword
                + "&start_after=1400000000&end_after=" + Calendar.getInstance().getTimeInMillis()/1000 + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<Party> parties = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        parties.add(constructParty(body));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Get Events by Keyword", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(parties);
            }
        }).execute();

    }

    /**Get users by keyword*/
    public static void server_getUsersByKeyword(String keyword, final OnResultReadyListener<ArrayList<User>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/find-by-keyword?keyword="
                + keyword + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);

        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                final ArrayList<User> users = new ArrayList<>();
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    JSONArray data = main_json.getJSONArray("data");
                    for ( int i = 0; i < data.length(); i++ ) {
                        HashMap<String, String> body = new HashMap<>();
                        Iterator iterKey = data.getJSONObject(i).keys();
                        while (iterKey.hasNext()) {
                            String key = (String) iterKey.next();
                            body.put(key, data.getJSONObject(i).getString(key));
                        }
                        users.add(constructUser(body));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Light-weight threads management
                class ThreadManager{
                    private int max, completes;
                    public ThreadManager(int max){
                        this.max = max;
                    }
                    void completeThreads(){
                        completes++;
                        if ( completes >= max && delegate != null )
                            delegate.onResultReady(users);
                    }
                }
                final ThreadManager threadManager = new ThreadManager(users.size()*2);

                for(final User user : users){
                    CurrentUser.server_getUserFollowing(user.getUserID(), new OnResultReadyListener<List<String>>() {
                        @Override
                        public void onResultReady(List<String> result) {
                            if (result != null)
                                user.getFollowing().addAll(result);
                            threadManager.completeThreads();
                        }
                    });
                    CurrentUser.server_getUserFollowers(user.getUserID(), new OnResultReadyListener<List<String>>() {
                        @Override
                        public void onResultReady(List<String> result) {
                            if (result != null)
                                user.getFollowers().addAll(result);
                            threadManager.completeThreads();
                        }
                    });
                }

                Log.d("Get Users by Keyword", result.get(0));
            }
        }).execute();

    }


//----------------------------------------------------------------------------------DELETE Requests

    /**Delete Best Friend on server*/
    public static void server_deleteBestFriend(String userId, String number, final OnResultReadyListener<String> delegate){
        String url = mainActivity.getString(R.string.server_url) + "users/"  + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();
        body.put("contact", number);
        RequestComponents comp = new RequestComponents(url, "DELETE", body);
        new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CurUser_DelBestFriend", result.get(0) + "");
                if ( delegate != null)
                    delegate.onResultReady(status);
            }
        }).execute();
    }

    /**Delete party from server*/
    public static void server_deleteParty(String partyID, final OnResultReadyListener<String> delegate) {
        RequestComponents comps[] = new RequestComponents[1];
        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID + "?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        String result = null;
        comps[0] = new RequestComponents(url, "DELETE", null);
        new DatabaseAccess.HttpRequestTask(mainActivity, comps, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                String status = null;
                try {
                    JSONObject main_json = new JSONObject(result.get(0));
                    status = main_json.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("CurUser_DeleteEvent", result.get(0));
                if ( delegate != null)
                    delegate.onResultReady(status);
            }
        }).execute();
        Log.d("Delete Party", result);
    }

//-----------------------------------------------------------------------------------Helper Methods

    /**Fill in all party information locally*/
    private static Party constructParty(HashMap<String, String> info) {
        String partyID = "", name = "", emoji = "", startDateTime = "", endDateTime = "", address = "", str_isPublic = "", hostName = "",
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

            startDateTime = info.get("start_timestamp");
            endDateTime = info.get("end_timestamp");
            if ( startDateTime != null ) {
                startingDateTimeCalendar.setTime(new Date(Long.parseLong(startDateTime) * 1000));
            }
            if( endDateTime != null ){
                endingDateTimeCalendar.setTime(new Date(Long.parseLong(endDateTime) * 1000));
            }
            Log.d("Calendar", startingDateTimeCalendar.get(Calendar.MONTH)+"");

            address = info.get("address");
            mapAddress.setAddress_string(address);
            if ( info.get("lat") != null && info.get("lng") != null ) {
                mapAddress.setAddress_latlng(new LatLng(Double.parseDouble(info.get("lat")), Double.parseDouble(info.get("lng"))));
                Log.d("MyLatLng", mapAddress.getAddress_latlng().toString());
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

    /**Fill in all user information locally*/
    private static User constructUser(HashMap<String, String> info) {
        String userID = "", firstName = "", lastName = "", email = "", college = "", gender = "", date = "", profilePic = "";
        List bestFriends = new ArrayList(), followers = new ArrayList(), following = new ArrayList(),
                hosting = new ArrayList(), hosted = new ArrayList(), attending = new ArrayList(),
                attended = new ArrayList(), bounced = new ArrayList(), going = new ArrayList();
        List notifications = new ArrayList();
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
        User user = new User(userID, firstName, lastName, email, password, college, gender, birthday,
                bestFriends, followers, following, hosting, attended, hosted, bounced, attending, going, notifications,
                "https://cdn.pixabay.com/photo/2017/02/17/20/05/donald-2075124_960_720.png");
        return user;
    }

    /**Fill in notification information locally*/
    private static Notification constructNotification(HashMap<String, String> info) {
        // TODO: 06/02/2017 Wait for notification implementation
        return new Notification();
    }
}
