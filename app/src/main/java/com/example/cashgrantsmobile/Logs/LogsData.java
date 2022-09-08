package com.example.cashgrantsmobile.Logs;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class LogsData extends AppCompatActivity {

    GridView gridView;
    ArrayList<Logs> list;
    LogsListAdapter adapter = null;
    TextView textStatus, textHHID, textDesc, textUsername, tvIdentifier, textDate, tvSearch, tvDelete;
    EditText  filterDate, filterHousehold;
    AutoCompleteTextView spinFilterType;
    String ftype, fhhid, fdate;
    String[] typeOptions = new String[]{"", "error", "pull", "update", "sync"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logs_list);
        gridView = (GridView) findViewById(R.id.gridView);

        textStatus = findViewById(R.id.textStatus);
        textHHID = findViewById(R.id.textHHID);
        textDesc = findViewById(R.id.textDesc);
        textUsername = findViewById(R.id.textUsername);
        textDate = findViewById(R.id.textDate);
        tvSearch = findViewById(R.id.logsSearch);
        tvDelete = findViewById(R.id.logsDelete);

        list = new ArrayList<>();
        adapter = new LogsListAdapter(this, R.layout.logs_list_item, list);
        gridView.setAdapter(adapter);
        try {
            getData(list,adapter, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem();
            }
        });

        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLogs();
            }
        });
    }

    public void deleteLogs() {
        new SweetAlertDialog(LogsData.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Delete Confirmation")
                .setContentText("Are you sure you want to delete all logs!")
                .setConfirmText("Yes")
                .setCancelText("No")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Toasty.info(LogsData.this, "Deleting Logs...", Toast.LENGTH_SHORT, true).show();
                        sqLiteHelper.deleteLogs();
                        sDialog.dismiss();
                        try {
                            getData(list,adapter,null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                }).show();
    }

    public void searchItem() {
        final Dialog dialog = new Dialog(LogsData.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_logs_list);

        Button btnSearch = dialog.findViewById(R.id.btn_search);
        Button btnReset = dialog.findViewById(R.id.btnReset);
        spinFilterType = dialog.findViewById(R.id.spinFilterType);
        filterHousehold = dialog.findViewById(R.id.edtFilterHHID);
        filterDate = dialog.findViewById(R.id.edtFilterDate);
        tvIdentifier = findViewById(R.id.tvIdentifier);

        filterDate.setFocusable(false);
        filterDate.setClickable(true);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, typeOptions);
        adapterType.setDropDownViewResource(simple_spinner_dropdown_item);
        spinFilterType.setAdapter(adapterType);

        filterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(filterDate);
            }
        });

        spinFilterType.setText(ftype, false);
        filterHousehold.setText(fhhid);
        filterDate.setText(fdate);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftype = spinFilterType.getText().toString();
                fhhid = filterHousehold.getText().toString();
                fdate = filterDate.getText().toString();

                JSONObject filterData = new JSONObject();
                try {
                    filterData.put("type", spinFilterType.getText().toString());
                    filterData.put("hh_id", filterHousehold.getText().toString());
                    filterData.put("date", filterDate.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    getData(list,adapter,filterData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ftype = null;
                    fhhid = null;
                    fdate = null;
                    getData(list,adapter,null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(LogsData.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void getData(ArrayList<Logs> list,LogsListAdapter adapter, JSONObject filterData) throws JSONException {
        tvIdentifier = findViewById(R.id.tvIdentifier);
        textStatus = findViewById(R.id.textStatus);
        String filterType = null;
        String filterHousehold = null;
        String filterDate = null;

        if (filterData != null) {
            filterType = filterData.getString("type");
            filterHousehold = filterData.getString("hh_id");
            filterDate = filterData.getString("date");
        }


        try {
            Cursor logsData;
            if (filterData!=null) {
                logsData = sqLiteHelper.getData("SELECT id, username, type, hh_id, description, created_at  FROM logs WHERE type LIKE '%"+filterType+"%' AND hh_id LIKE '%"+filterHousehold+"%' AND DATE(created_at) LIKE '%"+filterDate+"%' ORDER BY id DESC");
            } else {
                logsData = sqLiteHelper.getData("SELECT id, username, type, hh_id, description, created_at  FROM logs ORDER BY id DESC");
            }
            list.clear();
            while (logsData.moveToNext()) {
                int id = logsData.getInt(0);
                String username = logsData.getString(1);
                String type = logsData.getString(2);
                String hh_id = logsData.getString(3);
                String description = logsData.getString(4);
                String created_at = logsData.getString(5);

                list.add(new Logs(id, username, type, hh_id, description, created_at));
            }
            if (logsData.getCount() > 0) {
                tvIdentifier.setText("");
            } else {
                tvIdentifier.setText("No Data Found!");
            }
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(LogsData.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
