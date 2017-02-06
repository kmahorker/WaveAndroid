package com.thewavesocial.waveandroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
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

public class HomeDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateActionBar();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        //initialize map view
        FragmentManager fragmentM = getSupportFragmentManager();
        final Fragment frag = new MapsFragment();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, frag).commit();


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

    @Override
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                //Do stuff
                return true;
            case R.id.notif_button:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
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

    private void updateActionBar()
    {
        getSupportActionBar().setTitle("WAVE");
    }

    public void openUserProfile()
    {
        Fragment fragment = new UserProfileFragment();
        FragmentManager fragmentM = getSupportFragmentManager();
        fragmentM.beginTransaction().replace(R.id.content_home_drawer, fragment).commit();
    }

}
