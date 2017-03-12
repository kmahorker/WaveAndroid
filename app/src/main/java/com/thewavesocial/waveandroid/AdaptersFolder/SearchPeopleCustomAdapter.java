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

import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.List;

public class SearchPeopleCustomAdapter extends BaseAdapter
{
    private Activity mainActivity ;
    private List<User> userList;
    private static LayoutInflater inflater;

    public SearchPeopleCustomAdapter(Activity mainActivity, List<User> userList)
    {
        super();
        this.userList = userList;
        this.mainActivity = mainActivity;
        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return userList.size();
    }

    @Override
    public User getItem(int position)
    {
        return userList.get(position);
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
        TextView follow;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Holder holder;
        View layoutView = convertView;
        if(convertView == null)
        {
            layoutView = inflater.inflate(R.layout.each_searchpeople_item, null);
            holder = new Holder();
            layoutView.setTag(holder);
        }
        else
        {
            holder = (Holder) layoutView.getTag();
        }

        final User user = userList.get(position);

        holder.image = (ImageView) layoutView.findViewById(R.id.eachSearchPeople_image);
        holder.name = (TextView) layoutView.findViewById(R.id.eachSearchPeople_name);
        holder.follow = (TextView) layoutView.findViewById(R.id.eachSearchPeople_follow);

        if ( user.getProfilePic() != null )
        {
            holder.image.setImageDrawable(UtilityClass.toRoundImage(mainActivity.getResources(),
                    user.getProfilePic().getBitmap()));
        }
        holder.name.setText( user.getFullName() );
        holder.follow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
                intent.putExtra("userIDLong", user.getUserID());
                mainActivity.startActivity(intent);
            }
        });

        return layoutView;
    }
}
