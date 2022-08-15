package com.example.cashgrantsmobile.Pull;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Sync.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PullData extends AppCompatActivity {

    Button btnPull;
    String token = null;
    TextView progressCount, progressTarget, progressPercent;
    JSONArray remoteData;
    ProgressBar progressBar;

    public Integer getLastID() {
        Integer lastID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring ORDER BY id DESC LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            lastID = lastEmvDatabaseID.getInt(0);
        }

        progressCount = findViewById(R.id.progressFigure);

        progressCount.setText(lastID.toString());

        return lastID;
    }

    public void pullEmvData(Boolean init) {
        Activity_Splash_Login.NukeSSLCerts.nuke();
        btnPull = findViewById(R.id.btnPull);

        if (!init) {
            btnPull.setEnabled(false);
        }

        String url = "https://crg-finance-svr.entdswd.local/cgtracking/api/v1/staff/emvdatabasemonitoring/pulldata/" + getLastID();
//        String url = "http://192.168.254.112/cgtracking/public/api/v1/staff/emvdatabasemonitoring/pulldata/" + getLastID();

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(PullData.this);

        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // on below line we are displaying a success toast message.
                try {
                    remoteData = null;

                    JSONObject data = new JSONObject(response);
                    JSONArray dataSets = data.getJSONArray("data");

                    Integer totalDataCount = Integer.parseInt(data.getString("total_data_count"));
                    String status = data.getString("status");

                    if (status.matches("success")){

                        progressTarget = findViewById(R.id.progressFigureLast);
                        progressPercent = findViewById(R.id.progressCount);
                        progressBar = findViewById(R.id.progressBar);
                        Double localId = Double.valueOf(getLastID());
                        Double remoteId = Double.valueOf(totalDataCount);
                        Double progressCalc = localId / remoteId * 100;

                        progressTarget.setText(totalDataCount.toString());
                        progressPercent.setText(String.valueOf(progressCalc.intValue()));
                        progressBar.setProgress(progressCalc.intValue());

                        if (!init) {
                            Toasty.info(getApplicationContext(), "Now pulling the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();
                            MainActivity.sqLiteHelper.insertEmvData(dataSets);
                            if (progressPercent.getText().toString().matches("100")) {
                                btnPull.setEnabled(true);
                                Toasty.success(PullData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            } else {
                                pullEmvData(false);
                            }
                        }

                    }
                    else{
                        btnPull.setEnabled(true);
                        Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    btnPull.setEnabled(true);
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    JSONArray errors = data.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    String message = jsonMessage.getString("message");
                    Toasty.warning(PullData.this, message, Toast.LENGTH_SHORT, true).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    Toasty.warning(PullData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                }
                catch (Exception e) {
                    Toasty.error(PullData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
                }
            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        // below line is to make
        // a json object request.
        queue.add(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_data);

        btnPull = findViewById(R.id.btnPull);

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        getLastID();
        pullEmvData(true);

        btnPull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pullEmvData(false);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(PullData.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
