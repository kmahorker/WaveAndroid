package com.thewavesocial.waveandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;

public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private HomeDrawerActivity h = this;
    private Fragment mapFrag;

    @Override
    //initialize everything
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_drawer_layout);
        CurrentUser.setContext(this);

        setupDrawer();
        getSupportActionBar().setTitle("PLUG");
        setupMapFragment();
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
            fragment = new FriendsListFragment();
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
    private void setupDrawer()
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
}
