package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_getUsersOfEvent;

public class ManagePartyCustomAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<Party> partyList;
    private static LayoutInflater inflater;

    public ManagePartyCustomAdapter(Activity mainActivity, List<Party> partyList) {
        super();
        this.partyList = partyList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return partyList.size();
    }

    @Override
    public Party getItem(int position) {
        return partyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        EmojiconTextView partyEmoji;
        TextView partyname;
        TextView partyInfo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_manageevent_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        } else {
            holder = (Holder) layoutView.getTag();
        }

        final Party party = partyList.get(position);
        holder.partyEmoji = (EmojiconTextView) layoutView.findViewById(R.id.eachManage_partyEmoji_icon);
        holder.partyname = (TextView) layoutView.findViewById(R.id.eachManage_partyname_item);
        holder.partyInfo = (TextView) layoutView.findViewById(R.id.eachManage_partyInfo_item);

        if (party.getEmoji() != null && !party.getEmoji().isEmpty()) {
            holder.partyEmoji.setText(party.getEmoji());
        }
        holder.partyname.setText(party.getName());
        server_getUsersOfEvent(party.getId(), new OnResultReadyListener<HashMap<String, ArrayList<User>>>() {
            @Override
            public void onResultReady(HashMap<String, ArrayList<User>> result) {
                holder.partyInfo.setText(getCustomInfoText(party, result.get("going").size()));
            }
        });

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                intent.putExtra("callerActivity", EventStatsActivity.activityHostFragment);
                intent.putExtra("partyObject", party);
                mainActivity.startActivity(intent);
            }
        });

        return layoutView;
    }

    private String getCustomInfoText(Party party, int goingSize) {
        String compose = "";
        compose += getDays(UtilityClass.epochToCalendar(party.getDate())) + " days  ";
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
