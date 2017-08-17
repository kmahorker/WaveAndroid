package com.thewavesocial.waveandroid.DatabaseObjects;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thewavesocial.waveandroid.BusinessObjects.BestFriend;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public final class DatabaseAccess {
    public static Activity mainActivity;
    public static final String PATH_TO_GEOFIRE = "geofire";

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
        //tokens.put("jwt", pref.getString("jwt", ""));
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

    public static void server_createNewParty(String address,
                                             long date,
                                             long duration,
                                             String emoji,
                                             String host_id,
                                             String host_name,
                                             boolean is_public,
                                             double lat,
                                             double lng,
                                             int max_age,
                                             int min_age,
                                             String name,
                                             double price,
                                             final OnResultReadyListener<String> delegate){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        String eventID = UUID.randomUUID().toString(); //unique ID for each event

        Party party = new Party(address, date, duration, emoji, host_id, host_name, is_public, lat, lng, max_age, min_age, name, eventID, price);
        db.child(eventID).setValue(party);

        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE));
        geoFire.setLocation(eventID, new GeoLocation(lat, lng));

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
                                            String password,
                                            String fb_id,
                                            String fb_token,
                                            String gender,
                                            String birthday,
                                            final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        String userID = UUID.randomUUID().toString(); //unique ID for each event
        List<BestFriend> list = new ArrayList<>();
        User user = new User(userID, first_name, last_name, gender, list);
        db.child(userID).setValue(user);
        if(delegate != null)
            delegate.onResultReady(userID);

    }


    public static void server_manageUserForParty(String userID, String eventID, String relationship, String action, final OnResultReadyListener<String> delegate) {
        //RequestComponents[] comps = new RequestComponents[1];
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        //HashMap<String, String> about = new HashMap();
        //about.put("relationship", relationship);
        if (action.equals("POST")) {
            switch(relationship) {
                case "hosting":
                    db.child("hosting").child(userID).child(eventID).setValue(true);
                    db.child("event_hosts").child(eventID).child(userID).setValue(true);
                    break;
                case "bouncing":
                    db.child("bouncing").child(userID).child(eventID).setValue(true);
                    db.child("event_bouncing").child(eventID).child(userID).setValue(true);
                    break;
                case "invited":
                    db.child("invited").child(userID).child(eventID).setValue(true);
                    db.child("event_invited").child(eventID).child(userID).setValue(true);
                    break;
                case "attending":
                    db.child("attending").child(userID).child(eventID).setValue(true);
                    db.child("event_attending").child(eventID).child(userID).setValue(true);
                    break;
            }
        }
        else if (action.equals("DELETE")){
            switch(relationship) {
                case "hosting":
                    db.child("hosting").child(userID).child(eventID).removeValue();
                    db.child("event_hosts").child(eventID).child(userID).removeValue();
                case "bouncing":
                    db.child("bouncing").child(userID).child(eventID).removeValue();
                    db.child("event_bouncing").child(eventID).child(userID).removeValue();
                    break;
                case "invited":
                    db.child("invited").child(userID).child(eventID).removeValue();
                    db.child("event_invited").child(eventID).child(userID).removeValue();
                    break;
                case "attending":
                    db.child("attending").child(userID).child(eventID).removeValue();
                    db.child("event_attending").child(eventID).child(userID).removeValue();
                    break;
            }
        }
/*        else
            Log.d("Action", "Illegal passed action argument: " + action);*/
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("following").child(userID).child(targetID).setValue(true); //not so sure what value should appear as in followings list, so I just set it to the input targetID
        db.child("followers").child(targetID).child(userID).setValue(true);
        updateFollowersCount(userID, targetID, 1);
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
        //db.child("userID").setValue(userId);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_inviteUserToEvent(String userID, String eventID, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("event_invited").child(userID).setValue(true); //just setting the value of a user invited to the party as the userID, can change this as well
        db.child("invited").child(userID).setValue(true);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_createNotification(String receiverID, String senderID, String eventID, String type, final OnResultReadyListener<String> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "users/" + receiverID
                + "/notifications?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
/*        HashMap<String, String> body = new HashMap<>();*/
        //DatabaseReference db = FirebaseDatabase.getInstance().getReference("notifications").child(receiverID).child(type);
        //String uuid = UUID.randomUUID().toString(); //unique ID for each event
        int numType = notificationTypeGenerator(type);
        Notification notification = new Notification(senderID, numType, eventID);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static int notificationTypeGenerator(String type) {
        switch (type) {
            case "following":
                return 1;
            case "followed":
                return 2;
            case "hosting":
                return 3;
            case "going":
                return 4;
            case "bouncing":
                return 5;
            default:
                return 0;
        }
    }

    public static void server_getUserObject(final String userID, final OnResultReadyListener<User> delegate) {
        Log.d(HomeSwipeActivity.TAG, "DatabaseAccess.server_getUserObject");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(HomeSwipeActivity.TAG, "ValueEventListener.onDataChange");
                if(dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if(delegate != null)
                        delegate.onResultReady(user);
                }
                else
                    delegate.onResultReady(null);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(HomeSwipeActivity.TAG, "ValueEventListener.onCancelled");
            }
        });
        Log.i(HomeSwipeActivity.TAG, "DatabaseAccess.server_getUserObject DONE");
    }

    public static void server_getPartyObject(final String partyID, final OnResultReadyListener<Party> delegate) {
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("followers").child(userID);
        Log.i(TAG, "server_getUserFollowers: " + db);
        final ArrayList<String> followerIDlist = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    followerIDlist.add(postSnapshot.getKey());
                server_getUsersFromIDs(followerIDlist, new OnResultReadyListener<ArrayList<User>>() {
                    @Override
                    public void onResultReady(ArrayList<User> result) {
                        if(delegate != null)
                            delegate.onResultReady(result);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getUserFollowing(final String userID, final OnResultReadyListener<List<User>> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("following").child(userID);
        final ArrayList<String> followingIDlist = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                   followingIDlist.add(postSnapshot.getKey());
                Log.i(TAG, "onDataChange: FollowingIDList for user " + userID + ": " + followingIDlist);
                server_getUsersFromIDs(followingIDlist, new OnResultReadyListener<ArrayList<User>>() {
                    @Override
                    public void onResultReady(ArrayList<User> result) {
                        if(delegate != null)
                            delegate.onResultReady(result);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static void server_getEventsOfUser(String userID, final OnResultReadyListener<HashMap<String, ArrayList<Party>>> delegate) {
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
                    Party each_party = postSnapshot.getValue(Party.class);
                    each_party.setPartyID(postSnapshot.getKey());
                    if(postSnapshot.child("relationship").getValue().toString() == "attending")
                        attending.add(each_party);
                    else if(postSnapshot.child("relationship").getValue().toString() == "going")
                        going.add(each_party);
                    else if(postSnapshot.child("relationship").getValue().toString() == "hosting")
                        hosting.add(each_party);
                    else if(postSnapshot.child("relationship").getValue().toString() == "bouncing")
                        bouncing.add(each_party);
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
                //for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    bestfriends.add(dataSnapshot.getValue(BestFriend.class));
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

    public static void server_getEventsInDistance(LatLng center, double radius, final OnResultReadyListener<Party> onKeyEnteredDelegate) {
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(center.latitude, center.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(HomeSwipeActivity.TAG, "onKeyEntered key:" + key);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("events").child(key);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Party party = dataSnapshot.getValue(Party.class);
                            party.setPartyID(dataSnapshot.getKey());
                            onKeyEnteredDelegate.onResultReady(party);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(HomeSwipeActivity.TAG, "server_getEventsInDistance", databaseError.toException());
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


//   public static void server_getEventsInDistance(final String minLat, final String maxLat, final String minLng, final String maxLng, final OnResultReadyListener<ArrayList<Party>> delegate) {
//       DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
//       final ArrayList<Party> parties = new ArrayList<>();
//       db.addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(DataSnapshot dataSnapshot) {
//               for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
//                   float lat = Float.parseFloat(postSnapshot.child("lat").getValue().toString());
//                   float lng = Float.parseFloat(postSnapshot.child("lng").getValue().toString());
//                   if(      lat > Float.parseFloat(minLat) && lng < Float.parseFloat(maxLat) &&
//                            lng > Float.parseFloat(minLng) && lng < Float.parseFloat(maxLng)) {
//                       Party each_party = postSnapshot.getValue(Party.class);
//                       each_party.setPartyID(postSnapshot.getKey());
//                       parties.add(each_party);
//                   }
//               }
//               if(delegate != null)
//                   delegate.onResultReady(parties);
//           }
//
//           @Override
//           public void onCancelled(DatabaseError databaseError) {
//
//           }
//       });
//
//            /*String url = mainActivity.getString(R.string.server_url) + "events/find-by-coordinate?min_lat=" + minLat
//                    + "&max_lat=" + maxLat + "&min_lng=" + minLng + "&max_lng=" + maxLng
//                    + "&start_after=" + 1400000000 + "&end_after" + Calendar.getInstance().getTimeInMillis() / 1000
//                    + "&access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
//            /*RequestComponents comp = new RequestComponents(url, "GET", null);
//            new HttpRequestTask(mainActivity, new RequestComponents[]{comp}, new OnResultReadyListener<ArrayList<String>>() {
//                @Override
//                public void onResultReady(ArrayList<String> result) {
//                    ArrayList<Party> parties = new ArrayList<>();
//                    try {
//                        JSONObject json_result = new JSONObject(result.get(0));
//                        JSONArray data = json_result.getJSONArray("data");
//                        for (int i = 0; i < data.length(); i++) {
//                            HashMap<String, String> body = extractJSONData(data.getJSONObject(i));
//                            parties.add(constructParty(body));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("Get Events In Distance", result.get(0));
//                    if (delegate != null)
//                        delegate.onResultReady(parties);
//                }
//        }).execute();*/
//
//    }


    public static void server_getInvitesOfEvent(String eventID, final OnResultReadyListener<ArrayList<User>> delegate) {
/*        String url = mainActivity.getString(R.string.server_url) + "events/" + eventID
                + "/invites?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("event_invited").child(eventID);
        final ArrayList<String> userIDs = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    userIDs.add(postSnapshot.getKey());
                //Log.d("Get Invites of Event", result.get(0));
                server_getUsersFromIDs(userIDs, new OnResultReadyListener<ArrayList<User>>() {
                    @Override
                    public void onResultReady(ArrayList<User> result) {
                        if (delegate != null)
                            delegate.onResultReady(result);
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getUsersFromIDs(final ArrayList<String> userIDlist, final OnResultReadyListener<ArrayList<User>> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        final ArrayList<User> userList = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String userID: userIDlist){
                    User user = dataSnapshot.child(userID).getValue(User.class);
                    user.setUserID(userID);
                    userList.add(user);
                }
                if (delegate != null)
                    delegate.onResultReady(userList);
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
        Query q1 = db.orderByChild("name").startAt(keyword).endAt(keyword + " zzzz");
        final ArrayList<Party> parties = new ArrayList<>();
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Party each_party = postSnapshot.getValue(Party.class);
                    each_party.setPartyID(postSnapshot.getKey());
                    parties.add(each_party);
                }
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
        Query q1 = db.orderByChild("first_name").startAt(keyword).endAt(keyword + "zzzz");
        final ArrayList<User> users = new ArrayList<>();
        q1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    //Log.i(TAG, "onDataChange: Found key: " + postSnapshot.getKey());
                    user.setUserID(postSnapshot.getKey());
                    users.add(user);
                    //Log.i(TAG, "onDataChange: Found user: "+ postSnapshot.getValue(User.class).getFull_name());
                }
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
    public static void server_deleteParty(final String partyID, final OnResultReadyListener<Exception> delegate) {
        final GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE));

        final TaskCompletionSource<String> tcs1 = new TaskCompletionSource<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        db.child(partyID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    tcs1.setException(databaseError.toException());
                } else {
                    tcs1.setResult(partyID);
                }
            }
        });

        final TaskCompletionSource<String> tcs2 = new TaskCompletionSource<>();
        geoFire.removeLocation(partyID, new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if(error != null){
                    tcs2.setException(error.toException());
                } else {
                    tcs2.setResult(partyID);
                }
            }
        });

        if(delegate != null){
            Tasks.whenAll(tcs1.getTask(), tcs2.getTask()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    delegate.onResultReady(null);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    delegate.onResultReady(e);
                }
            });
        }
    }

    /**
     * User unfollow user from server. Return either success or error.
     */
    public static void server_unfollow(String userID, final OnResultReadyListener<String> delegate) {/*
        String url = mainActivity.getString(R.string.server_url) + "users/" + getTokenFromLocal(mainActivity).get("id")
                + "/followings/" + userID + "?access_token=" + getTokenFromLocal(mainActivity).get("jwt");*/
        String localUserId = getTokenFromLocal(mainActivity).get("id");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("following").child(localUserId);
        db.child(userID).removeValue();
        db = FirebaseDatabase.getInstance().getReference("followers").child(userID);
        db.child(localUserId).removeValue();
        updateFollowersCount(localUserId, userID, 0);
        if (delegate != null)
            delegate.onResultReady("success");
    }

    public static void updateFollowersCount(String userID, String targetID, final int mode){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID).child("following_count");
        db.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null)
                    mutableData.setValue(1);
                else {
                    int following_count = mutableData.getValue(Integer.class);
                    if (mode == 0) { //mode 0 = unfollow
                        if (following_count > 0)
                            following_count -= 1;
                    } else //mode 1 = follow
                        following_count += 1;
                    mutableData.setValue(following_count);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

        db = FirebaseDatabase.getInstance().getReference("users").child(targetID).child("follower_count");
        db.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null)
                    mutableData.setValue(1);
                else {
                    int follower_count = mutableData.getValue(Integer.class);
                    if (mode == 0) { //mode 0 = unfollow
                        if (follower_count > 0)
                            follower_count -= 1;
                    } else //mode 1 = follow
                        follower_count += 1;
                    mutableData.setValue(follower_count);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
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