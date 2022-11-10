package com.example.cashgrantsmobile.SyncMonitoring;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;
import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.sqLiteHelper;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.Logs.LogsData;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class SyncMonitoringList extends AppCompatActivity {
    GridView gridView;
    ArrayList<SyncMonitoringItem> list;
    SyncMonitoringAdapter adapter = null;
    TextView tvIdentifier, tvResultCount;
    String token = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.syncmonitoring_list);

        Cursor resultApi = MainActivity.sqLiteHelper.getData("SELECT token From Api");
        while (resultApi.moveToNext()) {
            token = resultApi.getString(0);
        }

        gridView = findViewById(R.id.gridView);
        list = new ArrayList<>();
        adapter = new SyncMonitoringAdapter(this, R.layout.syncmonitoring_list_item, list);
        gridView.setAdapter(adapter);

        try {
            getData(list, adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    public void getData(ArrayList<SyncMonitoringItem> list, SyncMonitoringAdapter adapter) throws JSONException {
        tvIdentifier = findViewById(R.id.tvIdentifier);
        tvResultCount = findViewById(R.id.result_count);

        Activity_Splash_Login.NukeSSLCerts.nuke();

        list.clear();

        String url = BASE_URL + "/api/v1/staff/syncmonitoring";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {

            try {
                JSONObject data = new JSONObject(response);
                String status = data.getString("status");
                if (status.matches("success")) {
                    JSONArray new_data = data.getJSONArray("data");

                    if (new_data.length() > 0) {
                        tvIdentifier.setText("");
                    } else {
                        tvIdentifier.setText("No Data Found!");
                    }

                    tvResultCount.setText(String.valueOf(new_data.length()));

                    for (int i = 0; i < new_data.length(); i++) {
                        JSONObject obj_data = new_data.getJSONObject(i);

                        String sync_counter = obj_data.getString("sync_counter");
                        String datetime_counter = obj_data.getString("sync_at");
                        String update_counter = obj_data.getString("update_counter");
                        String total_count = obj_data.getString("total_count");
                        String username = obj_data.getString("username");

                        datetime_counter = sqLiteHelper.convertDateFormat(datetime_counter, "yyyy-MM-dd kk:mm:ss", "yyyy-MM-dd hh:mm a", 2);

                        list.add(new SyncMonitoringItem(sync_counter, datetime_counter, update_counter, total_count, username));
                    }

                    adapter.notifyDataSetChanged();
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            try {
                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                JSONObject data = new JSONObject(responseBody);
                String description = data.getString("description");
                Toasty.error(getApplicationContext(), description, Toasty.LENGTH_LONG).show();
            } catch (Exception e) {
                queue.cancelAll(this);
                Toasty.error(getApplicationContext(), "No network found!", Toasty.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        queue.add(request);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(SyncMonitoringList.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
