package com.example.cashgrantsmobile.Scanner;




import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.example.cashgrantsmobile.Scanner.ScanCashCard.imageViewToByte;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashgrantsmobile.Inventory.InventoryList;
import com.example.cashgrantsmobile.Inventory.UpdateEntries;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Signatories.Informant;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class ScanCashCard extends AppCompatActivity {

    private static int MANDATORY_PAGE_LOCATION = 0 ;
    ImageView mPreviewIv;
    private static final int CAMERA_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String StoragePermission[],required_field;
    Button btn_search_hh, rescanCashCard,btn_scanID, btn_cash_card,btn_grantee;
    public static boolean scanned = true;
    public static boolean pressBtn_search = false;
    public static boolean pressNext = false;
    Uri image_uri;
    String full_name,hh_id,client_status,address,sex,hh_set_group,current_grantee_card_number,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_cardholder_name_3,upload_history_id,created_at,updated_at,validated_at;
    Integer emv_id;
    TextView tvAdditional,tViewCashCard1;
    ImageView mPreviewCashCard,mAdditionalID,mPreviewGrantee, mImgUri;
    private int prevCount = 0;
    Integer isValidationError = 0;


    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 4 ||currCount == 9 || currCount == 14 || currCount == 19;
    }


    //onboard

    Cursor search;

    private TextView tvNext, tvPrev;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;
    TextInputLayout tilHhId, tilFullname, tilClientStatus, tilAddress, tilSex, tilSet, tilContactNo, tilAssigned, tilMinorGrantee,tilGranteeOne;
    TextInputLayout tilCardReleased, tilWhoReleased, tilPlaceReleased, tilIsAvailable, tilCurrentGranteeNumber,tiladditionalID,tilCard,tilSeriesNumber, tilIsID,tilGrantee,tilCardBtn,til_other_remarks_1,til_other_remarks_2,til_other_remarks_3,tilPinAttached,tilAvailablereason,tilUnclaimedReason,tilRequestReplacement,tilContactNoOf,tilRepresentativeIntroOne,tilRepresentativeIntroTwo,tilDistribution,tilisPinAttached,tilOthersUnclaimedReason,tilReasonNotPresented,tilIsAvailableReason, tilOtherCardNumber1, tilOtherCardHolderName1, tilOtherIsAvailable1, tilOtherIsAvailableReason1, tilOtherCardNumber2, tilOtherCardHolderName2, tilOtherIsAvailable2, tilOtherIsAvailableReason2, tilOtherCardNumber3, tilOtherCardHolderName3, tilOtherIsAvailable3, tilOtherIsAvailableReason3, tilOtherCardNumberSeries1, tilOtherCardNumberSeries2, tilOtherCardNumberSeries3;
    TextInputLayout tilNmaAmount, tilNmaReason, tilDateWithdrawn, tilRemarks;
    TextInputLayout tilLenderName, tilPawningDate, tilLoanedAmount, tilLenderAddress, tilDateRetrieved, tilInterest, tilStatus, tilPawningReason, tilOffenseHistory, tilOffenseHistoryDate, tilPdRemarks, tilIntervention, tilOtherDetails;
    EditText edt_hh, edt_fullname, edt_address, edt_set, edt_contact_no, edt_assigned,edtRepresentativeOne,edtRepresentativeTwo;
    EditText edt_card_released, edt_who_released, edt_place_released, edt_current_grantee_number, edt_other_card_number_1, edt_other_card_holder_name_1, edt_other_card_number_2, edt_other_card_holder_name_2, edt_other_card_number_3, edt_other_card_holder_name_3, edt_other_card_number_series_1, edt_other_card_number_series_2, edt_other_card_number_series_3,edt_cashCardNumber,edt_series_no, edt_other_remarks_1,edt_other_remarks_2,edt_other_remarks_3,edtOthersUnclaimedReason,edtReasonNotPresented;
    EditText edt_nma_amount, edt_nma_reason,  edt_date_withdrawn, edt_remarks;
    EditText edt_lender_name, edt_pawning_date, edt_loaned_amount, edt_lender_address, edt_date_retrieved, edt_interest, edt_pawning_reason, edt_offense_history_date, edt_pd_remarks, edt_intervention, edt_other_details;
    AutoCompleteTextView spinSex, spinAnswer, spinIsAvail, spinIsAvail1, spinIsAvail2, spinIsAvail3, spinIsAvailReason, spinIsAvailReason1, spinIsAvailReason2, spinIsAvailReason3, spinClientStatus, spinStatus, spinOffenseHistory, spinIsID, spinIsDistribution_1,spinnerIsUnclaimedReason,spinnerContactNoOf,spinnerGrantee,spinnerIsReasonReplacement,spinnerIsPinAttached;

    MaterialCardView mcvPawning;

    String[] Ans = new String[]{"","Yes", "No"};
    String[] CardRequired = new String[]{"Yes", "No"};
    String[] Sex = new String[]{"MALE", "FEMALE"};
    String[] Reasons = new String[]{"Unclaimed", "Lost/Stolen", "Damaged/Defective", "Pawned", "Not Turned Over", "Others"};
    String[] Contact_no_of = new String[]{"Grantee", "Others"};
    String[] ClientStatus = new String[]{
            "1 - Active",
            "14 - No Eligible (0-18 y/o) for CVS Monitoring (Certified by RPMO)",
            "15 - No Eligible member of HH for CVS monitoring",
            "12 - Moved out of the Area Without Notice",
            "7 - Delisted due to non-compliance",
            "29 - Delisted due to Aging Inactive Status in the Program",
            "19 - Grants Temporarily On-Hold",
            "5 - GRS delisted due to Misbehavior",
            "6 - Duplicates",
            "8 - Waived",
            "10 - GRS delisted due to Disqualification",
            "17 - GRS (Not Eligible - Regular Income)",
            "3 - Graduated Due to Improved Level of Well-Being",
            "9 - Not Registered",
            "13 - Validated Not Poor due to Change of Address",
            "21 - RPMO Approved Household for NPMO Processing",
            "22 - Unlocated households",
            "24 - GRS: Suspended grants due to misbehavior of HH",
            "01 - Active",
            "02 - Delisted by the Field Office",
            "04 - Delisted (Fraud)",
            "05 - GRS (Fraud)",
            "06 - Duplicates (within MCCT)",
            "08 - Waived",
            "11 - Moved to non-PP Area",
            "29 - Household Integrated to the PPIS",
            "51 - Graduated (Included in the PPIS)",
            "53 - Pending Registration (HH also Encoded in PPIS)",
            "54 - Delisted (HH also in PPIS)",
            "56 - Inactive (No Longer Interested)",
            "59 - Eligible Child/ren not Selected for CVS Monitoring",
            "61 - Graduated (Not included in the PPIS)"
    };
    String[] Status = new String[]{"","Ongoing (card as collateral)", "Ongoing (card is on-hand)", "Retrieved"};
    String[] Offense = new String[]{"","1st Offense", "2nd Offense", "3rd Offense"};
    String[] distribution = new String[]{"Claimed", "Unclaimed"};
    String[] ReasonCashCardUnclaimed = new String[]{
            "Bedridden",
            "Deceased",
            "Imprisoned",
            "Mentally challenged",
            "Family conflict",
            "Working abroad",
            "Working outside the region",
            "Name discrepancy",
            "Transferred household",
            "Household moved out without notice",
            "Waived",
            "For delisting of client status",
            "Temporarily on hold (code 19)",
            "Force Majeure",
            "Others"
    };


    public Integer getUserId() {

        Integer userID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT user_id FROM Api LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            userID = lastEmvDatabaseID.getInt(0);
        }
        return userID;
    }

    public String getUserName() {

        String userNAME = "";
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT name FROM Api LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            userNAME = lastEmvDatabaseID.getString(0);
        }
        return userNAME;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cash_card_scanner_entries);
        mPreviewIv = findViewById(R.id.imageIv);
        mPreviewIv .setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        isValidationError = 0;
        if (extras != null) {
            String value = extras.getString("toast");
            Toasty.success(this,""+value, Toasty.LENGTH_SHORT).show();
            extras.clear();
        }
        temp_BLOB_status();
        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        StoragePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        MANDATORY_PAGE_LOCATION = 0;
        pressNext=false;
        //onboard

        introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()) {
            launchHomeScreen();
//            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        tvNext = findViewById(R.id.tvNext);
        tvPrev = findViewById(R.id.tvPrev);
//        viewPager = findViewById(R.id.viewPager);
        viewPager= findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);
        tvPrev.setVisibility(View.INVISIBLE);

        layouts = new int[]{
                R.layout.intro_one,
                R.layout.intro_two,
                R.layout.intro_three,
                R.layout.intro_four
        };
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_CheckNextValidation();
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                String hh_id = sh.getString("hh_id", "160310001-");
                String buttonNext = sh.getString("pressBtn_search", "");


                if (hh_id.length() > 0 && buttonNext.matches("true")){
                    nextValidation();
                }
                else{
                    Log.v(ContentValues.TAG,"Error" + " " +hh_id.length() + " " +buttonNext);
                    Toasty.info(getApplicationContext(),"Search household first", Toasty.LENGTH_SHORT).show();
                }

            }
        });
        tvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem();

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                String hh_id = sh.getString("hh_id", "160310001-");
                if (hh_id.length() > 0) {
                    pressBtn_search=true;
                }

                store_preferences(current+1);

                if (current > 0) {
                    MANDATORY_PAGE_LOCATION--;
                    pressNext =false;
                    current = current - 1;
                    viewPager.setCurrentItem(current);
                }
                if (current == 0){
                    tvPrev.setVisibility(View.INVISIBLE);

                    tilHhId = findViewById(R.id.til_hhid);
                    edt_hh = findViewById(R.id.edtHhId);
                    HouseholdOnChange(edt_hh);

                } else {
                    tvPrev.setVisibility(View.VISIBLE);
                }
            }
        });


        viewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);



        addBottomDots(0);
        changeStatusBarColor();
        //end onboard
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(ScanCashCard.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showImageImportDialog() {
        if(!checkCameraPermission()){
            requestCameraPermission();}
        else{
            pickCamera();
        }
    }


    public void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    public void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickCamera();
                    } else {
                        pickCamera();
//                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();

                    }
                }
        }
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
        edt_cashCardNumber.setText(sb.toString());
        edt_cashCardNumber.setSelection(sb.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mAdditionalID.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                String count = "SELECT count(*) FROM tmp_blob";
                Cursor mCursor = db.rawQuery(count, null);
                mCursor.moveToFirst();
                int iCount = mCursor.getInt(0);
                if(iCount==0){sqLiteHelper.insertDefaultAdditionalTmp(imageViewToByte(mAdditionalID));tiladditionalID.setError(null);}
                else {sqLiteHelper.updateTmpAdditional(imageViewToByte(mAdditionalID));tiladditionalID.setError(null);}
            }catch (Exception e){
                Log.v(TAG,"errordaw" + e);
            }
        }
        else if (requestCode == 103){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mPreviewGrantee.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);

                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                String count = "SELECT count(*) FROM tmp_blob";
                Cursor mCursor = db.rawQuery(count, null);
                mCursor.moveToFirst();
                int iCount = mCursor.getInt(0);
                if(iCount==0){sqLiteHelper.insertDefaultGranteeTmp(imageViewToByte(mPreviewGrantee));tilGrantee.setError(null);}
                else {tilGrantee.setError(null);sqLiteHelper.updateTmpGrantee(imageViewToByte(mPreviewGrantee));}

            }catch (Exception e){
                Log.v(TAG,"error" + e);
            }
        }
        else if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_CAMERA_CODE){

                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode ==RESULT_OK){
                Uri resultUri = result.getUri();
                resultUri.getPath();
                mPreviewCashCard.setImageURI(resultUri);
                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewCashCard.getDrawable();
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
                    image_uri = Uri.parse(image_uri.toString());
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                        mPreviewCashCard.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));

                        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                        String temp_status = sh.getString("temp_blob", "");

                        SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                        String count = "SELECT count(*) FROM tmp_blob";
                        Cursor mCursor = db.rawQuery(count, null);
                        mCursor.moveToFirst();
                        int iCount = mCursor.getInt(0);
                        if(iCount==0){sqLiteHelper.insertDefaultScannedTmp(imageViewToByte(mPreviewCashCard));tilCardBtn.setError(null);}
                        else {sqLiteHelper.updateTmpScannedCC(imageViewToByte(mPreviewCashCard));tilCardBtn.setError(null);}

                        if (sTextFromET.length() >23){
                            String limitString = sTextFromET.substring(0,23);
                            edt_cashCardNumber.setText(limitString);
                            String CardResult = edt_cashCardNumber.getText().toString();
                            if (!CardResult.matches("[0-9 ]+")){
                                tilCard.setError("Invalid format");
                            }
                        }
                        else{
                            edt_cashCardNumber.setText(sTextFromET);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();
                Toasty.error(this,""+error, Toasty.LENGTH_SHORT).show();
                sqLiteHelper.storeLogs("error", "", "Scanned: " + error);
            }
        }








  // -------------------- THIS LINE is OLD feature , Image Digitalization Signature ---------------------------------------------

//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if(resultCode ==RESULT_OK){
//                Uri resultUri = result.getUri();
//                resultUri.getPath();
//                mPreviewIv.setImageURI(resultUri);
//                BitmapDrawable bitmapDrawable = (BitmapDrawable)mPreviewIv.getDrawable();
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
//
//                if(!recognizer.isOperational()){
//                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//                    SparseArray<TextBlock> items = recognizer.detect(frame);
//                    StringBuilder sb = new StringBuilder();
//
//                    for (int i = 0; i<items.size(); i++){
//                        TextBlock myItem = items.valueAt(i);
//                        sb.append(myItem.getValue());
//                        sb.append("\n");
//                    }
//
//                    String sTextFromET=sb.toString().replaceAll("\\s+", "");
//                    sTextFromET = sTextFromET.replace("a", "8");
//                    sTextFromET = sTextFromET.replace("A", "8");
//                    sTextFromET = sTextFromET.replace("B", "6");
//                    sTextFromET = sTextFromET.replace("b", "6");
//                    sTextFromET = sTextFromET.replace("D", "0");
//                    sTextFromET = sTextFromET.replace("e", "8");
//                    sTextFromET = sTextFromET.replace("E", "8");
//                    sTextFromET = sTextFromET.replace("L", "6");
//                    sTextFromET = sTextFromET.replace("S", "5");
//                    sTextFromET = sTextFromET.replace("G", "6");
//                    sTextFromET = sTextFromET.replace("%", "6");
//                    sTextFromET = sTextFromET.replace("&", "6");
//                    sTextFromET = sTextFromET.replace("?", "7");
//                    sTextFromET = sTextFromET.replace("l", "1");
//                    sTextFromET = sTextFromET.replace("+", "7");
//                    sTextFromET = sTextFromET.replace("}", "7");
//                    sTextFromET = sTextFromET.replace("O", "0");
//                    sTextFromET = sTextFromET.replaceAll("....", "$0 ");
//                    //save temp database
//                    image_uri = Uri.parse(image_uri.toString());
//
//                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("signatureAccomplishment", "false");
//                    myEdit.putString("identifier", "false");
//                    myEdit.putString("granteeBtn", "false");
//                    myEdit.putInt("updateValue", 0);
//                    myEdit.commit();
//
//                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
//                    ScannedDetails.scanned = true;
//
//                    int emv_id = sh.getInt("emv_id", 0);
//                    String full_name = sh.getString("full_name", "");
//                    String household = sh.getString("hh_id", "160310001-");
//                    String client_status = sh.getString("client_status", "");
//                    String address = sh.getString("address", "");
//                    String sex = sh.getString("sex", "");
//                    String hh_set_group = sh.getString("hh_set_group", "");
//                    String contact_no = sh.getString("contact_no", "");
//                    String assigned = sh.getString("assigned", "");
//                    String minor_grantee = sh.getString("minor_grantee", "");
//
//                    String card_released = sh.getString("card_released", "");
//                    String who_released = sh.getString("who_released", "");
//                    String place_released = sh.getString("place_released", "");
//                    String is_available = sh.getString("is_available", "");
//                    String is_available_reason = sh.getString("is_available_reason", "");
//
//                    String other_card_number_1 = sh.getString("other_card_number_1", "");
//                    String other_card_holder_name_1 = sh.getString("other_card_holder_name_1", "");
//                    String other_is_available_1 = sh.getString("other_is_available_1", "");
//                    String other_is_available_reason_1 = sh.getString("other_is_available_reason_1", "");
//
//
//                    String other_card_number_2 = sh.getString("other_card_number_2", "");
//                    String other_card_holder_name_2 = sh.getString("other_card_holder_name_2", "");
//                    String other_is_available_2 = sh.getString("other_is_available_2", "");
//
//                    String other_is_available_reason_2 = sh.getString("other_is_available_reason_2", "");
//                    String other_card_number_3 = sh.getString("other_card_number_3", "");
//                    String other_card_holder_name_3 = sh.getString("other_card_holder_name_3", "");
//                    String other_is_available_3 = sh.getString("other_is_available_3", "");
//                    String other_is_available_reason_3 = sh.getString("other_is_available_reason_3", "");
//
//                    String other_card_number_series_1 = sh.getString("other_card_number_series_1", "");
//                    String other_card_number_series_2 = sh.getString("other_card_number_series_2", "");
//                    String other_card_number_series_3 = sh.getString("other_card_number_series_3", "");
//
//                    String nma_amount = sh.getString("nma_amount", "");
//                    String nma_reason = sh.getString("nma_reason", "");
//                    String date_withdrawn = sh.getString("date_withdrawn", "");
//                    String remarks = sh.getString("remarks", "");
//
//                    String lender_name = sh.getString("lender_name", "");
//                    String pawning_date = sh.getString("pawning_date", "");
//                    String date_retrieved = sh.getString("date_retrieved", "");
//                    String spin_status = sh.getString("spin_status", "");
//                    String pawning_reason = sh.getString("pawning_reason", "");
//
//                    String offense_history = sh.getString("offense_history", "");
//                    String offense_history_date = sh.getString("offense_history_date", "");
//                    String pd_remarks = sh.getString("pd_remarks", "");
//                    String intervention = sh.getString("intervention", "");
//                    String other_details = sh.getString("other_details", "");
//
//                    String pawn_loaned_amount = sh.getString("loaned_amount", "");
//                    String pawn_lender_address = sh.getString("lender_address", "");
//                    String pawn_interest = sh.getString("interest", "");
//                    String hh_no_1 = sh.getString("hh_id", "160310001-");
//
//                    Intent i = new Intent(ScanCashCard.this, ScannedDetails.class);
//
//                    try {
//                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
//                        mPreviewIv.setImageBitmap(Bitmap.createScaledBitmap(bm, 187, 250, false));
//                        if (sTextFromET.length() >23){
//                            String limitString = sTextFromET.substring(0,23);
//                            i.putExtra("cashCardNumber",limitString);
//                            sqLiteHelper.insertEmvDatabase(full_name,household,client_status,address,sex,hh_set_group,contact_no,assigned,minor_grantee,card_released,who_released,place_released,is_available,is_available_reason,other_card_number_1,other_card_holder_name_1,other_is_available_1,other_is_available_reason_1,other_card_number_2,other_card_holder_name_2,other_is_available_2,other_is_available_reason_2,other_card_number_3,other_card_holder_name_3,other_is_available_3,other_is_available_reason_3,nma_amount,nma_reason,date_withdrawn,remarks, lender_name,pawning_date,date_retrieved,spin_status,pawning_reason,offense_history,offense_history_date,pd_remarks,intervention,other_details,limitString,imageViewToByte(mPreviewIv), pawn_loaned_amount,pawn_lender_address,pawn_interest, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, getUserId(), emv_id, getUserName());
//                        }
//                        else{
//                            i.putExtra("cashCardNumber",sTextFromET);
//                            sqLiteHelper.insertEmvDatabase(full_name,household,client_status,address,sex,hh_set_group,contact_no,assigned,minor_grantee,card_released,who_released,place_released,is_available,is_available_reason,other_card_number_1,other_card_holder_name_1,other_is_available_1,other_is_available_reason_1,other_card_number_2,other_card_holder_name_2,other_is_available_2,other_is_available_reason_2,other_card_number_3,other_card_holder_name_3,other_is_available_3,other_is_available_reason_3,nma_amount,nma_reason,date_withdrawn,remarks, lender_name,pawning_date,date_retrieved,spin_status,pawning_reason,offense_history,offense_history_date,pd_remarks,intervention,other_details,sTextFromET,imageViewToByte(mPreviewIv), pawn_loaned_amount,pawn_lender_address,pawn_interest, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, getUserId(), emv_id, getUserName());
//                        }
//                        sqLiteHelper.update_emv_monitoring(
//                                hh_no_1
//                        );
//                        sqLiteHelper.storeLogs("scanned", household, "Scanned household successfully");
//                        clearSharedPref();
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        sqLiteHelper.storeLogs("error", household, "Scanned household failed");
//                    }
//                    //camera
//                    i.putExtra("CashCardImage",image_uri.toString());
//                    startActivity(i);
//                    finish();
//                }
//            }
//            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//
//                Exception error = result.getError();
//                Toasty.error(this,""+error, Toasty.LENGTH_SHORT).show();
//                sqLiteHelper.storeLogs("error", "", "Scanned: " + error);
//            }
//        }
    }



    public void getImage(int validation){
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT scanned_e_image,additional_id_image,grantee_e_image FROM tmp_blob WHERE id=1");
                Log.v(TAG,"cursorrr" + cursor.getCount());
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()) {
                        byte[] scanned_image = cursor.getBlob(0);
                        byte[] additional_image = cursor.getBlob(1);
                        byte[] grantee_image = cursor.getBlob(2);
                        if (scanned_image != null) {
                            Bitmap scanned = BitmapFactory.decodeByteArray(scanned_image, 0, scanned_image.length);
                            mPreviewCashCard.setImageBitmap(scanned);
                        }
                        if (additional_image != null) {
                            Bitmap additional = BitmapFactory.decodeByteArray(additional_image, 0, additional_image.length);
                            mAdditionalID.setImageBitmap(additional);
                        }

                        if (grantee_image != null) {
                            Bitmap grantee = BitmapFactory.decodeByteArray(grantee_image, 0, grantee_image.length);
                            mPreviewGrantee.setImageBitmap(grantee);
                        }
                    }

                }
                else{
                    tilCardBtn.setError(required_field);
                    tiladditionalID.setError(required_field);
                    tilGrantee.setError(required_field);
                }


            }
            catch (Exception e){
                Log.v(TAG,"Errors " + e);
            }


    }


    public void temp_BLOB_status(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        if (!sharedPreferences.contains("temp_blob")) {
            myEdit.putString("temp_blob", "0");
            myEdit.commit();
        }
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (pressNext ==false) {
                viewPager.setCurrentItem(MANDATORY_PAGE_LOCATION, true);
            }
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            if (position == layouts.length - 1) {
                tvNext.setText("SCAN");
            } else {
                tvNext.setText("NEXT");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

//    public void countIncomplete(){
//        Cursor incomplete_total = MainActivity.sqLiteHelper.getData("SELECT id FROM emv_database_monitoring_details WHERE card_scanning_status=0");
//
//        int incomplete = incomplete_total.getCount();
//        if (incomplete>0){
//            new SweetAlertDialog(ScanCashCard.this, SweetAlertDialog.WARNING_TYPE)
//                    .setTitleText("You have "+incomplete+" incomplete household entries")
//                    .setContentText("Please update the previous household to complete")
//                    .setConfirmText("Proceed")
//                    .setCancelText("Update")
//                    .showCancelButton(true)
//                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            sDialog.dismiss();
//                        }
//                    })
//                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sDialog) {
//                            Intent in = new Intent(getApplicationContext(), InventoryList.class);
//                            startActivity(in);
//                            finish();
//                        }
//                    }).show();
//        }
//
//    }

    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];
        int[] activeColors = getResources().getIntArray(R.array.active);
        int[] inActiveColors = getResources().getIntArray(R.array.inactive);
        layoutDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(50);
            dots[i].setTextColor(inActiveColors[currentPage]);
            layoutDots.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setTextColor(activeColors[currentPage]);
        }
    }


    public class MyViewPagerAdapter extends PagerAdapter {

        LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {

        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

            //intro_one.xml
            if (position == 0) {
                tilContactNo = findViewById(R.id.til_contact_no);
                tilSet = findViewById(R.id.til_set);
                tilHhId = findViewById(R.id.til_hhid);
                tilAssigned = findViewById(R.id.til_assigned);
                tilMinorGrantee = findViewById(R.id.til_minor_grantee);
                tilContactNoOf = findViewById(R.id.til_contact_no_of);
                tilRepresentativeIntroOne = findViewById(R.id.til_representative_one);
                tilGranteeOne = findViewById(R.id.til_grantee);
                edt_hh = findViewById(R.id.edtHhId);
                edt_fullname = findViewById(R.id.edtFullname);
                spinClientStatus = findViewById(R.id.spinnerClientStatus);
                edt_address = findViewById(R.id.edtAddress);
                spinSex = findViewById(R.id.spinnerSex);
                edt_set = findViewById(R.id.edtSet);
                edt_contact_no = findViewById(R.id.edtContactNo);
                spinnerContactNoOf = findViewById(R.id.spinnerContactNoOf);
                edt_assigned = findViewById(R.id.edtAssigned);
                edtRepresentativeOne = findViewById(R.id.edtRepresentativeOne);
                spinAnswer = findViewById(R.id.spinnerMinorGrantee);
                btn_search_hh = (Button) findViewById(R.id.btnSearchHh);
                spinnerGrantee = findViewById(R.id.spinnerGrantee);
                tilAssigned.setVisibility(View.GONE);
                tilMinorGrantee.setVisibility(View.GONE);
                tilRepresentativeIntroOne.setVisibility(View.GONE);

                btn_search_hh.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn_func();
                    }
                });


                ArrayAdapter<String> adapterSex = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Sex);
                ArrayAdapter<String> adapterAns = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterClientStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ClientStatus);
                ArrayAdapter<String> adapterContact_no_of = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Contact_no_of);
                ArrayAdapter<String> adapterGrantee = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, CardRequired);

                adapterSex.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterAns.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterClientStatus.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterContact_no_of.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterGrantee.setDropDownViewResource(simple_spinner_dropdown_item);

                spinSex.setAdapter(adapterSex);
                spinAnswer.setAdapter(adapterAns);
                spinClientStatus.setAdapter(adapterClientStatus);
                spinnerContactNoOf.setAdapter(adapterContact_no_of);
                spinnerGrantee.setAdapter(adapterGrantee);

                String hh_id = sh.getString("hh_id", "160310001-");
                String full_name = sh.getString("full_name", "");
                String client_status = sh.getString("client_status", "");
                String address = sh.getString("address", "");
                String sex = sh.getString("sex", "");
                String contact_no = sh.getString("contact_no", "");
                String contact_no_of = sh.getString("contact_no_of", "");
                String hh_set_group = sh.getString("hh_set_group", "");
                String assigned = sh.getString("assigned", "");
                String minor_grantee = sh.getString("minor_grantee", "");
                String grantee = sh.getString("grantee_in", "");
                String representativeOne = sh.getString("rpt_intro_one", "");

                representativeOne();
                contactNumber();
                contactNumberOf();
                granteeOne();
                setEntry();


                if (hh_id.matches("")){edt_hh.setText("160310001-");}else {edt_hh.setText(hh_id); }
                edt_fullname.setText(full_name);
                spinClientStatus.setText(client_status,false);
                edt_address.setText(address);
                spinSex.setText(sex,false);
                edt_set.setText(hh_set_group);
                edt_contact_no.setText(contact_no);
                edt_assigned.setText(assigned);
                spinAnswer.setText(minor_grantee,false);
                spinnerContactNoOf.setText(contact_no_of,false);
                spinnerGrantee.setText(grantee,false);
                edtRepresentativeOne.setText(representativeOne);


                spinnerContactNoOf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinnerContactNoOf.getText().toString().matches("Others")) {
                            tilRepresentativeIntroOne.setVisibility(View.VISIBLE);
                        } else {
                            edtRepresentativeOne.setText("");
                            tilRepresentativeIntroOne.setError(null);
                            tilRepresentativeIntroOne.setVisibility(View.GONE);
                        }
                    }
                });

                if (spinnerContactNoOf.getText().toString().matches("Others")){
                    tilRepresentativeIntroOne.setVisibility(View.VISIBLE);
                }else{tilRepresentativeIntroOne.setVisibility(View.GONE);}


            } else if (position == 1) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                pressBtn_search = false;

                edt_card_released = findViewById(R.id.edtCardReleased);
                edt_who_released = findViewById(R.id.edtWhoReleased);
                edt_place_released = findViewById(R.id.edtPlaceReleased);
                edt_current_grantee_number = findViewById(R.id.edtCurrentGranteeNumber);
                spinIsAvail = findViewById(R.id.spinnerIsAvailable);
                spinIsDistribution_1 = findViewById(R.id.spinnerDistribution);
                spinIsAvailReason = findViewById(R.id.spinnerIsAvailableReason);
                spinnerIsPinAttached  = findViewById(R.id.spinnerIsPinAttached);
                edt_other_card_number_1 = findViewById(R.id.edtOtherCardNumber1);
                edt_other_card_number_series_1 = findViewById(R.id.edtOtherCardNumberSeries1);
                edt_other_card_holder_name_1 = findViewById(R.id.edtOtherCardHolderName1);
                spinIsAvail1 = findViewById(R.id.spinnerOtherIsAvailable1);
                spinIsAvailReason1 = findViewById(R.id.spinnerOtherIsAvailableReason1);
                edt_other_card_number_2 = findViewById(R.id.edtOtherCardNumber2);
                edt_other_card_number_series_2 = findViewById(R.id.edtOtherCardNumberSeries2);
                edt_other_card_holder_name_2 = findViewById(R.id.edtOtherCardHolderName2);
                spinIsAvail2 = findViewById(R.id.spinnerOtherIsAvailable2);
                spinIsAvailReason2 = findViewById(R.id.spinnerOtherIsAvailableReason2);
                edt_other_card_number_3 = findViewById(R.id.edtOtherCardNumber3);
                edt_other_card_number_series_3 = findViewById(R.id.edtOtherCardNumberSeries3);
                edt_other_card_holder_name_3 = findViewById(R.id.edtOtherCardHolderName3);
                spinIsAvail3 = findViewById(R.id.spinnerOtherIsAvailable3);
                spinIsAvailReason3 = findViewById(R.id.spinnerOtherIsAvailableReason3);
                spinnerIsUnclaimedReason= findViewById(R.id.spinnerIsUnclaimedReason);
                spinnerIsReasonReplacement= findViewById(R.id.spinnerIsReasonReplacement);
                edtRepresentativeTwo = findViewById(R.id.edtRepresentativeTwo);
                edtOthersUnclaimedReason= findViewById(R.id.edtOthersUnclaimedReason);
                edtReasonNotPresented= findViewById(R.id.edtReasonNotPresented);

                spinIsID = findViewById(R.id.spinnerIsID);
                edt_cashCardNumber = findViewById(R.id.edt_cashCardNumber);
                edt_series_no = findViewById(R.id.edt_series_no);

                btn_cash_card = findViewById(R.id.btn_scanCashCard);
                btn_scanID = findViewById(R.id.btn_scanID);
                btn_grantee = findViewById(R.id.btn_grantee);
                tvAdditional = findViewById(R.id.tViewAdditionalId);
                tViewCashCard1= findViewById(R.id.tViewCashCard1);

                tilIsAvailable = findViewById(R.id.til_isavailable);
                tilWhoReleased = findViewById(R.id.til_whoreleased);
                tilPlaceReleased = findViewById(R.id.til_placereleased);
                tilCardReleased = findViewById(R.id.til_cardreleased);
                tilDistribution = findViewById(R.id.til_distribution);
                tiladditionalID = findViewById(R.id.til_additionalID);
                tilCard= findViewById(R.id.til_cashCard);
                tilSeriesNumber= findViewById(R.id.til_series_number);
                tilIsID = findViewById(R.id.til_isID);
                tilGrantee = findViewById(R.id.tilGrantee);
                tilCardBtn = findViewById(R.id.til_card);
                tilPinAttached = findViewById(R.id.til_isPinAttached);
                tilAvailablereason = findViewById(R.id.til_isavailablereason);
                tilUnclaimedReason = findViewById(R.id.til_isUnclaimedReason);
                tilRequestReplacement = findViewById(R.id.til_isRequestReplacement);
                tilOthersUnclaimedReason  = findViewById(R.id.til_OthersUnclaimedReason );
                tilRepresentativeIntroTwo = findViewById(R.id.til_representative);
                tilReasonNotPresented = findViewById(R.id.til_reason_not_presented);

                mcvPawning = findViewById(R.id.pawning);
                til_other_remarks_1 = findViewById(R.id.til_other_remarks_1);
                til_other_remarks_2 = findViewById(R.id.til_other_remarks_2);
                til_other_remarks_3 = findViewById(R.id.til_other_remarks_3);
                mPreviewCashCard = findViewById(R.id.ScannedImage);
                mPreviewGrantee = findViewById(R.id.mGrantee);
                mAdditionalID = findViewById(R.id.imgAdditionalId);
                mImgUri  = findViewById(R.id.imgUri);

                mPreviewGrantee.setClipToOutline(true);
                mPreviewCashCard.setClipToOutline(true);
                mAdditionalID.setClipToOutline(true);

                spinIsAvailReason1.setText(null);
                spinIsAvailReason1.setDropDownHeight(0);
                spinIsAvailReason1.setEnabled(false);
                spinIsAvailReason2.setText(null);
                spinIsAvailReason2.setDropDownHeight(0);
                spinIsAvailReason2.setEnabled(false);
                spinIsAvailReason3.setText(null);
                spinIsAvailReason3.setDropDownHeight(0);
                spinIsAvailReason3.setEnabled(false);

                tiladditionalID.setVisibility(View.GONE);
                btn_scanID.setVisibility(View.GONE);
                tvAdditional.setVisibility(View.GONE);
                mAdditionalID.setVisibility(View.GONE);
                mImgUri.setVisibility(View.INVISIBLE);
                tilCardReleased.setVisibility(View.GONE);
                tilWhoReleased.setVisibility(View.GONE);
                tilPlaceReleased.setVisibility(View.GONE);
                tilPinAttached.setVisibility(View.GONE);
                tilAvailablereason.setVisibility(View.GONE);
                tilUnclaimedReason.setVisibility(View.GONE);
                tilRequestReplacement.setVisibility(View.GONE);
                tilOthersUnclaimedReason.setVisibility(View.GONE);
                tilReasonNotPresented.setVisibility(View.GONE);


                dateReleased(edt_card_released);
                whoReleased(edt_who_released);
                placeReleased(edt_place_released);
                cardAvailable(spinIsAvail);
                scannedCardNumber(edt_cashCardNumber);
                cardSeries(edt_series_no);
                idExists(spinIsID);

                requestReplacement();
                unclaimedReason();
                pinAttatched();
                availableReason();
                representativeTwo();
                otherReasonForUnclaimed();
                reasonNotPresented();

                //PAWNING

                edt_lender_name = findViewById(R.id.edtLenderName);
                edt_pawning_date = findViewById(R.id.edtPawningDate);
                edt_loaned_amount = findViewById(R.id.edtLoanedAmount);
                edt_lender_address = findViewById(R.id.edtLenderAddress);
                edt_date_retrieved = findViewById(R.id.edtDateRetrieved);
                edt_interest = findViewById(R.id.edtInterest);
                spinStatus = findViewById(R.id.spinnerStatus);
                edt_pawning_reason = findViewById(R.id.edtPawningReason);
                spinOffenseHistory =findViewById(R.id.spinnerOffenseHistory);
                edt_offense_history_date = findViewById(R.id.edtOffenseHistoryDate);
                edt_pd_remarks = findViewById(R.id.edtPdRemarks);
                edt_intervention = findViewById(R.id.edtIntervention);
                edt_other_details = findViewById(R.id.edtOtherDetails);
                edt_other_remarks_1 = findViewById(R.id.edtotherRemarks1);
                edt_other_remarks_2 = findViewById(R.id.edtotherRemarks2);
                edt_other_remarks_3 = findViewById(R.id.edtotherRemarks3);

                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Status);
                ArrayAdapter<String> adapterOffenseHistory = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Offense);

                adapterStatus.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterOffenseHistory.setDropDownViewResource(simple_spinner_dropdown_item);

                spinStatus.setAdapter(adapterStatus);
                spinOffenseHistory.setAdapter(adapterOffenseHistory);

                edt_pawning_date.setFocusable(false);
                edt_pawning_date.setClickable(true);
                edt_date_retrieved.setFocusable(false);
                edt_date_retrieved.setClickable(true);
                edt_offense_history_date.setFocusable(false);
                edt_offense_history_date.setClickable(true);

                edt_pawning_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_pawning_date);
                    }
                });
                edt_date_retrieved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_date_retrieved);
                    }
                });
                edt_offense_history_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_offense_history_date);
                    }
                });
                // END OF PAWNING

                ArrayAdapter<String> adapterIsAvail = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, CardRequired);
                ArrayAdapter<String> adapterYesNoBlank = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterIsID = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, CardRequired);
                ArrayAdapter<String> adapterIsDistribution = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, distribution);
                ArrayAdapter<String> adapterIsCashCardUnclaimed = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ReasonCashCardUnclaimed);
                ArrayAdapter<String> adapterYesNo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);

                adapterIsAvail.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterYesNoBlank.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsID.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsDistribution.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsCashCardUnclaimed.setDropDownViewResource(simple_spinner_dropdown_item);

                adapterYesNo.setDropDownViewResource(simple_spinner_dropdown_item);

                spinIsAvail.setAdapter(adapterIsAvail);
                spinIsAvail1.setAdapter(adapterYesNoBlank);
                spinIsAvail2.setAdapter(adapterYesNoBlank);
                spinIsAvail3.setAdapter(adapterYesNoBlank);
                spinIsID.setAdapter(adapterIsID);
                spinIsAvailReason.setAdapter(adapterYesNo);
                spinIsAvailReason1.setAdapter(adapterYesNo);
                spinIsAvailReason2.setAdapter(adapterYesNo);
                spinIsAvailReason3.setAdapter(adapterYesNo);
                spinIsDistribution_1.setAdapter(adapterIsDistribution);
                spinnerIsUnclaimedReason.setAdapter(adapterIsCashCardUnclaimed);
                spinnerIsReasonReplacement.setAdapter(adapterIsAvail);
                spinnerIsPinAttached.setAdapter(adapterIsAvail);

                edt_card_released.setFocusable(false);
                edt_card_released.setClickable(true);
                edt_current_grantee_number.setEnabled(false);

                getImage(0);

                edt_card_released.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_card_released);
                    }
                });
                btn_cash_card.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchHomeScreen();
                    }
                });
                btn_scanID.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 102);
                    }
                });

                btn_grantee.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 103);
                    }
                });

                spinIsDistribution_1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsDistribution_1.getText().toString().matches("Unclaimed")||spinIsDistribution_1.getText().toString().matches("")) {
                            edt_card_released.setText("");
                            edt_who_released.setText("");
                            edt_place_released.setText("");
                            tilCardReleased.setVisibility(View.GONE);
                            tilWhoReleased.setVisibility(View.GONE);
                            tilPlaceReleased.setVisibility(View.GONE);

                        } else {
                            tilCardReleased.setVisibility(View.VISIBLE);
                            tilWhoReleased.setVisibility(View.VISIBLE);
                            tilPlaceReleased.setVisibility(View.VISIBLE);
                        }
                    }
                });

                spinIsID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsID.getText().toString().matches("No")||spinIsID.getText().toString().matches("")) {
                            tiladditionalID.setVisibility(View.GONE);
                            btn_scanID.setVisibility(View.GONE);
                            tvAdditional.setVisibility(View.GONE);
                            mAdditionalID.setVisibility(View.GONE);
                        } else {

                            tiladditionalID.setVisibility(View.VISIBLE);
                            btn_scanID.setVisibility(View.VISIBLE);
                            tvAdditional.setVisibility(View.VISIBLE);
                            mAdditionalID.setVisibility(View.VISIBLE);

                        }
                    }
                });

                spinIsAvail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail.getText().toString().matches("Yes")||spinIsAvail.getText().toString().matches("")) {

                            spinIsAvailReason.setText("");
                            tilAvailablereason.setVisibility(View.GONE);
                            mcvPawning.setVisibility(View.GONE);
                            tilPinAttached.setVisibility(View.VISIBLE);
                            tilUnclaimedReason.setVisibility(View.GONE);
                            tilRequestReplacement.setVisibility(View.GONE);

                        } else {
                            spinnerIsPinAttached.setText("");
                            spinIsAvailReason.setText("");
                            tilPinAttached.setVisibility(View.GONE);
                            tilAvailablereason.setVisibility(View.VISIBLE);
                        }
                    }
                });

                spinnerIsUnclaimedReason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinnerIsUnclaimedReason.getText().toString().matches("Others")){
                            edtOthersUnclaimedReason.setText("");
                            tilOthersUnclaimedReason.setVisibility(View.VISIBLE);
                        }
                        else {
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                        }
                    }
                });


                spinIsAvailReason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        clearFields();
                        if (spinIsAvailReason.getText().toString().matches("Pawned")) {
                            mcvPawning.setVisibility(View.VISIBLE);
                            tilUnclaimedReason.setVisibility(View.GONE);
                            tilRequestReplacement.setVisibility(View.GONE);
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                            tilReasonNotPresented.setVisibility(View.GONE);
                        }
                        else if (spinIsAvailReason.getText().toString().matches("Unclaimed")){
                            tilUnclaimedReason.setVisibility(View.VISIBLE);
                            mcvPawning.setVisibility(View.GONE);
                            tilRequestReplacement.setVisibility(View.GONE);
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                            tilReasonNotPresented.setVisibility(View.GONE);

                        }
                        else if (spinIsAvailReason.getText().toString().matches("Lost/Stolen")){
                            tilRequestReplacement.setVisibility(View.VISIBLE);
                            tilUnclaimedReason.setVisibility(View.GONE);
                            mcvPawning.setVisibility(View.GONE);
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                            tilReasonNotPresented.setVisibility(View.GONE);
                        }
                        else if (spinIsAvailReason.getText().toString().matches("Others")){
                            tilReasonNotPresented.setVisibility(View.VISIBLE);
                            tilRequestReplacement.setVisibility(View.GONE);
                            tilUnclaimedReason.setVisibility(View.GONE);
                            mcvPawning.setVisibility(View.GONE);
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                        }
                        else {
                            tilReasonNotPresented.setVisibility(View.GONE);
                            tilOthersUnclaimedReason.setVisibility(View.GONE);
                            mcvPawning.setVisibility(View.GONE);
                            tilUnclaimedReason.setVisibility(View.GONE);
                            tilRequestReplacement.setVisibility(View.GONE);
                        }
                    }
                });

                spinIsAvailReason1.setEnabled(false);
                spinIsAvail1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail1.getText().toString().matches("Yes")||spinIsAvail1.getText().toString().matches("")) {
                            spinIsAvailReason1.setText(null);
                            spinIsAvailReason1.setDropDownHeight(0);
                            spinIsAvailReason1.setEnabled(false);
                            edt_other_remarks_1.setText("");
                            til_other_remarks_1.setVisibility(View.GONE);
                        } else {
                            spinIsAvailReason1.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            spinIsAvailReason1.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason2.setEnabled(false);
                spinIsAvail2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail2.getText().toString().matches("Yes")||spinIsAvail2.getText().toString().matches("")) {
                            spinIsAvailReason2.setText(null);
                            spinIsAvailReason2.setDropDownHeight(0);
                            spinIsAvailReason2.setEnabled(false);
                            edt_other_remarks_2.setText("");
                            til_other_remarks_2.setVisibility(View.GONE);
                        } else {
                            spinIsAvailReason2.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            spinIsAvailReason2.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason3.setEnabled(false);
                spinIsAvail3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail3.getText().toString().matches("Yes") ||spinIsAvail3.getText().toString().matches("") ) {
                            spinIsAvailReason3.setText(null);
                            spinIsAvailReason3.setDropDownHeight(0);
                            spinIsAvailReason3.setEnabled(false);
                            edt_other_remarks_3.setText("");
                            til_other_remarks_3.setVisibility(View.GONE);
                        } else {
                            spinIsAvailReason3.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            spinIsAvailReason3.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                        if (spinIsAvailReason1.getText().toString().matches("Pawned")){
                            til_other_remarks_1.setVisibility(View.VISIBLE);
                        }
                        else{til_other_remarks_1.setVisibility(View.GONE);}

                        if (spinIsAvail3.getText().toString().matches("Yes") ||spinIsAvail3.getText().toString().matches("") ) {
                            spinIsAvailReason3.setText(null);
                            spinIsAvailReason3.setDropDownHeight(0);
                            spinIsAvailReason3.setEnabled(false);
                        } else {
                            spinIsAvailReason3.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            spinIsAvailReason3.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                        if (spinIsAvailReason2.getText().toString().matches("Pawned")){
                            til_other_remarks_2.setVisibility(View.VISIBLE);
                        }
                        else{til_other_remarks_2.setVisibility(View.GONE);}
                    }
                });

                spinIsAvailReason3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvailReason3.getText().toString().matches("Pawned")){
                            til_other_remarks_3.setVisibility(View.VISIBLE);
                        }
                        else{til_other_remarks_3.setVisibility(View.GONE);}
                    }
                });


//                spinnerGrantee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
//
//                        if (spinnerGrantee.getText().toString().matches("No")){
//                            spinnerGrantee.setFocusableInTouchMode(true);
//                            spinnerGrantee.setFocusable(true);
//                        }
//                        else{
//                            myEdit.putString("grantee_in", "");
//                            spinnerGrantee.setFocusableInTouchMode(false);
//                            spinnerGrantee.setFocusable(false);
//                        }
//                    }
//                });

                String distribution = sh.getString("distribution_status", "");
                String card_released = sh.getString("card_released", "");
                String who_released = sh.getString("who_released", "");
                String place_released = sh.getString("place_released", "");
                String temp_current_grantee_number = sh.getString("temp_current_grantee_number", "");
                String is_available = sh.getString("is_available", "");
                String is_available_reason = sh.getString("is_available_reason", "");
                String other_card_number_1 = sh.getString("other_card_number_1", "");
                String other_card_number_series_1 = sh.getString("other_card_number_series_1", "");
                String other_card_holder_name_1 = sh.getString("other_card_holder_name_1", "");
                String other_is_available_1 = sh.getString("other_is_available_1", "");
                String other_is_available_reason_1 = sh.getString("other_is_available_reason_1", "");
                String other_card_number_2 = sh.getString("other_card_number_2", "");
                String other_card_number_series_2 = sh.getString("other_card_number_series_2", "");
                String other_card_holder_name_2 = sh.getString("other_card_holder_name_2", "");
                String other_is_available_2 = sh.getString("other_is_available_2", "");
                String other_is_available_reason_2 = sh.getString("other_is_available_reason_2", "");
                String other_card_number_3 = sh.getString("other_card_number_3", "");
                String other_card_number_series_3 = sh.getString("other_card_number_series_3", "");
                String other_card_holder_name_3 = sh.getString("other_card_holder_name_3", "");
                String other_is_available_3 = sh.getString("other_is_available_3", "");
                String other_is_available_reason_3 = sh.getString("other_is_available_reason_3", "");
                String additional = sh.getString("is_additional", "");
                String scanned_cash_card = sh.getString("scanned_cash_card", "");
                String series_number = sh.getString("series_number", "");
                String representative = sh.getString("rpt_intro_two", "");
                String pin_attached = sh.getString("pin_attached", "");
                String reason_replacement = sh.getString("reason_replacement", "");
                String unclaimed_reason = sh.getString("unclaimed_reason", "");
                String reason_not_presented = sh.getString("reason_not_presented", "");
                String other_unclaimed_reason = sh.getString("other_unclaimed_reason", "");


                spinIsDistribution_1.setText(distribution,false);
                edt_card_released.setText(card_released);
                edt_who_released.setText(who_released);
                edt_place_released.setText(place_released);
                edt_current_grantee_number.setText(temp_current_grantee_number);
                spinIsAvail.setText(is_available,false);
                spinIsAvailReason.setText(is_available_reason,false);
                edt_other_card_number_1.setText(other_card_number_1);
                edt_other_card_number_series_1.setText(other_card_number_series_1);
                edt_other_card_holder_name_1.setText(other_card_holder_name_1);
                spinIsAvail1.setText(other_is_available_1,false);
                spinIsAvailReason1.setText(other_is_available_reason_1,false);
                edt_other_card_number_2.setText(other_card_number_2);
                edt_other_card_number_series_2.setText(other_card_number_series_2);
                edt_other_card_holder_name_2.setText(other_card_holder_name_2);
                spinIsAvail2.setText(other_is_available_2,false);
                spinIsAvailReason2.setText(other_is_available_reason_2,false);
                edt_other_card_number_3.setText(other_card_number_3);
                edt_other_card_number_series_3.setText(other_card_number_series_3);
                edt_other_card_holder_name_3.setText(other_card_holder_name_3);
                spinIsAvail3.setText(other_is_available_3,false);
                spinIsAvailReason3.setText(other_is_available_reason_3,false);
                spinIsID.setText(additional,false);
                edt_cashCardNumber.setText(scanned_cash_card);
                edt_series_no.setText(series_number);
                edtRepresentativeTwo.setText(representative);
                spinnerIsPinAttached.setText(pin_attached,false);
                spinnerIsReasonReplacement.setText(reason_replacement,false);
                spinnerIsUnclaimedReason.setText(unclaimed_reason,false);
                edtReasonNotPresented.setText(reason_not_presented);
                edtOthersUnclaimedReason.setText(other_unclaimed_reason);


                String lender_name = sh.getString("lender_name","");
                String pawning_date = sh.getString("pawning_date","");
                String loaned_amount = sh.getString("loaned_amount","");
                String lender_address = sh.getString("lender_address","");
                String date_retrieved = sh.getString("date_retrieved","");
                String interest = sh.getString("interest","");
                String spin_status = sh.getString("spin_status","");
                String pawning_reason = sh.getString("pawning_reason","");
                String offense_history = sh.getString("offense_history","");
                String offense_history_date = sh.getString("offense_history_date","");
                String pd_remarks = sh.getString("pd_remarks","");
                String intervention = sh.getString("intervention","");
                String other_details = sh.getString("other_details","");

                String other_remarks1 = sh.getString("other_remarks1","");
                String other_remarks2 = sh.getString("other_remarks2","");
                String other_remarks3 = sh.getString("other_remarks3","");

                edt_lender_name.setText(lender_name);
                edt_pawning_date.setText(pawning_date);
                edt_loaned_amount.setText(loaned_amount);
                edt_lender_address.setText(lender_address);
                edt_date_retrieved.setText(date_retrieved);
                edt_interest.setText(interest);
                spinStatus.setText(spin_status,false);
                edt_pawning_reason.setText(pawning_reason);
                spinOffenseHistory.setText(offense_history,false);
                edt_offense_history_date.setText(offense_history_date);
                edt_pd_remarks.setText(pd_remarks);
                edt_intervention.setText(intervention);
                edt_other_details.setText(other_details);

                edt_other_remarks_1.setText(other_remarks1);
                edt_other_remarks_2.setText(other_remarks2);
                edt_other_remarks_3.setText(other_remarks3);


                if (spinnerIsUnclaimedReason.getText().toString().matches("Others")) {
                    tilOthersUnclaimedReason.setVisibility(View.VISIBLE);
                }
                else {
                    tilOthersUnclaimedReason.setVisibility(View.GONE);
                }

                if (spinIsDistribution_1.getText().toString().matches("Claimed")) {
                    tilCardReleased.setVisibility(View.VISIBLE);
                    tilWhoReleased.setVisibility(View.VISIBLE);
                    tilPlaceReleased.setVisibility(View.VISIBLE);
                }
                else {
                    tilCardReleased.setVisibility(View.GONE);
                    tilWhoReleased.setVisibility(View.GONE);
                    tilPlaceReleased.setVisibility(View.GONE);
                }

                if (spinIsAvail.getText().toString().matches("No")) {
                    tilPinAttached.setVisibility(View.GONE);
                    tilAvailablereason.setVisibility(View.VISIBLE);
                }
                else {
                    tilPinAttached.setVisibility(View.VISIBLE);
                    tilAvailablereason.setVisibility(View.GONE);
                }

                if (spinIsID.getText().toString().matches("Yes")) {
                    tiladditionalID.setVisibility(View.VISIBLE);
                    btn_scanID.setVisibility(View.VISIBLE);
                    tvAdditional.setVisibility(View.VISIBLE);
                    mAdditionalID.setVisibility(View.VISIBLE);
                }
                if (spinIsAvailReason.getText().toString().matches("Pawned")){
                    spinnerIsReasonReplacement.setText("");
                    mcvPawning.setVisibility(View.VISIBLE);


                }
                else if (spinIsAvailReason.getText().toString().matches("Lost/Stolen")){
                    tilRequestReplacement.setVisibility(View.VISIBLE);
                    tilUnclaimedReason.setVisibility(View.GONE);
                    mcvPawning.setVisibility(View.GONE);
                    tilReasonNotPresented.setVisibility(View.GONE);
                }
                else if (spinIsAvailReason.getText().toString().matches("Unclaimed")){
                    tilReasonNotPresented.setVisibility(View.GONE);
                    tilUnclaimedReason.setVisibility(View.VISIBLE);
                    mcvPawning.setVisibility(View.GONE);
                    tilRequestReplacement.setVisibility(View.GONE);
                    spinnerIsReasonReplacement.setText("");
                }
                else if (spinIsAvailReason.getText().toString().matches("Others")){
                    tilReasonNotPresented.setVisibility(View.VISIBLE);
                    mcvPawning.setVisibility(View.GONE);
                    tilRequestReplacement.setVisibility(View.GONE);
                    tilUnclaimedReason.setVisibility(View.GONE);
                }
                else{mcvPawning.setVisibility(View.GONE);}

                if (spinIsAvailReason1.getText().toString().matches("Pawned")){
                    til_other_remarks_1.setVisibility(View.VISIBLE);
                }
                else{til_other_remarks_1.setVisibility(View.GONE);}

                if (spinIsAvailReason2.getText().toString().matches("Pawned")){
                    til_other_remarks_2.setVisibility(View.VISIBLE);
                }
                else{til_other_remarks_2.setVisibility(View.GONE);}

                if (spinIsAvailReason3.getText().toString().matches("Pawned")){
                    til_other_remarks_3.setVisibility(View.VISIBLE);
                }
                else{til_other_remarks_3.setVisibility(View.GONE);}









            } else if (position == 2) {
                //intro_three.xml
                tilDateWithdrawn = findViewById(R.id.til_datewithdrawn);
                edt_nma_amount = findViewById(R.id.edtNmaAmount);
                edt_nma_reason = findViewById(R.id.edtNmaReason);
                edt_date_withdrawn = findViewById(R.id.edtDateWithdrawn);
                edt_remarks = findViewById(R.id.edtRemarks);
                edt_date_withdrawn.setFocusable(false);
                edt_date_withdrawn.setClickable(true);
                tilDateWithdrawn.setVisibility(View.GONE);
                edt_date_withdrawn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_date_withdrawn);
                    }
                });
                String nma_amount = sh.getString("nma_amount", "");
                String nma_reason = sh.getString("nma_reason", "");
                String date_withdrawn = sh.getString("date_withdrawn", "");
                String remarks = sh.getString("remarks", "");
                edt_nma_amount.setText(nma_amount);
                edt_nma_reason.setText(nma_reason);
                edt_date_withdrawn.setText(date_withdrawn);
                edt_remarks.setText(remarks);

            } else if (position == 3) {
                //intro_four.xml
//                edt_lender_name = findViewById(R.id.edtLenderName);
//                edt_pawning_date = findViewById(R.id.edtPawningDate);
//                edt_loaned_amount = findViewById(R.id.edtLoanedAmount);
//                edt_lender_address = findViewById(R.id.edtLenderAddress);
//                edt_date_retrieved = findViewById(R.id.edtDateRetrieved);
//                edt_interest = findViewById(R.id.edtInterest);
//                spinStatus = findViewById(R.id.spinnerStatus);
//                edt_pawning_reason = findViewById(R.id.edtPawningReason);
//                spinOffenseHistory =findViewById(R.id.spinnerOffenseHistory);
//                edt_offense_history_date = findViewById(R.id.edtOffenseHistoryDate);
//                edt_pd_remarks = findViewById(R.id.edtPdRemarks);
//                edt_intervention = findViewById(R.id.edtIntervention);
//                edt_other_details = findViewById(R.id.edtOtherDetails);
//
//                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Status);
//                ArrayAdapter<String> adapterOffenseHistory = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Offense);
//
//                adapterStatus.setDropDownViewResource(simple_spinner_dropdown_item);
//                adapterOffenseHistory.setDropDownViewResource(simple_spinner_dropdown_item);
//
//                spinStatus.setAdapter(adapterStatus);
//                spinOffenseHistory.setAdapter(adapterOffenseHistory);
//
//                edt_pawning_date.setFocusable(false);
//                edt_pawning_date.setClickable(true);
//                edt_date_retrieved.setFocusable(false);
//                edt_date_retrieved.setClickable(true);
//                edt_offense_history_date.setFocusable(false);
//                edt_offense_history_date.setClickable(true);
//
//                edt_pawning_date.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDateDialog(edt_pawning_date);
//                    }
//                });
//                edt_date_retrieved.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDateDialog(edt_date_retrieved);
//                    }
//                });
//                edt_offense_history_date.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showDateDialog(edt_offense_history_date);
//                    }
//                });
//
//                String lender_name = sh.getString("lender_name","");
//                String pawning_date = sh.getString("pawning_date","");
//                String loaned_amount = sh.getString("loaned_amount","");
//                String lender_address = sh.getString("lender_address","");
//                String date_retrieved = sh.getString("date_retrieved","");
//                String interest = sh.getString("interest","");
//                String spin_status = sh.getString("spin_status","");
//                String pawning_reason = sh.getString("pawning_reason","");
//                String offense_history = sh.getString("offense_history","");
//                String offense_history_date = sh.getString("offense_history_date","");
//                String pd_remarks = sh.getString("pd_remarks","");
//                String intervention = sh.getString("intervention","");
//                String other_details = sh.getString("other_details","");
//
//                edt_lender_name.setText(lender_name);
//                edt_pawning_date.setText(pawning_date);
//                edt_loaned_amount.setText(loaned_amount);
//                edt_lender_address.setText(lender_address);
//                edt_date_retrieved.setText(date_retrieved);
//                edt_interest.setText(interest);
//                spinStatus.setText(spin_status,false);
//                edt_pawning_reason.setText(pawning_reason);
//                spinOffenseHistory.setText(offense_history,false);
//                edt_offense_history_date.setText(offense_history_date);
//                edt_pd_remarks.setText(pd_remarks);
//                edt_intervention.setText(intervention);
//                edt_other_details.setText(other_details);
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        showImageImportDialog();
    }

    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(ScanCashCard.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void nextValidation(){

        pressNext =true;
        isValidationError = 0;
        required_field = "This field is required!";

        int current = getItem(1);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

        edt_lender_name = findViewById(R.id.edtLenderName);

        if (current == 1) {

            pressNext =false;
            Log.v(ContentValues.TAG,"DAPAT DRIA");

            edt_hh = findViewById(R.id.edtHhId);
            edt_fullname = findViewById(R.id.edtFullname);
            spinClientStatus = findViewById(R.id.spinnerClientStatus);
            edt_address = findViewById(R.id.edtAddress);
            spinSex = findViewById(R.id.spinnerSex);
            edt_set = findViewById(R.id.edtSet);
            edt_assigned = findViewById(R.id.edtAssigned);
            spinAnswer = findViewById(R.id.spinnerMinorGrantee);
            edt_contact_no = findViewById(R.id.edtContactNo);
            edtRepresentativeOne = findViewById(R.id.edtRepresentativeOne);
            edt_assigned = findViewById(R.id.edtAssigned);
            spinnerContactNoOf = findViewById(R.id.spinnerContactNoOf);
            edtRepresentativeTwo= findViewById(R.id.edtRepresentativeTwo);


            String household = edt_hh.getText().toString();
            String full_name = edt_fullname.getText().toString();
            String client_status = spinClientStatus.getText().toString();
            String address = edt_address.getText().toString();
            String sex = spinSex.getText().toString();
            String hh_set = edt_set.getText().toString();
            String contact_no = edt_contact_no.getText().toString();
            String contact_no_of = spinnerContactNoOf.getText().toString();
            String representative = edtRepresentativeOne.getText().toString();
            String grantee = spinnerGrantee.getText().toString();


            tilHhId = findViewById(R.id.til_hhid);
            tilFullname = findViewById(R.id.til_fullname);
            tilClientStatus = findViewById(R.id.til_clientstatus);
            tilAddress = findViewById(R.id.til_address);
            tilSex = findViewById(R.id.til_sex);
            tilSet = findViewById(R.id.til_set);
            tilContactNo = findViewById(R.id.til_contact_no);
            tilContactNoOf = findViewById(R.id.til_contact_no_of);
            tilRepresentativeIntroOne = findViewById(R.id.til_representative_one);
            tilRepresentativeIntroTwo = findViewById(R.id.til_representative);
            tilGranteeOne = findViewById(R.id.til_grantee);


            if (household.matches("")){
                tilHhId.setError(required_field);
                isValidationError++;
            } else {
                tilHhId.setError(null);
            }


            if (contact_no_of.matches("")){
                tilContactNoOf.setError(required_field);
                isValidationError++;
            } else {
                tilContactNoOf.setError(null);
            }

            if (contact_no_of.matches("Grantee")){tilRepresentativeIntroOne.setError(null);}
            else{
                if (representative.matches("")){
                    tilRepresentativeIntroOne.setError(required_field);
                    isValidationError++;
                } else {
                    tilRepresentativeIntroOne.setError(null);
                }
            }

            if (full_name.matches("")){
                tilFullname.setError(required_field);
                isValidationError++;
            } else {
                tilFullname.setError(null);
            }

            if (client_status.matches("")){
                tilClientStatus.setError(required_field);
                isValidationError++;
            } else {
                tilClientStatus.setError(null);
            }

            if (address.matches("")){
                tilAddress.setError(required_field);
                isValidationError++;
            } else {
                tilAddress.setError(null);
            }

            if (sex.matches("")){
                tilSex.setError(required_field);
                isValidationError++;
            } else {
                tilSex.setError(null);
            }

            if (hh_set.matches("")){
                tilSet.setError(required_field);
                isValidationError++;
            } else {
                tilSet.setError(null);
            }

            if (contact_no.matches("")){
                tilContactNo.setError(required_field);
                isValidationError++;
            }
            else if (contact_no.length()!=10){
                tilContactNo.setError(required_field);
                isValidationError++;
            }
            else {
                tilContactNo.setError(null);
            }

            if (grantee.length()==0){
                tilGranteeOne.setError(required_field);
                isValidationError++;
            } else {
                tilGranteeOne.setError(null);
            }


            if (isValidationError > 0){}else{MANDATORY_PAGE_LOCATION++;}

            store_preferences(1);
            String grantee_in = sh.getString("grantee_in", "");
            if (grantee_in.matches("No")){tilRepresentativeIntroTwo.setVisibility(View.VISIBLE);}
            else {
                edtRepresentativeTwo.setText("");
                tilRepresentativeIntroTwo.setVisibility(View.GONE); }

        } else if (current == 2) {
            pressNext =false;
            spinIsDistribution_1 = findViewById(R.id.spinnerDistribution);
            edt_card_released = findViewById(R.id.edtCardReleased);
            edt_who_released = findViewById(R.id.edtWhoReleased);
            edt_place_released = findViewById(R.id.edtPlaceReleased);
            edt_current_grantee_number = findViewById(R.id.edtCurrentGranteeNumber);
            spinnerIsPinAttached = findViewById(R.id.spinnerIsPinAttached);
            edtOthersUnclaimedReason= findViewById(R.id.edtOthersUnclaimedReason);

            spinIsAvail = findViewById(R.id.spinnerIsAvailable);
            spinIsAvailReason = findViewById(R.id.spinnerIsAvailableReason);
            edt_other_card_number_1 = findViewById(R.id.edtOtherCardNumber1);
            edt_other_card_holder_name_1 = findViewById(R.id.edtOtherCardHolderName1);
            spinIsAvail1 = findViewById(R.id.spinnerOtherIsAvailable1);
            spinIsAvailReason1 = findViewById(R.id.spinnerOtherIsAvailableReason1);
            edt_other_card_number_2 = findViewById(R.id.edtOtherCardNumber2);
            edt_other_card_holder_name_2 = findViewById(R.id.edtOtherCardHolderName2);
            spinIsAvail2 = findViewById(R.id.spinnerOtherIsAvailable2);
            spinIsAvailReason2 = findViewById(R.id.spinnerOtherIsAvailableReason2);
            edt_other_card_number_3 = findViewById(R.id.edtOtherCardNumber3);
            edt_other_card_holder_name_3 = findViewById(R.id.edtOtherCardHolderName3);
            spinIsAvail3 = findViewById(R.id.spinnerOtherIsAvailable3);
            spinIsAvailReason3 = findViewById(R.id.spinnerOtherIsAvailableReason3);
            edt_other_card_number_series_1 = findViewById(R.id.edtOtherCardNumberSeries1);
            edt_other_card_number_series_2 = findViewById(R.id.edtOtherCardNumberSeries2);
            edt_other_card_number_series_3 = findViewById(R.id.edtOtherCardNumberSeries3);
            edt_cashCardNumber = findViewById(R.id.edt_cashCardNumber);
            edt_series_no = findViewById(R.id.edt_series_no);
            edtRepresentativeTwo= findViewById(R.id.edtRepresentativeTwo);
            spinnerIsReasonReplacement= findViewById(R.id.spinnerIsReasonReplacement);
            spinnerIsUnclaimedReason = findViewById(R.id.spinnerIsUnclaimedReason);
            edtReasonNotPresented  = findViewById(R.id.edtReasonNotPresented);

            String distribution = spinIsDistribution_1.getText().toString();
            String card_released = edt_card_released.getText().toString();
            String who_released = edt_who_released.getText().toString();
            String place_released = edt_place_released.getText().toString();
            String is_available = spinIsAvail.getText().toString();
            String representative = edtRepresentativeTwo.getText().toString();
            String reason_not_presented = spinIsAvailReason.getText().toString();
            String scanned_cash_card = edt_cashCardNumber.getText().toString();
            String series_number = edt_series_no.getText().toString();
            String spinnerIsID = spinIsID.getText().toString();
            String spnPinAttached =spinnerIsPinAttached.getText().toString();
            String spnReasonReplacement = spinnerIsReasonReplacement.getText().toString();
            String spnUnclaimedReason = spinnerIsUnclaimedReason.getText().toString();
            String reasonNotPresented = edtReasonNotPresented.getText().toString();
            String reasonNotUnclaimed = edtOthersUnclaimedReason.getText().toString();

            tilDistribution = findViewById(R.id.til_distribution);
            tilCardReleased = findViewById(R.id.til_cardreleased);
            tilWhoReleased = findViewById(R.id.til_whoreleased);
            tilPlaceReleased = findViewById(R.id.til_placereleased);
            tilCurrentGranteeNumber = findViewById(R.id.til_currentgranteenumber);
            tilIsAvailable = findViewById(R.id.til_isavailable);
            tiladditionalID = findViewById(R.id.til_additionalID);
            tilCard = findViewById(R.id.til_cashCard);
            tilCardBtn = findViewById(R.id.til_card);
            tilSeriesNumber = findViewById(R.id.til_series_number);
            tilIsID = findViewById(R.id.til_isID);
            tilGrantee = findViewById(R.id.tilGrantee);
            tilRepresentativeIntroTwo = findViewById(R.id.til_representative);
            tilAvailablereason = findViewById(R.id.til_isavailablereason);
            tilisPinAttached = findViewById(R.id.til_isPinAttached);
            tilRequestReplacement = findViewById(R.id.til_isRequestReplacement);
            tilUnclaimedReason = findViewById(R.id.til_isUnclaimedReason);
            tilReasonNotPresented = findViewById(R.id.til_reason_not_presented);
            tilOthersUnclaimedReason = findViewById(R.id.til_OthersUnclaimedReason );

            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT scanned_e_image,additional_id_image,grantee_e_image FROM tmp_blob WHERE id=1");
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()) {
                        byte[] scanned_image = cursor.getBlob(0);
                        byte[] additional_image = cursor.getBlob(1);
                        byte[] grantee_image = cursor.getBlob(2);
                        if (scanned_image != null) {
                            Bitmap scanned = BitmapFactory.decodeByteArray(scanned_image, 0, scanned_image.length);
                            mPreviewCashCard.setImageBitmap(scanned);
                            tilCard.setError(null);
                        }
                        else{
                            isValidationError++;
                            tilCard.setError(required_field);
                        }

                        if (spinnerIsID.matches("Yes")){
                            if (additional_image != null) {
                                Bitmap additional = BitmapFactory.decodeByteArray(additional_image, 0, additional_image.length);
                                mAdditionalID.setImageBitmap(additional);
                                tiladditionalID.setError(null);
                            }
                            else{
                            tiladditionalID.setError(required_field);
                            isValidationError++;
                            }
                        }
                        else {
                            tiladditionalID.setError(null);
                        }

                        if (grantee_image != null) {
                            Bitmap grantee = BitmapFactory.decodeByteArray(grantee_image, 0, grantee_image.length);
                            mPreviewGrantee.setImageBitmap(grantee);
                            tilGrantee.setError(null);
                        }
                        else{
                            tilGrantee.setError(required_field);
                            isValidationError++;
                        }
                    }
                }
                else{
                    tilCardBtn.setError(required_field);
                    tiladditionalID.setError(required_field);
                    tilGrantee.setError(required_field);
                }
            }
            catch (Exception e){
                Log.v(TAG,"Errors " + e);
            }


            if (distribution.matches("Unclaimed")){tilDistribution.setError(null);edt_card_released.setText("");edt_who_released.setText("");edt_place_released.setText("");}
            else{
                if (distribution.matches("")){
                    tilDistribution.setError(required_field);
                    isValidationError++;
                } else {
                    tilDistribution.setError(null);
                    if (card_released.matches("")) {
                        tilCardReleased.setError(required_field);
                        isValidationError++;
                    } else {
                        tilCardReleased.setError(null);
                    }

                    if (who_released.matches("")) {
                        tilWhoReleased.setError(required_field);
                        isValidationError++;
                    } else {
                        tilWhoReleased.setError(null);
                    }

                    if (place_released.matches("")) {
                        tilPlaceReleased.setError(required_field);
                        isValidationError++;
                    } else {
                        tilPlaceReleased.setError(null);
                    }
                }
            }

            if (is_available.matches("")) {
                tilIsAvailable.setError(required_field);
                isValidationError++;
            }
            else if (is_available.matches("Yes")){
                if (spnPinAttached.matches("")) {
                    tilisPinAttached.setError(required_field);
                    isValidationError++;
                } else {
                    tilisPinAttached.setError(null);
                }
            }
            else if (is_available.matches("No")){
                if (reason_not_presented.matches("")){
                    tilAvailablereason.setError(required_field);
                    isValidationError++;
                }
                else if (reason_not_presented.matches("Lost/Stolen")) {
                    if (spnReasonReplacement.matches("")) {
                        tilRequestReplacement.setError(required_field);
                        isValidationError++;
                    } else {
                        tilRequestReplacement.setError(null);
                    }
                }
                else if (reason_not_presented.matches("Unclaimed")){
                    if (spnUnclaimedReason.matches("")) {
                        tilUnclaimedReason.setError(required_field);
                        isValidationError++;
                    }
                    else if (spnUnclaimedReason.matches("Others")){
                        if (reasonNotUnclaimed.matches("")){
                            tilOthersUnclaimedReason.setError(required_field);
                            isValidationError++;
                        }
                        else {
                            tilOthersUnclaimedReason.setError(null);
                        }
                    }
                    else {
                        tilUnclaimedReason.setError(null);
                    }
                }
                else if (reason_not_presented.matches("Others")){
                    if (reasonNotPresented.matches("")){
                        tilReasonNotPresented.setError(required_field);
                        isValidationError++;
                    }
                    else {
                        tilReasonNotPresented.setError(null);
                    }

                }
            }
            else {
                tilIsAvailable.setError(null);
            }




            String grantee = sh.getString("grantee_in", "");
            if (grantee.matches("No")){
                if (representative.matches("")) {
                    tilRepresentativeIntroTwo.setError(required_field);
                    isValidationError++;
                } else {
                    tilRepresentativeIntroTwo.setError(null);
                }
            }

            if (scanned_cash_card.matches("")) {
                tilCard.setError(required_field);
                isValidationError++;
            }
            else if (!scanned_cash_card.matches("[0-9 ]+")){
                tilCard.setError("Invalid format");
                isValidationError++;
            }
            else {
                tilCard.setError(null);
            }

            if (series_number.matches("")) {
                tilSeriesNumber.setError(required_field);
                isValidationError++;
            } else {
                tilSeriesNumber.setError(null);
            }

            if (spinnerIsID.matches("")) {
                tilIsID.setError(required_field);
                isValidationError++;
            } else {
                tilIsID.setError(null);
            }

            if (isValidationError > 0){}else{MANDATORY_PAGE_LOCATION++;}
            store_preferences(2);

        }
        else if (current == 3) {
            MANDATORY_PAGE_LOCATION++;
            pressNext =false;
            store_preferences(3);
        } else {
            Log.v(ContentValues.TAG,"Error Current Btn Next");
        }

        if (isValidationError > 0) {
            Toasty.warning(getApplicationContext(), "Please fill-in all required fields!", Toast.LENGTH_SHORT).show();
        } else {
            Toasty.success(getApplicationContext(), "All fields are valid!", Toast.LENGTH_SHORT).show();
            if (current < layouts.length) {
                tvPrev.setVisibility(View.VISIBLE);
                viewPager.setCurrentItem(current);
            }
            else {
                store_preferences(4);
                launchHomeScreen();
            }
        }
    }


    public void store_preferences(int pos) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        switch(pos) {
            case 1:
                edt_hh = findViewById(R.id.edtHhId);
                edt_fullname = findViewById(R.id.edtFullname);
                spinClientStatus = findViewById(R.id.spinnerClientStatus);
                edt_address = findViewById(R.id.edtAddress);
                spinSex = findViewById(R.id.spinnerSex);
                edt_set = findViewById(R.id.edtSet);
                edt_contact_no = findViewById(R.id.edtContactNo);
                edt_assigned = findViewById(R.id.edtAssigned);
                spinAnswer = findViewById(R.id.spinnerMinorGrantee);
                spinnerContactNoOf = findViewById(R.id.spinnerContactNoOf);
                spinnerGrantee = findViewById(R.id.spinnerGrantee);


                String household = edt_hh.getText().toString();
                String full_name = edt_fullname.getText().toString();
                String client_status = spinClientStatus.getText().toString();
                String address = edt_address.getText().toString();
                String sex = spinSex.getText().toString();
                String hh_set = edt_set.getText().toString();
                String contact_no = edt_contact_no.getText().toString();
                String assigned = edt_assigned.getText().toString();
                String minor_grantee = spinAnswer.getText().toString();
                String contact_no_of = spinnerContactNoOf.getText().toString();
                String grantee = spinnerGrantee.getText().toString();
                String representativeOne = edtRepresentativeOne.getText().toString();

                myEdit.putString("hh_id", household);
                myEdit.putString("full_name", full_name);
                myEdit.putString("client_status", client_status);
                myEdit.putString("address", address);
                myEdit.putString("sex", sex);
                myEdit.putString("hh_set_group", hh_set);
                myEdit.putString("contact_no", contact_no);
                myEdit.putString("contact_no_of", contact_no_of);
                myEdit.putString("assigned", assigned);
                myEdit.putString("minor_grantee", minor_grantee);
                myEdit.putString("Informant_Identifier", full_name); //do not delete this line
                myEdit.putString("pressBtn_search", "true");
                myEdit.putString("grantee_in", grantee);
                myEdit.putString("rpt_intro_one", representativeOne);
                myEdit.commit();
                break;
            case 2:
                spinIsDistribution_1 = findViewById(R.id.spinnerDistribution);
                edt_card_released = findViewById(R.id.edtCardReleased);
                edt_who_released = findViewById(R.id.edtWhoReleased);
                edt_place_released = findViewById(R.id.edtPlaceReleased);
                edt_current_grantee_number = findViewById(R.id.edtCurrentGranteeNumber);
                spinIsAvail = findViewById(R.id.spinnerIsAvailable);
                spinIsAvailReason = findViewById(R.id.spinnerIsAvailableReason);
                edt_other_card_number_1 = findViewById(R.id.edtOtherCardNumber1);
                edt_other_card_holder_name_1 = findViewById(R.id.edtOtherCardHolderName1);
                spinIsAvail1 = findViewById(R.id.spinnerOtherIsAvailable1);
                spinIsAvailReason1 = findViewById(R.id.spinnerOtherIsAvailableReason1);
                edt_other_card_number_2 = findViewById(R.id.edtOtherCardNumber2);
                edt_other_card_holder_name_2 = findViewById(R.id.edtOtherCardHolderName2);
                spinIsAvail2 = findViewById(R.id.spinnerOtherIsAvailable2);
                spinIsAvailReason2 = findViewById(R.id.spinnerOtherIsAvailableReason2);
                edt_other_card_number_3 = findViewById(R.id.edtOtherCardNumber3);
                edt_other_card_holder_name_3 = findViewById(R.id.edtOtherCardHolderName3);
                spinIsAvail3 = findViewById(R.id.spinnerOtherIsAvailable3);
                spinIsAvailReason3 = findViewById(R.id.spinnerOtherIsAvailableReason3);
                edt_other_card_number_series_1 = findViewById(R.id.edtOtherCardNumberSeries1);
                edt_other_card_number_series_2 = findViewById(R.id.edtOtherCardNumberSeries2);
                edt_other_card_number_series_3 = findViewById(R.id.edtOtherCardNumberSeries3);
                edtRepresentativeOne = findViewById(R.id.edtRepresentativeOne);
                edt_other_remarks_1 = findViewById(R.id.edtotherRemarks1);
                edt_other_remarks_2 = findViewById(R.id.edtotherRemarks2);
                edt_other_remarks_3 = findViewById(R.id.edtotherRemarks3);
                spinIsID = findViewById(R.id.spinnerIsID);
                edt_cashCardNumber = findViewById(R.id.edt_cashCardNumber);
                edt_series_no = findViewById(R.id.edt_series_no);
                spinnerIsPinAttached = findViewById(R.id.spinnerIsPinAttached);
                spinnerIsReasonReplacement = findViewById(R.id.spinnerIsReasonReplacement);
                spinnerIsUnclaimedReason = findViewById(R.id.spinnerIsUnclaimedReason);
                edtReasonNotPresented = findViewById(R.id.edtReasonNotPresented);
                edtOthersUnclaimedReason = findViewById(R.id.edtOthersUnclaimedReason);

                String distribution = spinIsDistribution_1.getText().toString();
                String card_released = edt_card_released.getText().toString();
                String who_released = edt_who_released.getText().toString();
                String place_released = edt_place_released.getText().toString();
                String current_grantee_number = edt_current_grantee_number.getText().toString();
                String is_available = spinIsAvail.getText().toString();
                String is_available_reason = spinIsAvailReason.getText().toString();
                String other_card_number_1 = edt_other_card_number_1.getText().toString();
                String other_card_holder_name_1 = edt_other_card_holder_name_1.getText().toString();
                String other_is_available_1 = spinIsAvail1.getText().toString();
                String other_is_available_reason_1 = spinIsAvailReason1.getText().toString();
                String other_card_number_2 = edt_other_card_number_2.getText().toString();
                String other_card_holder_name_2 = edt_other_card_holder_name_2.getText().toString();
                String other_is_available_2 = spinIsAvail2.getText().toString();
                String other_is_available_reason_2 = spinIsAvailReason2.getText().toString();
                String other_card_number_3 = edt_other_card_number_3.getText().toString();
                String other_card_holder_name_3 = edt_other_card_holder_name_3.getText().toString();
                String other_is_available_3 = spinIsAvail3.getText().toString();
                String other_is_available_reason_3 = spinIsAvailReason3.getText().toString();
                String other_card_number_series_1 = edt_other_card_number_series_1.getText().toString();
                String other_card_number_series_2 = edt_other_card_number_series_2.getText().toString();
                String other_card_number_series_3 = edt_other_card_number_series_3.getText().toString();
                String representative = edtRepresentativeTwo.getText().toString();
                String pin_attached = spinnerIsPinAttached.getText().toString();
                String reason_replacement = spinnerIsReasonReplacement.getText().toString();
                String unclaimed_reason = spinnerIsUnclaimedReason.getText().toString();
                String reason_not_presented = edtReasonNotPresented.getText().toString();
                String other_unclaimed_reason = edtOthersUnclaimedReason.getText().toString();



                String spinAdditionalID = spinIsID.getText().toString();
                String cash_card = edt_cashCardNumber.getText().toString();
                String series_number = edt_series_no.getText().toString();
                String other_remarks_1 = edt_other_remarks_1.getText().toString();
                String other_remarks_2 = edt_other_remarks_2.getText().toString();
                String other_remarks_3 = edt_other_remarks_3.getText().toString();

                //PAWNING

                edt_lender_name = findViewById(R.id.edtLenderName);
                edt_pawning_date = findViewById(R.id.edtPawningDate);
                edt_loaned_amount = findViewById(R.id.edtLoanedAmount);
                edt_lender_address = findViewById(R.id.edtLenderAddress);
                edt_date_retrieved = findViewById(R.id.edtDateRetrieved);
                edt_interest = findViewById(R.id.edtInterest);
                spinStatus = findViewById(R.id.spinnerStatus);
                edt_pawning_reason = findViewById(R.id.edtPawningReason);
                spinOffenseHistory =findViewById(R.id.spinnerOffenseHistory);
                edt_offense_history_date = findViewById(R.id.edtOffenseHistoryDate);
                edt_pd_remarks = findViewById(R.id.edtPdRemarks);
                edt_intervention = findViewById(R.id.edtIntervention);
                edt_other_details = findViewById(R.id.edtOtherDetails);

                String lender_name = edt_lender_name.getText().toString();
                String pawning_date = edt_pawning_date.getText().toString();
                String loaned_amount = edt_loaned_amount.getText().toString();
                String lender_address = edt_lender_address.getText().toString();
                String date_retrieved = edt_date_retrieved.getText().toString();
                String interest = edt_interest.getText().toString();
                String spin_status = spinStatus.getText().toString();
                String pawning_reason = edt_pawning_reason.getText().toString();
                String offense_history = spinOffenseHistory.getText().toString();
                String offense_history_date = edt_offense_history_date.getText().toString();
                String pd_remarks = edt_pd_remarks.getText().toString();
                String intervention = edt_intervention.getText().toString();
                String other_details = edt_other_details.getText().toString();


                myEdit.putString("distribution_status", distribution);
                myEdit.putString("card_released", card_released);
                myEdit.putString("who_released", who_released);
                myEdit.putString("place_released", place_released);
                myEdit.putString("temp_current_grantee_number", current_grantee_number);
                myEdit.putString("is_available", is_available);
                myEdit.putString("is_available_reason", is_available_reason);
                myEdit.putString("other_card_number_1", other_card_number_1);
                myEdit.putString("other_card_holder_name_1", other_card_holder_name_1);
                myEdit.putString("other_is_available_1", other_is_available_1);
                myEdit.putString("other_is_available_reason_1", other_is_available_reason_1);
                myEdit.putString("other_card_number_2", other_card_number_2);
                myEdit.putString("other_card_holder_name_2", other_card_holder_name_2);
                myEdit.putString("other_is_available_2", other_is_available_2);
                myEdit.putString("other_is_available_reason_2", other_is_available_reason_2);
                myEdit.putString("other_card_number_3", other_card_number_3);
                myEdit.putString("other_card_holder_name_3", other_card_holder_name_3);
                myEdit.putString("other_is_available_3", other_is_available_3);
                myEdit.putString("other_is_available_reason_3", other_is_available_reason_3);
                myEdit.putString("other_card_number_series_1", other_card_number_series_1);
                myEdit.putString("other_card_number_series_2", other_card_number_series_2);
                myEdit.putString("other_card_number_series_3", other_card_number_series_3);
                myEdit.putString("is_additional", spinAdditionalID);
                myEdit.putString("scanned_cash_card", cash_card);
                myEdit.putString("series_number", series_number);
                myEdit.putString("other_remarks1", other_remarks_1);
                myEdit.putString("other_remarks2", other_remarks_2);
                myEdit.putString("other_remarks3", other_remarks_3);
                myEdit.putString("rpt_intro_two", representative);
                myEdit.putString("pin_attached", pin_attached);
                myEdit.putString("reason_replacement", reason_replacement);
                myEdit.putString("unclaimed_reason", unclaimed_reason);
                myEdit.putString("reason_not_presented", reason_not_presented);
                myEdit.putString("other_unclaimed_reason", other_unclaimed_reason);


                //PAWNING
                myEdit.putString("lender_name", lender_name);
                myEdit.putString("pawning_date", pawning_date);
                myEdit.putString("loaned_amount", loaned_amount);
                myEdit.putString("lender_address", lender_address);
                myEdit.putString("date_retrieved", date_retrieved);
                myEdit.putString("interest", interest);
                myEdit.putString("spin_status", spin_status);
                myEdit.putString("pawning_reason", pawning_reason);
                myEdit.putString("offense_history", offense_history);
                myEdit.putString("offense_history_date", offense_history_date);
                myEdit.putString("pd_remarks", pd_remarks);
                myEdit.putString("intervention", intervention);
                myEdit.putString("other_details", other_details);
                myEdit.commit();
                break;
            case 3:
                edt_nma_amount = findViewById(R.id.edtNmaAmount);
                edt_nma_reason = findViewById(R.id.edtNmaReason);
                edt_date_withdrawn = findViewById(R.id.edtDateWithdrawn);
                edt_remarks = findViewById(R.id.edtRemarks);

                String nma_amount = edt_nma_amount.getText().toString();
                String nma_reason = edt_nma_reason.getText().toString();
                String date_withdrawn = edt_date_withdrawn.getText().toString();
                String remarks = edt_remarks.getText().toString();

                myEdit.putString("nma_amount", nma_amount);
                myEdit.putString("nma_reason", nma_reason);
                myEdit.putString("date_withdrawn", date_withdrawn);
                myEdit.putString("remarks", remarks);
                myEdit.commit();
                break;
//            case 4:
//                edt_pawning_date = findViewById(R.id.edtPawningDate);
//                edt_loaned_amount = findViewById(R.id.edtLoanedAmount);
//                edt_lender_address = findViewById(R.id.edtLenderAddress);
//                edt_date_retrieved = findViewById(R.id.edtDateRetrieved);
//                edt_interest = findViewById(R.id.edtInterest);
//                spinStatus = findViewById(R.id.spinnerStatus);
//                edt_pawning_reason = findViewById(R.id.edtPawningReason);
//                spinOffenseHistory =findViewById(R.id.spinnerOffenseHistory);
//                edt_offense_history_date = findViewById(R.id.edtOffenseHistoryDate);
//                edt_pd_remarks = findViewById(R.id.edtPdRemarks);
//                edt_intervention = findViewById(R.id.edtIntervention);
//                edt_other_details = findViewById(R.id.edtOtherDetails);
//
//                String lender_name = edt_lender_name.getText().toString();
//                String pawning_date = edt_pawning_date.getText().toString();
//                String loaned_amount = edt_loaned_amount.getText().toString();
//                String lender_address = edt_lender_address.getText().toString();
//                String date_retrieved = edt_date_retrieved.getText().toString();
//                String interest = edt_interest.getText().toString();
//                String spin_status = spinStatus.getText().toString();
//                String pawning_reason = edt_pawning_reason.getText().toString();
//                String offense_history = spinOffenseHistory.getText().toString();
//                String offense_history_date = edt_offense_history_date.getText().toString();
//                String pd_remarks = edt_pd_remarks.getText().toString();
//                String intervention = edt_intervention.getText().toString();
//                String other_details = edt_other_details.getText().toString();
//
//                myEdit.putString("lender_name", lender_name);
//                myEdit.putString("pawning_date", pawning_date);
//                myEdit.putString("loaned_amount", loaned_amount);
//                myEdit.putString("lender_address", lender_address);
//                myEdit.putString("date_retrieved", date_retrieved);
//                myEdit.putString("interest", interest);
//                myEdit.putString("spin_status", spin_status);
//                myEdit.putString("pawning_reason", pawning_reason);
//                myEdit.putString("offense_history", offense_history);
//                myEdit.putString("offense_history_date", offense_history_date);
//                myEdit.putString("pd_remarks", pd_remarks);
//                myEdit.putString("intervention", intervention);
//                myEdit.putString("other_details", other_details);
//                myEdit.commit();
//                break;
            default:
                break;
        }

    }

    public void btn_func(){
        String household_no ="";
        household_no = edt_hh.getText().toString();
        try {
            if (!household_no.matches("")){
                search = MainActivity.sqLiteHelper.getData("SELECT id,full_name,hh_id,client_status,address,sex,hh_set_group,current_grantee_card_number,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_card_holder_name_3,upload_history_id,created_at,updated_at,validated_at FROM emv_database_monitoring WHERE hh_id='"+household_no+"'");
                while (search.moveToNext()) {
                    emv_id = search.getInt(0);
                    full_name = search.getString(1);
                    hh_id = search.getString(2);
                    client_status = search.getString(3);
                    address = search.getString(4);
                    sex = search.getString(5);
                    hh_set_group = search.getString(6);
                    current_grantee_card_number = search.getString(7);
                    other_card_number_1 = search.getString(8);
                    other_card_holder_name_1 = search.getString(9);
                    other_card_number_2 = search.getString(10);
                    other_card_holder_name_2 = search.getString(11);
                    other_card_number_3 = search.getString(12);
                    other_cardholder_name_3 = search.getString(13);
                    upload_history_id = search.getString(14);
                    created_at = search.getString(15);
                    updated_at = search.getString(16);
                    validated_at = search.getString(17);
                }

                Log.v(ContentValues.TAG,"angvalue " + other_card_number_3);
                if (search ==null || search.getCount() == 0){
                    clearSharedPref();
                    edt_fullname.setText("");
                    spinClientStatus.setText("");
                    edt_address.setText("");
                    edt_set.setText("");
                    spinSex.setText("");
                    Toasty.error(getApplicationContext(),"Household number not found", Toasty.LENGTH_SHORT).show();

                }
                else{
                    if  (validated_at.matches("null")){
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putInt("emv_id", emv_id);
                        myEdit.putString("hh_id", hh_id);
                        myEdit.putString("full_name", full_name);
                        myEdit.putString("client_status", client_status);
                        myEdit.putString("address", address);
                        myEdit.putString("sex", sex);
                        myEdit.putString("hh_set_group", hh_set_group);
                        myEdit.putString("pressBtn_search", "true");
                        myEdit.commit();

                        edt_fullname.setText(full_name);
                        spinClientStatus.setText(client_status,false);
                        edt_address.setText(address);
                        edt_set.setText(hh_set_group);
                        spinSex.setText(sex,false);
                        edt_contact_no.setText("");
                        Toasty.success(getApplicationContext(),"Household Found", Toasty.LENGTH_SHORT).show();


                        if (other_card_number_1!=null || other_card_number_1.length()!=4){
                            edt_other_card_number_1.setText(other_card_number_1);
                        }
                        if (edt_current_grantee_number!=null || edt_current_grantee_number.length()!=4){
                            edt_current_grantee_number.setText(current_grantee_card_number);
                        }
                        if (edt_other_card_holder_name_1!=null || edt_other_card_holder_name_1.length()!=4){
                            edt_other_card_holder_name_1.setText(other_card_holder_name_1);
                        }
                        if (edt_other_card_number_2!=null || edt_other_card_number_2.length()!=4){
                            edt_other_card_number_2.setText(other_card_number_2);
                        }
                        if (edt_other_card_holder_name_2!=null || edt_other_card_holder_name_2.length()!=4){
                            edt_other_card_holder_name_2.setText(other_card_holder_name_2);
                        }

                        String card_holder3 =other_card_number_3;
                        if (card_holder3.matches("null")){edt_other_card_number_3.setText("");}
                        else{edt_other_card_number_3.setText(other_card_number_3);}
                        String card_holder_3 =other_cardholder_name_3;
                        if (card_holder_3.matches("null")){edt_other_card_holder_name_3.setText("");}
                        else {edt_other_card_holder_name_3.setText(other_cardholder_name_3);}
                    }
                    else {
                        Toasty.info(getApplicationContext(),"Household " + household_no + " already validated" + " " +validated_at, Toasty.LENGTH_SHORT).show();
                    }
                    search.close();
                }
            }
            else {
                Toasty.info(getApplicationContext(),"Please enter a household number ", Toasty.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Log.v(ContentValues.TAG,"not found " +e);
            Toasty.error(getApplicationContext(),"Household not foundsa", Toasty.LENGTH_SHORT).show();
        }
    }

    public void btn_CheckNextValidation(){
        String household_no ="";
        household_no = edt_hh.getText().toString();
        try {
            if (!household_no.matches("")){
                search = MainActivity.sqLiteHelper.getData("SELECT id,full_name,hh_id,client_status,address,sex,hh_set_group,current_grantee_card_number,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_card_holder_name_3,upload_history_id,created_at,updated_at,validated_at FROM emv_database_monitoring WHERE hh_id='"+household_no+"'");
                while (search.moveToNext()) {
                    emv_id = search.getInt(0);
                    full_name = search.getString(1);
                    hh_id = search.getString(2);
                }
                if (search ==null || search.getCount() == 0){
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("pressBtn_search", "false");
                    myEdit.commit();
                }
                else{
                    search.close();
                }
            }
            else {
                Toasty.info(getApplicationContext(),"Please enter a household number ", Toasty.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            Log.v(ContentValues.TAG,"not found " +e);
            Toasty.error(getApplicationContext(),"Household not found", Toasty.LENGTH_SHORT).show();
        }
    }


    public void representativeOne(){
        edtRepresentativeOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() ==0){
                    tilRepresentativeIntroOne.setError(required_field);
                }
                else{
                    tilRepresentativeIntroOne.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void contactNumber(){
        edt_contact_no.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if(s.toString().length() != 10){
                    tilContactNo.setError(required_field);
                }
                else{
                    tilContactNo.setError(null);
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void contactNumberOf(){
        spinnerContactNoOf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilContactNoOf.setError(required_field);
                }
                else{
                    tilContactNoOf.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void granteeOne(){
        spinnerGrantee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {

                if(s.toString().length() == 0){
                    tilGranteeOne.setError(required_field);
                }
                else{
                    tilGranteeOne.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setEntry(){
        edt_set.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilSet.setError(required_field);
                }
                else{
                    tilSet.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void HouseholdOnChange(EditText household){
        household.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("pressBtn_search", "false");
                myEdit.commit();
                if(s.toString().length() ==0){
                    tilHhId.setError("Not enough length");
                }
                else{

                    tilHhId.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void dateReleased(EditText date_r){
        date_r.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilCardReleased.setError(required_field);
                }
                else{
                    tilCardReleased.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void whoReleased(EditText who_released){
        who_released.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilWhoReleased.setError(required_field);
                }
                else{
                    tilWhoReleased.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void placeReleased(EditText place_released){
        place_released.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilPlaceReleased.setError(required_field);
                }
                else{
                    tilPlaceReleased.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void availableReason(){
        spinIsAvailReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilAvailablereason.setError(required_field);
                }
                else{
                    tilAvailablereason.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void reasonNotPresented(){
        edtReasonNotPresented.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilReasonNotPresented.setError(required_field);
                }
                else{
                    tilReasonNotPresented.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void otherReasonForUnclaimed(){
        edtOthersUnclaimedReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilOthersUnclaimedReason.setError(required_field);
                }
                else{
                    tilOthersUnclaimedReason.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void pinAttatched(){
        spinnerIsPinAttached.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilPinAttached.setError(required_field);
                }
                else{
                    tilPinAttached.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void unclaimedReason(){
        spinnerIsUnclaimedReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilUnclaimedReason.setError(required_field);
                }
                else{
                    tilUnclaimedReason.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void requestReplacement(){
        spinnerIsReasonReplacement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilRequestReplacement.setError(required_field);
                }
                else{
                    tilRequestReplacement.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void representativeTwo(){
        edtRepresentativeTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilRepresentativeIntroTwo.setError(required_field);
                }
                else{
                    tilRepresentativeIntroTwo.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    public void cardAvailable(AutoCompleteTextView spnAvail){
        spnAvail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilIsAvailable.setError(required_field);
                }
                else{
                    tilIsAvailable.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void scannedCardNumber(EditText card_number){
        card_number.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                String cardNumber = card_number.getText().toString();
                if(s.toString().length() != 23){
                    tilCard.setError(required_field);
                }
                else if(!cardNumber.matches("[0-9 ]+")) {
                    tilCard.setError("Invalid Format");
                }
                else{
                    tilCard.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                String field = editable.toString();
                int currCount = field.length();

                if (shouldIncrementOrDecrement(currCount, true)){
                    appendOrStrip(field, true);
                } else if (shouldIncrementOrDecrement(currCount, false)) {
                    appendOrStrip(field, false);
                }
                prevCount = card_number.getText().toString().length();

            }
        });
    }
    public void cardSeries(EditText card_series){
        card_series.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilSeriesNumber.setError(required_field);
                }
                else{
                    tilSeriesNumber.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void idExists(EditText idExists){
        idExists.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilIsID.setError(required_field);
                }
                else{
                    tilIsID.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void clearFields(){
        spinnerIsReasonReplacement.setText("");
        spinnerIsUnclaimedReason.setText("");
        edt_lender_name.setText("");
        edtOthersUnclaimedReason.setText("");
        edt_pawning_date.setText("");
        edt_loaned_amount.setText("");
        edt_lender_address.setText("");
        edt_date_retrieved.setText("");
        edt_interest.setText("");
        spinStatus.setText("");
        edt_pawning_reason.setText("");
        spinOffenseHistory.setText("");
        edt_offense_history_date.setText("");
        edt_pd_remarks.setText("");
        edt_intervention.setText("");
        edt_other_details.setText("");
        edtReasonNotPresented.setText("");
    }

    public void clearSharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        //1
        myEdit.putString("hh_id", "160310001-");
        myEdit.putInt("emv_id", 0);
        myEdit.putString("full_name", "");
        myEdit.putString("client_status", "");
        myEdit.putString("address", "");
        myEdit.putString("sex", "");
        myEdit.putString("hh_set_group", "");
        myEdit.putString("contact_no", "");
        myEdit.putString("assigned", "");
        myEdit.putString("minor_grantee", "");
        //2
        myEdit.putString("distribution_status", "");
        myEdit.putString("card_released", "");
        myEdit.putString("who_released", "");
        myEdit.putString("place_released", "");
        myEdit.putString("current_grantee_number", "");
        myEdit.putString("temp_current_grantee_number", "");
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
        myEdit.putString("other_card_number_series_1", "");
        myEdit.putString("other_card_number_series_2", "");
        myEdit.putString("other_card_number_series_3", "");
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
        myEdit.putString("pressBtn_search", "false");
        myEdit.putString("is_additional", "");
        myEdit.putString("scanned_cash_card", "");
        myEdit.putString("series_number", "");
        myEdit.commit();
    }



}
