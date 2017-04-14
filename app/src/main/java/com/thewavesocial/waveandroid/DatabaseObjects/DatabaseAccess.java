package com.thewavesocial.waveandroid.DatabaseObjects;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.*;
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
import java.util.HashMap;

public final class DatabaseAccess{
    public static String access_token = "";
    public static String user_id = "";

//----------------------------------------------------------------------------Caller Functions------

    public DatabaseAccess() {
    }

    public static long createParty(Party party) {
        throw new NullPointerException();
    }

    public static long createUser() {
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

//----------------------------------------------------------------------------Actual Functions------

    /**
     * Given: email, password
     * Result: user_id, jwt token
     */
    public static class LoginTask extends AsyncTask< String, String, String> {
        private String email, password;
        private Activity mainActivity;

        public LoginTask(Activity mainActivity, String email, String password) {
            this.mainActivity = mainActivity;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... params){
            String authenURL = "https://api.theplugsocial.com/v1/Auth?email=" + email + "&password=" + password;
            return parseJSONFromServer(authenURL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String user_id = jsonObject.getJSONObject("data").getString("id");
                    String access_token = jsonObject.getJSONObject("data").getString("jwt");

                    saveTokentoLocal(mainActivity, user_id, access_token);
                    new GetInfoTask(mainActivity, user_id, access_token).execute();
                } catch (JSONException e) {
                    UtilityClass.printAlertMessage(mainActivity, "Incorrect email or password", true);
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
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
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
    }

    /**
     * Given: user_id, jwt token
     * Result: all user information and login
     */
    public static class GetInfoTask extends AsyncTask<String, String, String> {
        private String id, jwt;
        private Activity mainActivity;

        public GetInfoTask(Activity mainActivity, String id, String jwt) {
            this.mainActivity = mainActivity;
            this.id = id;
            this.jwt = jwt;
        }

        @Override
        protected String doInBackground(String... params){
            if ( id.isEmpty() || jwt.isEmpty() ) {
                Log.d("mainUser_getAllInfo", "No user_id or access_token found.");
                return "";
            }
            String dataURL = "https://api.theplugsocial.com/v1/users/" + id + "?access_token=" + jwt;
            return parseJSONFromServer(dataURL);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
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
                // TODO: 04/13/2017 update local user info and proceed to home screen
                UtilityClass.printAlertMessage(mainActivity, "Welcome back, " + first_name + " " + last_name + "!", true );
            } catch (JSONException e) {
                Toast.makeText(mainActivity, "Please login...", Toast.LENGTH_LONG).show();
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
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
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
    }

    /**
     * Given: 1.last_name 2. first_name 3. email 4. college 5. password
     * Result: login automatically
     */
    public static class CreateUserTask extends AsyncTask<String, String, String> {
        private static String last_name, first_name, email, college, password;
        public CreateUserTask(String last_name, String first_name, String email, String college, String password){
            this.last_name = last_name;
            this.first_name = first_name;
            this.email = email;
            this.college = college;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... params){
            return postJSONToServer();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        private static String postJSONToServer() {
            HttpURLConnection connection = null;
            InputStream stream;
            BufferedReader reader = null;
            String error = "";
            try {
                URL url = new URL("https://api.theplugsocial.com/v1/users");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setDoOutput(true);
                connection.setDoInput(true);

                HashMap<String, String> params = new HashMap<>();
                params.put("last_name", last_name);
                params.put("first_name", first_name);
                params.put("email", email);
                params.put("college", college);
                params.put("password", password);

                OutputStream output = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
                writer.write(paramsToString(params));
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
            return error;
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
        editor.putString("loginID", id);
        editor.putString("loginJWT", jwt);
        editor.commit();
    }

    //Get login info from phone.
    public static String[] getTokenFromLocal(Activity mainActivity) {
        SharedPreferences pref = mainActivity.getPreferences(Context.MODE_PRIVATE);
        String id = pref.getString("loginID", "");
        String jwt = pref.getString("loginJWT", "");
        return new String[]{id, jwt};
    }

}