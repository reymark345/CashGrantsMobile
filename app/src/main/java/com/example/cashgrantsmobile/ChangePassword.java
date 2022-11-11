package com.example.cashgrantsmobile;

import static com.example.cashgrantsmobile.Login.Activity_Splash_Login.BASE_URL;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cashgrantsmobile.Login.Activity_Splash_Login;
import com.example.cashgrantsmobile.Scanner.ScanCashCard;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class ChangePassword extends AppCompatActivity {

    EditText edtCurrent,edtNew,edtConfirm;
    TextView txtCompleteName;
    Button btnUpdatePass;

    TextInputLayout tilCurrent,tilNew,tilConfirm;
    String blankMessage = "Please don't leave a blank";
    String current,newP,confirm, type,message;
    Integer isValidationError = 0;
    String user_id;
    String password_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        edtCurrent = findViewById(R.id.edt_currentPassword);
        edtNew = findViewById(R.id.edt_newPass);
        edtConfirm = findViewById(R.id.edt_confirmPass);

        btnUpdatePass = findViewById(R.id.updateChangePassword);

        tilCurrent = findViewById(R.id.cc_currentPass);
        tilNew = findViewById(R.id.cc_newPassword);
        tilConfirm = findViewById(R.id.cc_confirmPassword);
        txtCompleteName = findViewById(R.id.txtCompleteName);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String accomplish_name = sh.getString("accomplish_by_name", "");
        user_id = sh.getString("user_id_pass", "");
        password_text = sh.getString("password_txt", "");


        txtCompleteName.setText(accomplish_name);
        isValidationError = 0;

        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValidationError = 0;
                newP = edtNew.getText().toString();
                confirm = edtConfirm.getText().toString();


                tilNew.setError(blankMessage);
                tilConfirm.setError(blankMessage);

                String currentPass = edtCurrent.getText().toString();
                String newPass = edtNew.getText().toString();
                String confirmPass = edtConfirm.getText().toString();

                if (currentPass.matches("")){
                    tilCurrent.setError(blankMessage);
                    isValidationError++;
                }
                else if (!currentPass.matches(password_text)){
                    tilCurrent.setError("Current Password does not match");
                    isValidationError++;
                }
                else {
                    tilCurrent.setError(null);
                }
                if(newPass.matches("")){
                    tilNew.setError(blankMessage);
                    isValidationError++;
                }
                else if(newPass.length()<8){
                    tilNew.setError("Password must be at least 8 characters.");
                    isValidationError++;
                }
                else {
                    tilNew.setError(null);
                }

                if (confirmPass.matches("")){
                    tilConfirm.setError(blankMessage);
                    isValidationError++;
                }
                else if (!newPass.matches(confirmPass)){
                    tilConfirm.setError("Password does not match");
                    isValidationError++;
                }
                else {
                    tilConfirm.setError(null);
                }

                if(isValidationError>0){
                    Toasty.warning(getApplicationContext(), "Please fill-in all required fields!", Toast.LENGTH_SHORT).show();
                }
                else{
                    changePassword(currentPass,newPass,confirmPass,user_id);
                }
            }
        });

    }
    public void changePassword(String currentPass, String newPass, String confirmPass, String user_id ){
//        Toasty.warning(getApplicationContext(), currentPass + " "+ newPass + " "+ confirmPass + " "+ user_id, Toast.LENGTH_SHORT).show();

        new SweetAlertDialog(ChangePassword.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Change password")
                .setConfirmText("Confirm")
                .showCancelButton(true)
                .setConfirmClickListener(sDialog -> {

                    String url = BASE_URL + "/api/v1/staff/auth/change_password";
                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(ChangePassword.this);

                    // in this we are calling a post method.
                    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // on below line we are displaying a success toast message.
                            try {
                                JSONObject data = new JSONObject(response);
                                String status = data.getString("status");

                                if (status.matches("success")){

                                    SharedPreferences sharedPreferencess = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                    SharedPreferences.Editor myEdits = sharedPreferencess.edit();

                                    myEdits.putString("password_txt", newPass);
                                    myEdits.commit();

                                    Toasty.success(ChangePassword.this, "Successfully save!", Toast.LENGTH_SHORT, true).show();
                                    Intent intent = new Intent(ChangePassword.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toasty.error(ChangePassword.this, "Please contact Administrator", Toast.LENGTH_SHORT, true).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // method to handle errors.
                            try {
                                String responseBody = new String(error.networkResponse.data, "utf-8");
                                JSONObject data = new JSONObject(responseBody);
                                JSONArray errors = data.getJSONArray("status");
//                                JSONObject jsonMessage = errors.getJSONObject(0);
//                                String message = jsonMessage.getString("description");
//                                Toasty.warning(ChangePassword.this, message, Toast.LENGTH_SHORT, true).show();
                            } catch (JSONException | UnsupportedEncodingException e) {
                                Toasty.warning(ChangePassword.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                            }
                            catch (Exception e) {

                                Toasty.error(ChangePassword.this, "Network not found." + e, Toast.LENGTH_SHORT, true).show();
                            }
                        }

                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // below line we are creating a map for
                            // storing our values in key and value pair.
                            Map<String, String> params = new HashMap<String, String>();

                            // on below line we are passing our key
                            // and value pair to our parameters.

                            params.put("current_password", currentPass);
                            params.put("new_password", newPass);
                            params.put("new_password_confirmation", confirmPass);
                            params.put("user_id", user_id);

                            // at last we are
                            // returning our params.
                            return params;
                        }
                    };
                    // below line is to make
                    // a json object request.
                    queue.add(request);

                }).show();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
           super.onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}