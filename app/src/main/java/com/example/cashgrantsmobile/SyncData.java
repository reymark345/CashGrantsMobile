package com.example.cashgrantsmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SyncData extends AppCompatActivity {
    private Toolbar mToolbars;
    private Button btnSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        btnSync = findViewById(R.id.btnSync);
        mToolbars = findViewById(R.id.mainToolbar);
        setSupportActionBar(mToolbars);
        getSupportActionBar().setTitle("Sync Data");

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(SyncData.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Please confirm")
                        .setConfirmText("Sync Now")
                        .showCancelButton(false)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Toast.makeText(getApplicationContext(),"Syncing please wait", Toast.LENGTH_SHORT).show();
                            }
                        }).show();


            }
        });

    }
}