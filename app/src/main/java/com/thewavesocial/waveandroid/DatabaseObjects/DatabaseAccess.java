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

import com.facebook.AccessToken;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public final class DatabaseAccess {
    public static Context sharedPreferencesContext;
    public static final String TAG = HomeSwipeActivity.TAG;
    public static final String PATH_TO_GEOFIRE = "geofire";

    /**
     * Initialize sharedPreferencesContext
     */
    public static void setContext(Context context) {
        sharedPreferencesContext = context.getApplicationContext();
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
//            Toast.makeText(sharedPreferencesContext, "Loading...", Toast.LENGTH_SHORT).show();
//            if (progressShowing)
//                return;
//            progress = new ProgressDialog(sharedPreferencesContext);
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
//                        UtilityClass.printAlertMessage(sharedPreferencesContext, "Sorry. Internet Connection Error.", "Network Error", true);
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
//            Toast.makeText(sharedPreferencesContext, "Loading...", Toast.LENGTH_SHORT).show();
//            if (progressShowing)
//                return;
//            progress = new ProgressDialog(sharedPreferencesContext);
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
    public static void saveTokentoLocal(String id) {
        SharedPreferences pref = sharedPreferencesContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.commit();
    }

    /**
     * Get login info from phone.
     */
    public static HashMap<String, String> getTokenFromLocal() {
        SharedPreferences pref = sharedPreferencesContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        HashMap<String, String> tokens = new HashMap<>();
        tokens.put("id", pref.getString("id", ""));
        //tokens.put("jwt", pref.getString("jwt", ""));
        return tokens;
    }

    public static void server_upload_image(Bitmap bitmap, final OnResultReadyListener<String> delegate) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getTokenFromLocal().get("id").toString());
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

    public static void server_SetCurrentUserByFacebookToken(final AccessToken token, final OnResultReadyListener<Boolean> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        //ensure the key is same in User class
        db.orderByChild("userID")
                .equalTo(token.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                            DatabaseAccess.saveTokentoLocal(userSnapshot.getKey());
                            CurrentUser.syncUser();
                            delegate.onResultReady(true);
                        } else {
                            delegate.onResultReady(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw new RuntimeException("Listener failed; check security rules.");
                    }
                });
    }


//todo --------------------------------------------------------------------------------POST Requests

    /**
     * generate key for Party, set partyID, then add Party to Firebase and Geofire
     * @param party Party to be stored; party.setPartyID() is called
     * @param delegate callback; is invoked immediately and does not wait on Firebase; beware of race condition
     * @return generated Firebase key
     */
    public static String server_createNewParty(Party party, final OnResultReadyListener<String> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        String partyID = db.push().getKey(); //unique ID for each event
        party.setPartyID(partyID);
        db.child(partyID).setValue(party);

        //generate corresponding Geofire entry
        GeoFire geoFire = new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE));
        geoFire.setLocation(partyID, new GeoLocation(party.getLat(), party.getLng()));

        if(delegate != null)
            delegate.onResultReady(partyID);
        return  partyID;
    }

    /**
     * generate key for User, set userID, then add User to Firebase
     * @param user User to be stored; user.setUserID() is called
     * @param delegate callback; is invoked immediately and does not wait on Firebase; beware of race condition
     * @return generated Firebase key
     */
    public static String server_createNewUser(User user, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        String userID = db.push().getKey(); //unique ID for each user
        user.setUserID(userID);
        db.child(userID).setValue(user);

        if(delegate != null)
            delegate.onResultReady(userID);
        return  userID;
    }


    public static void server_manageUserForParty(final String userID, final String eventID, final String relationship, final String action, final OnResultReadyListener<String> delegate) {
        final int change;
        switch(relationship){
            case "hosting":
                change = 8;
                break;
            case "bouncing":
                change = 130;
                break;
            case "invited":
                change = 132;
                break;
            case "attending":
                change = 64;
                break;
            case "going":
                change = 4;
                break;
            default:
                throw new RuntimeException("server_manageUserForParty: Invalid user_event relationship");
        }
        server_changeEventRelationship(userID, eventID, action, change, new OnResultReadyListener<String>() {
            @Override
            public void onResultReady(String result) {
                if(delegate != null)
                    delegate.onResultReady("success");
            }
        });
    }

    /**
     *
     * @param userID Firebase key for user
     * @param eventID Firebase key for event
     * @param action "POST" or "DELETE"
     * @param change the status change applied on top of existing bit array
     * @param delegate callback
     */
    private static void server_changeEventRelationship(final String userID, final String eventID, final String action, final int change, final OnResultReadyListener<String> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MutableData md_eu = mutableData.child("event_user").child(eventID).child(userID);
                MutableData md_ue = mutableData.child("user_event").child(userID).child(eventID);
                if(md_eu.getValue(Integer.class) == null && action.equals("POST")) {
                    md_eu.setValue(change);
                    md_ue.setValue(change);
                }
                else if (action.equals("POST")) {
                    md_eu.setValue(md_eu.getValue(Integer.class) + change + 128);
                    md_ue.setValue(md_ue.getValue(Integer.class) + change + 128);
                }
                else if (action.equals("DELETE")){
                    md_eu.setValue(md_eu.getValue(Integer.class) - change);
                    md_ue.setValue(md_ue.getValue(Integer.class) - change);
                }
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "server_changeEventRelationship:onComplete:" + error);
                if(!committed){
                    Log.w(TAG, "server_changeEventRelationship: Transaction complete but not committed", error.toException());
                }
                if(delegate != null)
                    delegate.onResultReady("success");
            }
        });
    }

    /**
     *
     * @param userID Firebase key for user
     * @param user User object as source of update
     * @param delegate callback
     */
    public static void server_updateUser(String userID, User user, final OnResultReadyListener<String> delegate) {
        FirebaseDatabase.getInstance().getReference("events")
                .child(userID)
                .setValue(user);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    /**
     *
     * @param partyID Firebase key for event
     * @param party Party object as source of update
     * @param delegate callback
     */
    public static void server_updateParty(String partyID, Party party, final OnResultReadyListener<String> delegate) {
        FirebaseDatabase.getInstance().getReference("events")
                .child(partyID)
                .setValue(party);
        new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE))
                .setLocation( partyID, new GeoLocation(party.getLat(), party.getLng()) );
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_followUser(String userID, String targetID, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("following").child(userID).child(targetID).setValue(true); //not so sure what value should appear as in followings list, so I just set it to the input targetID
        db.child("followers").child(targetID).child(userID).setValue(true);
        updateFollowersCount(userID, targetID, 1);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_addBestFriend(String name, String number, String userId, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bestfriends");
        BestFriend bestFriend = new BestFriend(name, number);
        db.setValue(bestFriend);
        //db.child("userID").setValue(userId);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_inviteUserToEvent(String userID, String eventID, final OnResultReadyListener<String> delegate) {
        server_manageUserForParty(userID, eventID, "invited", "POST", new OnResultReadyListener<String>() {
            @Override
            public void onResultReady(String result) {
                if(delegate != null)
                    delegate.onResultReady("success");
            }
        });
    }

    public static void server_createNotification(String receiverID, String senderID, String eventID, String type, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("notifications").child(receiverID);
        String key = db.push().getKey();
        int numType = notificationTypeGenerator(type);
        Notification notification = new Notification(senderID, numType, eventID);
        db.child(key).setValue(notification);
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

    /**
     * retrieve a single User object from Firebase
     * @param userID Firebase key referencing the User object under 'users'
     * @param delegate callback; invoked with User if successful, null if not
     */
    public static void server_getUserObject(final String userID, final OnResultReadyListener<User> delegate) {
        Log.d(HomeSwipeActivity.TAG, "DatabaseAccess.server_getUserObject");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(HomeSwipeActivity.TAG, "DatabaseAccess.server_getUserObject onDataChange");
                if(dataSnapshot.getValue() != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if(delegate != null)
                        delegate.onResultReady(user);
                } else {
                    delegate.onResultReady(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(HomeSwipeActivity.TAG, "server_getUserObject: ValueEventListener.onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * retrieve a single Party object from Firebase
     * @param partyID Firebase key referencing the Party object under 'events'
     * @param delegate callback; invoked with Party if successful, null if not
     */
    public static void server_getPartyObject(final String partyID, final OnResultReadyListener<Party> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(partyID);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(HomeSwipeActivity.TAG, "DatabaseAccess.server_getPartyObject onDataChange");
                if(dataSnapshot.getValue() != null){
                    Party party = dataSnapshot.getValue(Party.class);
                    if(delegate != null)
                        delegate.onResultReady(party);
                } else {
                    delegate.onResultReady(null);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(HomeSwipeActivity.TAG, "server_getPartyObject: ValueEventListener.onCancelled", databaseError.toException());
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
    public static void server_getEventsOfUser(final String userID, final OnResultReadyListener<HashMap<String, ArrayList<Party>>> delegate) {
        Log.d(TAG, "server_getEventsOfUser: entry:" + userID);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user_event").child(userID);
        final ArrayList<Party> invited = new ArrayList<>();
        final ArrayList<Party> going = new ArrayList<>();
        final ArrayList<Party> hosting = new ArrayList<>();
        final ArrayList<Party> bouncing = new ArrayList<>();
        final ArrayList<Party> attending = new ArrayList<>();
        final HashMap<String, ArrayList<Party>> parties = new HashMap();
        final HashMap<String, Integer> partyIDsAndR = new HashMap(); //partyIDsAndRelationships
        final ArrayList<String> partyIDs = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "server_getEventsOfUser: " + userID);
                Log.d(TAG, "server_getEventsOfUser: " + dataSnapshot.toString());
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    partyIDsAndR.put(postSnapshot.getKey(), postSnapshot.getValue(Integer.class));
                    partyIDs.add(postSnapshot.getKey());
                    //
                }
                server_getPartiesFromIDs(partyIDs, new OnResultReadyListener<ArrayList<Party>>() {
                    @Override
                    public void onResultReady(ArrayList<Party> result) {
                        for(Party party : result) {
                            int userRelationship = partyIDsAndR.get(party.getPartyID());
                            if (userRelationship >= 128) {
                                userRelationship -= 128;
                                invited.add(party);
                            }
                            if (userRelationship >= 64) {
                                userRelationship -= 64;
                                attending.add(party);
                            }
                            if (userRelationship >= 8) {
                                userRelationship -= 8;
                                hosting.add(party);
                            }
                            if (userRelationship >= 4) {
                                userRelationship -= 4;
                                going.add(party);
                            }
                            if (userRelationship >= 2)
                                bouncing.add(party);
                            parties.put("attending", attending);
                            parties.put("hosting", hosting);
                            parties.put("bouncing", bouncing);
                            parties.put("going", going);
                            parties.put("invited", invited);
                        }
                        if(delegate != null)
                            delegate.onResultReady(parties);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getPartiesFromIDs(final ArrayList<String> partyIDlist, final OnResultReadyListener<ArrayList<Party>> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        final ArrayList<Party> partyList = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(String eventID: partyIDlist){
                    Party party = dataSnapshot.child(eventID).getValue(Party.class);
                    party.setPartyID(eventID);
                    partyList.add(party);
                }
                if (delegate != null)
                    delegate.onResultReady(partyList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void server_getUsersOfEvent(final String eventID, final OnResultReadyListener<HashMap<String, ArrayList<User>>> delegate) {
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "events/" + eventID + "/users?access_token="
                + getTokenFromLocal(sharedPreferencesContext).get("jwt");*/
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("event_user").child(eventID);
        Log.i(TAG, "server_getUsersOfEvent: " + eventID);
        final ArrayList<User> attending = new ArrayList<>();
        final ArrayList<User> going = new ArrayList<>();
        final ArrayList<User> hosting = new ArrayList<>();
        final ArrayList<User> inviting = new ArrayList<>();
        final ArrayList<User> bouncing = new ArrayList<>();
        final HashMap<String, ArrayList<User>> users = new HashMap();
        final HashMap<String, Integer> userIDsAndR = new HashMap();
        final ArrayList<String> userIDs = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + postSnapshot.getKey());
                    userIDsAndR.put(postSnapshot.getKey(), postSnapshot.getValue(Integer.class));
                    userIDs.add(postSnapshot.getKey());
                }
                server_getUsersFromIDs(userIDs, new OnResultReadyListener<ArrayList<User>>() {
                    @Override
                    public void onResultReady(ArrayList<User> result) {
                        for(User user : result) {
                            int userRelationship = userIDsAndR.get(user.getUserID());
                            if (userRelationship >= 128) {
                                userRelationship -= 128;
                                inviting.add(user);
                            }
                            if (userRelationship >= 64) {
                                userRelationship -= 64;
                                attending.add(user);
                            }
                            if (userRelationship >= 8) {
                                userRelationship -= 8;
                                hosting.add(user);
                            }
                            if (userRelationship >= 4) {
                                userRelationship -= 4;
                                going.add(user);
                            }
                            if (userRelationship >= 2)
                                bouncing.add(user);
                            users.put("attending", attending);
                            users.put("hosting", hosting);
                            users.put("bouncing", bouncing);
                            users.put("going", going);
                            users.put("inviting", inviting);
                        }
                        if(delegate != null)
                            delegate.onResultReady(users);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getBestFriends(String userId, final OnResultReadyListener<List<BestFriend>> delegate) {
  /*      String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(sharedPreferencesContext).get("jwt");
        RequestComponents comp = new RequestComponents(url, "GET", null);*/

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(userId).child("bestfriends");
        final ArrayList<BestFriend> bestfriends = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                if(dataSnapshot.exists()) {
                    bestfriends.add(dataSnapshot.getValue(BestFriend.class));
                    if (delegate != null) {
                        delegate.onResultReady(bestfriends);
                    }
                }
                else
                    if (delegate != null) {
                        delegate.onResultReady(null);
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


    public static void server_getInvitesOfEvent(String eventID, final OnResultReadyListener<ArrayList<User>> delegate) {
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "events/" + eventID
                + "/invites?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");*/
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
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + userID
                + "/notifications?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");*/
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
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "events/find-by-keyword?keyword=" + keyword
                + "&start_after=1400000000&end_after=" + Calendar.getInstance().getTimeInMillis() / 1000 + "&access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");
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
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/find-by-keyword?keyword="
                + keyword + "&access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");
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
//        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + userID
//                + "/profile-photo?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");
//        RequestComponents comp = new RequestComponents(url, "GET", null);
//        new ImageDownloadTask(sharedPreferencesContext, comp, new OnResultReadyListener<Bitmap>() {
//            @Override
//            public void onResultReady(Bitmap result) {
//                if (result != null && delegate != null) {
//                    delegate.onResultReady(result);
//                    Log.d("Image_Download", "Success");
//                }
//            }
//        });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getTokenFromLocal().get("id"));

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                delegate.onResultReady(bitmap);
                Log.d("Download", "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Download", "Failed " + exception.getLocalizedMessage());
            }
        });
    }


//todo ------------------------------------------------------------------------------DELETE Requests

    /**
     * Delete Best Friend on server
     */
    public static void server_deleteBestFriend(String userId, String number, final OnResultReadyListener<String> delegate) {
/*        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + userId + "/bestfriends?access_token=" +
                getTokenFromLocal(sharedPreferencesContext).get("jwt");*/
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

        //attempt to achieve concurrent behavior using task API
        //should fire delegate(null) after event is deleted in both Firebase and Geofire.
        //should fire delegate(Exception e) after either one fails.

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
        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + getTokenFromLocal(sharedPreferencesContext).get("id")
                + "/followings/" + userID + "?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");*/
        String localUserId = getTokenFromLocal().get("id");
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
        String url = sharedPreferencesContext.getString(R.string.server_url) + "users/" + userID + "/notifications/"
                + notificationID + "?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");
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
        /*String url = sharedPreferencesContext.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getTokenFromLocal(sharedPreferencesContext).get("jwt");

        */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events").child(eventID).child("invites");
        db.child(userID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }
}