package com.thewavesocial.waveandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thewavesocial.waveandroid.BusinessObjects.CurrentUser;
import com.thewavesocial.waveandroid.BusinessObjects.User;


public class OptionsFragmet extends Fragment
{
    @Nullable
    @Override
    //initialize layout
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.options_layout, container, false);
    }

    @Override
    //initialize everything
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setTitle("");
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((HomeDrawerActivity)getActivity()).getSupportActionBar().setCustomView(R.layout.actionbar_options);

        setupOnClickListeners();
    }

    //set all option onClick events
    private void setupOnClickListeners()
    {
        TextView edit_profile = (TextView) getActivity().findViewById(R.id.options_editprofile_text);
        TextView set_bff = (TextView) getActivity().findViewById(R.id.options_setBestFriend_text);
        TextView change_pswd = (TextView) getActivity().findViewById(R.id.options_changePassword_text);
        TextView push_notif = (TextView) getActivity().findViewById(R.id.options_pushNotification_text);
        TextView manage_pay = (TextView) getActivity().findViewById(R.id.options_managePaymentMethods_text);
        TextView privacy = (TextView) getActivity().findViewById(R.id.options_privacyPolicy_text);
        final TextView terms_service = (TextView) getActivity().findViewById(R.id.options_termsOfService_text);

        edit_profile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), EditUserProfileActivity.class);
                intent.putExtra("myProfileObj", new User());
                startActivity(intent);
            }
        });

        set_bff.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017  
            }
        });

        change_pswd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017  
            }
        });

        push_notif.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017  
            }
        });

        manage_pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017  
            }
        });

        privacy.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017  
            }
        });

        terms_service.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 02/10/2017
            }
        });
    }
}
