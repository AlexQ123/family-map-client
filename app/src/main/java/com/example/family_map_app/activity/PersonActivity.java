package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import com.example.family_map_app.R;
import com.example.family_map_app.serverdata.DataCache;

import model.*;

public class PersonActivity extends AppCompatActivity {

    public static final String PERSON_ID_KEY = "PersonIDTextKey";

    private Person currentPerson;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView genderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        Intent intent = getIntent();
        DataCache dataCache = DataCache.getInstance();
        currentPerson = dataCache.getPeopleByID().get(intent.getStringExtra(PERSON_ID_KEY));

        firstNameText = findViewById(R.id.personFirstName);
        firstNameText.setText(currentPerson.getFirstName());
        lastNameText = findViewById(R.id.personLastName);
        lastNameText.setText(currentPerson.getLastName());
        genderText = findViewById(R.id.personGender);
        if (currentPerson.getGender().equals("m")) {
            genderText.setText(R.string.personMale);
        }
        else {
            genderText.setText(R.string.personFemale);
        }
    }

}