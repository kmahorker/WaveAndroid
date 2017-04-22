package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

public class PartyAttendeesCustomAdapter extends RecyclerView.Adapter<PartyAttendeesCustomAdapter.ViewHolder>
{
    private HomeSwipeActivity mainActivity;
    private List<User> userList = new ArrayList<>();
    private static LayoutInflater inflater;

    public PartyAttendeesCustomAdapter(HomeSwipeActivity mainActivity, List<User> userList)
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
        // TODO: 04/21/2017 Add image by URL
//        holder.imgView.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), userList.get(position).getProfilePic().getBitmap()));
//        holder.imgView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
//                intent.putExtra("userIDLong", userList.get(position).getUserID());
//                mainActivity.startActivity(intent);
//            }
//        });
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
