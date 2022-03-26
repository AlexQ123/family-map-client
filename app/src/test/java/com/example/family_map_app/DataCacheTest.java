package com.example.family_map_app;

import com.example.family_map_app.serverdata.DataCache;
import model.*;
import java.util.ArrayList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataCacheTest {

    private DataCache dataCache = null;

    private Person dad;
    private Person mom;
    private Person kid;
    private Person wife;

    private Event momDeath;
    private Event momBirth;
    private Event marriage;
    private Event dadDeath;
    private Event dadBirth;

    @Before
    public void setUp() {
        dataCache = DataCache.getInstance();

        dad = new Person("dad", "test", "pa", "smith", "m", null, null, "mom");
        mom = new Person("mom", "test", "ma", "smith", "f", null, null, "dad");
        kid = new Person("kid", "test", "kiddo", "smith", "m", "dad", "mom", "wife");
        wife = new Person("wife", "test", "wife", "smith", "f", null, null, "kid");
        ArrayList<Person> testFamily = new ArrayList<>();
        testFamily.add(kid);
        testFamily.add(dad);
        testFamily.add(mom);
        testFamily.add(wife);
        dataCache.setPersons(testFamily);

        momDeath = new Event("momDeath", "test", "mom", 1.0f, 1.0f, "USA", "Provo", "death", 2000);
        momBirth = new Event("momBirth", "test", "mom", 1.0f, 1.0f, "USA", "Provo", "birth", 1950);
        marriage = new Event("marriage", "test", "mom", 1.0f, 1.0f, "USA", "Provo", "marriage", 1975);
        dadDeath = new Event("dadDeath", "test", "dad", 1.0f, 1.0f, "USA", "Provo", "death", 2000);
        dadBirth = new Event("dadBirth", "test", "dad", 1.0f, 1.0f, "USA", "Provo", "birth", 1950);
        ArrayList<Event> testEvents = new ArrayList<>();
        testEvents.add(momDeath);
        testEvents.add(momBirth);
        testEvents.add(marriage);
        testEvents.add(dadDeath);
        testEvents.add(dadBirth);
        dataCache.setEvents(testEvents);
    }

    @After
    public void tearDown() {
        DataCache.setInstance(null);
    }

    // testing for family relationships that should exist (POSITIVE)
    @Test
    public void familyRelationshipsToExist() {
        dataCache.initialize();
        Map<String, ArrayList<Person>> immediateFamily = dataCache.getImmediateFamily();

        assertTrue(immediateFamily.containsKey("kid"));
        assertTrue(immediateFamily.get("kid").contains(dad));
        assertTrue(immediateFamily.get("kid").contains(mom));
        assertTrue(immediateFamily.get("kid").contains(wife));

        assertTrue(immediateFamily.containsKey("dad"));
        assertTrue(immediateFamily.get("dad").contains(mom));
        assertTrue(immediateFamily.get("dad").contains(kid));

        assertTrue(immediateFamily.containsKey("mom"));
        assertTrue(immediateFamily.get("mom").contains(dad));
        assertTrue(immediateFamily.get("mom").contains(kid));

        assertTrue(immediateFamily.containsKey("wife"));
        assertTrue(immediateFamily.get("wife").contains(kid));
    }

    // testing for family relationships that should NOT exist (NEGATIVE)
    @Test
    public void familyRelationshipsToNotExist() {
        dataCache.initialize();
        Map<String, ArrayList<Person>> immediateFamily = dataCache.getImmediateFamily();

        assertFalse(immediateFamily.get("dad").contains(wife));
        assertFalse(immediateFamily.get("dad").contains(dad));

        assertFalse(immediateFamily.get("mom").contains(wife));
        assertFalse(immediateFamily.get("mom").contains(mom));

        assertFalse(immediateFamily.get("wife").contains(dad));
        assertFalse(immediateFamily.get("wife").contains(mom));
        assertFalse(immediateFamily.get("wife").contains(wife));

        assertFalse(immediateFamily.get("kid").contains(kid));
    }

    // testing for correctly filtered events according to settings (POSITIVE)
    @Test
    public void existsInFilter() {
        dataCache.initialize();
        dataCache.setFatherSwitched(true);
        dataCache.setMaleSwitched(true);

        ArrayList<Event> filteredFatherMale = dataCache.getEventsToMap();
        assertTrue(filteredFatherMale.contains(dadBirth));
        assertTrue(filteredFatherMale.contains(dadDeath));

        dataCache.setFatherSwitched(false);
        dataCache.setMaleSwitched(false);
        dataCache.setMotherSwitched(true);
        dataCache.setFemaleSwitched(true);

        ArrayList<Event> filteredMotherFemale = dataCache.getEventsToMap();
        assertTrue(filteredMotherFemale.contains(momBirth));
        assertTrue(filteredMotherFemale.contains(marriage));
        assertTrue(filteredMotherFemale.contains(momDeath));
    }

    // testing to ensure other events are not erroneously included (NEGATIVE)
    @Test
    public void notExistsInFilter() {
        dataCache.initialize();
        dataCache.setFatherSwitched(true);
        dataCache.setMaleSwitched(true);

        ArrayList<Event> filteredFatherMale = dataCache.getEventsToMap();
        assertEquals(2, filteredFatherMale.size());
        assertFalse(filteredFatherMale.contains(momBirth));
        assertFalse(filteredFatherMale.contains(marriage));
        assertFalse(filteredFatherMale.contains(momDeath));

        dataCache.setFatherSwitched(false);
        dataCache.setMaleSwitched(false);
        dataCache.setMotherSwitched(true);
        dataCache.setFemaleSwitched(true);

        ArrayList<Event> filteredMotherFemale = dataCache.getEventsToMap();
        assertEquals(3, filteredMotherFemale.size());
        assertFalse(filteredMotherFemale.contains(dadBirth));
        assertFalse(filteredMotherFemale.contains(dadDeath));
    }

    // testing for events that should be sorted for each person (POSITIVE)
    @Test
    public void eventsSorted() {
        dataCache.initialize();

        ArrayList<Event> sortedMom = dataCache.sortEvents(dataCache.getEventsByPersonID().get("mom"));
        assertEquals(momBirth, sortedMom.get(0));
        assertEquals(marriage, sortedMom.get(1));
        assertEquals(momDeath, sortedMom.get(2));

        ArrayList<Event> sortedDad = dataCache.sortEvents(dataCache.getEventsByPersonID().get("dad"));
        assertEquals(2, sortedDad.size());
        assertEquals(dadBirth, sortedDad.get(0));
        assertEquals(dadDeath, sortedDad.get(1));
    }

    // testing for events that should NOT be sorted for each person (NEGATIVE)
    @Test
    public void eventsNotSorted() {
        dataCache.initialize();

        ArrayList<Event> sortedMom = dataCache.sortEvents(dataCache.getEventsByPersonID().get("mom"));
        assertEquals(3, sortedMom.size());
        assertFalse(sortedMom.contains(dadBirth));
        assertFalse(sortedMom.contains(dadDeath));


        ArrayList<Event> sortedDad = dataCache.sortEvents(dataCache.getEventsByPersonID().get("dad"));
        assertEquals(2, sortedDad.size());
        assertFalse(sortedDad.contains(momBirth));
        assertFalse(sortedDad.contains(momDeath));
    }

    // testing for a correct search result (POSITIVE)
    @Test
    public void searchReturnsResult() {
        dataCache.initialize();

        ArrayList<Person> aSearched = dataCache.getPersonsToSearch("a");
        assertTrue(aSearched.contains(mom));
        assertTrue(aSearched.contains(dad));

        ArrayList<Person> smithSearched = dataCache.getPersonsToSearch("smith");
        assertTrue(smithSearched.contains(dad));
        assertTrue(smithSearched.contains(mom));
        assertTrue(smithSearched.contains(kid));
        assertTrue(smithSearched.contains(wife));

        ArrayList<Event> yearSearched = dataCache.getEventsToSearch("1950");
        assertTrue(yearSearched.contains(momBirth));
        assertTrue(yearSearched.contains(dadBirth));
    }

    // testing for no search result (NEGATIVE)
    @Test
    public void searchReturnsNoResult() {
        dataCache.initialize();

        ArrayList<Person> emptyPersonSearched = dataCache.getPersonsToSearch("");
        assertEquals(0, emptyPersonSearched.size());

        ArrayList<Event> emptyEventSearched = dataCache.getEventsToSearch("");
        assertEquals(0, emptyEventSearched.size());
    }

}
