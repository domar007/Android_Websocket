package com.example.stefan.androidwebsockets;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends Activity {

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
        text = (EditText) findViewById(R.id.edit_text);

        json = new JSONObject();
        connectToWebSocket();
        // uuid = UUID.randomUUID().toString();
        uuid = value;


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

               // handler.removeCallbacks(task);
              //  handler.post(task);
                Log.i("im writing right now!!",text.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i("im stopping now!!!!!!","");
               // Toast.makeText(getApplicationContext(), "STRING MESSAGE", 1000).show();
              //  user.setText("ddd");

                final Handler handler = new Handler();

                Runnable task = new Runnable() {
                    @Override
                    public void run() {

                        //   handler.postDelayed(this, 2000);
                        user.setText("");
                    }
                };
                handler.postDelayed(task, 1000);


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

                            user.setText(resultJson.get("user").toString() + " is typing");
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
