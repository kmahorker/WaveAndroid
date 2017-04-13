package com.thewavesocial.waveandroid.DatabaseObjects;

import android.os.AsyncTask;
import android.util.Log;
import com.thewavesocial.waveandroid.BusinessObjects.*;
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
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseAccess{
    public static String access_token = "";
    public static String user_id = "";
    private static String currentMethod = "";

    public DatabaseAccess() {
    }

    public static long createParty(Party party) {
        throw new NullPointerException();
    }

    public static long createUser() {
        new JSONPostingTask().execute("https://api.theplugsocial.com/v1/users");
        return 0;
    }

    public static boolean deleteParty(long partyID) {
        throw new NullPointerException();
    }

    public static boolean deleteUser(long userID) {
        throw new NullPointerException();
    }

    public static Party getParty(long partyID) {
        throw new NullPointerException();
    }

    public static User getUser(long userID) {
        //Temporary for testing
        return CurrentUser.theUser;
    }

    public static boolean updateParty(long partyID, String param) {
        throw new NullPointerException();
    }

    public static boolean updateUser(long userID, String param) {
        throw new NullPointerException();
    }

//----------------------------------------------------------------------------Authentication-------

    public static void mainUser_login(String email, String password) {
        email = "pasta@farian.org"; //testing purpose
        password = "MEATBALLS!"; //testing purpose
        String authenURL = "https://api.theplugsocial.com/v1/Auth?email=" + email + "&password=" + password;

        currentMethod = "mainUser_login";
        new JSONParsingTask().execute(authenURL);
    }

    public static void mainUser_getAllInfo() {
        if ( user_id.isEmpty() || access_token.isEmpty() )
            Log.d("mainUser_getAllInfo", "No user_id or access_token found.");
        String dataURL = "https://api.theplugsocial.com/v1/users/" + user_id + "?access_token=" + access_token;

        currentMethod = "mainUser_getAllInfo";
        new JSONParsingTask().execute(dataURL);
    }

    public static class JSONParsingTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params){
            return parseJSONFromServer(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch(currentMethod) {
                case "mainUser_login":
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        access_token = jsonObject.getJSONObject("data").getString("jwt");
                        user_id = jsonObject.getJSONObject("data").getString("id");
                    } catch (JSONException e) {e.printStackTrace();}
                    mainUser_getAllInfo();
                    break;
                case "mainUser_getAllInfo":
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String id = jsonObject.getJSONObject("data").getString("id");
                        String fb_id = jsonObject.getJSONObject("data").getString("fb_id");
                        String last_name = jsonObject.getJSONObject("data").getString("last_name");
                        String first_name = jsonObject.getJSONObject("data").getString("first_name");
                        String email = jsonObject.getJSONObject("data").getString("email");
                        String image_path = jsonObject.getJSONObject("data").getString("image_path");
                        String college = jsonObject.getJSONObject("data").getString("college");
                        String gender = jsonObject.getJSONObject("data").getString("gender");
                        String birthday = jsonObject.getJSONObject("data").getString("birthday");
                        Log.d("JSON Result", "-----------------------------------------------------");
                        Log.d("id", id);
                        Log.d("fb_id", fb_id);
                        Log.d("last_name", last_name);
                        Log.d("first_name", first_name);
                        Log.d("email", email);
                        Log.d("image_path", image_path);
                        Log.d("college", college);
                        Log.d("gender", gender);
                        Log.d("birthday", birthday);
                        Log.d("JSON Result", "-----------------------------------------------------");
                    } catch (JSONException e) {e.printStackTrace();}
                default:
                    //do nothing
            }
        }
    }

    private static String parseJSONFromServer(String server_url) {
        HttpURLConnection connection = null;
        InputStream stream;
        BufferedReader reader = null;
        String error = "";
        try {
            URL url = new URL(server_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if ( connection.getResponseCode() == 500 )
                stream = connection.getErrorStream();
            else
                stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            String line ="";
            StringBuffer buffer = new StringBuffer();
            while( (line = reader.readLine()) != null )
                buffer.append(line);
            return buffer.toString();

        } catch (IOException e) {e.printStackTrace();}

        finally {
            if ( connection != null )
                connection.disconnect();
            try {
                if ( reader != null )
                    reader.close();
            }
            catch (IOException e){e.printStackTrace();}
        }
        return error + server_url;
    }

    public static class JSONPostingTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params){
            return postJSONToServer(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Post Result------", result);
        }
    }

    private static String postJSONToServer(String server_url) {
        HttpURLConnection connection = null;
        InputStream stream;
        BufferedReader reader = null;
        String error = "";
        try {
            URL url = new URL(server_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            List<AbstractMap.SimpleEntry> params = new ArrayList<>();
            params.add(new AbstractMap.SimpleEntry("email", "12345@gmail.com"));
            params.add(new AbstractMap.SimpleEntry("last_name", "Mario"));
            params.add(new AbstractMap.SimpleEntry("first_name", "Brothers"));
            params.add(new AbstractMap.SimpleEntry("college", "Stanefford"));
            params.add(new AbstractMap.SimpleEntry("password", "trans"));

            OutputStream output = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
            writer.write(toString(params));
            writer.flush();
            writer.close();
            output.close();
            connection.connect();

            if ( connection.getResponseCode() == 500 )
                stream = connection.getErrorStream();
            else
                stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));
            String line ="";
            StringBuffer buffer = new StringBuffer();
            while( (line = reader.readLine()) != null )
                buffer.append(line+"\n");
            return buffer.toString();

        } catch (IOException e) {e.printStackTrace();}

        finally {
            if ( connection != null )
                connection.disconnect();
            try {
                if ( reader != null )
                    reader.close();
            }
            catch (IOException e){e.printStackTrace();}
        }
        return error + server_url;
    }

    private static String toString(List<AbstractMap.SimpleEntry> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (AbstractMap.SimpleEntry pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey().toString(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
        }

        return result.toString();
    }
}
