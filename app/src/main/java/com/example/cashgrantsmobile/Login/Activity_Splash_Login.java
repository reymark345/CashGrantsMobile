package com.example.cashgrantsmobile.Login;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.google.android.gms.vision.L.TAG;

import android.content.ContentValues;
import android.content.Context;
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
import com.example.cashgrantsmobile.Loading.LoadingBar;
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
    public static String BASE_URL = "https://crg-finance-svr.entdswd.local/cgtracking";
    public static SQLiteHelper sqLiteHelper;
    private LoadingBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        btnLogin = (Button) findViewById(R.id.btnAccess);
        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        loadingBar = new LoadingBar(this);
        createDatabase();
        generateToken();

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String tokenStats = sh.getString("tokenStatus", "");

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
                load_loading_bar();
                NukeSSLCerts.nuke();

                String username, password, device;
                username = String.valueOf(edtUsername.getText());
                password = String.valueOf(edtPassword.getText());
                device = "mobile";

                if(!username.equals("") && !password.equals("")){
                    String url = BASE_URL + "/api/v1/staff/auth/login";
                    // creating a new variable for our request queue
                    RequestQueue queue = Volley.newRequestQueue(Activity_Splash_Login.this);

                    // in this we are calling a post method.
                    StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // on below line we are displaying a success toast message.
                            hide_loading_bar();
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

                                    SharedPreferences sharedPreferencess = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                    SharedPreferences.Editor myEdits = sharedPreferencess.edit();
                                    myEdits.putString("accomplish_by_name", name);
                                    myEdits.commit();


                                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
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
                                hide_loading_bar();
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
                                hide_loading_bar();
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
                    hide_loading_bar();
                    Toasty.error(Activity_Splash_Login.this, "All fields required ", Toast.LENGTH_SHORT, true).show();
                }
            }
        });
    }
    public void load_loading_bar(){
        loadingBar.Show_loading_bar();
    }
    public void hide_loading_bar(){
        loadingBar.Hide_loading_bar();
    }

    public void createDatabase(){
        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS DarkMode(Id INTEGER PRIMARY KEY AUTOINCREMENT, status VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS Api(Id INTEGER PRIMARY KEY AUTOINCREMENT, token VARCHAR, user_id VARCHAR, email VARCHAR, mobile VARCHAR, name VARCHAR, username VARCHAR, accomplish_e_signature BLOB )");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS logs(id INTEGER PRIMARY KEY AUTOINCREMENT, username VARCHAR, type VARCHAR, hh_id VARCHAR, description VARCHAR, created_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS tmp_blob(Id INTEGER PRIMARY KEY AUTOINCREMENT, scanned_e_image BLOB, additional_id_image BLOB,grantee_e_image BLOB, other_card_e_image_1 BLOB,other_card_e_image_2 BLOB,other_card_e_image_3 BLOB,other_card_e_image_4 BLOB,other_card_e_image_5 BLOB)");

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, hh_status VARCHAR, contact_no VARCHAR, contact_no_of VARCHAR, is_grantee VARCHAR, is_minor VARCHAR, relationship_to_grantee VARCHAR, assigned_staff VARCHAR, representative_name VARCHAR, grantee_validation_id INTEGER, pawning_validation_detail_id INTEGER, nma_validation_id INTEGER, card_validation_detail_id INTEGER,emv_validation_id INTEGER, sync_at TIMESTAMP, user_id INTEGER, additional_image BLOB, overall_remarks VARCHAR, created_at TIMESTAMP, updated_at TIMESTAMP, contact_no_of_others TEXT)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS grantee_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, hh_id INTEGER, first_name VARCHAR, last_name VARCHAR, middle_name VARCHAR, ext_name VARCHAR, sex VARCHAR, province_code VARCHAR, municipality_code VARCHAR, barangay_code VARCHAR, hh_set VARCHAR, grantee_image BLOB, created_at TIMESTAMP, other_ext_name TEXT)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS nma_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, amount DECIMAL, date_claimed DATE, reason VARCHAR, remarks VARCHAR,created_at TIMESTAMP, nma_others_reason text, nma_non_emv VARCHAR, nma_card_name VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS card_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, card_number_prefilled VARCHAR, card_number_system_generated VARCHAR, card_number_inputted VARCHAR, card_number_series VARCHAR, distribution_status VARCHAR, release_date DATE, release_by VARCHAR, release_place VARCHAR, card_physically_presented VARCHAR, card_pin_is_attached VARCHAR, reason_not_presented VARCHAR, reason_unclaimed VARCHAR, card_replacement_requests VARCHAR,card_replacement_submitted_details VARCHAR, card_image BLOB, created_at TIMESTAMP, others_reason_not_presented TEXT, others_reason_unclaimed TEXT, distribution_status_record VARCHAR, release_date_record VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS other_card_validations(id INTEGER PRIMARY KEY AUTOINCREMENT,card_holder_name VARCHAR,card_number_system_generated VARCHAR, card_number_inputted VARCHAR, card_number_series VARCHAR, distribution_status VARCHAR, release_date DATE, release_by VARCHAR, release_place VARCHAR, card_physically_presented VARCHAR, card_pin_is_attached VARCHAR, reason_not_presented VARCHAR, reason_unclaimed VARCHAR, card_replacement_requests VARCHAR,card_replacement_request_submitted_details VARCHAR,pawning_remarks VARCHAR, emv_validation_detail_id INTEGER,other_image BLOB,created_at TIMESTAMP, others_reason_not_presented TEXT, others_reason_unclaimed TEXT, distribution_status_record VARCHAR, release_date_record VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS pawning_validation_details(id INTEGER PRIMARY KEY AUTOINCREMENT, lender_name VARCHAR, lender_address VARCHAR, date_pawned DATE, date_retrieved DATE, loan_amount DECIMAL, status VARCHAR, reason VARCHAR, interest DECIMAL, offense_history VARCHAR, offense_date VARCHAR, remarks VARCHAR, staff_intervention VARCHAR,other_details VARCHAR,created_at TIMESTAMP)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS emv_validations(id INTEGER PRIMARY KEY AUTOINCREMENT, first_name VARCHAR, last_name VARCHAR, middle_name VARCHAR, ext_name VARCHAR, hh_id VARCHAR, hh_status VARCHAR, province VARCHAR, municipality VARCHAR, barangay VARCHAR, sex VARCHAR, hh_set_group VARCHAR, nma_amount DECIMAL,grantee_card_number VARCHAR,grantee_distribution_status VARCHAR, grantee_card_release_date VARCHAR, other_card_number_1 VARCHAR,other_card_holder_name_1 VARCHAR,other_card_distribution_status_1 VARCHAR,other_card_release_date_1 VARCHAR, other_card_number_2 VARCHAR,other_card_holder_name_2 VARCHAR,other_card_distribution_status_2 VARCHAR,other_card_release_date_2,other_card_number_3 VARCHAR,other_card_holder_name_3 VARCHAR,other_card_distribution_status_3 VARCHAR,other_card_release_date_3 VARCHAR, other_card_number_4 VARCHAR,other_card_holder_name_4 VARCHAR,other_card_distribution_status_4 VARCHAR,other_card_release_date_4 VARCHAR,other_card_number_5 VARCHAR,other_card_holder_name_5 VARCHAR,other_card_distribution_status_5 VARCHAR,other_card_release_date_5 VARCHAR,upload_history_id VARCHAR,record_counter VARCHAR,created_at TIMESTAMP,updated_at TIMESTAMP,validated_at DATE,nma_non_emv VARCHAR, nma_card_name VARCHAR)");
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS psgc(id INTEGER PRIMARY KEY AUTOINCREMENT, name_new VARCHAR, name_old VARCHAR, code VARCHAR, correspondence_code VARCHAR, geographic_level VARCHAR, created_at TIMESTAMP, updated_at TIMESTAMP)");
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
