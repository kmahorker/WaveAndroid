package com.thewavesocial.waveandroid.AdaptersFolder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.mainActivity;

import java.util.List;

public class FriendNotificationCustomAdapter extends BaseAdapter
{
    private FriendProfileActivity mainActivity ;
    private List<Notification> notifList;
    private static LayoutInflater inflater;

    public FriendNotificationCustomAdapter(FriendProfileActivity mainActivity, List<Notification> notifList)
    {
        super();
        this.notifList = notifList;
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
        TextView notifmessage;
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
        holder.notifmessage = (TextView) layoutView.findViewById(R.id.eachFriendNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachFriendNotif_timeAgo);

        if ( getItem(position).getRequestType() == Notification.TYPE_FOLLOWERS ||
                getItem(position).getRequestType() == Notification.TYPE_FOLLOWING ) //Friend type notification
        {
            final View finalLayoutView = layoutView;
            server_getUserObject(getItem(position).getSenderID(), new OnResultReadyListener<User>() {
                @Override
                public void onResultReady(final User result) {
                    if ( result != null ) {
                        holder.sender.setText(result.getFirstName() + ".");
                        holder.notifmessage.setText(getItem(position).getMessage());
                        holder.timeAgo.setText("28m");

                        finalLayoutView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                                intent.putExtra("userObject", result);
                                mainActivity.startActivity(intent);
                            }
                        });
                    }
                }
            });
        }
        else if ( getItem(position).getRequestType() == Notification.TYPE_GOING ||
                getItem(position).getRequestType() == Notification.TYPE_HOSTING ) //Friend type notification
        {
            final View finalLayoutView1 = layoutView;
            server_getPartyObject(getItem(position).getSenderID(), new OnResultReadyListener<Party>() {
                @Override
                public void onResultReady(final Party result) {
                    if ( result != null ) {
                        holder.sender.setText("\"" + result.getName() + "\".");
                        holder.notifmessage.setText(getItem(position).getMessage());
                        holder.timeAgo.setText("28min");

                        finalLayoutView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                                intent.putExtra("partyObject", result);
                                intent.putExtra("callerActivity", EventStatsActivity.activitySocialFragment);
                                mainActivity.startActivity(intent);
                            }
                        });
                    }
                }
            });
        }
        return layoutView;
    }
}
