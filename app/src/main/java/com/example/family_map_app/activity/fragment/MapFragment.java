package com.example.family_map_app.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Intent;

import com.example.family_map_app.activity.PersonActivity;
import com.example.family_map_app.activity.SearchActivity;
import com.example.family_map_app.activity.SettingsActivity;
import com.example.family_map_app.serverdata.DataCache;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import model.*;

import com.example.family_map_app.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    private Map<String, Float> colorsForEventTypes = new HashMap<>();

    private ImageView icon;
    private TextView nameText;
    private TextView eventText;
    private RelativeLayout bottomLayout;
    private boolean isPersonActivityClickable;
    private String clickedPersonID;

    private Event startEvent;
    private boolean isFromEventActivity = false;

    ArrayList<Polyline> lines = new ArrayList<>();

    public MapFragment() {
        // Required empty public constructor
    }

    public MapFragment(Event startEvent) {
        this.startEvent = startEvent;
        isFromEventActivity = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        if (!isFromEventActivity) {
            setHasOptionsMenu(true);
        }
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        icon = view.findViewById(R.id.icon);
        nameText = view.findViewById(R.id.nameTextView);
        eventText = view.findViewById(R.id.eventTextView);

        if (!isFromEventActivity) {
            icon.setImageResource(R.drawable.ic_android_brands);
            nameText.setText(R.string.nameDefaultText);
            eventText.setText(R.string.eventDefaultText);
            isPersonActivityClickable = false;
        }

        if (isFromEventActivity) {
            isPersonActivityClickable = true;
            clickedPersonID = startEvent.getPersonID();
        }

        bottomLayout = view.findViewById(R.id.bottomRelativeLayout);
        bottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPersonActivityClickable) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    intent.putExtra(PersonActivity.PERSON_ID_KEY, clickedPersonID);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataCache dataCache = DataCache.getInstance();

        ArrayList<Person> personsToMap = new ArrayList<>();
        if (dataCache.isMaleSwitched()) {
            personsToMap.add(dataCache.getPeopleByID().get(dataCache.getUser().getSpouseID()));
            if (dataCache.isFatherSwitched()) {
                personsToMap.addAll(dataCache.getFatherSideMales());
            }
            if (dataCache.isMotherSwitched()) {
                personsToMap.addAll(dataCache.getMotherSideMales());
            }
        }
        if (dataCache.isFemaleSwitched()) {
            personsToMap.add(dataCache.getUser());
            if (dataCache.isFatherSwitched()) {
                personsToMap.addAll(dataCache.getFatherSideFemales());
            }
            if (dataCache.isMotherSwitched()) {
                personsToMap.addAll(dataCache.getMotherSideFemales());
            }
        }

        ArrayList<Event> eventsToMap = new ArrayList<Event>();
        for (Person person : personsToMap) {
            ArrayList<Event> toAdd = dataCache.getEventsByPersonID().get(person.getPersonID());
            eventsToMap.addAll(toAdd);
        }

        dataCache.setEventsToMap(eventsToMap);
        if (map != null) {
            map.clear();
            addMarkers(map);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addMarkers(googleMap);

        if (isFromEventActivity) {
            correctDisplay(startEvent);
            drawLines(startEvent);
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                isPersonActivityClickable = true;

                // adjust camera
                Event event = (Event)marker.getTag();
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(),
                        event.getLongitude())));

                // fill event details
                String eventDetails = event.getEventType() + ": " + event.getCity() + ", " +
                        event.getCountry() + "(" + event.getYear() + ")";
                eventText.setText(eventDetails);

                // fill name details
                DataCache dataCache = DataCache.getInstance();
                clickedPersonID = event.getPersonID();
                Person person = dataCache.getPeopleByID().get(event.getPersonID());
                String nameDetails = person.getFirstName() + " " + person.getLastName();
                nameText.setText(nameDetails);

                // display appropriate icon
                if (person.getGender().equals("m")) {
                    icon.setImageResource(R.drawable.ic_person_solid);
                }
                else {
                    icon.setImageResource(R.drawable.ic_person_dress_solid);
                }

                // draw lines
                drawLines(event);

                return true;
            }
        });
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
        searchMenuItem.setIcon(R.drawable.ic_magnifying_glass_solid);

        MenuItem settingsMenuItem = menu.findItem(R.id.settingsMenuItem);
        settingsMenuItem.setIcon(R.drawable.ic_gear_solid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {
        switch (menu.getItemId()) {
            case R.id.searchMenuItem:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                return true;
            case R.id.settingsMenuItem:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    private void addMarkers(GoogleMap map) {
        DataCache dataCache = DataCache.getInstance();

        Set<String> eventTypes = dataCache.getEventTypes();
        Float hue = 0.0f;
        for (String type : eventTypes) {
            colorsForEventTypes.put(type, hue);
            hue = (hue + 30.0f) % 330.0f;
            if (hue == 90.0f) {
                hue += 30.0f;
            }
        }

        // ArrayList<Event> events = dataCache.getEvents();
        ArrayList<Event> events = dataCache.getEventsToMap();
        for (Event event : events) {
            float correspondingHue = colorsForEventTypes.get(event.getEventType().toLowerCase());

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(correspondingHue)));
            marker.setTag(event);
        }
    }

    private void correctDisplay(Event event) {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(),
                event.getLongitude())));

        String eventDetails = event.getEventType() + ": " + event.getCity() + ", " +
                event.getCountry() + "(" + event.getYear() + ")";
        eventText.setText(eventDetails);

        DataCache dataCache = DataCache.getInstance();
        Person person = dataCache.getPeopleByID().get(startEvent.getPersonID());
        String nameDetails = person.getFirstName() + " " + person.getLastName();
        nameText.setText(nameDetails);

        if (person.getGender().equals("m")) {
            icon.setImageResource(R.drawable.ic_person_solid);
        }
        else {
            icon.setImageResource(R.drawable.ic_person_dress_solid);
        }
    }

    private void drawLines(Event event) {
        clearLines();

        DataCache dataCache = DataCache.getInstance();

        // life story lines
        if (dataCache.isLifeStorySwitched()) {
            ArrayList<Event> lifeEvents = dataCache.getEventsByPersonID().get(event.getPersonID());
            ArrayList<Event> sortedLifeEvents = dataCache.sortEvents(lifeEvents);
            for (int i = 0; i < sortedLifeEvents.size() - 1; i++) {
                drawSingleLine(sortedLifeEvents.get(i), sortedLifeEvents.get(i + 1), Color.GREEN,20);
            }
        }

        // family tree lines
        if (dataCache.isFamilyTreeSwitched()) {
            Person person = dataCache.getPeopleByID().get(event.getPersonID());
            float startingWidth = 20.0f;
            familyTreeHelper(person, event, startingWidth);
        }

        // spouse lines
        if (dataCache.isSpouseSwitched()) {
            Person person = dataCache.getPeopleByID().get(event.getPersonID());
            if (person.getSpouseID() != null) {
                ArrayList<Person> family = dataCache.getImmediateFamily().get(event.getPersonID());
                Person spouse = null;
                for (Person member : family) {
                    if (person.getSpouseID().equals(member.getPersonID())) {
                        spouse = member;
                    }
                }
                ArrayList<Event> spouseEvents = dataCache.getEventsByPersonID().get(spouse.getPersonID());
                Event earliestSpouseEvent = dataCache.sortEvents(spouseEvents).get(0);
                drawSingleLine(event, earliestSpouseEvent, Color.RED, 20);
            }
        }
    }

    private void drawSingleLine(Event startEvent, Event endEvent, int color, float width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions().add(startPoint, endPoint).
                color(color).width(width);
        Polyline line = map.addPolyline(options);
        lines.add(line);
    }

    private void clearLines() {
        for (Polyline line : lines) {
            line.remove();
        }
    }

    private void familyTreeHelper(Person person, Event event, float width) {
        DataCache dataCache = DataCache.getInstance();
        if (person.getFatherID() != null) {
            ArrayList<Person> family = dataCache.getImmediateFamily().get(event.getPersonID());
            Person father = null;
            for (Person member : family) {
                if (person.getFatherID().equals(member.getPersonID())) {
                    father = member;
                }
            }
            ArrayList<Event> fatherEvents = dataCache.getEventsByPersonID().get(father.getPersonID());
            Event earliestFatherEvent = dataCache.sortEvents(fatherEvents).get(0);
            drawSingleLine(event, earliestFatherEvent, Color.BLUE, width);
            familyTreeHelper(father, earliestFatherEvent, width - 5);
        }
        if (person.getMotherID() != null) {
            ArrayList<Person> family = dataCache.getImmediateFamily().get(event.getPersonID());
            Person mother = null;
            for (Person member : family) {
                if (person.getMotherID().equals(member.getPersonID())) {
                    mother = member;
                }
            }
            ArrayList<Event> motherEvents = dataCache.getEventsByPersonID().get(mother.getPersonID());
            Event earliestMotherEvent = dataCache.sortEvents(motherEvents).get(0);
            drawSingleLine(event, earliestMotherEvent, Color.BLUE, width);
            familyTreeHelper(mother, earliestMotherEvent, width - 5);
        }
    }

}