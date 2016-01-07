/**
 * Created by waelgabsi on 26.11.15.
 */
package com.example.stefan.androidwebsockets;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

public class ProjekteActivity extends Activity implements OnItemSelectedListener {
    private final static String CONTENT_TYPE_JSON = "application/json";
    Spinner spinnerOsversions;
    private String[] state = { "Projekt1", "projekt2", "projekt3", "projekt4",
            "projekt5" };
    String value;
    String nanome_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekte);
        spinnerOsversions = (Spinner) findViewById(R.id.osversions);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            value = extras.getString("username");
            nanome_session = extras.getString("sessionId");
            Toast.makeText(getApplicationContext(),nanome_session , Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOsversions.setAdapter(adapter_state);
        spinnerOsversions.setOnItemSelectedListener(this);

        Button signInButton = (Button) findViewById(R.id.login_button);



        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new LongRunningGetIO().execute(nanome_session);
                // Perform action on click
                Intent activityChangeIntent = new Intent(ProjekteActivity.this, MainActivity.class);
                activityChangeIntent.putExtra("username", value);
                ProjekteActivity.this.startActivity(activityChangeIntent);
            }
        });


    }
    private class LongRunningGetIO extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... params) {

            String session = params[0];

            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/project/getAll");
            httpPost.setHeader("content-type", CONTENT_TYPE_JSON);
            // Convert value strings to json object
            JSONObject json = new JSONObject();
            try {
                json.put("nanomeSessionId", session); //wgabsi88@gmail.com

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

            if (results!=null) {


                Toast.makeText(getApplicationContext(),results , Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
