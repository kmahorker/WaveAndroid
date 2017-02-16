package com.thewavesocial.waveandroid.FriendsFolder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.thewavesocial.waveandroid.BusinessObjects.DummyUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;
import com.thewavesocial.waveandroid.AdaptersFolder.CustomAdapter;
import com.thewavesocial.waveandroid.HomeDrawerActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {FriendsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FriendsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FriendsListFragment thisFragment;
   // private OnFragmentInteractionListener mListener;

    public FriendsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param friendsList List of Friends of current user.
     * @return A new instance of fragment FriendsListFragment.
     */
    // DONE: Rename and change types and number of parameters
    public static FriendsListFragment newInstance(ArrayList<User> friendsList) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, friendsList);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            //mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        //ListView friendsList = (ListView) findViewById(R.id.friendsList);
        //List<User> users = generateTestUserList(); //TODO testing
        //friendsList.setAdapter(new CustomAdapter(this, users));
        return rootView; //inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        final FriendsListFragment fragment = this;
        DummyUser dummy = new DummyUser(getActivity()); //TODO: FOR TESTING ONLY

        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_friends);

        //Should take Sorted List as argument
        final ListView friendsListView = (ListView) getActivity().findViewById(R.id.friendsList);

        //CurrentUser.setContext(getContext());
        final List<User> friendsUsers = dummy.getFriendsListObjects(dummy.getFriends()); //CurrentUser.getFriendsListObjects(CurrentUser.theUser.getFriends());

        //generateTestUserList(); //CurrentUser.theUser.getFriends() //TODO testing need to replace with actual List

        final CustomAdapter adapt = new CustomAdapter(getActivity(), this, friendsUsers);
        friendsListView.setAdapter(adapt);


        final LinearLayout lin = (LinearLayout) getActivity().findViewById(R.id.LinearLayout);

        ImageView inviteFriends = (ImageView) getActivity().findViewById(R.id.addFriendButton);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //UtilityClass.hideKeyboard(fragment.getActivity());
                showInviteFriendsActivity(v);
            }
        });
        final SearchView searchView = (SearchView) getActivity().findViewById(R.id.searchView);
        searchView.setQueryHint("Search Friends Name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<User> refinedUserList = new ArrayList<User>();
                refinedUserList.addAll(search(friendsUsers, query));
                friendsListView.setAdapter(new CustomAdapter(getActivity(), fragment, refinedUserList));
                searchView.clearFocus(); //Hide Keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<User> refinedUserList = search(friendsUsers, newText);
                //adapt.updateUserList(refinedUserList);
                //adapt.notifyDataSetChanged();
                //Fragment fragment =
                friendsListView.setAdapter(new CustomAdapter(getActivity(),fragment, refinedUserList));

                return true;
            }
        });

        //Hide Keyboard
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                UtilityClass.hideKeyboard(fragment.getActivity());
                return true;
            }
        });

        friendsListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                searchView.clearFocus();
                return false;
            }
        });


    }

    //Supporting Methods

    public void showInviteFriendsActivity(View view){
        Intent f_intent = new Intent(getActivity(), InviteFriendsActivity.class);
        UtilityClass.hideKeyboard(getActivity());
        startActivity(f_intent);

    }

    public void showFriendProfileActivity(View view, User clickedUser){
        Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
        intent.putExtra("userIDLong", clickedUser.getUserID());
        startActivity(intent);

    }

    public List<User> generateTestUserList(){
        List<User> userList = new ArrayList<User>();
        //User a = new User("Bob", "Jones", "bj@k.com", "password", "UCSB", "Male", new Date(51215), userList, userList, userList);
        User a = new User();
        User b = new User();
        User c = new User();
        User d = new User();
        User e = new User();

        a.setFirstName("Bob");
        a.setLastName("Jones");
        b.setFirstName("John");
        b.setLastName("Smith");
        c.setFirstName("Jone");
        c.setLastName("Dude");
        d.setFirstName("Turn");
        d.setLastName("Up");
        e.setFirstName("Kau");
        e.setLastName("Mah");

        userList.add(a);
        userList.add(b);
        userList.add(c);
        userList.add(d);
        userList.add(e);

        return userList;

    }

    public List<User> search(List<User> us, String query){
        //System.out.print("query: " + query);
        if(query == ""){
            return us;
        }
        List<User> users = new ArrayList<User>();
        for(User u : us){

            if((u.getFirstName().matches("(?i:" + query + ".*)"))){
                Log.d("V","  firstName: " + u.getFirstName());
                Log.d("V","    bool: " + u.getFirstName().contains(query));
                users.add(u);
            }

            else if(u.getLastName().matches("(?i:" + query + ".*)")){
                Log.d("V","  lastName: " + u.getLastName());
                Log.d("V","    bool: " + u.getLastName().contains(query));
                users.add(u);
            }
            else if(u.getFullName().matches("(?i:" + query + ".*)")){
                users.add(u);
            }
            for(User check: users) {
                Log.d("V","    " + check.getFirstName());
            }
        }

        //System.out.println("    " + users);
        return users;
    }

}
