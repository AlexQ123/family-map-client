package com.example.family_map_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.family_map_app.R;
import com.example.family_map_app.serverdata.DataCache;

import java.util.ArrayList;

import model.*;

public class PersonActivity extends AppCompatActivity {

    public static final String PERSON_ID_KEY = "PersonIDTextKey";

    private Person currentPerson;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView genderText;
    private ExpandableListView expandableList;

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

        expandableList = findViewById(R.id.expandableListView);
        DataCache instance = DataCache.getInstance();
        ArrayList<Event> events = instance.getEventsByPersonID().get(currentPerson.getPersonID());
        ArrayList<Event> sortedEvents = sortEvents(events);
        ArrayList<Person> persons = instance.getImmediateFamily().get(currentPerson.getPersonID());
        expandableList.setAdapter(new ExpandableListAdapter(sortedEvents, persons, currentPerson));
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

    private ArrayList<Event> sortEvents(ArrayList<Event> toSort) {
        ArrayList<Event> sorted = new ArrayList<>();
        ArrayList<Event> unsorted = new ArrayList<>(toSort);
        while (unsorted.size() > 0) {
            Event minEvent = unsorted.get(0);
            int deleteIndex = 0;
            for (int i = 0; i < unsorted.size(); i++) {
                if (unsorted.get(i).getYear() < minEvent.getYear()) {
                    minEvent = unsorted.get(i);
                    deleteIndex = i;
                }
            }
            sorted.add(minEvent);
            unsorted.remove(deleteIndex);
        }
        return sorted;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_GROUP_POSITION = 0;
        private static final int FAMILY_GROUP_POSITION = 1;

        private final ArrayList<Event> events;
        private final ArrayList<Person> family;
        private final Person clickedPerson;

        ExpandableListAdapter(ArrayList<Event> events, ArrayList<Person> family, Person clickedPerson) {
            this.events = events;
            this.family = family;
            this.clickedPerson = clickedPerson;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    return events.size();
                case FAMILY_GROUP_POSITION:
                    return family.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition == EVENT_GROUP_POSITION) {
                return events;
            }
            else {
                return family;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (groupPosition == EVENT_GROUP_POSITION) {
                return events.get(childPosition);
            }
            else {
                return family.get(childPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    titleView.setText("LIFE EVENTS");
                    break;
                case FAMILY_GROUP_POSITION:
                    titleView.setText("FAMILY");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch (groupPosition) {
                case EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case FAMILY_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                    initializeFamilyView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventView, final int childPosition) {
            ImageView eventImage = eventView.findViewById(R.id.listItemIcon);
            eventImage.setImageResource(R.drawable.ic_location_dot_solid);

            Event currentEvent = events.get(childPosition);

            TextView eventInfo = eventView.findViewById(R.id.listItemTop);
            String info = currentEvent.getEventType() + ": " + currentEvent.getCity() + ", " + currentEvent.getCountry() +
                    "(" + currentEvent.getYear() + ")";
            eventInfo.setText(info);

            TextView personOfEvent = eventView.findViewById(R.id.listItemBottom);
            DataCache dataCache = DataCache.getInstance();
            Person thisPerson = dataCache.getPeopleByID().get(currentEvent.getPersonID());
            String person = thisPerson.getFirstName() + " " + thisPerson.getLastName();
            personOfEvent.setText(person);

            LinearLayout list = eventView.findViewById(R.id.list);
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                    intent.putExtra(EventActivity.EVENT_ID_KEY, currentEvent.getEventID());
                    startActivity(intent);
                }
            });
        }

        private void initializeFamilyView(View familyView, final int childPosition) {
            Person currentPerson = family.get(childPosition);
            ImageView personImage = familyView.findViewById(R.id.listItemIcon);
            if (currentPerson.getGender().equals("m")) {
                personImage.setImageResource(R.drawable.ic_person_solid);
            }
            else {
                personImage.setImageResource(R.drawable.ic_person_dress_solid);
            }

            TextView personName = familyView.findViewById(R.id.listItemTop);
            String name = currentPerson.getFirstName() + " " + currentPerson.getLastName();
            personName.setText(name);

            TextView relationship = familyView.findViewById(R.id.listItemBottom);
            String relation;
            if (currentPerson.getPersonID().equals(clickedPerson.getFatherID())) {
                relation = "Father";
            }
            else if (currentPerson.getPersonID().equals(clickedPerson.getMotherID())) {
                relation = "Mother";
            }
            else if (currentPerson.getPersonID().equals(clickedPerson.getSpouseID())) {
                relation = "Spouse";
            }
            else {
                relation = "Child";
            }
            relationship.setText(relation);

            LinearLayout list = familyView.findViewById(R.id.list);
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_ID_KEY, currentPerson.getPersonID());
                    startActivity(intent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

}