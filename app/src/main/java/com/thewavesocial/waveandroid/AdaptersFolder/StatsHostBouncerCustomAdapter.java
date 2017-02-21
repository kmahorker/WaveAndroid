package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.FindFriendsFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.HostControllerFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class StatsHostBouncerCustomAdapter extends RecyclerView.Adapter<StatsHostBouncerCustomAdapter.ViewHolder>
{
    private EventStatsActivity mainActivity;
    private List<User> userList = new ArrayList<>();
    private static LayoutInflater inflater;

    public StatsHostBouncerCustomAdapter(EventStatsActivity mainActivity, List<User> userList)
    {
        super();
        this.userList = userList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rowView = inflater.inflate(R.layout.each_statsfriend_item, null);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public int getItemCount()
    {
        return userList.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        holder.imgView.setImageDrawable(userList.get(position).getProfilePic());
        holder.imgView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", userList.get(position).getUserID());
                mainActivity.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imgView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imgView = (ImageView) itemView.findViewById(R.id.each_statsfriend_image);
        }
    }
}