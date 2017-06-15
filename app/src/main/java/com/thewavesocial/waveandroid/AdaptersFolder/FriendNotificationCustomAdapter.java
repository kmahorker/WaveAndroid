package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

import java.util.ArrayList;
import java.util.List;

public class FriendNotificationCustomAdapter extends BaseAdapter
{
    private FriendProfileActivity mainActivity ;
    private List<Notification> notifList;
    private static LayoutInflater inflater;
    private List<Object> senderObjects;

    public FriendNotificationCustomAdapter(FriendProfileActivity mainActivity, List<Notification> notifList, List<Object> senderObjects)
    {
        super();
        this.notifList = notifList;
        this.senderObjects = senderObjects;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return notifList.size();
    }

    @Override
    public Notification getItem(int position)
    {
        return notifList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public class Holder
    {
        TextView sender;
        TextView message;
        TextView timeAgo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Holder holder;
        View layoutView = convertView;
        if(convertView == null)
        {
            layoutView = inflater.inflate(R.layout.each_friendnotif_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        }
        else
        {
            holder = (Holder) layoutView.getTag();
        }

        holder.sender = (TextView) layoutView.findViewById(R.id.eachFriendNotif_sender);
        holder.message = (TextView) layoutView.findViewById(R.id.eachFriendNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachFriendNotif_timeAgo);

        holder.message.setText(getItem(position).getMessage());
        holder.timeAgo.setText( ". " + UtilityClass.getNotificationTime(notifList.get(position).getCreate_time()));

        if ( (getItem(position).getRequestType() == Notification.TYPE_FOLLOWED ||
                getItem(position).getRequestType() == Notification.TYPE_FOLLOWING) ) //Friend type notification
        {
            final User user = (User) senderObjects.get(position);
            holder.sender.setText(user.getFirstName());

            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                    intent.putExtra("userObject", user);
                    mainActivity.startActivity(intent);
                    Log.d("Position", position+"");
                }
            });
        }
        else if ( getItem(position).getRequestType() == Notification.TYPE_GOING ||
                getItem(position).getRequestType() == Notification.TYPE_HOSTING ||
                getItem(position).getRequestType() == Notification.TYPE_BOUNCING ) //Friend type notification
        {
            final Party party = (Party)senderObjects.get(position);
            holder.sender.setText("\"" + party.getName() + "\"");

            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                    intent.putExtra("partyObject", party);
                    intent.putExtra("callerActivity", EventStatsActivity.activitySocialFragment);
                    mainActivity.startActivity(intent);
                }
            });
        }
        return layoutView;
    }
}
