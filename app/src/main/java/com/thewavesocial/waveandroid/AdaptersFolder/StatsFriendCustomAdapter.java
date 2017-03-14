package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

public class StatsFriendCustomAdapter extends BaseAdapter
{
    private EventStatsActivity mainActivity;
    private List<User> userList = new ArrayList<>();
    private static LayoutInflater inflater;

    public StatsFriendCustomAdapter(EventStatsActivity mainActivity, List<User> userList)
    {
        super();
        this.userList = userList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            View rowView = inflater.inflate(R.layout.each_statsfriend_item, null);
            holder.img = (ImageView) rowView.findViewById(R.id.each_statsfriend_image);
            holder.img.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                    userList.get(position).getProfilePic().getBitmap()));
            rowView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                    intent.putExtra("userIDLong", ((User)getItem(position)).getUserID());
                    mainActivity.startActivity(intent);
                }
            });
            return rowView;
        }
        return convertView;
    }
}
