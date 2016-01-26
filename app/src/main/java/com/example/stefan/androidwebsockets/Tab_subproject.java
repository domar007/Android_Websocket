package com.example.stefan.androidwebsockets;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by waelgabsi on 05.01.16.
 */

public class Tab_subproject extends Fragment {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private SessionId sessionId;
    Bundle args;
    private EditText field;
    private Timer timer;
    private Bundle bundle;
    private String idex, lockId;
    private String[] params;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_subproject, container, false);
        field = (EditText) view.findViewById(R.id.task_textfield);
        field.setEllipsize(null);
        sessionId = SessionId.getInstance();
        timer = new Timer();
        args = getArguments();
        String text =  args.getString("subProjectText");
        lockId = args.getString("subProjectLockId");
        idex = args.getString("idex");
        // Save lockId on screen rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("subProjectLockId") != null) {
                lockId = (String) savedInstanceState.getSerializable("subProjectLockId");
            }
        }
        field.setText(text);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                params = new String[]{sessionId.getSessionId(), lockId, idex, field.getText().toString()};
                timer.cancel();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new SaveSubProjectTask().execute(params);
                    }
                }, 5000);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("subProjectLockId", lockId);
    }

    /**
     *  Async Task to save the subProject text
     */
    private class SaveSubProjectTask extends AsyncTask<String[], Void, String> {

        protected String doInBackground(String[]... params) {
            String[] passed = params[0];
            String sessionId = passed[0];
            String lockId = passed[1];
            String idex = passed[2];
            String text = passed[3];

            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/projectpart/write");
            httpPost.setHeader("content-type", CONTENT_TYPE_JSON);
            httpPost.addHeader("Cookie", NANOME_SESSIONID + "=" + sessionId);
            // Convert value strings to json object
            JSONObject json = new JSONObject();
            String serverResponse = null;
            try {
                json.put("idex", idex); //
                json.put("lockid", lockId);
                json.put("text", text);
                StringEntity entity = new StringEntity(json.toString());
                // Add entity to post request
                httpPost.setEntity(entity);
                // Execute request and handle response
                HttpResponse resp = httpClient.execute(httpPost);
                serverResponse = EntityUtils.toString(resp.getEntity());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
            //	return text;
        }



        protected void onPostExecute(String results) {
            if (results != null) {
                Toast.makeText(getActivity().getApplicationContext(), results, Toast.LENGTH_LONG).show();
                try {
                    JSONObject result = new JSONObject(results);
                    int errorCode = result.getInt("errorcode");
                    if (errorCode == 0) {
                        lockId = result.getString("lockid");
                        Toast.makeText(getActivity().getApplicationContext(), "Task saved", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}