package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class ManagePartyCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Party> partyList;
    private static LayoutInflater inflater;

    public ManagePartyCustomAdapter(Activity activity, List<Party> partyList)
    {
        super();
        this.partyList = partyList;
        this.mainActivity = activity;
        inflater = ( LayoutInflater )mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final Holder holder;
        View layoutView = convertView;
        if(convertView == null)
        {
            layoutView = inflater.inflate(R.layout.each_manageevent_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        }
        else
        {
            holder = (Holder) layoutView.getTag();
        }

        holder.partyname = (TextView) layoutView.findViewById(R.id.eachManage_partyname_item);
        holder.partydate = (TextView) layoutView.findViewById(R.id.eachManage_partydate_item);
        holder.partyname.setText(partyList.get(position).getName());
        holder.partydate.setText( UtilityClass.dateToString(
                partyList.get(position).getStartingDateTime()));

        layoutView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mainActivity, EventStatsActivity.class);
                intent.putExtra("partyIDLong", getItem(position).getPartyID());
                mainActivity.startActivity(intent);
            }
        });
        return layoutView;
    }
}
