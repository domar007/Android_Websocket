package com.example.stefan.androidwebsockets;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.util.UUID;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private EditText text;
    private TextView user;
    private WebSocketClient mWebSocketClient;
    private boolean textChanged = true;
    private JSONObject json;
    String uuid;
    String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            value = extras.getString("username");
        }

        user = (TextView) findViewById(R.id.username);
        button = (Button) findViewById(R.id.send_button);
        text = (EditText) findViewById(R.id.edit_text);

        json = new JSONObject();
        connectToWebSocket();
        // uuid = UUID.randomUUID().toString();
        uuid = value ;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mWebSocketClient.send(text.getText().toString());
            }
        });

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (textChanged) {
                    try {
                        json.put("cursorPos", text.getSelectionStart());
                        json.put("text", text.getText().toString());
                        json.put("user", uuid);

                    } catch (JSONException e) {
                        Log.i("JSON Exception", e.getStackTrace().toString());
                    }
                    //Send json
                    mWebSocketClient.send(json.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               // Toast.makeText(getApplicationContext(), "STRING MESSAGE", 1000).show();
              //  user.setText("ddd");
            }
        });

    }

    private void connectToWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://10.0.3.2:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
            //    mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            textChanged = false;
                            text.setText("");

                            JSONObject resultJson = new JSONObject(message);
                            text.setText(text.getText() + resultJson.get("text").toString());
                            text.setSelection(resultJson.getInt("cursorPos"));

                            user.setText(resultJson.get("user").toString() + "TYPING");
                            // text.setText(text.getText() + message );

                            textChanged = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                      //  user.setText("ddd");
                    }
                });

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }
}
