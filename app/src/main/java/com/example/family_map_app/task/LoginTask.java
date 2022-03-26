package com.example.family_map_app.task;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.example.family_map_app.serverdata.DataCache;
import com.example.family_map_app.serverdata.ServerProxy;

import request.LoginRequest;
import result.AllEventsResult;
import result.AllPersonsResult;
import result.LoginResult;

public class LoginTask implements Runnable {

    private static final String LOGIN_SUCCESS_KEY = "LoginSuccessKey";

    private final Handler messageHandler;
    private LoginRequest loginRequest;
    private String serverHost;
    private String serverPort;

    public LoginTask(Handler messageHandler, LoginRequest loginRequest, String serverHost, String serverPort) {
        this.messageHandler = messageHandler;
        this.loginRequest = loginRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        ServerProxy server = new ServerProxy();
        LoginResult loginResult = server.login(loginRequest, serverHost, serverPort);

        if (loginResult.isSuccess()) {
            DataCache dataCache = DataCache.getInstance();
            dataCache.setAuthToken(loginResult.getAuthtoken());

            String authToken = dataCache.getAuthToken();
            AllPersonsResult allPersonsResult = server.getPeople(authToken, serverHost, serverPort);
            AllEventsResult allEventsResult = server.getEvents(authToken, serverHost, serverPort);
            if (isGetDataSuccess(allPersonsResult, allEventsResult)) {
                dataCache.setPersons(allPersonsResult.getData());
                dataCache.setEvents(allEventsResult.getData());
                dataCache.setUser(allPersonsResult.getData().get(allPersonsResult.getData().size() - 1));
            }
        }

        sendMessage(loginResult);
    }

    private void sendMessage(LoginResult result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(LOGIN_SUCCESS_KEY, result.isSuccess());
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

    private boolean isGetDataSuccess(AllPersonsResult allPersonsResult, AllEventsResult allEventsResult) {
        return (allPersonsResult.isSuccess() && allEventsResult.isSuccess());
    }

}
