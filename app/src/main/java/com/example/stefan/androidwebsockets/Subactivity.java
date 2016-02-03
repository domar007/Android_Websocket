package com.example.stefan.androidwebsockets;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class Subactivity extends AppCompatActivity {
    private final static String CONTENT_TYPE_JSON = "application/json";
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> subProjects = new ArrayList<JSONObject>();
    private List<String> subProjectNames = new ArrayList<String>();
    private int subProjectslength;
    private ArrayAdapter<String> adapter;
    private GetSubProjectsTask getSubProjectsTask;
    private ListView listView;
    private SessionId nanomeSessionId;
    String projectId;
    TabLayout tabLayout;
    String TabTitle;
    String TabSelectedTitle;
     CustomViewPager viewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        nanomeSessionId = SessionId.getInstance();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            projectId = extras.getString("projectId");
        }
        executeGetSubProjectsTask();
        getWindow().setTitle("Hello");
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
                    subProjectslength = subProjectsFromJson.length();
                    subProjectNames.clear();
                    for (int i = 0; i < subProjectslength; i++) {
                        JSONObject subProjectsJson = subProjectsFromJson.getJSONObject(i);
                        subProjects.add(subProjectsJson);
                        subProjectNames.add(subProjectsJson.getString("title"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < subProjectslength; i++) {
                    tabLayout.addTab(tabLayout.newTab().setText(subProjectNames.get(i)));
                }

                viewPager = (CustomViewPager) findViewById(R.id.pager);
                viewPager.setPagingEnabled(false);
                final PagerAdapter adapter = new PagerAdapter
                        (getSupportFragmentManager(), tabLayout.getTabCount(), subProjects);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {


                        viewPager.setCurrentItem(tab.getPosition());

                        TabTitle = tab.getText().toString();
                      int a =  TabTitle.indexOf("*");
                        Log.e("dsafsadf",""+a);
                        if(a > -1){
                        TabTitle = TabTitle.substring(0, TabTitle.length() - 1);
                        Log.e("bbbbbbb", "" + TabTitle);
                        tab.setText(TabTitle);
                        }


                        TabSubProject fragment = (TabSubProject) adapter.getItem(tab.getPosition());
                        new GetSingleSubProjectTask().execute(fragment,tab,TabTitle);
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                        TabTitle = tab.getText().toString();
                        tab.setText(TabTitle+"*");
                        Log.e("aaaaaaaaa", "" + TabTitle);
                 /*       TabSelectedTitle = tab.getText().toString();
                        Log.e("aaaaaaaaa", "" + TabSelectedTitle);
                        TabSelectedTitle = TabSelectedTitle.substring(0, TabSelectedTitle.length() - 1);
                        Log.e("bbbbbbbb", "" + TabSelectedTitle);
                        tab.setText(TabSelectedTitle); */

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }
        }
    }

    private class GetSingleSubProjectTask extends AsyncTask<Object , Void, TabSubProject> {

        ProgressDialog progressDialog = new ProgressDialog(Subactivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Updating");
            progressDialog.show();

        }

        protected TabSubProject doInBackground(Object... params) {
            TabSubProject currentSubProject = (TabSubProject) params[0];
            TabLayout.Tab currentTab = (TabLayout.Tab) params[1];
          //  String currentTabTitle = (String) params[2];
          //  currentTabTitle = currentTabTitle.substring(0, currentTabTitle.length() - 1);
            currentSubProject.getArguments().putString("test", "AsyncTaskDone");
          //  currentTab.setText(currentTabTitle);
            return currentSubProject;
        }
        protected void onPostExecute(TabSubProject tabSubProject) {
            if (tabSubProject != null) {

                Bundle args = tabSubProject.getArguments();
                //tabSubProject.changeText(args.getString("..."));
                Toast.makeText(getApplicationContext(), "Idex: " + args.getString("idex"), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }




}