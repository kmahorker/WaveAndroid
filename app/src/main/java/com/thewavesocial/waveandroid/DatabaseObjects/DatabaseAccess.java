package com.thewavesocial.waveandroid.DatabaseObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.*;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public final class DatabaseAccess{

//----------------------------------------------------------------------------Caller Functions------

    public DatabaseAccess() {
    }

    //Basics Done
    public static User loginByEmail(final Activity mainActivity, String email, String password) {
        final User[] user = new User[1];
        HashMap<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        //Login User
        String authenURL = "https://api.theplugsocial.com/v1/auth";
        new HttpRequestTask(mainActivity, authenURL, "POST", body, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("LoginByEmail1", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String user_id = jsonObject.getJSONObject("data").getString("id");
                    String access_token = jsonObject.getJSONObject("data").getString("jwt");

                    saveTokentoLocal(mainActivity, user_id, access_token);
                } catch (JSONException e) {
                    UtilityClass.printAlertMessage(mainActivity, "Incorrect email or password", true);
                }

                //Get User Info
                String getUserURL = "https://api.theplugsocial.com/v1/users/" + getTokenFromLocal(mainActivity).get("id") + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
                new HttpRequestTask(mainActivity, getUserURL, "GET", null, new OnResultReady() {
                    @Override
                    public void sendBackResult(String result) {
                        Log.d("LoginByEmail2", result);
                    }
                }).execute();
            }
        }).execute();
        return user[0];
    }

    //Basics Done
    public static Party createParty(final Activity mainActivity, String name, String price, String address, String address2, String city, String state, String is_public, String start_date, String start_time, String end_date, String end_time) {
        final Party[] event_id = {null};
        String url = "https://api.theplugsocial.com/v1/events?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> event_info = new HashMap();
        event_info.put("name", name);
        event_info.put("price", price);
        event_info.put("address", address);
        event_info.put("address2", address2);
        event_info.put("city", city);
        event_info.put("state", state);
        event_info.put("is_public", is_public);
        event_info.put("start_date", start_date);
        event_info.put("start_time", start_time);
        event_info.put("end_date", end_date);
        event_info.put("end_time", end_time);

        new HttpRequestTask(mainActivity, url, "POST", event_info, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Create Party Result", result);
            }
        }).execute();
        return event_id[0];
    }

    //Basics Done
    public static void addUserToParty(final Activity mainActivity, String eventID, String userID, String relationship) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/users/"
                + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();
        body.put("relationship", relationship);
        new HttpRequestTask(mainActivity, url, "POST", body, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Add User to Party", result);
            }
        }).execute();
    }

    //Basics Done
    public static User createUser(final Activity mainActivity, String last_name, String first_name, String email, String college, String password) {
        final User[] user_id = {null};
        String url = "https://api.theplugsocial.com/v1/users";
        HashMap<String, String> user_info = new HashMap();
        user_info.put("last_name", last_name);
        user_info.put("first_name", first_name);
        user_info.put("email", email);
        user_info.put("college", college);
        user_info.put("password", password);
        new HttpRequestTask(mainActivity, url, "POST", user_info, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Create User Result", result);
            }
        }).execute();
        return user_id[0];
    }

    //Basics Done
    public static User getUser(final Activity mainActivity, String userID) {
        final User[] user = new User[1];
        HashMap tokens = getTokenFromLocal(mainActivity);
        String url = "https://api.theplugsocial.com/v1/users/" + userID + "?access_token=" + tokens.get("jwt");
        new HttpRequestTask(mainActivity, url, "GET", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("GetUserResult", result);
//                try {
//                    JSONObject jsonObject = new JSONObject(result);
//                    String id = jsonObject.getJSONObject("data").getString("id");
//                    String fb_id = jsonObject.getJSONObject("data").getString("fb_id");
//                    String last_name = jsonObject.getJSONObject("data").getString("last_name");
//                    String first_name = jsonObject.getJSONObject("data").getString("first_name");
//                    String email = jsonObject.getJSONObject("data").getString("email");
//                    String image_path = jsonObject.getJSONObject("data").getString("image_path");
//                    String college = jsonObject.getJSONObject("data").getString("college");
//                    String gender = jsonObject.getJSONObject("data").getString("gender");
//                    String birthday = jsonObject.getJSONObject("data").getString("birthday");
//                    // TODO: 04/13/2017 update local user info and proceed to home screen
//                    UtilityClass.printAlertMessage(mainActivity, "Welcome back, " + first_name + " " + last_name + "!", true );
//                } catch (JSONException e) {
//                    Toast.makeText(mainActivity, "Please login...", Toast.LENGTH_LONG).show();
//                }
            }
        }).execute();
        return user[0];
    }

    //Basics Done
    public static Party getParty(final Activity mainActivity, String partyID) {
        final Party[] party = new Party[1];
        HashMap tokens = getTokenFromLocal(mainActivity);

        String url = "https://api.theplugsocial.com/v1/events/" + partyID + "/users?access_token=" + tokens.get("jwt");
        new HttpRequestTask(mainActivity, url, "GET", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("GetPartyResult", result);
            }
        }).execute();
        return party[0];
    }

    //Basics Done
    public static ArrayList<Party> getUserParties(final Activity mainActivity, String userID) {
        ArrayList<Party> parties = new ArrayList<>();
        HashMap tokens = getTokenFromLocal(mainActivity);

        String url = "https://api.theplugsocial.com/v1/users/" + userID + "/events?access_token=" + tokens.get("jwt");
        new HttpRequestTask(mainActivity, url, "GET", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("GetUserPartiesResult", result);
            }
        }).execute();
        return parties;
    }

    //Basics Done
    public static void getFollowing(final Activity mainActivity, String userID) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "/followings?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "GET", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Get Following", result);
            }
        }).execute();
    }

    //Basics Done
    public static void getFollowers(final Activity mainActivity, String userID) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "/followers?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "GET", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Get Followers", result);
            }
        }).execute();
    }

    //Basics Done
    public static void followUser(final Activity mainActivity, String userID) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + getTokenFromLocal(mainActivity).get("id")
                + "/followings/" + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "POST", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Follow User", result);
            }
        }).execute();
    }

    //Basics Done
    public static void deleteParty(final Activity mainActivity, String partyID) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID + " ?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "DELETE", null, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Delete Party", result);
            }
        }).execute();
    }

    //Basics Done
    public static boolean deleteUserFromParty(final Activity mainActivity, String partyID, String userID, String relationship) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID + "/users/" + userID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();
        body.put("relationship", relationship);
        new HttpRequestTask(mainActivity, url, "DELETE", body, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Delete User From Party", result);
            }
        }).execute();
    }

    //Basics Done
    public static void updateParty(final Activity mainActivity, String eventID, HashMap<String, String> body) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "POST", body, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Update Party", result);
            }
        }).execute();

    }

    //Basics Done
    public static void updateUser(final Activity mainActivity, String userID, HashMap<String, String> body) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        new HttpRequestTask(mainActivity, url, "POST", body, new OnResultReady() {
            @Override
            public void sendBackResult(String result) {
                Log.d("Update User", result);
            }
        }).execute();

    }

    //Basics DOne
    public static void unfollowUser(final Activity mainActivity, String userID) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + getTokenFromLocal(mainActivity).get("id")
                + "/followings/" + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
    }

//----------------------------------------------------------------------------Actual Functions------

    public interface OnResultReady {
        void sendBackResult(String result);
    }

    /**
     * Given: url, endpoint, body, resultBack
     * Result: all user information and login
     */
    public static class HttpRequestTask extends AsyncTask<String, String, String> {
        private String url, endpoint;
        private HashMap<String, String> body;
        private OnResultReady delegate = null;
        private Activity mainActivity;
        private ProgressDialog progress;

        public HttpRequestTask(Activity mainActivity, String url, String endpoint, HashMap<String, String> body, OnResultReady delegate) {
            this.url = url;
            this.endpoint = endpoint;
            this.body = body;
            this.mainActivity = mainActivity;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mainActivity);
            progress.setTitle("Please wait");
            progress.setMessage("Connecting to Server...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params){
            return sendHttpRequest(url, endpoint, body);
        }

        @Override
        protected void onPostExecute(String result) {
            progress.dismiss();
            delegate.sendBackResult(result);
        }


        private String sendHttpRequest(String url, String endpoint, HashMap<String, String> body) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;

            try {
                URL request_url = new URL(url);
                connection = (HttpURLConnection) request_url.openConnection();
                connection.setReadTimeout(10000); //Time out both at 10 seconds
                connection.setConnectTimeout(10000);
                connection.setRequestMethod(endpoint); //Set endpoint

                if ( body != null ) {
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    HashMap<String, String> params = body;
                    OutputStream output = connection.getOutputStream();
                    writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                    writer.write(paramsToString(params));
                    writer.flush();
                    writer.close();
                    output.close();
                }
                connection.connect(); //Begin http request

                //Now, read the JSON response from server and return
                InputStream stream;
                if ( connection.getResponseCode() == 500 )
                    stream = connection.getErrorStream();
                else
                    stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuffer buffer = new StringBuffer();
                while( (line = reader.readLine()) != null )
                    buffer.append(line + "\n");

                connection.disconnect();
                reader.close();
                return buffer.toString();
            } catch (IOException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UtilityClass.printAlertMessage(mainActivity, "Sorry. Internet Connection Error.", true);
                    }
                });
            }

            //Close connection and reader and writer (Just in case things don't go well...)
            finally {
                try {
                    if ( connection != null )
                        connection.disconnect();
                    if ( reader != null )
                        reader.close();
                    if ( writer != null )
                        writer.close();
                } catch (IOException e){e.printStackTrace();}
            }
            return "error";
        }


        private static String paramsToString(HashMap<String, String> params) throws UnsupportedEncodingException {
            String result = "";
            boolean first = true;
            for (String key : params.keySet()) {
                if (first)
                    first = false;
                else
                    result += "&";
                result += URLEncoder.encode(key, "UTF-8");
                result += "=";
                result += URLEncoder.encode(params.get(key), "UTF-8");
            }
            return result.toString();
        }
    }

//----------------------------------------------------------------------------Local Save Functions--

    //Save login info to phone.
    public static void saveTokentoLocal(Activity mainActivity, String id, String jwt) {
        SharedPreferences pref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.putString("jwt", jwt);
        editor.commit();
    }

    //Get login info from phone.
    public static HashMap<String, String> getTokenFromLocal(Activity mainActivity) {
        SharedPreferences pref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("id", pref.getString("id", ""));
        tokens.put("jwt", pref.getString("jwt", ""));
        return tokens;
    }

}