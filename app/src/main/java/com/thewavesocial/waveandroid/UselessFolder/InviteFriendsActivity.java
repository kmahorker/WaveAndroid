//package com.thewavesocial.waveandroid.SocialFolder;
//
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ListView;
//import android.widget.SearchView;
//
//import com.thewavesocial.waveandroid.AdaptersFolder.AddFriendCustomAdapter;
//import com.thewavesocial.waveandroid.BusinessObjects.DummyUser;
//import com.thewavesocial.waveandroid.BusinessObjects.User;
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.UtilityClass;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class InviteFriendsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.list_friends_invite);
//        updateActionbar();
//        final DummyUser us = new DummyUser(this);
//        final InviteFriendsActivity currentActivity = this;
//        final SearchView inviteFriendSearchBar = (SearchView) findViewById(R.id.inviteFriendSearchBar);
//        final ListView addFriendsResultListView = (ListView) findViewById(R.id.resultListView);
//
//
//        inviteFriendSearchBar.setQueryHint("Search Name");
//        inviteFriendSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                List<User> userList = new ArrayList<User>();
//                userList.add(us);
//                addFriendsResultListView.setAdapter(new AddFriendCustomAdapter(currentActivity, userList));
//                inviteFriendSearchBar.clearFocus();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //Suggestions??
//                return false;
//            }
//        });
//
//        addFriendsResultListView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent ev) {
//                inviteFriendSearchBar.clearFocus();
//                return false;
//            }
//        });
//
//
//
//    }
//
//    public void showFriendProfileActivity(View view, User clickedUser){
//        Intent in = new Intent(this, FriendProfileActivity.class);
//        in.putExtra("userIdLong", clickedUser.getUserID());
//        startActivity(in);
//
//    }
//
//    public void updateActionbar(){
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Add Friends");
//        //getSupportActionBar().setDisplayShowCustomEnabled(true);
//        //getSupportActionBar().setCustomView();
//    }
//
//    @Override
//
//    public boolean onOptionsItemSelected(MenuItem item){
//        if(item.getItemId() == android.R.id.home){
//            onBackPressed();
//            UtilityClass.hideKeyboard(this);
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//}
