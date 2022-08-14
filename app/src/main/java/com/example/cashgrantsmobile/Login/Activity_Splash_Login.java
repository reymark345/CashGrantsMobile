package com.example.cashgrantsmobile.Login;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.google.android.gms.vision.L.TAG;

import android.content.ContentValues;
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
import com.example.cashgrantsmobile.Database.SQLiteHelper;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import es.dmoral.toasty.Toasty;
import com.example.cashgrantsmobile.Database.SQLiteHelper;


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

import com.example.cashgrantsmobile.Scanner.ScanCashCard;
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
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class Activity_Splash_Login extends AppCompatActivity {

    Button btnLogin;
    EditText edtUsername, edtPassword;
    String BASE_URL = "http://192.168.1.7/cgtracking/public";
    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        btnLogin = (Button) findViewById(R.id.btnAccess);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        createDatabase();
        generateToken();

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String tokenStats = sh.getString("tokenStatus", "");

        Log.v(ContentValues.TAG,"token " +tokenStats);

        if(tokenStats.matches("1")){
            Intent intent = new Intent(Activity_Splash_Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

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
                NukeSSLCerts.nuke();

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                BASE_URL  = sh.getString("urlBased", "");

                String username, password, device;
                username = String.valueOf(edtUsername.getText());
                password = String.valueOf(edtPassword.getText());
                device = "mobile";

                if(!username.equals("") && !password.equals("")){
                    String url = "http://192.168.1.12/cgtracking/public/api/v1/staff/auth/login";

//                    String url = "https://crg-finance-svr.entdswd.local/cgtracking/api/v1/staff/auth/login";
                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(Activity_Splash_Login.this);

                    // in this we are calling a post method.
                    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // on below line we are displaying a success toast message.
                            try {
                                JSONObject data = new JSONObject(response);
                                JSONObject dataObject = data.getJSONObject("data");

                                String status = data.getString("status");
                                String token = data.getString("token");
                                String user_id = dataObject.getString("id");
                                String email = dataObject.getString("email");
                                String mobile = dataObject.getString("mobile");
                                String name = dataObject.getString("name");
                                String username = dataObject.getString("username");

                                if (status.matches("success")){

                                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                                    String tokenStats = sh.getString("tokenStatus", "");

                                    if (tokenStats.matches("1")){
                                        sqLiteHelper.updateUser(token,user_id,email,mobile,name,username);
                                        Toasty.success(Activity_Splash_Login.this, "Login Successfully", Toast.LENGTH_SHORT, true).show();
                                    }
                                    else{
                                        sqLiteHelper.insertDefaultUser(token,user_id,email,mobile,name,username);
                                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                        myEdit.putString("tokenStatus", "1");
                                        myEdit.commit();

                                        Toasty.success(Activity_Splash_Login.this, "Login Successfully", Toast.LENGTH_SHORT, true).show();

                                    }
                                    Intent intent = new Intent(Activity_Splash_Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toasty.success(Activity_Splash_Login.this, "Please contact Administrator", Toast.LENGTH_SHORT, true).show();
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
                                JSONArray errors = data.getJSONArray("errors");
                                JSONObject jsonMessage = errors.getJSONObject(0);
                                String message = jsonMessage.getString("message");
                                Toasty.warning(Activity_Splash_Login.this, message, Toast.LENGTH_SHORT, true).show();
                            } catch (JSONException | UnsupportedEncodingException e) {
                                Toasty.warning(Activity_Splash_Login.this, (CharSequence) e, Toast.LENGTH_SHORT, true).show();
                            }
                            catch (Exception e) {
//                                Toasty.error(Activity_Splash_Login.this, e.toString(), Toast.LENGTH_SHORT, true).show();
                                Toasty.error(Activity_Splash_Login.this, "Network not found.", Toast.LENGTH_SHORT, true).show();
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

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card_actual_no VARCHAR, accomplish_by VARCHAR, informant VARCHAR, cc_image BLOB , id_image BLOB, cash_card_scanned_no VARCHAR , card_scanning_status VARCHAR, date_insert DATETIME DEFAULT CURRENT_TIMESTAMP, accomplish_img BLOB , informant_image BLOB, attested_img BLOB, attested VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS Api(Id INTEGER PRIMARY KEY AUTOINCREMENT, token VARCHAR, user_id VARCHAR, email VARCHAR, mobile VARCHAR, name VARCHAR, username VARCHAR )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, upload_history_id INTEGER, created_at TIMESTAMP, updated_at TIMESTAMP, validated_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_database_monitoring_details(id INTEGER PRIMARY KEY AUTOINCREMENT, full_name VARCHAR, hh_id VARCHAR, client_status VARCHAR, address VARCHAR, sex VARCHAR, hh_set_group VARCHAR, assigned_staff VARCHAR, minor_grantee VARCHAR, contact INTEGER, current_grantee_card_release_date DATE, current_grantee_card_release_place VARCHAR, current_grantee_card_release_by VARCHAR, current_grantee_is_available VARCHAR, current_grantee_reason VARCHAR, current_grantee_card_number VARCHAR, other_card_number_1 VARCHAR, other_card_holder_name_1 VARCHAR, other_card_number_2 VARCHAR, other_card_holder_name_2 VARCHAR, other_card_number_3 VARCHAR, other_card_holder_name_3 VARCHAR, other_card_is_available VARCHAR, other_card_reason VARCHAR, nma_amount DECIMAL, nma_date_claimed DATE, nma_reason VARCHAR, nma_remarks VARCHAR, pawn_name_of_lender VARCHAR, pawn_date DATE, pawn_retrieved_date DATE, pawn_status VARCHAR, pawn_reason VARCHAR, pawn_offense_history VARCHAR, pawn_offense_date DATE, pawn_remarks VARCHAR, pawn_intervention_staff VARCHAR, pawn_other_details VARCHAR, accomplish_e_signature BLOB, informant_e_signature BLOB, attested_by_e_signature BLOB, attested_by_full_name VARCHAR, other_card_number_series_1 VARCHAR, other_card_number_series_2 VARCHAR, other_card_number_series_3 VARCHAR, emv_database_monitoring_id INTEGER, current_grantee_card_number_series VARCHAR, user_id INTEGER, sync_at TIMESTAMP, created_at TIMESTAMP, updated_at TIMESTAMP)");
    }
    public void generateToken(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if (!sharedPreferences.contains("tokenStatus")) {
            myEdit.putString("tokenStatus", "0");
            myEdit.commit();
        }
    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

}
