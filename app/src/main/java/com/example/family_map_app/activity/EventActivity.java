package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import com.example.family_map_app.R;
import com.example.family_map_app.activity.fragment.MapFragment;
import com.example.family_map_app.serverdata.DataCache;

import model.*;

public class EventActivity extends AppCompatActivity {

    public static final String EVENT_ID_KEY = "EventIDTextKey";

    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = getIntent();
        DataCache dataCache = DataCache.getInstance();
        currentEvent = dataCache.getEventByID().get(intent.getStringExtra(EVENT_ID_KEY));

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment(currentEvent);
        fragmentManager.beginTransaction().add(R.id.fragmentFrameLayout, fragment).commit();
    }
}