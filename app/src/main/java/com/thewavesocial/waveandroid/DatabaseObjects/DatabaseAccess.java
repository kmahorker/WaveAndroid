package com.thewavesocial.waveandroid.DatabaseObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thewavesocial.waveandroid.BusinessObjects.BestFriend;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.MapAddress;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class DatabaseAccess {
    public static Activity mainActivity;

    /**
     * Initialize mainActivity
     */
    public static void setContext(Activity activity) {
        mainActivity = activity;
    }

    /**
     * Given: url, endpoint, body, callBack
     * Result: all user information and login
     */
    public static class HttpRequestTask extends AsyncTask<String, String, ArrayList<String>> {
        private RequestComponents[] comps;
        private Activity mainActivity;
        private ProgressDialog progress;
        private Handler handler;
        private Runnable run;
        private OnResultReadyListener<ArrayList<String>> delegate;

        public HttpRequestTask(Activity mainActivity, RequestComponents[] comps, OnResultReadyListener<ArrayList<String>> delegate) {
            this.mainActivity = mainActivity;
            this.comps = comps;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(mainActivity, "Loading...", Toast.LENGTH_SHORT).show();
//            if (progressShowing)
//                return;
//            progress = new ProgressDialog(mainActivity);
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setTitle("Please wait");
//            progress.setMessage("Connecting to Server...");
//            progress.setCancelable(false);
//            progress.show();
//            progressShowing = true;
//            handler = new Handler();
//            run = new Runnable() {
//                @Override
//                public void run() {
//                    progress.show();
//                }
//            };
//            handler.postDelayed(run, 3000);
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> results = new ArrayList<>();
            for (RequestComponents comp : comps) {
                results.add(sendHttpRequest(comp.url, comp.endpoint, comp.body));
            }
            return results;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
//            if ( progress != null && progress.isShowing() )
//                progress.dismiss();
//            progressShowing = false;
//            handler.removeCallbacks(run);
            if (delegate != null)
                delegate.onResultReady(result);
        }


        private String sendHttpRequest(String url, String endpoint, HashMap<String, String> body) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            BufferedWriter writer = null;

            try {
                URL request_url = new URL(url);
                connection = (HttpURLConnection) request_url.openConnection();
                connection.setReadTimeout(2000); //Time out both at 5 seconds
                connection.setConnectTimeout(2000);
                connection.setRequestMethod(endpoint); //Set endpoint

                if (body != null) {
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
                if (connection.getResponseCode() == 500)
                    stream = connection.getErrorStream();
                else
                    stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = reader.readLine()) != null)
                    buffer.append(line + "\n");

                connection.disconnect();
                reader.close();
                return buffer.toString();
            } catch (IOException e) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        UtilityClass.printAlertMessage(mainActivity, "Sorry. Internet Connection Error.", "Network Error", true);
//                    }
//                });
            }

            //Close connection and reader and writer (Just in case things don't go well...)
            finally {
                try {
                    if (connection != null)
                        connection.disconnect();
                    if (reader != null)
                        reader.close();
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    /**
     * Given: url, endpoint, body, callBack
     * Result: all user information and login
     */
    public static class ImageDownloadTask extends AsyncTask<String, String, Bitmap> {
        private RequestComponents comp;
        private Activity mainActivity;
        private ProgressDialog progress;
        private Handler handler;
        private Runnable run;
        private OnResultReadyListener<Bitmap> delegate;

        public ImageDownloadTask(Activity mainActivity, RequestComponents comp, OnResultReadyListener<Bitmap> delegate) {
            this.mainActivity = mainActivity;
            this.comp = comp;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            Toast.makeText(mainActivity, "Loading...", Toast.LENGTH_SHORT).show();
//            if (progressShowing)
//                return;
//            progress = new ProgressDialog(mainActivity);
//            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progress.setTitle("Please wait");
//            progress.setMessage("Connecting to Server...");
//            progress.setCancelable(false);
//            progress.show();
//            progressShowing = true;
//            handler = new Handler();
//            run = new Runnable() {
//                @Override
//                public void run() {
//                    progress.show();
//                }
//            };
//            handler.postDelayed(run, 3000);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return sendHttpRequest(comp.url, comp.endpoint, comp.body);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
//            if ( progress != null && progress.isShowing() )
//                progress.dismiss();
//            progressShowing = false;
//            handler.removeCallbacks(run);
            if (delegate != null)
                delegate.onResultReady(result);
        }


        private Bitmap sendHttpRequest(String url, String endpoint, HashMap<String, String> body) {
            HttpURLConnection connection = null;
            BufferedWriter writer = null;

            try {
                URL request_url = new URL(url);
                connection = (HttpURLConnection) request_url.openConnection();
                connection.setReadTimeout(5000); //Time out both at 5 seconds
                connection.setConnectTimeout(5000);
                connection.setRequestMethod(endpoint); //Set endpoint

                if (body != null) {
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
                if (connection.getResponseCode() == 500)
                    stream = connection.getErrorStream();
                else
                    stream = connection.getInputStream();

                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                connection.disconnect();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Close connection and reader and writer (Just in case things don't go well...)
            finally {
                try {
                    if (connection != null)
                        connection.disconnect();
                    if (writer != null)
                        writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
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

    /**
     * Given: bitmap, callBack
     * Result: all user information and login
     */
    public static class ImageUploadTask extends AsyncTask<String, String, String> {
        private static Activity mainActivity;
        private Bitmap image;
        private OnResultReadyListener<String> delegate;
        private String url;

        public ImageUploadTask(Activity mainActivity, String url, Bitmap image, OnResultReadyListener<String> delegate) {
            this.mainActivity = mainActivity;
            this.image = image;
            this.delegate = delegate;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return sendImageRequest(url, image);
        }

        @Override
        protected void onPostExecute(String result) {
            delegate.onResultReady(result);
        }

        /**
         * Use OkHttpClient to upload image
         */
        private String sendImageRequest(String url, Bitmap bitmap) {
            File file = bitmapToFile(bitmap);
            String content_type = getFileType(file.getPath());
            String file_path = file.getAbsolutePath();

            OkHttpClient client = new OkHttpClient();
            RequestBody file_body = RequestBody.create(MediaType.parse(content_type), file);
            RequestBody request_body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("type", content_type)
                    .addFormDataPart("profile-photo", file_path.substring(file_path.lastIndexOf('/') + 1), file_body)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .post(request_body)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.message();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }

        /**
         * Convert bitmap to File.
         */
        private File bitmapToFile(Bitmap bitmap) {
            File file = new File(mainActivity.getCacheDir(), "profile.jpg");
            try {
                //create a file to write bitmap data
                file.createNewFile();

                //Convert bitmap to byte array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }

        /**
         * Get filetype of image
         */
        private static String getFileType(String path) {
            String extention = MimeTypeMap.getFileExtensionFromUrl(path);
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extention);
        }
    }

//todo -------------------------------------------------------------------------Local Save Functions

    /**
     * Save login info to phone.
     */
    public static void saveTokentoLocal(Activity mainActivity, String id) {
        SharedPreferences pref = mainActivity.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.commit();
    }

    /**
     * Get login info from phone.
     */
    public static HashMap<String, String> getTokenFromLocal(Activity mainActivity) {
        SharedPreferences pref = mainActivity.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("id", pref.getString("id", ""));
        tokens.put("jwt", pref.getString("jwt", ""));
        return tokens;
    }

    public static void server_upload_image(Bitmap bitmap, final OnResultReadyListener<String> delegate) {
/*
        String url = mainActivity.getString(R.string.server_url) + "users/" + getTokenFromLocal(mainActivity).get("id")
                + "/profile-photo?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
*/
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getTokenFromLocal(mainActivity).get("id").toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    public static void server_login_facebook(String fb_token, final OnResultReadyListener<String> delegate) {
        //String url = mainActivity.getString(R.string.server_url) + "FBauth";
        final HashMap<String, String> body = new HashMap<>();
        body.put("fb_token", fb_token);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        Query query = db.orderByChild("fb_id").startAt(fb_token);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    if (postSnapshot.hasChild(body.get("fb_token"))) {
                        DatabaseAccess.saveTokentoLocal(mainActivity, body.get("fb_token"));
                        server_getUserObject(DatabaseAccess.getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<User>() {
                            @Override
                            public void onResultReady(User result) {
                                if (result != null) {
                                    CurrentUser.theUser = result;
                                }
                            }
                        });
                        delegate.onResultReady("success");
                    }
                    else
                        delegate.onResultReady("error");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //RequestComponents comp = new RequestComponents(url, "POST", body);
        /*new DatabaseAccess.HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                Log.d("Login by Facebook", result.get(0));
                try {
                    JSONObject jsonObject = new JSONObject(result.get(0));
                    String user_id = jsonObject.getJSONObject("data").getString("id");
                    String access_token = jsonObject.getJSONObject("data").getString("jwt");
                    DatabaseAccess.saveTokentoLocal(mainActivity, user_id);, access_token);

                    server_getUserObject(DatabaseAccess.getTokenFromLocal(mainActivity).get("id"), new OnResultReadyListener<User>() {
                        @Override
                        public void onResultReady(User result) {
                            if (result != null) {
                                CurrentUser.theUser = result;
                            }
                        }
                    });
                    delegate.onResultReady("success");
                } catch (JSONException e) {
                    UtilityClass.printAlertMessage(mainActivity, "Could not authorize facebook", "Facebook Login Error", true);
                    delegate.onResultReady("error");
                }
            }
        });*/
    }


//todo --------------------------------------------------------------------------------POST Requests

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

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        String eventID = UUID.randomUUID().toString(); //unique ID for each event
        db.child(eventID).child("name").setValue(name);
        db.child(eventID).child("emoji").setValue(emoji);
        db.child(eventID).child("price").setValue(price);
        db.child(eventID).child("address").setValue(address);
        db.child(eventID).child("lat").setValue(lat);
        db.child(eventID).child("lng").setValue(lng);
        db.child(eventID).child("isPublic").setValue(isPublic);
        db.child(eventID).child("startTimeStamp").setValue(startTimeStamp);
        db.child(eventID).child("endingTimeStamp").setValue(endingTimeStamp);
        db.child(eventID).child("minAge").setValue(minAge);
        db.child(eventID).child("maxAge").setValue(maxAge);
        if(delegate != null)
            delegate.onResultReady("success,"+eventID);

/*	HashMap<String, String> event_info = new HashMap();
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
	event_info.put("max_age", maxAge + "");*/ //obsolete
    }

    public static void server_createNewUser(String first_name,
                                            String last_name,
                                            String email,
                                            String college,
                                            String password,
                                            String fb_id,
                                            String fb_token,
                                            String gender,
                                            String birthday,
                                            final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        String userID = UUID.randomUUID().toString(); //unique ID for each event
        db.child(userID).child("first_name").setValue(first_name);
        db.child(userID).child("last_name").setValue(last_name);
        db.child(userID).child("email").setValue(email);
        db.child(userID).child("college").setValue(college);
        db.child(userID).child("password").setValue(password);
        db.child(userID).child("fb_id").setValue(fb_id);
        db.child(userID).child("fb_token").setValue(fb_token);
        db.child(userID).child("gender").setValue(gender);
        db.child(userID).child("birthday").setValue(birthday);
        if(delegate != null)
            delegate.onResultReady("success,"+userID);

    }


    public static void server_manageUserForParty(String userID, String eventID, String relationship, String action, final OnResultReadyListener<String> delegate) {
        //RequestComponents[] comps = new RequestComponents[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("users").child(userID);
        //HashMap<String, String> about = new HashMap();
        //about.put("relationship", relationship);
        if (action.equals("POST"))
            db.child("relationship").setValue(relationship);
        else if (action.equals("DELETE"))
            db.child("relationship").removeValue(); //"DELETE" will just remove the relationship
        else
            Log.d("Action", "Illegal passed action argument: " + action);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_updateUser(String userID, HashMap<String, String> body, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID);
        Iterator it = body.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            db.child(pair.getKey().toString()).setValue(pair.getValue()); //iterates through every value in body parameter and updates those values in database
        }
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_updateParty(String partyID, HashMap<String, String> body, final OnResultReadyListener<String> delegate) {
        //String url = mainActivity.getString(R.string.server_url) + "events/" + partyID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(partyID);
/*        RequestComponents[] comps = new RequestComponents[1];
        comps[0] = new RequestComponents(url, "POST", body);*/
        Iterator it = body.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            db.child(pair.getKey().toString()).setValue(pair.getValue()); //iterates through every value in body parameter and updates those values in database
        }
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_followUser(String userID, String targetID, final OnResultReadyListener<String> delegate) {
        /*String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followings/" + targetID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        db.child(userID).child("followings").child(targetID).setValue(targetID); //not so sure what value should appear as in followings list, so I just set it to the input targetID
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_addBestFriend(String name, String number, String userId, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");
        HashMap<String, String> body = new HashMap<>();*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bestfriends");
        BestFriend bestFriend = new BestFriend(name, number);
        db.setValue(bestFriend);
        db.child("userID").setValue(userId);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_inviteUserToEvent(String userID, String eventID, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("invites").child(userID);
        db.setValue(userID); //just setting the value of a user invited to the party as the userID, can change this as well
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_createNotification(String receiverID, String senderID, String eventID, String type, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + receiverID
                + "/notifications?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
/*        HashMap<String, String> body = new HashMap<>();*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(receiverID).child("notifications");
        db.child("sender_id").setValue(senderID);
        db.child("event_id").setValue(eventID);
        db.child("type").setValue(type);
        if(delegate != null)
            delegate.onResultReady("success");
    }
    public static void server_getUserObject(final String userID, final OnResultReadyListener<User> delegate) {
        /*String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (delegate != null)
                        delegate.onResultReady(user);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getPartyObject(final String partyID, final OnResultReadyListener<Party> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/" + partyID
                + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(partyID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Party party = dataSnapshot.getValue(Party.class);
                    if(delegate != null)
                        delegate.onResultReady(party);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getUserFollowers(String userID, final OnResultReadyListener<List<User>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followers?access_token=" + getTokenFromLocal(mainActivity).get("jwt");

*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("followers");
        final List<User> followerList = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    followerList.add(user);
                }
                if(delegate != null)
                    delegate.onResultReady(followerList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getUserFollowing(String userID, final OnResultReadyListener<List<User>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/followings?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
    */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("followings");
        final List<User> followingList = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    followingList.add(user);
                }
                if(delegate != null)
                    delegate.onResultReady(followingList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void server_getEventsOfUser(String userID, final OnResultReadyListener<HashMap<String, ArrayList<Party>>> delegate) {
        /*String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "/events?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("events");
        final ArrayList<Party> attending = new ArrayList<>();
        final ArrayList<Party> going = new ArrayList<>();
        final ArrayList<Party> hosting = new ArrayList<>();
        final ArrayList<Party> bouncing = new ArrayList<>();
        final HashMap<String, ArrayList<Party>> parties = new HashMap();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.child("relationship").getValue().toString() == "attending")
                        attending.add(postSnapshot.getValue(Party.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "going")
                        going.add(postSnapshot.getValue(Party.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "hosting")
                        hosting.add(postSnapshot.getValue(Party.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "bouncing")
                        bouncing.add(postSnapshot.getValue(Party.class));
                }
                parties.put("attending", attending);
                parties.put("hosting", hosting);
                parties.put("bouncing", bouncing);
                parties.put("going", going);
                if(delegate != null)
                    delegate.onResultReady(parties);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void server_getUsersOfEvent(final String eventID, final OnResultReadyListener<HashMap<String, ArrayList<User>>> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/users?access_token="
                + getTokenFromLocal(mainActivity).get("jwt");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("users");
        final ArrayList<User> attending = new ArrayList<>();
        final ArrayList<User> going = new ArrayList<>();
        final ArrayList<User> hosting = new ArrayList<>();
        final ArrayList<User> bouncing = new ArrayList<>();
        final HashMap<String, ArrayList<User>> users = new HashMap();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(postSnapshot.child("relationship").getValue().toString() == "attending")
                        attending.add(postSnapshot.getValue(User.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "going")
                        going.add(postSnapshot.getValue(User.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "hosting")
                        hosting.add(postSnapshot.getValue(User.class));
                    else if(postSnapshot.child("relationship").getValue().toString() == "bouncing")
                        bouncing.add(postSnapshot.getValue(User.class));
                }
                users.put("attending", attending);
                users.put("hosting", hosting);
                users.put("bouncing", bouncing);
                users.put("going", going);
                if(delegate != null)
                    delegate.onResultReady(users);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getBestFriends(String userId, final OnResultReadyListener<List<BestFriend>> delegate) {
  /*      String url = mainActivity.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);*/

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bestfriends");
        final ArrayList<BestFriend> bestfriends = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    bestfriends.add(postSnapshot.getValue(BestFriend.class));
                //Log.d("getBestFriends", result.get(0) + "");
                if (delegate != null) {
                    delegate.onResultReady(bestfriends);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

   public static void server_getEventsInDistance(final String minLat, final String maxLat, final String minLng, final String maxLng, final OnResultReadyListener<ArrayList<Party>> delegate) {
       DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
       final ArrayList<Party> parties = new ArrayList<>();
       db.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                   float lat = Float.parseFloat(postSnapshot.child("lat").getValue().toString());
                   float lng = Float.parseFloat(postSnapshot.child("lng").getValue().toString());
                   if(      lat > Float.parseFloat(minLat) && lng < Float.parseFloat(maxLat) &&
                            lng > Float.parseFloat(minLng) && lng < Float.parseFloat(maxLng))
                       parties.add(postSnapshot.getValue(Party.class));
               }
               if(delegate != null)
                   delegate.onResultReady(parties);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
       //UNSURE ABOUT THIS ONE FOR NOW
        /*String url = mainActivity.getString(R.string.server_url) + "events/find-by-coordinate?min_lat=" + minLat
                + "&max_lat=" + maxLat + "&min_lng=" + minLng + "&max_lng=" + maxLng
                + "&start_after=" + 1400000000 + "&end_after" + Calendar.getInstance().getTimeInMillis() / 1000
                + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        /*RequestComponents comp = new RequestComponents(url, "GET", null);
        new HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
            @Override
            public void onResultReady(ArrayList<String> result) {
                ArrayList<Party> parties = new ArrayList<>();
                try {
                    JSONObject json_result = new JSONObject(result.get(0));
                    JSONArray data = json_result.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        HashMap<String, String> body = extractJSONData(data.getJSONObject(i));
                        parties.add(constructParty(body));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("Get Events In Distance", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(parties);
            }
    }).execute();*/
}

    public static void server_getInvitesOfEvent(String eventID, final OnResultReadyListener<ArrayList<User>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID
                + "/invites?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("invites");
        final ArrayList<User> invites = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    invites.add(postSnapshot.getValue(User.class));
                //Log.d("Get Invites of Event", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(invites);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Get Notification by UserID
     */
    public static void server_getNotificationsOfUser(String userID, final OnResultReadyListener<ArrayList<Notification>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/notifications?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("notifications");
        final ArrayList<Notification> notifications = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    notifications.add(postSnapshot.getValue(Notification.class));
                //Log.d("Get Invites of Event", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(notifications);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Get events by keyword
     */
    public static void server_getEventsByKeyword(String keyword, final OnResultReadyListener<ArrayList<Party>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/find-by-keyword?keyword=" + keyword
                + "&start_after=1400000000&end_after=" + Calendar.getInstance().getTimeInMillis() / 1000 + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        Query q1 = db.orderByChild("name").startAt(keyword);
        final ArrayList<Party> parties = new ArrayList<>();
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    parties.add(postSnapshot.getValue(Party.class));
                //Log.d("Get Invites of Event", result.get(0));
                if (delegate != null)
                    delegate.onResultReady(parties);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Get users by keyword
     */
    public static void server_getUsersByKeyword(String keyword, final OnResultReadyListener<ArrayList<User>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/find-by-keyword?keyword="
                + keyword + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        Query q1 = db.orderByChild("first_name").startAt(keyword);
        final ArrayList<User> users = new ArrayList<>();
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    users.add(postSnapshot.getValue(User.class));
                if (delegate != null)
                    delegate.onResultReady(users);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Download Profile Picture from Server. Return bitmap or null.
     */
    public static void server_getProfilePicture(String userID, final OnResultReadyListener<Bitmap> delegate) {
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID
                + "/profile-photo?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);
        new ImageDownloadTask(mainActivity, comp, new OnResultReadyListener<Bitmap>() {
            @Override
            public void onResultReady(Bitmap result) {
                if (result != null && delegate != null) {
                    delegate.onResultReady(result);
                    Log.d("Image_Download", "Success");
                }
            }
        });
    }


//todo ------------------------------------------------------------------------------DELETE Requests

    /**
     * Delete Best Friend on server
     */
    public static void server_deleteBestFriend(String userId, String number, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(mainActivity).get("jwt");*/
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bestfriends");
        Query q1 = db.orderByChild("number").startAt(number);
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    db.child(postSnapshot.getKey()).removeValue();
                if (delegate != null)
                    delegate.onResultReady("success");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Delete party from server
     */
    public static void server_deleteParty(String partyID, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        db.child(partyID).removeValue();
        if(delegate != null)
            delegate.onResultReady("success");
        Log.d("Delete Party", "Success");
    }

    /**
     * User unfollow user from server. Return either success or error.
     */
    public static void server_unfollow(String userID, final OnResultReadyListener<String> delegate) {/*
        String url = mainActivity.getString(R.string.server_url) + "users/" + getTokenFromLocal(mainActivity).get("id")
                + "/followings/" + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        String localUserId = getTokenFromLocal(mainActivity).get("id");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(localUserId).child("followings");
        db.child(userID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }

    /**
     * Delete notification. Return success or error.
     */
    public static void server_deleteNotification(String userID, String notificationID, final OnResultReadyListener<String> delegate) {
        /*RequestComponents comps[] = new RequestComponents[1];
        String url = mainActivity.getString(R.string.server_url) + "users/" + userID + "/notifications/"
                + notificationID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");
*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("notifications");
        db.child(notificationID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }

    /**
     * Uninvite user to event. Return success or error.
     */
    public static void server_uninviteUser(String userID, String eventID, final OnResultReadyListener<String> delegate) {
        /*String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");

        */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("invites");
        db.child(userID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }
}