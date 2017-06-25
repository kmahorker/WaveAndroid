package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

public class SearchEventCustomAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<Party> partyList;
    private static LayoutInflater inflater;

    public SearchEventCustomAdapter(Activity mainActivity, List<Party> partyList) {
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
        EmojiconTextView image;
        TextView name;
        TextView go;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View layoutView = convertView;
        if (convertView == null) {
            layoutView = inflater.inflate(R.layout.each_searchevent_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        } else {
            holder = (Holder) layoutView.getTag();
        }

        final Party party = partyList.get(position);

        holder.image = (EmojiconTextView) layoutView.findViewById(R.id.eachSearchEvent_image);
        holder.name = (TextView) layoutView.findViewById(R.id.eachSearchEvent_name);
        holder.go = (TextView) layoutView.findViewById(R.id.eachSearchEvent_go);

        if (party.getPartyEmoji() != null) {
            holder.image.setText(party.getPartyEmoji());
        }

        holder.name.setText(party.getName());
        holder.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CurrentUser.theUser.getAttending().contains(party.getPartyID())) {
                    Toast.makeText(mainActivity, "Party Already Added.", Toast.LENGTH_LONG).show();
                } else {
                    CurrentUser.theUser.getAttending().add(0, party.getPartyID());
                    Toast.makeText(mainActivity, "Party Added!", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                intent.putExtra("partyObject", party);
                intent.putExtra("callerActivity", EventStatsActivity.activitySocialFragment);
                mainActivity.startActivity(intent);
            }
        });

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(mainActivity);
            }
        });

        return layoutView;
    }
}
