package com.thewavesocial.waveandroid.HomeFolder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thewavesocial.waveandroid.AdaptersFolder.MyEventsCustomAdapter;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.DummyUser;
import com.thewavesocial.waveandroid.BusinessObjects.Party;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyEventsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyEventsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyEventsFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param partyList List of parties user is attending
//     * @return A new instance of fragment MyEventsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MyEventsFragment newInstance(ArrayList<Party> partyList) {
//        MyEventsFragment fragment = new MyEventsFragment();
//        Bundle args = new Bundle();
//        args.putParcelableArrayList(ARG_PARAM1, partyList);
//        fragment.setArguments(args);
//        return fragment;
//    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_events_find, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        MyEventsFragment thisFragment = this;
        DummyUser dummyUser = new DummyUser(getActivity());
        ((HomeSwipeActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((HomeSwipeActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeSwipeActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_my_events);
        List<Party> partyList = CurrentUser.getPartyListObjects(dummyUser.getAttending()); //TODO: Get Parties from User Object from database
        ListView myEventsList = (ListView) getActivity().findViewById(R.id.myEventsListView);
        myEventsList.setAdapter(new MyEventsCustomAdapter(getActivity(),thisFragment,partyList));

    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showPartyProfilePage(Party clickedParty){
        Intent intent = new Intent(getActivity(), PartyProfileFragment.class);
        intent.putExtra("partyIDLong", clickedParty.getPartyID());
        startActivity(intent);
    }
}
