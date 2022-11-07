package com.example.cashgrantsmobile.Sync;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Internet.NetworkChangeListener;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.helpers.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
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
    TextView progressCount, progressTarget, progressPercent, tvStatus;
    ProgressBar progressBar;
    Integer countEmvDetails = 0;
    JSONArray remoteData;

    ArrayList<String> lst = new ArrayList<>();
    ArrayList<String> lst2 = new ArrayList<>();
    GridView gvMain, gvMain2;
    String[] data = {};
    String[] data2 = {};
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    RequestQueue queue;
    VolleyMultipartRequest request;
    final Double[] progressCC = {0.00};
    String pullStatus = "";



    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressLint("SetTextI18n")
    public Integer getCountEmvDetails() {
        int result = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_validation_details");
        result = lastEmvDatabaseID.getCount();
        lastEmvDatabaseID.close();
        return result;
    }

    @SuppressLint("SetTextI18n")
    public Integer getLastID() {
        int lastID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_validations ORDER BY id DESC LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            lastID = lastEmvDatabaseID.getInt(0);
        }
        lastEmvDatabaseID.close();

        progressCount = findViewById(R.id.progressFigure);

        progressCount.setText(Integer.toString(lastID));

        return lastID;
    }

    public void initialize_logs() {
        lst.addAll(Arrays.asList(data));
        lst2.addAll(Arrays.asList(data2));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lst);
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lst2);

        gvMain = (GridView) findViewById(R.id.gridView);
        gvMain2 = (GridView) findViewById(R.id.gridView1);

        gvMain.setAdapter(adapter);
        gvMain2.setAdapter(adapter2);
    }

    public void pullEmvData(Boolean init) {

        tvStatus.setText("Initializing Pulling...");

        if (pullStatus.matches("completed")) {
            syncEmvData();
            progressTarget.setText("0");
            progressPercent.setText("0");
            progressBar.setProgress(0);
            progressCount.setText("0");
            return;
        }

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        Activity_Splash_Login.NukeSSLCerts.nuke();

        String url = BASE_URL + "/api/v1/staff/emvvalidations/pulldata/" + getLastID();

        RequestQueue queue = Volley.newRequestQueue(SyncData.this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                remoteData = null;
                JSONObject data = new JSONObject(response);
                JSONArray dataSets = data.getJSONArray("data");
                Integer totalDataCount = 0;
                String status = "";
                try {
                    totalDataCount = Integer.parseInt(data.getString("total_data_count"));
                    status = data.getString("status");
                } catch (Exception e) {
                    Toasty.error(getApplicationContext(), "Error on API Conversion of Data!", Toasty.LENGTH_LONG).show();
                    btnSync.setEnabled(false);
                }
                if (status.matches("success")){
                    sqLiteHelper.storeLogs("pull", "", "Pull data successfully.");
                    progressCount = findViewById(R.id.progressFigure);
                    progressTarget = findViewById(R.id.progressFigureLast);
                    progressPercent = findViewById(R.id.progressCount);
                    progressBar = findViewById(R.id.progressBar);
                    Double localId = Double.valueOf(getLastID());
                    Double remoteId = Double.valueOf(totalDataCount);
                    Double progressCalc = localId / remoteId * 100;

                    progressTarget.setText(totalDataCount.toString());
                    progressPercent.setText(String.valueOf(progressCalc.intValue()));
                    progressBar.setProgress(progressCalc.intValue());

                    if (init) {
                        initialize_logs();
                        btnSync.setEnabled(true);
                        tvStatus.setText("Checking pulled data...");
                        if (progressCalc.intValue() == 100) {
                            lst.add("Pull data omitted.");
                            progressTarget.setText("0");
                            progressPercent.setText("0");
                            progressBar.setProgress(0);
                            progressCount.setText("0");
                            pullStatus = "completed";
                            tvStatus.setText("Preparing for syncing...");
                        }
                    }

                    if (!init) {
                        MainActivity.sqLiteHelper.insertEmvData(dataSets);
                        if (progressPercent.getText().toString().matches("100")) {
                            btnSync.setEnabled(true);
                            Toasty.success(SyncData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                            sqLiteHelper.storeLogs("pull", "", "Pull Data Completed.");
                            initialize_logs();
                            lst.add("Pull data completed!.");
                            pullStatus = "completed";
                        } else {
                            sqLiteHelper.storeLogs("pull", "", "Pull Data Successfully.");
                            pullEmvData(false);
                        }
                    }

                }
                else{
                    btnSync.setEnabled(true);
                    sqLiteHelper.storeLogs("error", "", "Pull: Error on pulling data.");
                    initialize_logs();
                    lst2.add("Error on pulling data.");
                }

            } catch (JSONException e) {
                initialize_logs();
                lst2.add("Request to PULL Data Error!");
                btnSync.setEnabled(true);
                e.printStackTrace();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnSync.setEnabled(true);
                initialize_logs();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject data = new JSONObject(responseBody);
                    JSONArray errors = data.getJSONArray("errors");
                    JSONObject jsonMessage = errors.getJSONObject(0);
                    String message = jsonMessage.getString("message");
                    Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                    sqLiteHelper.storeLogs("error", "", "Pull: " +  message);
                    lst2.add("Pull: " +  message);
                } catch (JSONException e) {
                    sqLiteHelper.storeLogs("error", "", "Pull: Error Exception Found.");
                    lst2.add("Pull: Error Exception Found.");
                }
                catch (Exception e) {
                    queue.cancelAll(this);
                    sqLiteHelper.storeLogs("error", "", "Pull: Network not found.");
                    lst2.add("Pull: Network not found.");
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

    public void updateEmvValidations() {
        Activity_Splash_Login.NukeSSLCerts.nuke();

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        tvStatus.setText("Initializing update...");

        String url = BASE_URL + "/api/v1/staff/emvvalidations/updater";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(SyncData.this);

        // in this we are calling a post method.
        Toasty.info(getApplicationContext(), "Now updating local data. Please wait!", Toast.LENGTH_SHORT, true).show();
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            // on below line we are displaying a success toast message.
            try {
                JSONObject data = new JSONObject(response);
                JSONArray dataSets = data.getJSONArray("data");

                String status = data.getString("status");
                if (status.matches("success")){
                    initialize_logs();
                    lst.add("Update data succeed.");
                    tvStatus.setText("Updating success...");
                    progressCount = findViewById(R.id.progressFigure);
                    progressTarget = findViewById(R.id.progressFigureLast);
                    progressPercent = findViewById(R.id.progressCount);
                    progressBar = findViewById(R.id.progressBar);

                    progressTarget.setText(String.valueOf(dataSets.length()));

                    for (int i = 0; i < dataSets.length(); i++) {
                        Double localId = Double.valueOf(i + 1);
                        Double remoteId = Double.valueOf(dataSets.length());
                        Double progressCalc = localId / remoteId * 100;
                        progressCount.setText(String.valueOf(i + 1));
                        progressPercent.setText(String.valueOf(progressCalc.intValue()));
                        progressBar.setProgress(progressCalc.intValue());
                        JSONObject jsonData = dataSets.getJSONObject(i);
                        sqLiteHelper.updateEmvValidations(jsonData.getString("validated_at"), jsonData.getString("id"));
                    }
                    sqLiteHelper.storeLogs("update", "", "Update Data Completed.");
                    btnSync.setEnabled(true);
                }
                else{
                    Toasty.error(getApplicationContext(), "Error on updating the data.", Toast.LENGTH_SHORT, true).show();
                    initialize_logs();
                    lst.add("Error on updating the data.");
                    sqLiteHelper.storeLogs("error", "", "Update Data Error.");
                }

            } catch (JSONException e) {
                btnSync.setEnabled(true);
                e.printStackTrace();
            }
        }, error -> {
            // method to handle errors.
            try {
                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                JSONArray errors = data.getJSONArray("errors");
                JSONObject jsonMessage = errors.getJSONObject(0);
                String message = jsonMessage.getString("message");
                Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                sqLiteHelper.storeLogs("error", "", "Update Error.");
                lst2.add("Update data error.");
            } catch (JSONException e) {
                btnSync.setEnabled(true);
                Toasty.warning(SyncData.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                sqLiteHelper.storeLogs("error", "", "Update Error.");
                lst2.add("Update error.");
            }
            catch (Exception e) {
                btnSync.setEnabled(true);
                Toasty.error(SyncData.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
                initialize_logs();
                lst.add("Network not found.");
                sqLiteHelper.storeLogs("error", "", "Update Network not found.");
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

        btnSync.setEnabled(false);

        Cursor emv_validation_details = sqLiteHelper.getData("SELECT id, hh_status, contact_no, contact_no_of, is_grantee, is_minor, relationship_to_grantee, assigned_staff, representative_name, grantee_validation_id, pawning_validation_detail_id, nma_validation_id, card_validation_detail_id, emv_validation_id, sync_at, user_id, additional_image, created_at, updated_at, overall_remarks, contact_no_of_others FROM emv_validation_details");

        tvStatus.setText("Initializing syncing...");

        if (getCountEmvDetails() == 0) {
            tvStatus.setText("Preparing update...");
            initialize_logs();
            lst.add("Sync data omitted");
            updateEmvValidations();
            progressTarget.setText("0");
            progressPercent.setText("0");
            progressBar.setProgress(0);
            progressCount.setText("0");
            return;
        }

        while (emv_validation_details.moveToNext()) {
            JSONArray arr_other_card = new JSONArray();
            JSONArray arr_ocv_id = new JSONArray();

            int evd_id;
            String evd_hh_status;
            String evd_contact_no;
            String evd_contact_no_of;
            String evd_is_grantee;
            String evd_is_minor;
            String evd_relationship_to_grantee;
            String evd_assigned_staff;
            String evd_representative_name;
            String evd_user_id;
            String evd_created_at;
            String evd_overall_remarks;
            byte[] evd_additional_image;

            int gv_id = 0;
            String gv_hh_id = "";
            String gv_first_name = "";
            String gv_last_name = "";
            String gv_middle_name = "";
            String gv_ext_name = "";
            String gv_sex = "";
            String gv_province_code = "";
            String gv_municipality_code = "";
            String gv_barangay_code = "";
            String gv_hh_set = "";
            byte[] gv_grantee_image = null;

            int pvd_id = 0;
            String pvd_lender_name = "";
            String pvd_lender_address = "";
            String pvd_date_pawned = "";
            String pvd_date_retrieved = "";
            String pvd_loan_amount = "0";
            String pvd_status = "";
            String pvd_reason = "";
            String pvd_interest = "0";
            String pvd_offense_history = "";
            String pvd_offense_date = "";
            String pvd_remarks = "";
            String pvd_staff_intervention = "";
            String pvd_other_details = "";

            int nv_id = 0;
            String nv_amount = "0";
            String nv_date_claimed = "";
            String nv_reason = "";
            String nv_remarks = "";

            int cvd_id = 0;
            String cvd_card_number_prefilled = "";
            String cvd_card_number_system_generated = "";
            String cvd_card_number_inputted = "";
            String cvd_card_number_series = "";
            String cvd_distribution_status = "";
            String cvd_release_date = "";
            String cvd_release_by = "";
            String cvd_release_place = "";
            String cvd_card_physically_presented = "";
            String cvd_card_pin_is_attached = "";
            String cvd_reason_not_presented = "";
            String cvd_reason_unclaimed = "";
            String cvd_card_replacement_requests = "";
            String cvd_card_replacement_submitted_details = "";
            byte[] cvd_card_image = null;

            byte[] ocv_other_image_1 = null;
            byte[] ocv_other_image_2 = null;
            byte[] ocv_other_image_3 = null;
            byte[] ocv_other_image_4 = null;
            byte[] ocv_other_image_5 = null;

            evd_id = emv_validation_details.getInt(0);
            evd_hh_status = emv_validation_details.getString(1);
            evd_contact_no = emv_validation_details.getString(2);
            evd_is_grantee = emv_validation_details.getString(4);
            evd_is_minor = emv_validation_details.getString(5);
            evd_relationship_to_grantee = emv_validation_details.getString(6);
            evd_assigned_staff = emv_validation_details.getString(7);
            evd_representative_name = emv_validation_details.getString(8);
            evd_user_id = emv_validation_details.getString(15);
            evd_additional_image = emv_validation_details.getBlob(16);
            evd_created_at = emv_validation_details.getString(17);
            evd_overall_remarks = emv_validation_details.getString(19);
            if (emv_validation_details.getString(3).matches("Others")) {
                evd_contact_no_of = emv_validation_details.getString(20);
            } else {
                evd_contact_no_of = emv_validation_details.getString(3);
            }

            Cursor grantee_validations = sqLiteHelper.getData("SELECT id, hh_id, first_name, last_name, middle_name, ext_name, sex, province_code, municipality_code, barangay_code, hh_set, grantee_image, other_ext_name FROM grantee_validations WHERE id=" + emv_validation_details.getInt(9));
            while (grantee_validations.moveToNext()) {
                gv_id = grantee_validations.getInt(0);
                gv_hh_id = grantee_validations.getString(1);
                gv_first_name = grantee_validations.getString(2);
                gv_last_name = grantee_validations.getString(3);
                gv_middle_name = grantee_validations.getString(4);
                gv_sex = grantee_validations.getString(6);
                gv_province_code = grantee_validations.getString(7);
                gv_municipality_code = grantee_validations.getString(8);
                gv_barangay_code = grantee_validations.getString(9);
                gv_hh_set = grantee_validations.getString(10);
                gv_grantee_image = grantee_validations.getBlob(11);
                if (grantee_validations.getString(5).matches("Others")) {
                    gv_ext_name = grantee_validations.getString(12);
                } else {
                    gv_ext_name = grantee_validations.getString(5);
                }
            }
            grantee_validations.close();

            Cursor pawning_validation_details = sqLiteHelper.getData("SELECT id, lender_name, lender_address, date_pawned, date_retrieved, loan_amount, status, reason, interest, offense_history, offense_date, remarks, staff_intervention, other_details FROM pawning_validation_details WHERE id=" + emv_validation_details.getInt(10));
            while (pawning_validation_details.moveToNext()) {
                pvd_id = pawning_validation_details.getInt(0);
                pvd_lender_name = pawning_validation_details.getString(1);
                pvd_lender_address = pawning_validation_details.getString(2);
                pvd_loan_amount = pawning_validation_details.getString(5);
                pvd_status = pawning_validation_details.getString(6);
                pvd_reason = pawning_validation_details.getString(7);
                pvd_interest = pawning_validation_details.getString(8);
                pvd_offense_history = pawning_validation_details.getString(9);
                pvd_remarks = pawning_validation_details.getString(11);
                pvd_staff_intervention = pawning_validation_details.getString(12);
                pvd_other_details = pawning_validation_details.getString(13);
                pvd_date_retrieved = pawning_validation_details.getString(4);
                pvd_offense_date = pawning_validation_details.getString(10);
                pvd_date_pawned = pawning_validation_details.getString(3);

            }
            pawning_validation_details.close();

            Cursor nma_validations = sqLiteHelper.getData("SELECT id, amount, date_claimed, reason, remarks, nma_others_reason FROM nma_validations WHERE id="+emv_validation_details.getInt(11));
            while (nma_validations.moveToNext()) {
                nv_id = nma_validations.getInt(0);
                nv_amount = nma_validations.getString(1);
                nv_remarks = nma_validations.getString(4);
                nv_date_claimed = nma_validations.getString(2);
                if (nma_validations.getString(3).matches("Others")) {
                    nv_reason = nma_validations.getString(5);
                } else {
                    nv_reason = nma_validations.getString(3);
                }
            }
            nma_validations.close();

            Cursor card_validation_details = sqLiteHelper.getData("SELECT id, card_number_prefilled, card_number_system_generated, card_number_inputted, card_number_series, distribution_status, release_date, release_by, release_place, card_physically_presented, card_pin_is_attached, reason_not_presented, reason_unclaimed, card_replacement_requests, card_replacement_submitted_details, card_image, others_reason_not_presented, others_reason_unclaimed FROM card_validation_details WHERE id=" + emv_validation_details.getInt(12));
            while (card_validation_details.moveToNext()) {
                cvd_id = card_validation_details.getInt(0);
                cvd_card_number_prefilled = card_validation_details.getString(1);
                cvd_card_number_system_generated = card_validation_details.getString(2);
                cvd_card_number_inputted = card_validation_details.getString(3);
                cvd_card_number_series = card_validation_details.getString(4);
                cvd_distribution_status = card_validation_details.getString(5);
                cvd_release_by = card_validation_details.getString(7);
                cvd_release_place = card_validation_details.getString(8);
                cvd_card_physically_presented = card_validation_details.getString(9);
                cvd_card_pin_is_attached = card_validation_details.getString(10);
                cvd_card_replacement_requests = card_validation_details.getString(13);
                cvd_card_replacement_submitted_details = card_validation_details.getString(14);
                cvd_card_image = card_validation_details.getBlob(15);
                cvd_release_date = card_validation_details.getString(6);
                if (card_validation_details.getString(11).matches("Others")) {
                    cvd_reason_not_presented = card_validation_details.getString(16);
                } else {
                    cvd_reason_not_presented = card_validation_details.getString(11);
                }
                if (card_validation_details.getString(12).matches("Others")) {
                    cvd_reason_unclaimed = card_validation_details.getString(17);
                } else {
                    cvd_reason_unclaimed = card_validation_details.getString(12);
                }
            }
            card_validation_details.close();

            Cursor other_card_validations = sqLiteHelper.getData("SELECT id, card_holder_name, card_number_system_generated, card_number_inputted, card_number_series, distribution_status, release_date, release_by, release_place, card_physically_presented, card_pin_is_attached, reason_not_presented, reason_unclaimed, card_replacement_requests, card_replacement_request_submitted_details, pawning_remarks, other_image, others_reason_not_presented, others_reason_unclaimed FROM other_card_validations WHERE emv_validation_detail_id=" + emv_validation_details.getInt(0));
            int ocv_counter = 0;
            while (other_card_validations.moveToNext()) {
                ocv_counter++;
                JSONObject obj_other_card = new JSONObject();
                try {
                    arr_ocv_id.put(other_card_validations.getString(0));
                    obj_other_card.put("id", other_card_validations.getString(0));
                    obj_other_card.put("card_holder_name", other_card_validations.getString(1));
                    obj_other_card.put("card_number_system_generated", other_card_validations.getString(2));
                    obj_other_card.put("card_number_inputted", other_card_validations.getString(3));
                    obj_other_card.put("card_number_series", other_card_validations.getString(4));
                    obj_other_card.put("distribution_status", other_card_validations.getString(5));
                    obj_other_card.put("release_date", other_card_validations.getString(6));
                    obj_other_card.put("release_by", other_card_validations.getString(7));
                    obj_other_card.put("release_place", other_card_validations.getString(8));
                    obj_other_card.put("card_physically_presented", other_card_validations.getString(9));
                    obj_other_card.put("card_pin_is_attached", other_card_validations.getString(10));
                    obj_other_card.put("card_replacement_requests", other_card_validations.getString(13));
                    obj_other_card.put("card_replacement_request_submitted_details", other_card_validations.getString(14));
                    obj_other_card.put("pawning_remarks", other_card_validations.getString(15));
                    if (other_card_validations.getString(11).matches("Others")) {
                        obj_other_card.put("reason_not_presented", other_card_validations.getString(17));
                    } else {
                        obj_other_card.put("reason_not_presented", other_card_validations.getString(11));
                    }
                    if (other_card_validations.getString(12).matches("Others")) {
                        obj_other_card.put("reason_unclaimed", other_card_validations.getString(18));
                    } else {
                        obj_other_card.put("reason_unclaimed", other_card_validations.getString(12));
                    }

                    if (other_card_validations.getBlob(16) != null) {
                        if (ocv_counter == 1) {
                            ocv_other_image_1 = other_card_validations.getBlob(16);
                        } else if (ocv_counter == 2) {
                            ocv_other_image_2 = other_card_validations.getBlob(16);
                        } else if (ocv_counter == 3) {
                            ocv_other_image_3 = other_card_validations.getBlob(16);
                        } else if (ocv_counter == 4) {
                            ocv_other_image_4 = other_card_validations.getBlob(16);
                        } else if (ocv_counter == 5) {
                            ocv_other_image_5 = other_card_validations.getBlob(16);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr_other_card.put(obj_other_card);
            }


            String url = BASE_URL + "/api/v1/staff/emvvalidationdetails/sync";

            String finalGv_hh_id = gv_hh_id;
            String finalEvd_hh_status = evd_hh_status;
            String finalEvd_contact_no = evd_contact_no;
            String finalEvd_contact_no_of = evd_contact_no_of;
            String finalEvd_is_grantee = evd_is_grantee;
            String finalEvd_is_minor = evd_is_minor;
            String finalEvd_relationship_to_grantee = evd_relationship_to_grantee;
            String finalEvd_assigned_staff = evd_assigned_staff;
            String finalEvd_representative_name = evd_representative_name;
            String finalEvd_user_id = evd_user_id;
            String finalEvd_created_at = evd_created_at;
            String finalEvd_overall_remarks = evd_overall_remarks;
            String finalGv_hh_id1 = gv_hh_id;
            String finalGv_first_name = gv_first_name;
            String finalGv_last_name = gv_last_name;
            String finalGv_middle_name = gv_middle_name;
            String finalGv_ext_name = gv_ext_name;
            String finalGv_sex = gv_sex;
            String finalGv_province_code = gv_province_code;
            String finalGv_municipality_code = gv_municipality_code;
            String finalGv_barangay_code = gv_barangay_code;
            String finalGv_hh_set = gv_hh_set;
            String finalPvd_lender_name = pvd_lender_name;
            String finalPvd_lender_address = pvd_lender_address;
            String finalPvd_date_pawned = pvd_date_pawned;
            String finalPvd_date_retrieved = pvd_date_retrieved;
            String finalPvd_loan_amount = pvd_loan_amount;
            String finalPvd_status = pvd_status;
            String finalPvd_reason = pvd_reason;
            String finalPvd_interest = pvd_interest;
            String finalPvd_offense_history = pvd_offense_history;
            String finalPvd_offense_date = pvd_offense_date;
            String finalPvd_remarks = pvd_remarks;
            String finalPvd_staff_intervention = pvd_staff_intervention;
            String finalPvd_other_details = pvd_other_details;
            String finalNv_amount = nv_amount;
            String finalNv_date_claimed = nv_date_claimed;
            String finalNv_reason = nv_reason;
            String finalNv_remarks = nv_remarks;
            String finalCvd_card_number_prefilled = cvd_card_number_prefilled;
            String finalCvd_card_number_system_generated = cvd_card_number_system_generated;
            String finalCvd_card_number_inputted = cvd_card_number_inputted;
            String finalCvd_card_number_series = cvd_card_number_series;
            String finalCvd_distribution_status = cvd_distribution_status;
            String finalCvd_release_date = cvd_release_date;
            String finalCvd_release_by = cvd_release_by;
            String finalCvd_release_place = cvd_release_place;
            String finalCvd_card_physically_presented = cvd_card_physically_presented;
            String finalCvd_card_pin_is_attached = cvd_card_pin_is_attached;
            String finalCvd_reason_not_presented = cvd_reason_not_presented;
            String finalCvd_reason_unclaimed = cvd_reason_unclaimed;
            String finalCvd_card_replacement_requests = cvd_card_replacement_requests;
            String finalCvd_card_replacement_submitted_details = cvd_card_replacement_submitted_details;
            byte[] finalCvd_card_image = cvd_card_image;
            byte[] finalEvd_additional_image = evd_additional_image;
            byte[] finalGv_grantee_image = gv_grantee_image;
            byte[] finalOcv_other_image_1 = ocv_other_image_1;
            byte[] finalOcv_other_image_2 = ocv_other_image_2;
            byte[] finalOcv_other_image_3 = ocv_other_image_3;
            byte[] finalOcv_other_image_4 = ocv_other_image_4;
            byte[] finalOcv_other_image_5 = ocv_other_image_5;
            Integer finalEvd_id = evd_id;
            Integer finalGv_id = gv_id;
            Integer finalCvd_id = cvd_id;
            Integer finalNv_id = nv_id;
            Integer finalPvd_id = pvd_id;


            tvStatus.setText("Syncing...");

            request = new VolleyMultipartRequest(Request.Method.POST, url, response -> {
                try {
                    JSONObject data = new JSONObject(new String(response.data));
                    String status = data.getString("status");
                    String description = data.getString("description");
                    JSONObject dataObject = data.getJSONObject("data");

                    initialize_logs();
                    if (status.matches("success")){
                        tvStatus.setText("Success syncing...");

                        JSONObject granteeObj = dataObject.getJSONObject("grantee_validations");
                        progressCC[0]++;

                        progressPercent = findViewById(R.id.progressCount);
                        progressBar = findViewById(R.id.progressBar);
                        progressCount = findViewById(R.id.progressFigure);
                        Double progressCalc = progressCC[0] / getCountEmvDetails() * 100;

                        progressCount.setText(String.valueOf(progressCC[0].intValue()));
                        progressPercent.setText(String.valueOf(progressCalc.intValue()));
                        progressBar.setProgress(progressCalc.intValue());

                        lst.add(description + " household id: " + granteeObj.getString("hh_id"));
                        gvMain.setAdapter(adapter);

                        sqLiteHelper.storeLogs("sync", finalGv_hh_id, "Sync data successfully.");
                        sqLiteHelper.deleteScannedData(finalEvd_id, finalGv_id, finalCvd_id, finalNv_id, finalPvd_id, arr_ocv_id);

                        if (progressPercent.getText().toString().matches("100")) {
                            tvStatus.setText("Syncing completed...");
                            Toasty.success(SyncData.this, "Completed", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                    else{
                        tvStatus.setText("Syncing encountered an error...");
                        btnSync.setEnabled(true);
                        lst2.add("Error on syncing the data!");
                        gvMain2.setAdapter(adapter2);
                        sqLiteHelper.storeLogs("error", finalGv_hh_id, "Error on syncing data.");
                        Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                    }

                } catch (JSONException e) {
                    btnSync.setEnabled(true);
                    e.printStackTrace();
                    tvStatus.setText("Syncing encountered an error...");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    tvStatus.setText("Syncing encountered an error...");
                    initialize_logs();
                    btnSync.setEnabled(true);
                    try {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        int responseCode = error.networkResponse.statusCode;
                        if (responseCode == 401) {
                            JSONObject data = new JSONObject(responseBody);
                            JSONArray errors = data.getJSONArray("errors");
                            JSONObject jsonMessage = errors.getJSONObject(0);
                            String message = jsonMessage.getString("message");
                            Toasty.warning(SyncData.this, message, Toast.LENGTH_SHORT, true).show();
                            lst2.add(message);
                            gvMain2.setAdapter(adapter2);
                            sqLiteHelper.storeLogs("error", finalGv_hh_id, "Sync: " + message);
                        } else if (responseCode == 404) {
                            JSONObject data = new JSONObject(responseBody);
                            String desc = data.getString("description");
                            Toasty.error(SyncData.this, "Error 404:" + desc, Toast.LENGTH_SHORT, true).show();
                            lst2.add(desc);
                            gvMain2.setAdapter(adapter2);
                            sqLiteHelper.storeLogs("error", finalGv_hh_id, "Sync: Error 404");
                        }
                    } catch (Exception e) {
                        Log.d("Error co", String.valueOf(e));
                        queue.cancelAll(this);
                        lst2.add("Network not found");
                        gvMain2.setAdapter(adapter2);
                        sqLiteHelper.storeLogs("error", finalGv_hh_id, " Sync: Network Not Found");
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
                protected Map<String,String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("evd_hh_status", finalEvd_hh_status);
                    params.put("evd_contact_no", finalEvd_contact_no);
                    params.put("evd_contact_no_of", finalEvd_contact_no_of);
                    params.put("evd_is_grantee", finalEvd_is_grantee);
                    params.put("evd_is_minor", finalEvd_is_minor);
                    params.put("evd_relationship_to_grantee", finalEvd_relationship_to_grantee);
                    params.put("evd_assigned_staff", finalEvd_assigned_staff);
                    params.put("evd_representative_name", finalEvd_representative_name);
                    params.put("evd_user_id", finalEvd_user_id);
                    params.put("evd_created_at", finalEvd_created_at);
                    params.put("evd_overall_remarks", finalEvd_overall_remarks);
                    params.put("gv_hh_id", finalGv_hh_id1);
                    params.put("gv_first_name", finalGv_first_name);
                    params.put("gv_last_name", finalGv_last_name);
                    params.put("gv_middle_name", finalGv_middle_name);
                    params.put("gv_ext_name", finalGv_ext_name);
                    params.put("gv_sex", finalGv_sex);
                    params.put("gv_province_code", finalGv_province_code);
                    params.put("gv_municipality_code", finalGv_municipality_code);
                    params.put("gv_barangay_code", finalGv_barangay_code);
                    params.put("gv_hh_set", finalGv_hh_set);
                    params.put("pvd_lender_name", finalPvd_lender_name);
                    params.put("pvd_lender_address", finalPvd_lender_address);
                    if (!finalPvd_date_pawned.matches("")) {
                        params.put("pvd_date_pawned", finalPvd_date_pawned);
                    }
                    if (!finalPvd_date_retrieved.matches("")) {
                        params.put("pvd_date_retrieved", finalPvd_date_retrieved);
                    }
                    params.put("pvd_loan_amount", finalPvd_loan_amount);
                    params.put("pvd_status", finalPvd_status);
                    params.put("pvd_reason", finalPvd_reason);
                    params.put("pvd_interest", finalPvd_interest);
                    params.put("pvd_offense_history", finalPvd_offense_history);
                    if (!finalPvd_offense_date.matches("")) {
                        params.put("pvd_offense_date", finalPvd_offense_date);
                    }
                    params.put("pvd_remarks", finalPvd_remarks);
                    params.put("pvd_staff_intervention", finalPvd_staff_intervention);
                    params.put("pvd_other_details", finalPvd_other_details);
                    params.put("nv_amount", finalNv_amount);
                    if (!finalNv_date_claimed.matches("")) {
                        params.put("nv_date_claimed", finalNv_date_claimed);
                    }
                    params.put("nv_nma_reason", finalNv_reason);
                    params.put("nv_nma_remarks", finalNv_remarks);
                    params.put("cvd_card_number_prefilled", "LBP" + finalCvd_card_number_prefilled.replace(" ", ""));
                    params.put("cvd_card_number_system_generated", "LBP" + finalCvd_card_number_system_generated.replace(" ", ""));
                    params.put("cvd_card_number_inputted", "LBP" + finalCvd_card_number_inputted.replace(" ", ""));
                    params.put("cvd_card_number_series", finalCvd_card_number_series);
                    params.put("cvd_distribution_status", finalCvd_distribution_status);
                    if (!finalCvd_release_date.matches("")) {
                        params.put("cvd_release_date", finalCvd_release_date);
                    }
                    params.put("cvd_release_by", finalCvd_release_by);
                    params.put("cvd_release_place", finalCvd_release_place);
                    params.put("cvd_card_physically_presented", finalCvd_card_physically_presented);
                    params.put("cvd_card_pin_is_attached", finalCvd_card_pin_is_attached);
                    params.put("cvd_reason_not_presented", finalCvd_reason_not_presented);
                    params.put("cvd_reason_unclaimed", finalCvd_reason_unclaimed);
                    params.put("cvd_card_replacement_request", finalCvd_card_replacement_requests);
                    params.put("cvd_card_replacement_submitted_details", finalCvd_card_replacement_submitted_details);

                    Log.d("OBJ OTHER CARD =", "Length " + arr_other_card.length());
                    for (int i = 0; i < arr_other_card.length(); i++) {
                        try {
                            JSONObject new_obj = arr_other_card.getJSONObject(i);
                            params.put("ocv_card_holder_name_" + (i + 1), new_obj.getString("card_holder_name"));
                            params.put("ocv_card_number_system_generated_" + (i + 1), "LBP" + new_obj.getString("card_number_system_generated").replace(" ", ""));
                            params.put("ocv_card_number_inputted_" + (i + 1), "LBP" + new_obj.getString("card_number_inputted").replace(" ", ""));
                            params.put("ocv_card_number_series_" + (i + 1), new_obj.getString("card_number_series"));
                            params.put("ocv_distribution_status_" + (i + 1), new_obj.getString("distribution_status"));
                            if (!new_obj.getString("release_date").matches("")) {
                                params.put("ocv_release_date_" + (i + 1), new_obj.getString("release_date"));
                            }
                            params.put("ocv_release_by_" + (i + 1), new_obj.getString("release_by"));
                            params.put("ocv_release_place_" + (i + 1), new_obj.getString("release_place"));
                            params.put("ocv_card_physically_presented_" + (i + 1), new_obj.getString("card_physically_presented"));
                            params.put("ocv_card_pin_is_attached_" + (i + 1), new_obj.getString("card_pin_is_attached"));
                            params.put("ocv_reason_not_presented_" + (i + 1), new_obj.getString("reason_not_presented"));
                            params.put("ocv_reason_unclaimed_" + (i + 1), new_obj.getString("reason_unclaimed"));
                            params.put("ocv_card_replacement_request_" + (i + 1), new_obj.getString("card_replacement_requests"));
                            params.put("ocv_card_replacement_submitted_details_" + (i + 1), new_obj.getString("card_replacement_request_submitted_details"));
                            params.put("ocv_pawning_remarks_" + (i + 1), new_obj.getString("pawning_remarks"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    return params;
                }
                /*
                 * Here we are passing image by renaming it with a unique name
                 * */
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                        if (finalCvd_card_image != null) {
                            Bitmap card_image = BitmapFactory.decodeByteArray(finalCvd_card_image, 0, finalCvd_card_image.length);
                            params.put("gv_image", new DataPart("cvd_card_image.png",getFileDataFromDrawable(card_image) ) );
                        }
                        if (finalEvd_additional_image != null) {
                            Bitmap additional_image = BitmapFactory.decodeByteArray(finalEvd_additional_image, 0, finalEvd_additional_image.length);
                            params.put("gv_image_additional", new DataPart("evd_additional_image.png",getFileDataFromDrawable(additional_image) ));
                        }
                        if (finalGv_grantee_image != null) {
                            Bitmap grantee_image = BitmapFactory.decodeByteArray(finalGv_grantee_image, 0, finalGv_grantee_image.length);
                            params.put("cvd_card_image", new DataPart("gv_grantee_image.png",getFileDataFromDrawable(grantee_image) ));
                        }

                        for (int i = 1; i <= arr_other_card.length(); i++) {
                            if (i == 1 && finalOcv_other_image_1 != null) {
                                Bitmap image1 = BitmapFactory.decodeByteArray(finalOcv_other_image_1, 0, finalOcv_other_image_1.length);
                                params.put("ocv_card_image_1", new DataPart("ocv_card_image_1.png", getFileDataFromDrawable(image1)));
                            } else if (i == 2 && finalOcv_other_image_2 != null) {
                                Bitmap image2 = BitmapFactory.decodeByteArray(finalOcv_other_image_2, 0, finalOcv_other_image_2.length);
                                params.put("ocv_card_image_2", new DataPart("ocv_card_image_2.png", getFileDataFromDrawable(image2)));
                            } else if (i == 3 && finalOcv_other_image_3 != null) {
                                Bitmap image3 = BitmapFactory.decodeByteArray(finalOcv_other_image_3, 0, finalOcv_other_image_3.length);
                                params.put("ocv_card_image_3", new DataPart("ocv_card_image_3.png", getFileDataFromDrawable(image3)));
                            } else if (i == 4 && finalOcv_other_image_4 != null) {
                                Bitmap image4 = BitmapFactory.decodeByteArray(finalOcv_other_image_4, 0, finalOcv_other_image_4.length);
                                params.put("ocv_card_image_4", new DataPart("ocv_card_image_4.png", getFileDataFromDrawable(image4)));
                            } else if (i == 5 && finalOcv_other_image_5 != null) {
                                Bitmap image5 = BitmapFactory.decodeByteArray(finalOcv_other_image_5, 0, finalOcv_other_image_5.length);
                                params.put("ocv_card_image_5", new DataPart("ocv_card_image_5.png", getFileDataFromDrawable(image5)));
                            }
                        }

                    return params;
                }

            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    -1,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);

        }
        emv_validation_details.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_data);

        queue = Volley.newRequestQueue(SyncData.this);
        System.setProperty("http.keepAlive", "false");

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        btnSync = findViewById(R.id.btnSync);
        tvStatus = findViewById(R.id.tv_status);

        getLastID();
        pullEmvData(true);

        btnSync.setOnClickListener(v -> new SweetAlertDialog(SyncData.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Please Confirm to Sync Data")
                .setConfirmText("Sync Now")
                .showCancelButton(false)
                .setConfirmClickListener(sDialog -> {
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    registerReceiver(networkChangeListener, filter);
                    if (networkChangeListener.connection){
                        pullEmvData(false);
                        sDialog.dismiss();
                    }
                }).show());
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