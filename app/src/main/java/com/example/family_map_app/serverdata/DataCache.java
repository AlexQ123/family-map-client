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
    private ArrayList<Person> persons = new ArrayList<>();
    private ArrayList<Event> events = new ArrayList<>();

    // Authorization token for this current session
    private String authToken;

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

    // getters and setters

    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> persons) {
        this.persons = persons;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public Map<String, Person> getPeopleByID() {
        return peopleByID;
    }

    public Map<String, List<Person>> getChildrenByParentID() {
        return childrenByParentID;
    }

    public Map<String, SortedSet<Event>> getEventsByPersonID() {
        return eventsByPersonID;
    }

    public Comparator<Event> getEventComparator() {
        return eventComparator;
    }

    public Set<String> getEventTypes() {
        return eventTypes;
    }

    public Set<Person> getFatherSideMales() {
        return fatherSideMales;
    }

    public Set<Person> getFatherSideFemales() {
        return fatherSideFemales;
    }

    public Set<Person> getMotherSideMales() {
        return motherSideMales;
    }

    public Set<Person> getMotherSideFemales() {
        return motherSideFemales;
    }
}
