package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.FindFriendsFolder.FriendsListFragment;
import com.thewavesocial.waveandroid.FindFriendsFolder.InviteFriendsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kaushik on 2/4/2017.
 */

public class AddFriendCustomAdapter extends BaseAdapter {
    String [] result;
    InviteFriendsActivity context;
    int [] imageId;
    List<User> userList = new ArrayList<User>();;
    FriendsListFragment fragment;
    private static LayoutInflater inflater=null;

    public AddFriendCustomAdapter(InviteFriendsActivity mainActivity, List<User> userList) {
        super();
        this.userList.addAll(userList);
        context = mainActivity;
        this.fragment = fragment;
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
        ImageView btn;

    }


    public void updateUserList(List<User> user){
        userList.clear();
        userList.addAll(user);
        this.notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            final Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.add_friend_cell_layout, null);
            holder.tv = (TextView) rowView.findViewById(R.id.addFriendName);
            holder.img = (ImageView) rowView.findViewById(R.id.addFriendImage);
            holder.btn = (ImageView) rowView.findViewById(R.id.addFriendButton);
            //holder.tv.setText("Name"); //Testing
            holder.tv.setText(userList.get(position).getFullName());
            //holder.img.setImageResource(R.drawable.happy_house); //testing //TODO Change to user's image
            holder.img.setImageDrawable(UtilityClass.convertRoundImage(context.getResources(),
                    userList.get(position).getProfilePic().getBitmap())); //TODO Double check this imp
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Not sure if this is right imp
                    context.showFriendProfileActivity(v,userList.get(position));
                }
            });
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn.setImageResource(R.drawable.checkmark);
                    //TODO: SEND Notification to recepient user
                }
            });
            return rowView;
        }
        return convertView;

    }
}
