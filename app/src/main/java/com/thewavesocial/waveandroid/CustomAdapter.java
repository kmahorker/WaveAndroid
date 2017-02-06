package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushik on 2/4/2017.
 */

public class CustomAdapter extends BaseAdapter {
    String [] result;
    FragmentActivity context;
    int [] imageId;
    List<User> userList = new ArrayList<User>();;
    FriendsListFragment fragment;
    private static LayoutInflater inflater=null;

    public CustomAdapter(FragmentActivity mainActivity, FriendsListFragment fragment, List<User> userList) {
        super();
        this.userList.addAll(userList);
        //this.userList.addAll(userList);
        //result = friendsNamesList;
        context = mainActivity;
        this.fragment = fragment;
        //imageId = friendsImagesList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    /*public void setUserList(List<User> user){
        this.userList = user;
    }*/

    public void updateUserList(List<User> user){
        userList.clear();
        userList.addAll(user);
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.friends_list_cell_layout, null);
            holder.tv = (TextView) rowView.findViewById(R.id.friendName);
            holder.img = (ImageView) rowView.findViewById(R.id.friendImage);
            //holder.tv.setText("Name"); //Testing
            holder.tv.setText(userList.get(position).getFullName());
            //holder.img.setImageResource(R.drawable.happy_house); //testing //TODO Change to user's image
            holder.img.setImageDrawable(userList.get(position).getProfilePic()); //TODO Double check this imp
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Not sure if this is right imp
                   // FriendsListActivity f = new FriendsListActivity();
                    fragment.showFriendProfileActivity(v, userList.get(position));
                   //Intent in = new Intent(FriendsListActivity, FriendProfileActivity.class)
                }
            });
            return rowView;
        }
        return convertView;

    }
}
