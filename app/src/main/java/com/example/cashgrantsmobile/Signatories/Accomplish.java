package com.example.cashgrantsmobile.Signatories;

import static android.content.ContentValues.TAG;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.example.cashgrantsmobile.Scanner.ScanCashCard.imageViewToByte;
import static com.example.cashgrantsmobile.Scanner.ScannedDetails.id;
import static com.example.cashgrantsmobile.Scanner.ScannedDetails.signature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Scanner.ScannedDetails;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

public class Accomplish extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accomplished_signature);

        SignaturePad signaturePad = findViewById(R.id.signature_pad);
        Button buttonSignature = findViewById(R.id.btnSubmit);
        Button buttonClear = findViewById(R.id.btnClear);
        ImageView imageViewSignature = findViewById(R.id.imageSignature);
        imageViewSignature.setVisibility(View.INVISIBLE);

        Intent in = getIntent();
        int signatureCondition = in.getIntExtra("conditionForSignature", 0);
        int edtAccomplish = in.getIntExtra("edtAccomplish", 0);
        String edtAccomplished = in.getStringExtra("edtAccomplish");
        String edtInformant = in.getStringExtra("edtInformant");
        String edtAttested = in.getStringExtra("edtAttest");



        Log.v(TAG,"walay value?" + id + " "+signatureCondition);

        buttonClear.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        buttonSignature.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Bitmap bitmap = signaturePad.getSignatureBitmap();
                    imageViewSignature.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
                    byte[] byteArray = stream.toByteArray();


                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("signatureAccomplishment", "true");
                    myEdit.commit();

                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                    String identifier = sh.getString("identifier", "");

                    Intent intent = new Intent(getApplicationContext(), ScannedDetails.class);

                    if (signatureCondition ==0 && identifier.matches("false")){
                        int currentId = sh.getInt("maxIdScanned", 0);

                        sqLiteHelper.updateAccomplishSignature(
                                currentId,
                                edtAccomplished,
                                edtInformant,
                                edtAttested,
                                imageViewToByte(imageViewSignature)
                        );
                        intent.putExtra("detailScan", 0);
                        Log.v(TAG,"samplee zerro" +  edtAttested);
                    }
                    else {
                        sqLiteHelper.updateAccomplishSignature(
                                (signatureCondition+1),
                                edtAccomplished,
                                edtInformant,
                                edtAttested,
                                imageViewToByte(imageViewSignature)
                        );
                        intent.putExtra("detailScan", signatureCondition);
                        Log.v(TAG,"samplee 1" + edtAttested);

                    }



                    startActivity(intent);

//
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Error" + e, Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"Error ni " + e);

                }

//                Bitmap bitmap = signaturePad.getSignatureBitmap();
//                imageViewSignature.setImageBitmap(bitmap);
//                signaturePad.clear();
            }
        });

    }
}