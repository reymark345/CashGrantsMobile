package com.example.cashgrantsmobile.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import es.dmoral.toasty.Toasty;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;





public class Activity_Splash_Login extends AppCompatActivity {

    Button btnLogin;
    EditText edtUsername, edtPassword;
    String BASE_URL = "http://cgtracking.test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        btnLogin = (Button) findViewById(R.id.btnAccess);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "s " + username + " " + password ,Toast.LENGTH_SHORT).show();
                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                        BASE_URL  = sh.getString("urlBased", "");

                        String username, password;
                        username = String.valueOf(edtUsername.getText());
                        password = String.valueOf(edtPassword.getText());
                        String result ="";

                        if(!username.equals("") && !password.equals("")){
                            //Start ProgressBar first (Set visibility VISIBLE)
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try{

                                        String[] field = new String[2];
                                        field[0] = "username";
                                        field[1] = "password";

                                        //Creating array for data
                                        String[] data = new String[2];
                                        data[0] = username;
                                        data[1] = password;
                                        PutData putData = new PutData(BASE_URL+"/api/v1/staff/granteelists", "POST", field, data);
                                        if (putData.startPut()) {
                                            if (putData.onComplete()) {
                                                String result = putData.getResult();
                                                try
                                                {
                                                    Toasty.error(Activity_Splash_Login.this, "Result "+result, Toast.LENGTH_SHORT, true).show();
//                                                    JSONArray jsonArray = new JSONArray(result);
//                                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
//                                                    JSONArray ar = (JSONArray) jsonObject1.get("result");
//                                                    JSONObject jsonObject2 = ar.getJSONObject(0);

//                                                    message = jsonObject1.optString("message");
//                                                    type = jsonObject1.optString("type");
//                                                    token = jsonObject1.optString("token");
//                                                    id_number = jsonObject2.optString("id_no");
//                                                    fname = jsonObject2.optString("first_name");
//                                                    lname = jsonObject2.optString("last_name");
//                                                    user_id = jsonObject2.optString("user_id");
//                                                    role = jsonObject2.optString("role_name");
//                                                    image = jsonObject2.optString("image");
//                                                    role_id = jsonObject2.optString("user_role");

//                                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
//                                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//
//                                                    myEdit.putString("idNo", id_number);
//                                                    myEdit.putString("firstName", fname);
//                                                    myEdit.putString("lastName", lname);
//                                                    myEdit.putString("userId", user_id);
//                                                    myEdit.putString("roleName", role);
//                                                    myEdit.putString("imageUrl", image);
//                                                    myEdit.putString("roleId", role_id);
//                                                    myEdit.putString("token", token);
//                                                    myEdit.commit();


//                                                    if (type.matches("success")){
//                                                        //                                            Toast.makeText(getApplicationContext(), "test " + id, Toast.LENGTH_SHORT).show();
//                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                                        startActivity(intent);
//                                                        finish();
//                                                    }
//                                                    else{
//                                                        Toasty.error(Activity_Splash_Login.this, message, Toast.LENGTH_SHORT, true).show();
//                                                    }

                                                }
//                                                catch (JSONException e)
                                                catch (Exception e)
                                                {
                                                    Toasty.error(Activity_Splash_Login.this, "ID no./Password is wrong", Toast.LENGTH_SHORT, true).show();
                                                }

                                            }
                                        }

                                    }

                                    catch (Exception e){
                                        Toast.makeText(getApplicationContext(), "Errs " + e, Toast.LENGTH_SHORT).show();
                                    }


                                    //End Write and Read data with URL
                                }
                            });

                        }
                        else {
                            Toasty.error(Activity_Splash_Login.this, "All fields required ", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });




//                Intent intent = new Intent(Activity_Splash_Login.this, MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }
}