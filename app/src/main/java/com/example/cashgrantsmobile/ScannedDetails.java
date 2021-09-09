package com.example.cashgrantsmobile;

import static android.content.ContentValues.TAG;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ScannedDetails extends AppCompatActivity {

    EditText edtCashCard, edtHhnumber, edtSeriesno;
    ImageView mPreview4PsId, mPreviewCashCard;
    Button btnSubmit, btnrescanCashCard, btnrescanBeneId;

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);

        //database connection
//        sqLiteHelper = new SQLiteHelper(this, "CgTracking.sqlite", null, 1);
//        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS CgList(Id INTEGER PRIMARY KEY AUTOINCREMENT, cash_card VARCHAR, hh_number VARCHAR,series_number VARCHAR, cc_image BLOB , id_image BLOB)");


        //EdiText
        edtCashCard = findViewById(R.id.Idresult);
        edtHhnumber = (EditText) findViewById(R.id.hhNo);
        edtSeriesno = (EditText) findViewById(R.id.seriesNo);

        //ImageView
        mPreview4PsId = findViewById(R.id.PsID);
        mPreviewCashCard = findViewById(R.id.ScannedImage);

        //buttons

        btnrescanCashCard = (Button) findViewById(R.id.rescanCashCard);
        btnrescanBeneId = (Button) findViewById(R.id.rescanBeneId);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //round ImageView
        mPreview4PsId.setClipToOutline(true);
        mPreviewCashCard.setClipToOutline(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String resultUri = extras.getString("CashCardImage");

//            Bitmap bitmap = (Bitmap) intent.getParcelableExtra("CashCardImage");
            String value = extras.getString("cashCardNumber");

//            mPreviewCashcard.setImageBitmap(bitmap);
            edtCashCard.setText(value);

            //uri
            Uri myUri = Uri.parse(resultUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),myUri);
                mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 187, 250, false));
            } catch (IOException e) {
                e.printStackTrace();
            }
//            mPreviewCashcard.setImageURI(myUri);
        }
//        if (ContextCompat.checkSelfPermission(ScannedDetails.this, Manifest.permission.CAMERA)
//        != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(ScannedDetails.this, new String[]{Manifest.permission.CAMERA}, 101);
//        }

        btnrescanBeneId.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);
            }
        });
        btnrescanCashCard.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanCashCard action = new ScanCashCard();
                action.pickCamera();

//                Intent intent = new Intent(ScannedDetails.this, InventoryList.class);
//                startActivity(intent);
            }
        });

        // save to database
        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    sqLiteHelper.insertData(
                            edtCashCard.getText().toString().trim(),
                            edtHhnumber.getText().toString().trim(),
                            edtSeriesno.getText().toString().trim(),
                            imageViewToByte(mPreviewCashCard),
                            imageViewToByte(mPreview4PsId)
                    );
                    Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
                    edtHhnumber.setText("");
                    edtSeriesno.setText("");
                    Intent intent = new Intent(ScannedDetails.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
                catch (Exception e ){
                    Toast.makeText(getApplicationContext(), "error "+e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }



//                String Cardresult = CardResult.getText().toString();
//                String household = txtHhnumber.getText().toString();
//                String seriesno = txtSeriesno.getText().toString();
//                if (Cardresult.length() != 23 ){
//                    Toast.makeText(ScannedDetails.this, "Cash card number was not in length" + CardResult.length(), Toast.LENGTH_SHORT).show();
//                }
//                else if (household.matches("")){
//                    Toast.makeText(ScannedDetails.this," Household number is empty ", Toast.LENGTH_SHORT).show();
//                }
//                else if (seriesno.matches("")){
//                    Toast.makeText(ScannedDetails.this," Series number is empty ", Toast.LENGTH_SHORT).show();
//                }
//                else if (Cardresult.matches("[0-9]+")){
//                    Toast.makeText(ScannedDetails.this," Success numbers all ", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    Toast.makeText(ScannedDetails.this, "Error" + Cardresult, Toast.LENGTH_SHORT).show();
//
//                }
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mPreview4PsId.setImageBitmap(bitmap);
            btnrescanBeneId.setText("RE-SCAN");
        }
    }

}