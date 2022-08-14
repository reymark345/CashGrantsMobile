package com.example.cashgrantsmobile;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.cashgrantsmobile.Database.SQLiteHelper;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.Scanner.ScanCashCard;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.material.navigation.NavigationView;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    CardView CashCardScanner, InventoryList, PullData, SyncData, Logout;
    ImageButton DarkMode;
    public static SQLiteHelper sqLiteHelper;
    private SQLiteDatabase mDatabase;
    TextView txtInventoryCount, txtPendingCount, txtPullDataCount;
    public boolean EnableNightMode = false;
    private String night = "true";
    private String light = "false";
    String status;

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        createDatabase();
        //CardView
        CashCardScanner = (CardView) findViewById(R.id.CardScan);
        InventoryList = (CardView) findViewById(R.id.inventoryList);
        PullData = (CardView) findViewById(R.id.pullData);
        SyncData = (CardView) findViewById(R.id.syncData);
        Logout = (CardView) findViewById(R.id.logout);

        //TextView
        txtInventoryCount =(TextView)findViewById(R.id.txtInventoryAmount);
        txtPendingCount =(TextView)findViewById(R.id.txtPending);
        txtPullDataCount = findViewById(R.id.textPullData);
        //Button
        DarkMode =(ImageButton) findViewById(R.id.textViews);

        drawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.navigation_open,R.string.navigation_close);
        drawerlayout.addDrawerListener(toggle);
        toggle.syncState();


        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        darkModeStatus();
        dashboardDataCount();
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
                finish();
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
                Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Inventory.InventoryList.class);
                startActivity(intent);
                finish();

            }
        });
        PullData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Pull.PullData.class);
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
                sqLiteHelper.deleteAccess();

                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("tokenStatus", "0");
                myEdit.commit();
                Toasty.success(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT, true).show();
                Intent intent = new Intent(MainActivity.this, Activity_Splash_Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card_actual_no VARCHAR, accomplish_by VARCHAR, informant VARCHAR, cc_image BLOB , id_image BLOB, cash_card_scanned_no VARCHAR , card_scanning_status VARCHAR, date_insert DATETIME DEFAULT CURRENT_TIMESTAMP, accomplish_img BLOB , informant_image BLOB, attested_img BLOB, attested VARCHAR)");
//        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card_actual_no VARCHAR, accomplish_by VARCHAR, informant VARCHAR, cc_image BLOB , id_image BLOB, cash_card_scanned_no VARCHAR , card_scanning_status VARCHAR, date_insert DATETIME DEFAULT CURRENT_TIMESTAMP, accomplish_img BLOB , informant_image BLOB, attested_img BLOB, attested VARCHAR, household_no VARCHAR, full_name VARCHAR,client_status VARCHAR,address VARCHAR,sex VARCHAR,set VARCHAR,contact_no VARCHAR,assigned_c VARCHAR,minor_grantee VARCHAR, data_card_released VARCHAR, who_released_card VARCHAR, place_card_released VARCHAR, current_grantee VARCHAR, card_availability VARCHAR, availability_reason VARCHAR, card_availability VARCHAR )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS Api(Id INTEGER PRIMARY KEY AUTOINCREMENT, token VARCHAR, user_id VARCHAR, email VARCHAR, mobile VARCHAR, name VARCHAR, username VARCHAR )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, upload_history_id INTEGER, created_at TIMESTAMP, updated_at TIMESTAMP, validated_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring_details(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, assigned_staff VARCHAR, minor_grantee VARCHAR, contact INTEGER, current_grantee_card_release_date DATE, current_grantee_card_release_place VARCHAR, current_grantee_card_release_by VARCHAR, current_grantee_is_available VARCHAR, current_grantee_reason VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, other_card_is_available VARCHAR, other_card_reason VARCHAR, nma_amount DECIMAL, nma_date_claimed DATE, nma_reason VARCHAR, nma_remarks VARCHAR, pawn_name_of_lender VARCHAR, pawn_date DATE, pawn_retrieved_date DATE, pawn_status VARCHAR, pawn_reason VARCHAR, pawn_offense_history VARCHAR, pawn_offense_date DATE, pawn_remarks VARCHAR, pawn_intervention_staff VARCHAR, pawn_other_details VARCHAR, accomplish_e_signature BLOB, informant_e_signature BLOB, attested_by_e_signature BLOB, attested_by_full_name VARCHAR, other_card_number_series_1 VARCHAR, other_card_number_series_2 VARCHAR, other_card_number_series_3 VARCHAR, emv_database_monitoring_id INTEGER, current_grantee_card_number_series VARCHAR, user_id INTEGER, sync_at TIMESTAMP, created_at TIMESTAMP, updated_at TIMESTAMP)");
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
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,accomplish_by,accomplish_by,id_image,cash_card_scanned_no, card_scanning_status FROM CgList");
        int z = cursor.getCount();
        txtInventoryCount.setText(String.valueOf(z));
        txtPendingCount.setText(String.valueOf(z));
    }

    public void dashboardDataCount() {
        Cursor emvList = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring");
        txtPullDataCount.setText(String.valueOf(emvList.getCount()));
    }
}