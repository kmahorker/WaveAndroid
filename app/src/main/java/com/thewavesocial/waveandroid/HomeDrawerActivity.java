package com.thewavesocial.waveandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.NotificationCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.FindFriendsFolder.FriendsListFragment;
import com.thewavesocial.waveandroid.EventsFolder.MyEventsFragment;
import com.thewavesocial.waveandroid.HomeFolder.OptionsFragment;
import com.thewavesocial.waveandroid.HostControllerFolder.HostControllerFragment;
import com.thewavesocial.waveandroid.FindEventsFolder.MapsFragment;
import com.thewavesocial.waveandroid.UserFolder.UserProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private HomeDrawerActivity h = this;
    private Fragment mapFrag;
    private List<Notification> friendRequests, hostRequests, inviteRequests;
    private NotificationCustomAdapter friendAdapter, hostAdapter, inviteAdapter;
    private ListView friendRequestList, hostRequestList, inviteRequestList;
    private TextView drawerNotifCountText;
    private int notifCount;

    @Override
    //initialize everything
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_drawer_layout);
        CurrentUser.setContext(this);

        setupMapFragment();
        setupLeftDrawer();
        setupUserProfileOnClickEvents();
    }

    @Override
    //back pressed only when drawer is closed
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
            for ( Notification n : friendRequests )
            {
                Log.d("Check Requests", n.getMessage());
            }
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    //onclick events for drawer items
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if(id == R.id.map){
            fragment = mapFrag;
        }
        else if (id == R.id.find_events)
        {
            fragment = mapFrag;
        }
        else if (id == R.id.my_events)
        {
            fragment = new MyEventsFragment();
        }
        else if (id == R.id.friends)
        {
            fragment = new FriendsListFragment();
        }
        else if (id == R.id.host)
        {
            fragment = new HostControllerFragment();
        }
        else if (id == R.id.options)
        {
            fragment = new OptionsFragment();
        }
        else if (id == R.id.log_out)
        {
            fragment = new FriendsListFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_home_drawer, fragment).commit();

        return true;
    }

//-------------------------------------------------------------------------------OnCreate Sub-tasks

    //initialize drawer layout
    private void setupLeftDrawer()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                UtilityClass.hideKeyboard(h);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                UtilityClass.hideKeyboard(h);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    public void setupRightDrawer()
    {
        drawerNotifCountText = (TextView) findViewById(R.id.rightDrawer_text_notifCount);
        friendRequestList = (ListView) findViewById(R.id.rightDrawer_list_friendRequests);
        hostRequestList = (ListView) findViewById(R.id.rightDrawer_list_hostings);
        inviteRequestList = (ListView) findViewById(R.id.rightDrawer_list_invitations);

        friendRequests = new ArrayList<>();
        hostRequests = new ArrayList<>();
        inviteRequests = new ArrayList<>();
        //Queue<Notification> generalRequests = new LinkedList<>();

        for ( Notification notif : CurrentUser.theUser.getNotifications() )
        {
            if ( notif.getRequestType() == Notification.type1FriendRequests )
            {
                friendRequests.add( notif );
            }
            else if ( notif.getRequestType() == Notification.type2HostingRequests )
            {
                hostRequests.add( notif );
            }
            else if ( notif.getRequestType() == Notification.type3InviteRequests )
            {
                inviteRequests.add( notif );
            }
//            else if ( notif.getRequestType() == Notification.type0GeneralRequests )
//            {
//                generalRequests.add( notif );
//            }
        }
        inviteAdapter = new NotificationCustomAdapter( this, inviteRequests );
        friendAdapter = new NotificationCustomAdapter( this, friendRequests );
        hostAdapter = new NotificationCustomAdapter( this, hostRequests );

        friendRequestList.setAdapter( friendAdapter);
        hostRequestList.setAdapter( hostAdapter );
        inviteRequestList.setAdapter( inviteAdapter );
        notifCount = friendRequests.size() + hostRequests.size() + inviteRequests.size();
        ((MapsFragment)mapFrag).notifCountText.setText(notifCount + "");
        drawerNotifCountText.setText(notifCount + "");
    }

    //initialize map view
    private void setupMapFragment()
    {
        FragmentManager fragmentM = getSupportFragmentManager();
        mapFrag = new MapsFragment();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, mapFrag).commit();
    }

    //onclick events for user profile
    private void setupUserProfileOnClickEvents()
    {
        //get reference from navigation drawer layout
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        //onClick for username
        TextView homeUsername = (TextView) headerView.findViewById(R.id.home_user_name);
        homeUsername.setText( CurrentUser.theUser.getFullName() );
        homeUsername.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openUserProfile();
            }
        });

        //onClick for user profile pic
        ImageView homeUserProfile = (ImageView) headerView.findViewById(R.id.home_user_profile);
        homeUserProfile.setImageDrawable( CurrentUser.theUser.getProfilePic() );
        homeUserProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openUserProfile();
            }
        });
    }

    //open user profile fragment
    public void openUserProfile()
    {
        //close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        //open user fragment
        Fragment fragment = new UserProfileFragment();
        FragmentManager fragmentM = getSupportFragmentManager();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, fragment).commit();
    }

    public void removePosition(int position, int requestType)
    {
        if ( requestType == Notification.type1FriendRequests )
        {
            friendRequests.remove(position);
            friendAdapter = new NotificationCustomAdapter(this, friendRequests);
            friendRequestList.setAdapter( friendAdapter );
        }
        else if ( requestType == Notification.type2HostingRequests )
        {
            hostRequests.remove(position);
            hostAdapter = new NotificationCustomAdapter(this, hostRequests);
            hostRequestList.setAdapter( hostAdapter );
        }
        else if ( requestType == Notification.type3InviteRequests )
        {
            inviteRequests.remove(position);
            inviteAdapter = new NotificationCustomAdapter(this, inviteRequests);
            inviteRequestList.setAdapter( inviteAdapter );
        }
        ((MapsFragment)mapFrag).notifCountText.setText( --notifCount  + "" );
        drawerNotifCountText.setText( notifCount + "");
    }
}
