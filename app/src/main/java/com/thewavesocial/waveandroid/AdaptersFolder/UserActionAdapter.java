package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class UserActionAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<Notification> notifList;
    private List<Party> partyList;
    private int type;
    private static LayoutInflater inflater;
    public static final int type1Notification = 1, type2Attending = 2;

    public UserActionAdapter(Activity mainActivity, List<Notification> notifList) {
        super();
        this.notifList = notifList;
        this.mainActivity = mainActivity;
        this.type = type1Notification;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UserActionAdapter(Activity mainActivity, List<Party> partyList, int random) {
        super();
        this.partyList = partyList;
        this.mainActivity = mainActivity;
        this.type = type2Attending;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (type == type1Notification)
            return notifList.size();
        else
            return partyList.size();
    }

    @Override
    public Object getItem(int position) {
        if (type == type1Notification)
            return notifList.get(position);
        else
            return partyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder1 {
        ImageView senderImage;
        TextView sender;
        TextView notifmessage;
        TextView timeAgo;
    }

    public class Holder2 {
        // TODO: 04/19/2017 wait for design
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if ( type == type1Notification )
            return setupViewActivity(position, convertView, parent);
        else
            return setupViewAttending(position, convertView, parent);
    }

    private View setupViewActivity(int position, View convertView, ViewGroup parent) {
        Holder1 holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_usernotif_item, null);
            holder = new Holder1();
            layoutView.setTag(holder);
        } else {
            holder = (Holder1) layoutView.getTag();
        }
        final User sender = CurrentUser.getUserObject(notifList.get(position).getSenderID());
        holder.senderImage = (ImageView) layoutView.findViewById(R.id.eachNotif_senderPhoto);
        holder.sender = (TextView) layoutView.findViewById(R.id.eachNotif_sender);
        holder.notifmessage = (TextView) layoutView.findViewById(R.id.eachNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachNotif_timeAgo);
        if (sender.getProfilePic() != null)
            holder.senderImage.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), sender.getProfilePic().getBitmap()));
        holder.sender.setText(sender.getFirstName());
        holder.notifmessage.setText(notifList.get(position).getMessage());
        holder.timeAgo.setText("28m");
        holder.senderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", sender.getUserID());
                mainActivity.startActivity(intent);
            }
        });
        holder.sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", sender.getUserID());
                mainActivity.startActivity(intent);
            }
        });
        holder.notifmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", sender.getUserID());
                mainActivity.startActivity(intent);
            }
        });
        return layoutView;
    }

    private View setupViewAttending(int position, View convertView, ViewGroup parent) {
        // TODO: 04/19/2017 wait for design
        return convertView;
    }
}
