//package com.thewavesocial.waveandroid;
//
//import android.content.Intent;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBar;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.thewavesocial.waveandroid.BusinessObjects.User;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class FriendsListActivity extends AppCompatActivity {
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //super.onCreateDrawer(savedInstanceState);
//        setContentView(R.layout.activity_friends_list);
//
//        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        this.getSupportActionBar().setCustomView(R.layout.actionbar_friends);
//
//        ListView friendsList = (ListView) findViewById(R.id.friendsList);
//        List<User> users = generateTestUserList(); //TODO testing
//        //friendsList.setAdapter(new CustomAdapter(this, users));
//
//    }
//
//    public void showInviteFriendsActivity(View view){
//        Intent f_intent = new Intent(this, InviteFriendsActivity.class);
//        startActivity(f_intent);
//    }
//
//    public void showFriendProfileActivity(View view, User clickedUser){
//        Intent intent = new Intent(this, FriendProfileActivity.class);
//        intent.putExtra("userObj", clickedUser);
//        startActivity(intent);
//    }
//
//    public List<User> generateTestUserList(){
//        List<User> userList = new ArrayList<User>();
//        //User a = new User("Bob", "Jones", "bj@k.com", "password", "UCSB", "Male", new Date(51215), userList, userList, userList);
//        User a = new User();
//        User b = new User();
//        User c = new User();
//        User d = new User();
//        User e = new User();
//
//        a.setFirstName("Bob");
//        a.setLastName("Jones");
//        b.setFirstName("John");
//        b.setLastName("Smith");
//        c.setFirstName("Dumb");
//        c.setLastName("Dude");
//        d.setFirstName("Turn");
//        d.setLastName("Up");
//        e.setFirstName("Kau");
//        e.setLastName("Mah");
//
//        userList.add(a);
//        userList.add(b);
//        userList.add(c);
//        userList.add(d);
//        userList.add(e);
//
//        return userList;
//
//    }
//}
