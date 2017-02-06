package com.thewavesocial.waveandroid;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import static com.thewavesocial.waveandroid.R.id.holo_dark;
import static com.thewavesocial.waveandroid.R.id.searchView;

public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private HomeDrawerActivity h = this;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_drawer_layout);
        initializeDrawer();
        updateActionBar();
        initializeGoogleMapFragment();
        setUserProfileOnclickEvents();
    }

    @Override
    //back pressed only when drawer is closed
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    //notification button clicked (need to be changed to another style)
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.notif_button:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    //onclick events for drawer items
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.find_events)
        {
            fragment = new FriendsListFragment();
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
            fragment = new FriendsListFragment();
        }
        else if (id == R.id.options)
        {
            fragment = new FriendsListFragment();
        }
        else if (id == R.id.log_out)
        {
            fragment = new FriendsListFragment();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_home_drawer, fragment).commit(); //Not sure if this is right

        return true;
    }

//------------------------------------------------------------------------------ OnCreate Sub-tasks

    //initialize drawer layout
    private void initializeDrawer()
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

    //update actionbar
    private void updateActionBar()
    {
        getSupportActionBar().setTitle("WAVE");
    }

    //initialize map view
    private void initializeGoogleMapFragment()
    {
        FragmentManager fragmentM = getSupportFragmentManager();
        final Fragment frag = new MapsFragment();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, frag).commit();
    }

    //onclick events for user profile
    private void setUserProfileOnclickEvents()
    {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView homeUsername = (TextView) headerView.findViewById(R.id.home_user_name);
        homeUsername.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openUserProfile();
            }
        });

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        Fragment fragment = new UserProfileFragment();
        FragmentManager fragmentM = getSupportFragmentManager();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, fragment).commit();
    }
}
