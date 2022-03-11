package com.example.family_map_app;

import com.example.family_map_app.serverdata.ServerProxy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import request.*;
import result.*;

public class ServerProxyTest {

    private ServerProxy server = new ServerProxy();
    private String authToken;

    @Before
    public void setUp() {
        RegisterRequest registerRequest = new RegisterRequest("test1", "password", "email", "john", "doe","m");
        RegisterResult registerResult = server.register(registerRequest, "localhost", "8080");
        this.authToken = registerResult.getAuthtoken();
    }

    @After
    public void tearDown() {
        ClearResult result = server.clear("localhost", "8080");
    }

    @Test
    public void loginSuccess() {
        LoginRequest request = new LoginRequest("test1", "password");
        LoginResult result = server.login(request, "localhost", "8080");
        assertTrue(result.getUsername().equals("test1"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void loginFailure() {
        LoginRequest request = new LoginRequest("test1", "badpassword");
        LoginResult result = server.login(request, "localhost", "8080");
        assertTrue(result.getMessage().equals("Error: Incorrect password"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void registerSuccess() {
        RegisterRequest request = new RegisterRequest("test2", "password", "email", "john", "doe","m");
        RegisterResult result = server.register(request, "localhost", "8080");

        assertTrue(result.getUsername().equals("test2"));
        assertTrue(result.isSuccess());
    }

    @Test
    public void registerFailure() {
        RegisterRequest request = new RegisterRequest("test1", "password", "email", "john", "doe","m");
        RegisterResult result = server.register(request, "localhost", "8080");

        assertTrue(result.getMessage().equals("Error: Username already taken"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void getPeopleSuccess() {
        AllPersonsResult result = server.getPeople(authToken, "localhost", "8080");

        assertEquals(31, result.getData().size());
        assertTrue(result.isSuccess());
    }

    @Test
    public void getPeopleFailure() {
        AllPersonsResult result = server.getPeople("badToken", "localhost", "8080");

        assertTrue(result.getMessage().equals("Error: Invalid authtoken"));
        assertFalse(result.isSuccess());
    }

    @Test
    public void getEventsSuccess() {
        AllEventsResult result = server.getEvents(authToken, "localhost", "8080");

        assertEquals(91, result.getData().size());
        assertTrue(result.isSuccess());
    }

    @Test
    public void getEventsFailure() {
        AllEventsResult result = server.getEvents("badToken", "localhost", "8080");

        assertTrue(result.getMessage().equals("Error: Invalid authtoken"));
        assertFalse(result.isSuccess());
    }

}
