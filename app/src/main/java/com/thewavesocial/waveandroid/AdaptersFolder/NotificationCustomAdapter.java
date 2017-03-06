package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Notification> notifList;
    private static LayoutInflater inflater;

    public NotificationCustomAdapter(Activity mainActivity, List<Notification> notifList)
    {
        super();
        this.notifList = new ArrayList<>();
        this.notifList.addAll( notifList );
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
        TextView notifmessage;
        ImageView acceptButton;
        ImageView denyButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            Holder holder = new Holder();
            View layoutView = inflater.inflate(R.layout.each_notif_item, null);

            holder.notifmessage = (TextView) layoutView.findViewById(R.id.eachNotif_message);
            holder.acceptButton = (ImageView) layoutView.findViewById(R.id.eachNotif_accept_button);
            holder.denyButton = (ImageView) layoutView.findViewById(R.id.eachNotif_deny_button);
            holder.notifmessage.setText( notifList.get(position).getMessage() );

            holder.acceptButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if ( notifList.get(position).getRequestType() == Notification.type1FriendRequests )
                    {
                        CurrentUser.getUserObject(notifList.get(position).getSenderID())
                                .getFriends().add(CurrentUser.theUser.getUserID());
                        CurrentUser.theUser.getFriends().add( notifList.get(position).getSenderID() );
                    }
                    else if ( notifList.get(position).getRequestType() == Notification.type2HostingRequests )
                    {
                        CurrentUser.getPartyObject(notifList.get(position).getSenderID())
                                .getHostingUsers().add( CurrentUser.theUser.getUserID() );
                    }
//                    ((HomeDrawerActivity)mainActivity).removePosition(position, notifList.get(position).getRequestType());
                }
            });

            holder.denyButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
//                    ((HomeDrawerActivity)mainActivity).removePosition(position, notifList.get(position).getRequestType());
                }
            });
            return layoutView;
        }
        else
        {
            return convertView;
        }
    }
}
