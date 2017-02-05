package com.thewavesocial.waveandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.thewavesocial.waveandroid.BusinessObjects.User;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendsListFragment.OnFragmentInteractionListener} interface
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

        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_friends);

        ListView friendsList = (ListView) getActivity().findViewById(R.id.friendsList);
        List<User> users = generateTestUserList(); //TODO testing
        friendsList.setAdapter(new CustomAdapter(getActivity(), this, users));
        ImageButton inviteFriends = (ImageButton) getActivity().findViewById(R.id.addFriendButton);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showInviteFriendsActivity(v);
            }
        });
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    //Supporting Methods

    public void showInviteFriendsActivity(View view){
        Intent f_intent = new Intent(getActivity(), InviteFriendsActivity.class);
        startActivity(f_intent);

    }

    public void showFriendProfileActivity(View view, User clickedUser){
        Intent intent = new Intent(getActivity(), FriendProfileActivity.class);
        intent.putExtra("userObj", clickedUser);
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
        c.setFirstName("Dumb");
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
}
