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
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class UserNotificationCustomAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<Notification> notifList;
    private static LayoutInflater inflater;

    public UserNotificationCustomAdapter(Activity mainActivity, List<Notification> notifList) {
        super();
        this.notifList = notifList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return notifList.size();
    }

    @Override
    public Notification getItem(int position) {
        return notifList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        ImageView senderImage;
        TextView sender;
        TextView notifmessage;
        TextView timeAgo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_usernotif_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        } else {
            holder = (Holder) layoutView.getTag();
        }

        final User sender = CurrentUser.getUserObject(getItem(position).getSenderID());

        holder.senderImage = (ImageView) layoutView.findViewById(R.id.eachNotif_senderPhoto);
        holder.sender = (TextView) layoutView.findViewById(R.id.eachNotif_sender);
        holder.notifmessage = (TextView) layoutView.findViewById(R.id.eachNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachNotif_timeAgo);

        if (sender.getProfilePic() != null) {
            // TODO: 04/21/2017 Add image by url
//            holder.senderImage.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), sender.getProfilePic().getBitmap()));
        }
        holder.sender.setText(sender.getFirstName());
        holder.notifmessage.setText(getItem(position).getMessage());
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
                intent.putExtra("userObject", sender);
                mainActivity.startActivity(intent);
            }
        });

        holder.notifmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userObject", sender);
                mainActivity.startActivity(intent);
            }
        });

        return layoutView;
    }
}
