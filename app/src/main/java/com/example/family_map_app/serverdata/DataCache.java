package com.example.family_map_app.serverdata;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeSet;

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
    private final Map<String, Person> peopleByID = new HashMap<>();
    private final Map<String, Event> eventByID = new HashMap<>();
    private final Map<String, ArrayList<Person>> childrenByParentID = new HashMap<>();
    private final Map<String, ArrayList<Event>> eventsByPersonID = new HashMap<>();
    private final Map<String, ArrayList<Person>> immediateFamily = new HashMap<>();
    private final Set<String> eventTypes = new HashSet<>();

    private final Set<Person> fatherSideMales = new HashSet<>();
    private final Set<Person> fatherSideFemales = new HashSet<>();
    private final Set<Person> motherSideMales = new HashSet<>();
    private final Set<Person> motherSideFemales = new HashSet<>();

    private boolean isLifeStorySwitched;
    private boolean isFamilyTreeSwitched;
    private boolean isSpouseSwitched;
    private boolean isFatherSwitched;
    private boolean isMotherSwitched;
    private boolean isMaleSwitched;
    private boolean isFemaleSwitched;

    // getters and setters

    public static void setInstance(DataCache instance) {
        DataCache.instance = instance;
    }

    public boolean isLifeStorySwitched() {
        return isLifeStorySwitched;
    }

    public void setLifeStorySwitched(boolean lifeStorySwitched) {
        isLifeStorySwitched = lifeStorySwitched;
    }

    public boolean isFamilyTreeSwitched() {
        return isFamilyTreeSwitched;
    }

    public void setFamilyTreeSwitched(boolean familyTreeSwitched) {
        isFamilyTreeSwitched = familyTreeSwitched;
    }

    public boolean isSpouseSwitched() {
        return isSpouseSwitched;
    }

    public void setSpouseSwitched(boolean spouseSwitched) {
        isSpouseSwitched = spouseSwitched;
    }

    public boolean isFatherSwitched() {
        return isFatherSwitched;
    }

    public void setFatherSwitched(boolean fatherSwitched) {
        isFatherSwitched = fatherSwitched;
    }

    public boolean isMotherSwitched() {
        return isMotherSwitched;
    }

    public void setMotherSwitched(boolean motherSwitched) {
        isMotherSwitched = motherSwitched;
    }

    public boolean isMaleSwitched() {
        return isMaleSwitched;
    }

    public void setMaleSwitched(boolean maleSwitched) {
        isMaleSwitched = maleSwitched;
    }

    public boolean isFemaleSwitched() {
        return isFemaleSwitched;
    }

    public void setFemaleSwitched(boolean femaleSwitched) {
        isFemaleSwitched = femaleSwitched;
    }

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

    public Map<String, Event> getEventByID() { return eventByID; }

    public Map<String, ArrayList<Person>> getChildrenByParentID() {
        return childrenByParentID;
    }

    public Map<String, ArrayList<Event>> getEventsByPersonID() {
        return eventsByPersonID;
    }

    public Map<String, ArrayList<Person>> getImmediateFamily() {
        return immediateFamily;
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

    // OTHER METHODS
    public void initialize() {
        fillEventTypes();
        fillPeopleByID();
        fillEventByID();
        fillEventsByPID();
        fillChildrenByParent();
        fillImmediateFamily();
    }

    public ArrayList<Event> sortEvents(ArrayList<Event> toSort) {
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

    private void fillEventTypes() {
        for (Event event : events) {
            eventTypes.add(event.getEventType().toLowerCase());
        }
    }

    private void fillPeopleByID() {
        for (Person person : persons) {
            peopleByID.put(person.getPersonID(), person);
        }
    }

    private void fillEventByID() {
        for (Event event : events) {
            eventByID.put(event.getEventID(), event);
        }
    }

    private void fillEventsByPID() {
        for (Event event : events) {
            String personID = event.getPersonID();
            if (eventsByPersonID.containsKey(personID)) {
                ArrayList<Event> toAdd = eventsByPersonID.get(personID);
                toAdd.add(event);
                eventsByPersonID.put(personID, toAdd);
            }
            else {
                ArrayList<Event> toAdd = new ArrayList<>();
                toAdd.add(event);
                eventsByPersonID.put(personID, toAdd);
            }
        }
    }

    private void fillChildrenByParent() {
        for (Person person : persons) {
            String fatherID = person.getFatherID();
            if (fatherID != null) {
                if (childrenByParentID.containsKey(fatherID)) {
                    ArrayList<Person> toAdd = childrenByParentID.get(fatherID);
                    toAdd.add(person);
                    childrenByParentID.put(fatherID, toAdd);
                }
                else {
                    ArrayList<Person> toAdd = new ArrayList<>();
                    toAdd.add(person);
                    childrenByParentID.put(fatherID, toAdd);
                }
            }

            String motherID = person.getMotherID();
            if (motherID != null) {
                if (childrenByParentID.containsKey(motherID)) {
                    ArrayList<Person> toAdd = childrenByParentID.get(motherID);
                    toAdd.add(person);
                    childrenByParentID.put(motherID, toAdd);
                }
                else {
                    ArrayList<Person> toAdd = new ArrayList<>();
                    toAdd.add(person);
                    childrenByParentID.put(motherID, toAdd);
                }
            }
        }
    }

    private void fillImmediateFamily() {
        for (Person person : persons) {
            String personID = person.getPersonID();
            ArrayList<Person> family = new ArrayList<>();
            if (person.getFatherID() != null) {
                family.add(peopleByID.get(person.getFatherID()));
            }
            if (person.getMotherID() != null) {
                family.add(peopleByID.get(person.getMotherID()));
            }
            if (person.getSpouseID() != null) {
                family.add(peopleByID.get(person.getSpouseID()));
            }
            if (childrenByParentID.containsKey(personID)) {
                for (Person child : childrenByParentID.get(personID)) {
                    family.add(child);
                }
            }
            immediateFamily.put(personID, family);
        }
    }
}
