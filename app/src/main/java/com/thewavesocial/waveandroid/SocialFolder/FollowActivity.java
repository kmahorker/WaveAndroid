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
            CurrentUser.server_getUsersListObjects(CurrentUser.theUser.getFollowers(), new OnResultReadyListener<List<User>>() {
                @Override
                public void onResultReady(List<User> result) {
                    follows.addAll(result);
                    Log.d("Sizeeeeeeeeeeeeeeee", follows.size() + "" );
                    followUsersList.setAdapter(new SearchPeopleCustomAdapter(followActivity, follows));
                }
            });
        } else if (pageType == UserProfileFragment.PopupPage.FOLLOWING) {
            CurrentUser.server_getUsersListObjects(CurrentUser.theUser.getFollowing(), new OnResultReadyListener<List<User>>() {
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
/*
    private class FollowersAdapter extends ArrayAdapter<User> {

        public FollowersAdapter(Context mainActivity, int textViewResourceId, List<User> follows) {
            super(mainActivity, textViewResourceId, follows);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.follow_user_row, null);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilityClass.hideKeyboard(followActivity);
                    }
                });
            }
            final User user = getItem(position);
            if (user != null) {

                final ImageView userImage = (ImageView) v.findViewById(R.id.iv_follow_row_user_image);
                if (userImage != null) {
<<<<<<< HEAD
                    UtilityClass.getBitmapFromURL(followActivity, user.getProfilePic(), new OnResultReadyListener<Bitmap>() {
                        @Override
                        public void onResultReady(Bitmap result) {
                            Bitmap image = result;
                            if (image != null) {
                                userImage.setImageDrawable(UtilityClass.toRoundImage(getResources(), image));
                            }
                        }
                    });
=======
                    // TODO: 04/21/2017 Add image by url
//                    BitmapDrawable pic = user.getProfilePic();
//                    if (pic != null) {
//                        userImage.setImageDrawable(UtilityClass.toRoundImage(getResources(), pic.getBitmap()));
//                    }
>>>>>>> master
                }

                final TextView name = (TextView) v.findViewById(R.id.iv_follow_row_user_name);
                if (name != null) {
                    if (user.getFullName() == null) {
                        //TODO
                        name.setText("Unknown Name");
                    } else {
                        name.setText(user.getFullName());
                    }
                }

                final Button followed = (Button) v.findViewById(R.id.iv_follow_row_user_followed);
                if (followed != null) {
                    if (CurrentUser.theUser.getFollowing().contains(user.getUserID())) {
                        followed.setText(getResources().getString(R.string.follow_row_following));
                        followed.setTextColor(getResources().getColor(R.color.white_solid));
                        followed.setBackgroundResource(R.drawable.rounded_pink_button);
                    } else {
                        followed.setText(getResources().getString(R.string.follow_row_follow));
                        followed.setTextColor(getResources().getColor(R.color.pink_solid));
                        followed.setBackgroundResource(R.drawable.rounded_pink_button_stroke);
                    }

                }

            }
            return v;
        }
    }
    */
}
