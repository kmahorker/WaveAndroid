package com.thewavesocial.waveandroid.AdaptersFolder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class SearchEventCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<Party> partyList;
    private static LayoutInflater inflater;

    public SearchEventCustomAdapter(Activity mainActivity, List<Party> partyList)
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
        ImageView image;
        TextView name;
        TextView go;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Holder holder;
        View layoutView = convertView;
        if(convertView == null)
        {
            layoutView = inflater.inflate(R.layout.each_searchevent_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        }
        else
        {
            holder = (Holder) layoutView.getTag();
        }

        final Party party = partyList.get(position);

        holder.image = (ImageView) layoutView.findViewById(R.id.eachSearchEvent_image);
        holder.name = (TextView) layoutView.findViewById(R.id.eachSearchEvent_name);
        holder.go = (TextView) layoutView.findViewById(R.id.eachSearchEvent_go);

        if ( party.getPartyEmoji() != null )
        {
            holder.image.setImageDrawable(UtilityClass.toRoundImage(
                    mainActivity.getResources(), party.getPartyEmoji().getBitmap()));
        }
        holder.name.setText( party.getName() );
        holder.go.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
//                Intent intent = new Intent(mainActivity, PartyProfileFragment.class);
//                intent.putExtra("partyIDLong", party.getPartyID());
//                mainActivity.startActivity(intent);
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
