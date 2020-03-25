package com.testswitch_api.testswitchapi.models;

public class LoginResponse {

    private String token = null;
    private boolean loggedIn = false;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
