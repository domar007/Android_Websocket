package com.example.stefan.androidwebsockets;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Subactivity extends AppCompatActivity implements OnSelectLastSelectedTabListener {
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private List<JSONObject> subProjects = new ArrayList<JSONObject>();
    private List<String> subProjectNames = new ArrayList<String>();
    private Connection connection;
    private int subProjectsLength, selectedTabPosition;
    private ArrayAdapter<String> adapter;
    private GetSubProjectsTask getSubProjectsTask;
    private ListView listView;
    private SessionId nanomeSessionId;
    String projectId;
    TabLayout tabLayout;
    private OnSelectLastSelectedTabListener onSelectLastSelectedTabListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subactivity);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        nanomeSessionId = SessionId.getInstance();
        connection = new Connection();
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
    }

    @Override
    public void onStart() {
        super.onStart();
        selectLastSelectedTab();
    }

    @Override
    public void onPause() {
        super.onPause();
        selectedTabPosition = tabLayout.getSelectedTabPosition();
    }

    /**
     * Execute async task GetSubProjectsTask
     */
    private void executeGetSubProjectsTask() {
        getSubProjectsTask = new GetSubProjectsTask();
        getSubProjectsTask.execute(projectId);
    }

    /**
     * Select last selected tab
     */
    public void selectLastSelectedTab() {
        if (selectedTabPosition >= 0) {
            TabLayout.Tab selectedTab = tabLayout.getTabAt(selectedTabPosition);
            selectedTab.select();
        }
    }

    /**
     * Select last selected tab text
     */
    public void selectLastSelectedTabText() {
        if (selectedTabPosition >= 0) {
            TabLayout.Tab selectedTab = tabLayout.getTabAt(selectedTabPosition);
             String tabtext = (String) selectedTab.getText();
            tabtext = tabtext.replace('*', '\0');
            selectedTab.setText(tabtext+"*");
        }
    }



    public void delectLastSelectedTabText() {
        if (selectedTabPosition >= 0) {
            TabLayout.Tab selectedTab2 = tabLayout.getTabAt(selectedTabPosition);
            String tabtext = (String) selectedTab2.getText();
            tabtext = tabtext.replace('*', '\0');
            selectedTab2.setText(tabtext);
        }
    }

    private class GetSubProjectsTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            String projectId = params[0];
            String sessionId = nanomeSessionId.getSessionId();
            String serverResponse = null;
            try {
                serverResponse = connection.doPostRequestWithAdditionalHeader("http://beta.taskql.com/rest/api/1/project/getInfoByProjectId?objectid=" + projectId, NANOME_SESSIONID + "=" + sessionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
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
                        selectedTabPosition = tab.getPosition();
                        String tabtext = (String) tab.getText();
                        tabtext = tabtext.replace('*', '\0');
                        tab.setText(tabtext);

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
                        selectedTabPosition = tab.getPosition();
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
            String serverResponse = null;
            try {
                serverResponse = connection.doPostRequestWithAdditionalHeader("http://beta.taskql.com/rest/api/1/projectpart/getInfoByIdEx/" + idEx, NANOME_SESSIONID + "=" + sessionId);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject subProject = new JSONObject(result);
                    if (subProject.length() > 0) {
                        tabSubProject.changeTabText(subProject.getString("text"));
                        tabSubProject.setLockId(subProject.getString("lockid"));
                    } else {
                        tabSubProject.showDialogOnDeletedSubProject();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }
    }


}
