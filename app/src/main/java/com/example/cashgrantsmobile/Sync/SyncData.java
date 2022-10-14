package com.example.cashgrantsmobile.Sync;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Internet.NetworkChangeListener;
import com.example.cashgrantsmobile.helpers.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
    VolleyMultipartRequest request;
    final Double[] progressCC = {0.00};

    Integer evd_id = null;
    String evd_hh_status = null;
    String evd_contact_no = null;
    String evd_contact_no_of = null;
    String evd_is_grantee = null;
    String evd_is_minor = null;
    String evd_relationship_to_grantee = null;
    String evd_assigned_staff = null;
    String evd_representative_name = null;
    String evd_sync_at = null;
    String evd_user_id = null;
    String evd_created_at = null;

    Integer other_card = 0;

    Integer gv_id = null;
    String gv_hh_id = null;
    String gv_first_name = null;
    String gv_last_name = null;
    String gv_middle_name = null;
    String gv_ext_name = null;
    String gv_sex = null;
    String gv_province_code = null;
    String gv_municipality_code = null;
    String gv_barangay_code = null;
    String gv_hh_set = null;

    Integer pvd_id = null;
    String pvd_lender_name = null;
    String pvd_lender_address = null;
    String pvd_date_pawned = null;
    String pvd_date_retrieved = null;
    String pvd_loan_amount = null;
    String pvd_status = null;
    String pvd_reason = null;
    String pvd_interest = null;
    String pvd_offense_history = null;
    String pvd_offense_date = null;
    String pvd_remarks = null;
    String pvd_staff_intervention = null;
    String pvd_other_details = null;

    Integer nv_id = null;
    String nv_amount = null;
    String nv_date_claimed = null;
    String nv_reason = null;
    String nv_remarks = null;

    Integer cvd_id = null;
    String cvd_card_number_prefilled = null;
    String cvd_card_number_system_generated = null;
    String cvd_card_number_inputted = null;
    String cvd_card_number_series = null;
    String cvd_distribution_status = null;
    String cvd_release_date = null;
    String cvd_release_by = null;
    String cvd_release_place = null;
    String cvd_card_physically_presented = null;
    String cvd_card_pin_is_attached = null;
    String cvd_reason_not_presented = null;
    String cvd_reason_unclaimed = null;
    String cvd_card_replacement_requests = null;
    String cvd_card_replacement_submitted_details = null;


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void getCountEmvDetails() {
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring_details");
        Integer totalCount = lastEmvDatabaseID.getCount();
        countEmvDetails = totalCount;

        progressTarget = findViewById(R.id.progressFigureLast);
        progressTarget.setText(totalCount.toString());
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

        gvMain = (GridView) findViewById(R.id.gridView);
        gvMain2 = (GridView) findViewById(R.id.gridView1);

        gvMain.setAdapter(adapter);
        gvMain2.setAdapter(adapter2);

        btnSync = findViewById(R.id.btnSync);
        btnSync.setEnabled(false);

        Cursor emv_validation_details = sqLiteHelper.getData("SELECT id, hh_status, contact_no, contact_no_of, is_grantee, is_minor, relationship_to_grantee, assigned_staff, representative_name, grantee_validation_id, pawning_validation_detail_id, nma_validation_id, card_validation_detail_id, emv_validation_id, sync_at, user_id, created_at, updated_at FROM emv_validation_details");

        while (emv_validation_details.moveToNext()) {
            JSONArray arr_other_card = new JSONArray();

            evd_id = emv_validation_details.getInt(0);
            evd_hh_status = emv_validation_details.getString(1);
            evd_contact_no = emv_validation_details.getString(2);
            evd_contact_no_of = emv_validation_details.getString(3);
            evd_is_grantee = emv_validation_details.getString(4);
            evd_is_minor = emv_validation_details.getString(5);
            evd_relationship_to_grantee = emv_validation_details.getString(6);
            evd_assigned_staff = emv_validation_details.getString(7);
            evd_representative_name = emv_validation_details.getString(8);
            evd_sync_at = emv_validation_details.getString(14);
            evd_user_id = emv_validation_details.getString(15);
            evd_created_at = emv_validation_details.getString(16);

            Cursor grantee_validations = sqLiteHelper.getData("SELECT id, hh_id, first_name, last_name, middle_name, ext_name, sex, province_code, municipality_code, barangay_code, hh_set FROM grantee_validations WHERE id=" + emv_validation_details.getInt(9));
            while (grantee_validations.moveToNext()) {
                gv_id = grantee_validations.getInt(0);
                gv_hh_id = grantee_validations.getString(1);
                gv_first_name = grantee_validations.getString(2);
                gv_last_name = grantee_validations.getString(3);
                gv_middle_name = grantee_validations.getString(4);
                gv_ext_name = grantee_validations.getString(5);
                gv_sex = grantee_validations.getString(6);
                gv_province_code = grantee_validations.getString(7);
                gv_municipality_code = grantee_validations.getString(8);
                gv_barangay_code = grantee_validations.getString(9);
                gv_hh_set = grantee_validations.getString(10);
            }
            grantee_validations.close();

            Cursor pawning_validation_details = sqLiteHelper.getData("SELECT id, lender_name, lender_address, date_pawned, date_retrieved, loan_amount, status, reason, interest, offense_history, offense_date, remarks, staff_intervention, other_details FROM pawning_validation_details WHERE id=" + emv_validation_details.getInt(10));
            while (pawning_validation_details.moveToNext()) {
                pvd_id = pawning_validation_details.getInt(0);
                pvd_lender_name = pawning_validation_details.getString(1);
                pvd_lender_address = pawning_validation_details.getString(2);
                pvd_date_pawned = pawning_validation_details.getString(3);
                pvd_date_retrieved = pawning_validation_details.getString(4);
                pvd_loan_amount = pawning_validation_details.getString(5);
                pvd_status = pawning_validation_details.getString(6);
                pvd_reason = pawning_validation_details.getString(7);
                pvd_interest = pawning_validation_details.getString(8);
                pvd_offense_history = pawning_validation_details.getString(9);
                pvd_offense_date = pawning_validation_details.getString(10);
                pvd_remarks = pawning_validation_details.getString(11);
                pvd_staff_intervention = pawning_validation_details.getString(12);
                pvd_other_details = pawning_validation_details.getString(13);
            }
            pawning_validation_details.close();

            Cursor nma_validations = sqLiteHelper.getData("SELECT id, amount, date_claimed, reason, remarks FROM nma_validations WHERE id="+emv_validation_details.getInt(11));
            while (nma_validations.moveToNext()) {
                nv_id = nma_validations.getInt(0);
                nv_amount = nma_validations.getString(1);
                nv_date_claimed = nma_validations.getString(2);
                nv_reason = nma_validations.getString(3);
                nv_remarks = nma_validations.getString(4);
            }
            nma_validations.close();

            Cursor card_validation_details = sqLiteHelper.getData("SELECT id, card_number_prefilled, card_number_system_generated, card_number_inputted, card_number_series, distribution_status, release_date, release_by, release_place, card_physically_presented, card_pin_is_attached, reason_not_presented, reason_unclaimed, card_replacement_requests, card_replacement_submitted_details FROM card_validation_details WHERE id=" + emv_validation_details.getInt(12));
            while (card_validation_details.moveToNext()) {
                cvd_id = card_validation_details.getInt(0);
                cvd_card_number_prefilled = card_validation_details.getString(1);
                cvd_card_number_system_generated = card_validation_details.getString(2);
                cvd_card_number_inputted = card_validation_details.getString(3);
                cvd_card_number_series = card_validation_details.getString(4);
                cvd_distribution_status = card_validation_details.getString(5);
                cvd_release_date = card_validation_details.getString(6);
                cvd_release_by = card_validation_details.getString(7);
                cvd_release_place = card_validation_details.getString(8);
                cvd_card_physically_presented = card_validation_details.getString(9);
                cvd_card_pin_is_attached = card_validation_details.getString(10);
                cvd_reason_not_presented = card_validation_details.getString(11);
                cvd_reason_unclaimed = card_validation_details.getString(12);
                cvd_card_replacement_requests = card_validation_details.getString(13);
                cvd_card_replacement_submitted_details = card_validation_details.getString(14);
            }
            card_validation_details.close();

            Cursor other_card_validations = sqLiteHelper.getData("SELECT id, card_holder_name, card_number_system_generated, card_number_inputted, card_number_series, distribution_status, release_date, release_by, release_place, card_physically_presented, card_pin_is_attached, reason_not_presented, reason_unclaimed, card_replacement_requests, card_replacement_request_submitted_details, pawning_remarks FROM other_card_validations WHERE emv_validation_detail_id=" + emv_validation_details.getInt(0));
            while (other_card_validations.moveToNext()) {
                JSONObject obj_other_card = new JSONObject();
                try {
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
                    obj_other_card.put("reason_not_presented", other_card_validations.getString(11));
                    obj_other_card.put("reason_unclaimed", other_card_validations.getString(12));
                    obj_other_card.put("card_replacement_requests", other_card_validations.getString(13));
                    obj_other_card.put("card_replacement_request_submitted_details", other_card_validations.getString(14));
                    obj_other_card.put("pawning_remarks", other_card_validations.getString(15));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arr_other_card.put(obj_other_card);
            }


            String url = BASE_URL + "/api/v1/staff/emvvalidationdetails/sync";

            request = new VolleyMultipartRequest(Request.Method.POST, url,  new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // on below line we are displaying a success toast message.
                    try {
                        JSONObject data = new JSONObject(response);
                        String status = data.getString("status");
                        String description = data.getString("description");
                        JSONObject dataObject = data.getJSONObject("data");
                        String household = dataObject.getString("hh_id");

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
//
//                            sqLiteHelper.storeLogs("sync", hh_id, "Sync data successfully.");
//                            sqLiteHelper.deleteEmvMonitoringDetails(id);

                        }
                        else{
                            btnSync.setEnabled(true);
                            lst2.add("Error on syncing the data!");
                            gvMain2.setAdapter(adapter2);
//                            sqLiteHelper.storeLogs("error", hh_id, "Error on syncing data.");
//                            Toasty.error(getApplicationContext(), "Error on pulling data.", Toast.LENGTH_SHORT, true).show();
                        }

                    } catch (JSONException e) {
                        btnSync.setEnabled(true);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // method to handle errors.
                    btnSync.setEnabled(true);
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
//                            sqLiteHelper.storeLogs("error", hh_id, "Sync: " + message);
                        } else if (responseCode == 404) {
                            JSONObject data = new JSONObject(responseBody);
                            String desc = data.getString("description");
                            Toasty.error(SyncData.this, "Error 404:" + desc, Toast.LENGTH_SHORT, true).show();
                            lst2.add(desc);
                            gvMain2.setAdapter(adapter2);
//                            sqLiteHelper.storeLogs("error", hh_id, "Sync: Error 404");
                        }
                    } catch (Exception e) {
                        Log.d("Error co", String.valueOf(e));
                        queue.cancelAll(this);
                        lst2.add("Network not found");
                        gvMain2.setAdapter(adapter2);
//                        sqLiteHelper.storeLogs("error", hh_id, " Sync: Network Not Found");
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
                    params.put("evd_hh_status", evd_hh_status);
//                    params.put("evd_contact_no", evd_contact_no);
//                    params.put("evd_contact_no_of", evd_contact_no_of);
//                    params.put("evd_is_grantee", evd_is_grantee);
//                    params.put("evd_is_minor", evd_is_minor);
//                    params.put("evd_relationship_to_grantee", evd_relationship_to_grantee);
//                    params.put("evd_assigned_staff", evd_assigned_staff);
//                    params.put("evd_representative_name", evd_representative_name);
                    params.put("evd_user_id", evd_user_id);
                    params.put("evd_created_at", evd_created_at);
                    params.put("gv_hh_id", gv_hh_id);
                    params.put("gv_first_name", gv_first_name);
                    params.put("gv_last_name", gv_last_name);
                    params.put("gv_middle_name", gv_middle_name);
                    params.put("gv_ext_name", gv_ext_name);
                    params.put("gv_sex", gv_sex);
                    params.put("gv_province_code", gv_province_code);
                    params.put("gv_municipality_code", gv_municipality_code);
                    params.put("gv_barangay_code", gv_barangay_code);
                    params.put("evd_overall_remarks", "Lorem Ipsum");
//                    params.put("gv_hh_set", gv_hh_set);
//                    params.put("pvd_lender_name", pvd_lender_name);
//                    params.put("pvd_lender_address", pvd_lender_address);
//                    params.put("pvd_date_pawned", pvd_date_pawned);
//                    params.put("pvd_date_retrieved", pvd_date_retrieved);
//                    params.put("pvd_loan_amount", pvd_loan_amount);
//                    params.put("pvd_status", pvd_status);
//                    params.put("pvd_reason", pvd_reason);
//                    params.put("pvd_interest", pvd_interest);
//                    params.put("pvd_offense_history", pvd_offense_history);
//                    params.put("pvd_offense_date", pvd_offense_date);
//                    params.put("pvd_remarks", pvd_remarks);
//                    params.put("pvd_staff_intervention", pvd_staff_intervention);
//                    params.put("pvd_other_details", pvd_other_details);
//                    params.put("nv_amount", nv_amount);
//                    params.put("nv_date_claimed", nv_date_claimed);
//                    params.put("nv_nma_reason", nv_reason);
//                    params.put("nv_nma_remarks", nv_remarks);
//                    params.put("cvd_card_number_prefilled", cvd_card_number_prefilled);
//                    params.put("cvd_card_number_system_generated", cvd_card_number_system_generated);
//                    params.put("cvd_card_number_inputted", cvd_card_number_inputted);
//                    params.put("cvd_card_number_series", cvd_card_number_series);
//                    params.put("cvd_distribution_status", cvd_distribution_status);
//                    params.put("cvd_release_date", cvd_release_date);
//                    params.put("cvd_release_by", cvd_release_by);
//                    params.put("cvd_release_place", cvd_release_place);
//                    params.put("cvd_card_physically_presented", cvd_card_physically_presented);
//                    params.put("cvd_card_pin_is_attached", cvd_card_pin_is_attached);
//                    params.put("cvd_reason_not_presented", cvd_reason_not_presented);
//                    params.put("cvd_reason_unclaimed", cvd_reason_unclaimed);
//                    params.put("cvd_card_replacement_requests", cvd_card_replacement_requests);
//                    params.put("cvd_card_replacement_submitted_details", cvd_card_replacement_submitted_details);
                    Log.d("OBJ OTHER CARD =", "Length " + arr_other_card.length());
                    for (int i = 0; i < arr_other_card.length(); i++) {
                        try {
                            JSONObject new_obj = arr_other_card.getJSONObject(i);
                            params.put("ocv_card_holder_name_" + (i + 1), new_obj.getString("card_holder_name"));
                            params.put("ocv_card_number_system_generated_" + (i + 1), new_obj.getString("card_number_system_generated"));
                            params.put("ocv_card_number_inputted_" + (i + 1), new_obj.getString("card_number_inputted"));
                            params.put("ocv_card_number_series_" + (i + 1), new_obj.getString("card_number_series"));
                            params.put("ocv_distribution_status_" + (i + 1), new_obj.getString("distribution_status"));
                            params.put("ocv_release_date_" + (i + 1), new_obj.getString("release_date"));
                            params.put("ocv_release_by_" + (i + 1), new_obj.getString("release_by"));
                            params.put("ocv_release_place_" + (i + 1), new_obj.getString("release_place"));
                            params.put("ocv_card_physically_presented_" + (i + 1), new_obj.getString("card_physically_presented"));
                            params.put("ocv_card_pin_is_attached_" + (i + 1), new_obj.getString("card_pin_is_attached"));
                            params.put("ocv_reason_not_presented_" + (i + 1), new_obj.getString("reason_not_presented"));
                            params.put("ocv_reason_unclaimed_" + (i + 1), new_obj.getString("reason_unclaimed"));
                            params.put("ocv_card_replacement_requests_" + (i + 1), new_obj.getString("card_replacement_requests"));
                            params.put("ocv_card_replacement_request_submitted_details_" + (i + 1), new_obj.getString("card_replacement_request_submitted_details"));
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
                    Cursor tmp_blob = sqLiteHelper.getData("SELECT scanned_e_image, additional_id_image, grantee_e_image, other_card_e_image_1, other_card_e_image_2, other_card_e_image_3, other_card_e_image_4, other_card_e_image_5 FROM tmp_blob LIMIT 1");
                    while (tmp_blob.moveToNext()) {
                        byte[] scanned_image = tmp_blob.getBlob(0);
                        byte[] additional_image = tmp_blob.getBlob(1);
                        byte[] grantee_image = tmp_blob.getBlob(2);
                        byte[] other_image1 = tmp_blob.getBlob(3);
                        byte[] other_image2 = tmp_blob.getBlob(4);
                        byte[] other_image3 = tmp_blob.getBlob(5);
                        byte[] other_image4 = tmp_blob.getBlob(6);
                        byte[] other_image5 = tmp_blob.getBlob(7);

//                        if (scanned_image != null) {
//                            Bitmap scanned = BitmapFactory.decodeByteArray(scanned_image, 0, scanned_image.length);
//                            params.put("gv_image", new DataPart("evd_image.png",getFileDataFromDrawable(scanned) ) );
//                        }
//                        if (additional_image != null) {
//                            Bitmap additional = BitmapFactory.decodeByteArray(additional_image, 0, additional_image.length);
//                            params.put("gv_image_additional", new DataPart("evd_additional_image.png",getFileDataFromDrawable(additional) ));
//                        }
//                        if (grantee_image != null) {
//                            Bitmap grantee = BitmapFactory.decodeByteArray(grantee_image, 0, grantee_image.length);
//                            params.put("cvd_card_image", new DataPart("gv_grantee_image.png",getFileDataFromDrawable(grantee) ));
//                        }

//                        for (int i = 1; i <= arr_other_card.length(); i++) {
//                            if (i == 1 && other_image1 != null) {
//                                Bitmap image1 = BitmapFactory.decodeByteArray(other_image1, 0, other_image1.length);
//                                params.put("ocv_card_image_1", new DataPart("ocv_card_image_1.png",getFileDataFromDrawable(image1) ));
//                            } else if (i == 2 && other_image2 != null) {
//                                Bitmap image2 = BitmapFactory.decodeByteArray(other_image2, 0, other_image2.length);
//                                params.put("ocv_card_image_2", new DataPart("ocv_card_image_2.png",getFileDataFromDrawable(image2) ));
//                            } else if (i == 3 && other_image3 != null) {
//                                Bitmap image3 = BitmapFactory.decodeByteArray(other_image3, 0, other_image3.length);
//                                params.put("ocv_card_image_3", new DataPart("ocv_card_image_3.png",getFileDataFromDrawable(image3) ));
//                            } else if (i == 4 && other_image4 != null) {
//                                Bitmap image4 = BitmapFactory.decodeByteArray(other_image4, 0, other_image4.length);
//                                params.put("ocv_card_image_4", new DataPart("ocv_card_image_4.png",getFileDataFromDrawable(image4) ));
//                            } else if (i == 5 && other_image5 != null) {
//                                Bitmap image5 = BitmapFactory.decodeByteArray(other_image5, 0, other_image5.length);
//                                params.put("ocv_card_image_5", new DataPart("ocv_card_image_5.png",getFileDataFromDrawable(image5) ));
//                            }
//                        }
                    }
                    tmp_blob.close();

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

        getCountEmvDetails();

        btnSync = findViewById(R.id.btnSync);


//        if (countEmvDetails == 0) {
//            mainToolbar = findViewById(R.id.mainToolbar);
//            mainToolbar.setTitle("No data to be sync!");
//
//            btnSync.setEnabled(false);
//        }

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