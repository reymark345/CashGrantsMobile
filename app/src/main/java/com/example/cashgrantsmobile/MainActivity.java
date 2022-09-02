package com.example.cashgrantsmobile;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cashgrantsmobile.Database.SQLiteHelper;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.Scanner.ScanCashCard;
import com.google.android.material.navigation.NavigationView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    CardView CashCardScanner, InventoryList, PullUpdateData, SyncData, UpdateData, Logout;
    ImageButton DarkMode;
    public static SQLiteHelper sqLiteHelper;
    TextView txtInventoryCount, txtPullUpdateDataCount, txtUpdateDataCount, txtSyncDataCount, txtScannedTotal, txtErrorTotal, txtSyncTotal;
    public boolean EnableNightMode = false;
    private String night = "true";
    private String light = "false";
    String status;

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Toolbar toolbar;

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS Api(Id INTEGER PRIMARY KEY AUTOINCREMENT, token VARCHAR, user_id VARCHAR, email VARCHAR, mobile VARCHAR, name VARCHAR, username VARCHAR )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, upload_history_id INTEGER, created_at TIMESTAMP, updated_at TIMESTAMP, validated_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring_details(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, assigned_staff VARCHAR, minor_grantee VARCHAR, contact INTEGER, current_grantee_card_release_date DATE, current_grantee_card_release_place VARCHAR, current_grantee_card_release_by VARCHAR, current_grantee_is_available VARCHAR, current_grantee_reason VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, other_card_is_available VARCHAR, other_card_reason VARCHAR, nma_amount DECIMAL, nma_date_claimed DATE, nma_reason VARCHAR, nma_remarks VARCHAR, pawn_name_of_lender VARCHAR, pawn_date DATE, pawn_retrieved_date DATE, pawn_status VARCHAR, pawn_reason VARCHAR, pawn_offense_history VARCHAR, pawn_offense_date DATE, pawn_remarks VARCHAR, pawn_intervention_staff VARCHAR, pawn_other_details VARCHAR, informant_full_name VARCHAR, accomplish_by_full_name VARCHAR, accomplish_e_signature BLOB, informant_e_signature BLOB, attested_by_e_signature BLOB, current_cash_card_picture BLOB,cash_card_scanned_no BLOB,beneficiary_picture BLOB, attested_by_full_name VARCHAR, other_card_number_series_1 VARCHAR, other_card_number_series_2 VARCHAR, other_card_number_series_3 VARCHAR, emv_database_monitoring_id INTEGER, current_grantee_card_number_series VARCHAR, user_id INTEGER, sync_at TIMESTAMP, created_at TIMESTAMP, updated_at TIMESTAMP, card_scanning_status INTEGER, other_card_is_available_2 VARCHAR, other_card_is_available_3 VARCHAR, other_card_reason_2 VARCHAR, other_card_reason_3 VARCHAR, pawn_loaned_amount DECIMAL, pawn_lender_address VARCHAR, pawn_interest DECIMAL)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS logs(id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, type VARCHAR, description VARCHAR, created_at TIMESTAMP)");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        createDatabase();
        //CardView
        CashCardScanner = (CardView) findViewById(R.id.CardScan);
        InventoryList = (CardView) findViewById(R.id.inventoryList);
        PullUpdateData = (CardView) findViewById(R.id.pullUpdateData);
        SyncData = (CardView) findViewById(R.id.syncData);
        Logout = (CardView) findViewById(R.id.logout);




//        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
//        String accomplish_name = sh.getString("accomplish_by_name", "");

        //TextView
        txtInventoryCount =(TextView)findViewById(R.id.txtInventoryAmount);
        txtPullUpdateDataCount = findViewById(R.id.textPullUpdateData);
        txtSyncDataCount = findViewById(R.id.txtSyncData);
        txtScannedTotal = findViewById(R.id.scannedTotal);
        txtErrorTotal = findViewById(R.id.errorTotal);
        txtSyncTotal = findViewById(R.id.syncTotal);

        //Button
        DarkMode =(ImageButton) findViewById(R.id.textViews);

        drawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        navigationView.bringToFront();


        View headerView = navigationView.getHeaderView(0);
        TextView navfullName = (TextView) headerView.findViewById(R.id.txt_accomplish);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String accomplish_name = sh.getString("accomplish_by_name", "");
        navfullName.setText(accomplish_name);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.logout_menu)
                {
                    logout();
                }
                return false;
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();


        darkModeStatus();
        dashboardDataCount();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
        }

        CashCardScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanCashCard.class);
                startActivity(intent);
                finish();
            }
        });
        DarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(EnableNightMode ==false){
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    EnableNightMode = true;
//                    sqLiteHelper.updateDarkmodeStatus(night,1);
//                }
//                else{
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    EnableNightMode = false;
//                    sqLiteHelper.updateDarkmodeStatus(light,1);
//                }
            }
        });
        InventoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Inventory.InventoryList.class);
                startActivity(intent);
                finish();

            }
        });
        PullUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.PullUpdate.PullUpdateData.class);
                startActivity(intent);
                finish();
            }
        });
        SyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Sync.SyncData.class);
                startActivity(intent);
                finish();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout(){
    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Logout?")
            .setContentText("Are you sure?")
            .setConfirmText("Confirm")
            .showCancelButton(true)
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sqLiteHelper.deleteAccess();
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("tokenStatus", "0");
                    myEdit.commit();
                    clearSharedPref();
                    Toasty.success(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT, true).show();
                    Intent intent = new Intent(MainActivity.this, Activity_Splash_Login.class);
                    startActivity(intent);
                    finish();
                }
            }).show();
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

    public void dashboardDataCount() {
        Cursor user_data = MainActivity.sqLiteHelper.getData("SELECT username FROM Api");
        String username = null;
        while (user_data.moveToNext()) {
            username = user_data.getString(0);
        }
        Cursor listCount = MainActivity.sqLiteHelper.getData("SELECT id,current_grantee_card_number ,accomplish_by_full_name,accomplish_by_full_name,beneficiary_picture,cash_card_scanned_no, card_scanning_status FROM emv_database_monitoring_details");
        Cursor emvList = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring");
        Cursor emvListValidated = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring WHERE validated_at != 'null'");
        Cursor unsyncEmvList = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring_details");
        Cursor scanned_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"' AND type='scanned'");
        Cursor error_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"'AND type='error'");
        Cursor sync_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"'AND type='sync'");

        txtInventoryCount.setText(String.valueOf(listCount.getCount()));
        txtPullUpdateDataCount.setText(String.valueOf(emvList.getCount()));
        txtSyncDataCount.setText(String.valueOf(unsyncEmvList.getCount()));
        txtScannedTotal.setText(String.valueOf(scanned_total.getCount()));
        txtErrorTotal.setText(String.valueOf(error_total.getCount()));
        txtSyncTotal.setText(String.valueOf(sync_total.getCount()));
    }

    public void clearSharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        //1
        myEdit.putString("hh_id", "");
        myEdit.putString("full_name", "");
        myEdit.putString("client_status", "");
        myEdit.putString("address", "");
        myEdit.putString("sex", "");
        myEdit.putString("hh_set_group", "");
        myEdit.putString("contact_no", "");
        myEdit.putString("assigned", "");
        myEdit.putString("minor_grantee", "");

        //2
        myEdit.putString("card_released", "");
        myEdit.putString("who_released", "");
        myEdit.putString("place_released", "");
        myEdit.putString("current_grantee_number", "");
        myEdit.putString("is_available", "");
        myEdit.putString("is_available_reason", "");
        myEdit.putString("other_card_number_1", "");
        myEdit.putString("other_card_holder_name_1", "");
        myEdit.putString("other_is_available_1", "");
        myEdit.putString("other_is_available_reason_1", "");
        myEdit.putString("other_card_number_2", "");
        myEdit.putString("other_card_holder_name_2", "");
        myEdit.putString("other_is_available_2", "");
        myEdit.putString("other_is_available_reason_2", "");
        myEdit.putString("other_card_number_3", "");
        myEdit.putString("other_card_holder_name_3", "");
        myEdit.putString("other_is_available_3", "");
        myEdit.putString("other_is_available_reason_3", "");

        myEdit.putString("accomplish_by_name", "");

        //3
        myEdit.putString("nma_amount", "");
        myEdit.putString("nma_reason", "");
        myEdit.putString("date_withdrawn", "");
        myEdit.putString("remarks", "");

        //4
        myEdit.putString("lender_name", "");
        myEdit.putString("pawning_date", "");
        myEdit.putString("loaned_amount", "");
        myEdit.putString("lender_address", "");
        myEdit.putString("date_retrieved", "");
        myEdit.putString("interest", "");
        myEdit.putString("spin_status", "");
        myEdit.putString("pawning_reason", "");
        myEdit.putString("offense_history", "");
        myEdit.putString("offense_history_date", "");
        myEdit.putString("pd_remarks", "");
        myEdit.putString("intervention", "");
        myEdit.putString("other_details", "");
        myEdit.commit();
    }
}