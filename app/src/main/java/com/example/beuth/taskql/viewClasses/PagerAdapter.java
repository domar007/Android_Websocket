package com.example.beuth.taskql.viewClasses;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Wael Gabsi, Stefan Völkel
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<Fragment> fragments;
    List<JSONObject> subProjects = new ArrayList<JSONObject>();
    String subProjectText, subProjectLockId, idex;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,List<JSONObject> subProjects) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subProjects = subProjects;
        fragments = new ArrayList<Fragment>();
        for (int i = 0; i < mNumOfTabs; i++) {
            fragments.add(new TabSubProject());
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment currentFragment = fragments.get(position);
        if (!currentFragment.isAdded()) {
            Bundle args = new Bundle();
            try {
                subProjectText =  subProjects.get(position).getString("text");
                subProjectLockId = subProjects.get(position).getString("lockid");
                idex = subProjects.get(position).getString("idex");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            args.putString("subProjectText",subProjectText);
            args.putString("subProjectLockId",subProjectLockId);
            args.putString("idex",idex);
            args.putInt("name", position + 1);
            currentFragment.setArguments(args);
        }
        return currentFragment ;
    }





    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


