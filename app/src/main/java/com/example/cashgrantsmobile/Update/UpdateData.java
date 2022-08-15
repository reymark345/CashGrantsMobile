package com.example.cashgrantsmobile.Update;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Internet.NetworkChangeListener;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Sync.SyncData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class UpdateData extends AppCompatActivity {
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    Button btnUpdate;
    String token = null;
    TextView progressCount, progressTarget, progressPercent;
    ProgressBar progressBar;

    public void updateEmvData() {
        Activity_Splash_Login.NukeSSLCerts.nuke();

        progressPercent = findViewById(R.id.progressCount);
        progressCount = findViewById(R.id.progressFigure);
        progressTarget = findViewById(R.id.progressFigureLast);
        progressBar = findViewById(R.id.progressBar);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnUpdate.setEnabled(false);

        String url = BASE_URL + "/api/v1/staff/emvdatabasemonitoring/updater";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(UpdateData.this);

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
                    progressTarget.setText(String.valueOf(dataSets.length()));

                    String status = data.getString("status");

                    if (status.matches("success")){
                        Toasty.info(getApplicationContext(), "Now updating local data. Please wait!", Toast.LENGTH_SHORT, true).show();
                        for (int i = 0; i < dataSets.length(); i++) {
                            counter++;
                            JSONObject jsonData = dataSets.getJSONObject(i);
                            sqLiteHelper.updateEmvMonitoring(jsonData.getString("validated_at"), jsonData.getString("id"));

                            progressFormula = counter / dLength * 100;
                            progressCount.setText(String.valueOf(counter.intValue()));
                            progressPercent.setText(String.valueOf(progressFormula.intValue()));
                            progressBar.setProgress(progressFormula.intValue());
                        }

                        if (progressPercent.getText().toString().matches("100")) {
                            Toasty.success(UpdateData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            btnUpdate.setEnabled(true);
                        }

                    }
                    else{
                        Toasty.error(getApplicationContext(), "Error on updating the  data.", Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    btnUpdate.setEnabled(true);
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
                    Toasty.warning(UpdateData.this, message, Toast.LENGTH_SHORT, true).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    btnUpdate.setEnabled(true);
                    Toasty.warning(UpdateData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                }
                catch (Exception e) {
                    btnUpdate.setEnabled(true);
                    Toasty.error(UpdateData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
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
        setContentView(R.layout.update_data);

        RequestQueue queue = Volley.newRequestQueue(UpdateData.this);

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UpdateData.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Please Confirm to Update Data")
                        .setConfirmText("Sync Now")
                        .showCancelButton(false)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                                registerReceiver(networkChangeListener, filter);
                                if (networkChangeListener.connection == true){
                                    updateEmvData();
                                    sDialog.dismiss();
                                }
                            }
                        }).show();
                }
            });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(UpdateData.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}