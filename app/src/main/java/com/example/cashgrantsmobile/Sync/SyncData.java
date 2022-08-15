package com.example.cashgrantsmobile.Sync;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
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
    TextView progressCount, progressTarget, progressPercent;
    JSONArray remoteData;
    ProgressBar progressBar;
    Integer countEmvDetails = 0;

    ArrayList<String> lst = new ArrayList<String>();
    GridView gvMain;
    String[] data = {};
    ArrayAdapter<String> adapter;


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


    public void syncEmvData() {


        lst.addAll(Arrays.asList(data));
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,lst);

        gvMain = (GridView) findViewById(R.id.gridView);
        gvMain.setAdapter(adapter);

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        Cursor emvDetailsList = sqLiteHelper.getData("SELECT id, fullname, hh_id, client_status, address, sex, hh_set_group, assigned_staff, minor_grantee, contact, current_grantee_card_release_data, current_grantee_card_release_place, current_grantee_card_release_by, current_grantee_is_available, current_grantee_reason, current_grantee_card_number, other_card_number_1, other_card_holder_name_1, other_card_number_2, other_card_holder_name_2, other_card_number_3, other_card_holder_name_3, other_card_is_available, other_card_reason, nma_amount, nma_date_claimed, nma_reason, nma_remarks, pawn_name_of_lender, pawn_date, pawn_retrieved_date, pawn_status, pawn_reason, pawn_offense_history, pawn_offense_date, pawn_remarks, pawn_intervention_staff, pawn_other_details, informant_full_name, accomplish_by_full_name, accomplish_e_signature, informant_e_signature, attested_by_e_signature, current_cash_card_picture, beneficiary_picture, attested_by_full_name, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, emv_database_monitoring_id, current_grantee_card_number_series, created_at FROM emv_database_monitoring_details");

        while (emvDetailsList.moveToNext()) {

            String id = emvDetailsList.getString(0);
            String fullname = emvDetailsList.getString(1);
            String hh_id = emvDetailsList.getString(2);
            String client_status = emvDetailsList.getString(3);
            String address = emvDetailsList.getString(4);
            String sex = emvDetailsList.getString(5);
            String hh_set_group = emvDetailsList.getString(6);
            String assigned_staff = emvDetailsList.getString(7);
            String minor_grantee = emvDetailsList.getString(8);
            String contact = emvDetailsList.getString(9);
            String current_grantee_card_release_data = emvDetailsList.getString(10);
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
            String accomplish_e_signature = emvDetailsList.getString(40);
            String informant_e_signature = emvDetailsList.getString(41);
            String attested_by_e_signature = emvDetailsList.getString(42);
            String current_cash_card_picture = emvDetailsList.getString(43);
            String beneficiary_picture = emvDetailsList.getString(44);
            String attested_by_full_name = emvDetailsList.getString(45);
            String other_card_number_series_1 = emvDetailsList.getString(46);
            String other_card_number_series_2 = emvDetailsList.getString(47);
            String other_card_number_series_3 = emvDetailsList.getString(48);
            String emv_database_monitoring_id = emvDetailsList.getString(49);
            String current_grantee_card_number_series = emvDetailsList.getString(50);
            String user_id = getUserId().toString();
            String created_at = emvDetailsList.getString(51);

            Activity_Splash_Login.NukeSSLCerts.nuke();

            String url = "http://192.168.1.9/cgtracking/public/api/v1/staff/emvdatabasemonitoring/sync/";

            // creating a new variable for our request queue
            RequestQueue queue = Volley.newRequestQueue(SyncData.this);

            // in this we are calling a post method.
            StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // on below line we are displaying a success toast message.
                    try {
                        Double progressCC = 0.00;
                        remoteData = null;

                        JSONObject data = new JSONObject(response);
                        JSONArray dataSets = data.getJSONArray("data");

                        Integer totalDataCount = Integer.parseInt(data.getString("total_data_count"));
                        String status = data.getString("status");
                        Toasty.info(getApplicationContext(), "Now syncing the data from the server. Please wait!", Toast.LENGTH_SHORT, true).show();


                        if (status.matches("success")){
                            progressCC++;

                            progressPercent = findViewById(R.id.progressCount);
                            progressBar = findViewById(R.id.progressBar);
                            Double progressCalc = progressCC / countEmvDetails * 100;

                            progressPercent.setText(String.valueOf(progressCalc.intValue()));
                            progressBar.setProgress(progressCalc.intValue());

                            lst.add("Success entry");
                            gvMain.setAdapter(adapter);

                            MainActivity.sqLiteHelper.insertEmvData(dataSets);

                            if (progressPercent.getText().toString().matches("100")) {
                                btnSync.setEnabled(true);
                                Toasty.success(SyncData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            }

                        }
                        else{
                            btnSync.setEnabled(true);
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
                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        JSONObject data = new JSONObject(responseBody);
                        JSONArray errors = data.getJSONArray("errors");
                        JSONObject jsonMessage = errors.getJSONObject(0);
                        String message = jsonMessage.getString("message");
                        Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                    } catch (JSONException | UnsupportedEncodingException e) {
                        Toasty.warning(SyncData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                    }
                    catch (Exception e) {
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
                @Override
                protected Map<String, String> getParams() {
                    // below line we are creating a map for
                    // storing our values in key and value pair.
                    Map<String, String> params = new HashMap<String, String>();

                    // on below line we are passing our key
                    // and value pair to our parameters.
                    params.put("id", id);
                    params.put("fullname", fullname);
                    params.put("hh_id", hh_id);
                    params.put("client_status", client_status);
                    params.put("address", address);
                    params.put("sex", sex);
                    params.put("hh_set_group", hh_set_group);
                    params.put("assigned_staff", assigned_staff);
                    params.put("minor_grantee", minor_grantee);
                    params.put("contact", contact);
                    params.put("current_grantee_card_release_data", current_grantee_card_release_data);
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
                    params.put("accomplish_e_signature", accomplish_e_signature);
                    params.put("informant_e_signature", informant_e_signature);
                    params.put("attested_by_e_signature", attested_by_e_signature);
                    params.put("current_cash_card_picture", current_cash_card_picture);
                    params.put("beneficiary_picture", beneficiary_picture);
                    params.put("attested_by_full_name", attested_by_full_name);
                    params.put("other_card_number_series_1", other_card_number_series_1);
                    params.put("other_card_number_series_2", other_card_number_series_2);
                    params.put("other_card_number_series_3", other_card_number_series_3);
                    params.put("emv_database_monitoring_id", emv_database_monitoring_id);
                    params.put("current_grantee_card_number_series", current_grantee_card_number_series);
                    params.put("user_id", user_id);
                    params.put("created_at", created_at);
                    // at last we are
                    // returning our params.
                    return params;
                }
            };
            queue.add(request);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_data);

        getCountEmvDetails();

        btnSync = findViewById(R.id.btnSync);

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
                                if (networkChangeListener.connection ==true){
                                    syncEmvData();
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