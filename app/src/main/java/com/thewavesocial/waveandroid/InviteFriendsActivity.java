package com.thewavesocial.waveandroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;

public class InviteFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);
        SearchView inviteFriendSearchBar = (SearchView) findViewById(R.id.inviteFriendSearchBar);
        inviteFriendSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO: Search for users from database
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Suggestions??
                return false;
            }
        });




    }
}
