package com.example.cashgrantsmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CardView CashCardScanner, InventoryList, SyncData, Logout;
    ImageButton DarkMode;
    public static SQLiteHelper sqLiteHelper;
    private SQLiteDatabase mDatabase;
    TextView txtInventoryCount, txtPendingCount;
    public boolean EnableNightMode = false;
    private String night = "true";
    private String light = "false";

    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDatabase();
        //CardView
        CashCardScanner = (CardView) findViewById(R.id.CardScan);
        InventoryList = (CardView) findViewById(R.id.inventoryList);
        SyncData = (CardView) findViewById(R.id.syncData);
        Logout = (CardView) findViewById(R.id.logout);

        //TextView
        txtInventoryCount =(TextView)findViewById(R.id.txtInventoryAmount);
        txtPendingCount =(TextView)findViewById(R.id.txtPending);
        //Button
        DarkMode =(ImageButton) findViewById(R.id.textViews);
        darkModeStatus();
        InventoryListCount();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        CashCardScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanCashCard.class);
                startActivity(intent);
            }
        });
        DarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(EnableNightMode ==false){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    EnableNightMode = true;
                    sqLiteHelper.updateDarkmodeStatus(night,1);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    EnableNightMode = false;
                    sqLiteHelper.updateDarkmodeStatus(light,1);
                }
            }
        });
        InventoryList.setOnClickListener(new View.OnClickListener() {
            @Override 
            public void onClick(View v) {
                new android.os.Handler(Looper.getMainLooper()).postDelayed(
                        new Runnable() {
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, InventoryList.class);
                                startActivity(intent);
                            }
                        },
                        300);
            }
        });
        SyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Sync Data",Toast.LENGTH_SHORT).show();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card VARCHAR, hh_number VARCHAR,series_number VARCHAR, cc_image BLOB , id_image BLOB)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
    }

    public void darkModeStatus(){
        try {
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            String count = "SELECT count(*) FROM DarkMode";
            Cursor mCursor = db.rawQuery(count, null);
            mCursor.moveToFirst();
            int iCount = mCursor.getInt(0);
            if(iCount==0){
                sqLiteHelper.insertDarkModeStatus(light);
            }
            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,status FROM DarkMode");
            while (cursor.moveToNext()) {
                status = cursor.getString(1);
            }
            if (status.matches(light)){
                DarkMode.setImageResource(R.drawable.ic_light);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else if(status.matches(night)){
                DarkMode.setImageResource(R.drawable.ic_night);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this,"Please Contact IT Administrator",Toast.LENGTH_SHORT).show();
        }

    }
    public void InventoryListCount(){
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card,hh_number,series_number,id_image FROM CgList");
        int z = cursor.getCount();
        txtInventoryCount.setText(String.valueOf(z));
        txtPendingCount.setText(String.valueOf(z));

    }



}