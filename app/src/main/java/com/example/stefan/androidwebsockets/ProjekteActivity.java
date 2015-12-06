/**
 * Created by waelgabsi on 26.11.15.
 */
package com.example.stefan.androidwebsockets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;

public class ProjekteActivity extends AppCompatActivity implements OnItemSelectedListener {
    Spinner spinnerOsversions;
    private String[] state = { "Projekt1", "projekt2", "projekt3", "projekt4",
            "projekt5" };
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projekte);
        spinnerOsversions = (Spinner) findViewById(R.id.osversions);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            value = extras.getString("username");
        }

        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, state);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOsversions.setAdapter(adapter_state);
        spinnerOsversions.setOnItemSelectedListener(this);

        Button signInButton = (Button) findViewById(R.id.login_button);



        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            //    String username = mUsernameView.getText().toString().trim();
                Intent activityChangeIntent = new Intent(ProjekteActivity.this, MainActivity.class);
                activityChangeIntent.putExtra("username", value);
                // currentContext.startActivity(activityChangeIntent);

                ProjekteActivity.this.startActivity(activityChangeIntent);
            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
