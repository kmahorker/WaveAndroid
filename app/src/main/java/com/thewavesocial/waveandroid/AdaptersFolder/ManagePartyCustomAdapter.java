package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
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
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

public class ManagePartyCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Party> partyList;
    private static LayoutInflater inflater;

    public ManagePartyCustomAdapter(Activity mainActivity, List<Party> partyList)
    {
        super();
        this.partyList = partyList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ImageView partyEmoji;
        TextView partyname;
        TextView partyInfo;
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

        Party party = partyList.get(position);
        holder.partyEmoji = (ImageView) layoutView.findViewById(R.id.eachManage_partyEmoji_icon);
        holder.partyname = (TextView) layoutView.findViewById(R.id.eachManage_partyname_item);
        holder.partyInfo = (TextView) layoutView.findViewById(R.id.eachManage_partyInfo_item);

        if ( party.getPartyEmoji() != null )
        {
            holder.partyEmoji.setImageDrawable(UtilityClass.toRoundImage(
                    mainActivity.getResources(), party.getPartyEmoji().getBitmap()));
        }
        holder.partyname.setText(party.getName());
        holder.partyInfo.setText( getCustomInfoText( party ) );

        return layoutView;
    }

    private String getCustomInfoText(Party party)
    {
        String compose = "";
        compose += getDays(party.getStartingDateTime()) + " days  ";
        compose += party.getAttendingUsers().size() + " going";
        return compose;
    }

    private int getDays(Calendar startingDateTime)
    {
        int sum = 0;
        sum += (startingDateTime.get(Calendar.YEAR) - Calendar.getInstance().get(Calendar.YEAR))*365;
        sum += (startingDateTime.get(Calendar.MONTH) - Calendar.getInstance().get(Calendar.MONTH))*30;
        sum += (startingDateTime.get(Calendar.DATE) - Calendar.getInstance().get(Calendar.DATE));
        return sum;
    }
}
