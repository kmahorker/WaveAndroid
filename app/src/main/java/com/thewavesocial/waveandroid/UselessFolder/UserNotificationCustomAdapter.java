//package com.thewavesocial.waveandroid.AdaptersFolder;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
//import com.thewavesocial.waveandroid.BusinessObjects.Notification;
//import com.thewavesocial.waveandroid.BusinessObjects.User;
//import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
//import com.thewavesocial.waveandroid.R;
//import com.thewavesocial.waveandroid.SocialFolder.FriendProfileActivity;
//import com.thewavesocial.waveandroid.UtilityClass;
//import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.*;
//
//import java.util.List;
//
//public class UserNotificationCustomAdapter extends BaseAdapter {
//    private Activity mainActivity;
//    private List<Notification> notifList;
//    private static LayoutInflater inflater;
//
//    public UserNotificationCustomAdapter(Activity mainActivity, List<Notification> notifList) {
//        super();
//        this.notifList = notifList;
//        this.mainActivity = mainActivity;
//        inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return notifList.size();
//    }
//
//    @Override
//    public Notification getItem(int position) {
//        return notifList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public class Holder {
//        ImageView senderImage;
//        TextView sender;
//        TextView message;
//        TextView timeAgo;
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        final Holder holder;
//        View layoutView = convertView;
//        if (convertView == null) {
//            layoutView = inflater.inflate(R.layout.each_usernotif_item, null);
//            holder = new Holder();
//            layoutView.setTag(holder);
//        } else {
//            holder = (Holder) layoutView.getTag();
//        }
//
//        final User[] sender = {new User()};
//        final View finalLayoutView = layoutView;
//        server_getUserObject(getItem(position).getSenderID(), new OnResultReadyListener<User>() {
//            @Override
//            public void onResultReady(User result) {
//                if ( result != null ) {
//                    sender[0] = result;
//
//                    holder.sender = (TextView) finalLayoutView.findViewById(R.id.eachNotif_sender);
//                    holder.message = (TextView) finalLayoutView.findViewById(R.id.eachNotif_message);
//                    holder.timeAgo = (TextView) finalLayoutView.findViewById(R.id.eachNotif_timeAgo);
//
//                    UtilityClass.getBitmapFromURL(mainActivity, sender[0].getProfilePic(), new OnResultReadyListener<Bitmap>() {
//                        @Override
//                        public void onResultReady(Bitmap image) {
//                            if (image != null)
//                                holder.senderImage.setImageDrawable( UtilityClass.toRoundImage(mainActivity.getResources(), image));
//                        }
//                    });
//
//                    holder.sender.setText(sender[0].getFirstName());
//                    holder.message.setText(getItem(position).getMessage());
//                    holder.timeAgo.setText(".  28min");
//
//                    holder.sender.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
//                            intent.putExtra("userIDLong", sender[0].getUserID());
//                            mainActivity.startActivity(intent);
//                        }
//                    });
//
//                    holder.message.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent intent = new Intent(mainActivity, FriendProfileActivity.class);
//                            intent.putExtra("userIDLong", sender[0].getUserID());
//                            mainActivity.startActivity(intent);
//                        }
//                    });
//                }
//
//            }
//        });
//
//        return layoutView;
//    }
//}
