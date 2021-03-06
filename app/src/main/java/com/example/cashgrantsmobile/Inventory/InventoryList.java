package com.example.cashgrantsmobile.Inventory;


import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Scanner.ScannedDetails;
import java.lang.reflect.Field;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class InventoryList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Inventory> list;
    InventoryListAdapter adapter = null;
    String cashCardNumber;
    private Toolbar mToolbars;
    int status;
    byte[] id_image;
    String DialogStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        gridView = (GridView) findViewById(R.id.gridView);
        mToolbars = findViewById(R.id.mainToolbar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("ExcludeInclude");
            Toasty.success(this,value+"d", Toasty.LENGTH_SHORT).show();
        }

        setSupportActionBar(mToolbars);
        getSupportActionBar().setTitle("Inventory List");
        list = new ArrayList<>();
        adapter = new InventoryListAdapter(this, R.layout.activity_inventory_items, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Long l= new Long(id);
                int i=l.intValue();
                int stats = i+1;

                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,id_image,card_scanning_status FROM CgList WHERE id ="+stats);
                while (cursor.moveToNext()) {
                    id_image = cursor.getBlob(1);
                    status = cursor.getInt(2);
                }

                if (status==0){DialogStatus ="Include";}else{DialogStatus ="Exclude";}
                new SweetAlertDialog(InventoryList.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Please choose corresponding action")
                        .setConfirmText("Update")
                        .setCancelText(DialogStatus)
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                ScannedDetails.scanned = false;
                                Intent in = new Intent(getApplicationContext(), ScannedDetails.class);

                                if (id_image.length ==1){
                                    in.putExtra("updateData", i);
                                    in.putExtra("EmptyImageView","triggerEvent");
                                }
                                else{
                                    in.putExtra("updateData", i);
                                }
                                startActivity(in);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                if (id_image.length ==1){
                                    Toasty.error(getApplicationContext(),"Update data first ", Toasty.LENGTH_SHORT).show();
                                }
                                else{
                                    sqLiteHelper.excludeData(i,status);
                                    Intent in = new Intent(getApplicationContext(), InventoryList.class);
                                    in.putExtra("ExcludeInclude", DialogStatus);
                                    startActivity(in);
                                    finish();

                                }
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
        try {
            Cursor cursor = sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no, card_scanning_status FROM CgList");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                if (cursor.getString(1).matches("")){
                    cashCardNumber = cursor.getString(6);
                }
                else{
                    cashCardNumber = cursor.getString(1);
                }
                String hhNumber = cursor.getString(2);
                String seriesNumber = cursor.getString(3);
                byte[] CashCardImage = cursor.getBlob(4);
                byte[] idImage = cursor.getBlob(5);
                int status = cursor.getInt(7);
                list.add(new Inventory(cashCardNumber, hhNumber,seriesNumber, CashCardImage, idImage,status, id));

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