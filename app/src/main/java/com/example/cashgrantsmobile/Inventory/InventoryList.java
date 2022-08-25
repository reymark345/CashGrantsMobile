package com.example.cashgrantsmobile.Inventory;


import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Scanner.ScannedDetails;
import com.example.cashgrantsmobile.Update.UpdateEntries;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        EditText noteField = dialog.findViewById(R.id.edtSearch);
        TextInputLayout tilError = dialog.findViewById(R.id.til_Search);
        noteField.setText("160310001-");
        Button submitButton = dialog.findViewById(R.id.btn_search);
        Button btnRefresh = dialog.findViewById(R.id.btnRefresh);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteField.length()==0){
                    tilError.setError("Please fill this blank");
                }
                else {
                    String household = noteField.getText().toString();

                    queries(household);

                    dialog.dismiss();
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvIdentifier.setVisibility(View.INVISIBLE);
                tvResultHh.setVisibility(View.INVISIBLE);
                filterAllData(list,cashCardNumber,adapter);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void queries(String household_no){
        try {
            Cursor cursor = sqLiteHelper.getData("SELECT id,hh_id,current_grantee_card_number ,accomplish_by_full_name,informant_full_name,current_cash_card_picture , beneficiary_picture, cash_card_scanned_no, card_scanning_status FROM emv_database_monitoring_details WHERE hh_id LIKE'%"+household_no+"%'");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String hhNumber = cursor.getString(1);
                if (cursor.getString(2).matches("")){
                    cashCardNumber = cursor.getString(7);
                }
                else{
                    cashCardNumber = cursor.getString(2);
                }
                String grantee_number = cursor.getString(3);
                String seriesNumber = cursor.getString(4);
                byte[] CashCardImage = cursor.getBlob(5);
                byte[] idImage = cursor.getBlob(6);
                int status = cursor.getInt(8);
                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, CashCardImage, idImage,status, id,hhNumber));
            }
            if (cursor.getCount()==0){
                tvIdentifier.setText("Household not found");
                tvIdentifier.setVisibility(View.VISIBLE);
            }
            else {
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
            Cursor cursor = sqLiteHelper.getData("SELECT id,hh_id,current_grantee_card_number ,accomplish_by_full_name,informant_full_name,current_cash_card_picture , beneficiary_picture, cash_card_scanned_no, card_scanning_status FROM emv_database_monitoring_details");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String hhNumber = cursor.getString(1);
                if (cursor.getString(2).matches("")){
                    cashCardNumber = cursor.getString(7);
                }
                else{
                    cashCardNumber = cursor.getString(2);
                }
                String grantee_number = cursor.getString(3);
                String seriesNumber = cursor.getString(4);
                byte[] CashCardImage = cursor.getBlob(5);
                byte[] idImage = cursor.getBlob(6);
                int status = cursor.getInt(8);
                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, CashCardImage, idImage,status, id,hhNumber));
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