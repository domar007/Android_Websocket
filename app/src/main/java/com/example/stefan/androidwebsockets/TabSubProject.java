package com.example.stefan.androidwebsockets;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by waelgabsi on 05.01.16.
 */

public class TabSubProject extends Fragment {
    private final static String NANOME_SESSIONID = "nanomeSessionId";
    private SaveSubProjectTask saveSubProjectTask;
    private Connection connection;
    private OnSelectLastSelectedTabListener onSelectLastSelectedTabListener;
    private SessionId sessionId;
    private Bundle args;
    private Timer timer;
    private String idEx, lockId;
    private String[] params;
    private boolean changeText = true;
    private EditText field = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_subproject, container, false);
        connection = new Connection();
        field = (EditText) view.findViewById(R.id.task_textfield);
        field.setEllipsize(null);
        sessionId = SessionId.getInstance();
        timer = new Timer();
        args = getArguments();
        String text =  args.getString("subProjectText");
        lockId = args.getString("subProjectLockId");
        idEx = args.getString("idex");
        // Save lockId on screen rotation
        if (savedInstanceState != null) {
            if (savedInstanceState.getSerializable("subProjectLockId") != null) {
                lockId = (String) savedInstanceState.getSerializable("subProjectLockId");
            }
        }
        field.setText(text);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onSelectLastSelectedTabListener = (OnSelectLastSelectedTabListener) context;
        } catch (ClassCastException castException) {
            castException.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (changeText) {
                    onSelectLastSelectedTabListener.selectLastSelectedTabText();
                    params = new String[]{sessionId.getSessionId(), lockId, idEx, field.getText().toString()};
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                                saveSubProjectTask = new SaveSubProjectTask();
                                saveSubProjectTask.execute(params);
                        }
                    }, 5000);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("subProjectLockId", lockId);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (saveSubProjectTask != null) {
            saveSubProjectTask.cancel(true);
        }
    }

    /**
     *  Async Task to save the subProject
     */
    private class SaveSubProjectTask extends AsyncTask<String[], Void, String> {

        protected String doInBackground(String[]... params) {
            String[] passed = params[0];
            String sessionId = passed[0];
            String lockId = passed[1];
            String idEx = passed[2];
            String text = passed[3];

            String serverResponse = null;
            JSONObject json = new JSONObject();
            try {
                json.put("idex", idEx);
                json.put("lockid", lockId);
                json.put("text", text);
                serverResponse = connection.doPostRequestWithAdditionalDataAndHeader("http://beta.taskql.com/rest/api/1/projectpart/write", json.toString(), NANOME_SESSIONID + "=" + sessionId);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverResponse;
        }

        protected void onPostExecute(String results) {
            if (results != null) {
                try {
                    JSONObject result = new JSONObject(results);
                    int errorCode = result.getInt("errorCode");
                    switch (errorCode) {
                        case 0:
                            lockId = result.getString("lockid");
                            break;
                        case 3:
                            showDialogOnDeletedSubProject();
                            break;
                        case 4:
                            showDialogOnLockIdChanged();
                            break;
                        default:break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Show dialog when an subProject was deleted
     */
    protected void showDialogOnDeletedSubProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Das Teilprojekt wurde gelöscht.")
            .setCancelable(false)
            .setPositiveButton("Neu laden", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            })
            .setNegativeButton("Beenden", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show dialog when lockId has changed
     */
    protected void showDialogOnLockIdChanged() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Fehler beim Speichern des Teilprojekts.")
            .setCancelable(false)
            .setPositiveButton("Neu laden", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    onSelectLastSelectedTabListener.selectLastSelectedTab();
                }
            })
            .setNegativeButton("Beenden", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finish();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void changeTabText(String text) {
        changeText = false;
        field.setText(text);
        changeText = true;
    }

    public void setLockId(String lockId) {
        Bundle args = getArguments();
        args.putString("subProjectLockId", lockId);
        this.lockId = args.getString("subProjectLockId");
    }
}
