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
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;

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

        if ( getItem(position).getRequestType() == Notification.type4FriendFollowingNotice ) //Friend type notification
        {
            final User senderUser = CurrentUser.getUserObject(getItem(position).getSenderID());
            holder.sender.setText(senderUser.getFirstName() + ".");
            holder.notifmessage.setText(getItem(position).getMessage());
            holder.timeAgo.setText("28m");

            layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                    intent.putExtra("userObject", senderUser);
                    mainActivity.startActivity(intent);
                }
            });
        }
        else //Party type notification
        {
            final Party senderParty = CurrentUser.getPartyObject(getItem(position).getSenderID());
            holder.sender.setText( "\"" + senderParty.getName() + "\"." );
            holder.notifmessage.setText(getItem(position).getMessage());
            holder.timeAgo.setText("28min");

            layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // TODO: 03/12/2017 Figure out a stand-alone party profile or fragment party profile
//                    Fragment fragment = new PartyProfileFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putLong("partyIDLong", senderParty.getPartyID());
//                    fragment.setArguments(bundle);
//
//                    FragmentManager fm = mainActivity.getSupportFragmentManager();
//                    FragmentTransaction transaction = fm.beginTransaction();
//                    transaction.replace(R.id.home_mapsView_infoFrame, fragment);
//                    transaction.commit();
                }
            });
        }
        return layoutView;
    }
}
