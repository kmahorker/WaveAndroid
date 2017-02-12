package com.thewavesocial.waveandroid;

import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CreateEventActivity extends AppCompatActivity
{

    private int maleNum, femaleNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_fragment1);

        setupActionBar();
        setupOnClicks();
    }

    private void setupOnClicks()
    {
        final TextView privateText = (TextView) findViewById(R.id.createEvent1_private_text);
        final TextView publicText = (TextView) findViewById(R.id.createEvent1_public_text);
        final TextView paidText = (TextView) findViewById(R.id.createEvent1_paid_text);
        final TextView freeText = (TextView) findViewById(R.id.createEvent1_free_text);
        final ImageView maleMinus = (ImageView) findViewById(R.id.createEvent1_maleMinus_image);
        final ImageView malePlus = (ImageView) findViewById(R.id.createEvent1_malePlus_image);
        final ImageView femaleMinus = (ImageView) findViewById(R.id.createEvent1_femaleMinus_image);
        final ImageView femalePlus = (ImageView) findViewById(R.id.createEvent1_femalePlus_image);
        final TextView maleCount = (TextView)findViewById(R.id.createEvent1_maleCount_text);
        final TextView femaleCount = (TextView)findViewById(R.id.createEvent1_femaleCount_text);
        maleNum = Integer.parseInt(maleCount.getText().toString());
        femaleNum = Integer.parseInt(femaleCount.getText().toString());



        privateText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                privateText.setTextColor(Color.WHITE);
                privateText.setBackgroundResource(R.color.appColor);
                publicText.setTextColor(getResources().getColor(R.color.appColor));
                publicText.setBackgroundColor(Color.WHITE);
            }
        });

        publicText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                publicText.setTextColor(Color.WHITE);
                publicText.setBackgroundResource(R.color.appColor);
                privateText.setTextColor(getResources().getColor(R.color.appColor));
                privateText.setBackgroundColor(Color.WHITE);
            }
        });

        paidText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                paidText.setTextColor(Color.WHITE);
                paidText.setBackgroundResource(R.color.appColor);
                freeText.setTextColor(getResources().getColor(R.color.appColor));
                freeText.setBackgroundColor(Color.WHITE);
            }
        });

        freeText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                freeText.setTextColor(Color.WHITE);
                freeText.setBackgroundResource(R.color.appColor);
                paidText.setTextColor(getResources().getColor(R.color.appColor));
                paidText.setBackgroundColor(Color.WHITE);
            }
        });

        maleMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                maleNum--;
                maleCount.setText(maleNum+"");
            }
        });

        malePlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                maleNum++;
                maleCount.setText(maleNum+"");
            }
        });

        femaleMinus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                femaleNum--;
                femaleCount.setText(femaleNum+"");
            }
        });

        femalePlus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                femaleNum++;
                femaleCount.setText(femaleNum+"");
            }
        });

    }

    private void setupActionBar()
    {
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_create_event1);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView forwardButton = (ImageView) findViewById(R.id.actionbar_createEvent1_image);
        forwardButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FragmentManager fragM = getSupportFragmentManager();
                fragM.beginTransaction().replace(R.id.create_event_fragment1, new CreateEvent2Fragment()).commit();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
