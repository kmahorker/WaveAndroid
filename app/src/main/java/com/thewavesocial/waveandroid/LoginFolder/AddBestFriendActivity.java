package com.thewavesocial.waveandroid.LoginFolder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.BestFriend;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.HomeSwipeActivity;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import org.w3c.dom.Text;

public class AddBestFriendActivity extends AppCompatActivity {
    ActionBar actionBar;
    EditText phoneNumberEditText;
    KeyListener phoneTextKeyListener;
    String name = "";
    String phoneNumber = "0";
    TextView doneTextView;
    TextView skipTextView;
    boolean contact = false;
    final Activity thisActivity = this;
    private static final int RESULT_PICK_CONTACT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_best_friend);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_add_best_friend);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(thisActivity);
            }
        });
        setUpActionBar();
        setUpTextViews();
        setUpEditText();
    }
    private void setUpActionBar(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_addbestfriend);
    }

    private void setUpTextViews(){
        skipTextView = (TextView)findViewById(R.id.skipTextView);

        doneTextView = (TextView) findViewById(R.id.doneTextView);
        doneTextView.setEnabled(false);

        final Intent intent = new Intent(this, HomeSwipeActivity.class);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(thisActivity);
                startActivity(intent);
            }
        });

        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(thisActivity);
                if(contact) {
                    //TODO: Update CurrentUser object with new BestFriend Object including name and phonenumber
                    // CurrentUser.theUser.getBestFriends().add(new BestFriend(name, phoneNumber));
                }
                else{
                    //TODO: Update Current User object with phoneNumberEditText.text
                }
                startActivity(intent);
            }
        });
    }
    private void setUpEditText(){
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        //phoneTextKeyListener = phoneNumberEditText.getKeyListener();
        phoneNumberEditText.setKeyListener(null);

        phoneNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (phoneNumberEditText.getRight() - phoneNumberEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //fire contact picker
                        if(phoneNumberEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getConstantState().equals(getResources().getDrawable(R.drawable.plus_button).getConstantState())){ //TODO: Change with actual pics
                            UtilityClass.hideKeyboard(thisActivity);
                            pickContact(v);
                        }
                        /*else{
                            UtilityClass.hideKeyboard(thisActivity);
                            phoneNumberEditText.clearFocus();
                            phoneNumberEditText.setText("");
                            phoneNumberEditText.setKeyListener(phoneTextKeyListener);
                            drawableToPlus();
                            disableDoneTextView();
                            contact = false;
                        }*/

                        return true;
                    }
                }
                return false;
            }
        });

        /*phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    enableDoneTextView();
                    drawableToDelete();
                }
                else{
                    disableDoneTextView();
                    drawableToPlus();
                }
            }
        });*/

    }

    public void pickContact(View v)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("AddBestFriendActivity", "Failed to pick contact");
        }
    }

    private void contactPicked(Intent data){
        Cursor cursor = null;
        try{
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNumber = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            phoneNumberEditText.setText(name);
            phoneNumberEditText.setKeyListener(null);
            drawableToDelete();
            enableDoneTextView();
            contact = true;
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void disableDoneTextView(){
        doneTextView.setTextColor(getResources().getColor(R.color.cardview_dark_background));
        doneTextView.setEnabled(false);
    }

    private void enableDoneTextView(){
        doneTextView.setTextColor(getResources().getColor(R.color.appColor));
        doneTextView.setEnabled(true);
    }

    private void drawableToDelete(){
        phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.cross_button, 0);
    }

    private void drawableToPlus(){
        phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.plus_button, 0);
    }


}
