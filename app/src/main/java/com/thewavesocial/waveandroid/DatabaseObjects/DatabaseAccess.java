package com.thewavesocial.waveandroid.DatabaseObjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DatabaseAccess {
    private static final String PATH_TO_EVENTS = "events";
    private static final String PATH_TO_USERS = "users";
    private static final String PATH_TO_EVENT_USER = "event_user";
    private static final String PATH_TO_BESTFRIENDS = "bestfriends";
    private static final String PATH_TO_FOLLOWER_COUNT = "follower_count";
    private static final String PATH_TO_FOLLOWING = "following";
    private static final String PATH_TO_FOLLOWERS = "followers";
    private static final String PATH_TO_USER_EVENT = "user_event";
    private static final String PATH_TO_NOTIFICATIONS = "notifications";
    private static final String PATH_TO_GEOFIRE = "geofire";

    //ignore the warning; storing ApplicationContext is safe (//stackoverflow.com/questions/37709918/)
    private static Context sharedPreferencesContext;

    public static final String TAG = HomeSwipeActivity.TAG;

    //prevent instantiation
    private DatabaseAccess(){}

    /**
     * Initialize sharedPreferencesContext
     */
    public static void setContext(Context context) {
        sharedPreferencesContext = context.getApplicationContext();
    }

//todo -------------------------------------------------------------------------Local Save Functions

    /**
     * Save Firebase user ID for current user to persistent storage
     * @param id user ID for current user
     */
    public static void saveCurrentUserId(String id) {
        SharedPreferences pref = sharedPreferencesContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("id", id);
        editor.apply();
    }

    /**
     * Retrieve Firebase user ID for current user from persistent storage
     * @return user ID for current user
     */
    public static String getCurrentUserId() {
        SharedPreferences pref = sharedPreferencesContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return pref.getString("id", "");
    }

    public static void server_upload_image(Bitmap bitmap, final OnResultReadyListener<String> delegate) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getCurrentUserId());
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
        if(delegate != null) {
            delegate.onResultReady("success");
        }
    }

    public static void server_SetCurrentUserByFacebookToken(final AccessToken token, final OnResultReadyListener<Boolean> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS);
        //ensure the key is same in User class
        db.orderByChild("userID")
                .equalTo(token.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            DataSnapshot userSnapshot = dataSnapshot.getChildren().iterator().next();
                            DatabaseAccess.saveCurrentUserId(userSnapshot.getKey());
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(PATH_TO_USERS);
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
                MutableData md_eu = mutableData.child(PATH_TO_EVENT_USER).child(eventID).child(userID);
                MutableData md_ue = mutableData.child(PATH_TO_USER_EVENT).child(userID).child(eventID);
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
        FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS)
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
        FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS)
                .child(partyID)
                .setValue(party);
        new GeoFire(FirebaseDatabase.getInstance().getReference(PATH_TO_GEOFIRE))
                .setLocation( partyID, new GeoLocation(party.getLat(), party.getLng()) );
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_followUser(String userID, String targetID, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(PATH_TO_FOLLOWING).child(userID).child(targetID).setValue(true); //not so sure what value should appear as in followings list, so I just set it to the input targetID
        db.child("followers").child(targetID).child(userID).setValue(true);
        updateFollowersCount(userID, targetID, 1);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    public static void server_addBestFriend(String name, String number, String userId, final OnResultReadyListener<String> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userId).child(PATH_TO_BESTFRIENDS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_NOTIFICATIONS).child(receiverID);
        String key = db.push().getKey();
        int numType = notificationTypeGenerator(type);
        Notification notification = new Notification(senderID, numType, eventID);
        db.child(key).setValue(notification);
        if(delegate != null)
            delegate.onResultReady("success");
    }

    private static int notificationTypeGenerator(String type) {
        switch (type) {
            case PATH_TO_FOLLOWING:
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userID);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS).child(partyID);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_FOLLOWERS).child(userID);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_FOLLOWING).child(userID);
        final ArrayList<String> followingIDlist = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                   followingIDlist.add(postSnapshot.getKey());
                Log.i(TAG, "server_getUserFollowing: " + dataSnapshot.toString());
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USER_EVENT).child(userID);
        final ArrayList<Party> invited = new ArrayList<>();
        final ArrayList<Party> going = new ArrayList<>();
        final ArrayList<Party> hosting = new ArrayList<>();
        final ArrayList<Party> bouncing = new ArrayList<>();
        final ArrayList<Party> attending = new ArrayList<>();
        final HashMap<String, ArrayList<Party>> parties = new HashMap<>();
        final HashMap<String, Integer> partyIDsAndR = new HashMap<>(); //partyIDsAndRelationships
        final ArrayList<String> partyIDs = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    private static void server_getPartiesFromIDs(final ArrayList<String> partyIDlist, final OnResultReadyListener<ArrayList<Party>> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENT_USER).child(eventID);
        Log.i(TAG, "server_getUsersOfEvent: " + eventID);
        final ArrayList<User> attending = new ArrayList<>();
        final ArrayList<User> going = new ArrayList<>();
        final ArrayList<User> hosting = new ArrayList<>();
        final ArrayList<User> inviting = new ArrayList<>();
        final ArrayList<User> bouncing = new ArrayList<>();
        final HashMap<String, ArrayList<User>> users = new HashMap<>();
        final HashMap<String, Integer> userIDsAndR = new HashMap<>();
        final ArrayList<String> userIDs = new ArrayList<>();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.i(TAG, "onDataChange: " + postSnapshot.getKey());
                    userIDsAndR.put(postSnapshot.getKey(), postSnapshot.getValue(Integer.class));
                    userIDs.add(postSnapshot.getKey());
                }
                //Apparently data snapshot calls need a few seconds break (it'll return nothing if not)
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
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
                }, 5000);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void server_getBestFriends(String userId, final OnResultReadyListener<List<BestFriend>> delegate) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userId).child(PATH_TO_BESTFRIENDS);
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
                Log.d(TAG, "server_getEventsInDistance onKeyEntered:" + key);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS).child(key);
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
                        Log.e(TAG, "server_getEventsInDistance", databaseError.toException());
                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Log.d(TAG, "server_getEventsInDistance onKeyExited:" + key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, "server_getEventsInDistance onKeyMoved:" + key);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private static void server_getUsersFromIDs(final ArrayList<String> userIDlist, final OnResultReadyListener<ArrayList<User>> delegate){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userID).child("notifications");
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS);
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(getCurrentUserId());

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
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userId).child(PATH_TO_BESTFRIENDS);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS);
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
    public static void server_unfollow(String userID, final OnResultReadyListener<String> delegate) {
        String localUserId = getCurrentUserId();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_FOLLOWING).child(localUserId);
        db.child(userID).removeValue();
        db = FirebaseDatabase.getInstance().getReference(PATH_TO_FOLLOWERS).child(userID);
        db.child(localUserId).removeValue();
        updateFollowersCount(localUserId, userID, 0);
        if (delegate != null)
            delegate.onResultReady("success");
    }

    private static void updateFollowersCount(String userID, String targetID, final int mode){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userID).child("following_count");
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

        db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(targetID).child(PATH_TO_FOLLOWER_COUNT);
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
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_USERS).child(userID).child(PATH_TO_NOTIFICATIONS);
        db.child(notificationID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }

    /**
     * Uninvite user to event. Return success or error.
     */
    public static void server_uninviteUser(String userID, String eventID, final OnResultReadyListener<String> delegate) {
        /*String url = sharedPreferencesContext.getString(R.string.server_url) + "events/" + eventID + "/invites/"
                + userID + "?access_token=" + getCurrentUserId(sharedPreferencesContext).get("jwt");

        */
        DatabaseReference db = FirebaseDatabase.getInstance().getReference(PATH_TO_EVENTS).child(eventID).child("invites");
        db.child(userID).removeValue();
        if (delegate != null)
            delegate.onResultReady("success");
    }
}