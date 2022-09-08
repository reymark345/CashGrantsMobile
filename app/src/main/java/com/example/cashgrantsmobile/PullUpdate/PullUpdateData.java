package com.example.cashgrantsmobile.PullUpdate;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PullUpdateData extends AppCompatActivity {

    Button btnPullUpdate;
    String token = null;
    TextView progressCount, progressTarget, progressPercent, textView1;
    JSONArray remoteData;
    ProgressBar progressBar;
    String pullstatus = "uncomplete";


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

    public void updateEmvData() {
        Activity_Splash_Login.NukeSSLCerts.nuke();

        progressPercent = findViewById(R.id.progressCount);
        progressCount = findViewById(R.id.progressFigure);
        progressTarget = findViewById(R.id.progressFigureLast);
        progressBar = findViewById(R.id.progressBar);

        btnPullUpdate = findViewById(R.id.btnPullUpdate);
        btnPullUpdate.setEnabled(false);

        String url = BASE_URL + "/api/v1/staff/emvdatabasemonitoring/updater";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(PullUpdateData.this);

        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // on below line we are displaying a success toast message.
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray dataSets = data.getJSONArray("data");
                    Double counter = 0.00;
                    Double progressFormula = 0.00;
                    Double dLength = Double.valueOf(dataSets.length());
                    String HHID = "";
                    progressTarget.setText(String.valueOf(dataSets.length()));

                    String status = data.getString("status");

                    if (status.matches("success")){
                        Toasty.info(getApplicationContext(), "Now updating local data. Please wait!", Toast.LENGTH_SHORT, true).show();
                        for (int i = 0; i < dataSets.length(); i++) {
                            counter++;
                            JSONObject jsonData = dataSets.getJSONObject(i);
                            HHID = jsonData.getString("hh_id");
                            sqLiteHelper.updateEmvMonitoring(jsonData.getString("validated_at"), jsonData.getString("id"));

                            progressFormula = counter / dLength * 100;
                            progressCount.setText(String.valueOf(counter.intValue()));
                            progressPercent.setText(String.valueOf(progressFormula.intValue()));
                            progressBar.setProgress(progressFormula.intValue());
                        }

                        if (progressPercent.getText().toString().matches("100")) {
                            Toasty.success(PullUpdateData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            btnPullUpdate.setEnabled(true);
                        }

                        sqLiteHelper.storeLogs("update", HHID, "Successfully update data");

                    }
                    else{
                        Toasty.error(getApplicationContext(), "Error on updating the  data.", Toast.LENGTH_SHORT, true).show();
                        sqLiteHelper.storeLogs("error", "", "Error on updating the data");
                    }

                } catch (JSONException e) {
                    queue.cancelAll(this);
                    btnPullUpdate.setEnabled(true);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    JSONArray errors = data.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    String message = jsonMessage.getString("message");
                    Toasty.warning(PullUpdateData.this, message, Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Update: " + message);
                } catch (JSONException | UnsupportedEncodingException e) {
                    btnPullUpdate.setEnabled(true);
                    Toasty.warning(PullUpdateData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Update: Exception.");
                }
                catch (Exception e) {
                    btnPullUpdate.setEnabled(true);
                    Toasty.error(PullUpdateData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Update: Network not found.");
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

    public void pullEmvData(Boolean init) {
        Activity_Splash_Login.NukeSSLCerts.nuke();
        btnPullUpdate = findViewById(R.id.btnPullUpdate);

        btnPullUpdate.setEnabled(false);

        String url = BASE_URL + "/api/v1/staff/emvdatabasemonitoring/pulldata/" + getLastID();


        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(PullUpdateData.this);

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

                        progressCount = findViewById(R.id.progressFigure);
                        progressTarget = findViewById(R.id.progressFigureLast);
                        progressPercent = findViewById(R.id.progressCount);
                        progressBar = findViewById(R.id.progressBar);
                        textView1 = findViewById(R.id.textView1);
                        Double localId = Double.valueOf(getLastID());
                        Double remoteId = Double.valueOf(totalDataCount);
                        Double progressCalc = localId / remoteId * 100;

                        progressTarget.setText(totalDataCount.toString());
                        progressPercent.setText(String.valueOf(progressCalc.intValue()));
                        progressBar.setProgress(progressCalc.intValue());

                        if (init) {
                            btnPullUpdate.setEnabled(true);
                            if (progressCalc.intValue() == 100) {
                                progressTarget.setText("0");
                                progressPercent.setText("0");
                                progressBar.setProgress(0);
                                textView1.setText("Update Data");
                                pullstatus = "completed";
                                progressCount.setText("0");
                                btnPullUpdate.setText("UPDATE");
                            }
                        }

                        if (!init) {
                            MainActivity.sqLiteHelper.insertEmvData(dataSets);
                            if (progressPercent.getText().toString().matches("100")) {
                                btnPullUpdate.setEnabled(true);
                                Toasty.success(PullUpdateData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                                sqLiteHelper.storeLogs("pull", "", "Pull Data Completed.");
                            } else {
                                sqLiteHelper.storeLogs("pull", "", "Pull Data Successfully.");
                                pullEmvData(false);
                            }
                        }

                    }
                    else{
                        btnPullUpdate.setEnabled(true);
                        sqLiteHelper.storeLogs("error", "", "Pull: Error on pulling data.");
                        Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    btnPullUpdate.setEnabled(true);
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnPullUpdate.setEnabled(true);
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    JSONArray errors = data.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    String message = jsonMessage.getString("message");
                    Toasty.warning(PullUpdateData.this, message, Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Pull: " +  message);
                } catch (JSONException | UnsupportedEncodingException e) {
                    sqLiteHelper.storeLogs("error", "", "Pull: Error Exception Found.");
                    Toasty.warning(PullUpdateData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                }
                catch (Exception e) {
                    queue.cancelAll(this);
                    sqLiteHelper.storeLogs("error", "", "Pull: Network not found.");
                    Toasty.error(PullUpdateData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
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
        setContentView(R.layout.pull_update_data);

        btnPullUpdate = findViewById(R.id.btnPullUpdate);

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        getLastID();
        pullEmvData(true);

        btnPullUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pullstatus.matches("completed")) {
                    updateEmvData();
                } else {
                    Toasty.info(getApplicationContext(), "Now pulling the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();
                    pullEmvData(false);
                }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(PullUpdateData.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
