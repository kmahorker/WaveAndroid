package com.thewavesocial.waveandroid.SocialFolder;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.AdaptersFolder.SearchPeopleCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Ballin' Android stuff by haydenchristensen on 3/12/17.
 */


public class FollowActivity extends AppCompatActivity {
    public static final String TAG = FollowActivity.class.getName();
    public static final String FOLLOW_POPUP_TYPE_ARG = "FOLLOW_POPUP_TYPE_ARG";
    private Activity followActivity = this;
    private UserProfileFragment.PopupPage pageType;
    private TextView title;
    private SearchView searchField;
    private ListView followUsersList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_follow);

        pageType = UserProfileFragment.PopupPage
                .valueOf(getIntent().getExtras().getString(FOLLOW_POPUP_TYPE_ARG));

        setupFollowActionbar();
        createViews();
    }

    public void createViews() {
        followUsersList = (ListView) findViewById(R.id.lv_follow_follows_list);
        final List<User> follows = new ArrayList<>();

        if (pageType == UserProfileFragment.PopupPage.FOLLOWERS) {
            server_getUsersListObjects(CurrentUser.theUser.getFollowers(), new OnResultReadyListener<List<User>>() {
                @Override
                public void onResultReady(List<User> result) {
                    follows.addAll(result);
                    Log.d("Sizeeeeeeeeeeeeeeee", follows.size() + "");
                    followUsersList.setAdapter(new SearchPeopleCustomAdapter(followActivity, follows));
                }
            });
        } else if (pageType == UserProfileFragment.PopupPage.FOLLOWING) {
            server_getUsersListObjects(CurrentUser.theUser.getFollowing(), new OnResultReadyListener<List<User>>() {
                @Override
                public void onResultReady(List<User> result) {
                    follows.addAll(result);
                    followUsersList.setAdapter(new SearchPeopleCustomAdapter(followActivity, follows));
                }
            });
        }

        final List<User> finalFollows = follows;
        searchField = (SearchView) findViewById(R.id.sf_follow_search_field);
        searchField.setQueryHint(getString(R.string.search));
        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                followUsersList.setAdapter(new SearchPeopleCustomAdapter(followActivity, UtilityClass.search(finalFollows, query)));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                followUsersList.setAdapter(new SearchPeopleCustomAdapter(followActivity, UtilityClass.search(finalFollows, query)));
                return true;
            }
        });
    }

    public void setupFollowActionbar() {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_follow);

        ImageView back = (ImageView) findViewById(R.id.ib_follow_go_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = (TextView) findViewById(R.id.tv_follow_title);
        if (pageType == UserProfileFragment.PopupPage.FOLLOWERS) {
            title.setText(getString(R.string.followers));
        } else if (pageType == UserProfileFragment.PopupPage.FOLLOWING) {
            title.setText(getString(R.string.following));
        }
    }
}
