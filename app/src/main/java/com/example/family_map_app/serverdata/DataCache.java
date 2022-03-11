package com.example.family_map_app.serverdata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;
import java.util.ArrayList;

import model.*;

public class DataCache {

    // DataCache singleton
    private static DataCache instance;
    private DataCache() {}
    public static DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    // The current user's persons and events
    private final ArrayList<Person> persons = new ArrayList<>();
    private final ArrayList<Event> events = new ArrayList<>();

    // Authorization token for this current session
    private AuthToken authToken;

    // The person of the current user
    private Person user;

    // Useful filters
    // TODO: populate these filtered data structures using the persons and events ArrayLists
    private final Map<String, Person> peopleByID = new HashMap<>();
    private final Map<String, List<Person>> childrenByParentID = new HashMap<>();
    private final Map<String, SortedSet<Event>> eventsByPersonID = new HashMap<>();
    private final Comparator<Event> eventComparator = new Comparator<Event>() {
        @Override
        public int compare(Event event, Event t1) {
            return 0;
        }
    };
    private final Set<String> eventTypes = new HashSet<>();

    private final Set<Person> fatherSideMales = new HashSet<>();
    private final Set<Person> fatherSideFemales = new HashSet<>();
    private final Set<Person> motherSideMales = new HashSet<>();
    private final Set<Person> motherSideFemales = new HashSet<>();

}
