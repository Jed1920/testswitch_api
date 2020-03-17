package com.testswitch_api.testswitchapi.models;

public class ErrorMessage {

    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
