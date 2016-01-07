package com.example.stefan.androidwebsockets;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends Activity {

    private EditText text;
    private TextView user;
    private WebSocketClient mWebSocketClient;
    private boolean textChanged = true;
    private JSONObject json;
    private int stringStartPosition, stringEndPosition, stringEndPositionBefore, color;
    private String stringText, stringEditMethod, uuid, value;
    public static final String ADD = "add";
    public static final String REPLACE = "replace";
    public static final String DELETE = "delete";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("username");
        }

        user = (TextView) findViewById(R.id.username);
        text = (EditText) findViewById(R.id.task_textfield);

        json = new JSONObject();
        uuid = UUID.randomUUID().toString();
        color = generateRandomColor();
        text.setTextColor(color);
        connectToWebSocket();

        // Add event listener
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (textChanged) {
                    stringStartPosition = start;
                    String hexColorValue = convertIntColorToHexColor(color);
                    if ((before == 0) && (count >= 1)) {
                        stringEditMethod = ADD;
                        stringEndPosition = stringStartPosition + count;
                        stringText = text.getText().toString().substring(stringStartPosition, stringEndPosition);
                    } else if ((count > 0) && (before > 0)) {
                        stringEndPosition = stringStartPosition + count;
                        stringEndPositionBefore = stringStartPosition + before;
                        stringText = text.getText().toString().substring(stringStartPosition, stringEndPosition);
                        stringEditMethod = REPLACE;
                    } else {
                        stringEndPosition = stringStartPosition + before;
                        stringEditMethod = DELETE;
                    }

                    try {
                        json.put("uuid", uuid);
                        json.put("user", value);
                        json.put("editMethod", stringEditMethod);
                        json.put("startPos", stringStartPosition);
                        json.put("endPos", stringEndPosition);
                        json.put("endPosBefore", stringEndPositionBefore);
                        json.put("textColor", hexColorValue);
                        json.put("text", stringText);
                    } catch (JSONException e) {
                        Log.i("JSON Exception", e.getStackTrace().toString());
                    }

                    //Send json
                    mWebSocketClient.send(json.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                final Handler handler = new Handler();

                Runnable task = new Runnable() {
                    @Override
                    public void run() {
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
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            textChanged = false;
                            JSONObject resultJson = new JSONObject(message);
                            String userId = resultJson.getString("uuid");
                            if (!userId.equals(uuid)) {
                                String editMethod = resultJson.getString("editMethod");
                                int startPos = resultJson.getInt("startPos");
                                int endPos = resultJson.getInt("endPos");
                                int textColor = convertHexColorToIntColor(resultJson.getString("textColor"));
                                String resultText = resultJson.getString("text");
                                Spannable span = new SpannableString(resultText);
                                span.setSpan(new ForegroundColorSpan(textColor), 0, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                if (editMethod.equals(ADD)) {
                                    text.getText().insert(startPos, span);
                                } else if (editMethod.equals(REPLACE)) {
                                    int endPosBefore = resultJson.getInt("endPosBefore");
                                    text.getText().replace(startPos, endPosBefore, span);
                                } else if (editMethod.equals(DELETE)) {
                                    text.getText().replace(startPos, endPos, "");
                                }
                            }
                            user.setText(resultJson.get("user").toString() + " is typing");
                            textChanged = true;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    /**
     * Generates random integer color value
     * @return
     */
    private int generateRandomColor(){
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }

    /**
     * Convert integer color value to hexadecimal color value
     * @param intColor
     * @return
     */
    private String convertIntColorToHexColor(int intColor){
        String hexColor = String.format("#%06X", (0xFFFFFF & intColor));
        return hexColor;
    }

    /**
     * Convert hexadecimal color value to integer color value
     * @param hexColor
     * @return
     */
    private int convertHexColorToIntColor(String hexColor){
        int intColor = Color.parseColor(hexColor);
        return intColor;
    }
}
