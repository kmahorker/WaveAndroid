package com.thewavesocial.waveandroid.LoginFolder;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thewavesocial.waveandroid.R;



public class LoginTutorialFragment extends Fragment {
    private int fragNum;
    private View view;
    private LoginTutorialActivity mainActivity;
    private ImageView largeTutorialImage, tutorialIcon;
    private TextView tutorialLargeText, tutorialDescription;
    public final static int ADD_PROFILEPIC_INTENT_ID = 5;

    public LoginTutorialFragment() {
       fragNum = 1;
    }

    public LoginTutorialFragment(int fragNum) {
        this.fragNum = fragNum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       // view = inflater.inflate(R.layout.fragment_login_tutorial, container, false);

      //  view = inflater.inflate(R.layout.fragment_login_tutorial, container, false);
        if (fragNum == 1) {
            //changeInfo1();
            view = inflater.inflate(R.layout.fragment_login_tutorial, container, false);
            //setupReferences();

        } else if (fragNum == 2) {
           // setupReferences();
            //changeInfo2();
            view = inflater.inflate(R.layout.fragment_login_tutorial_2, container, false);
            //setupReferences2();


        } else if (fragNum == 3) {
            //setupReferences();
            //changeInfo3();
            view = inflater.inflate(R.layout.fragment_login_tutorial_3, container, false);

        }
        //view = inflater.inflate(R.layout.fragment_login_tutorial, container, false);
        return view;
    }



    //Unused Methods

//    private void setupReferences(){
//        largeTutorialImage = (ImageView)mainActivity.findViewById(R.id.tutorialBigImage);
//        tutorialIcon = (ImageView)mainActivity.findViewById(R.id.tutorialIcon);
//        tutorialLargeText = (TextView)mainActivity.findViewById(R.id.tutorialLargeText);
//        tutorialDescription = (TextView)mainActivity.findViewById(R.id.tutorialDescription);
//    }
//
//    private void changeInfo1(){
//        tutorialIcon.setImageResource(R.drawable.tutorial_find_button);
//        tutorialLargeText.setText("Find Parties Near You");
//        tutorialDescription.setText("Just tap on a house icon to check it out. You can see who's going " +
//                "to an event and when it's going down.");
//
//
//    }
//
//    private void changeInfo2(){
//        tutorialIcon.setImageResource(R.drawable.tutorial_search_icon);
//        tutorialLargeText.setText("Search for a friend or an event");
//        tutorialDescription.setText("Use the Search Box to find and follow a friend. Tap on the Events " +
//                "box to find events happening near you.");
//
//    }
//
//    private void changeInfo3(){
//        tutorialIcon.setImageResource(R.drawable.tutorial_host_icon);
//        tutorialLargeText.setText("Host parties and invite friends");
//        tutorialDescription.setText("Just tap on a house icon to see who's going to a party" +
//                " and when it's getting shut down");
//    }
//
//    public ImageView getLargeTutorialImage() {
//        return largeTutorialImage;
//    }
//
//    public void setLargeTutorialImage(int largeTutorialImage) {
//        this.largeTutorialImage.setImageResource(largeTutorialImage);
//    }
//
//    public ImageView getTutorialIcon() {
//        return tutorialIcon;
//    }
//
//    public void setTutorialIcon(int tutorialIcon) {
//        this.tutorialIcon.setImageResource(tutorialIcon);
//    }
//
//    public TextView getTutorialLargeText() {
//        return tutorialLargeText;
//    }
//
//    public void setTutorialLargeText(String tutorialLargeText) {
//        this.tutorialLargeText.setText(tutorialLargeText);
//    }
//
//    public TextView getTutorialDescription() {
//        return tutorialDescription;
//    }
//
//    public void setTutorialDescription(String tutorialDescription) {
//        this.tutorialDescription.setText(tutorialDescription);
//    }
}
