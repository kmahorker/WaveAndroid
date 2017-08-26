//package com.thewavesocial.waveandroid.AdaptersFolder;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.thewavesocial.waveandroid.BusinessObjects.User;
//import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
//import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
//import com.thewavesocial.waveandroid.HostFolder.EventStatsActivity;
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.UtilityClass;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StatsHostBouncerCustomAdapter extends RecyclerView.Adapter<StatsHostBouncerCustomAdapter.ViewHolder>
//{
//    private EventStatsActivity sharedPreferencesContext;
//    private List<User> userList = new ArrayList<>();
//    private static LayoutInflater inflater;
//
//    public StatsHostBouncerCustomAdapter(EventStatsActivity sharedPreferencesContext, List<User> userList)
//    {
//        super();
//        this.userList = userList;
//        this.sharedPreferencesContext = sharedPreferencesContext;
//        inflater = (LayoutInflater)sharedPreferencesContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View rowView = inflater.inflate(R.layout.each_statsfriend_item, null);
//        ViewHolder viewHolder = new ViewHolder(rowView);
//        return viewHolder;
//    }
//
//    @Override
//    public long getItemId(int position)
//    {
//        return position;
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        return userList.size();
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, final int position)
//    {
//        UtilityClass.getBitmapFromURL(sharedPreferencesContext, userList.get(position).getProfilePic(), new OnResultReadyListener<Bitmap>() {
//            @Override
//            public void onResultReady(Bitmap image) {
//                if (image != null)
//                    holder.imgView.setImageDrawable( UtilityClass.toRoundImage(sharedPreferencesContext.getResources(), image));
//            }
//        });
//        holder.imgView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(sharedPreferencesContext, FriendProfileActivity.class);
//                intent.putExtra("userIDLong", userList.get(position).getUserID());
//                sharedPreferencesContext.startActivity(intent);
//            }
//        });
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder
//    {
//        public ImageView imgView;
//
//        public ViewHolder(View itemView)
//        {
//            super(itemView);
//            imgView = (ImageView) itemView.findViewById(R.id.each_statsfriend_image);
//        }
//    }
//}
