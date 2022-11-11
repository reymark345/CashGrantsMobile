package com.example.cashgrantsmobile.PullUpdate;

import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class PullPsgcData extends AppCompatActivity {

    Button btnPullPsgc;
    String token = null;
    TextView progressCount, progressTarget, progressPercent, textView1;
    JSONArray remoteData;
    ProgressBar progressBar;
    String pullstatus = "uncomplete";

    @SuppressLint("SetTextI18n")
    public Integer getLastID() {
        int lastID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM psgc ORDER BY id DESC LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            lastID = lastEmvDatabaseID.getInt(0);
        }

        progressCount = findViewById(R.id.progressFigurePsgc);

        progressCount.setText(Integer.toString(lastID));

        return lastID;
    }

    public void pullPsgcData(Boolean init) {
        Activity_Splash_Login.NukeSSLCerts.nuke();
        btnPullPsgc = findViewById(R.id.btnPullPsgc);

        btnPullPsgc.setEnabled(false);

        String url = BASE_URL + "/api/v1/staff/psgc";


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(PullPsgcData.this);

        // in this we are calling a post method.
        @SuppressLint("SetTextI18n") StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            // on below line we are displaying a success toast message.
            try {
                remoteData = null;

                JSONObject data = new JSONObject(response);
                JSONArray dataSets = data.getJSONArray("data");

                Integer totalDataCount = dataSets.length();
                String status = data.getString("status");


                if (status.matches("success")){
                    progressCount = findViewById(R.id.progressFigurePsgc);
                    progressTarget = findViewById(R.id.progressFigureLastPsgc);
                    progressPercent = findViewById(R.id.progressCountPsgc);
                    progressBar = findViewById(R.id.progressBarPsgc);
                    textView1 = findViewById(R.id.textView1);
                    Double localId = Double.valueOf(getLastID());
                    Double remoteId = Double.valueOf(totalDataCount);
                    Double progressCalc = localId / remoteId * 100;

                    progressTarget.setText(totalDataCount.toString());
                    progressPercent.setText(String.valueOf(progressCalc.intValue()));
                    progressBar.setProgress(progressCalc.intValue());

                    if (init) {
                        btnPullPsgc.setEnabled(true);
                        if (progressCalc.intValue() == 100) {
                            progressTarget.setText("0");
                            progressPercent.setText("0");
                            progressBar.setProgress(0);
                            textView1.setText("Update Data");
                            pullstatus = "completed";
                            progressCount.setText("0");
                            btnPullPsgc.setText("UPDATE");
                        }
                    }

                    if (!init) {
                        MainActivity.sqLiteHelper.insertPsgcData(dataSets);
                        if (progressPercent.getText().toString().matches("100")) {
                            btnPullPsgc.setEnabled(true);
                            Toasty.success(PullPsgcData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            sqLiteHelper.storeLogs("pull", "", "Pull Data Completed.");
                        } else {
                            sqLiteHelper.storeLogs("pull", "", "Pull Data Successfully.");
                            pullPsgcData(false);
                        }
                    }

                }
                else{
                    btnPullPsgc.setEnabled(true);
                    sqLiteHelper.storeLogs("error", "", "Pull: Error on pulling data.");
                    Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                }

            } catch (JSONException e) {
                Log.v(TAG,"nag error " + e);
                Toasty.error(getApplicationContext(), "Request to PULL Data Error!", Toast.LENGTH_SHORT, true).show();
                btnPullPsgc.setEnabled(true);
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnPullPsgc.setEnabled(true);
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject data = new JSONObject(responseBody);
                    JSONArray errors = data.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    String message = jsonMessage.getString("message");
                    Toasty.warning(PullPsgcData.this, message, Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Pull: " +  message);
                } catch (JSONException e) {
                    sqLiteHelper.storeLogs("error", "", "Pull: Error Exception Found.");
//                    Toasty.warning(PullUpdateData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                }
                catch (Exception e) {
                    Log.v(TAG,"not foundd" + e);
                    queue.cancelAll(this);
                    sqLiteHelper.storeLogs("error", "", "Pull: Network not found.");
                    Toasty.error(PullPsgcData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
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
        setContentView(R.layout.pull_psgc_data);

        btnPullPsgc = findViewById(R.id.btnPullPsgc);

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        getLastID();
        pullPsgcData(true);
        btnPullPsgc.setOnClickListener(view -> {
            if (pullstatus.matches("completed")) {
                new SweetAlertDialog(PullPsgcData.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Update Data and Pull?")
                        .setContentText("Please confirm to update changes")
                        .setConfirmText("Confirm")
                        .showCancelButton(false)
                        .setConfirmClickListener(sDialog -> {
                            sqLiteHelper.deletePSGC();
                            pullPsgcData(false);
                            sDialog.dismiss();
                            Toasty.info(getApplicationContext(), "Now pulling the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();
                        }).show();
            } else {
                Toasty.info(getApplicationContext(), "Now pulling the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();
                pullPsgcData(false);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent(PullPsgcData.this, MainActivity.class);
//            startActivity(intent);
//            finish();
            super.onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}
