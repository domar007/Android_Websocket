/**
 * Created by waelgabsi on 26.11.15.
 */
package com.example.stefan.androidwebsockets;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
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

public class ProjectActivity extends AppCompatActivity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    public static final String PREFS_NAME = "LoginPrefs";
    private List<JSONObject> projects = new ArrayList<JSONObject>();
    private List<String> projectNames = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private GetProjectsTask getProjectsTask;
    private ListView listView;
    private SessionId nanomeSessionId;
    String username;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    String[] osArray =  new String[10];
    AlertDialog.Builder builder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        listView = (ListView) findViewById(R.id.projectListView);
        nanomeSessionId = SessionId.getInstance();
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        builder = new AlertDialog.Builder(this);
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString("username");
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, projectNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String projectId = projects.get(position).getString("id");
                    navigateToSubProjectActivity(projectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        getProjectsTask = new GetProjectsTask();
        getProjectsTask.execute();
    }

    private class GetProjectsTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String sessionId = nanomeSessionId.getSessionId();

//            Log.d("sessionId", sessionId);
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

    private void navigateToSubProjectActivity(String projectId){
        Intent subProjectIntent = new Intent(getApplicationContext(),Subactivity.class);
        subProjectIntent.putExtra("projectId", projectId);
        subProjectIntent.putExtra("username", username);
        subProjectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(subProjectIntent);
    }



    private void addDrawerItems() {
        osArray = new String[]{"Abmelden", "Impressum"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
builder.setTitle("Abmelden")
                                .setMessage("Sind Sie sicher, dass Sie sich abmelden wollen?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.clear();
                                        editor.commit();
                                        Intent homeIntent = new Intent(getApplicationContext(),LoginActivity.class);
                                        startActivity(homeIntent);

                                    }
                                })
                                .setNegativeButton("Nein", null)						//Do nothing on no
                                .show();

                        break;
                    case 1:
                        Toast.makeText(ProjectActivity.this, osArray[position], Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(ProjectActivity.this, "you didnt clicked", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("TaskQl");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}