package com.example.family_map_app.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap map;
    private Map<String, Float> colorsForEventTypes = new HashMap<>();

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        addMarkers(googleMap);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Event event = (Event)marker.getTag();
                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(event.getLatitude(),
                        event.getLongitude())));
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

    private void addMarkers(GoogleMap map) {
        DataCache dataCache = DataCache.getInstance();
        dataCache.fillEventTypes();

        Set<String> eventTypes = dataCache.getEventTypes();
        Float hue = 0.0f;
        for (String type : eventTypes) {
            colorsForEventTypes.put(type, hue);
            hue = (hue + 30.0f) % 330.0f;
            if (hue == 90.0f) {
                hue += 30.0f;
            }
        }

        ArrayList<Event> events = dataCache.getEvents();
        for (Event event : events) {
            float correspondingHue = colorsForEventTypes.get(event.getEventType().toLowerCase());

            Marker marker = map.addMarker(new MarkerOptions().
                    position(new LatLng(event.getLatitude(), event.getLongitude())).
                    icon(BitmapDescriptorFactory.defaultMarker(correspondingHue)));
            marker.setTag(event);
        }
    }

}