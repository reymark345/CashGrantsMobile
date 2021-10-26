package com.example.cashgrantsmobile.Scanner;

import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.example.cashgrantsmobile.Scanner.ScanCashCard.imageViewToByte;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ScannedDetails extends AppCompatActivity {

    EditText edtCashCard, edtHhNumber, edtSeriesNo;
    ImageView mPreview4PsId, mPreviewCashCard;
    Button btnSubmit, btnRescanCashCard, btnRescanBeneId;
    TextInputLayout tilCashCard, tilHousehold, tilSeriesNo;
    public static boolean scanned;
    private int prevCount = 0;
    public int id = 0;
    private String cashCardNumber;
    String blankMessage = "Please filled this blank";
    Intent intent;

    Uri image_uri;

    ImageView mPreviewIv;



    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 4 || currCount == 9 || currCount == 14 || currCount == 19;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);

        //EdiText
        edtCashCard = findViewById(R.id.Idresult);
        edtHhNumber = (EditText) findViewById(R.id.hhNo);
        edtSeriesNo = (EditText) findViewById(R.id.seriesNo);

        //ImageView
        mPreview4PsId = findViewById(R.id.PsID);
        mPreviewCashCard = findViewById(R.id.ScannedImage);


        mPreviewIv = findViewById(R.id.imgUri);
        mPreviewIv .setVisibility(View.INVISIBLE);

        //buttons

        btnRescanCashCard = (Button) findViewById(R.id.rescanCashCard);
        btnRescanBeneId = (Button) findViewById(R.id.rescanBeneId);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //round ImageView
        mPreview4PsId.setClipToOutline(true);
        mPreviewCashCard.setClipToOutline(true);

        //layoutMaterial
        tilCashCard = findViewById(R.id.til_cashCard);
        tilHousehold = findViewById(R.id.til_household);
        tilSeriesNo = findViewById(R.id.til_seriesno);
        CashCardOnChange();
        HouseholdOnChanged();
        SeriesOnChanged();
        getData();

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

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 102);

//                Toast.makeText(getApplicationContext(), "No function yet, to be update using Inheritance", Toast.LENGTH_SHORT).show();


//                ScanCashCard action = new ScanCashCard();
//                action.pickCamera();
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
            mPreview4PsId.setImageBitmap(bitmap);
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
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
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
                            mPreviewIv.setImageBitmap(Bitmap.createScaledBitmap(bm, 187, 250, false));
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
                            mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bitmaps, 187, 250, false));
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
                            imageViewToByte(mPreview4PsId)
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
                            imageViewToByte(mPreview4PsId),id
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
            Toast.makeText(getApplicationContext(), "Please Scan 4P's Id", Toast.LENGTH_SHORT).show();
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
        if (scanned ==true){
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String resultUri = extras.getString("CashCardImage");
                String value = extras.getString("cashCardNumber");
                edtCashCard.setText(value);
                Uri myUri = Uri.parse(resultUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),myUri);
                    mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 187, 250, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Intent in = getIntent();
            int updateId = in.getIntExtra("updateData", 0);
            id = updateId+1;
            btnRescanBeneId.setText("RE-SCAN");
            btnSubmit.setText("UPDATE");
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no FROM CgList WHERE id="+id);
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
                    Bitmap bmpCashCard = BitmapFactory.decodeByteArray(CashCardImage, 0, CashCardImage.length);
                    Bitmap bmpId = BitmapFactory.decodeByteArray(idImage, 0, idImage.length);
                    mPreviewCashCard.setImageBitmap(bmpCashCard);
                    mPreview4PsId.setImageBitmap(bmpId);
                    edtCashCard.setText(cashCardNumber);
                    edtHhNumber.setText(hhNumber);
                    edtSeriesNo.setText(seriesNumber);
                }
            }catch (Exception e){
                Toast.makeText(ScannedDetails.this, "Please contact It administrator" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}