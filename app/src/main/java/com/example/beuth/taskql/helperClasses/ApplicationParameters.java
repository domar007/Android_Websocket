package com.example.beuth.taskql.helperClasses;

import android.app.Application;

/** Application class to use session id globally in the application
 * @author Wael Gabsi, Stefan Völkel
 */
public class ApplicationParameters extends Application
{
    private String sessionId;
    private String serverUrl;
    private static ApplicationParameters singleInstance = null;

    public static ApplicationParameters getInstance()
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

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
