package com.example.cashgrantsmobile.Scanner;

import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.example.cashgrantsmobile.Scanner.ScanCashCard.imageViewToByte;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashgrantsmobile.Inventory.InventoryList;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Signatories.Accomplish;
import com.example.cashgrantsmobile.Signatories.Attested;
import com.example.cashgrantsmobile.Signatories.Informant;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ScannedDetails extends AppCompatActivity {

    EditText edtCashCard, edtAccomplishBy, edtInformant, edtAttested, edtSeriesNumber;
    ImageView mPreviewGrantee, mPreviewCashCard,mAccomplished,mInformant,mAttested;
    Button btnSubmit, btnRescanCashCard, btnRescanBeneId, btn_Accomplished, btn_informant, btn_attested;
    TextInputLayout tilCashCard, tilHousehold, tilSeriesNo, tilAttested, tilSeriesNumber;
    public static boolean scanned;
    public static boolean signature;
    private int prevCount = 0;
    public static int id = 0, max_id=0;
    private String cashCardNumber;
    String blankMessage = "Please fill this blank";
    Intent intent;
    Uri image_uri;
    ImageView mPreviewIv;
    byte[] accomplish = new byte[0];
    byte[] informant = new byte[0];
    byte[] granteeImage = null;
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 4 ||currCount == 9 || currCount == 14 || currCount == 19;
    }
    int dataUp =0;
    int detailScan=0;
    int grante_no = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);

        //EdiText
        edtCashCard = findViewById(R.id.Idresult);
        edtAccomplishBy = (EditText) findViewById(R.id.edtAccomplish);
        edtInformant = (EditText) findViewById(R.id.edtInformant);
        edtAttested = (EditText) findViewById(R.id.edt_attested);
        edtSeriesNumber = findViewById(R.id.edt_series_no);

        //ImageView
        mPreviewGrantee = findViewById(R.id.PsID);
        mPreviewCashCard = findViewById(R.id.ScannedImage);
        mAccomplished = findViewById(R.id.imgAccomplished);
        mInformant = findViewById(R.id.imgInformant);
        mAttested = findViewById(R.id.imgAttested);



        mPreviewIv = findViewById(R.id.imgUri);
        mPreviewIv .setVisibility(View.INVISIBLE);

        //buttons

        btnRescanCashCard = (Button) findViewById(R.id.rescanCashCard);
        btnRescanBeneId = (Button) findViewById(R.id.rescanBeneId);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btn_Accomplished = (Button) findViewById(R.id.btn_Accomplished);
        btn_informant = (Button) findViewById(R.id.btn_informant);
        btn_attested = (Button) findViewById(R.id.btn_attested);



        //round ImageView
        mPreviewGrantee.setClipToOutline(true);
        mPreviewCashCard.setClipToOutline(true);

        //layoutMaterial
        tilCashCard = findViewById(R.id.til_cashCard);
        tilHousehold = findViewById(R.id.til_household);
        tilSeriesNo = findViewById(R.id.til_seriesno);
        tilAttested = findViewById(R.id.til_attested);
        tilSeriesNumber = findViewById(R.id.til_series_number);


        signature =false;
        CashCardOnChange();
        HouseholdOnChanged();
        SeriesOnChanged();
        getMaxID();
        getData();
        signatoriesValidation();


        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String accomplish_shared = sh.getString("accomplish_by_name", "");

        edtAccomplishBy.setText(accomplish_shared);



        btnRescanBeneId.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);
            }
        });
        btnRescanCashCard.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickCamera();
            }
        });

        // save to database
        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanned==true){
                    submit();
                }
                else{
                    new SweetAlertDialog(ScannedDetails.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Confirm update")
                            .setContentText("Are you sure?")
                            .setConfirmText("Confirm")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    submit();
                                }
                            }).show();
                }
            }
        });

        //signatories
        btn_Accomplished.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                int signature = sh.getInt("updateMoriah", 0);
                Intent in = getIntent();
                dataUp = in.getIntExtra("updateData", 0);
                Intent intent = new Intent(getApplicationContext(), Accomplish.class);
                intent.putExtra("conditionForSignature", signature);
                intent.putExtra("edtCashCard", edtCashCard.getText().toString());
                intent.putExtra("edtAccomplish", edtAccomplishBy.getText().toString());
                intent.putExtra("edtInformant", edtInformant.getText().toString());
                intent.putExtra("edtAttest", edtAttested.getText().toString());
                intent.putExtra("edtSeries", edtSeriesNumber.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        btn_informant.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                int moriah = sh.getInt("updateMoriah", 0);
                Intent in = getIntent();
                dataUp = in.getIntExtra("updateData", 0);
                Intent intent = new Intent(getApplicationContext(), Informant.class);
                intent.putExtra("conditionForSignature", moriah);
                intent.putExtra("edtCashCard", edtCashCard.getText().toString());
                intent.putExtra("edtAccomplish", edtAccomplishBy.getText().toString());
                intent.putExtra("edtInformant", edtInformant.getText().toString());
                intent.putExtra("edtAttest", edtAttested.getText().toString());
                intent.putExtra("edtSeries", edtSeriesNumber.getText().toString());
                startActivity(intent);
                finish();
            }
        });
        btn_attested.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                int moriah = sh.getInt("updateMoriah", 0);
                Intent in = getIntent();
                dataUp = in.getIntExtra("updateData", 0);
                Intent intent = new Intent(getApplicationContext(), Attested.class);
                intent.putExtra("conditionForSignature", moriah);
                intent.putExtra("edtCashCard", edtCashCard.getText().toString());
                intent.putExtra("edtAccomplish", edtAccomplishBy.getText().toString());
                intent.putExtra("edtInformant", edtInformant.getText().toString());
                intent.putExtra("edtAttest", edtAttested.getText().toString());
                intent.putExtra("edtSeries", edtSeriesNumber.getText().toString());
                startActivity(intent);
                finish();
            }
        });

    }

    public void getSignatories(){
        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                byte[] byteArray = extras.getByteArray("imageAccomplish");
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mAccomplished.setImageBitmap(Bitmap.createScaledBitmap(bmp, 374, 500, false));
//                image.setImageBitmap(bmp);

//                Toast.makeText(this, "111111111 ", Toast.LENGTH_SHORT).show();
            }
            else {
//                Toast.makeText(this, "22222222", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e){
//            Toast.makeText(this, "Error kayah ni" + e, Toast.LENGTH_SHORT).show();

        }
    }

    public void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "CashCardRes-can");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mPreviewGrantee.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);

                if (grante_no !=0){
                    sqLiteHelper.updateGranteeEmv(grante_no,imageViewToByte(mPreviewGrantee));
                }
                else{
                    sqLiteHelper.updateGranteeEmv(id,imageViewToByte(mPreviewGrantee)
                    );
                }
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("granteeBtn", "true");
                myEdit.commit();
                btnRescanBeneId.setText("RE-SCAN");

            }catch (Exception e){
                Log.v(TAG,"bye" + e);

            }

        }
        else{
            if (resultCode == RESULT_OK){
                if (requestCode == 102){

                    CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode ==RESULT_OK){
                    Uri resultUri = result.getUri();
                    resultUri.getPath();
                    mPreviewIv.setImageURI(resultUri);
                    BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                    if(!recognizer.isOperational()){
                        Toast.makeText(this,"Error this",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                        SparseArray<TextBlock> items = recognizer.detect(frame);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i<items.size(); i++){
                            TextBlock myItem = items.valueAt(i);
                            sb.append(myItem.getValue());
                            sb.append("\n");
                        }

                        String sTextFromET=sb.toString().replaceAll("\\s+", "");
                        sTextFromET = sTextFromET.replace("a", "8");
                        sTextFromET = sTextFromET.replace("A", "8");
                        sTextFromET = sTextFromET.replace("B", "6");
                        sTextFromET = sTextFromET.replace("b", "6");
                        sTextFromET = sTextFromET.replace("D", "0");
                        sTextFromET = sTextFromET.replace("e", "8");
                        sTextFromET = sTextFromET.replace("E", "8");
                        sTextFromET = sTextFromET.replace("L", "6");
                        sTextFromET = sTextFromET.replace("S", "5");
                        sTextFromET = sTextFromET.replace("G", "6");
                        sTextFromET = sTextFromET.replace("%", "6");
                        sTextFromET = sTextFromET.replace("&", "6");
                        sTextFromET = sTextFromET.replace("?", "7");
                        sTextFromET = sTextFromET.replace("l", "1");
                        sTextFromET = sTextFromET.replace("+", "7");
                        sTextFromET = sTextFromET.replace("}", "7");
                        sTextFromET = sTextFromET.replace("O", "0");
                        sTextFromET = sTextFromET.replaceAll("....", "$0 ");

                        //save temp database
                        image_uri = Uri.parse(image_uri.toString());
                        try {
                            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                            mPreviewIv.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
//                            sqLiteHelper.updateScannedCashCard(sTextFromET,imageViewToByte(mPreviewIv));
                            sqLiteHelper.updateScannedCashCard_emv(sTextFromET,imageViewToByte(mPreviewIv));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //---

                        ScannedDetails.scanned = true;
                        Intent i = new Intent(ScannedDetails.this, ScannedDetails.class);
                        if (sTextFromET.length() >23){
                            String limitString = sTextFromET.substring(0,23);
                            edtCashCard.setText(limitString);
                        }
                        else{
                            edtCashCard.setText(sTextFromET);
                        }
                        try {
                            Bitmap bitmaps = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                            mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bitmaps, 374, 500, false));

                            if (grante_no !=0){sqLiteHelper.updateCashCardEmv(grante_no,imageViewToByte(mPreviewCashCard));}
                            else{sqLiteHelper.updateCashCardEmv(id,imageViewToByte(mPreviewCashCard)); }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    Exception error = result.getError();
                    Toast.makeText(this,""+error, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void submit(){
        Log.v(TAG,"here");
        String CardResult = edtCashCard.getText().toString();
        String household = edtAccomplishBy.getText().toString();
        String informant_ = edtInformant.getText().toString();
        String attested = edtAttested.getText().toString();
        String series_number = edtSeriesNumber.getText().toString();
        int length = CardResult.length();

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String granteeBtnStatus = sh.getString("granteeBtn", "");


        if (CardResult.matches("[0-9 ]+") && !household.matches("") && !informant_.matches("") && !series_number.matches("") && length==23 && accomplish!=null && accomplish!=null && informant!=null && informant!=null && (granteeImage!=null|| granteeBtnStatus.matches("true"))){
            try{

                if ( scanned ==true){
                    sqLiteHelper.updateSubmitData_emv(
                            edtCashCard.getText().toString().trim(),
                            edtAccomplishBy.getText().toString().trim(),
                            edtInformant.getText().toString().trim(),
                            imageViewToByte(mPreviewCashCard),
                            imageViewToByte(mPreviewGrantee),
                            edtAttested.getText().toString().trim(),
                            edtSeriesNumber.getText().toString().trim()
                    );

                    String hh_no_1 = sh.getString("hh_id", "");
                    sqLiteHelper.update_emv_monitoring(hh_no_1);
                    clearSharedPref();
                    String value="Added Successfully";
                    intent= new Intent(ScannedDetails.this, ScanCashCard.class);
                    intent.putExtra("toast",value);
                }
                else{
                    Log.v(TAG,"submitted successfully last");
                    sqLiteHelper.updateInventoryList_emv(
                            edtCashCard.getText().toString().trim(),
                            edtAccomplishBy.getText().toString().trim(),
                            edtInformant.getText().toString().trim(),
                            imageViewToByte(mPreviewCashCard),
                            imageViewToByte(mPreviewGrantee),id,
                            edtAttested.getText().toString().trim(),
                            edtSeriesNumber.getText().toString().trim()
                    );
                    intent = new Intent(ScannedDetails.this, InventoryList.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            catch (Exception e ){
                Toasty.warning(this,"Don't leave the required fields/image", Toasty.LENGTH_SHORT).show();
                Log.v(TAG,"error submit" + e);
                e.printStackTrace();
            }
        }

        else if (!household.matches("") && !informant_.matches("") && !attested.matches("") && !series_number.matches("") ){
            tilHousehold.setError(null);
            tilSeriesNo.setError(null);
            tilAttested.setError(null);
            tilSeriesNumber.setError(null);
        }

        if (informant_.matches("") || household.matches("") || series_number.matches("")){
            Toasty.warning(this,"Don't leave a blank", Toasty.LENGTH_SHORT).show();
        }

        if (!informant_.matches("")){
            tilSeriesNo.setError(null);
        }
        if (informant_.matches("")){
            tilSeriesNo.setError(blankMessage);
        }
        if (household.matches("")){
            tilHousehold.setError(blankMessage);
        }
        if (!household.matches("")){
            tilAttested.setError(null);
        }

        if (!series_number.matches("")){
            tilSeriesNumber.setError(null);
        }
        if (series_number.matches("")){
            tilSeriesNumber.setError(blankMessage);
        }

        if (!CardResult.matches("[0-9 ]+")){
            tilCashCard.setError("Invalid format");
        }
        if (length!=23 ){
            tilCashCard.setError("Not enough length");
        }

        if (granteeImage ==null && granteeBtnStatus.matches("false")){
            Toasty.warning(this,"Please Scan Grantee", Toasty.LENGTH_SHORT).show();
        }

        if(accomplish==null){
            Toasty.warning(this,"Signature is required for accomplished by", Toasty.LENGTH_SHORT).show();
        }

        if(informant==null){
            Toasty.warning(this,"Signature is required for Informant", Toasty.LENGTH_SHORT).show();
        }

        if (!CardResult.matches("[0-9 ]+")){
            tilCashCard.setError("Cash Card contains a character");
        }
    }

    public void CashCardOnChange(){
        edtCashCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() !=23){
                    tilCashCard.setError("Not enough length");
                }
                else{
                    tilCashCard.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String field = s.toString();
                int currCount = field.length();

                if (shouldIncrementOrDecrement(currCount, true)){
                    appendOrStrip(field, true);
                } else if (shouldIncrementOrDecrement(currCount, false)) {
                    appendOrStrip(field, false);
                }
                prevCount = edtCashCard.getText().toString().length();
            }
        });
    }
    public void HouseholdOnChanged(){
        edtAccomplishBy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilHousehold.setError(blankMessage);
                }
                else{
                    tilHousehold.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void SeriesOnChanged(){
        edtInformant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilSeriesNo.setError(blankMessage);
                }
                else{
                    tilSeriesNo.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean shouldIncrementOrDecrement(int currCount, boolean shouldIncrement) {
        if (shouldIncrement) {
            return prevCount <= currCount && isAtSpaceDelimiter(currCount);
        } else {
            return prevCount > currCount && isAtSpaceDelimiter(currCount);
        }
    }
    private void appendOrStrip(String field, boolean shouldAppend) {
        StringBuilder sb = new StringBuilder(field);
        if (shouldAppend) {
            sb.append(" ");
        } else {
            sb.setLength(sb.length() - 1);
        }
        edtCashCard.setText(sb.toString());
        edtCashCard.setSelection(sb.length());
    }


    //update and get data
    public void getData(){
        Intent in = getIntent();
        detailScan = in.getIntExtra("detailScan", 0);
        Log.v(TAG,"detailsScan" + detailScan);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String signatories = sh.getString("signatureAccomplishment", "");
        String identifier = sh.getString("identifier", "");
        if (scanned ==true && !signatories.matches("true")){
            grante_no = max_id;
            Log.v(TAG,"FIRSTSCANNED");
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String resultUri = extras.getString("CashCardImage");
                String value = extras.getString("cashCardNumber");
                edtCashCard.setText(value);
                Uri myUri = Uri.parse(resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),myUri);
                    mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (signatories.matches("true") && detailScan==0 && identifier.matches("false")){

            Log.v(TAG,"SECONDSCANNED" + dataUp);
            int updateId = in.getIntExtra("updateData", 0);
            id = updateId+1;
            btnRescanBeneId.setText("RE-SCAN");
            btnSubmit.setText("UPDATE");
            try {
//                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,accomplish_by,informant,cc_image, id_image, cash_card_scanned_no, accomplish_img, informant_image, attested_img,attested FROM CgList WHERE id="+max_id);
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,current_grantee_card_number,accomplish_by_full_name,informant_full_name,current_cash_card_picture , beneficiary_picture, cash_card_scanned_no, accomplish_e_signature, informant_e_signature, attested_by_e_signature,attested_by_full_name,current_grantee_card_number_series FROM emv_database_monitoring_details WHERE id="+max_id);
                while (cursor.moveToNext()) {
                    if (cursor.getString(1).matches("")){
                        cashCardNumber = cursor.getString(6);
                    }
                    else{
                        cashCardNumber = cursor.getString(1);
                    }
                    String hhNumber = cursor.getString(2);
                    String seriesNumber = cursor.getString(3);
                    byte[] CashCardImage = cursor.getBlob(4);
                    byte[] granteeImage = cursor.getBlob(5);
                    accomplish = cursor.getBlob(7);
                    informant = cursor.getBlob(8);
                    byte[] attested = cursor.getBlob(9);
                    String Attest = cursor.getString(10);
                    String grantee_series = cursor.getString(11);
                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);


                    //Grantee
                    if(granteeImage!=null){Bitmap grantee = BitmapFactory.decodeByteArray(granteeImage, 0, granteeImage.length);mPreviewGrantee.setImageBitmap(grantee);}
                    else{mPreviewGrantee.setImageResource(R.drawable.ic_image);}

                    //accomplish
                    if(accomplish!=null){Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);mAccomplished.setImageBitmap(accomplishedBy);}
                    else{mAccomplished.setImageResource(R.drawable.ic_image);}

                    //informant
                    if(informant!=null){Bitmap inform = BitmapFactory.decodeByteArray(informant, 0, informant.length);mInformant.setImageBitmap(inform);}
                    else{mInformant.setImageResource(R.drawable.ic_image);}

                    //Attested
                    if(attested!=null){Bitmap attest = BitmapFactory.decodeByteArray(attested, 0, attested.length);mAttested.setImageBitmap(attest);}
                    else{mAttested.setImageResource(R.drawable.ic_image);}
                    edtCashCard.setText(cashCardNumber);
                    edtAccomplishBy.setText(hhNumber);
                    edtInformant.setText(seriesNumber);
                    edtAttested.setText(Attest);
                    edtSeriesNumber.setText(grantee_series);

//                    if (in.hasExtra("EmptyImageView")) {mPreviewGrantee.setImageResource(R.drawable.ic_image); }
//                    else{mPreviewGrantee.setImageBitmap(bmpId); }
                }
            }catch (Exception e){
                Toast.makeText(ScannedDetails.this, "Please contact It administrator" + e, Toast.LENGTH_SHORT).show();
            }
        }
        else if (detailScan!=0){
            int updateId = in.getIntExtra("updateData", 0);
            id = updateId+1;
            Log.v(TAG,"SIGNATORIES" + signatories + "identifier" + identifier + "details SCAN" + detailScan);
            btnRescanBeneId.setText("RE-SCAN");
            btnSubmit.setText("UPDATE");
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,current_grantee_card_number ,accomplish_by_full_name,informant_full_name,current_cash_card_picture , beneficiary_picture, cash_card_scanned_no, accomplish_e_signature, informant_e_signature, attested_by_e_signature, attested_by_full_name, current_grantee_card_number_series FROM emv_database_monitoring_details WHERE id="+(detailScan+1));
//                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no FROM CgList WHERE id="+id);
                while (cursor.moveToNext()) {
                    if (cursor.getString(1).matches("")){
                        cashCardNumber = cursor.getString(6);
                    }
                    else{
                        cashCardNumber = cursor.getString(1);
                    }
                    String hhNumber = cursor.getString(2);
                    String seriesNumber = cursor.getString(3);
                    byte[] CashCardImage = cursor.getBlob(4);
                    byte[] granteeImage = cursor.getBlob(5);
                    accomplish = cursor.getBlob(7);
                    informant = cursor.getBlob(8);
                    byte[] attested = cursor.getBlob(9);
                    String Attest = cursor.getString(10);
                    String grantee_series = cursor.getString(11);
                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);

                    //Grantee
                    if(granteeImage!=null){Bitmap grantee = BitmapFactory.decodeByteArray(granteeImage, 0, granteeImage.length);mPreviewGrantee.setImageBitmap(grantee);}
                    else{mPreviewGrantee.setImageResource(R.drawable.ic_image);}

                    //accomplish
                    if(accomplish!=null){Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);mAccomplished.setImageBitmap(accomplishedBy);}
                    else{mAccomplished.setImageResource(R.drawable.ic_image);}

                    //informant
                    if(informant!=null){Bitmap inform = BitmapFactory.decodeByteArray(informant, 0, informant.length);mInformant.setImageBitmap(inform);}
                    else{mInformant.setImageResource(R.drawable.ic_image);}

                    //Attested
                    if(attested!=null){Bitmap attest = BitmapFactory.decodeByteArray(attested, 0, attested.length);mAttested.setImageBitmap(attest);}
                    else{mAttested.setImageResource(R.drawable.ic_image);}
                    edtCashCard.setText(cashCardNumber);
                    edtAccomplishBy.setText(hhNumber);
                    edtInformant.setText(seriesNumber);
                    edtAttested.setText(Attest);
                    edtSeriesNumber.setText(grantee_series);
//                    if (in.hasExtra("EmptyImageView")) {mPreviewGrantee.setImageResource(R.drawable.ic_image); }
//                    else{mPreviewGrantee.setImageBitmap(bmpId); }
                }
            }catch (Exception e){
                Toast.makeText(ScannedDetails.this, "Please contact It administrator" + e, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int updateId = in.getIntExtra("updateData", 0);
            id = updateId+1;
            Log.v(TAG,"3rdScanned" + max_id + "id " + id);
            btnRescanBeneId.setText("RE-SCAN");
            btnSubmit.setText("UPDATE");
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,current_grantee_card_number ,accomplish_by_full_name,informant_full_name,current_cash_card_picture, beneficiary_picture, cash_card_scanned_no, accomplish_e_signature, informant_e_signature, attested_by_e_signature, attested_by_full_name, current_grantee_card_number_series FROM emv_database_monitoring_details WHERE id="+id);
//                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no FROM CgList WHERE id="+id);
                while (cursor.moveToNext()) {
                    if (cursor.getString(1).matches("")){
                        cashCardNumber = cursor.getString(6);
                    }
                    else{
                        cashCardNumber = cursor.getString(1);
                    }
                    String hhNumber = cursor.getString(2);
                    String seriesNumber = cursor.getString(3);
                    byte[] CashCardImage = cursor.getBlob(4);
                    granteeImage = cursor.getBlob(5);
                    accomplish = cursor.getBlob(7);
                    informant = cursor.getBlob(8);
                    byte[] attested = cursor.getBlob(9);
                    String grantee_series = cursor.getString(11);
                    String Attest = cursor.getString(10);

                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);

                    Log.v(TAG,"Test grantee null "+granteeImage);

                    //grantee
                    if (granteeImage!=null){Bitmap bmpId = BitmapFactory.decodeByteArray(granteeImage, 0, granteeImage.length);mPreviewGrantee.setImageBitmap(bmpId); }
                    else{mPreviewGrantee.setImageResource(R.drawable.ic_image);}
                    //accomplish
                    if(accomplish!=null){Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);mAccomplished.setImageBitmap(accomplishedBy);}
                    else{mAccomplished.setImageResource(R.drawable.ic_image);}

                    //informant
                    if(informant!=null){Bitmap inform = BitmapFactory.decodeByteArray(informant, 0, informant.length);mInformant.setImageBitmap(inform);}
                    else{mInformant.setImageResource(R.drawable.ic_image);}

                    //Attested
                    if(attested!=null){Bitmap attest = BitmapFactory.decodeByteArray(attested, 0, attested.length);mAttested.setImageBitmap(attest);}
                    else{mAttested.setImageResource(R.drawable.ic_image);}
                    edtCashCard.setText(cashCardNumber);
                    edtAccomplishBy.setText(hhNumber);
                    edtInformant.setText(seriesNumber);
                    edtAttested.setText(Attest);
                    edtSeriesNumber.setText(grantee_series);
                }
            }catch (Exception e){
                Toast.makeText(ScannedDetails.this, "Please contact It administrator" + e, Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (scanned==true){intent = new Intent(ScannedDetails.this, ScanCashCard.class);}
            else{intent = new Intent(ScannedDetails.this, InventoryList.class);}
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    public void signatoriesValidation (){

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        String signatories = sh.getString("signatureAccomplishment", "");
        if (!signatories.matches("true")){
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("signatureAccomplishment", "false");
            myEdit.commit();
        }
    }

    public void getMaxID(){
        try {
//            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT max(id) FROM CGList");
            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT max(id) FROM emv_database_monitoring_details");
            while (cursor.moveToNext()) {
                max_id = cursor.getInt(0);
            }
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt("maxIdScanned", max_id);
            myEdit.commit();

//            Toast.makeText(ScannedDetails.this, "Max IDD" + max_id, Toast.LENGTH_SHORT).show();
            Log.v(TAG,"test Maxs"+max_id);
        }catch (Exception e){
            Log.v(TAG,"test Maxa"+e);
        }
    }
    public void clearSharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        //1
        myEdit.putString("hh_id", "");
        myEdit.putString("full_name", "");
        myEdit.putString("client_status", "");
        myEdit.putString("address", "");
        myEdit.putString("sex", "");
        myEdit.putString("hh_set_group", "");
        myEdit.putString("contact_no", "");
        myEdit.putString("assigned", "");
        myEdit.putString("minor_grantee", "");

        //2
        myEdit.putString("card_released", "");
        myEdit.putString("who_released", "");
        myEdit.putString("place_released", "");
        myEdit.putString("current_grantee_number", "");
        myEdit.putString("is_available", "");
        myEdit.putString("is_available_reason", "");
        myEdit.putString("other_card_number_1", "");
        myEdit.putString("other_card_holder_name_1", "");
        myEdit.putString("other_is_available_1", "");
        myEdit.putString("other_is_available_reason_1", "");
        myEdit.putString("other_card_number_2", "");
        myEdit.putString("other_card_holder_name_2", "");
        myEdit.putString("other_is_available_2", "");
        myEdit.putString("other_is_available_reason_2", "");
        myEdit.putString("other_card_number_3", "");
        myEdit.putString("other_card_holder_name_3", "");
        myEdit.putString("other_is_available_3", "");
        myEdit.putString("other_is_available_reason_3", "");

        //3
        myEdit.putString("nma_amount", "");
        myEdit.putString("nma_reason", "");
        myEdit.putString("date_withdrawn", "");
        myEdit.putString("remarks", "");

        //4
        myEdit.putString("lender_name", "");
        myEdit.putString("pawning_date", "");
        myEdit.putString("loaned_amount", "");
        myEdit.putString("lender_address", "");
        myEdit.putString("date_retrieved", "");
        myEdit.putString("interest", "");
        myEdit.putString("spin_status", "");
        myEdit.putString("pawning_reason", "");
        myEdit.putString("offense_history", "");
        myEdit.putString("offense_history_date", "");
        myEdit.putString("pd_remarks", "");
        myEdit.putString("intervention", "");
        myEdit.putString("other_details", "");
        myEdit.commit();
    }
}