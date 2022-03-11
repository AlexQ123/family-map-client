package com.example.family_map_app.serverdata;

import java.io.*;
import java.net.*;
import result.*;
import request.*;
import com.google.gson.Gson;

public class ServerProxy {

    public LoginResult login(LoginRequest request, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                LoginResult result = (LoginResult)gson.fromJson(respData, LoginResult.class);
                return result;
            }
            else {
                System.out.println("LOGIN ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                LoginResult result = (LoginResult)gson.fromJson(respData, LoginResult.class);
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            LoginResult result = new LoginResult("Error: Server proxy login failure", false);
            return result;
        }
    }

    public RegisterResult register(RegisterRequest request, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                RegisterResult result = (RegisterResult)gson.fromJson(respData, RegisterResult.class);
                return result;
            }
            else {
                System.out.println("REGISTER ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                RegisterResult result = (RegisterResult)gson.fromJson(respData, RegisterResult.class);
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            RegisterResult result = new RegisterResult("Error: Server proxy register failure", false);
            return result;
        }
    }

    public AllPersonsResult getPeople(String authToken, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                AllPersonsResult result = (AllPersonsResult)gson.fromJson(respData, AllPersonsResult.class);
                return result;
            }
            else {
                System.out.println("GET PEOPLE ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                AllPersonsResult result = (AllPersonsResult)gson.fromJson(respData, AllPersonsResult.class);
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            AllPersonsResult result = new AllPersonsResult("Error: Server proxy getPeople failure", false);
            return result;
        }
    }

    public AllEventsResult getEvents(String authToken, String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                AllEventsResult result = (AllEventsResult) gson.fromJson(respData, AllEventsResult.class);
                return result;
            }
            else {
                System.out.println("GET EVENT ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                AllEventsResult result = (AllEventsResult) gson.fromJson(respData, AllEventsResult.class);
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            AllEventsResult result = new AllEventsResult("Error: Server proxy getEvents failure", false);
            return result;
        }
    }

    public ClearResult clear(String serverHost, String serverPort) {
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");

            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(false);
            http.addRequestProperty("Accept", "application/json");
            http.connect();

            Gson gson = new Gson();

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream respBody = http.getInputStream();
                String respData = readString(respBody);
                ClearResult result = (ClearResult) gson.fromJson(respData, ClearResult.class);
                return result;
            }
            else {
                System.out.println("CLEAR ERROR: " + http.getResponseMessage());
                InputStream respBody = http.getErrorStream();
                String respData = readString(respBody);
                ClearResult result = (ClearResult) gson.fromJson(respData, ClearResult.class);
                return result;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            ClearResult result = new ClearResult("Error: Server proxy clear failure", false);
            return result;
        }
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

}
