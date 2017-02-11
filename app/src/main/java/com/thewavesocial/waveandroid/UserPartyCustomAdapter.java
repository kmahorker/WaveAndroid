package com.thewavesocial.waveandroid;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kaushik on 2/4/2017.
 */

public class UserPartyCustomAdapter extends BaseAdapter
{
    String [] result;
    int [] imageId;
    FragmentActivity mainActivity;
    List<Party> partyList = new ArrayList<Party>();;
    UserProfileFragment fragment;
    private static LayoutInflater inflater = null;

    public UserPartyCustomAdapter(FragmentActivity mainActivity, UserProfileFragment fragment, List<Party> partyList) {
        super();
        this.partyList.addAll(partyList);
        this.mainActivity = mainActivity;
        this.fragment = fragment;
        inflater = ( LayoutInflater )mainActivity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public void updateUserList(List<Party> parties){
        partyList.clear();
        partyList.addAll(parties);
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            Holder holder = new Holder();
            View layoutView = inflater.inflate(R.layout.each_userparty_item, null);

            holder.partyname = (TextView) layoutView.findViewById(R.id.each_partyname_item);
            holder.partydate = (TextView) layoutView.findViewById(R.id.each_partydate_item);

            holder.partyname.setText(partyList.get(position).getName());
            holder.partydate.setText(
                    partyList.get(position).getStartingDateTime().get(Calendar.MONTH) + "/"
                    + partyList.get(position).getStartingDateTime().get(Calendar.DATE));

            layoutView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(mainActivity, PartyProfileActivity.class);
                    intent.putExtra("partyFromPartyList", getItem(position));
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
