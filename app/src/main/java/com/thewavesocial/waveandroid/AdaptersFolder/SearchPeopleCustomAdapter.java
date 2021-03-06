package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

import static android.content.ContentValues.TAG;

public class SearchPeopleCustomAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<User> userList, following;
    private static LayoutInflater inflater;

    public SearchPeopleCustomAdapter(Activity mainActivity, List<User> userList, List<User> following) {
        super();
        this.userList = userList;
        this.following = following;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_searchpeople_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        } else {
            holder = (Holder) layoutView.getTag();
        }

        final User user = userList.get(position);

        holder.image = (ImageView) layoutView.findViewById(R.id.eachSearchPeople_image);
        holder.name = (TextView) layoutView.findViewById(R.id.eachSearchPeople_name);
        holder.follow = (TextView) layoutView.findViewById(R.id.eachSearchPeople_follow);

        if (holder.image.getDrawable() == null) {
            DatabaseAccess.server_getProfilePicture(user.getId(), new OnResultReadyListener<Bitmap>() {
                @Override
                public void onResultReady(Bitmap result) {
                    if (result != null) {
                        holder.image.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), result));
                    }
                }
            });
        }

        holder.name.setText(user.getFull_name());

        Log.i(TAG, "containsID: UserID: " + user.getId());
        if (user.getId().equals(CurrentUser.getUser().getId())) {
            holder.follow.setVisibility(View.INVISIBLE);
        } else if (!containsID(following, user.getId())) {
            changeButton(holder.follow, "Follow", R.color.appColor, R.drawable.round_corner_red_edge);
        } else {
            changeButton(holder.follow, "Following", R.color.white_solid, R.drawable.round_corner_red);
        }
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.follow.getText().equals("Following")) {
                    //If delete from server is successful, then delete locally and change button.
                    DatabaseAccess.server_unfollow(user.getId(), new OnResultReadyListener<String>() {
                        @Override
                        public void onResultReady(String result) {
                            if (result.equals("success")) {
                                changeButton(holder.follow, "Follow", R.color.appColor, R.drawable.round_corner_red_edge);
                            }
                        }
                    });
                } else {
                    //If follow from server is successful, then follow locally and change button.
                    DatabaseAccess.server_followUser(CurrentUser.getUser().getId(), user.getId(), new OnResultReadyListener<String>() {
                        @Override
                        public void onResultReady(String result) {
                            if (result.equals("success")) {
                                changeButton(holder.follow, "Following", R.color.white_solid, R.drawable.round_corner_red);
                                DatabaseAccess.server_createNotification(CurrentUser.getUser().getId(), user.getId(), "", "following", null);
                                DatabaseAccess.server_createNotification(user.getId(), CurrentUser.getUser().getId(), "", "followed", null);
                            }
                        }
                    });
                }
            }
        });

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userObject", user);
                mainActivity.startActivity(intent);
                UtilityClass.hideKeyboard(mainActivity);
            }
        });

        return layoutView;
    }


    public class Holder {
        ImageView image;
        TextView name;
        TextView follow;
    }


    private void changeButton(TextView view, String text, int textColor, int backgroundColor) {
        view.setText(text);
        view.setTextColor(mainActivity.getResources().getColor(textColor));
        view.setBackgroundResource(backgroundColor);
    }

    private boolean containsID(List<User> following, String userID) {
        for ( User user : following ) {
            if ( user.getId().equals(userID) ) {
                return true;
            }
        }
        return false;
    }
}
