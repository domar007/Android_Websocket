/**
 * Created by waelgabsi on 26.11.15.
 */
package com.example.stefan.androidwebsockets;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
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

public class ProjekteActivity extends Activity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> projects = new ArrayList<JSONObject>();
    private List<String> projectNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private GetProjectsTask getProjectsTask;
    private ListView listView;
    String value;
    String nanomeSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekte);
        listView = (ListView) findViewById(R.id.mainListView);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            value = extras.getString("username");
            nanomeSessionId = extras.getString("sessionId");
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projectNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int projectId = projects.get(position).getInt("id");
                    navigateToSubprojectActivity(nanomeSessionId, projectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getProjectsTask = new GetProjectsTask();
        getProjectsTask.execute(nanomeSessionId);
    }

    private class GetProjectsTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String sessionId = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/project/getAll");
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
                    JSONArray projectsFromJson = project.getJSONArray("projects");
                    adapter.clear();
                    projectNames.clear();
                    for (int i = 0; i < projectsFromJson.length(); i++) {
                        JSONObject projectsJson = projectsFromJson.getJSONObject(i);
                        projects.add(projectsJson);
                        projectNames.add(projectsJson.getString("title"));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void navigateToSubprojectActivity(String sessionId, int projectId){
        Intent subprojectIntent = new Intent(getApplicationContext(),SubprojectActivity.class);
        subprojectIntent.putExtra("projectId", projectId);
        subprojectIntent.putExtra("sessionId", sessionId);
        subprojectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(subprojectIntent);
    }
}