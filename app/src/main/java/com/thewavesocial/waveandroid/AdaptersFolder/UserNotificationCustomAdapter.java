package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Notification;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

import static android.os.Build.ID;

public class UserNotificationCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Notification> notifList;
    private static LayoutInflater inflater;

    public UserNotificationCustomAdapter(Activity mainActivity, List<Notification> notifList)
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
        ImageView senderImage;
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
            layoutView = inflater.inflate(R.layout.each_notif_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        }
        else
        {
            holder = (Holder) layoutView.getTag();
        }

        User sender = CurrentUser.getUserObject(getItem(position).getSenderID());

        holder.senderImage = (ImageView) layoutView.findViewById(R.id.eachNotif_senderPhoto);
        holder.sender = (TextView) layoutView.findViewById(R.id.eachNotif_sender);
        holder.notifmessage = (TextView) layoutView.findViewById(R.id.eachNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachNotif_timeAgo);

        holder.senderImage.setImageDrawable(UtilityClass.toRoundImage(
                mainActivity.getResources(), sender.getProfilePic().getBitmap()));
        holder.sender.setText(sender.getFirstName());
        holder.notifmessage.setText(getItem(position).getMessage());
        holder.timeAgo.setText("28m");

        holder.notifmessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UtilityClass.printAlertMessage(mainActivity, getItem(position).getMessage() + ", " + position, true);
            }
        });

        return layoutView;
    }
}
