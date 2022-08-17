package com.example.cashgrantsmobile.Sync;

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
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Internet.NetworkChangeListener;

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

public class SyncData extends AppCompatActivity {
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    Button btnSync;
    String token = null;
    Toolbar mainToolbar;
    TextView progressCount, progressTarget, progressPercent;
    ProgressBar progressBar;
    Integer countEmvDetails = 0;

    ArrayList<String> lst = new ArrayList<String>();
    ArrayList<String> lst2 = new ArrayList<String>();
    GridView gvMain, gvMain2;
    String[] data = {};
    String[] data2 = {};
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    RequestQueue queue;


    public Integer getUserId() {
        Integer userID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT user_id FROM Api LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            userID = lastEmvDatabaseID.getInt(0);
        }
        return userID;
    }

    public void getCountEmvDetails() {
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring_details");
        lastEmvDatabaseID.getCount();
        while (lastEmvDatabaseID.moveToNext()) {
            countEmvDetails = lastEmvDatabaseID.getInt(0);
        }

        progressTarget = findViewById(R.id.progressFigureLast);
        progressTarget.setText(countEmvDetails.toString());
    }

    public void updaterEmvMonitoring() {
        Activity_Splash_Login.NukeSSLCerts.nuke();

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        String url = BASE_URL + "/api/v1/staff/emvdatabasemonitoring/updater";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(SyncData.this);

        // in this we are calling a post method.
        StringRequest request = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // on below line we are displaying a success toast message.
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray dataSets = data.getJSONArray("data");

                    String status = data.getString("status");

                    if (status.matches("success")){
                        Toasty.info(getApplicationContext(), "Now updating local data. Please wait!", Toast.LENGTH_SHORT, true).show();
                        for (int i = 0; i < dataSets.length(); i++) {
                            JSONObject jsonData = dataSets.getJSONObject(i);
                            sqLiteHelper.updateEmvMonitoring(jsonData.getString("validated_at"), jsonData.getString("id"));
                        }

                        btnSync.setEnabled(true);
                    }
                    else{
                        Toasty.error(getApplicationContext(), "Error on updateing the  data.", Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    btnSync.setEnabled(true);
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
                    Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                } catch (JSONException | UnsupportedEncodingException e) {
                    btnSync.setEnabled(true);
                    Toasty.warning(SyncData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                }
                catch (Exception e) {
                    btnSync.setEnabled(true);
                    Toasty.error(SyncData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
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

    public void syncEmvData() {
        Activity_Splash_Login.NukeSSLCerts.nuke();

        Toasty.info(getApplicationContext(), "Now syncing the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();

        lst.addAll(Arrays.asList(data));
        lst2.addAll(Arrays.asList(data2));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lst);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lst2);

        final Double[] progressCC = {0.00};

        gvMain = (GridView) findViewById(R.id.gridView);
        gvMain2 = (GridView) findViewById(R.id.gridView1);

        gvMain.setAdapter(adapter);
        gvMain2.setAdapter(adapter2);

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        Cursor emvDetailsList = sqLiteHelper.getData("SELECT id, full_name, hh_id, client_status, address, sex, hh_set_group, assigned_staff, minor_grantee, contact, current_grantee_card_release_date, current_grantee_card_release_place, current_grantee_card_release_by, current_grantee_is_available, current_grantee_reason, current_grantee_card_number, other_card_number_1, other_card_holder_name_1, other_card_number_2, other_card_holder_name_2, other_card_number_3, other_card_holder_name_3, other_card_is_available, other_card_reason, nma_amount, nma_date_claimed, nma_reason, nma_remarks, pawn_name_of_lender, pawn_date, pawn_retrieved_date, pawn_status, pawn_reason, pawn_offense_history, pawn_offense_date, pawn_remarks, pawn_intervention_staff, pawn_other_details, informant_full_name, accomplish_by_full_name, accomplish_e_signature, informant_e_signature, attested_by_e_signature, current_cash_card_picture, beneficiary_picture, attested_by_full_name, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, emv_database_monitoring_id, current_grantee_card_number_series, created_at, other_card_is_available_2, other_card_is_available_3, other_card_reason_2, other_card_reason_3, pawn_loaned_amount, pawn_lender_address, pawn_interest FROM emv_database_monitoring_details");

        while (emvDetailsList.moveToNext()) {
            Integer id = emvDetailsList.getInt(0);
            String full_name = emvDetailsList.getString(1);
            String hh_id = emvDetailsList.getString(2);
            String client_status = emvDetailsList.getString(3);
            String address = emvDetailsList.getString(4);
            String sex = emvDetailsList.getString(5);
            String hh_set_group = emvDetailsList.getString(6);
            String assigned_staff = emvDetailsList.getString(7);
            String minor_grantee = emvDetailsList.getString(8);
            String contact = emvDetailsList.getString(9);
            String current_grantee_card_release_date = emvDetailsList.getString(10);
            String current_grantee_card_release_place = emvDetailsList.getString(11);
            String current_grantee_card_release_by = emvDetailsList.getString(12);
            String current_grantee_is_available = emvDetailsList.getString(13);
            String current_grantee_reason = emvDetailsList.getString(14);
            String current_grantee_card_number = emvDetailsList.getString(15);
            String other_card_number_1 = emvDetailsList.getString(16);
            String other_card_holder_name_1 = emvDetailsList.getString(17);
            String other_card_number_2 = emvDetailsList.getString(18);
            String other_card_holder_name_2 = emvDetailsList.getString(19);
            String other_card_number_3 = emvDetailsList.getString(20);
            String other_card_holder_name_3 = emvDetailsList.getString(21);
            String other_card_is_available = emvDetailsList.getString(22);
            String other_card_reason = emvDetailsList.getString(23);
            String nma_amount = emvDetailsList.getString(24);
            String nma_date_claimed = emvDetailsList.getString(25);
            String nma_reason = emvDetailsList.getString(26);
            String nma_remarks = emvDetailsList.getString(27);
            String pawn_name_of_lender = emvDetailsList.getString(28);
            String pawn_date = emvDetailsList.getString(29);
            String pawn_retrieved_date = emvDetailsList.getString(30);
            String pawn_status = emvDetailsList.getString(31);
            String pawn_reason = emvDetailsList.getString(32);
            String pawn_offense_history = emvDetailsList.getString(33);
            String pawn_offense_date = emvDetailsList.getString(34);
            String pawn_remarks = emvDetailsList.getString(35);
            String pawn_intervention_staff = emvDetailsList.getString(36);
            String pawn_other_details = emvDetailsList.getString(37);
            String informant_full_name = emvDetailsList.getString(38);
            String accomplish_by_full_name = emvDetailsList.getString(39);
            byte[] accomplish_e_signature = emvDetailsList.getBlob(40);
            byte[] informant_e_signature = emvDetailsList.getBlob(41);

            byte[] attested_by_e_signature = emvDetailsList.getBlob(42);
            byte[] current_cash_card_picture = emvDetailsList.getBlob(43);
            byte[] beneficiary_picture = emvDetailsList.getBlob(44);
            String attested_by_full_name = emvDetailsList.getString(45);
            String other_card_number_series_1 = emvDetailsList.getString(46);
            String other_card_number_series_2 = emvDetailsList.getString(47);
            String other_card_number_series_3 = emvDetailsList.getString(48);
            String emv_database_monitoring_id = emvDetailsList.getString(49);
            String current_grantee_card_number_series = emvDetailsList.getString(50);
            String user_id = getUserId().toString();
            String created_at = emvDetailsList.getString(51);
            String other_card_is_available_2 = emvDetailsList.getString(52);
            String other_card_is_available_3 = emvDetailsList.getString(53);
            String other_card_reason_2 = emvDetailsList.getString(54);
            String other_card_reason_3 = emvDetailsList.getString(55);
            String pawn_loaned_amount = emvDetailsList.getString(56);
            String pawn_lender_address = emvDetailsList.getString(57);
            String pawn_interest = emvDetailsList.getString(58);

            String url = BASE_URL + "/api/v1/staff/emvdatabasemonitoringdetails/sync";


            Map<String, String> params = new HashMap<String, String>();

            params.put("full_name", full_name);
            params.put("hh_id", hh_id);
            params.put("client_status", client_status);
            params.put("address", address);
            params.put("sex", sex);
            params.put("hh_set_group", hh_set_group);
            params.put("assigned_staff", assigned_staff);
            params.put("minor_grantee", minor_grantee);
            params.put("contact", contact);
            params.put("current_grantee_card_release_date", current_grantee_card_release_date);
            params.put("current_grantee_card_release_place", current_grantee_card_release_place);
            params.put("current_grantee_card_release_by", current_grantee_card_release_by);
            params.put("current_grantee_is_available", current_grantee_is_available);
            params.put("current_grantee_reason", current_grantee_reason);
            params.put("current_grantee_card_number", current_grantee_card_number);
            params.put("other_card_number_1", other_card_number_1);
            params.put("other_card_holder_name_1", other_card_holder_name_1);
            params.put("other_card_number_2", other_card_number_2);
            params.put("other_card_holder_name_2", other_card_holder_name_2);
            params.put("other_card_number_3", other_card_number_3);
            params.put("other_card_holder_name_3", other_card_holder_name_3);
            params.put("other_card_is_available", other_card_is_available);
            params.put("other_card_reason", other_card_reason);
            params.put("nma_amount", nma_amount);
            params.put("nma_date_claimed", nma_date_claimed);
            params.put("nma_reason", nma_reason);
            params.put("nma_remarks", nma_remarks);
            params.put("pawn_name_of_lender", pawn_name_of_lender);
            params.put("pawn_date", pawn_date);
            params.put("pawn_retrieved_date", pawn_retrieved_date);
            params.put("pawn_status", pawn_status);
            params.put("pawn_reason", pawn_reason);
            params.put("pawn_offense_history", pawn_offense_history);
            params.put("pawn_offense_date", pawn_offense_date);
            params.put("pawn_remarks", pawn_remarks);
            params.put("pawn_intervention_staff", pawn_intervention_staff);
            params.put("pawn_other_details", pawn_other_details);
            params.put("informant_full_name", informant_full_name);
            params.put("accomplish_by_full_name", accomplish_by_full_name);
            params.put("accomplish_e_signature", String.valueOf(accomplish_e_signature));
            params.put("informant_e_signature", String.valueOf(informant_e_signature));
            params.put("attested_by_e_signature", String.valueOf(attested_by_e_signature));
            params.put("current_cash_card_picture", String.valueOf(current_cash_card_picture));
            params.put("beneficiary_picture", String.valueOf(beneficiary_picture));
            params.put("attested_by_full_name", attested_by_full_name);
            params.put("other_card_number_series_1", other_card_number_series_1);
            params.put("other_card_number_series_2", other_card_number_series_2);
            params.put("other_card_number_series_3", other_card_number_series_3);
            params.put("emv_database_monitoring_id", emv_database_monitoring_id);
            params.put("current_grantee_card_number_series", current_grantee_card_number_series);
            params.put("user_id", user_id);
            params.put("created_at", created_at);
            params.put("other_card_is_available_2", other_card_is_available_2);
            params.put("other_card_is_available_3", other_card_is_available_3);
            params.put("other_card_reason_2", other_card_reason_2);
            params.put("other_card_reason_3", other_card_reason_3);
            params.put("pawn_loaned_amount", pawn_loaned_amount);
            params.put("pawn_lender_address", pawn_lender_address);
            params.put("pawn_interest", pawn_interest);

            JsonObjectRequest request = new JsonObjectRequest(url, new JSONObject(params), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject data) {
                    // on below line we are displaying a success toast message.
                    try {
                        String status = data.getString("status");
                        String description = data.getString("description");
                        JSONObject dataObject = data.getJSONObject("data");
                        String household = dataObject.getString("hh_id");


                        Log.d("response d", String.valueOf(data));

                        if (status.matches("success")){

                            progressCC[0]++;

                            progressPercent = findViewById(R.id.progressCount);
                            progressBar = findViewById(R.id.progressBar);
                            progressCount = findViewById(R.id.progressFigure);
                            Double progressCalc = progressCC[0] / countEmvDetails * 100;

                            progressCount.setText(String.valueOf(progressCC[0].intValue()));
                            progressPercent.setText(String.valueOf(progressCalc.intValue()));
                            progressBar.setProgress(progressCalc.intValue());

                            lst.add(description + "household id: " + household);
                            gvMain.setAdapter(adapter);

                            if (progressPercent.getText().toString().matches("100")) {
                                Toasty.success(SyncData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                                Toasty.success(SyncData.this, "Updating local data please wait!", Toast.LENGTH_SHORT, true).show();
                                updaterEmvMonitoring();
                            }

                            MainActivity.sqLiteHelper.deleteEmvMonitoringDetails(id);

                        }
                        else{
                            btnSync.setEnabled(true);
                            lst2.add("Error on Pulling the data!");
                            gvMain2.setAdapter(adapter2);
                            Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                        }

                    } catch (JSONException e) {
                        btnSync.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // method to handle errors.
                    btnSync.setEnabled(true);
                    VolleyLog.e("Error: ", error.getMessage());
                    lst2.add(error.getMessage());
                    gvMain2.setAdapter(adapter2);
                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Integer responseCode = error.networkResponse.statusCode;
                        if (responseCode == 401) {
                            JSONObject data = new JSONObject(responseBody);
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            String message = jsonMessage.getString("message");
                            Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                            lst2.add(message);
                            gvMain2.setAdapter(adapter2);
                        } else if (responseCode == 404) {
                            JSONObject data = new JSONObject(responseBody);
                            String desc = data.getString("description");
                            Toasty.error(SyncData.this, "Error 404:" + desc, Toast.LENGTH_SHORT, true).show();
                            lst2.add(desc);
                            gvMain2.setAdapter(adapter2);
                        }
                    } catch (Exception e) {
                        Log.d("Error co", String.valueOf(e));
                        lst2.add(String.valueOf(e));
                        gvMain2.setAdapter(adapter2);
                        Toasty.error(SyncData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
                    }
                }

            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }

            };
            queue.add(request);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_data);

        queue = Volley.newRequestQueue(SyncData.this);


        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        getCountEmvDetails();

        btnSync = findViewById(R.id.btnSync);


        if (countEmvDetails == 0) {
            mainToolbar = findViewById(R.id.mainToolbar);
            mainToolbar.setTitle("No data to be sync!");

            btnSync.setEnabled(false);
        }

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(SyncData.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Please Confirm to Sync Data")
                        .setConfirmText("Sync Now")
                        .showCancelButton(false)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                                registerReceiver(networkChangeListener, filter);
                                if (networkChangeListener.connection == true){
                                    syncEmvData();
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
            Intent intent = new Intent(SyncData.this, MainActivity.class);
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