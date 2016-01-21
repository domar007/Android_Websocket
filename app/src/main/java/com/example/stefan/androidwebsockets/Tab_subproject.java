package com.example.stefan.androidwebsockets;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by waelgabsi on 05.01.16.
 */

public class Tab_subproject extends Fragment {
    EditText field;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_subproject, container, false);
        field = (EditText) view.findViewById(R.id.task_textfield);
        field.setEllipsize(null);
        Bundle args = getArguments();
        int aa =  args.getInt("name");
        String text =  args.getString("textproject");
        field.setText(text);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }




}