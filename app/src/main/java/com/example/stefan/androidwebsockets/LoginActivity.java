package com.example.stefan.androidwebsockets;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by waelgabsi on 26.11.15.
 */
public class LoginActivity  extends Activity {
    private final static String CONTENT_TYPE_JSON = "application/json";

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;
    ProgressDialog prgDialog;
    String username;
    String password;
    String errorCode;
    String Session_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = (EditText) findViewById(R.id.username_input);
        mPasswordView = (EditText) findViewById(R.id.password_input);
        Button signInButton = (Button) findViewById(R.id.login_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
                username = mUsernameView.getText().toString();
                password = mPasswordView.getText().toString();
                new LongRunningGetIO().execute(username,password);
            }
        });

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);



    }

    public void loginUser(){
        // Get Email Edit View Value
        String email = mUsernameView.getText().toString();
        // Get Password Edit View Value
        String password = mPasswordView.getText().toString();
        // Instantiate Http Request Param Object

        // When Email Edit View and Password Edit View have values other than Null
        if(Utility.isNotNull(email) && Utility.isNotNull(password)){
            // When Email entered is Valid
            if(Utility.validate(email)){
                // Put Http parameter username with value of Email Edit View control
                prgDialog.show();
            }
            // When Email is invalid
            else{
                Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_LONG).show();
            }
        }
        // When any of the Edit View control left blank
        else{
            Toast.makeText(getApplicationContext(), "Please fill the form, don't leave any field blank", Toast.LENGTH_LONG).show();
        }

    }



    private class LongRunningGetIO extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... params) {

            String username = params[0];
            String password = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/taskql/login");
            httpPost.setHeader("content-type", CONTENT_TYPE_JSON);
            // Convert value strings to json object
            JSONObject json = new JSONObject();
            try {
                json.put("username", username); //wgabsi88@gmail.com
                json.put("password", password); //5B5F-7CC4-4C2E AC84-B443-468A
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //json.put("token", token);
            // Pass json object to string entity
            StringEntity entity = null;
            try {
                entity = new StringEntity(json.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            // Add entity to post request
            httpPost.setEntity(entity);
            // Execute request and handle response
            HttpResponse resp = null;
            try {
                resp = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String serverResponse = null;
            try {
                serverResponse = EntityUtils.toString(resp.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
            //	return text;
        }



        protected void onPostExecute(String results) {
            prgDialog.hide();
            if (results!=null) {

                Log.w("myApp", "1");
                JSONObject arr = null;
                Log.w("myApp", "2");
                try {
                    arr = new JSONObject(results);
                    Log.w("myApp", "3");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Log.w("myApp", "4");
                    errorCode = arr.getString("errorCode");
                    Session_id = arr.getString("nanomeSessionId");
                    Toast.makeText(getApplicationContext(), Session_id, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(errorCode.equals("0"))
                {
                    navigatetoHomeActivity(Session_id);
                }
                else {
                    Toast.makeText(getApplicationContext(), "username or password not correct", Toast.LENGTH_LONG).show();

                }
            }

        }
    }


    /**
     * Method which navigates from Login Activity to Home Activity
     */
    public void navigatetoHomeActivity(String sessionId){
        String username = mUsernameView.getText().toString().trim();
        Intent homeIntent = new Intent(getApplicationContext(),ProjectActivity.class);
        homeIntent.putExtra("username", username);
        homeIntent.putExtra("sessionId", sessionId);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }



}
