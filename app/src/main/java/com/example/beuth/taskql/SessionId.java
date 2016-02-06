package com.example.beuth.taskql;

import android.app.Application;

/**
 * Created by Stefan Völkel on 26.01.2016.
 */
public class SessionId extends Application
{
    private String sessionId;
    private static SessionId singleInstance = null;

    public static SessionId getInstance()
    {
        return singleInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleInstance = this;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}
