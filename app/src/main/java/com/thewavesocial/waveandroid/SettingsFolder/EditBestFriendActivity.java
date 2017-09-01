package com.thewavesocial.waveandroid.SettingsFolder;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thewavesocial.waveandroid.BusinessObjects.BestFriend;
import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.DatabaseObjects.OnResultReadyListener;
import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.util.ArrayList;
import java.util.List;

import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_addBestFriend;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_deleteBestFriend;
import static com.thewavesocial.waveandroid.DatabaseObjects.DatabaseAccess.server_getBestFriends;

public class EditBestFriendActivity extends AppCompatActivity {

    private TextView doneTextView;
    private ImageView backImageView;
    private EditText phoneNumberEditText;

    Activity thisActivity = this;

    String name = "";
    String phoneNumber = "0";
    boolean contact = false;

    private final int RESULT_PICK_CONTACT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_best_friend);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_editbestfriend);

        doneTextView = (TextView) findViewById(R.id.edit_doneTextView);
        backImageView = (ImageView) findViewById(R.id.edit_backImageView);
        phoneNumberEditText = (EditText) findViewById(R.id.edit_phoneNumberEditText);

        doneTextView.setTextColor(getResources().getColor(R.color.grey_default));


        server_getBestFriends(CurrentUser.getUser().getId(), new OnResultReadyListener<List<BestFriend>>() {
            @Override
            public void onResultReady(List<BestFriend> result) {
                if(result != null){
                    if(result.size() == 1){
                        name = result.get(0).getName();
                        phoneNumber = result.get(0).getPhoneNumber();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!name.isEmpty()){
                                    phoneNumberEditText.setText(name);
                                    drawableToDelete();
                                }
                                else if(!phoneNumber.equals("0")){
                                    phoneNumberEditText.setText(phoneNumber);
                                    drawableToDelete();
                                }
                            }
                        });
                    }
                }
            }
        });


        setupFunctionalities();

    }

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

    private void setupFunctionalities() {

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertMessage = new AlertDialog.Builder(thisActivity);
                alertMessage.setTitle("Warning")
                        .setMessage("Your changes will not be saved. Are you sure you want to continue?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .setCancelable(true)
                        .show();
            }
        });



        doneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityClass.hideKeyboard(thisActivity);
                if (contact) {
                    final List<BestFriend> bestFriends = new ArrayList<BestFriend>();
                    server_getBestFriends(CurrentUser.getUser().getId(), new OnResultReadyListener<List<BestFriend>>() {
                        @Override
                        public void onResultReady(List<BestFriend> result) {
                            boolean duplicate = false;
                            bestFriends.addAll(result);
                            for (BestFriend bf : result) {
                                if (bf.getPhoneNumber().equals(phoneNumber)) {
                                    duplicate = true;
                                    break; //no need to check others once one duplicate found
                                }
                            }
                            if (!duplicate) { //Add best friend to server if not a duplicate
                                if(bestFriends.size() == 1) {
                                    server_deleteBestFriend(CurrentUser.getUser().getId(), bestFriends.get(0).getPhoneNumber(), new OnResultReadyListener<String>() {
                                        @Override
                                        public void onResultReady(String result) {
                                            if (result.equals("success")) {
                                                //good
                                            } else {
                                                //error
                                            }
                                        }
                                    });
                                }
                                server_addBestFriend(name, phoneNumber, CurrentUser.getUser().getId(), new OnResultReadyListener<String>() {
                                    @Override
                                    public void onResultReady(String result) {
                                        if (result.equals("success")) {
                                            // CurrentUser.user.getBestFriends().add(new BestFriend(name, phoneNumber));
                                            onBackPressed();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error adding a best friend", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });



                            } else { //Alert that this is a duplicate contact

                                final AlertDialog.Builder confirmMessage = new AlertDialog.Builder(thisActivity);
                                confirmMessage.setTitle("Duplicate Contact")
                                        .setMessage("This contact is already in your best friend's list!")
                                        .setCancelable(true)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .show();

                            }
                        }
                    });

                } else {
                    doneTextView.setTextColor(getResources().getColor(R.color.grey_default));
                    //TODO: When contact is manually entered
                }

            }
        });

        phoneNumberEditText.setKeyListener(null);

        phoneNumberEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (phoneNumberEditText.getRight() - phoneNumberEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        //fire contact picker
                        if (phoneNumberEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getConstantState().equals(getResources().getDrawable(R.drawable.plus_button_small).getConstantState())) { //TODO: Change with actual pics
                            UtilityClass.hideKeyboard(thisActivity);
                            pickContact(v);
                        } else {
                            UtilityClass.hideKeyboard(thisActivity);
                            phoneNumberEditText.clearFocus();
                            phoneNumberEditText.setText("");
                            //phoneNumberEditText.setKeyListener(phoneTextKeyListener);
                            drawableToPlus();
                            contact = false;
                        }

                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void pickContact(View v) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            doneTextView.setTextColor(getResources().getColor(R.color.appColor));

            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String numberText = cursor.getString(phoneIndex);
            phoneNumber = numberText.replaceAll("\\D+", "");
            name = cursor.getString(nameIndex);
            phoneNumberEditText.setText(name);
            phoneNumberEditText.setKeyListener(null);
            drawableToDelete();
            contact = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawableToDelete() {
        phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cross_button, 0);
    }

    private void drawableToPlus() {
        phoneNumberEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_button_small, 0);
    }

}
