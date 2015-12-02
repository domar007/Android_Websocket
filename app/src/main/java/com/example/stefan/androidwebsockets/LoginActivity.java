package com.example.stefan.androidwebsockets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

/**
 * Created by waelgabsi on 26.11.15.
 */
public class LoginActivity  extends AppCompatActivity {

    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsernameView = (EditText) findViewById(R.id.username_input);
        mPasswordView = (EditText) findViewById(R.id.password_input);
        Button signInButton = (Button) findViewById(R.id.login_button);



        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                String username = mUsernameView.getText().toString().trim();
                Intent activityChangeIntent = new Intent(LoginActivity.this, ProjekteActivity.class);
                activityChangeIntent.putExtra("username", username);
                // currentContext.startActivity(activityChangeIntent);

                LoginActivity.this.startActivity(activityChangeIntent);
            }
        });


    }



}
