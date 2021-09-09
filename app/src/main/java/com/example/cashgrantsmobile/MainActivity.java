package com.example.cashgrantsmobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CardView CashCardScanner, InventoryList, SyncData, Logout;
    public static SQLiteHelper sqLiteHelper;
    private SQLiteDatabase mDatabase;
    TextView txtInventoryCount, txtPendingCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card VARCHAR, hh_number VARCHAR,series_number VARCHAR, cc_image BLOB , id_image BLOB)");

        //CardView
        CashCardScanner = (CardView) findViewById(R.id.CardScan);
        InventoryList = (CardView) findViewById(R.id.inventoryList);
        SyncData = (CardView) findViewById(R.id.syncData);
        Logout = (CardView) findViewById(R.id.logout);

        //TextView
        txtInventoryCount =(TextView)findViewById(R.id.txtInventoryAmount);
        txtPendingCount =(TextView)findViewById(R.id.txtPending);
        Count();
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

    public void Count(){
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card,hh_number,series_number,id_image FROM CgList");
        int z = cursor.getCount();
        txtInventoryCount.setText(String.valueOf(z));
        txtPendingCount.setText(String.valueOf(z));

    }



}