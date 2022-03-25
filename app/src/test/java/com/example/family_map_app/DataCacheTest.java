package com.example.family_map_app;

import com.example.family_map_app.serverdata.DataCache;
import model.*;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DataCacheTest {

    private DataCache dataCache = null;

    @Before
    public void setUp() {
        dataCache = DataCache.getInstance();

        Person dad = new Person("dad", "test", "pa", "smith", "m", null, null, "mom");
        Person mom = new Person("mom", "test", "ma", "smith", "f", null, null, "dad");
        Person kid = new Person("kid", "test", "kiddo", "smith", "m", "dad", "mom", "wife");
        Person wife = new Person("wife", "test", "wife", "smith", "f", null, null, "kid");
        ArrayList<Person> testFamily = new ArrayList<>();
        testFamily.add(dad);
        testFamily.add(mom);
        testFamily.add(kid);
        testFamily.add(wife);
        dataCache.setPersons(testFamily);

        Event death = new Event("death", "test", "kid", 1.0f, 1.0f, "USA", "Provo", "death", 2000);
        Event birth = new Event("birth", "test", "kid", 1.0f, 1.0f, "USA", "Provo", "birth", 1950);
        Event marriage = new Event("marriage", "test", "kid", 1.0f, 1.0f, "USA", "Provo", "marriage", 1975);
        ArrayList<Event> testEvents = new ArrayList<>();
        testEvents.add(death);
        testEvents.add(birth);
        testEvents.add(marriage);
        dataCache.setEvents(testEvents);

    }

}
