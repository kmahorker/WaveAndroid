package com.thewavesocial.waveandroid.LoginFolder;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.thewavesocial.waveandroid.R;
import com.thewavesocial.waveandroid.UtilityClass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

public class SignupFragment extends Fragment
{
    private int fragNum;
    private View view;
    private SignupActivity mainActivity;
    public final static int ADD_PROFILEPIC_INTENT_ID = 5;

    public SignupFragment()
    {
        fragNum = 0;
    }

    public SignupFragment (int fragNum)
    {
        this.fragNum = fragNum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mainActivity = (SignupActivity)getActivity();
        if ( fragNum == 0 )
        {
            view = inflater.inflate(R.layout.signup_layout1, container, false);
            setupReferences1();
        }
        else if ( fragNum == 1 )
        {
            view = inflater.inflate(R.layout.signup_layout2, container, false);
            setupReferences2();
        }
        else if ( fragNum == 2 )
        {
            view = inflater.inflate(R.layout.signup_layout3, container, false);
            setupReferences3();
        }
        else if ( fragNum == 3 )
        {
            view = inflater.inflate(R.layout.signup_layout4, container, false);
            setupReferences4();
        }
        else if ( fragNum == 4)
        {
            view = inflater.inflate(R.layout.signup_layout5, container, false);
            setupReferences5();
        }
        else
        {
            view = inflater.inflate(R.layout.signup_layout6, container, false);
            setupReferences6();
        }
        return view;
    }

    private void setupReferences1()
    {
        final EditText emailField = (EditText) view.findViewById(R.id.signup1_edittext_email);
        Button nextButton = (Button) view.findViewById(R.id.signup1_button_next);

        emailField.setText(mainActivity.email);

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );
                mainActivity.email = emailField.getText().toString();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.email = emailField.getText().toString();
                return true;
            }
        });
    }

    private void setupReferences2()
    {
        final EditText newPassField = (EditText) view.findViewById(R.id.signup2_editttext_createPass);
        final EditText confirmField = (EditText) view.findViewById(R.id.signup2_editttext_confirmPass);
        Button nextButton = (Button) view.findViewById(R.id.signup2_button_next);

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );
                mainActivity.password = newPassField.getText().toString();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.password = newPassField.getText().toString();
                return true;
            }
        });
    }

    private void setupReferences3()
    {
        final EditText month = (EditText) view.findViewById(R.id.signup3_edittext_month);
        final EditText date = (EditText) view.findViewById(R.id.signup3_edittext_date);
        final EditText year = (EditText) view.findViewById(R.id.signup3_edittext_year);
        final Button nextButton = (Button) view.findViewById(R.id.signup3_button_next);

        if ( mainActivity.birthday != Calendar.getInstance() )
        {
            month.setText(mainActivity.birthday.get(Calendar.MONTH) + "");
            date.setText(mainActivity.birthday.get(Calendar.DATE) + "");
            year.setText(mainActivity.birthday.get(Calendar.YEAR) + "");
        }

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );

                if ( !year.getText().toString().equals("") && !month.getText().toString().equals("") &&
                        !date.getText().toString().equals("") )
                {
                    mainActivity.birthday.set(Integer.parseInt(year.getText().toString()),
                            Integer.parseInt(month.getText().toString()),
                            Integer.parseInt(date.getText().toString()));
                }
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                if ( !year.getText().toString().equals("") && !month.getText().toString().equals("") &&
                        !date.getText().toString().equals("") )
                {
                    mainActivity.birthday.set(Integer.parseInt(year.getText().toString()),
                            Integer.parseInt(month.getText().toString()),
                            Integer.parseInt(date.getText().toString()));
                }
                Log.d("Year", year.getText() + " " + mainActivity.birthday.get(Calendar.YEAR));
                return true;
            }
        });
    }

    private void setupReferences4()
    {
        final Button male = (Button) view.findViewById(R.id.signup4_button_male);
        final Button female = (Button) view.findViewById(R.id.signup4_button_female);

        if ( mainActivity.gender.equals("male") )
        {
            male.setBackgroundResource(R.drawable.round_button_yellow);
            female.setBackgroundResource(R.drawable.round_button);
        }
        else if ( mainActivity.gender.equals("female") )
        {
            female.setBackgroundResource(R.drawable.round_button_yellow);
            male.setBackgroundResource(R.drawable.round_button);
        }

        male.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );
                mainActivity.gender = "male";
                male.setBackgroundResource(R.drawable.round_button_yellow);
                female.setBackgroundResource(R.drawable.round_button);
            }
        });

        female.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );
                mainActivity.gender = "female";
                female.setBackgroundResource(R.drawable.round_button_yellow);
                male.setBackgroundResource(R.drawable.round_button);
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                return true;
            }
        });
    }

    private void setupReferences5()
    {
        final ImageView profilepic = (ImageView) view.findViewById(R.id.signup5_button_addpic);
        final Button nextButton = (Button) view.findViewById(R.id.signup5_button_next);

        if ( mainActivity.profilePic != null )
        {
            profilepic.setImageDrawable(mainActivity.profilePic);
        }

        profilepic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, ADD_PROFILEPIC_INTENT_ID);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mainActivity.mPager.setCurrentItem( mainActivity.mPager.getCurrentItem() + 1 );
                // TODO: 02/22/2017 Save Image
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                // TODO: 02/22/2017 Save Image
                return true;
            }
        });
    }

    private void setupReferences6()
    {
        final EditText friendname = (EditText) view.findViewById(R.id.signup6_edittext_friendname);
        final EditText friendphone = (EditText) view.findViewById(R.id.signup6_edittext_friendphone);
        final Button finishButton = (Button) view.findViewById(R.id.signup6_button_finish);

        finishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.friendname = friendname.getText().toString();
                if ( !friendphone.getText().toString().equals("") )
                    mainActivity.friendphone = Long.parseLong(friendphone.getText().toString());
                mainActivity.saveUserData();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                UtilityClass.hideKeyboard(mainActivity);
                mainActivity.friendname = friendname.getText().toString();
                if ( !friendphone.getText().toString().equals("") )
                    mainActivity.friendphone = Long.parseLong(friendphone.getText().toString());
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //http://stackoverflow.com/questions/9107900/how-to-upload-image-from-gallery-in-android
        if(requestCode==ADD_PROFILEPIC_INTENT_ID && resultCode == Activity.RESULT_OK)
        {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                ImageView profilepic = (ImageView) view.findViewById(R.id.signup5_button_addpic);
                profilepic.setImageDrawable( UtilityClass.toRoundImage(getResources(), bitmap) );
                mainActivity.profilePic = new BitmapDrawable(bitmap);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
