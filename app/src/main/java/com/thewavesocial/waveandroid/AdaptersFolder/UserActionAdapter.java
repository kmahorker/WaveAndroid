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
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;

import java.util.Calendar;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

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
        EmojiconTextView partyEmoji;
        TextView partyname;
        TextView partyInfo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (type == type1Notification)
            return setupViewActivity(position, convertView, parent);
        else
            return setupViewAttending(position, convertView, parent);
    }

    private View setupViewActivity(final int position, View convertView, ViewGroup parent) {
        final Holder1 holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_usernotif_item, null);
            holder = new Holder1();
            layoutView.setTag(holder);
        } else {
            holder = (Holder1) layoutView.getTag();
        }

        final View finalLayoutView = layoutView;
        CurrentUser.server_getUserObject(notifList.get(position).getSenderID(), new OnResultReadyListener<User>() {
            @Override
            public void onResultReady(final User sender) {
                if ( sender != null ) {
                    holder.senderImage = (ImageView) finalLayoutView.findViewById(R.id.eachNotif_senderPhoto);
                    holder.sender = (TextView) finalLayoutView.findViewById(R.id.eachNotif_sender);
                    holder.notifmessage = (TextView) finalLayoutView.findViewById(R.id.eachNotif_message);
                    holder.timeAgo = (TextView) finalLayoutView.findViewById(R.id.eachNotif_timeAgo);
                    if (sender.getProfilePic() != null)
                        //TODO: Get Image from URL
                        //holder.senderImage.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(), sender.getProfilePic().getBitmap()));
                        holder.sender.setText(sender.getFirstName());
                    holder.notifmessage.setText(notifList.get(position).getMessage());
                    holder.timeAgo.setText("28m");
                    holder.senderImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                            intent.putExtra("userObject", sender);
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
                }
             }
        });

        return layoutView;
    }

    private View setupViewAttending(final int position, View convertView, ViewGroup parent) {
        Holder2 holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_userattending_item, null);
            holder = new Holder2();
            layoutView.setTag(holder);
        } else {
            holder = (Holder2) layoutView.getTag();
        }

        Party party = partyList.get(position);
        holder.partyEmoji = (EmojiconTextView) layoutView.findViewById(R.id.eachUser_partyEmoji_icon);
        holder.partyname = (TextView) layoutView.findViewById(R.id.eachUser_partyname_item);
        holder.partyInfo = (TextView) layoutView.findViewById(R.id.eachUser_partyInfo_item);

        if ( party.getPartyEmoji() != null && !party.getPartyEmoji().isEmpty()) {
            holder.partyEmoji.setText(party.getPartyEmoji());
        }
        holder.partyname.setText(party.getName());
        holder.partyInfo.setText(getCustomInfoText(party));

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                intent.putExtra("partyObject", partyList.get(position));
                intent.putExtra("callerActivity", EventStatsActivity.activitySocialFragment);
                mainActivity.startActivity(intent);

            }
        });

        return layoutView;
    }

    private String getCustomInfoText(Party party) {
        String compose = "";
        compose += getDays(party.getStartingDateTime()) + " days  ";
        compose += party.getAttendingUsers().size() + " going";
        return compose;
    }

    private int getDays(Calendar startingDateTime) {
        int sum = 0;
        sum += (startingDateTime.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR)) * 365;
        sum += (startingDateTime.get(Calendar.MONTH) - Calendar.getInstance().get(Calendar.MONTH)) * 30;
        sum += (startingDateTime.get(Calendar.DATE) - Calendar.getInstance().get(Calendar.DATE));
        return sum;
    }
}