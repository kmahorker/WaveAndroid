package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
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
import com.thewavesocial.waveandroid.UtilityClass;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

public class UserActionAdapter extends BaseAdapter {
    private Activity mainActivity;
    private ArrayList<Notification> notifList;
    private ArrayList<Object> senderObjects;
    private List<Party> partyList;
    private int type;
    private static LayoutInflater inflater;
    public static final int type1Notification = 1, type2Attending = 2;

    public UserActionAdapter(Activity mainActivity, ArrayList<Notification> notifList, ArrayList<Object> senderObjects) {
        super();
        this.notifList = notifList;
        this.senderObjects = senderObjects;
        this.mainActivity = mainActivity;
        this.type = type1Notification;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UserActionAdapter(Activity mainActivity, List<Party> partyList) {
        super();
        this.partyList = partyList;
        this.mainActivity = mainActivity;
        this.type = type2Attending;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (type == type1Notification)
            return senderObjects.size();
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
        TextView sender, message, timeAgo, accept, decline;
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
        holder.sender = (TextView) layoutView.findViewById(R.id.eachNotif_sender);
        holder.message = (TextView) layoutView.findViewById(R.id.eachNotif_message);
        holder.timeAgo = (TextView) layoutView.findViewById(R.id.eachNotif_timeAgo);
        holder.accept = (TextView) layoutView.findViewById(R.id.eachNotif_accept);
        holder.decline = (TextView) layoutView.findViewById(R.id.eachNotif_decline);

        holder.message.setText(notifList.get(position).getMessage());
        holder.timeAgo.setText(". " + UtilityClass.getNotificationTime(notifList.get(position).getCreate_time()));

        if ((notifList.get(position).getRequestType() == Notification.TYPE_FOLLOWED ||
                notifList.get(position).getRequestType() == Notification.TYPE_FOLLOWING)) //Friend type notification
        {
            final User sender = (User) senderObjects.get(position);
            holder.sender.setText(sender.getFirst_name());
            holder.accept.setVisibility(View.INVISIBLE);
            holder.decline.setVisibility(View.INVISIBLE);

            layoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                    intent.putExtra("userObject", sender);
                    mainActivity.startActivity(intent);
                }
            });
        } else if (notifList.get(position).getRequestType() == Notification.TYPE_GOING ||
                notifList.get(position).getRequestType() == Notification.TYPE_HOSTING ||
                notifList.get(position).getRequestType() == Notification.TYPE_BOUNCING ||
                notifList.get(position).getRequestType() == Notification.TYPE_INVITE_GOING ||
                notifList.get(position).getRequestType() == Notification.TYPE_INVITE_BOUNCING) //Friend type notification
        {
            final Party party = (Party) senderObjects.get(position);
            holder.sender.setText("\"" + party.getName() + "\"");
            holder.accept.setVisibility(View.INVISIBLE);
            holder.decline.setVisibility(View.INVISIBLE);

            if (notifList.get(position).getRequestType() == Notification.TYPE_INVITE_GOING || notifList.get(position).getRequestType() == Notification.TYPE_INVITE_BOUNCING) {
                holder.accept.setVisibility(View.VISIBLE);
                holder.decline.setVisibility(View.VISIBLE);

                holder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilityClass.startProgressbar(mainActivity);
                        final String type = (notifList.get(position).getRequestType() == Notification.TYPE_INVITE_GOING) ? "going" : "bouncing";

                        server_manageUserForParty(CurrentUser.theUser.getUserID(), notifList.get(position).getSenderID(), type, "POST", new OnResultReadyListener<String>() {
                            @Override
                            public void onResultReady(String result) {
                                if (result.equals("success")) {
                                    server_createNotification(CurrentUser.theUser.getUserID(), "", notifList.get(position).getSenderID(), type, new OnResultReadyListener<String>() {
                                        @Override
                                        public void onResultReady(String result) {
                                            if (result.equals("success")) {
                                                server_deleteNotification(CurrentUser.theUser.getUserID(), notifList.get(position).getNotificationID(), new OnResultReadyListener<String>() {
                                                    @Override
                                                    public void onResultReady(String result) {
                                                        if (result.equals("success")) {
                                                            notifList.remove(position);
                                                            senderObjects.remove(position);
                                                            UserActionAdapter.this.notifyDataSetChanged();
                                                            UtilityClass.endProgressbar(mainActivity, true);
                                                        } else
                                                            UtilityClass.endProgressbar(mainActivity, false);
                                                    }
                                                });
                                            } else
                                                UtilityClass.endProgressbar(mainActivity, false);
                                        }
                                    });
                                    server_uninviteUser(CurrentUser.theUser.getUserID(), notifList.get(position).getSenderID(), null);
                                } else
                                    UtilityClass.endProgressbar(mainActivity, false);
                            }
                        });
                    }
                });

                holder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UtilityClass.startProgressbar(mainActivity);
                        server_deleteNotification(CurrentUser.theUser.getUserID(), notifList.get(position).getNotificationID(), new OnResultReadyListener<String>() {
                            @Override
                            public void onResultReady(String result) {
                                if (result.equals("success")) {
                                    notifList.remove(position);
                                    senderObjects.remove(position);
                                    UserActionAdapter.this.notifyDataSetChanged();
                                    UtilityClass.endProgressbar(mainActivity, true);
                                } else
                                    UtilityClass.endProgressbar(mainActivity, false);
                            }
                        });
                    }
                });
            }

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

    private View setupViewAttending(final int position, View convertView, ViewGroup parent) {
        final Holder2 holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_userattending_item, null);
            holder = new Holder2();
            layoutView.setTag(holder);
        } else {
            holder = (Holder2) layoutView.getTag();
        }

        final Party party = partyList.get(position);
        holder.partyEmoji = (EmojiconTextView) layoutView.findViewById(R.id.eachUser_partyEmoji_icon);
        holder.partyname = (TextView) layoutView.findViewById(R.id.eachUser_partyname_item);
        holder.partyInfo = (TextView) layoutView.findViewById(R.id.eachUser_partyInfo_item);

        if (party.getPartyEmoji() != null && !party.getPartyEmoji().isEmpty())
            holder.partyEmoji.setText(party.getPartyEmoji());
        holder.partyname.setText(party.getName());
        server_getUsersOfEvent(party.getPartyID(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                holder.partyInfo.setText(getCustomInfoText(party, result.get("going").size()));
            }
        });

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

    private String getCustomInfoText(Party party, int goingSize) {
        String compose = "";
        compose += getDays(UtilityClass.epochToCalendar(party.getStartingDateTime())) + " days  ";
        compose += goingSize + " going";
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