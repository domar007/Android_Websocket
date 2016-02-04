package com.example.stefan.androidwebsockets;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.stefan.androidwebsockets.CustomViewPager;
import com.example.stefan.androidwebsockets.R;
import com.example.stefan.androidwebsockets.SessionId;
import com.example.stefan.androidwebsockets.TabSubProject;

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





public class Subactivity extends AppCompatActivity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> subProjects = new ArrayList<JSONObject>();
    private List<String> subProjectNames = new ArrayList<String>();
    private int subProjectsLength, selectedTabPosition;
    private ArrayAdapter<String> adapter;
    private GetSubProjectsTask getSubProjectsTask;
    private ListView listView;
    private SessionId nanomeSessionId;
    String projectId;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        nanomeSessionId = SessionId.getInstance();
        selectedTabPosition = -1;

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            projectId = extras.getString("projectId");
        }
        executeGetSubProjectsTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(getClass().toString(), "OnResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(getClass().toString(), "OnStart");
        if (selectedTabPosition >= 0) {
            TabLayout.Tab selectedTab = tabLayout.getTabAt(selectedTabPosition);
            selectedTab.select();
            Log.d(getClass().toString(), "Tab selected");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        selectedTabPosition = tabLayout.getSelectedTabPosition();
        Log.d("TabId: ", String.valueOf(selectedTabPosition));
    }

    /**
     * Execute async task GetSubProjectsTask
     */
    public void executeGetSubProjectsTask() {
        getSubProjectsTask = new GetSubProjectsTask();
        getSubProjectsTask.execute(projectId);
    }

    private class GetSubProjectsTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String projectId = params[0];
            String sessionId = nanomeSessionId.getSessionId();

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
                    subProjectsLength = subProjectsFromJson.length();
                    subProjectNames.clear();
                    for (int i = 0; i < subProjectsLength; i++) {
                        JSONObject subProjectsJson = subProjectsFromJson.getJSONObject(i);
                        subProjects.add(subProjectsJson);
                        subProjectNames.add(subProjectsJson.getString("title"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < subProjectsLength; i++) {
                    tabLayout.addTab(tabLayout.newTab().setText(subProjectNames.get(i)));
                }

                final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
                viewPager.setPagingEnabled(false);
                final PagerAdapter adapter = new PagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount(), subProjects);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                        TabSubProject fragment = (TabSubProject) adapter.getItem(tab.getPosition());
                        String idEx = fragment.getArguments().getString("idex");
                        new GetSingleSubProjectTask(fragment, idEx).execute();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                        TabSubProject fragment = (TabSubProject) adapter.getItem(tab.getPosition());
                        String idEx = fragment.getArguments().getString("idex");
                        new GetSingleSubProjectTask(fragment, idEx).execute();
                    }
                });
            }
        }
    }

    private class GetSingleSubProjectTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog = new ProgressDialog(Subactivity.this);
        TabSubProject tabSubProject;
        String idEx;

        GetSingleSubProjectTask(TabSubProject tabSubProject, String idEx) {
            this.tabSubProject = tabSubProject;
            this.idEx = idEx;
        }

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Updating");
            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            String sessionId = nanomeSessionId.getSessionId();
            HttpClient httpClient = new DefaultHttpClient();
            // Post request
            HttpPost httpPost = new HttpPost("http://beta.taskql.com/rest/api/1/projectpart/getInfoByIdEx/" + idEx);
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
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject subProject = new JSONObject(result);
                    tabSubProject.changeTabText(subProject.getString("text"));
                    tabSubProject.getArguments().putString("subProjectLockId", subProject.getString("lockid"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }
    }


}
