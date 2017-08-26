//package com.thewavesocial.waveandroid.AdaptersFolder;
//
//import android.content.Context;
//import android.support.v4.app.FragmentActivity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.thewavesocial.waveandroid.BusinessObjects.Party;
//import com.thewavesocial.waveandroid.HomeFolder.MyEventsFragment;
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.UtilityClass;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
///**
// * Created by Kaushik on 2/4/2017.
// */
//
//public class MyEventsCustomAdapter extends BaseAdapter {
//    String [] result;
//    FragmentActivity context;
//    int [] imageId;
//    List<Party> partyList = new ArrayList<Party>();;
//    MyEventsFragment fragment;
//    private static LayoutInflater inflater=null;
//
//    public MyEventsCustomAdapter(FragmentActivity sharedPreferencesContext, MyEventsFragment fragment, List<Party> partyList) {
//        super();
//        this.partyList.addAll(partyList);
//        //this.partyList.addAll(partyList);
//        //result = friendsNamesList;
//        context = sharedPreferencesContext;
//        this.fragment = fragment;
//        //imageId = friendsImagesList;
//        inflater = ( LayoutInflater )context.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//    @Override
//    public int getCount() {
//        return partyList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return partyList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public class Holder
//    {
//        TextView tvName;
//        TextView tvDate;
//        TextView tvDistance;
//    }
//
//    /*public void setUserList(List<User> user){
//        this.partyList = user;
//    }*/
//
//    public void updateUserList(List<Party> party){
//        partyList.clear();
//        partyList.addAll(party);
//        this.notifyDataSetChanged();
//    }
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        if(convertView == null) {
//            Holder holder = new Holder();
//            View rowView;
//            rowView = inflater.inflate(R.layout.each_event_item, null);
//            holder.tvName = (TextView) rowView.findViewById(R.id.partyName);
//            holder.tvDate = (TextView) rowView.findViewById(R.id.partyDate);
//            holder.tvDistance = (TextView) rowView.findViewById(R.id.distance);
//            //holder.tv.setText("Name"); //Testing
//            holder.tvName.setText(partyList.get(position).getName());
//            Calendar cal =  partyList.get(position).getStartingDateTime();
//            holder.tvDate.setText(UtilityClass.dateToString(cal));
//            holder.tvDistance.setText("distHere");
//            rowView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // FriendsListActivity f = new FriendsListActivity();
//                    //fragment.showFriendProfileActivity(v, partyList.get(position));
//                    fragment.showPartyProfilePage(partyList.get(position));
//                    //Intent in = new Intent(FriendsListActivity, FriendProfileActivity.class)
//                }
//            });
//            return rowView;
//        }
//        return convertView;
//
//    }
//}