package com.example.family_map_app.task;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.example.family_map_app.serverdata.DataCache;
import com.example.family_map_app.serverdata.ServerProxy;

import request.RegisterRequest;
import result.AllEventsResult;
import result.AllPersonsResult;
import result.RegisterResult;

public class RegisterTask implements Runnable {

    private static final String REGISTER_SUCCESS_KEY = "RegisterSuccessKey";

    private final Handler messageHandler;
    private RegisterRequest registerRequest;
    private String serverHost;
    private String serverPort;

    public RegisterTask(Handler messageHandler, RegisterRequest registerRequest, String serverHost, String serverPort) {
        this.messageHandler = messageHandler;
        this.registerRequest = registerRequest;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        ServerProxy server = new ServerProxy();
        RegisterResult registerResult = server.register(registerRequest, serverHost, serverPort);

        if (registerResult.isSuccess()) {
            // fill data cache if successful
            DataCache dataCache = DataCache.getInstance();
            dataCache.setAuthToken(registerResult.getAuthtoken());

            String authToken = dataCache.getAuthToken();
            AllPersonsResult allPersonsResult = server.getPeople(authToken, serverHost, serverPort);
            AllEventsResult allEventsResult = server.getEvents(authToken, serverHost, serverPort);
            if (isGetDataSuccess(allPersonsResult, allEventsResult)) {
                dataCache.setPersons(allPersonsResult.getData());
                dataCache.setEvents(allEventsResult.getData());
                dataCache.setUser(allPersonsResult.getData().get(allPersonsResult.getData().size() - 1));
            }
        }

        sendMessage(registerResult);
    }

    private void sendMessage(RegisterResult result) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putBoolean(REGISTER_SUCCESS_KEY, result.isSuccess());
        message.setData(messageBundle);

        messageHandler.sendMessage(message);
    }

    private boolean isGetDataSuccess(AllPersonsResult allPersonsResult, AllEventsResult allEventsResult) {
        return (allPersonsResult.isSuccess() && allEventsResult.isSuccess());
    }

}
