package com.example.stefan.androidwebsockets;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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

public class Subactivity extends AppCompatActivity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> subProjects = new ArrayList<JSONObject>();
    private List<String> subProjectNames = new ArrayList<String>();
    private int subProjectslength;
    private ArrayAdapter<String> adapter;
    private GetSubProjectsTask getSubProjectsTask;
    private ListView listView;
    String projectId, nanomeSessionId, username;
    String[] params;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nanomeSessionId = extras.getString("sessionId");
            projectId = extras.getString("projectId");
            username = extras.getString("username");
        }
        params = new String[] {nanomeSessionId, projectId};
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
                    subProjectslength = subProjectsFromJson.length();
                    subProjectNames.clear();
                    for (int i = 0; i < subProjectslength; i++) {
                        JSONObject subProjectsJson = subProjectsFromJson.getJSONObject(i);
                        subProjects.add(subProjectsJson);
                        subProjectNames.add(subProjectsJson.getString("description"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(subProjectslength > 4){


                }
                for (int i = 0; i < subProjectslength; i++) {
                    tabLayout.addTab(tabLayout.newTab().setText(subProjectNames.get(i)));

                }

//
                // tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                final CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.pager);
                viewPager.setPagingEnabled(false);
                final PagerAdapter adapter = new PagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount(),subProjects);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }
    }


}