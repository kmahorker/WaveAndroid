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
import java.util.HashMap;

public final class DatabaseAccess{
    public static String access_token = "";
    public static String user_id = "";
    private static String currentMethod = "";

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

//----------------------------------------------------------------------------Meat Functions-------

    //Provide 1. Email 2. Password
    public static class LoginTask extends AsyncTask< String, String, String> {
        private String email, password;

        public LoginTask(String email, String password) {
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
                    String access_token = jsonObject.getJSONObject("data").getString("jwt");
                    String user_id = jsonObject.getJSONObject("data").getString("id");
                    new LoginInfoTask(user_id, access_token).execute();
                } catch (JSONException e) {e.printStackTrace();}
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
    }

    //Provide 1. User ID 2. JWT Token
    public static class LoginInfoTask extends AsyncTask<String, String, String> {
        private String id, jwt;

        public LoginInfoTask(String id, String jwt) {
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
                    Log.d("Result", result);
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
    }

    //Provide 1.last_name 2. first_name 3. email 4. college 5. password
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
            Log.d("Post Result------", result);
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
}
