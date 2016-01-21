package com.example.stefan.androidwebsockets;

/**
 * Created by waelgabsi on 05.01.16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    List<Fragment> fragments;
    List<JSONObject> subProjectsobj = new ArrayList<JSONObject>();
    String subProjectText;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,List<JSONObject> subProjectsobj) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.subProjectsobj = subProjectsobj;
        fragments = new ArrayList<Fragment>();
        for (int i = 0; i < mNumOfTabs; i++) {

            fragments.add(new Tab_subproject());
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment actuel_fragment =fragments.get(position);
       Bundle args = new Bundle();

        try {
             subProjectText =  subProjectsobj.get(position).getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        args.putString("textproject",subProjectText);
        args.putInt("name", position + 1);
        actuel_fragment.setArguments(args);
                return actuel_fragment ;

    }



    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}


