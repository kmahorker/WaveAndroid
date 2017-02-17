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

import java.util.ArrayList;
import java.util.List;

public class StatsFriendCustomAdapter extends BaseAdapter
{
    InviteFriendsActivity context;
    List<User> userList = new ArrayList<>();
    private static LayoutInflater inflater;

    public StatsFriendCustomAdapter(InviteFriendsActivity mainActivity, List<User> userList)
    {
        super();
        this.userList = userList;
        context = mainActivity;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        return userList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public class Holder
    {
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            final Holder holder = new Holder();
            View rowView = inflater.inflate(R.layout.add_friend_cell_layout, null);
            holder.img = (ImageView) rowView.findViewById(R.id.addFriendImage);
            holder.img.setImageDrawable(userList.get(position).getProfilePic());
            rowView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    context.showFriendProfileActivity(v,userList.get(position));
                }
            });
            return rowView;
        }
        return convertView;
    }
}
