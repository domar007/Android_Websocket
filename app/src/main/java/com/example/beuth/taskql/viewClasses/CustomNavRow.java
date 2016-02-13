package com.example.beuth.taskql.viewClasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.beuth.tasql.R;

/**
 * @author Wael Gabsi, Stefan Völkel
 */
public class CustomNavRow extends ArrayAdapter<String> {


        private View rowView;
        private final Activity context;
        private final String[] web;
        private final Integer[] imageId;
        public CustomNavRow(Activity context,
                          String[] web, Integer[] imageId) {super(context, R.layout.navrow, web);
            this.context = context;
            this.web = web;
            this.imageId = imageId;

        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {

                LayoutInflater inflater = context.getLayoutInflater();

                rowView = inflater.inflate(R.layout.navrow, null);


            TextView txtTitle = (TextView) rowView.findViewById(R.id.Itemname);

                ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
                txtTitle.setText(web[position]);

                imageView.setImageResource(imageId[position]);
            return rowView;
        }
    }

