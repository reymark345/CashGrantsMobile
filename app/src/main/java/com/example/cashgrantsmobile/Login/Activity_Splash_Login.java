package com.example.cashgrantsmobile.Login;

import static com.google.android.gms.vision.L.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.textfield.TextInputLayout;
import com.vishnusivadas.advanced_httpurlconnection.FetchData;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;


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

        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                BASE_URL  = sh.getString("urlBased", "");

                String username, password, device;
                username = String.valueOf(edtUsername.getText());
                password = String.valueOf(edtPassword.getText());
                device = "mobile";

                if(!username.equals("") && !password.equals("")){
                    String url = "http://172.26.153.104/cgtracking/public/api/v1/staff/auth/login";

                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(Activity_Splash_Login.this);

                    // in this we are calling a post method.
                    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // on below line we are displaying a success toast message.
                            try {
                                JSONObject data = new JSONObject(response);
                                String status = data.getString("status");
                                Toasty.success(Activity_Splash_Login.this, status, Toast.LENGTH_SHORT, true).show();
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
                                JSONArray errors = data.getJSONArray("errors");
                                JSONObject jsonMessage = errors.getJSONObject(0);
                                String message = jsonMessage.getString("message");
                                Toasty.warning(Activity_Splash_Login.this, message, Toast.LENGTH_SHORT, true).show();
                            } catch (JSONException | UnsupportedEncodingException e) {
                                Toasty.warning(Activity_Splash_Login.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
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
                            params.put("username", username);
                            params.put("password", password);
                            params.put("device_name", device);

                            // at last we are
                            // returning our params.
                            return params;
                        }
                    };
                    // below line is to make
                    // a json object request.
                    queue.add(request);
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
}