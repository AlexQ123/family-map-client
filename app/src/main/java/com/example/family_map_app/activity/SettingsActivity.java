package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.example.family_map_app.R;
import com.example.family_map_app.serverdata.DataCache;

public class SettingsActivity extends AppCompatActivity {

    private Switch lifeStorySwitch;
    private Switch familyTreeSwitch;
    private Switch spouseSwitch;

    private Switch fatherSwitch;
    private Switch motherSwitch;
    private Switch maleSwitch;
    private Switch femaleSwitch;

    private RelativeLayout logoutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DataCache dataCache = DataCache.getInstance();
        lifeStorySwitch = findViewById(R.id.lifeStorySwitch);
        lifeStorySwitch.setChecked(dataCache.isLifeStorySwitched());
        familyTreeSwitch = findViewById(R.id.familyTreeSwitch);
        familyTreeSwitch.setChecked(dataCache.isFamilyTreeSwitched());
        spouseSwitch = findViewById(R.id.spouseSwitch);
        spouseSwitch.setChecked(dataCache.isSpouseSwitched());
        fatherSwitch = findViewById(R.id.fatherSwitch);
        fatherSwitch.setChecked(dataCache.isFatherSwitched());
        motherSwitch = findViewById(R.id.motherSwitch);
        motherSwitch.setChecked(dataCache.isMotherSwitched());
        maleSwitch = findViewById(R.id.maleSwitch);
        maleSwitch.setChecked(dataCache.isMaleSwitched());
        femaleSwitch = findViewById(R.id.femaleSwitch);
        femaleSwitch.setChecked(dataCache.isFemaleSwitched());
        logoutLayout = findViewById(R.id.logoutLayout);

        lifeStorySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setLifeStorySwitched(b);
            }
        });

        familyTreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFamilyTreeSwitched(b);
            }
        });

        spouseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setSpouseSwitched(b);
            }
        });

        fatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFatherSwitched(b);
            }
        });

        motherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setMotherSwitched(b);
            }
        });

        maleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setMaleSwitched(b);
            }
        });

        femaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dataCache.setFemaleSwitched(b);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCache.setInstance(null);
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

}