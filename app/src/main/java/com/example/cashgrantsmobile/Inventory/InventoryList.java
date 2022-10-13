package com.example.cashgrantsmobile.Inventory;


import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cashgrantsmobile.Logs.LogsData;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Scanner.ScannedDetails;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class InventoryList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Inventory> list;
    InventoryListAdapter adapter = null;
    String cashCardNumber;
    int status;
    byte[] id_image;
    private TextView tvSearch, tvResultHh, tvIdentifier;
    EditText  filterDate, filterHousehold;
//    AutoCompleteTextView spinFilterType;
    String ftype, fhhid, fdate;
    String[] typeOptions = new String[]{"", "complete", "incomplete"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_list);
        gridView = (GridView) findViewById(R.id.gridView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("ExcludeInclude");
            Toasty.success(this,value+"d", Toasty.LENGTH_SHORT).show();
        }
        tvSearch = findViewById(R.id.tvSearch);
        tvResultHh = findViewById(R.id.tvResultHousehold);
        tvIdentifier = findViewById(R.id.tvIdentifier);
        tvResultHh.setVisibility(View.INVISIBLE);
        tvIdentifier.setVisibility(View.INVISIBLE);

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItem();
            }
        });
        list = new ArrayList<>();
        adapter = new InventoryListAdapter(this, R.layout.activity_inventory_items, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Inventory data = list.get(position);
                Integer emv_id = data.getEmvId();
                Long l= new Long(id);
                int i=l.intValue();
                int stats = i+1;
                Cursor cursor = sqLiteHelper.getData("SELECT id,beneficiary_picture,card_scanning_status FROM emv_database_monitoring_details WHERE id ="+stats);
                while (cursor.moveToNext()) {
                    id_image = cursor.getBlob(1);
                    status = cursor.getInt(2);
                }
                new SweetAlertDialog(InventoryList.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Edit information")
                        .setContentText("Please choose corresponding action")
                        .setConfirmText("Scanner")
                        .setCancelText("Details")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                ScannedDetails.scanned = false;

                                Intent in = new Intent(getApplicationContext(), ScannedDetails.class);
                                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString("signatureAccomplishment", "false");
                                myEdit.putInt("updateValue", emv_id);
                                myEdit.putString("identifier", "true");
                                myEdit.commit();

                                if (id_image ==null){
                                    in.putExtra("updateData", emv_id);
                                    in.putExtra("EmptyImageView","triggerEvent");
                                }
                                else{
                                    in.putExtra("updateData", emv_id);
                                }
                                startActivity(in);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                    Intent in = new Intent(getApplicationContext(), UpdateEntries.class);
                                    in.putExtra("list_emv_id", emv_id);
                                    startActivity(in);
                                    finish();
                            }
                        }).show();
                    }
                });
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterAllData(list,cashCardNumber,adapter);
    }

    public void searchItem() {
        final Dialog dialog = new Dialog(InventoryList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_inventory_list);
//        spinFilterType = dialog.findViewById(R.id.spinFilterType);
        filterHousehold = dialog.findViewById(R.id.edtFilterHHID);
        filterDate = dialog.findViewById(R.id.edtFilterDate);
        tvIdentifier = findViewById(R.id.tvIdentifier);
        TextInputLayout tilError = dialog.findViewById(R.id.til_Search);
        filterHousehold.setText("160310001-");

        Button submitButton = dialog.findViewById(R.id.btn_search);
        Button btnRefresh = dialog.findViewById(R.id.btnRefresh);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, typeOptions);
        adapterType.setDropDownViewResource(simple_spinner_dropdown_item);
//        spinFilterType.setAdapter(adapterType);

        filterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(filterDate);
            }
        });

//        spinFilterType.setText(ftype, false);
        filterHousehold.setText(fhhid);
        filterDate.setText(fdate);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ftype = spinFilterType.getText().toString();
                fhhid = filterHousehold.getText().toString();
                fdate = filterDate.getText().toString();

//                queries(fhhid, ftype, fdate);
                queries(fhhid, fdate);

                dialog.dismiss();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftype = null;
                fhhid = null;
                fdate = null;
                tvIdentifier.setVisibility(View.INVISIBLE);
                tvResultHh.setVisibility(View.INVISIBLE);
                filterAllData(list,cashCardNumber,adapter);
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

        new DatePickerDialog(InventoryList.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void queries(String household_no, String filter_date){
        try {
            Cursor cursor = sqLiteHelper.getData("SELECT evd.id, gv.hh_id, cvd.card_number_inputted, cvd.card_number_system_generated, cvd.card_number_series FROM emv_validation_details AS evd LEFT JOIN card_validation_details AS cvd ON cvd.id = evd.card_validation_detail_id LEFT JOIN grantee_validations AS gv ON gv.id = evd.grantee_validation_id WHERE gv.hh_id LIKE'%"+household_no+"%' AND DATE(evd.created_at) LIKE'%"+filter_date+"%' order by evd.id DESC");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String hhNumber = cursor.getString(1);
                if (cursor.getString(2).matches("")){
                    cashCardNumber = cursor.getString(3);
                }
                else{
                    cashCardNumber = cursor.getString(2);
                }
                String grantee_number = cursor.getString(3);
                String seriesNumber = cursor.getString(4);
//                int status = cursor.getInt(8);
                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, id,hhNumber));

                //                int id = cursor.getInt(0);
//                String hhNumber = cursor.getString(1);
//                if (cursor.getString(2).matches("")){
//                    cashCardNumber = cursor.getString(7);
//                }
//                else{
//                    cashCardNumber = cursor.getString(2);
//                }
//                String grantee_number = cursor.getString(3);
//                String seriesNumber = cursor.getString(4);
//                byte[] CashCardImage = cursor.getBlob(5);
//                byte[] idImage = cursor.getBlob(6);
//                int status = cursor.getInt(8);
//                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, CashCardImage, idImage,status, id,hhNumber));
            }
            if (cursor.getCount()==0){
                tvIdentifier.setText("Household not found");
                tvIdentifier.setVisibility(View.VISIBLE);
            }
            else {
                tvIdentifier.setVisibility(View.INVISIBLE);
                Toasty.success(this,"Search successfully", Toasty.LENGTH_SHORT).show();
            }
            tvResultHh.setVisibility(View.VISIBLE);
            tvResultHh.setText(household_no);
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
    }

    public void filterAllData(ArrayList<Inventory> list, String cashCardNumber,InventoryListAdapter adapter){
        try {
            Log.d(TAG, "Nisulod kay diri");
            Cursor cursor = sqLiteHelper.getData("SELECT evd.id, gv.hh_id, cvd.card_number_inputted, cvd.card_number_system_generated, cvd.card_number_series FROM emv_validation_details AS evd LEFT JOIN card_validation_details AS cvd ON cvd.id = evd.card_validation_detail_id LEFT JOIN grantee_validations AS gv ON gv.id = evd.grantee_validation_id order by evd.id DESC");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String hhNumber = cursor.getString(1);
                if (cursor.getString(2).matches("")){
                    cashCardNumber = cursor.getString(3);
                }
                else{
                    cashCardNumber = cursor.getString(2);
                }
                String grantee_number = cursor.getString(3);
                String seriesNumber = cursor.getString(4);
//                byte[] CashCardImage = cursor.getBlob(5);
//                byte[] idImage = cursor.getBlob(6);
                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, id,hhNumber));
//                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, CashCardImage, idImage,status, id,hhNumber));
            }
            if (cursor.getCount()==0){
                tvIdentifier.setText("No Scanned available");
                tvIdentifier.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(InventoryList.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}