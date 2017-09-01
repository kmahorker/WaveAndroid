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
import com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import github.ankushsachdeva.emojicon.EmojiconTextView;

public class SearchEventCustomAdapter extends BaseAdapter {
    private Activity mainActivity;
    private List<Party> partyList;
    private HashMap<String, ArrayList<Party>> userParties;
    private static LayoutInflater inflater;

    public SearchEventCustomAdapter(Activity mainActivity, List<Party> partyList, HashMap<String, ArrayList<Party>> userParties) {
        super();
        this.partyList = partyList;
        this.userParties = userParties;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
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

        if (party.getEmoji() != null) {
            holder.image.setText(party.getEmoji());
        }

        holder.name.setText(party.getName());
        holder.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (containsID(userParties.get("hosting"), party.getId()))
                    Toast.makeText(mainActivity, "Party Already Hosting.", Toast.LENGTH_LONG).show();
                else if (containsID(userParties.get("bouncing"), party.getId()))
                    Toast.makeText(mainActivity, "Party Already Bouncing.", Toast.LENGTH_LONG).show();
                else if (containsID(userParties.get("going"),party.getId()))
                    Toast.makeText(mainActivity, "Party Already Going.", Toast.LENGTH_LONG).show();
                else if (containsID(userParties.get("attending"), party.getId())) {
                    Toast.makeText(mainActivity, "Party Already Attending.", Toast.LENGTH_LONG).show();
                }
                else {
                    DatabaseAccess.server_manageUserForParty(CurrentUser.getUser().getId(), party.getId(), "going", "POST", new OnResultReadyListener<String>() {
                        @Override
                        public void onResultReady(String result) {
                            if ( result.equals("success") ) {
                                Toast.makeText(mainActivity, "Success.", Toast.LENGTH_LONG).show();
                                ((TextView)mainActivity.findViewById(R.id.actionbar_activity_home_text_social)).setText("SOCIAL(1)");
                                ((TextView)mainActivity.findViewById(R.id.user_going_button)).setText("Going(1)");
                            } else
                                Toast.makeText(mainActivity, "Error.", Toast.LENGTH_LONG).show();
                        }
                    });
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

    private boolean containsID(List<Party> following, String partyID) {
        for ( Party party : following ) {
            if ( party.getId().equals(partyID) ) {
                return true;
            }
        }
        return false;
    }
}
