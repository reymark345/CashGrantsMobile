package com.example.cashgrantsmobile.Splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;


public class Activity_Splash_Login extends AppCompatActivity {

    Button BtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_login);
        BtnLogin = (Button) findViewById(R.id.btnAccess);


        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Splash_Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}