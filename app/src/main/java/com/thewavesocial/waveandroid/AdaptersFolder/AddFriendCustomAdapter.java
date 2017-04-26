package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.SocialFolder.FriendsListFragment;
import com.thewavesocial.waveandroid.SocialFolder.InviteFriendsActivity;
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
            rowView = inflater.inflate(R.layout.each_add_friend_item, null);
            holder.tv = (TextView) rowView.findViewById(R.id.addFriendName);
            holder.img = (ImageView) rowView.findViewById(R.id.addFriendImage);
            holder.btn = (ImageView) rowView.findViewById(R.id.addFriendButton);
            //holder.tv.setText("Name"); //Testing
            holder.tv.setText(userList.get(position).getFullName());

            UtilityClass.getBitmapFromURL(context, userList.get(position).getProfilePic(), new OnResultReadyListener<Bitmap>() {
                @Override
                public void onResultReady(Bitmap image) {
                    if (image != null)
                        holder.img.setImageDrawable( UtilityClass.toRoundImage(context.getResources(), image));
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.showFriendProfileActivity(v,userList.get(position));
                }
            });
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btn.setImageResource(R.drawable.checkmark);
                }
            });
            return rowView;
        }
        return convertView;

    }
}
