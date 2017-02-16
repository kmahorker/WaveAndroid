package com.thewavesocial.waveandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.thewavesocial.waveandroid.BusinessObjects.Party;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserPartyCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Party> partyList;
    private UserProfileFragment fragment;
    private int type = 1;
    private static LayoutInflater inflater;

    public UserPartyCustomAdapter(FragmentActivity mainActivity, UserProfileFragment fragment, List<Party> partyList)
    {
        super();
        this.partyList = partyList;
        Party p = new Party();
        this.mainActivity = mainActivity;
        this.fragment = fragment;
        inflater = ( LayoutInflater )mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        type = 1;
    }

    public UserPartyCustomAdapter(Activity activity, List<Party> partyList)
    {
        super();
        this.partyList = partyList;
        this.mainActivity = activity;
        inflater = ( LayoutInflater )mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        type = 2;
    }

    @Override
    public int getCount()
    {
        return partyList.size();
    }

    @Override
    public Party getItem(int position)
    {
        return partyList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public class Holder
    {
        TextView partyname;
        TextView partydate;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            Holder holder = new Holder();

            View layoutView;
            if (type == 1)
            {
                layoutView = inflater.inflate(R.layout.each_userparty_item, null);
                holder.partyname = (TextView) layoutView.findViewById(R.id.each_partyname_item);
                holder.partydate = (TextView) layoutView.findViewById(R.id.each_partydate_item);
            }
            else
            {
                layoutView = inflater.inflate(R.layout.each_manageevent_item, null);
                holder.partyname = (TextView) layoutView.findViewById(R.id.eachManage_partyname_item);
                holder.partydate = (TextView) layoutView.findViewById(R.id.eachManage_partydate_item);
            }
            holder.partyname.setText(partyList.get(position).getName());
            holder.partydate.setText( UtilityClass.dateToString(
                    partyList.get(position).getStartingDateTime()));

            layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mainActivity, PartyProfileActivity.class);
                    intent.putExtra("partyIDLong", getItem(position).getPartyID());
                    mainActivity.startActivity(intent);
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
