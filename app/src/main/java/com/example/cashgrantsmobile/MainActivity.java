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
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

    CardView CashCardScanner, InventoryList, PullPsgcData, SyncData, LogsData, Logout, UnvalidatedData;
    ImageButton DarkMode;
    public static SQLiteHelper sqLiteHelper;
    TextView txtInventoryCount, txtPullPsgcDataCount, txtLogsTotal, txtSyncData, txtScannedTotal, txtErrorTotal, txtIncompleteTotal, txtUnvalidatedCount;
    public boolean EnableNightMode = false;
    private final String night = "true";
    private final String light = "false";
    String status;

    DrawerLayout drawerlayout;
    NavigationView navigationView;
    Toolbar toolbar;
    Cursor psgcList;
    Cursor emvList;

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS Api(Id INTEGER PRIMARY KEY AUTOINCREMENT, token VARCHAR, user_id VARCHAR, email VARCHAR, mobile VARCHAR, name VARCHAR, username VARCHAR )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS logs(id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, type VARCHAR, hh_id VARCHAR, description VARCHAR, created_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS tmp_blob(Id INTEGER PRIMARY KEY AUTOINCREMENT, scanned_e_image BLOB, additional_id_image BLOB,grantee_e_image BLOB, other_card_e_image_1 BLOB,other_card_e_image_2 BLOB,other_card_e_image_3 BLOB,other_card_e_image_4 BLOB,other_card_e_image_5 BLOB)");

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, hh_status VARCHAR, contact_no VARCHAR, contact_no_of VARCHAR, is_grantee VARCHAR, is_minor VARCHAR, relationship_to_grantee VARCHAR, assigned_staff VARCHAR, representative_name VARCHAR, grantee_validation_id INTEGER, pawning_validation_detail_id INTEGER, nma_validation_id INTEGER, card_validation_detail_id INTEGER,emv_validation_id INTEGER, sync_at TIMESTAMP, user_id INTEGER, additional_image BLOB, overall_remarks VARCHAR, created_at TIMESTAMP, updated_at TIMESTAMP, contact_no_of_others TEXT)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS grantee_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, hh_id INTEGER, first_name VARCHAR, last_name VARCHAR, middle_name VARCHAR, ext_name VARCHAR, sex VARCHAR, province_code VARCHAR, municipality_code VARCHAR, barangay_code VARCHAR, hh_set VARCHAR, grantee_image BLOB, created_at TIMESTAMP, other_ext_name TEXT)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS nma_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, amount DECIMAL, date_claimed DATE, reason VARCHAR, remarks VARCHAR,created_at TIMESTAMP, nma_others_reason text, nma_non_emv VARCHAR, nma_card_name VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS card_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, card_number_prefilled VARCHAR, card_number_system_generated VARCHAR, card_number_inputted VARCHAR, card_number_series VARCHAR, distribution_status VARCHAR, release_date DATE, release_by VARCHAR, release_place VARCHAR, card_physically_presented VARCHAR, card_pin_is_attached VARCHAR, reason_not_presented VARCHAR, reason_unclaimed VARCHAR, card_replacement_requests VARCHAR,card_replacement_submitted_details VARCHAR, card_image BLOB, created_at TIMESTAMP, others_reason_not_presented TEXT, others_reason_unclaimed TEXT, distribution_status_record VARCHAR, release_date_record VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS other_card_validations(id INTEGER PRIMARY KEY AUTOINCREMENT,card_holder_name VARCHAR,card_number_system_generated VARCHAR, card_number_inputted VARCHAR, card_number_series VARCHAR, distribution_status VARCHAR, release_date DATE, release_by VARCHAR, release_place VARCHAR, card_physically_presented VARCHAR, card_pin_is_attached VARCHAR, reason_not_presented VARCHAR, reason_unclaimed VARCHAR, card_replacement_requests VARCHAR,card_replacement_request_submitted_details VARCHAR,pawning_remarks VARCHAR, emv_validation_detail_id INTEGER,other_image BLOB,created_at TIMESTAMP, others_reason_not_presented TEXT, others_reason_unclaimed TEXT, distribution_status_record VARCHAR, release_date_record VARCHAR, card_number_prefilled VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS pawning_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, lender_name VARCHAR, lender_address VARCHAR, date_pawned DATE, date_retrieved DATE, loan_amount DECIMAL, status VARCHAR, reason VARCHAR, interest DECIMAL, offense_history VARCHAR, offense_date VARCHAR, remarks VARCHAR, staff_intervention VARCHAR,other_details VARCHAR,created_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, first_name VARCHAR, last_name VARCHAR, middle_name VARCHAR, ext_name VARCHAR, hh_id VARCHAR, hh_status VARCHAR, province VARCHAR, municipality VARCHAR, barangay VARCHAR, sex VARCHAR, hh_set_group VARCHAR, nma_amount DECIMAL,grantee_card_number VARCHAR,grantee_distribution_status VARCHAR, grantee_card_release_date VARCHAR, other_card_number_1 VARCHAR,other_card_holder_name_1 VARCHAR,other_card_distribution_status_1 VARCHAR,other_card_release_date_1 VARCHAR, other_card_number_2 VARCHAR,other_card_holder_name_2 VARCHAR,other_card_distribution_status_2 VARCHAR,other_card_release_date_2,other_card_number_3 VARCHAR,other_card_holder_name_3 VARCHAR,other_card_distribution_status_3 VARCHAR,other_card_release_date_3 VARCHAR, other_card_number_4 VARCHAR,other_card_holder_name_4 VARCHAR,other_card_distribution_status_4 VARCHAR,other_card_release_date_4 VARCHAR,other_card_number_5 VARCHAR,other_card_holder_name_5 VARCHAR,other_card_distribution_status_5 VARCHAR,other_card_release_date_5 VARCHAR,upload_history_id VARCHAR,record_counter VARCHAR,created_at TIMESTAMP,updated_at TIMESTAMP,validated_at DATE,nma_non_emv VARCHAR, nma_card_name VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS psgc(id INTEGER PRIMARY KEY AUTOINCREMENT, name_new VARCHAR, name_old VARCHAR, code VARCHAR, correspondence_code VARCHAR, geographic_level VARCHAR, create_at TIMESTAMP, updated_at TIMESTAMP)");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        createDatabase();
        //CardView
        CashCardScanner = (CardView) findViewById(R.id.cvCashCard);
        InventoryList = (CardView) findViewById(R.id.cvCashCardList);
        UnvalidatedData = (CardView) findViewById(R.id.mc_unvalidated_field);
        PullPsgcData = (CardView) findViewById(R.id.cvPullPsgcData);
        LogsData = (CardView) findViewById(R.id.logsItem);
        SyncData = (CardView) findViewById(R.id.cvSyncData);
        Logout = (CardView) findViewById(R.id.cvLogout);


        //TextView
        txtInventoryCount =(TextView)findViewById(R.id.txtInventoryAmount);
        txtPullPsgcDataCount = findViewById(R.id.textPullPsgcData);
        txtScannedTotal = findViewById(R.id.scannedTotal);
        txtErrorTotal = findViewById(R.id.errorTotal);
        txtIncompleteTotal = findViewById(R.id.incompleteTotal);
        txtLogsTotal = findViewById(R.id.textLogsCount);
        txtSyncData = findViewById(R.id.txtSyncData);
        txtUnvalidatedCount = findViewById(R.id.txtUnvalidatedCount);

        //Button
        DarkMode =(ImageButton) findViewById(R.id.textViews);

        drawerlayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        navigationView.bringToFront();


        View headerView = navigationView.getHeaderView(0);
        TextView navfullName = (TextView) headerView.findViewById(R.id.txt_accomplish);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String accomplish_name = sh.getString("accomplish_by_name", "");
        navfullName.setText(accomplish_name);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.logout_menu)
            {
                logout();
            }
            return false;
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

        CashCardScanner.setOnClickListener(v -> {
            if (psgcList.getCount() ==0){
                String identifier = "PSGC";
                countIdentifier(identifier);
            }
            else if(emvList.getCount()==0){
                String identifier = "SYNC";
                countIdentifier(identifier);
            }
            else{
                Intent intent = new Intent(MainActivity.this, ScanCashCard.class);
                startActivity(intent);
                finish();
            }
        });
        DarkMode.setOnClickListener(v -> {
            if(!EnableNightMode){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                EnableNightMode = true;
                sqLiteHelper.updateDarkmodeStatus(night,1);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                EnableNightMode = false;
                sqLiteHelper.updateDarkmodeStatus(light,1);
            }
        });
        InventoryList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Inventory.InventoryList.class);
            startActivity(intent);
            finish();

        });
        UnvalidatedData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Unvalidated.UnvalidatedData.class);
            startActivity(intent);
            finish();
        });
        PullPsgcData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.PullUpdate.PullPsgcData.class);
            startActivity(intent);
            finish();
        });
        SyncData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Sync.SyncData.class);
            startActivity(intent);
            finish();
        });
        LogsData.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, com.example.cashgrantsmobile.Logs.LogsData.class);
            startActivity(intent);
            finish();
        });
        Logout.setOnClickListener(v -> logout());
    }

    public void logout(){

    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Logout?")
            .setContentText("Are you sure?")
            .setConfirmText("Confirm")
            .showCancelButton(true)
            .setConfirmClickListener(sDialog -> {
                sqLiteHelper.deleteAccess();
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("tokenStatus", "0");
                myEdit.apply();
                clearSharedPref();
                Toasty.success(MainActivity.this, "Logout Successfully", Toast.LENGTH_SHORT, true).show();
                Intent intent = new Intent(MainActivity.this, Activity_Splash_Login.class);
                startActivity(intent);
                finish();
            }).show();
    }

    public void countIdentifier(String validation){
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Cannot proceed!")
                .setContentText("Please pull the " + validation +" Data")
                .setConfirmText("Confirm")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismiss();
                }).show();
    }

    public void darkModeStatus(){
        try {
            SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
            String count = "SELECT count(*) FROM DarkMode";
            Cursor mCursor = db.rawQuery(count, null);
            mCursor.moveToFirst();
            int iCount = mCursor.getInt(0);
            mCursor.close();
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

    @SuppressLint("SetTextI18n")
    public void dashboardDataCount() {
        Cursor user_data = MainActivity.sqLiteHelper.getData("SELECT username FROM Api");
        String username = null;

        while (user_data.moveToNext()) {
            username = user_data.getString(0);
        }

        Cursor listCount = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_validation_details");
        emvList = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_validations");
        psgcList = MainActivity.sqLiteHelper.getData("SELECT id FROM psgc");
        Cursor scanned_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"' AND type='scanned'");
        Cursor error_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"'AND type='error'");
        Cursor logs_total = MainActivity.sqLiteHelper.getData("SELECT id FROM logs WHERE username='"+username+"'");
        Cursor unvalidated_total = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_validations WHERE validated_at ='null'");

        txtInventoryCount.setText(String.valueOf(listCount.getCount()));
        txtPullPsgcDataCount.setText(String.valueOf(psgcList.getCount()));
        txtSyncData.setText("None");
        txtScannedTotal.setText(String.valueOf(scanned_total.getCount()));
        txtErrorTotal.setText(String.valueOf(error_total.getCount()));
        txtLogsTotal.setText(String.valueOf(logs_total.getCount()));
        txtIncompleteTotal.setText("0");
        txtSyncData.setText(String.valueOf(emvList.getCount()));
        txtUnvalidatedCount.setText(String.valueOf(unvalidated_total.getCount()));

        listCount.close();
        emvList.close();
        psgcList.close();
        scanned_total.close();
        error_total.close();
        logs_total.close();
        unvalidated_total.close();
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
        myEdit.apply();
    }
}