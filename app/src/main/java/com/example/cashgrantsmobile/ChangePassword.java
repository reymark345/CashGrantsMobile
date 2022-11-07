package com.example.cashgrantsmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashgrantsmobile.Scanner.ScanCashCard;
import com.google.android.material.textfield.TextInputLayout;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ChangePassword extends AppCompatActivity {

    EditText edtCurrent,edtNew,edtConfirm;
    TextView txtCompleteName;
    Button btnUpdatePass;

    TextInputLayout tilCurrent,tilNew,tilConfirm;
    String blankMessage = "Please don't leave a blank";
    String current,newP,confirm, type,message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        edtCurrent = findViewById(R.id.edt_currentPassword);
        edtNew = findViewById(R.id.edt_newPassword);
        edtConfirm = findViewById(R.id.edt_confirmPass);
        btnUpdatePass = findViewById(R.id.updateChangePassword);
        tilCurrent = findViewById(R.id.cc_CurrentPass);
        tilNew = findViewById(R.id.cc_newPass);
        tilConfirm = findViewById(R.id.cc_confirmPassword);
        txtCompleteName = findViewById(R.id.txtCompleteName);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String accomplish_name = sh.getString("accomplish_by_name", "");
        txtCompleteName.setText(accomplish_name);

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = edtCurrent.getText().toString();
                newP = edtNew.getText().toString();
                confirm = edtConfirm.getText().toString();
                if (!current.matches("") && !newP.matches("") && !confirm.matches("")){

                    String md5Current = current;
                    String md5new = newP;
                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    String token = sh.getString("token", "");
                    String userId = sh.getString("userId", "");

                    if (md5Current.matches(token)){

                        if (newP.matches(confirm)){
                            if(!current.matches(newP)){
                                //id and password md5
                                ///users/update/change_pass
//                                updatePassword(userId,md5new);
                            }
                            else{
                                new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("You entered an Old Password")
                                        .setContentText("Invalid")
                                        .showCancelButton(true)
                                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                            }
                                        }).show();
                            }

                        }
                        else{
                            new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Mismatched Password!")
                                    .setContentText("Password not match")
                                    .showCancelButton(true)
                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                        }
                                    }).show();
                        }
                    }
                    else {
                        new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Wrong Password!")
                                .setContentText("Incorrect Old Password")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                    }
                                }).show();

                    }
                }
                else {
                    new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Invalid")
                            .setContentText("Please complete the details")
                            .showCancelButton(true)
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                }
                            }).show();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(ChangePassword.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}