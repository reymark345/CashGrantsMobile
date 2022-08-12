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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class ScannedDetails extends AppCompatActivity {

    EditText edtCashCard, edtHhNumber, edtSeriesNo;
    ImageView mPreviewGrantee, mPreviewCashCard,mAccomplished,mInformant,mAttested;
    Button btnSubmit, btnRescanCashCard, btnRescanBeneId, btn_Accomplished, btn_informant, btn_attested;
    TextInputLayout tilCashCard, tilHousehold, tilSeriesNo;
    public static boolean scanned;
    public static boolean signature;
    private int prevCount = 0;
    public static int id = 0, max_id=0;
    private String cashCardNumber;
    String blankMessage = "Please fill this blank";
    Intent intent;
    Uri image_uri;
    ImageView mPreviewIv;
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 4 || currCount == 9 || currCount == 14 || currCount == 19;
    }
    int dataUp =0;
    int detailScan=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);

        //EdiText
        edtCashCard = findViewById(R.id.Idresult);
        edtHhNumber = (EditText) findViewById(R.id.hhNo);
        edtSeriesNo = (EditText) findViewById(R.id.seriesNo);

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
        signature =false;
        CashCardOnChange();
        HouseholdOnChanged();
        SeriesOnChanged();
        getMaxID();
        getData();
        signatoriesValidation();



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
                String falses = sh.getString("identifier", "");

                int moriah = sh.getInt("updateMoriah", 0);



                Intent in = getIntent();
                dataUp = in.getIntExtra("updateData", 0);
                Toast.makeText(getApplicationContext(), "Acc ni" + dataUp + " " + id, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Accomplish.class);
                intent.putExtra("conditionForSignature", moriah);

                Log.v(TAG,"moriah " + dataUp + " " +moriah);
                startActivity(intent);


//                if (dataUp==0){
//                    Intent in = getIntent();
//                    dataUp = in.getIntExtra("updateData", 0);
//                    Toast.makeText(getApplicationContext(), "Acc ni" + dataUp + " " + id, Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getApplicationContext(), Accomplish.class);
//                    intent.putExtra("conditionForSignature", dataUp);
//                    Log.v(TAG,"moriah " + dataUp + " " +id);
//
//
//                    startActivity(intent);
//
//                }
//                else {

//                finish();
            }
        });
        getSignatories();

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
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            mPreviewGrantee.setImageBitmap(bitmap);
            btnRescanBeneId.setText("RE-SCAN");
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
                            sqLiteHelper.updateScannedCashCard(sTextFromET,imageViewToByte(mPreviewIv));
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

        String CardResult = edtCashCard.getText().toString();
        String household = edtHhNumber.getText().toString();
        String seriesNo = edtSeriesNo.getText().toString();
        String idCard = btnRescanBeneId.getText().toString();
        int length = CardResult.length();

        if (CardResult.matches("[0-9 ]+") && !household.matches("") && !seriesNo.matches("") && idCard.equals("RE-SCAN") && length==23 ){
            try{
                if ( scanned ==true){
                    sqLiteHelper.updateSubmitData(
                            edtCashCard.getText().toString().trim(),
                            edtHhNumber.getText().toString().trim(),
                            edtSeriesNo.getText().toString().trim(),
                            imageViewToByte(mPreviewCashCard),
                            imageViewToByte(mPreviewGrantee)
                    );
                    String value="Added Successfully";
                    intent= new Intent(ScannedDetails.this, ScanCashCard.class);
                    intent.putExtra("toast",value);

                }
                else{
                    sqLiteHelper.updateInventoryList(
                            edtCashCard.getText().toString().trim(),
                            edtHhNumber.getText().toString().trim(),
                            edtSeriesNo.getText().toString().trim(),
                            imageViewToByte(mPreviewCashCard),
                            imageViewToByte(mPreviewGrantee),id
                    );
                    intent = new Intent(ScannedDetails.this, InventoryList.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            catch (Exception e ){
                Toast.makeText(getApplicationContext(), "error "+e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        else if (!household.matches("") && !seriesNo.matches("") ){
            tilHousehold.setError(null);
            tilSeriesNo.setError(null);
        }

        if (!seriesNo.matches("")){
            tilSeriesNo.setError(null);
        }
        if (seriesNo.matches("")){
            tilSeriesNo.setError(blankMessage);
        }
        if (household.matches("")){
            tilHousehold.setError(blankMessage);
        }
        if (!household.matches("")){
            tilHousehold.setError(null);
        }
        if (!CardResult.matches("[0-9 ]+")){
            tilCashCard.setError("Invalid format");
        }
        if (length!=23 ){
            tilCashCard.setError("Not enough length");
        }

        if (idCard.equals("Scan")){
            Toasty.error(this,"Please Scan 4P's Id", Toasty.LENGTH_SHORT).show();
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
        edtHhNumber.addTextChangedListener(new TextWatcher() {
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
        edtSeriesNo.addTextChangedListener(new TextWatcher() {
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

//        if (identifier.matches("true")){
//            intent.putExtra("detailScan", (signatureCondition+1));
//            Log.v(TAG,"unta 1" + identifier + " " + (signatureCondition+1));
//        }
//        else {
//            intent.putExtra("detailScan", signatureCondition);
//            Log.v(TAG,"unta 2" + identifier);
//        }

        if (scanned ==true && !signatories.matches("true")){
            Toast.makeText(ScannedDetails.this, "this is 1" + signatories, Toast.LENGTH_SHORT).show();
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
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no, accomplish_img FROM CgList WHERE id="+max_id);
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
                    byte[] accomplish = cursor.getBlob(7);
                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);


                    //Grantee
                    if(granteeImage.length > 1){Bitmap grantee = BitmapFactory.decodeByteArray(granteeImage, 0, granteeImage.length);mPreviewGrantee.setImageBitmap(grantee);}
                    else{mPreviewGrantee.setImageResource(R.drawable.ic_image);}

                    //accomplish
                    if(accomplish.length > 1){Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);mAccomplished.setImageBitmap(accomplishedBy);}
                    else{mAccomplished.setImageResource(R.drawable.ic_image);}



                    edtCashCard.setText(cashCardNumber);
                    edtHhNumber.setText(hhNumber);
                    edtSeriesNo.setText(seriesNumber);
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
            Log.v(TAG,"lastt" + detailScan);
            btnRescanBeneId.setText("RE-SCAN");
            btnSubmit.setText("UPDATE");
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no, accomplish_img FROM CgList WHERE id="+(detailScan+1));
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
                    byte[] idImage = cursor.getBlob(5);
                    byte[] accomplish = cursor.getBlob(7);

                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    Bitmap bmpId = BitmapFactory.decodeByteArray(idImage, 0, idImage.length);
                    Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);
                    mAccomplished.setImageBitmap(accomplishedBy);
                    edtCashCard.setText(cashCardNumber);
                    edtHhNumber.setText(hhNumber);
                    edtSeriesNo.setText(seriesNumber);
                    if (in.hasExtra("EmptyImageView")) {mPreviewGrantee.setImageResource(R.drawable.ic_image); }
                    else{mPreviewGrantee.setImageBitmap(bmpId); }
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
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no, accomplish_img FROM CgList WHERE id="+id);
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
                    byte[] idImage = cursor.getBlob(5);
                    byte[] accomplish = cursor.getBlob(7);

                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    Bitmap bmpId = BitmapFactory.decodeByteArray(idImage, 0, idImage.length);
                    Bitmap accomplishedBy = BitmapFactory.decodeByteArray(accomplish, 0, accomplish.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);
                    mAccomplished.setImageBitmap(accomplishedBy);
                    edtCashCard.setText(cashCardNumber);
                    edtHhNumber.setText(hhNumber);
                    edtSeriesNo.setText(seriesNumber);
                    if (in.hasExtra("EmptyImageView")) {mPreviewGrantee.setImageResource(R.drawable.ic_image); }
                    else{mPreviewGrantee.setImageBitmap(bmpId); }
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
            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT max(id) FROM CGList");
            while (cursor.moveToNext()) {
                max_id = cursor.getInt(0);
            }
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putInt("maxIdScanned", max_id);
            myEdit.commit();

            Toast.makeText(ScannedDetails.this, "Max IDD" + max_id, Toast.LENGTH_SHORT).show();
            Log.v(TAG,"test Maxs"+max_id);
        }catch (Exception e){
            Log.v(TAG,"test Maxa"+e);
        }
    }
}