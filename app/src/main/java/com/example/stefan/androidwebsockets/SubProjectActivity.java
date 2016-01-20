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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubProjectActivity extends Activity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> subProjects = new ArrayList<JSONObject>();
    private List<String> subProjectNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private GetSubProjectsTask getSubProjectsTask;
    private ListView listView;
    String projectId, nanomeSessionId, username;
    String[] params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subprojects);
        listView = (ListView) findViewById(R.id.subProjectListView);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nanomeSessionId = extras.getString("sessionId");
            projectId = extras.getString("projectId");
            username = extras.getString("username");
        }
        params = new String[] {nanomeSessionId, projectId};
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, subProjectNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String subProjectText = subProjects.get(position).getString("text");
                    String idex = subProjects.get(position).getString("idex");
                    navigateToMainActivity(subProjectText, idex);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getSubProjectsTask = new GetSubProjectsTask();
        getSubProjectsTask.execute(params);
    }

    private class GetSubProjectsTask extends AsyncTask<String[], Void, String> {

        protected String doInBackground(String[]... params) {
            String[] passed = params[0];
            String sessionId = passed[0];
            String projectId = passed[1];

            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/project/getInfoByProjectId?objectid=" + projectId);
            httpPost.setHeader("content-type", CONTENT_TYPE_JSON);
            httpPost.addHeader("Cookie", NANOME_SESSIONID + "=" + sessionId);
            String serverResponse = null;

            try {
                HttpResponse resp = httpClient.execute(httpPost);
                serverResponse = EntityUtils.toString(resp.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
            //	return text;
        }



        protected void onPostExecute(String results) {
            if (results != null) {
                try {
                    JSONObject project = new JSONObject(results);
                    JSONArray subProjectsFromJson = project.getJSONArray("projectparts");
                    adapter.clear();
                    subProjectNames.clear();
                    for (int i = 0; i < subProjectsFromJson.length(); i++) {
                        JSONObject subProjectsJson = subProjectsFromJson.getJSONObject(i);
                        subProjects.add(subProjectsJson);
                        subProjectNames.add(subProjectsJson.getString("description"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void navigateToMainActivity(String subProjectText, String idex){
        Intent subProjectIntent = new Intent(getApplicationContext(),MainActivity.class);
        subProjectIntent.putExtra("subProjectText", subProjectText);
        subProjectIntent.putExtra("sessionId", nanomeSessionId);
        subProjectIntent.putExtra("idex", idex);
        subProjectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(subProjectIntent);
    }
}