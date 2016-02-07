package com.example.beuth.taskql;

import android.app.Application;

/** Application class to use session id globally in the application
 * @author Wael Gabsi, Stefan Völkel
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
