package com.thewavesocial.waveandroid.LoginFolder;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
    private static final int RESULT_PICK_CONTACT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_best_friend);
        setUpActionBar();
        setUpEditText();


    }
    private void setUpActionBar(){
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.actionbar_addbestfriend);
        skipTextView = (TextView)findViewById(R.id.skipTextView);
        doneTextView = (TextView) findViewById(R.id.doneTextView);
        doneTextView.setEnabled(false);
        final Intent intent = new Intent(this, HomeSwipeActivity.class);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Update CurrentUser object with new BestFriend Object including name and phonenumber
                // CurrentUser.theUser.getBestFriends().add(new BestFriend(name, phoneNumber));
                startActivity(intent);
            }
        });

    }
    private void setUpEditText(){
        phoneNumberEditText = (EditText) findViewById(R.id.phoneNumberEditText);
        phoneTextKeyListener = phoneNumberEditText.getKeyListener();
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
                        if(phoneNumberEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getConstantState().equals(getResources().getDrawable(R.drawable.plus_sign).getConstantState())){ //TODO: Change with actual pics
                            pickContact(v);
                        }
                        else{
                            phoneNumberEditText.setText("");
                            phoneNumberEditText.setKeyListener(phoneTextKeyListener);
                            phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.plus_sign, 0);//TODO: Change with actual pics
                            doneTextView.setTextColor(getResources().getColor(R.color.cardview_dark_background));
                            doneTextView.setEnabled(false);
                        }

                        return true;
                    }
                }
                return false;
            }
        });

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
            phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.happy_house, 0); //TODO: Change with actual pics
            doneTextView.setTextColor(getResources().getColor(R.color.appColor));
            doneTextView.setEnabled(true);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}
