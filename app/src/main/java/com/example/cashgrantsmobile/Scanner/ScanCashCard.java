package com.example.cashgrantsmobile.Scanner;




import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashgrantsmobile.Loading.LoadingBar;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class ScanCashCard extends AppCompatActivity {

    private static int MANDATORY_PAGE_LOCATION = 0 ;
    ImageView mPreviewIv;
    private static final int CAMERA_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String StoragePermission[], required_field, required_length, required_btn, required_cc_length,required_cc_invalid_format;
    Button btn_search_hh, rescanCashCard, btn_cash_card;
    public static boolean scanned = true;
    public static boolean pressBtn_search = false;
    public static boolean pressNext = false;
    Uri image_uri;

    String hh_id,sex,hh_set_group,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_cardholder_name_3, first_name,last_name,middle_name,ext_name,hh_status,province,municipality,barangay,nma_amount,nma_non_emv,nma_card_name,grantee_card_number,grantee_card_release_date,grantee_card_release_date1,grantee_card_release_date2,grantee_card_release_date3,grantee_card_release_date4,grantee_card_release_date5,other_card_release_date_1,other_card_release_date_2,grantee_distribution_status,grantee_distribution_status_record,grantee_distribution_status_record1,grantee_distribution_status_record2,grantee_distribution_status_record3,grantee_distribution_status_record4,grantee_distribution_status_record5,other_card_distribution_status_1,other_card_distribution_status_2,upload_history_id,other_card_holder_1,other_card_holder_2,other_card_holder_3,other_card_distribution_status_3,other_card_release_date_3,other_card_number_4,other_card_holder_4,other_card_distribution_status_4,other_card_release_date_4, other_card_number_5,other_card_holder_5,other_card_distribution_status_5,other_card_release_date_5,created_at,updated_at,validated_at;
    Integer emv_monitoring_id,record_counter;

    private int prevCount = 0;
    Integer isValidationError = 0;
    Integer card_count = 0;

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


//    Intro 1 XML Fields
    TextInputLayout til_hh_id, til_set, til_last_name, til_first_name, til_middle_name, til_ext_name, til_other_ext_name, til_hh_status, til_province_code, til_municipality_code, til_barangay_code, til_sex, til_is_grantee, til_relationship_to_grantee, til_contact_no, til_contact_no_of, til_contact_no_of_others, til_assigned_staff, til_is_minor;
    EditText edt_hh_id, edt_last_name, edt_first_name, edt_middle_name, edt_other_ext_name, edt_contact_no, edt_contact_no_of_others, edt_assigned_staff;
    AutoCompleteTextView aat_set, aat_ext_name, aat_hh_status, aat_province_code, aat_municipality_code, aat_barangay_code, aat_sex, aat_is_grantee, aat_relationship_to_grantee, aat_contact_no_of, aat_is_minor;

//    Intro 2 XML Fields
    TextInputLayout til_card_number_prefilled, til_distribution_status, til_release_date, til_release_by, til_release_place, til_card_physically_presented, til_card_pin_is_attached, til_reason_not_presented, til_reason_unclaimed, til_card_replacement_request, til_others_reason_not_presented, til_others_reason_unclaimed, til_card_replacement_request_submitted_details, til_lender_name, til_date_pawned, til_loan_amount, til_lender_address, til_date_retrieved, til_interest, til_status, til_reason, til_offense_history, til_offense_date, til_staff_intervention, til_other_details, til_card, til_card_number_inputted, til_card_number_series, til_id_exists, til_additionalID, tilGrantee, til_representative_name, til_card_number_prefilled1, til_card_holder_name1, til_distribution_status1, til_release_date1, til_release_by1, til_release_place1, til_card_physically_presented1, til_card_pin_is_attached1, til_reason_not_presented1, til_others_reason_not_presented1, til_reason_unclaimed1, til_others_reason_unclaimed1, til_card_replacement_request1, til_card_replacement_request_submitted_details1, til_pawning_remarks1, tilOtherScanned1, til_card_number_inputted1, til_card_number_series1, til_card_number_prefilled2, til_card_holder_name2, til_distribution_status2, til_release_date2, til_release_by2, til_release_place2, til_card_physically_presented2, til_card_pin_is_attached2, til_reason_not_presented2, til_others_reason_not_presented2, til_reason_unclaimed2, til_others_reason_unclaimed2, til_card_replacement_request2, til_card_replacement_request_submitted_details2, til_pawning_remarks2, tilOtherScanned2, til_card_number_inputted2, til_card_number_series2, til_card_number_prefilled3, til_card_holder_name3, til_distribution_status3, til_release_date3, til_release_by3, til_release_place3, til_card_physically_presented3, til_card_pin_is_attached3, til_reason_not_presented3, til_others_reason_not_presented3, til_reason_unclaimed3, til_others_reason_unclaimed3, til_card_replacement_request3, til_card_replacement_request_submitted_details3, til_pawning_remarks3, tilOtherScanned3, til_card_number_inputted3, til_card_number_series3, til_card_number_prefilled4, til_card_holder_name4, til_distribution_status4, til_release_date4, til_release_by4, til_release_place4, til_card_physically_presented4, til_card_pin_is_attached4, til_reason_not_presented4, til_others_reason_not_presented4, til_reason_unclaimed4, til_others_reason_unclaimed4, til_card_replacement_request4, til_card_replacement_request_submitted_details4, til_pawning_remarks4, tilOtherScanned4, til_card_number_inputted4, til_card_number_series4, til_card_number_prefilled5, til_card_holder_name5, til_distribution_status5, til_release_date5, til_release_by5, til_release_place5, til_card_physically_presented5, til_card_pin_is_attached5, til_reason_not_presented5, til_others_reason_not_presented5, til_reason_unclaimed5, til_others_reason_unclaimed5, til_card_replacement_request5, til_card_replacement_request_submitted_details5, til_pawning_remarks5, tilOtherScanned5, til_card_number_inputted5, til_card_number_series5, til_current_scan_btn;
    EditText edt_card_number_prefilled, edt_release_date, edt_release_by, edt_release_place, edt_others_reason_not_presented, edt_others_reason_unclaimed, edt_card_replacement_request_submitted_details, edt_lender_name, edt_date_pawned, edt_loan_amount, edt_lender_address, edt_date_retrieved, edt_interest, edt_reason, edt_offense_date, edt_remarks, edt_staff_intervention, edt_other_details, edt_card_number_inputted, edt_card_number_series, edt_representative_name, edt_card_number_prefilled1, edt_card_holder_name1, edt_release_date1, edt_release_by1, edt_release_place1, edt_others_reason_not_presented1, edt_others_reason_unclaimed1, edt_card_replacement_request_submitted_details1, edt_pawning_remarks1, edt_card_number_inputted1, edt_card_number_series1, edt_card_number_prefilled2, edt_card_holder_name2, edt_release_date2, edt_release_by2, edt_release_place2, edt_others_reason_not_presented2, edt_others_reason_unclaimed2, edt_card_replacement_request_submitted_details2, edt_pawning_remarks2, edt_card_number_inputted2, edt_card_number_series2, edt_card_number_prefilled3, edt_card_holder_name3, edt_release_date3, edt_release_by3, edt_release_place3, edt_others_reason_not_presented3, edt_others_reason_unclaimed3, edt_card_replacement_request_submitted_details3, edt_pawning_remarks3, edt_card_number_inputted3, edt_card_number_series3, edt_card_number_prefilled4, edt_card_holder_name4, edt_release_date4, edt_release_by4, edt_release_place4, edt_others_reason_not_presented4, edt_others_reason_unclaimed4, edt_card_replacement_request_submitted_details4, edt_pawning_remarks4, edt_card_number_inputted4, edt_card_number_series4, edt_card_number_prefilled5, edt_card_holder_name5, edt_release_date5, edt_release_by5, edt_release_place5, edt_others_reason_not_presented5, edt_others_reason_unclaimed5, edt_card_replacement_request_submitted_details5, edt_pawning_remarks5, edt_card_number_inputted5, edt_card_number_series5, aat_distribution_status_record, edt_release_date_record, aat_distribution_status_record1, edt_release_date_record1, aat_distribution_status_record2, edt_release_date_record2, aat_distribution_status_record3, edt_release_date_record3, aat_distribution_status_record4, edt_release_date_record4, aat_distribution_status_record5, edt_release_date_record5;
    AutoCompleteTextView aat_distribution_status, aat_card_physically_presented, aat_card_pin_is_attached, aat_reason_not_presented, aat_reason_unclaimed, aat_card_replacement_request, aat_status, aat_offense_history, aat_id_exists, aat_distribution_status1, aat_card_physically_presented1, aat_card_pin_is_attached1, aat_reason_not_presented1, aat_reason_unclaimed1, aat_card_replacement_request1, aat_distribution_status2, aat_card_physically_presented2, aat_card_pin_is_attached2, aat_reason_not_presented2, aat_reason_unclaimed2, aat_card_replacement_request2, aat_distribution_status3, aat_card_physically_presented3, aat_card_pin_is_attached3, aat_reason_not_presented3, aat_reason_unclaimed3, aat_card_replacement_request3, aat_distribution_status4, aat_card_physically_presented4, aat_card_pin_is_attached4, aat_reason_not_presented4, aat_reason_unclaimed4, aat_card_replacement_request4, aat_distribution_status5, aat_card_physically_presented5, aat_card_pin_is_attached5, aat_reason_not_presented5, aat_reason_unclaimed5, aat_card_replacement_request5;
    ImageView ScannedImage, imgUri, imgAdditionalId, mGrantee, ivOtherScannedImage1, ivOtherScannedImageUrl1, ivOtherScannedImage2, ivOtherScannedImageUrl2, ivOtherScannedImage3, ivOtherScannedImageUrl3, ivOtherScannedImage4, ivOtherScannedImageUrl4, ivOtherScannedImage5, ivOtherScannedImageUrl5;
    Button btn_scanCashCard, btn_scanID, btn_grantee, btnOtherScanned1, btnOtherScanned2, btnOtherScanned3, btnOtherScanned4, btnOtherScanned5;
    MaterialCardView otherCardAvailability1, otherCardAvailability2, otherCardAvailability3, otherCardAvailability4, otherCardAvailability5;
    ImageButton btnCancelOtherCard1, btnCancelOtherCard2, btnCancelOtherCard3, btnCancelOtherCard4, btnCancelOtherCard5;
    AppCompatButton btnAddCard;
    LinearLayout ll_additional_id_layout;
    MaterialCardView mcvPawning;
    Integer ScanImagePos = 0;
    RelativeLayout rlOtherCardScanningField1, rlOtherCardScanningField2, rlOtherCardScanningField3, rlOtherCardScanningField4, rlOtherCardScanningField5;

//    Intro 3 XML Fields
    TextInputLayout til_nma_amount, til_nma_reason, til_nma_others_reason, til_nma_date_claimed, til_nma_remarks, til_overall_remarks;
    EditText edt_nma_amount, edt_nma_others_reason, edt_nma_date_claimed, edt_nma_remarks, edt_overall_remarks, edt_non_emv, edt_card_name;
    AutoCompleteTextView aat_nma_reason;

    String[] Ans = new String[]{"Yes", "No"};
    String[] CardRequired = new String[]{"Yes", "No"};
    String[] Sex = new String[]{"MALE", "FEMALE"};
    String[] Reasons = new String[]{"Lost/Stolen", "Damaged/Defective", "Pawned", "Not Turned Over", "Others"};
    String[] modifiedArray = Arrays.copyOfRange(Reasons, 1, Reasons.length);

    String[] Contact_no_of = new String[]{"Grantee", "Others"};
    String[] Interviewee = new String[]{"Grantee", "Representative"};
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
            "Inactive and zero balance",
            "Waived",
            "For delisting of client status",
            "Temporarily on hold (code 19)",
            "With cash card of other family member",
            "Force Majeure",
            "For distribution",
            "Others"
    };
    String[] ReasonNMA = new String[]{
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
    String[] RelationshipToGrantee = new String[]{
            "2 - Wife / Spouse",
            "3 - Son / Daughter",
            "4 - Brother / Sister",
            "5 - Son-in-law / Daughter-in-law",
            "6 - Grandson / Granddaughter",
            "7 - Father / Mother",
            "8 - Other Relatives",
            "9 - Boarders",
            "10 - Domestic Helper",
            "11 - Non-relative",
            "12 - Guardian"
    };
    String[] HouseholdSet = new String[]{
            "1A",
            "1B",
            "1C",
            "1D",
            "2A",
            "3A",
            "3B",
            "3C",
            "3D",
            "4A",
            "4B",
            "4C",
            "4D",
            "5A",
            "5B",
            "6A",
            "6B",
            "6C",
            "6D",
            "6E",
            "7A",
            "7B",
            "7C",
            "7E",
            "7M",
            "8A",
            "8B",
            "8C",
            "8D",
            "8E",
            "9A",
            "10A",
            "11A",
            "11B",
            "11C",
            "NOV15",
            "MAY16",
            "AUG18",
            "OCT18",
            "DEC19"
    };
    String[] ExtensionName = new String[]{
            "Jr.",
            "Sr.",
            "I",
            "II",
            "III",
            "IV",
            "V",
            "Jra.",
            "Others"
    };

    String psgc_province = "";
    String psgc_municipality = "";
    String psgc_barangay = "";

    final ArrayList<String> province_list = new ArrayList<String>();
    final ArrayList<String> municipality_list = new ArrayList<String>();
    final ArrayList<String> barangay_list = new ArrayList<String>();

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

    public LoadingBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cash_card_scanner_entries);
        mPreviewIv = findViewById(R.id.imageIv);
        mPreviewIv .setVisibility(View.INVISIBLE);
        Bundle extras = getIntent().getExtras();
        isValidationError = 0;

        loadingBar = new LoadingBar(this);




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

        introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
        }

        tvNext = findViewById(R.id.tvNext);
        tvPrev = findViewById(R.id.tvPrev);
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
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                String hh_id = sh.getString("hh_id", "");
                String buttonNext = sh.getString("pressBtn_search", "");

                if (hh_id.length() > 0 && buttonNext.matches("true")){
                    nextValidation();

                    if (MANDATORY_PAGE_LOCATION == 1) {
                        otherCardVisibility();
                    }
                }
                else{
                    Toasty.info(getApplicationContext(),"Search household first", Toasty.LENGTH_SHORT).show();
                }
            }
        });

        tvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem();

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                String hh_id = sh.getString("hh_id", "160310001-");
                String nma_amount = sh.getString("nma_amount", "0");
                if (hh_id.length() > 0) {
                    pressBtn_search=true;
                }

                store_preferences(current+1);

                if (MANDATORY_PAGE_LOCATION == 1) {
                    otherCardVisibility();
                }

                if (MANDATORY_PAGE_LOCATION == 3 && Float.parseFloat(nma_amount) < 100) {
                    MANDATORY_PAGE_LOCATION = MANDATORY_PAGE_LOCATION - 2;
                    current = current - 2;
                    viewPager.setCurrentItem(current);
                } else {
                    if (current > 0) {
                        MANDATORY_PAGE_LOCATION--;
                        pressNext =false;
                        current = current - 1;
                        viewPager.setCurrentItem(current);
                    }
                    if (current == 0){
                        tvPrev.setVisibility(View.INVISIBLE);

                        til_hh_id = findViewById(R.id.til_hh_id);
                        edt_hh_id = findViewById(R.id.edt_hh_id);
                        HouseholdOnChange(edt_hh_id);

                    } else {
                        tvPrev.setVisibility(View.VISIBLE);
                    }
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
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
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

    private void appendOrStrip(String field, boolean shouldAppend, EditText textName) {
        StringBuilder sb = new StringBuilder(field);
        if (shouldAppend) {
            sb.append(" ");
        } else {
            sb.setLength(sb.length() - 1);
        }

        if (textName.toString().contains("edt_card_number_inputted1")){
            edt_card_number_inputted1.setText(sb.toString());
            edt_card_number_inputted1.setSelection(sb.length());
        }
        else if (textName.toString().contains("edt_card_number_inputted2")){
            edt_card_number_inputted2.setText(sb.toString());
            edt_card_number_inputted2.setSelection(sb.length());
        }
        else if (textName.toString().contains("edt_card_number_inputted3")){
            edt_card_number_inputted3.setText(sb.toString());
            edt_card_number_inputted3.setSelection(sb.length());
        }
        else if (textName.toString().contains("edt_card_number_inputted4")){
            edt_card_number_inputted4.setText(sb.toString());
            edt_card_number_inputted4.setSelection(sb.length());
        }
        else if (textName.toString().contains("edt_card_number_inputted5")){
            edt_card_number_inputted5.setText(sb.toString());
            edt_card_number_inputted5.setSelection(sb.length());
        }
        else {
            edt_card_number_inputted.setText(sb.toString());
            edt_card_number_inputted.setSelection(sb.length());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imgAdditionalId.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                sqLiteHelper.insertImageTmp("additional_id_image", imageViewToByte(imgAdditionalId), 1);
                til_additionalID.setError(null);

            }catch (Exception e){
                Log.v(TAG,"Error" + e);
            }
        }
        else if (requestCode == 103){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mGrantee.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
                sqLiteHelper.insertImageTmp("grantee_e_image", imageViewToByte(mGrantee), 1);
                tilGrantee.setError(null);
            } catch (Exception e) {
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
                BitmapDrawable bitmapDrawable;
                switch (ScanImagePos) {
                    case 1:
                        ivOtherScannedImage1.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ivOtherScannedImage1.getDrawable();
                        break;
                    case 2:
                        ivOtherScannedImage2.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ivOtherScannedImage2.getDrawable();
                        break;
                    case 3:
                        ivOtherScannedImage3.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ivOtherScannedImage3.getDrawable();
                        break;
                    case 4:
                        ivOtherScannedImage4.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ivOtherScannedImage4.getDrawable();
                        break;
                    case 5:
                        ivOtherScannedImage5.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ivOtherScannedImage5.getDrawable();
                        break;
                    default:
                        ScannedImage.setImageURI(resultUri);
                        bitmapDrawable = (BitmapDrawable)ScannedImage.getDrawable();
                        break;
                }

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
                    sTextFromET = sTextFromET.replace("-", " ");
                    sTextFromET = sTextFromET.replaceAll("....", "$0 ");
                    image_uri = Uri.parse(image_uri.toString());
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                        switch (ScanImagePos) {
                            case 1:
                                ivOtherScannedImage1.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                            case 2:
                                ivOtherScannedImage2.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                            case 3:
                                ivOtherScannedImage3.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                            case 4:
                                ivOtherScannedImage4.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                            case 5:
                                ivOtherScannedImage5.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                            default:
                                ScannedImage.setImageBitmap(Bitmap.createScaledBitmap(bm, 374, 500, false));
                                break;
                        }


                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();

                        switch (ScanImagePos) {
                            case 1:
                                tilOtherScanned1 = findViewById(R.id.tilOtherScanned1);
                                tilOtherScanned1.setError(null);
                                sqLiteHelper.insertImageTmp("other_card_e_image_1", imageViewToByte(ivOtherScannedImage1), 1);

                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    edt_card_number_inputted1.setText(limitString);
                                    myEdit.putString("card_number_system_generated1",  limitString);
                                    String CardResult = edt_card_number_inputted1.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted1.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated1", sTextFromET);
                                    edt_card_number_inputted1.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
                            case 2:
                                tilOtherScanned2 = findViewById(R.id.tilOtherScanned2);
                                tilOtherScanned2.setError(null);
                                sqLiteHelper.insertImageTmp("other_card_e_image_2", imageViewToByte(ivOtherScannedImage2), 1);

                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    edt_card_number_inputted2.setText(limitString);
                                    myEdit.putString("card_number_system_generated2", limitString);
                                    String CardResult = edt_card_number_inputted2.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted2.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated2", sTextFromET);
                                    edt_card_number_inputted2.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
                            case 3:
                                tilOtherScanned3 = findViewById(R.id.tilOtherScanned3);
                                tilOtherScanned3.setError(null);
                                sqLiteHelper.insertImageTmp("other_card_e_image_3", imageViewToByte(ivOtherScannedImage3), 1);

                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    edt_card_number_inputted3.setText(limitString);
                                    myEdit.putString("card_number_system_generated3", limitString);
                                    String CardResult = edt_card_number_inputted3.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted3.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated3", sTextFromET);
                                    edt_card_number_inputted3.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
                            case 4:
                                tilOtherScanned4 = findViewById(R.id.tilOtherScanned4);
                                tilOtherScanned4.setError(null);
                                sqLiteHelper.insertImageTmp("other_card_e_image_4", imageViewToByte(ivOtherScannedImage4), 1);

                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    myEdit.putString("card_number_system_generated4", limitString);
                                    edt_card_number_inputted4.setText(limitString);
                                    String CardResult = edt_card_number_inputted4.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted4.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated4", sTextFromET);
                                    edt_card_number_inputted4.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
                            case 5:
                                tilOtherScanned5 = findViewById(R.id.tilOtherScanned5);
                                tilOtherScanned5.setError(null);
                                sqLiteHelper.insertImageTmp("other_card_e_image_5", imageViewToByte(ivOtherScannedImage5), 1);

                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    myEdit.putString("card_number_system_generated5", limitString);
                                    edt_card_number_inputted5.setText(limitString);
                                    String CardResult = edt_card_number_inputted5.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted5.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated5", sTextFromET);
                                    edt_card_number_inputted5.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
                            default:
                                til_current_scan_btn.setError(null);
                                sqLiteHelper.insertImageTmp("scanned_e_image", imageViewToByte(ScannedImage), 1);


                                if (sTextFromET.length() >23){
                                    String limitString = sTextFromET.substring(0,23);
                                    myEdit.putString("card_number_system_generated", limitString);
                                    edt_card_number_inputted.setText(limitString);
                                    String CardResult = edt_card_number_inputted.getText().toString();
                                    if (!CardResult.matches("[0-9 ]+")){
                                        til_card_number_inputted.setError("Invalid format");
                                    }
                                }
                                else{
                                    myEdit.putString("card_number_system_generated", sTextFromET);
                                    edt_card_number_inputted.setText(sTextFromET);
                                }
                                myEdit.commit();
                                break;
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
    }

    public void getImage(){
            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT scanned_e_image,additional_id_image,grantee_e_image,other_card_e_image_1,other_card_e_image_2, other_card_e_image_3,other_card_e_image_4,other_card_e_image_5 FROM tmp_blob WHERE id=1");
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()) {
                        byte[] scanned_image = cursor.getBlob(0);
                        byte[] additional_image = cursor.getBlob(1);
                        byte[] grantee_image = cursor.getBlob(2);
                        byte[] other_image1 = cursor.getBlob(3);
                        byte[] other_image2 = cursor.getBlob(4);
                        byte[] other_image3 = cursor.getBlob(5);
                        byte[] other_image4 = cursor.getBlob(6);
                        byte[] other_image5 = cursor.getBlob(7);
                        if (scanned_image != null) {
                            Bitmap scanned = BitmapFactory.decodeByteArray(scanned_image, 0, scanned_image.length);
                            ScannedImage.setImageBitmap(scanned);
                        }
                        if (additional_image != null) {
                            Bitmap additional = BitmapFactory.decodeByteArray(additional_image, 0, additional_image.length);
                            imgAdditionalId.setImageBitmap(additional);
                        }
                        if (grantee_image != null) {
                            Bitmap grantee = BitmapFactory.decodeByteArray(grantee_image, 0, grantee_image.length);
                            mGrantee.setImageBitmap(grantee);
                        }
                        if (other_image1 != null) {
                            if (otherCardAvailability1.getVisibility() == View.VISIBLE) {
                                Bitmap b_o_image1 = BitmapFactory.decodeByteArray(other_image1, 0, other_image1.length);
                                ivOtherScannedImage1.setImageBitmap(b_o_image1);
                            }
                        }
                        if (other_image2 != null) {
                            if (otherCardAvailability2.getVisibility() == View.VISIBLE) {
                                Bitmap b_o_image2 = BitmapFactory.decodeByteArray(other_image2, 0, other_image2.length);
                                ivOtherScannedImage2.setImageBitmap(b_o_image2);
                            }
                        }
                        if (other_image3 != null) {
                            if (otherCardAvailability3.getVisibility() == View.VISIBLE) {
                                Bitmap b_o_image3 = BitmapFactory.decodeByteArray(other_image3, 0, other_image3.length);
                                ivOtherScannedImage3.setImageBitmap(b_o_image3);
                            }
                        }
                        if (other_image4 != null) {
                            if (otherCardAvailability4.getVisibility() == View.VISIBLE) {
                                Bitmap b_o_image4 = BitmapFactory.decodeByteArray(other_image4, 0, other_image4.length);
                                ivOtherScannedImage4.setImageBitmap(b_o_image4);
                            }
                        }
                        if (other_image5 != null) {
                            if (otherCardAvailability5.getVisibility() == View.VISIBLE) {
                                Bitmap b_o_image5 = BitmapFactory.decodeByteArray(other_image5, 0, other_image5.length);
                                ivOtherScannedImage5.setImageBitmap(b_o_image5);
                            }
                        }
                    }

                }
                else{
                    til_current_scan_btn.setError(required_field);
                    til_additionalID.setError(required_field);
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

    public static byte[] imageViewToByte(ImageView image) {
        try{
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
            image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 374, 500, false));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        }
        catch (Exception e){
            return null;
        }
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
                tvNext.setText("SAVE");
            } else {
                tvNext.setText("NEXT");
            }

            initialize_layout(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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

        View mCurrentView;

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mCurrentView = layoutInflater.inflate(layouts[position], container, false);
            container.addView(mCurrentView);
            if (position == 0) {
                initialize_layout(0);
            }

            return mCurrentView;
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

    public void scannedCardNumber(EditText card_number, TextInputLayout tilCard ){
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
                    appendOrStrip(field, true,card_number);
                } else if (shouldIncrementOrDecrement(currCount, false)) {
                    appendOrStrip(field, false, card_number);
                }
                prevCount = card_number.getText().toString().length();
            }
        });
    }

    private void otherCardVisibility() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();

        card_count = sh.getInt("card_count", 0);

        otherCardAvailability1 = findViewById(R.id.otherCardAvailability1);
        otherCardAvailability2 = findViewById(R.id.otherCardAvailability2);
        otherCardAvailability3 = findViewById(R.id.otherCardAvailability3);
        otherCardAvailability4 = findViewById(R.id.otherCardAvailability4);
        otherCardAvailability5 = findViewById(R.id.otherCardAvailability5);

        otherCardAvailability1.setVisibility(View.GONE);
        otherCardAvailability2.setVisibility(View.GONE);
        otherCardAvailability3.setVisibility(View.GONE);
        otherCardAvailability4.setVisibility(View.GONE);
        otherCardAvailability5.setVisibility(View.GONE);

        btnCancelOtherCard1 = findViewById(R.id.btnCancelOtherCard1);
        btnCancelOtherCard2 = findViewById(R.id.btnCancelOtherCard2);
        btnCancelOtherCard3 = findViewById(R.id.btnCancelOtherCard3);
        btnCancelOtherCard4 = findViewById(R.id.btnCancelOtherCard4);
        btnCancelOtherCard5 = findViewById(R.id.btnCancelOtherCard5);

        btnAddCard.setVisibility(View.VISIBLE);

        for (int i = 1; i <= card_count; i++) {
            switch (i){
                case 1:
                    otherCardAvailability1.setVisibility(View.VISIBLE);
                    btnCancelOtherCard1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            card_count--;
                            myEdit.putInt("card_count", card_count);
                            myEdit.commit();
                            otherCardVisibility();
                        }
                    });
                    break;
                case 2:
                    otherCardAvailability2.setVisibility(View.VISIBLE);
                    btnCancelOtherCard2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            card_count--;
                            myEdit.putInt("card_count", card_count);
                            myEdit.commit();
                            otherCardVisibility();
                        }
                    });
                    break;
                case 3:
                    otherCardAvailability3.setVisibility(View.VISIBLE);
                    btnCancelOtherCard3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            card_count--;
                            myEdit.putInt("card_count", card_count);
                            myEdit.commit();
                            otherCardVisibility();
                        }
                    });
                    break;
                case 4:
                    otherCardAvailability4.setVisibility(View.VISIBLE);
                    btnCancelOtherCard4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            card_count--;
                            myEdit.putInt("card_count", card_count);
                            myEdit.commit();
                            otherCardVisibility();
                        }
                    });
                    break;
                case 5:
                    otherCardAvailability5.setVisibility(View.VISIBLE);
                    btnAddCard.setVisibility(View.GONE);
                    btnCancelOtherCard5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            card_count--;
                            myEdit.putInt("card_count", card_count);
                            myEdit.commit();
                            otherCardVisibility();
                        }
                    });
                    break;
            }
        }

        if (otherCardAvailability1.getVisibility() == View.GONE) {
            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_1=NULL WHERE id=1");
            ivOtherScannedImage1.setImageResource(R.drawable.ic_image);
            aat_distribution_status1.setText("", false);
            edt_release_date1.setText("");
            edt_release_by1.setText("");
            edt_release_place1.setText("");
            aat_card_physically_presented1.setText("", false);
            aat_card_pin_is_attached1.setText("", false);
            aat_reason_not_presented1.setText("", false);
            edt_others_reason_not_presented1.setText("");
            aat_reason_unclaimed1.setText("", false);
            edt_others_reason_unclaimed1.setText("");
            aat_card_replacement_request1.setText("", false);
            edt_card_replacement_request_submitted_details1.setText("");
            edt_card_number_inputted1.setText("");
            edt_card_number_series1.setText("");
            edt_pawning_remarks1.setText("");
            edt_card_holder_name1.setText("");
        }

        if (otherCardAvailability2.getVisibility() == View.GONE) {
            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_2=NULL WHERE id=1");
            ivOtherScannedImage2.setImageResource(R.drawable.ic_image);
            aat_distribution_status2.setText("", false);
            edt_release_date2.setText("");
            edt_release_by2.setText("");
            edt_release_place2.setText("");
            aat_card_physically_presented2.setText("", false);
            aat_card_pin_is_attached2.setText("", false);
            aat_reason_not_presented2.setText("", false);
            edt_others_reason_not_presented2.setText("");
            aat_reason_unclaimed2.setText("", false);
            edt_others_reason_unclaimed2.setText("");
            aat_card_replacement_request2.setText("", false);
            edt_card_replacement_request_submitted_details2.setText("");
            edt_card_number_inputted2.setText("");
            edt_card_number_series2.setText("");
            edt_pawning_remarks2.setText("");
            edt_card_holder_name2.setText("");
        }

        if (otherCardAvailability3.getVisibility() == View.GONE) {
            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_3=NULL WHERE id=1");
            ivOtherScannedImage3.setImageResource(R.drawable.ic_image);
            aat_distribution_status3.setText("", false);
            edt_release_date3.setText("");
            edt_release_by3.setText("");
            edt_release_place3.setText("");
            aat_card_physically_presented3.setText("", false);
            aat_card_pin_is_attached3.setText("", false);
            aat_reason_not_presented3.setText("", false);
            edt_others_reason_not_presented3.setText("");
            aat_reason_unclaimed3.setText("", false);
            edt_others_reason_unclaimed3.setText("");
            aat_card_replacement_request3.setText("", false);
            edt_card_replacement_request_submitted_details3.setText("");
            edt_card_number_inputted3.setText("");
            edt_card_number_series3.setText("");
            edt_pawning_remarks3.setText("");
            edt_card_holder_name3.setText("");
        }

        if (otherCardAvailability4.getVisibility() == View.GONE) {
            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_4=NULL WHERE id=1");
            ivOtherScannedImage4.setImageResource(R.drawable.ic_image);
            aat_distribution_status4.setText("", false);
            edt_release_date4.setText("");
            edt_release_by4.setText("");
            edt_release_place4.setText("");
            aat_card_physically_presented4.setText("", false);
            aat_card_pin_is_attached4.setText("", false);
            aat_reason_not_presented4.setText("", false);
            edt_others_reason_not_presented4.setText("");
            aat_reason_unclaimed4.setText("", false);
            edt_others_reason_unclaimed4.setText("");
            aat_card_replacement_request4.setText("", false);
            edt_card_replacement_request_submitted_details4.setText("");
            edt_card_number_inputted4.setText("");
            edt_card_number_series4.setText("");
            edt_pawning_remarks4.setText("");
            edt_card_holder_name4.setText("");
        }

        if (otherCardAvailability5.getVisibility() == View.GONE) {
            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_5=NULL WHERE id=1");
            ivOtherScannedImage5.setImageResource(R.drawable.ic_image);
            aat_distribution_status5.setText("", false);
            edt_release_date5.setText("");
            edt_release_by5.setText("");
            edt_release_place5.setText("");
            aat_card_physically_presented5.setText("", false);
            aat_card_pin_is_attached5.setText("", false);
            aat_reason_not_presented5.setText("", false);
            edt_others_reason_not_presented5.setText("");
            aat_reason_unclaimed5.setText("", false);
            edt_others_reason_unclaimed5.setText("");
            aat_card_replacement_request5.setText("", false);
            edt_card_replacement_request_submitted_details5.setText("");
            edt_card_number_inputted5.setText("");
            edt_card_number_series5.setText("");
            edt_pawning_remarks5.setText("");
            edt_card_holder_name5.setText("");
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        edt_overall_remarks = findViewById(R.id.edt_overall_remarks);
        edt_overall_remarks.setEnabled(true);

        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String household_id = sh.getString("hh_id", "");
        String first_name = sh.getString("first_name","");
        String last_name = sh.getString("last_name","");
        String middle_name = sh.getString("middle_name","");
        String ext_name = sh.getString("ext_name","");
        String other_ext_name = sh.getString("other_ext_name","");
        String sex = sh.getString("sex","");
        String province_code = psgc_province;
        String municipality_code = psgc_municipality;
        String barangay_code = psgc_barangay;
        String set = sh.getString("hh_set","");

        String lender_name = sh.getString("lender_name","");
        String lender_address = sh.getString("lender_address","");
        String date_pawned = sh.getString("date_pawned","");
        String date_retrieved = sh.getString("date_retrieved","");
        String loaned_amount = sh.getString("loan_amount","");
        String status = sh.getString("status","");
        String reason = sh.getString("reason","");
        String interest = sh.getString("interest","");
        String offense_history = sh.getString("offense_history","");
        String offense_date = sh.getString("offense_date","");
        String remarks = sh.getString("remarks","");
        String staff_intervention = sh.getString("staff_intervention","");
        String other_details = sh.getString("other_details","");

        String nma_non_emv = sh.getString("nma_non_emv","");
        String nma_card_name = sh.getString("nma_card_name","");
        String amount = sh.getString("nma_amount","");
        String date_claimed = sh.getString("nma_date_claimed","");
        String nma_reason = sh.getString("nma_reason","");
        String nma_others_reason = sh.getString("nma_others_reason","");
        String nma_remarks = sh.getString("nma_remarks","");

        String hh_status = sh.getString("hh_status","");
        String contact_no = sh.getString("contact_no","");
        String contact_no_of = sh.getString("contact_no_of","");
        String is_grantee = sh.getString("is_grantee","");
        String is_minor = sh.getString("is_minor","");
        String relationship_to_grantee = sh.getString("relationship_to_grantee","");
        String assigned_staff = sh.getString("assigned_staff","");
        String representative_name = sh.getString("representative_name","");
        String sync_at = sh.getString("sync_at","");
        int user_id = sh.getInt("user_id",0);
        int emv_validation_id = sh.getInt("emv_alidation_id",0);


        String card_number_prefilled = sh.getString("card_number_prefilled","");
        String card_number_system_generated = sh.getString("card_number_system_generated","");
        String card_number_unputted = sh.getString("card_number_inputted","");
        String card_number_series = sh.getString("card_number_series","");
        String distribution_status = sh.getString("distribution_status","");

        //new
        String distribution_status_record = sh.getString("distribution_status_record","");
        String distribution_status_record1 = sh.getString("distribution_status_record1","");
        String distribution_status_record2 = sh.getString("distribution_status_record2","");
        String distribution_status_record3 = sh.getString("distribution_status_record3","");
        String distribution_status_record4 = sh.getString("distribution_status_record4","");
        String distribution_status_record5 = sh.getString("distribution_status_record5","");
        //end"distribution_status_record"


        //new
        String release_date_record = sh.getString("release_date_record","");
        String release_date_record1 = sh.getString("release_date_record1","");
        String release_date_record2 = sh.getString("release_date_record2","");
        String release_date_record3 = sh.getString("release_date_record3","");
        String release_date_record4 = sh.getString("release_date_record4","");
        String release_date_record5 = sh.getString("release_date_record5","");
        //end"distribution_status_record"

        Log.v(TAG,"test "+release_date_record);
        Log.v(TAG,"test "+release_date_record1);
        Log.v(TAG,"test "+release_date_record2);
        Log.v(TAG,"test "+release_date_record3);
        Log.v(TAG,"test "+release_date_record4);
        Log.v(TAG,"test "+release_date_record5);




        String release_date = sh.getString("release_date","");
        String release_by = sh.getString("release_by","");
        String release_place = sh.getString("release_place","");
        String card_physically_presented = sh.getString("card_physically_presented","");
        String card_pin_is_attached = sh.getString("card_pin_is_attached","");
        String reason_not_presented = sh.getString("reason_not_presented","");
        String others_reason_not_presented = sh.getString("others_reason_not_presented","");
        String others_reason_unclaimed = sh.getString("others_reason_unclaimed","");
        String reason_unclaimed = sh.getString("reason_unclaimed","");
        String card_replacement_request = sh.getString("card_replacement_request","");
        String card_replacement_submitted_details = sh.getString("card_replacement_submitted_details","");
        String overall_remarks = sh.getString("overall_remarks", "");
        String contact_no_of_others = sh.getString("contact_no_of_others", "");
        int emv_monitoring_id = sh.getInt("emv_monitoring_id",0);

        String card_holder_name1 = sh.getString("card_holder_name1","");
        String card_number_system_generated1 = sh.getString("card_number_system_generated1","");
        String card_number_inputted1 = sh.getString("card_number_inputted1","");
        String card_number_series1 = sh.getString("card_number_series1","");
        String distribution_status1 = sh.getString("distribution_status1","");
        String release_date1 = sh.getString("release_date1","");
        String release_by1 = sh.getString("release_by1","");
        String release_place1 = sh.getString("release_place1","");
        String card_physically_presented1 = sh.getString("card_physically_presented1","");
        String card_pin_is_attached1 = sh.getString("card_pin_is_attached1","");
        String reason_not_presented1 = sh.getString("reason_not_presented1","");
        String others_reason_not_presented1 = sh.getString("others_reason_not_presented1","");
        String others_reason_unclaimed1 = sh.getString("others_reason_unclaimed1","");
        String reason_unclaimed1 = sh.getString("reason_unclaimed1","");
        String card_replacement_request1 = sh.getString("card_replacement_request1","");
        String card_replacement_request_submitted_details1 = sh.getString("card_replacement_request_submitted_details1","");
        String pawning_remarks1 = sh.getString("pawning_remarks1","");

        String card_holder_name2 = sh.getString("card_holder_name2","");
        String card_number_system_generated2 = sh.getString("card_number_system_generated2","");
        String card_number_inputted2 = sh.getString("card_number_inputted2","");
        String card_number_series2 = sh.getString("card_number_series2","");
        String distribution_status2 = sh.getString("distribution_status2","");
        String release_date2 = sh.getString("release_date2","");
        String release_by2 = sh.getString("release_by2","");
        String release_place2 = sh.getString("release_place2","");
        String card_physically_presented2 = sh.getString("card_physically_presented2","");
        String card_pin_is_attached2 = sh.getString("card_pin_is_attached2","");
        String reason_not_presented2 = sh.getString("reason_not_presented2","");
        String others_reason_not_presented2 = sh.getString("others_reason_not_presented2","");
        String others_reason_unclaimed2 = sh.getString("others_reason_unclaimed2","");
        String reason_unclaimed2 = sh.getString("reason_unclaimed2","");
        String card_replacement_request2 = sh.getString("card_replacement_request2","");
        String card_replacement_request_submitted_details2 = sh.getString("card_replacement_request_submitted_details2","");
        String pawning_remarks2 = sh.getString("pawning_remarks2","");

        String card_holder_name3 = sh.getString("card_holder_name3","");
        String card_number_system_generated3 = sh.getString("card_number_system_generated3","");
        String card_number_inputted3 = sh.getString("card_number_inputted3","");
        String card_number_series3 = sh.getString("card_number_series3","");
        String distribution_status3 = sh.getString("distribution_status3","");
        String release_date3 = sh.getString("release_date3","");
        String release_by3 = sh.getString("release_by3","");
        String release_place3 = sh.getString("release_place3","");
        String card_physically_presented3 = sh.getString("card_physically_presented3","");
        String card_pin_is_attached3 = sh.getString("card_pin_is_attached3","");
        String reason_not_presented3 = sh.getString("reason_not_presented3","");
        String others_reason_not_presented3 = sh.getString("others_reason_not_presented3","");
        String others_reason_unclaimed3 = sh.getString("others_reason_unclaimed3","");
        String reason_unclaimed3 = sh.getString("reason_unclaimed3","");
        String card_replacement_request3 = sh.getString("card_replacement_request3","");
        String card_replacement_request_submitted_details3 = sh.getString("card_replacement_request_submitted_details3","");
        String pawning_remarks3 = sh.getString("pawning_remarks3","");

        String card_holder_name4 = sh.getString("card_holder_name4","");
        String card_number_system_generated4 = sh.getString("card_number_system_generated4","");
        String card_number_inputted4 = sh.getString("card_number_inputted4","");
        String card_number_series4 = sh.getString("card_number_series4","");
        String distribution_status4 = sh.getString("distribution_status4","");
        String release_date4 = sh.getString("release_date4","");
        String release_by4 = sh.getString("release_by4","");
        String release_place4 = sh.getString("release_place4","");
        String card_physically_presented4 = sh.getString("card_physically_presented4","");
        String card_pin_is_attached4 = sh.getString("card_pin_is_attached4","");
        String reason_not_presented4 = sh.getString("reason_not_presented4","");
        String others_reason_not_presented4 = sh.getString("others_reason_not_presented4","");
        String others_reason_unclaimed4 = sh.getString("others_reason_unclaimed4","");
        String reason_unclaimed4 = sh.getString("reason_unclaimed4","");
        String card_replacement_request4 = sh.getString("card_replacement_request4","");
        String card_replacement_request_submitted_details4 = sh.getString("card_replacement_request_submitted_details4","");
        String pawning_remarks4 = sh.getString("pawning_remarks4","");

        String card_holder_name5= sh.getString("card_holder_name5","");
        String card_number_system_generated5 = sh.getString("card_number_system_generated5","");
        String card_number_inputted5 = sh.getString("card_number_inputted5","");
        String card_number_series5 = sh.getString("card_number_series5","");
        String distribution_status5 = sh.getString("distribution_status5","");
        String release_date5 = sh.getString("release_date5","");
        String release_by5 = sh.getString("release_by5","");
        String release_place5 = sh.getString("release_place5","");
        String card_physically_presented5 = sh.getString("card_physically_presented5","");
        String card_pin_is_attached5 = sh.getString("card_pin_is_attached5","");
        String reason_not_presented5 = sh.getString("reason_not_presented5","");
        String others_reason_not_presented5 = sh.getString("others_reason_not_presented5","");
        String others_reason_unclaimed5 = sh.getString("others_reason_unclaimed5","");
        String reason_unclaimed5 = sh.getString("reason_unclaimed5","");
        String card_replacement_request5 = sh.getString("card_replacement_request5","");
        String card_replacement_request_submitted_details5 = sh.getString("card_replacement_request_submitted_details5","");
        String pawning_remarks5 = sh.getString("pawning_remarks5","");
        Integer card_count = sh.getInt("card_count", 0);

        String card_number_prefilled1 = sh.getString("card_number_prefilled1", "");
        String card_number_prefilled2 = sh.getString("card_number_prefilled2", "");
        String card_number_prefilled3 = sh.getString("card_number_prefilled3", "");
        String card_number_prefilled4 = sh.getString("card_number_prefilled4", "");
        String card_number_prefilled5 = sh.getString("card_number_prefilled5", "");
        Log.v(TAG,"distribution_status_record" + distribution_status_record);
        Log.v(TAG,"distribution_status_record1" + distribution_status_record1);
        Log.v(TAG,"distribution_status_record2" + distribution_status_record2);
        Log.v(TAG,"distribution_status_record3" + distribution_status_record3);
        Log.v(TAG,"distribution_status_record4" + distribution_status_record4);

        new SweetAlertDialog(ScanCashCard.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Save data?")
            .setContentText("Please confirm to save data")
            .setConfirmText("Confirm")
            .setCancelText("Cancel")
            .showCancelButton(true)
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {

                    sqLiteHelper.insertDatabase(household_id, first_name, last_name, middle_name, ext_name, sex, province_code, municipality_code,barangay_code,set,
                                lender_name, lender_address, date_pawned, date_retrieved, loaned_amount, status, reason, interest, offense_history, offense_date, remarks, staff_intervention, other_details,
                                amount, date_claimed, nma_reason, nma_remarks,
                                hh_status, contact_no, contact_no_of, is_grantee, is_minor, relationship_to_grantee, assigned_staff, representative_name,sync_at, user_id,emv_validation_id,
                                card_number_prefilled, card_number_system_generated,  card_number_unputted,  card_number_series,  distribution_status,  release_date,  release_by,  release_place,  card_physically_presented,  card_pin_is_attached,  reason_not_presented, reason_unclaimed,  card_replacement_request,  card_replacement_submitted_details,  emv_monitoring_id,
                                card_holder_name1,card_number_system_generated1, card_number_inputted1, card_number_series1, distribution_status1, release_date1, release_by1, release_place1, card_physically_presented1, card_pin_is_attached1, reason_not_presented1, reason_unclaimed1, card_replacement_request1, card_replacement_request_submitted_details1, pawning_remarks1,
                                card_holder_name2,card_number_system_generated2, card_number_inputted2, card_number_series2, distribution_status2,  release_date2, release_by2, release_place2, card_physically_presented2, card_pin_is_attached2, reason_not_presented2, reason_unclaimed2, card_replacement_request2, card_replacement_request_submitted_details2, pawning_remarks2,
                                card_holder_name3,card_number_system_generated3, card_number_inputted3, card_number_series3, distribution_status3, release_date3,  release_by3, release_place3, card_physically_presented3, card_pin_is_attached3, reason_not_presented3, reason_unclaimed3, card_replacement_request3, card_replacement_request_submitted_details3, pawning_remarks3,
                                card_holder_name4,card_number_system_generated4, card_number_inputted4, card_number_series4, distribution_status4, release_date4, release_by4, release_place4, card_physically_presented4, card_pin_is_attached4, reason_not_presented4, reason_unclaimed4, card_replacement_request4, card_replacement_request_submitted_details4, pawning_remarks4,
                                card_holder_name5,card_number_system_generated5, card_number_inputted5, card_number_series5, distribution_status5, release_date5, release_by5, release_place5, card_physically_presented5, card_pin_is_attached5, reason_not_presented5, reason_unclaimed5, card_replacement_request5, card_replacement_request_submitted_details5, pawning_remarks5, card_count,
                                imageViewToByte(ScannedImage),
                                imageViewToByte(mGrantee),
                                imageViewToByte(imgAdditionalId),
                                imageViewToByte(ivOtherScannedImage1),
                                imageViewToByte(ivOtherScannedImage2),
                                imageViewToByte(ivOtherScannedImage3),
                                imageViewToByte(ivOtherScannedImage4),
                                imageViewToByte(ivOtherScannedImage5), overall_remarks, other_ext_name, contact_no_of_others, others_reason_not_presented, others_reason_not_presented1, others_reason_not_presented2, others_reason_not_presented3, others_reason_not_presented4, others_reason_not_presented5, others_reason_unclaimed, others_reason_unclaimed1, others_reason_unclaimed2, others_reason_unclaimed3, others_reason_unclaimed4, others_reason_unclaimed5, nma_others_reason,nma_non_emv,nma_card_name,
                                distribution_status_record,distribution_status_record1,distribution_status_record2,distribution_status_record3,distribution_status_record4,distribution_status_record5,
                                release_date_record,release_date_record1,release_date_record2,release_date_record3,release_date_record4,release_date_record5,
                                card_number_prefilled1,card_number_prefilled2,card_number_prefilled3,card_number_prefilled4,card_number_prefilled5);



                    load_loading_bar();
                    edt_overall_remarks.setEnabled(false);

                    clear_preferences();
                    sDialog.dismiss();

                    new android.os.Handler(Looper.getMainLooper()).postDelayed(
                            new Runnable() {
                                public void run() {
                                    hide_loading_bar();
//                                    int current = viewPager.getCurrentItem();
//                                    viewPager.setCurrentItem(current-3);
                                    Intent intent = new Intent(ScanCashCard.this, ScanCashCard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            300);
                }
            })
            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sDialog.dismiss();
                }
            }).show();
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

    public void xml_initialization(int pos) {
        switch (pos) {
            case 1:
                til_hh_id = findViewById(R.id.til_hh_id);
                til_set = findViewById(R.id.til_set);
                til_last_name = findViewById(R.id.til_last_name);
                til_first_name = findViewById(R.id.til_first_name);
                til_middle_name = findViewById(R.id.til_middle_name);
                til_ext_name = findViewById(R.id.til_ext_name);
                til_other_ext_name = findViewById(R.id.til_other_ext_name);
                til_hh_status = findViewById(R.id.til_hh_status);
                til_province_code = findViewById(R.id.til_province_code);
                til_municipality_code = findViewById(R.id.til_municipality_code);
                til_barangay_code = findViewById(R.id.til_barangay_code);
                til_sex = findViewById(R.id.til_sex);
                til_is_grantee = findViewById(R.id.til_is_grantee);
                til_representative_name = findViewById(R.id.til_representative_name);


                til_relationship_to_grantee = findViewById(R.id.til_relationship_to_grantee);
                til_contact_no = findViewById(R.id.til_contact_no);
                til_contact_no_of = findViewById(R.id.til_contact_no_of);
                til_contact_no_of_others = findViewById(R.id.til_contact_no_of_others);
                til_is_minor = findViewById(R.id.til_is_minor);
                til_assigned_staff = findViewById(R.id.til_assigned_staff);
                edt_hh_id = findViewById(R.id.edt_hh_id);
                aat_set = findViewById(R.id.aat_set);
                edt_last_name = findViewById(R.id.edt_last_name);
                edt_first_name = findViewById(R.id.edt_first_name);
                edt_middle_name = findViewById(R.id.edt_middle_name);
                aat_ext_name = findViewById(R.id.aat_ext_name);
                edt_other_ext_name = findViewById(R.id.edt_other_ext_name);
                aat_hh_status = findViewById(R.id.aat_hh_status);
                aat_province_code = findViewById(R.id.aat_province_code);
                aat_municipality_code = findViewById(R.id.aat_municipality_code);
                aat_barangay_code = findViewById(R.id.aat_barangay_code);
                aat_sex = findViewById(R.id.aat_sex);
                aat_is_grantee = findViewById(R.id.aat_is_grantee);
                edt_representative_name = findViewById(R.id.edt_representative_name);
                aat_relationship_to_grantee = findViewById(R.id.aat_relationship_to_grantee);
                edt_contact_no = findViewById(R.id.edt_contact_no);
                aat_contact_no_of = findViewById(R.id.aat_contact_no_of);
                edt_contact_no_of_others = findViewById(R.id.edt_contact_no_of_others);
                aat_is_minor = findViewById(R.id.aat_is_minor);
                edt_assigned_staff = findViewById(R.id.edt_assigned_staff);

                break;
            case 2:
                til_card_number_prefilled = findViewById(R.id.til_card_number_prefilled);
                til_distribution_status = findViewById(R.id.til_distribution_status);
                til_release_date = findViewById(R.id.til_release_date);
                til_release_by = findViewById(R.id.til_release_by);
                til_release_place = findViewById(R.id.til_release_place);
                til_card_physically_presented = findViewById(R.id.til_card_physically_presented);
                til_card_pin_is_attached = findViewById(R.id.til_card_pin_is_attached);
                til_reason_not_presented = findViewById(R.id.til_reason_not_presented);
                til_others_reason_not_presented = findViewById(R.id.til_others_reason_not_presented);
                til_reason_unclaimed = findViewById(R.id.til_reason_unclaimed);
                til_others_reason_unclaimed = findViewById(R.id.til_others_reason_unclaimed);
                til_card_replacement_request = findViewById(R.id.til_card_replacement_request);
                til_card_replacement_request_submitted_details = findViewById(R.id.til_card_replacement_request_submitted_details);

                til_current_scan_btn = findViewById(R.id.til_current_scan_btn);

                aat_distribution_status_record = findViewById(R.id.aat_distribution_status_record);
                edt_release_date_record = findViewById(R.id.edt_release_date_record);

                aat_distribution_status_record1 = findViewById(R.id.aat_distribution_status_record1);
                aat_distribution_status_record2 = findViewById(R.id.aat_distribution_status_record2);
                aat_distribution_status_record3 = findViewById(R.id.aat_distribution_status_record3);
                aat_distribution_status_record4 = findViewById(R.id.aat_distribution_status_record4);
                aat_distribution_status_record5 = findViewById(R.id.aat_distribution_status_record5);

                edt_card_number_prefilled = findViewById(R.id.edt_card_number_prefilled);
                aat_distribution_status = findViewById(R.id.aat_distribution_status);

                edt_release_date = findViewById(R.id.edt_release_date);
                edt_release_date_record1 = findViewById(R.id.edt_release_date_record1);
                edt_release_date_record2 = findViewById(R.id.edt_release_date_record2);
                edt_release_date_record3 = findViewById(R.id.edt_release_date_record3);
                edt_release_date_record4 = findViewById(R.id.edt_release_date_record4);
                edt_release_date_record5 = findViewById(R.id.edt_release_date_record5);

                edt_release_by = findViewById(R.id.edt_release_by);
                edt_release_place = findViewById(R.id.edt_release_place);
                aat_card_physically_presented = findViewById(R.id.aat_card_physically_presented);
                aat_card_pin_is_attached = findViewById(R.id.aat_card_pin_is_attached);
                aat_reason_not_presented = findViewById(R.id.aat_reason_not_presented);
                edt_others_reason_not_presented = findViewById(R.id.edt_others_reason_not_presented);
                aat_reason_unclaimed = findViewById(R.id.aat_reason_unclaimed);
                edt_others_reason_unclaimed = findViewById(R.id.edt_others_reason_unclaimed);
                aat_card_replacement_request = findViewById(R.id.aat_card_replacement_request);
                edt_card_replacement_request_submitted_details = findViewById(R.id.edt_card_replacement_request_submitted_details);

                ScannedImage = findViewById(R.id.ScannedImage);
                imgUri = findViewById(R.id.imgUri);
                btn_scanCashCard = findViewById(R.id.btn_scanCashCard);

                til_card_number_inputted = findViewById(R.id.til_card_number_inputted);
                til_card_number_series = findViewById(R.id.til_card_number_series);
                til_id_exists = findViewById(R.id.til_id_exists);
                edt_card_number_inputted = findViewById(R.id.edt_card_number_inputted);
                edt_card_number_inputted1 = findViewById(R.id.edt_card_number_inputted1);
                edt_card_number_inputted2 = findViewById(R.id.edt_card_number_inputted2);
                edt_card_number_inputted3 = findViewById(R.id.edt_card_number_inputted3);
                edt_card_number_inputted4 = findViewById(R.id.edt_card_number_inputted4);
                edt_card_number_inputted5 = findViewById(R.id.edt_card_number_inputted5);
                edt_card_number_series = findViewById(R.id.edt_card_number_series);
                aat_id_exists = findViewById(R.id.aat_id_exists);
                imgAdditionalId = findViewById(R.id.imgAdditionalId);
                til_additionalID = findViewById(R.id.til_additionalID);
                btn_scanID = findViewById(R.id.btn_scanID);
                mGrantee = findViewById(R.id.mGrantee);
                tilGrantee = findViewById(R.id.tilGrantee);
                btn_grantee = findViewById(R.id.btn_grantee);
                mcvPawning = findViewById(R.id.pawning);
                ll_additional_id_layout = findViewById(R.id.ll_additional_id_layout);

                //PAWNING
                edt_lender_name = findViewById(R.id.edt_lender_name);
                edt_date_pawned = findViewById(R.id.edt_date_pawned);
                edt_loan_amount = findViewById(R.id.edt_loan_amount);
                edt_lender_address = findViewById(R.id.edt_lender_address);
                edt_date_retrieved = findViewById(R.id.edt_date_retrieved);
                edt_interest = findViewById(R.id.edt_interest);
                aat_status = findViewById(R.id.aat_status);
                edt_reason = findViewById(R.id.edt_reason);
                aat_offense_history =findViewById(R.id.aat_offense_history);
                edt_offense_date = findViewById(R.id.edt_offense_date);
                edt_remarks = findViewById(R.id.edt_remarks);
                edt_staff_intervention = findViewById(R.id.edt_staff_intervention);
                edt_other_details = findViewById(R.id.edt_other_details);
                til_lender_name = findViewById(R.id.til_lender_name);

//                Other Details 1 - 5
                otherCardAvailability1 = findViewById(R.id.otherCardAvailability1);
                otherCardAvailability2 = findViewById(R.id.otherCardAvailability2);
                otherCardAvailability3 = findViewById(R.id.otherCardAvailability3);
                otherCardAvailability4 = findViewById(R.id.otherCardAvailability4);
                otherCardAvailability5 = findViewById(R.id.otherCardAvailability5);

                btnCancelOtherCard1 = findViewById(R.id.btnCancelOtherCard1);
                btnCancelOtherCard2 = findViewById(R.id.btnCancelOtherCard2);
                btnCancelOtherCard3 = findViewById(R.id.btnCancelOtherCard3);
                btnCancelOtherCard4 = findViewById(R.id.btnCancelOtherCard4);
                btnCancelOtherCard5 = findViewById(R.id.btnCancelOtherCard5);

                til_card_number_prefilled1 = findViewById(R.id.til_card_number_prefilled1);
                til_card_number_prefilled2 = findViewById(R.id.til_card_number_prefilled2);
                til_card_number_prefilled3 = findViewById(R.id.til_card_number_prefilled3);
                til_card_number_prefilled4 = findViewById(R.id.til_card_number_prefilled4);
                til_card_number_prefilled5 = findViewById(R.id.til_card_number_prefilled5);

                edt_card_number_prefilled1 = findViewById(R.id.edt_card_number_prefilled1);
                edt_card_number_prefilled2 = findViewById(R.id.edt_card_number_prefilled2);
                edt_card_number_prefilled3 = findViewById(R.id.edt_card_number_prefilled3);
                edt_card_number_prefilled4 = findViewById(R.id.edt_card_number_prefilled4);
                edt_card_number_prefilled5 = findViewById(R.id.edt_card_number_prefilled5);

                til_card_holder_name1 = findViewById(R.id.til_card_holder_name1);
                til_card_holder_name2 = findViewById(R.id.til_card_holder_name2);
                til_card_holder_name3 = findViewById(R.id.til_card_holder_name3);
                til_card_holder_name4 = findViewById(R.id.til_card_holder_name4);
                til_card_holder_name5 = findViewById(R.id.til_card_holder_name5);

                edt_card_holder_name1 = findViewById(R.id.edt_card_holder_name1);
                edt_card_holder_name2 = findViewById(R.id.edt_card_holder_name2);
                edt_card_holder_name3 = findViewById(R.id.edt_card_holder_name3);
                edt_card_holder_name4 = findViewById(R.id.edt_card_holder_name4);
                edt_card_holder_name5 = findViewById(R.id.edt_card_holder_name5);

                til_distribution_status1 = findViewById(R.id.til_distribution_status1);
                til_distribution_status2 = findViewById(R.id.til_distribution_status2);
                til_distribution_status3 = findViewById(R.id.til_distribution_status3);
                til_distribution_status4 = findViewById(R.id.til_distribution_status4);
                til_distribution_status5 = findViewById(R.id.til_distribution_status5);

                aat_distribution_status1 = findViewById(R.id.aat_distribution_status1);
                aat_distribution_status2 = findViewById(R.id.aat_distribution_status2);
                aat_distribution_status3 = findViewById(R.id.aat_distribution_status3);
                aat_distribution_status4 = findViewById(R.id.aat_distribution_status4);
                aat_distribution_status5 = findViewById(R.id.aat_distribution_status5);

                til_release_date1 = findViewById(R.id.til_release_date1);
                til_release_date2 = findViewById(R.id.til_release_date2);
                til_release_date3 = findViewById(R.id.til_release_date3);
                til_release_date4 = findViewById(R.id.til_release_date4);
                til_release_date5 = findViewById(R.id.til_release_date5);

                edt_release_date1 = findViewById(R.id.edt_release_date1);
                edt_release_date2 = findViewById(R.id.edt_release_date2);
                edt_release_date3 = findViewById(R.id.edt_release_date3);
                edt_release_date4 = findViewById(R.id.edt_release_date4);
                edt_release_date5 = findViewById(R.id.edt_release_date5);

                til_release_by1 = findViewById(R.id.til_release_by1);
                til_release_by2 = findViewById(R.id.til_release_by2);
                til_release_by3 = findViewById(R.id.til_release_by3);
                til_release_by4 = findViewById(R.id.til_release_by4);
                til_release_by5 = findViewById(R.id.til_release_by5);

                edt_release_by1 = findViewById(R.id.edt_release_by1);
                edt_release_by2 = findViewById(R.id.edt_release_by2);
                edt_release_by3 = findViewById(R.id.edt_release_by3);
                edt_release_by4 = findViewById(R.id.edt_release_by4);
                edt_release_by5 = findViewById(R.id.edt_release_by5);

                til_release_place1 = findViewById(R.id.til_release_place1);
                til_release_place2 = findViewById(R.id.til_release_place2);
                til_release_place3 = findViewById(R.id.til_release_place3);
                til_release_place4 = findViewById(R.id.til_release_place4);
                til_release_place5 = findViewById(R.id.til_release_place5);

                edt_release_place1 = findViewById(R.id.edt_release_place1);
                edt_release_place2 = findViewById(R.id.edt_release_place2);
                edt_release_place3 = findViewById(R.id.edt_release_place3);
                edt_release_place4 = findViewById(R.id.edt_release_place4);
                edt_release_place5 = findViewById(R.id.edt_release_place5);

                til_card_physically_presented1 = findViewById(R.id.til_card_physically_presented1);
                til_card_physically_presented2 = findViewById(R.id.til_card_physically_presented2);
                til_card_physically_presented3 = findViewById(R.id.til_card_physically_presented3);
                til_card_physically_presented4 = findViewById(R.id.til_card_physically_presented4);
                til_card_physically_presented5 = findViewById(R.id.til_card_physically_presented5);

                aat_card_physically_presented1 = findViewById(R.id.aat_card_physically_presented1);
                aat_card_physically_presented2 = findViewById(R.id.aat_card_physically_presented2);
                aat_card_physically_presented3 = findViewById(R.id.aat_card_physically_presented3);
                aat_card_physically_presented4 = findViewById(R.id.aat_card_physically_presented4);
                aat_card_physically_presented5 = findViewById(R.id.aat_card_physically_presented5);

                til_card_pin_is_attached1 = findViewById(R.id.til_card_pin_is_attached1);
                til_card_pin_is_attached2 = findViewById(R.id.til_card_pin_is_attached2);
                til_card_pin_is_attached3 = findViewById(R.id.til_card_pin_is_attached3);
                til_card_pin_is_attached4 = findViewById(R.id.til_card_pin_is_attached4);
                til_card_pin_is_attached5 = findViewById(R.id.til_card_pin_is_attached5);

                aat_card_pin_is_attached1 = findViewById(R.id.aat_card_pin_is_attached1);
                aat_card_pin_is_attached2 = findViewById(R.id.aat_card_pin_is_attached2);
                aat_card_pin_is_attached3 = findViewById(R.id.aat_card_pin_is_attached3);
                aat_card_pin_is_attached4 = findViewById(R.id.aat_card_pin_is_attached4);
                aat_card_pin_is_attached5 = findViewById(R.id.aat_card_pin_is_attached5);

                til_reason_not_presented1 = findViewById(R.id.til_reason_not_presented1);
                til_reason_not_presented2 = findViewById(R.id.til_reason_not_presented2);
                til_reason_not_presented3 = findViewById(R.id.til_reason_not_presented3);
                til_reason_not_presented4 = findViewById(R.id.til_reason_not_presented4);
                til_reason_not_presented5 = findViewById(R.id.til_reason_not_presented5);

                aat_reason_not_presented1 = findViewById(R.id.aat_reason_not_presented1);
                aat_reason_not_presented2 = findViewById(R.id.aat_reason_not_presented2);
                aat_reason_not_presented3 = findViewById(R.id.aat_reason_not_presented3);
                aat_reason_not_presented4 = findViewById(R.id.aat_reason_not_presented4);
                aat_reason_not_presented5 = findViewById(R.id.aat_reason_not_presented5);

                til_others_reason_not_presented1 = findViewById(R.id.til_others_reason_not_presented1);
                til_others_reason_not_presented2 = findViewById(R.id.til_others_reason_not_presented2);
                til_others_reason_not_presented3 = findViewById(R.id.til_others_reason_not_presented3);
                til_others_reason_not_presented4 = findViewById(R.id.til_others_reason_not_presented4);
                til_others_reason_not_presented5 = findViewById(R.id.til_others_reason_not_presented5);

                edt_others_reason_not_presented1 = findViewById(R.id.edt_others_reason_not_presented1);
                edt_others_reason_not_presented2 = findViewById(R.id.edt_others_reason_not_presented2);
                edt_others_reason_not_presented3 = findViewById(R.id.edt_others_reason_not_presented3);
                edt_others_reason_not_presented4 = findViewById(R.id.edt_others_reason_not_presented4);
                edt_others_reason_not_presented5 = findViewById(R.id.edt_others_reason_not_presented5);

                til_reason_unclaimed1 = findViewById(R.id.til_reason_unclaimed1);
                til_reason_unclaimed2 = findViewById(R.id.til_reason_unclaimed2);
                til_reason_unclaimed3 = findViewById(R.id.til_reason_unclaimed3);
                til_reason_unclaimed4 = findViewById(R.id.til_reason_unclaimed4);
                til_reason_unclaimed5 = findViewById(R.id.til_reason_unclaimed5);

                aat_reason_unclaimed1 = findViewById(R.id.aat_reason_unclaimed1);
                aat_reason_unclaimed2 = findViewById(R.id.aat_reason_unclaimed2);
                aat_reason_unclaimed3 = findViewById(R.id.aat_reason_unclaimed3);
                aat_reason_unclaimed4 = findViewById(R.id.aat_reason_unclaimed4);
                aat_reason_unclaimed5 = findViewById(R.id.aat_reason_unclaimed5);

                til_others_reason_unclaimed1 = findViewById(R.id.til_others_reason_unclaimed1);
                til_others_reason_unclaimed2 = findViewById(R.id.til_others_reason_unclaimed2);
                til_others_reason_unclaimed3 = findViewById(R.id.til_others_reason_unclaimed3);
                til_others_reason_unclaimed4 = findViewById(R.id.til_others_reason_unclaimed4);
                til_others_reason_unclaimed5 = findViewById(R.id.til_others_reason_unclaimed5);

                edt_others_reason_unclaimed1 = findViewById(R.id.edt_others_reason_unclaimed1);
                edt_others_reason_unclaimed2 = findViewById(R.id.edt_others_reason_unclaimed2);
                edt_others_reason_unclaimed3 = findViewById(R.id.edt_others_reason_unclaimed3);
                edt_others_reason_unclaimed4 = findViewById(R.id.edt_others_reason_unclaimed4);
                edt_others_reason_unclaimed5 = findViewById(R.id.edt_others_reason_unclaimed5);

                til_card_replacement_request1 = findViewById(R.id.til_card_replacement_request1);
                til_card_replacement_request2 = findViewById(R.id.til_card_replacement_request2);
                til_card_replacement_request3 = findViewById(R.id.til_card_replacement_request3);
                til_card_replacement_request4 = findViewById(R.id.til_card_replacement_request4);
                til_card_replacement_request5 = findViewById(R.id.til_card_replacement_request5);

                aat_card_replacement_request1 = findViewById(R.id.aat_card_replacement_request1);
                aat_card_replacement_request2 = findViewById(R.id.aat_card_replacement_request2);
                aat_card_replacement_request3 = findViewById(R.id.aat_card_replacement_request3);
                aat_card_replacement_request4 = findViewById(R.id.aat_card_replacement_request4);
                aat_card_replacement_request5 = findViewById(R.id.aat_card_replacement_request5);

                til_card_replacement_request_submitted_details1 = findViewById(R.id.til_card_replacement_request_submitted_details1);
                til_card_replacement_request_submitted_details2 = findViewById(R.id.til_card_replacement_request_submitted_details2);
                til_card_replacement_request_submitted_details3 = findViewById(R.id.til_card_replacement_request_submitted_details3);
                til_card_replacement_request_submitted_details4 = findViewById(R.id.til_card_replacement_request_submitted_details4);
                til_card_replacement_request_submitted_details5 = findViewById(R.id.til_card_replacement_request_submitted_details5);

                edt_card_replacement_request_submitted_details1 = findViewById(R.id.edt_card_replacement_request_submitted_details1);
                edt_card_replacement_request_submitted_details2 = findViewById(R.id.edt_card_replacement_request_submitted_details2);
                edt_card_replacement_request_submitted_details3 = findViewById(R.id.edt_card_replacement_request_submitted_details3);
                edt_card_replacement_request_submitted_details4 = findViewById(R.id.edt_card_replacement_request_submitted_details4);
                edt_card_replacement_request_submitted_details5 = findViewById(R.id.edt_card_replacement_request_submitted_details5);

                til_pawning_remarks1 = findViewById(R.id.til_pawning_remarks1);
                til_pawning_remarks2 = findViewById(R.id.til_pawning_remarks2);
                til_pawning_remarks3 = findViewById(R.id.til_pawning_remarks3);
                til_pawning_remarks4 = findViewById(R.id.til_pawning_remarks4);
                til_pawning_remarks5 = findViewById(R.id.til_pawning_remarks5);

                edt_pawning_remarks1 = findViewById(R.id.edt_pawning_remarks1);
                edt_pawning_remarks2 = findViewById(R.id.edt_pawning_remarks2);
                edt_pawning_remarks3 = findViewById(R.id.edt_pawning_remarks3);
                edt_pawning_remarks4 = findViewById(R.id.edt_pawning_remarks4);
                edt_pawning_remarks5 = findViewById(R.id.edt_pawning_remarks5);

                ivOtherScannedImage1 = findViewById(R.id.ivOtherScannedImage1);
                ivOtherScannedImage2 = findViewById(R.id.ivOtherScannedImage2);
                ivOtherScannedImage3 = findViewById(R.id.ivOtherScannedImage3);
                ivOtherScannedImage4 = findViewById(R.id.ivOtherScannedImage4);
                ivOtherScannedImage5 = findViewById(R.id.ivOtherScannedImage5);

                ivOtherScannedImageUrl1 = findViewById(R.id.ivOtherScannedImageUrl1);
                ivOtherScannedImageUrl2 = findViewById(R.id.ivOtherScannedImageUrl2);
                ivOtherScannedImageUrl3 = findViewById(R.id.ivOtherScannedImageUrl3);
                ivOtherScannedImageUrl4 = findViewById(R.id.ivOtherScannedImageUrl4);
                ivOtherScannedImageUrl5 = findViewById(R.id.ivOtherScannedImageUrl5);

                btnOtherScanned1 = findViewById(R.id.btnOtherScanned1);
                btnOtherScanned2 = findViewById(R.id.btnOtherScanned2);
                btnOtherScanned3 = findViewById(R.id.btnOtherScanned3);
                btnOtherScanned4 = findViewById(R.id.btnOtherScanned4);
                btnOtherScanned5 = findViewById(R.id.btnOtherScanned5);

                til_card_number_inputted1 = findViewById(R.id.til_card_number_inputted1);
                til_card_number_inputted2 = findViewById(R.id.til_card_number_inputted2);
                til_card_number_inputted3 = findViewById(R.id.til_card_number_inputted3);
                til_card_number_inputted4 = findViewById(R.id.til_card_number_inputted4);
                til_card_number_inputted5 = findViewById(R.id.til_card_number_inputted5);

                edt_card_number_inputted1 = findViewById(R.id.edt_card_number_inputted1);
                edt_card_number_inputted2 = findViewById(R.id.edt_card_number_inputted2);
                edt_card_number_inputted3 = findViewById(R.id.edt_card_number_inputted3);
                edt_card_number_inputted4 = findViewById(R.id.edt_card_number_inputted4);
                edt_card_number_inputted5 = findViewById(R.id.edt_card_number_inputted5);

                til_card_number_series1 = findViewById(R.id.til_card_number_series1);
                til_card_number_series2 = findViewById(R.id.til_card_number_series2);
                til_card_number_series3 = findViewById(R.id.til_card_number_series3);
                til_card_number_series4 = findViewById(R.id.til_card_number_series4);
                til_card_number_series5 = findViewById(R.id.til_card_number_series5);

                edt_card_number_series1 = findViewById(R.id.edt_card_number_series1);
                edt_card_number_series2 = findViewById(R.id.edt_card_number_series2);
                edt_card_number_series3 = findViewById(R.id.edt_card_number_series3);
                edt_card_number_series4 = findViewById(R.id.edt_card_number_series4);
                edt_card_number_series5 = findViewById(R.id.edt_card_number_series5);
                break;
            case 3:
                til_nma_amount = findViewById(R.id.til_nma_amount);
                til_nma_reason = findViewById(R.id.til_nma_reason);
                til_nma_others_reason = findViewById(R.id.til_nma_others_reason);
                til_nma_date_claimed = findViewById(R.id.til_nma_date_claimed);
                til_nma_remarks = findViewById(R.id.til_nma_remarks);

                edt_non_emv = findViewById(R.id.edt_non_emv);
                edt_card_name = findViewById(R.id.edt_card_name);
                edt_nma_amount = findViewById(R.id.edt_nma_amount);
                aat_nma_reason = findViewById(R.id.aat_nma_reason);
                edt_nma_others_reason = findViewById(R.id.edt_nma_others_reason);
                edt_nma_date_claimed = findViewById(R.id.edt_nma_date_claimed);
                edt_nma_remarks = findViewById(R.id.edt_nma_remarks);
                edt_nma_amount.setEnabled(false);
                edt_non_emv.setEnabled(false);
                edt_card_name.setEnabled(false);
                break;
            case 4:
                til_overall_remarks = findViewById(R.id.til_overall_remarks);
                edt_overall_remarks = findViewById(R.id.edt_overall_remarks);
                break;

        }
    }

    public void nextValidation(){

        pressNext =true;
        isValidationError = 0;
        required_field = "This field is required!";
        required_cc_length = "This field value must have 19 digits";
        required_cc_invalid_format = "Invalid format!";
        required_length = "This field value must have 10 digits!";
        required_btn = "Required!";

        int current = getItem(1);
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        if (current == 1) {

            pressNext =false;

            xml_initialization(1);

            String hh_id = edt_hh_id.getText().toString();
            String hh_set = aat_set.getText().toString();
            String last_name = edt_last_name.getText().toString();
            String first_name = edt_first_name.getText().toString();
            String middle_name = edt_middle_name.getText().toString();
            String ext_name = aat_ext_name.getText().toString();
            String other_ext_name = edt_other_ext_name.getText().toString();
            String hh_status = aat_hh_status.getText().toString();
            String province = aat_province_code.getText().toString();
            String municipality = aat_municipality_code.getText().toString();
            String barangay = aat_barangay_code.getText().toString();
            String sex = aat_sex.getText().toString();
            String is_grantee = aat_is_grantee.getText().toString();
            String representative_name = edt_representative_name.getText().toString();
            String relationship_to_grantee = aat_relationship_to_grantee.getText().toString();
            String contact_no = edt_contact_no.getText().toString();
            String contact_no_of = aat_contact_no_of.getText().toString();
            String contact_no_of_others = edt_contact_no_of_others.getText().toString();
            String assigned_staff = edt_assigned_staff.getText().toString();
            String is_minor = aat_is_minor.getText().toString();

            if (hh_id.matches("")){
                til_hh_id.setError(required_field);
                isValidationError++;
            } else {
                til_hh_id.setError(null);
            }

            if (hh_set.matches("")){
                til_set.setError(required_field);
                isValidationError++;
            } else {
                til_set.setError(null);
            }

            if (last_name.matches("")){
                til_last_name.setError(required_field);
                isValidationError++;
            } else {
                til_last_name.setError(null);
            }

            if (first_name.matches("")){
                til_first_name.setError(required_field);
                isValidationError++;
            } else {
                til_first_name.setError(null);
            }

            if (ext_name.matches("Others")) {
                if (other_ext_name.matches("")) {
                    til_other_ext_name.setError(required_field);
                    isValidationError++;
                } else {
                    til_other_ext_name.setError(null);
                }
            }

            if (hh_status.matches("")){
                til_hh_status.setError(required_field);
                isValidationError++;
            } else {
                til_hh_status.setError(null);
            }

            if (province.matches("")){
                til_province_code.setError(required_field);
                isValidationError++;
            } else {
                til_province_code.setError(null);
            }

            if (municipality.matches("")){
                til_municipality_code.setError(required_field);
                isValidationError++;
            } else {
                til_municipality_code.setError(null);
            }

            if (barangay.matches("")){
                til_barangay_code.setError(required_field);
                isValidationError++;
            } else {
                til_barangay_code.setError(null);
            }

            if (sex.matches("")){
                til_sex.setError(required_field);
                isValidationError++;
            } else {
                til_sex.setError(null);
            }

            if (is_grantee.matches("")){
                til_is_grantee.setError(required_field);
                isValidationError++;
            } else {
                til_is_grantee.setError(null);

                if (is_grantee.matches("Representative")) {
                    if (relationship_to_grantee.matches("")){
                        til_relationship_to_grantee.setError(required_field);
                        isValidationError++;
                    } else {
                        til_relationship_to_grantee.setError(null);
                    }
                    if (representative_name.matches("")) {
                        til_representative_name.setError(required_field);
                        isValidationError++;
                    } else {
                        til_representative_name.setError(null);
                    }
                }

            }

            if (contact_no.matches("") || contact_no.length() != 10){
                String new_required_field = contact_no.matches("") ? required_field : required_length;
                til_contact_no.setError(new_required_field);
                isValidationError++;
            } else {
                til_contact_no.setError(null);

                if (contact_no.length() == 10) {
                    if (contact_no_of.matches("")) {
                        til_contact_no_of.setError(required_field);
                        isValidationError++;
                    } else {
                        til_contact_no_of.setError(null);

                        if (contact_no_of.matches("Others")) {
                            if (contact_no_of_others.matches("")) {
                                til_contact_no_of_others.setError(required_field);
                                isValidationError++;
                            } else {
                                til_contact_no_of_others.setError(null);
                            }
                        }
                    }
                }
            }

            store_preferences(1);

        } else if (current == 2) {
            pressNext =false;

            xml_initialization(2);


            //old_data
            String distribution_status = aat_distribution_status.getText().toString();
            String release_date = edt_release_date.getText().toString();
            String release_by = edt_release_by.getText().toString();
            String release_place = edt_release_place.getText().toString();
            String card_physically_presented = aat_card_physically_presented.getText().toString();
            String card_pin_is_attached = aat_card_pin_is_attached.getText().toString();
            String reason_not_presented = aat_reason_not_presented.getText().toString();
            String others_reason_not_presented = edt_others_reason_not_presented.getText().toString();
            String reason_unclaimed = aat_reason_unclaimed.getText().toString();
            String others_reason_unclaimed = edt_others_reason_unclaimed.getText().toString();
            String card_replacement_request = aat_card_replacement_request.getText().toString();
            String card_replacement_request_submitted_details = edt_card_replacement_request_submitted_details.getText().toString();
            String card_number_inputted = edt_card_number_inputted.getText().toString();
            String card_number_series = edt_card_number_series.getText().toString();
            String id_exists = aat_id_exists.getText().toString();

            String lender_name = edt_lender_name.getText().toString();
            String date_pawned = edt_date_pawned.getText().toString();
            String loan_amount = edt_loan_amount.getText().toString();
            String lender_address = edt_lender_address.getText().toString();
            String date_retrieved = edt_date_retrieved.getText().toString();
            String interest = edt_interest.getText().toString();
            String status = aat_status.getText().toString();
            String reason = edt_reason.getText().toString();
            String offense_history = aat_offense_history.getText().toString();
            String offense_date = edt_offense_date.getText().toString();
            String remarks = edt_remarks.getText().toString();
            String staff_intervention = edt_staff_intervention.getText().toString();
            String other_details = edt_other_details.getText().toString();
            String card_holder_name1 = edt_card_holder_name1.getText().toString();
            String card_holder_name2 = edt_card_holder_name2.getText().toString();
            String card_holder_name3 = edt_card_holder_name3.getText().toString();
            String card_holder_name4 = edt_card_holder_name4.getText().toString();
            String card_holder_name5 = edt_card_holder_name5.getText().toString();
            String distribution_status1 = aat_distribution_status1.getText().toString();
            String distribution_status2 = aat_distribution_status2.getText().toString();
            String distribution_status3 = aat_distribution_status3.getText().toString();
            String distribution_status4 = aat_distribution_status4.getText().toString();
            String distribution_status5 = aat_distribution_status5.getText().toString();
            String release_date1 = edt_release_date1.getText().toString();
            String release_date2 = edt_release_date2.getText().toString();
            String release_date3 = edt_release_date3.getText().toString();
            String release_date4 = edt_release_date4.getText().toString();
            String release_date5 = edt_release_date5.getText().toString();
            String release_by1 = edt_release_by1.getText().toString();
            String release_by2 = edt_release_by2.getText().toString();
            String release_by3 = edt_release_by3.getText().toString();
            String release_by4 = edt_release_by4.getText().toString();
            String release_by5 = edt_release_by5.getText().toString();
            String release_place1 = edt_release_place1.getText().toString();
            String release_place2 = edt_release_place2.getText().toString();
            String release_place3 = edt_release_place3.getText().toString();
            String release_place4 = edt_release_place4.getText().toString();
            String release_place5 = edt_release_place5.getText().toString();
            String card_physically_presented1 = aat_card_physically_presented1.getText().toString();
            String card_physically_presented2 = aat_card_physically_presented2.getText().toString();
            String card_physically_presented3 = aat_card_physically_presented3.getText().toString();
            String card_physically_presented4 = aat_card_physically_presented4.getText().toString();
            String card_physically_presented5 = aat_card_physically_presented5.getText().toString();
            String card_pin_is_attached1 = aat_card_pin_is_attached1.getText().toString();
            String card_pin_is_attached2 = aat_card_pin_is_attached2.getText().toString();
            String card_pin_is_attached3 = aat_card_pin_is_attached3.getText().toString();
            String card_pin_is_attached4 = aat_card_pin_is_attached4.getText().toString();
            String card_pin_is_attached5 = aat_card_pin_is_attached5.getText().toString();
            String reason_not_presented1 = aat_reason_not_presented1.getText().toString();
            String reason_not_presented2 = aat_reason_not_presented2.getText().toString();
            String reason_not_presented3 = aat_reason_not_presented3.getText().toString();
            String reason_not_presented4 = aat_reason_not_presented4.getText().toString();
            String reason_not_presented5 = aat_reason_not_presented5.getText().toString();
            String others_reason_not_presented1 = edt_others_reason_not_presented1.getText().toString();
            String others_reason_not_presented2 = edt_others_reason_not_presented2.getText().toString();
            String others_reason_not_presented3 = edt_others_reason_not_presented3.getText().toString();
            String others_reason_not_presented4 = edt_others_reason_not_presented4.getText().toString();
            String others_reason_not_presented5 = edt_others_reason_not_presented5.getText().toString();
            String reason_unclaimed1 = aat_reason_unclaimed1.getText().toString();
            String reason_unclaimed2 = aat_reason_unclaimed2.getText().toString();
            String reason_unclaimed3 = aat_reason_unclaimed3.getText().toString();
            String reason_unclaimed4 = aat_reason_unclaimed4.getText().toString();
            String reason_unclaimed5 = aat_reason_unclaimed5.getText().toString();
            String others_reason_unclaimed1 = edt_others_reason_unclaimed1.getText().toString();
            String others_reason_unclaimed2 = edt_others_reason_unclaimed2.getText().toString();
            String others_reason_unclaimed3 = edt_others_reason_unclaimed3.getText().toString();
            String others_reason_unclaimed4 = edt_others_reason_unclaimed4.getText().toString();
            String others_reason_unclaimed5 = edt_others_reason_unclaimed5.getText().toString();
            String card_replacement_request1 = aat_card_replacement_request1.getText().toString();
            String card_replacement_request2 = aat_card_replacement_request2.getText().toString();
            String card_replacement_request3 = aat_card_replacement_request3.getText().toString();
            String card_replacement_request4 = aat_card_replacement_request4.getText().toString();
            String card_replacement_request5 = aat_card_replacement_request5.getText().toString();
            String card_replacement_request_submitted_details1 = edt_card_replacement_request_submitted_details1.getText().toString();
            String card_replacement_request_submitted_details2 = edt_card_replacement_request_submitted_details2.getText().toString();
            String card_replacement_request_submitted_details3 = edt_card_replacement_request_submitted_details3.getText().toString();
            String card_replacement_request_submitted_details4 = edt_card_replacement_request_submitted_details4.getText().toString();
            String card_replacement_request_submitted_details5 = edt_card_replacement_request_submitted_details5.getText().toString();
            String card_number_inputted1 = edt_card_number_inputted1.getText().toString();
            String card_number_inputted2 = edt_card_number_inputted2.getText().toString();
            String card_number_inputted3 = edt_card_number_inputted3.getText().toString();
            String card_number_inputted4 = edt_card_number_inputted4.getText().toString();
            String card_number_inputted5 = edt_card_number_inputted5.getText().toString();
            String card_number_series1 = edt_card_number_series1.getText().toString();
            String card_number_series2 = edt_card_number_series2.getText().toString();
            String card_number_series3 = edt_card_number_series3.getText().toString();
            String card_number_series4 = edt_card_number_series4.getText().toString();
            String card_number_series5 = edt_card_number_series5.getText().toString();
            String pawning_remarks1 = edt_pawning_remarks1.getText().toString();
            String pawning_remarks2 = edt_pawning_remarks2.getText().toString();
            String pawning_remarks3 = edt_pawning_remarks3.getText().toString();
            String pawning_remarks4 = edt_pawning_remarks4.getText().toString();
            String pawning_remarks5 = edt_pawning_remarks5.getText().toString();


            if (distribution_status.matches("")) {
                til_distribution_status.setError(required_field);
                isValidationError++;
                Log.v(TAG,"tracingerror 1 ");
            } else {
                til_distribution_status.setError(null);

                if (distribution_status.matches("Claimed")) {
                    if (release_date.matches("")) {
                        til_release_date.setError(required_field);
                        isValidationError++;
                        Log.v(TAG,"tracingerror 2 ");
                    } else {
                        til_release_date.setError(null);
                    }

                    if (release_by.matches("")) {
                        til_release_by.setError(required_field);
                        isValidationError++;
                        Log.v(TAG,"tracingerror 3 ");
                    } else {
                        til_release_by.setError(null);
                    }

                    if (release_place.matches("")) {
                        til_release_place.setError(required_field);
                        isValidationError++;
                        Log.v(TAG,"tracingerror 4 ");
                    } else {
                        til_release_place.setError(null);
                    }

                    //new
                    if (card_physically_presented.matches("")) {
                        til_card_physically_presented.setError(required_field);
                        isValidationError++;
                        Log.v(TAG,"tracingerror 8 ");
                    } else {
                        til_card_physically_presented.setError(null);

                        if (card_physically_presented.matches("Yes")) {
                            if (distribution_status.matches("Unclaimed")){
                                til_distribution_status.setError("Must be Claimed if Yes on physical cash card presented");
                                isValidationError++;
                                Log.v(TAG,"tracingerror 9 ");
                            }
                            else{
                                til_distribution_status.setError(null);
                            }
                            if (card_pin_is_attached.matches("")) {
                                til_card_pin_is_attached.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"tracingerror 10 ");
                            } else {
                                til_card_pin_is_attached.setError(null);
                            }
                        } else {
                            if (reason_not_presented.matches("")) {
                                til_reason_not_presented.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"tracingerror 11 ");
                            } else {
                                til_reason_not_presented.setError(null);

                                if (reason_not_presented.matches("Unclaimed")) {
//                            if (reason_unclaimed.matches("")) {
//                                til_reason_unclaimed.setError(required_field);
//                                isValidationError++;
//                            } else {
//                                til_reason_unclaimed.setError(null);
//
//                                if (reason_unclaimed.matches("Others")) {
//                                    if (others_reason_unclaimed.matches("")) {
//                                        til_others_reason_unclaimed.setError(required_field);
//                                        isValidationError++;
//                                    } else {
//                                        til_others_reason_unclaimed.setError(null);
//                                    }
//                                }
//                            }
                                } else

                                if (reason_not_presented.matches("Lost/Stolen") || reason_not_presented.matches("Damaged/Defective")) {
                                    if (card_replacement_request.matches("")) {
                                        til_card_replacement_request.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG,"tracingerror 12 ");
                                    } else {
                                        til_card_replacement_request.setError(null);

                                        if (card_replacement_request.matches("Yes")) {
                                            if (card_replacement_request_submitted_details.matches("")) {
                                                til_card_replacement_request_submitted_details.setError(required_field);
                                                isValidationError++;
                                                Log.v(TAG,"tracingerror 13 ");
                                            } else {
                                                til_card_replacement_request_submitted_details.setError(null);
                                            }
                                        }
                                    }
                                } else if (reason_not_presented.matches("Pawned")) {
                                    if (lender_name.matches("")) {
                                        til_lender_name.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG,"tracingerror 14 ");
                                    } else {
                                        til_lender_name.setError(null);
                                    }
                                } else if (reason_not_presented.matches("Others")) {
                                    if (others_reason_not_presented.matches("")) {
                                        til_others_reason_not_presented.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG,"tracingerror 15 ");
                                    } else {
                                        til_others_reason_not_presented.setError(null);
                                    }
                                }
                            }
                        }
                    }
                    //end
                }
                //new
                else {
                    if (reason_unclaimed.matches("")) {
                        til_reason_unclaimed.setError(required_field);
                        isValidationError++;
                        Log.v(TAG,"tracingerror 5 ");
                    } else {
                        til_reason_unclaimed.setError(null);
                        if (reason_unclaimed.matches("Others")) {
                            if (others_reason_unclaimed.matches("")) {
                                til_others_reason_unclaimed.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"tracingerror 6 ");
                            } else {
                                til_others_reason_unclaimed.setError(null);
                            }
                        }
                    }

                }
                //end

            }



            if (aat_card_physically_presented.getText().toString().matches("Yes")) {

                if (aat_distribution_status.getText().toString().matches("Unclaimed")){
                    til_distribution_status.setError("Must be Claimed if physical cash card presented");
                    isValidationError++;
                    Log.v(TAG,"tracingerror 7 ");
                }
            }
//            else {
//                til_reason_not_presented.setVisibility(View.VISIBLE);
//            }


            if (card_number_inputted.matches("")) {
                til_card_number_inputted.setError(required_field);
                isValidationError++;
                Log.v(TAG,"tracingerror 16 ");
            }
            else if(card_number_inputted.length() != 23){
                til_card_number_inputted.setError(required_cc_length);
                isValidationError++;
                Log.v(TAG,"tracingerror 17");
            }
            else if (!card_number_inputted.matches("[0-9 ]+")){
                til_card_number_inputted.setError(required_cc_invalid_format);
                isValidationError++;
                Log.v(TAG,"tracingerror 18 ");
            }
            else {
                til_card_number_inputted.setError(null);
            }

            if (card_number_series.matches("")) {
                til_card_number_series.setError(required_field);
                isValidationError++;
                Log.v(TAG,"tracingerror 19 ");
            } else {
                til_card_number_series.setError(null);
            }

            if (id_exists.matches("")) {
                til_id_exists.setError(required_field);
                isValidationError++;
                Log.v(TAG,"tracingerror 20 ");
            } else {
                til_id_exists.setError(null);
            }


            if (aat_card_physically_presented5.getText().toString().matches("Yes")) {
                til_card_pin_is_attached5.setVisibility(View.VISIBLE);
                rlOtherCardScanningField5.setVisibility(View.VISIBLE);
                if (aat_distribution_status5.getText().toString().matches("Unclaimed")){
                    til_distribution_status5.setError("Must be Claimed if physical cash card presented");
                    isValidationError++;
                    Log.v(TAG,"tracingerror 21 ");
                }
                else {
                    til_distribution_status5.setError(null);
                }
            } else {
                til_distribution_status5.setError(null);
                til_reason_not_presented5.setVisibility(View.VISIBLE);
                rlOtherCardScanningField5.setVisibility(View.GONE);
                sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_5=NULL WHERE id=1");
                ivOtherScannedImage5.setImageResource(R.drawable.ic_image);
            }

//            Other Card Availablity 1 - 5

            til_distribution_status1.setError(null);
            til_release_date1.setError(null);
            til_release_by1.setError(null);
            til_release_place1.setError(null);
            til_card_physically_presented1.setError(null);
            til_card_pin_is_attached1.setError(null);
            til_reason_not_presented1.setError(null);
            til_reason_unclaimed1.setError(null);
            til_others_reason_unclaimed1.setError(null);
            til_card_replacement_request1.setError(null);
            til_card_replacement_request_submitted_details1.setError(null);
            til_pawning_remarks1.setError(null);
            til_others_reason_not_presented1.setError(null);
            til_card_number_inputted1.setError(null);
            til_card_number_series1.setError(null);

            til_distribution_status2.setError(null);
            til_release_date2.setError(null);
            til_release_by2.setError(null);
            til_release_place2.setError(null);
            til_card_physically_presented2.setError(null);
            til_card_pin_is_attached2.setError(null);
            til_reason_not_presented2.setError(null);
            til_reason_unclaimed2.setError(null);
            til_others_reason_unclaimed2.setError(null);
            til_card_replacement_request2.setError(null);
            til_card_replacement_request_submitted_details2.setError(null);
            til_pawning_remarks2.setError(null);
            til_others_reason_not_presented2.setError(null);
            til_card_number_inputted2.setError(null);
            til_card_number_series2.setError(null);

            til_distribution_status3.setError(null);
            til_release_date3.setError(null);
            til_release_by3.setError(null);
            til_release_place3.setError(null);
            til_card_physically_presented3.setError(null);
            til_card_pin_is_attached3.setError(null);
            til_reason_not_presented3.setError(null);
            til_reason_unclaimed3.setError(null);
            til_others_reason_unclaimed3.setError(null);
            til_card_replacement_request3.setError(null);
            til_card_replacement_request_submitted_details3.setError(null);
            til_pawning_remarks3.setError(null);
            til_others_reason_not_presented3.setError(null);
            til_card_number_inputted3.setError(null);
            til_card_number_series3.setError(null);

            til_distribution_status4.setError(null);
            til_release_date4.setError(null);
            til_release_by4.setError(null);
            til_release_place4.setError(null);
            til_card_physically_presented4.setError(null);
            til_card_pin_is_attached4.setError(null);
            til_reason_not_presented4.setError(null);
            til_reason_unclaimed4.setError(null);
            til_others_reason_unclaimed4.setError(null);
            til_card_replacement_request4.setError(null);
            til_card_replacement_request_submitted_details4.setError(null);
            til_pawning_remarks4.setError(null);
            til_others_reason_not_presented4.setError(null);
            til_card_number_inputted4.setError(null);
            til_card_number_series4.setError(null);

            til_distribution_status5.setError(null);
            til_release_date5.setError(null);
            til_release_by5.setError(null);
            til_release_place5.setError(null);
            til_card_physically_presented5.setError(null);
            til_card_pin_is_attached5.setError(null);
            til_reason_not_presented5.setError(null);
            til_reason_unclaimed5.setError(null);
            til_others_reason_unclaimed5.setError(null);
            til_card_replacement_request5.setError(null);
            til_card_replacement_request_submitted_details5.setError(null);
            til_pawning_remarks5.setError(null);
            til_others_reason_not_presented5.setError(null);
            til_card_number_inputted5.setError(null);
            til_card_number_series5.setError(null);


            if (otherCardAvailability1.getVisibility() == View.VISIBLE) {
                if (distribution_status1.matches("")) {
                    til_distribution_status1.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 1 ");
                } else {

                    if (distribution_status1.matches("Claimed")) {
                        if (release_date1.matches("")) {
                            til_release_date1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 2 ");
                        }
                        if (release_by1.matches("")) {
                            til_release_by1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 3 ");
                        }
                        if (release_place1.matches("")) {
                            til_release_place1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 4 ");
                        }
                    }
                }

            if (distribution_status1.matches("Claimed")){
                if (card_physically_presented1.matches("")) {
                    til_card_physically_presented1.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 5 ");
                } else {
                    if (card_physically_presented1.matches("Yes")) {
                        if (distribution_status1.matches("Unclaimed")) {
                            til_distribution_status1.setError("Must be Claimed if Yes on physical cash card presented");
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 6 ");
                        } else {
                            til_distribution_status1.setError(null);
                        }
                        if (card_pin_is_attached1.matches("")) {
                            til_card_pin_is_attached1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 7 ");
                        } else {
                            til_card_pin_is_attached1.setError(null);
                        }
                        //start
                        if (card_number_inputted1.matches("")) {
                            til_card_number_inputted1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG,"newtracedaerror 15 ");
                        }
                        else if(card_number_inputted1.length() != 23){
                            til_card_number_inputted1.setError(required_cc_length);
                            isValidationError++;
                            Log.v(TAG,"newtracedaerror 16");
                        }
                        else if (!card_number_inputted1.matches("[0-9 ]+")){
                            til_card_number_inputted1.setError(required_cc_invalid_format);
                            isValidationError++;
                            Log.v(TAG,"newtracedaerror 17 ");
                        }

                        else {
                            til_card_number_inputted1.setError(null);

                        }
                        if (card_number_series1.matches("")) {
                            til_card_number_series1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG,"newtracedaerror 18 ");
                        }
                        //end

                    } else {
                        if (reason_not_presented1.matches("")) {
                            til_reason_not_presented1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 8 ");
                        } else {
                             if (reason_not_presented1.matches("Lost/Stolen") || reason_not_presented1.matches("Damaged/Defective")) {
                                if (card_replacement_request1.matches("")) {
                                    til_card_replacement_request1.setError(required_field);
                                    isValidationError++;
                                    Log.v(TAG, "newtracedaerror 11 ");
                                } else {
                                    if (card_replacement_request1.matches("Yes")) {
                                        if (card_replacement_request_submitted_details1.matches("")) {
                                            til_card_replacement_request_submitted_details1.setError(required_field);
                                            isValidationError++;
                                            Log.v(TAG, "newtracedaerror 12 ");
                                        }
                                    }
                                }
                            } else if (reason_not_presented1.matches("Pawned")) {
                                if (pawning_remarks1.matches("")) {
                                    til_pawning_remarks1.setError(required_field);
                                    isValidationError++;
                                    Log.v(TAG, "pawwneed daww ");
                                }
                            } else if (reason_not_presented1.matches("Others")) {
                                if (others_reason_not_presented1.matches("")) {
                                    til_others_reason_not_presented1.setError(required_field);
                                    isValidationError++;
                                    Log.v(TAG, "newtracedaerror 14 ");
                                }
                            }
                        }
                    }
                }
            }
            else if (distribution_status1.matches("Unclaimed")) {
                if (reason_unclaimed1.matches("")) {
                    til_reason_unclaimed1.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 9 ");
                } else {
                    if (reason_unclaimed1.matches("Others")) {
                        if (others_reason_unclaimed1.matches("")) {
                            til_others_reason_unclaimed1.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 10 ");
                        }
                    }
                }
              }
            }

            if (otherCardAvailability2.getVisibility() == View.VISIBLE) {
                if (distribution_status2.matches("")) {
                    til_distribution_status2.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 1 ");
                } else {
                    if (distribution_status2.matches("Claimed")) {
                        if (release_date2.matches("")) {
                            til_release_date2.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 2 ");
                        }
                        if (release_by2.matches("")) {
                            til_release_by2.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 3 ");
                        }
                        if (release_place2.matches("")) {
                            til_release_place2.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 4 ");
                        }
                    }
                }

                if (distribution_status2.matches("Claimed")){
                    if (card_physically_presented2.matches("")) {
                        til_card_physically_presented2.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 5 ");
                    } else {
                        if (card_physically_presented2.matches("Yes")) {
                            if (distribution_status2.matches("Unclaimed")) {
                                til_distribution_status2.setError("Must be Claimed if Yes on physical cash card presented");
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 6 ");
                            } else {
                                til_distribution_status2.setError(null);
                            }
                            if (card_pin_is_attached2.matches("")) {
                                til_card_pin_is_attached2.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 7 ");
                            } else {
                                til_card_pin_is_attached2.setError(null);
                            }
                            //start
                            if (card_number_inputted2.matches("")) {
                                til_card_number_inputted2.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 15 ");
                            }
                            else if(card_number_inputted2.length() != 23){
                                til_card_number_inputted2.setError(required_cc_length);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 16");
                            }
                            else if (!card_number_inputted2.matches("[0-9 ]+")){
                                til_card_number_inputted2.setError(required_cc_invalid_format);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 17 ");
                            }

                            else {
                                til_card_number_inputted2.setError(null);

                            }
                            if (card_number_series2.matches("")) {
                                til_card_number_series2.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 18 ");
                            }
                            //end

                        } else {
                            if (reason_not_presented2.matches("")) {
                                til_reason_not_presented2.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 8 ");
                            } else {
                                if (reason_not_presented2.matches("Lost/Stolen") || reason_not_presented2.matches("Damaged/Defective")) {
                                    if (card_replacement_request2.matches("")) {
                                        til_card_replacement_request2.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 11 ");
                                    } else {
                                        if (card_replacement_request2.matches("Yes")) {
                                            if (card_replacement_request_submitted_details2.matches("")) {
                                                til_card_replacement_request_submitted_details2.setError(required_field);
                                                isValidationError++;
                                                Log.v(TAG, "newtracedaerror 12 ");
                                            }
                                        }
                                    }
                                } else if (reason_not_presented2.matches("Pawned")) {
                                    Log.v(TAG, "resson 1111");
                                    if (pawning_remarks2.matches("")) {
                                        til_pawning_remarks2.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "resson 2222" + pawning_remarks2);
                                    }
                                    else{
                                        til_pawning_remarks2.setError(null);
                                    }
                                } else if (reason_not_presented2.matches("Others")) {
                                    if (others_reason_not_presented2.matches("")) {
                                        til_others_reason_not_presented2.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 14 ");
                                    }
                                }
                            }
                        }
                    }
                }
                else if (distribution_status2.matches("Unclaimed")) {
                    if (reason_unclaimed2.matches("")) {
                        til_reason_unclaimed2.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 9 ");
                    } else {
                        if (reason_unclaimed2.matches("Others")) {
                            if (others_reason_unclaimed2.matches("")) {
                                til_others_reason_unclaimed2.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 10 ");
                            }
                        }
                    }
                }



//                if (distribution_status2.matches("")) {
//                    til_distribution_status2.setError(required_field);
//                    isValidationError++;
//
//                } else {
//                    if (distribution_status2.matches("Claimed")) {
//                        if (release_date2.matches("")) {
//                            til_release_date2.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_by2.matches("")) {
//                            til_release_by2.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_place2.matches("")) {
//                            til_release_place2.setError(required_field);
//                            isValidationError++;
//                        }
//                    }
//
//                }
//
//                if (card_physically_presented2.matches("")) {
//                    til_card_physically_presented2.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (card_physically_presented2.matches("Yes")) {
//                        if (distribution_status2.matches("Unclaimed")){
//                            til_distribution_status2.setError("Must be Claimed if Yes on physical cash card presented");
//                            isValidationError++;
//                        }
//                        else{
//                            til_distribution_status2.setError(null);
//                        }
//
//                        if (card_pin_is_attached2.matches("")) {
//                            til_card_pin_is_attached2.setError(required_field);
//                            isValidationError++;
//                        }
//                    } else {
//                        if (reason_not_presented2.matches("")) {
//                            til_reason_not_presented2.setError(required_field);
//                            isValidationError++;
//                        } else {
//                            if (reason_not_presented2.matches("Unclaimed")) {
//                                if (reason_unclaimed2.matches("")) {
//                                    til_reason_unclaimed2.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (reason_unclaimed2.matches("Others")) {
//                                        if (others_reason_unclaimed2.matches("")) {
//                                            til_others_reason_unclaimed2.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented2.matches("Lost/Stolen") || reason_not_presented2.matches("Damaged/Defective")) {
//                                if (card_replacement_request2.matches("")) {
//                                    til_card_replacement_request2.setError(required_field);
//                                    isValidationError++;
//                                } else {
//
//                                    if (card_replacement_request2.matches("Yes")) {
//                                        if (card_replacement_request_submitted_details2.matches("")) {
//                                            til_card_replacement_request_submitted_details2.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented2.matches("Pawned")) {
//                                if (pawning_remarks2.matches("")) {
//                                    til_pawning_remarks2.setError(required_field);
//                                    isValidationError++;
//                                }
//                            } else if (reason_not_presented2.matches("Others")) {
//                                if (others_reason_not_presented2.matches("")) {
//                                    til_others_reason_not_presented2.setError(required_field);
//                                    isValidationError++;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (card_number_inputted2.matches("")) {
//                    til_card_number_inputted2.setError(required_field);
//                    isValidationError++;
//                }
//                else if(card_number_inputted2.length() != 23){
//                    til_card_number_inputted2.setError(required_cc_length);
//                    isValidationError++;
//                }
//                else if (!card_number_inputted2.matches("[0-9 ]+")){
//                    til_card_number_inputted2.setError(required_cc_invalid_format);
//                    isValidationError++;
//                }
//                else {
//                    til_card_number_inputted2.setError(null);
//                }
//
//                if (card_number_series2.matches("")) {
//                    til_card_number_series2.setError(required_field);
//                    isValidationError++;
//                }
            }


            if (otherCardAvailability3.getVisibility() == View.VISIBLE) {
                if (distribution_status3.matches("")) {
                    til_distribution_status3.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 1 ");
                } else {

                    if (distribution_status3.matches("Claimed")) {
                        if (release_date3.matches("")) {
                            til_release_date3.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 2 ");
                        }
                        if (release_by3.matches("")) {
                            til_release_by3.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 3 ");
                        }
                        if (release_place3.matches("")) {
                            til_release_place3.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 4 ");
                        }
                    }
                }

                if (distribution_status3.matches("Claimed")){
                    if (card_physically_presented3.matches("")) {
                        til_card_physically_presented3.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 5 ");
                    } else {
                        if (card_physically_presented3.matches("Yes")) {
                            if (distribution_status3.matches("Unclaimed")) {
                                til_distribution_status3.setError("Must be Claimed if Yes on physical cash card presented");
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 6 ");
                            } else {
                                til_distribution_status3.setError(null);
                            }
                            if (card_pin_is_attached3.matches("")) {
                                til_card_pin_is_attached3.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 7 ");
                            } else {
                                til_card_pin_is_attached3.setError(null);
                            }
                            //start
                            if (card_number_inputted3.matches("")) {
                                til_card_number_inputted3.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 15 ");
                            }
                            else if(card_number_inputted3.length() != 23){
                                til_card_number_inputted3.setError(required_cc_length);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 16");
                            }
                            else if (!card_number_inputted3.matches("[0-9 ]+")){
                                til_card_number_inputted3.setError(required_cc_invalid_format);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 17 ");
                            }

                            else {
                                til_card_number_inputted3.setError(null);

                            }
                            if (card_number_series3.matches("")) {
                                til_card_number_series3.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 18 ");
                            }
                            //end

                        } else {
                            if (reason_not_presented3.matches("")) {
                                til_reason_not_presented3.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 8 ");
                            } else {
                                if (reason_not_presented3.matches("Lost/Stolen") || reason_not_presented3.matches("Damaged/Defective")) {
                                    if (card_replacement_request3.matches("")) {
                                        til_card_replacement_request3.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 11 ");
                                    } else {
                                        if (card_replacement_request3.matches("Yes")) {
                                            if (card_replacement_request_submitted_details3.matches("")) {
                                                til_card_replacement_request_submitted_details3.setError(required_field);
                                                isValidationError++;
                                                Log.v(TAG, "newtracedaerror 12 ");
                                            }
                                        }
                                    }
                                } else if (reason_not_presented3.matches("Pawned")) {
                                    if (pawning_remarks3.matches("")) {
                                        til_pawning_remarks3.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 13 ");
                                    }
                                } else if (reason_not_presented3.matches("Others")) {
                                    if (others_reason_not_presented3.matches("")) {
                                        til_others_reason_not_presented3.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 14 ");
                                    }
                                }
                            }
                        }
                    }
                }
                else if (distribution_status3.matches("Unclaimed")) {
                    if (reason_unclaimed3.matches("")) {
                        til_reason_unclaimed3.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 9 ");
                    } else {
                        if (reason_unclaimed3.matches("Others")) {
                            if (others_reason_unclaimed3.matches("")) {
                                til_others_reason_unclaimed3.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 10 ");
                            }
                        }
                    }
                }
            }

            if (otherCardAvailability4.getVisibility() == View.VISIBLE) {
                if (distribution_status4.matches("")) {
                    til_distribution_status4.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 4 ");
                } else {

                    if (distribution_status4.matches("Claimed")) {
                        if (release_date4.matches("")) {
                            til_release_date4.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 2 ");
                        }
                        if (release_by4.matches("")) {
                            til_release_by4.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 3 ");
                        }
                        if (release_place4.matches("")) {
                            til_release_place4.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 4 ");
                        }
                    }
                }

                if (distribution_status4.matches("Claimed")){
                    if (card_physically_presented4.matches("")) {
                        til_card_physically_presented4.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 5 ");
                    } else {
                        if (card_physically_presented4.matches("Yes")) {
                            if (distribution_status4.matches("Unclaimed")) {
                                til_distribution_status4.setError("Must be Claimed if Yes on physical cash card presented");
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 6 ");
                            } else {
                                til_distribution_status4.setError(null);
                            }
                            if (card_pin_is_attached4.matches("")) {
                                til_card_pin_is_attached4.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 7 ");
                            } else {
                                til_card_pin_is_attached4.setError(null);
                            }
                            //start
                            if (card_number_inputted4.matches("")) {
                                til_card_number_inputted4.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 45 ");
                            }
                            else if(card_number_inputted4.length() != 23){
                                til_card_number_inputted4.setError(required_cc_length);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 46");
                            }
                            else if (!card_number_inputted4.matches("[0-9 ]+")){
                                til_card_number_inputted4.setError(required_cc_invalid_format);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 47 ");
                            }

                            else {
                                til_card_number_inputted4.setError(null);

                            }
                            if (card_number_series4.matches("")) {
                                til_card_number_series4.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 48 ");
                            }
                            //end

                        } else {
                            if (reason_not_presented4.matches("")) {
                                til_reason_not_presented4.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 8 ");
                            } else {
                                if (reason_not_presented4.matches("Lost/Stolen") || reason_not_presented4.matches("Damaged/Defective")) {
                                    if (card_replacement_request4.matches("")) {
                                        til_card_replacement_request4.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 44 ");
                                    } else {
                                        if (card_replacement_request4.matches("Yes")) {
                                            if (card_replacement_request_submitted_details4.matches("")) {
                                                til_card_replacement_request_submitted_details4.setError(required_field);
                                                isValidationError++;
                                                Log.v(TAG, "newtracedaerror 42 ");
                                            }
                                        }
                                    }
                                } else if (reason_not_presented4.matches("Pawned")) {
                                    if (pawning_remarks4.matches("")) {
                                        til_pawning_remarks4.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 43 ");
                                    }
                                } else if (reason_not_presented4.matches("Others")) {
                                    if (others_reason_not_presented4.matches("")) {
                                        til_others_reason_not_presented4.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 44 ");
                                    }
                                }
                            }
                        }
                    }
                }
                else if (distribution_status4.matches("Unclaimed")) {
                    if (reason_unclaimed4.matches("")) {
                        til_reason_unclaimed4.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 9 ");
                    } else {
                        if (reason_unclaimed4.matches("Others")) {
                            if (others_reason_unclaimed4.matches("")) {
                                til_others_reason_unclaimed4.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 40 ");
                            }
                        }
                    }
                }
            }

            if (otherCardAvailability5.getVisibility() == View.VISIBLE) {
                if (distribution_status5.matches("")) {
                    til_distribution_status5.setError(required_field);
                    isValidationError++;
                    Log.v(TAG, "newtracedaerror 5 ");
                } else {

                    if (distribution_status5.matches("Claimed")) {
                        if (release_date5.matches("")) {
                            til_release_date5.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 2 ");
                        }
                        if (release_by5.matches("")) {
                            til_release_by5.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 3 ");
                        }
                        if (release_place5.matches("")) {
                            til_release_place5.setError(required_field);
                            isValidationError++;
                            Log.v(TAG, "newtracedaerror 5 ");
                        }
                    }
                }

                if (distribution_status5.matches("Claimed")){
                    if (card_physically_presented5.matches("")) {
                        til_card_physically_presented5.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 5 ");
                    } else {
                        if (card_physically_presented5.matches("Yes")) {
                            if (distribution_status5.matches("Unclaimed")) {
                                til_distribution_status5.setError("Must be Claimed if Yes on physical cash card presented");
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 6 ");
                            } else {
                                til_distribution_status5.setError(null);
                            }
                            if (card_pin_is_attached5.matches("")) {
                                til_card_pin_is_attached5.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 7 ");
                            } else {
                                til_card_pin_is_attached5.setError(null);
                            }
                            //start
                            if (card_number_inputted5.matches("")) {
                                til_card_number_inputted5.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 55 ");
                            }
                            else if(card_number_inputted5.length() != 23){
                                til_card_number_inputted5.setError(required_cc_length);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 56");
                            }
                            else if (!card_number_inputted5.matches("[0-9 ]+")){
                                til_card_number_inputted5.setError(required_cc_invalid_format);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 57 ");
                            }

                            else {
                                til_card_number_inputted5.setError(null);

                            }
                            if (card_number_series5.matches("")) {
                                til_card_number_series5.setError(required_field);
                                isValidationError++;
                                Log.v(TAG,"newtracedaerror 58 ");
                            }
                            //end

                        } else {
                            if (reason_not_presented5.matches("")) {
                                til_reason_not_presented5.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 8 ");
                            } else {
                                if (reason_not_presented5.matches("Lost/Stolen") || reason_not_presented5.matches("Damaged/Defective")) {
                                    if (card_replacement_request5.matches("")) {
                                        til_card_replacement_request5.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 55 ");
                                    } else {
                                        if (card_replacement_request5.matches("Yes")) {
                                            if (card_replacement_request_submitted_details5.matches("")) {
                                                til_card_replacement_request_submitted_details5.setError(required_field);
                                                isValidationError++;
                                                Log.v(TAG, "newtracedaerror 52 ");
                                            }
                                        }
                                    }
                                } else if (reason_not_presented5.matches("Pawned")) {
                                    if (pawning_remarks5.matches("")) {
                                        til_pawning_remarks5.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 53 ");
                                    }
                                } else if (reason_not_presented5.matches("Others")) {
                                    if (others_reason_not_presented5.matches("")) {
                                        til_others_reason_not_presented5.setError(required_field);
                                        isValidationError++;
                                        Log.v(TAG, "newtracedaerror 55 ");
                                    }
                                }
                            }
                        }
                    }
                }
                else if (distribution_status5.matches("Unclaimed")) {
                    if (reason_unclaimed5.matches("")) {
                        til_reason_unclaimed5.setError(required_field);
                        isValidationError++;
                        Log.v(TAG, "newtracedaerror 9 ");
                    } else {
                        if (reason_unclaimed5.matches("Others")) {
                            if (others_reason_unclaimed5.matches("")) {
                                til_others_reason_unclaimed5.setError(required_field);
                                isValidationError++;
                                Log.v(TAG, "newtracedaerror 50 ");
                            }
                        }
                    }
                }
            }

//            if (otherCardAvailability3.getVisibility() == View.VISIBLE) {
//                if (distribution_status3.matches("")) {
//                    til_distribution_status3.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (distribution_status3.matches("Claimed")) {
//                        if (release_date3.matches("")) {
//                            til_release_date3.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_by3.matches("")) {
//                            til_release_by3.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_place3.matches("")) {
//                            til_release_place3.setError(required_field);
//                            isValidationError++;
//                        }
//                    }
//                }
//                if (card_physically_presented3.matches("")) {
//                    til_card_physically_presented3.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (card_physically_presented3.matches("Yes")) {
//                        if (distribution_status3.matches("Unclaimed")){
//                            til_distribution_status3.setError("Must be Claimed if Yes on physical cash card presented");
//                            isValidationError++;
//                        }
//                        else{
//                            til_distribution_status3.setError(null);
//                        }
//                        if (card_pin_is_attached3.matches("")) {
//                            til_card_pin_is_attached3.setError(required_field);
//                            isValidationError++;
//                        }
//
//                    } else {
//                        if (reason_not_presented3.matches("")) {
//                            til_reason_not_presented3.setError(required_field);
//                            isValidationError++;
//                        } else {
//                            if (reason_not_presented3.matches("Unclaimed")) {
//                                if (reason_unclaimed3.matches("")) {
//                                    til_reason_unclaimed3.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (reason_unclaimed3.matches("Others")) {
//                                        if (others_reason_unclaimed3.matches("")) {
//                                            til_others_reason_unclaimed3.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented3.matches("Lost/Stolen") || reason_not_presented3.matches("Damaged/Defective")) {
//                                if (card_replacement_request3.matches("")) {
//                                    til_card_replacement_request3.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (card_replacement_request3.matches("Yes")) {
//                                        if (card_replacement_request_submitted_details3.matches("")) {
//                                            til_card_replacement_request_submitted_details3.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented3.matches("Pawned")) {
//                                if (pawning_remarks3.matches("")) {
//                                    til_pawning_remarks3.setError(required_field);
//                                    isValidationError++;
//                                }
//                            } else if (reason_not_presented3.matches("Others")) {
//                                if (others_reason_not_presented3.matches("")) {
//                                    til_others_reason_not_presented3.setError(required_field);
//                                    isValidationError++;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (card_number_inputted3.matches("")) {
//                    til_card_number_inputted3.setError(required_field);
//                    isValidationError++;
//                }
//                else if(card_number_inputted3.length() != 23){
//                    til_card_number_inputted3.setError(required_cc_length);
//                    isValidationError++;
//                }
//                else if (!card_number_inputted3.matches("[0-9 ]+")){
//                    til_card_number_inputted3.setError(required_cc_invalid_format);
//                    isValidationError++;
//                }
//
//                else {
//                    til_card_number_inputted3.setError(null);
//
//                }
//
//                if (card_number_series3.matches("")) {
//                    til_card_number_series3.setError(required_field);
//                    isValidationError++;
//                }
//            }

//            if (otherCardAvailability4.getVisibility() == View.VISIBLE) {
//                if (distribution_status4.matches("")) {
//                    til_distribution_status4.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (distribution_status4.matches("Claimed")) {
//                        if (release_date4.matches("")) {
//                            til_release_date4.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_by4.matches("")) {
//                            til_release_by4.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_place4.matches("")) {
//                            til_release_place4.setError(required_field);
//                            isValidationError++;
//                        }
//                    }
//                }
//                if (card_physically_presented4.matches("")) {
//                    til_card_physically_presented4.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (card_physically_presented4.matches("Yes")) {
//                        if (distribution_status4.matches("Unclaimed")){
//                            til_distribution_status4.setError("Must be Claimed if Yes on physical cash card presented");
//                            isValidationError++;
//                        }
//                        else{
//                            til_distribution_status4.setError(null);
//                        }
//                        if (card_pin_is_attached4.matches("")) {
//                            til_card_pin_is_attached4.setError(required_field);
//                            isValidationError++;
//                        }
//                    } else {
//                        if (reason_not_presented4.matches("")) {
//                            til_reason_not_presented4.setError(required_field);
//                            isValidationError++;
//                        } else {
//                            if (reason_not_presented4.matches("Unclaimed")) {
//                                if (reason_unclaimed4.matches("")) {
//                                    til_reason_unclaimed4.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (reason_unclaimed4.matches("Others")) {
//                                        if (others_reason_unclaimed4.matches("")) {
//                                            til_others_reason_unclaimed4.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented4.matches("Lost/Stolen") || reason_not_presented4.matches("Damaged/Defective")) {
//                                if (card_replacement_request4.matches("")) {
//                                    til_card_replacement_request4.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (card_replacement_request4.matches("Yes")) {
//                                        if (card_replacement_request_submitted_details4.matches("")) {
//                                            til_card_replacement_request_submitted_details4.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented4.matches("Pawned")) {
//                                if (pawning_remarks4.matches("")) {
//                                    til_pawning_remarks4.setError(required_field);
//                                    isValidationError++;
//                                }
//                            } else if (reason_not_presented4.matches("Others")) {
//                                if (others_reason_not_presented4.matches("")) {
//                                    til_others_reason_not_presented4.setError(required_field);
//                                    isValidationError++;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (card_number_inputted4.matches("")) {
//                    til_card_number_inputted4.setError(required_field);
//                    isValidationError++;
//                }
//                else if(card_number_inputted4.length() != 23){
//                    til_card_number_inputted4.setError(required_cc_length);
//                    isValidationError++;
//                }
//                else if (!card_number_inputted4.matches("[0-9 ]+")){
//                    til_card_number_inputted4.setError(required_cc_invalid_format);
//                    isValidationError++;
//                }
//
//                else {
//                    til_card_number_inputted4.setError(null);
//                }
//
//
//                if (card_number_series4.matches("")) {
//                    til_card_number_series4.setError(required_field);
//                    isValidationError++;
//                }
//            }

//            if (otherCardAvailability5.getVisibility() == View.VISIBLE) {
//                if (distribution_status5.matches("")) {
//                    til_distribution_status5.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (distribution_status5.matches("Claimed")) {
//                        if (release_date5.matches("")) {
//                            til_release_date5.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_by5.matches("")) {
//                            til_release_by5.setError(required_field);
//                            isValidationError++;
//                        }
//                        if (release_place5.matches("")) {
//                            til_release_place5.setError(required_field);
//                            isValidationError++;
//                        }
//                    }
//                }
//                if (card_physically_presented5.matches("")) {
//                    til_card_physically_presented5.setError(required_field);
//                    isValidationError++;
//                } else {
//                    if (card_physically_presented5.matches("Yes")) {
//                        if (distribution_status5.matches("Unclaimed")){
//                            til_distribution_status5.setError("Must be Claimed if Yes on physical cash card presented");
//                            isValidationError++;
//                        }
//                        else{
//                            til_distribution_status5.setError(null);
//                        }
//                        if (card_pin_is_attached5.matches("")) {
//                            til_card_pin_is_attached5.setError(required_field);
//                            isValidationError++;
//                        }
//                    } else {
//                        if (reason_not_presented5.matches("")) {
//                            til_reason_not_presented5.setError(required_field);
//                            isValidationError++;
//                        } else {
//                            if (reason_not_presented5.matches("Unclaimed")) {
//                                if (reason_unclaimed5.matches("")) {
//                                    til_reason_unclaimed5.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (reason_unclaimed5.matches("Others")) {
//                                        if (others_reason_unclaimed5.matches("")) {
//                                            til_others_reason_unclaimed5.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented5.matches("Lost/Stolen") || reason_not_presented5.matches("Damaged/Defective")) {
//                                if (card_replacement_request5.matches("")) {
//                                    til_card_replacement_request5.setError(required_field);
//                                    isValidationError++;
//                                } else {
//                                    if (card_replacement_request5.matches("Yes")) {
//                                        if (card_replacement_request_submitted_details5.matches("")) {
//                                            til_card_replacement_request_submitted_details5.setError(required_field);
//                                            isValidationError++;
//                                        }
//                                    }
//                                }
//                            } else if (reason_not_presented5.matches("Pawned")) {
//                                if (pawning_remarks5.matches("")) {
//                                    til_pawning_remarks5.setError(required_field);
//                                    isValidationError++;
//                                }
//                            } else if (reason_not_presented5.matches("Others")) {
//                                if (others_reason_not_presented5.matches("")) {
//                                    til_others_reason_not_presented5.setError(required_field);
//                                    isValidationError++;
//                                }
//                            }
//                        }
//                    }
//                }
//                if (card_number_inputted5.matches("")) {
//                    til_card_number_inputted5.setError(required_field);
//                    isValidationError++;
//                }
//                else if(card_number_inputted5.length() != 23){
//                    til_card_number_inputted5.setError(required_cc_length);
//                    isValidationError++;
//                }
//                else if (!card_number_inputted5.matches("[0-9 ]+")){
//                    til_card_number_inputted5.setError(required_cc_invalid_format);
//                    isValidationError++;
//                }
//                else {
//                    til_card_number_inputted5.setError(null);
//                }
//
//                if (card_number_series5.matches("")) {
//                    til_card_number_series5.setError(required_field);
//                    isValidationError++;
//                }
//            }

            try {
                Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT scanned_e_image,additional_id_image,grantee_e_image,other_card_e_image_1, other_card_e_image_2, other_card_e_image_3, other_card_e_image_4, other_card_e_image_5 FROM tmp_blob WHERE id=1");
                if(cursor.getCount()!=0){
                    while (cursor.moveToNext()) {
                        byte[] scanned_image = cursor.getBlob(0);
                        byte[] additional_image = cursor.getBlob(1);
                        byte[] grantee_image = cursor.getBlob(2);
                        byte[] other_image1 = cursor.getBlob(3);
                        byte[] other_image2 = cursor.getBlob(4);
                        byte[] other_image3 = cursor.getBlob(5);
                        byte[] other_image4 = cursor.getBlob(6);
                        byte[] other_image5 = cursor.getBlob(7);

                        if (scanned_image != null) {
                            Bitmap scanned = BitmapFactory.decodeByteArray(scanned_image, 0, scanned_image.length);
                            ScannedImage.setImageBitmap(scanned);
                            til_current_scan_btn.setError(null);
                        } else {
                            isValidationError++;
                            til_current_scan_btn.setError(required_btn);
                        }

                        if (otherCardAvailability1.getVisibility() == View.VISIBLE) {
                            if (aat_card_physically_presented1.getText().toString().matches("Yes")) {
                                tilOtherScanned1 = findViewById(R.id.tilOtherScanned1);
                                if (other_image1 != null) {
                                    Bitmap b_o_image1 = BitmapFactory.decodeByteArray(other_image1, 0, other_image1.length);
                                    ivOtherScannedImage1.setImageBitmap(b_o_image1);
                                    tilOtherScanned1.setError(null);
                                } else {
                                    tilOtherScanned1.setError(required_btn);
                                    isValidationError++;
                                }
                            }
                        }


                        if (otherCardAvailability2.getVisibility() == View.VISIBLE) {
                            if (aat_card_physically_presented2.getText().toString().matches("Yes")) {
                                tilOtherScanned2 = findViewById(R.id.tilOtherScanned2);
                                if (other_image2 != null) {
                                    Bitmap b_o_image2 = BitmapFactory.decodeByteArray(other_image2, 0, other_image2.length);
                                    ivOtherScannedImage2.setImageBitmap(b_o_image2);
                                    tilOtherScanned2.setError(null);
                                } else {
                                    isValidationError++;
                                    tilOtherScanned2.setError(required_btn);
                                }
                            }
                        }

                        if (otherCardAvailability3.getVisibility() == View.VISIBLE) {
                            if (aat_card_physically_presented3.getText().toString().matches("Yes")) {
                                tilOtherScanned3 = findViewById(R.id.tilOtherScanned3);
                                if (other_image3 != null) {
                                    Bitmap b_o_image3 = BitmapFactory.decodeByteArray(other_image3, 0, other_image3.length);
                                    ivOtherScannedImage3.setImageBitmap(b_o_image3);
                                    tilOtherScanned3.setError(null);
                                } else {
                                    isValidationError++;
                                    tilOtherScanned3.setError(required_btn);
                                }
                            }
                        }

                        if (otherCardAvailability4.getVisibility() == View.VISIBLE) {
                            if (aat_card_physically_presented4.getText().toString().matches("Yes")) {
                                tilOtherScanned4 = findViewById(R.id.tilOtherScanned4);
                                if (other_image4 != null) {
                                    Bitmap b_o_image4 = BitmapFactory.decodeByteArray(other_image4, 0, other_image4.length);
                                    ivOtherScannedImage4.setImageBitmap(b_o_image4);
                                    tilOtherScanned4.setError(null);
                                } else {
                                    isValidationError++;
                                    tilOtherScanned4.setError(required_btn);
                                }
                            }
                        }

                        if (otherCardAvailability5.getVisibility() == View.VISIBLE) {
                            if (aat_card_physically_presented5.getText().toString().matches("Yes")) {
                                tilOtherScanned5 = findViewById(R.id.tilOtherScanned5);
                                if (other_image5 != null) {
                                    Bitmap b_o_image5 = BitmapFactory.decodeByteArray(other_image5, 0, other_image5.length);
                                    ivOtherScannedImage5.setImageBitmap(b_o_image5);
                                    tilOtherScanned5.setError(null);
                                } else {
                                    isValidationError++;
                                    tilOtherScanned5.setError(required_btn);
                                }
                            }
                        }

                        if (id_exists.matches("Yes")){
                            if (additional_image != null) {
                                Bitmap additional = BitmapFactory.decodeByteArray(additional_image, 0, additional_image.length);
                                imgAdditionalId.setImageBitmap(additional);
                                til_additionalID.setError(null);
                            } else {
                                til_additionalID.setError(required_btn);
                                isValidationError++;
                            }
                        }
                        else {
                            til_additionalID.setError(null);
                        }

                        if (grantee_image != null) {
                            Bitmap grantee = BitmapFactory.decodeByteArray(grantee_image, 0, grantee_image.length);
                            mGrantee.setImageBitmap(grantee);
                            tilGrantee.setError(null);
                        }
                        else{
                            tilGrantee.setError(required_btn);
                            isValidationError++;
                        }


                    }
                }
                else{
                    til_current_scan_btn.setError(required_btn);
                    til_additionalID.setError(required_btn);
                    tilGrantee.setError(required_btn);
                    isValidationError++;
                }
            }
            catch (Exception e){
                Log.v(TAG,"Errors " + e);
            }

            store_preferences(2);
        } else if (current == 3) {
            pressNext =false;

            xml_initialization(3);

            String nma_non_emv = edt_non_emv.getText().toString();
            String nma_card_name = edt_card_name.getText().toString();

            String nma_amount = edt_nma_amount.getText().toString();
            String nma_reason = aat_nma_reason.getText().toString();
            String nma_others_reason = edt_nma_others_reason.getText().toString();
            String nma_date_claimed = edt_nma_date_claimed.getText().toString();
            String nma_remarks = edt_nma_remarks.getText().toString();

            til_nma_reason.setError(null);
            til_nma_others_reason.setError(null);

            if (!TextUtils.isEmpty(nma_amount)) {
                if (Float.parseFloat(nma_amount) >= 100) {
                    if (nma_reason.matches("")) {
                        til_nma_reason.setError(required_field);;
                        isValidationError++;
                    } else {
                        if (nma_reason.matches("Others")) {
                            if (nma_others_reason.matches("")) {
                                til_nma_others_reason.setError(required_field);
                                isValidationError++;
                            }
                        }
                    }
                }
            }

            store_preferences(3);

        } else if (current == 4) {
            pressNext =false;
            xml_initialization(4);
            store_preferences(4);
        } else {
            Log.v(ContentValues.TAG,"Error Current Btn Next");
        }
        if (isValidationError > 0) {
            Toasty.warning(getApplicationContext(), "Please fill-in all required fields!", Toast.LENGTH_SHORT).show();
        } else {
            Toasty.success(getApplicationContext(), "All fields are valid!", Toast.LENGTH_SHORT).show();
            String nma_amount = sh.getString("nma_amount", "0");
            if (MANDATORY_PAGE_LOCATION == 1 && Float.parseFloat(nma_amount) < 100) {
                MANDATORY_PAGE_LOCATION = MANDATORY_PAGE_LOCATION + 2;
                if (current < layouts.length) {
                    tvPrev.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(current + 1);
                } else {
//                    hide_loading_bar();
                    store_preferences(4);
                    launchHomeScreen();
                }
            } else {
                MANDATORY_PAGE_LOCATION++;
                if (current < layouts.length) {
                    tvPrev.setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem(current);
                } else {
                    store_preferences(4);
                    launchHomeScreen();
                }
            }
        }
    }

    public void province_event() {
        String tmp_province_code = !aat_province_code.getText().toString().matches("") ? aat_province_code.getText().toString().toUpperCase() : "";
        Cursor get_prov_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='"+tmp_province_code+"' AND geographic_level='province' LIMIT 1");
        String split_prov_psgc = null;

        aat_municipality_code.setText(null, false);
        aat_barangay_code.setText(null, false);
        municipality_list.clear();
        barangay_list.clear();

        try {
            while (get_prov_psgc.moveToNext()) {
                psgc_province = get_prov_psgc.getString(0);
                split_prov_psgc = get_prov_psgc.getString(0);
                split_prov_psgc = split_prov_psgc.substring(0,4);
            }
        } finally {
            get_prov_psgc.close();
        }

        Cursor muni_data = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE correspondence_code LIKE '%"+split_prov_psgc+"%' AND geographic_level='municipality'");
        municipality_list.clear();
        try {
            while(muni_data.moveToNext()) {
                municipality_list.add(muni_data.getString(0).toUpperCase());
            }
        } finally {
            muni_data.close();
        }

        String[] muni_list = new String[municipality_list.size()];
        muni_list = municipality_list.toArray(muni_list);

        ArrayAdapter<String> adapterMunicipality = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, muni_list);
        adapterMunicipality.setDropDownViewResource(simple_spinner_dropdown_item);
        aat_municipality_code.setAdapter(adapterMunicipality);

    }

    public void municipality_event() {
        String split_prov_code = psgc_province.substring(0, 4);
        String tmp_municipality_code = !aat_municipality_code.getText().toString().matches("") ? aat_municipality_code.getText().toString().toUpperCase() : "";
        Cursor get_muni_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='"+tmp_municipality_code+"' AND geographic_level='municipality' AND correspondence_code LIKE '%"+split_prov_code+"%' LIMIT 1");
        String split_muni_psgc = null;

        aat_barangay_code.setText(null, false);
        barangay_list.clear();

        try {
            while (get_muni_psgc.moveToNext()) {
                psgc_municipality = get_muni_psgc.getString(0);
                split_muni_psgc = get_muni_psgc.getString(0);
                split_muni_psgc = split_muni_psgc.substring(0,6);
            }
        } finally {
            get_muni_psgc.close();
        }

        Cursor brgy_data = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE correspondence_code LIKE '%"+split_muni_psgc+"%' AND geographic_level='barangay'");
        barangay_list.clear();
        try {
            while(brgy_data.moveToNext()) {
                barangay_list.add(brgy_data.getString(0).toUpperCase());
            }
        } finally {
            brgy_data.close();
        }

        String[] brgy_list = new String[barangay_list.size()];
        brgy_list = barangay_list.toArray(brgy_list);

        ArrayAdapter<String> adapterBarangay = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, brgy_list);
        adapterBarangay.setDropDownViewResource(simple_spinner_dropdown_item);
        aat_barangay_code.setAdapter(adapterBarangay);
    }

    public void barangay_event() {
        String split_muni_code = psgc_municipality.substring(0, 6);
        String tmp_barangay_code = !aat_barangay_code.getText().toString().matches("") ? aat_barangay_code.getText().toString().toUpperCase() : "";
        Cursor get_brgy_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='"+tmp_barangay_code+"' AND geographic_level='barangay' AND correspondence_code LIKE '%"+ split_muni_code +"%' LIMIT 1");
        try {
            while (get_brgy_psgc.moveToNext()) {
                psgc_barangay = get_brgy_psgc.getString(0);
            }
        } finally {
            get_brgy_psgc.close();
        }
    }

    public void store_preferences(int pos) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        switch(pos) {
            case 1:
                String hh_id = edt_hh_id.getText().toString();
                String hh_set = aat_set.getText().toString();
                String last_name = edt_last_name.getText().toString();
                String first_name = edt_first_name.getText().toString();
                String middle_name = edt_middle_name.getText().toString();
                String ext_name = aat_ext_name.getText().toString();
                String other_ext_name = edt_other_ext_name.getText().toString();
                String hh_status = aat_hh_status.getText().toString();
                String province = aat_province_code.getText().toString();
                String municipality = aat_municipality_code.getText().toString();
                String barangay = aat_barangay_code.getText().toString();
                String sex = aat_sex.getText().toString();
                String is_grantee = aat_is_grantee.getText().toString();
                String relationship_to_grantee = aat_relationship_to_grantee.getText().toString();
                String contact_no = edt_contact_no.getText().toString();
                String contact_no_of = aat_contact_no_of.getText().toString();
                String contact_no_of_others = edt_contact_no_of_others.getText().toString();
                String assigned_staff = edt_assigned_staff.getText().toString();
                String is_minor = aat_is_minor.getText().toString();
                String representative_name = edt_representative_name.getText().toString();

                myEdit.putString("hh_id",hh_id);
                myEdit.putString("hh_set",hh_set);
                myEdit.putString("last_name",last_name);
                myEdit.putString("first_name",first_name);
                myEdit.putString("middle_name",middle_name);
                myEdit.putString("ext_name",ext_name);
                myEdit.putString("other_ext_name",other_ext_name);
                myEdit.putString("hh_status",hh_status);
                myEdit.putString("province",province);
                myEdit.putString("municipality",municipality);
                myEdit.putString("barangay",barangay);
                myEdit.putString("sex",sex);
                myEdit.putString("is_grantee",is_grantee);
                myEdit.putString("relationship_to_grantee",relationship_to_grantee);
                myEdit.putString("contact_no",contact_no);
                myEdit.putString("contact_no_of",contact_no_of);
                myEdit.putString("contact_no_of_others",contact_no_of_others);
                myEdit.putString("assigned_staff",assigned_staff);
                myEdit.putString("is_minor",is_minor);
                myEdit.putString("representative_name", representative_name);

                myEdit.commit();
                break;
            case 2:
                String card_number_prefilled = edt_card_number_prefilled.getText().toString();
                String distribution_status = aat_distribution_status.getText().toString();
                String release_date = edt_release_date.getText().toString();
                String release_by = edt_release_by.getText().toString();
                String release_place = edt_release_place.getText().toString();
                String card_physically_presented = aat_card_physically_presented.getText().toString();
                String card_pin_is_attached = aat_card_pin_is_attached.getText().toString();
                String reason_not_presented = aat_reason_not_presented.getText().toString();
                String others_reason_not_presented = edt_others_reason_not_presented.getText().toString();
                String reason_unclaimed = aat_reason_unclaimed.getText().toString();
                String others_reason_unclaimed = edt_others_reason_unclaimed.getText().toString();
                String card_replacement_request = aat_card_replacement_request.getText().toString();
                String card_replacement_request_submitted_details = edt_card_replacement_request_submitted_details.getText().toString();
                String card_number_inputted = edt_card_number_inputted.getText().toString();
                String card_number_series = edt_card_number_series.getText().toString();
                String id_exists = aat_id_exists.getText().toString();
                String lender_name = edt_lender_name.getText().toString();
                String date_pawned = edt_date_pawned.getText().toString();
                String loan_amount = edt_loan_amount.getText().toString();
                String lender_address = edt_lender_address.getText().toString();
                String date_retrieved = edt_date_retrieved.getText().toString();
                String interest = edt_interest.getText().toString();
                String status = aat_status.getText().toString();
                String reason = edt_reason.getText().toString();
                String offense_history = aat_offense_history.getText().toString();
                String offense_date = edt_offense_date.getText().toString();
                String remarks = edt_remarks.getText().toString();
                String staff_intervention = edt_staff_intervention.getText().toString();
                String other_details = edt_other_details.getText().toString();
                String card_holder_name1 = edt_card_holder_name1.getText().toString();
                String card_holder_name2 = edt_card_holder_name2.getText().toString();
                String card_holder_name3 = edt_card_holder_name3.getText().toString();
                String card_holder_name4 = edt_card_holder_name4.getText().toString();
                String card_holder_name5 = edt_card_holder_name5.getText().toString();
                String distribution_status1 = aat_distribution_status1.getText().toString();
                String distribution_status2 = aat_distribution_status2.getText().toString();
                String distribution_status3 = aat_distribution_status3.getText().toString();
                String distribution_status4 = aat_distribution_status4.getText().toString();
                String distribution_status5 = aat_distribution_status5.getText().toString();
                String release_date1 = edt_release_date1.getText().toString();
                String release_date2 = edt_release_date2.getText().toString();
                String release_date3 = edt_release_date3.getText().toString();
                String release_date4 = edt_release_date4.getText().toString();
                String release_date5 = edt_release_date5.getText().toString();
                String release_by1 = edt_release_by1.getText().toString();
                String release_by2 = edt_release_by2.getText().toString();
                String release_by3 = edt_release_by3.getText().toString();
                String release_by4 = edt_release_by4.getText().toString();
                String release_by5 = edt_release_by5.getText().toString();
                String release_place1 = edt_release_place1.getText().toString();
                String release_place2 = edt_release_place2.getText().toString();
                String release_place3 = edt_release_place3.getText().toString();
                String release_place4 = edt_release_place4.getText().toString();
                String release_place5 = edt_release_place5.getText().toString();
                String card_physically_presented1 = aat_card_physically_presented1.getText().toString();
                String card_physically_presented2 = aat_card_physically_presented2.getText().toString();
                String card_physically_presented3 = aat_card_physically_presented3.getText().toString();
                String card_physically_presented4 = aat_card_physically_presented4.getText().toString();
                String card_physically_presented5 = aat_card_physically_presented5.getText().toString();
                String card_pin_is_attached1 = aat_card_pin_is_attached1.getText().toString();
                String card_pin_is_attached2 = aat_card_pin_is_attached2.getText().toString();
                String card_pin_is_attached3 = aat_card_pin_is_attached3.getText().toString();
                String card_pin_is_attached4 = aat_card_pin_is_attached4.getText().toString();
                String card_pin_is_attached5 = aat_card_pin_is_attached5.getText().toString();
                String reason_not_presented1 = aat_reason_not_presented1.getText().toString();
                String reason_not_presented2 = aat_reason_not_presented2.getText().toString();
                String reason_not_presented3 = aat_reason_not_presented3.getText().toString();
                String reason_not_presented4 = aat_reason_not_presented4.getText().toString();
                String reason_not_presented5 = aat_reason_not_presented5.getText().toString();
                String others_reason_not_presented1 = edt_others_reason_not_presented1.getText().toString();
                String others_reason_not_presented2 = edt_others_reason_not_presented2.getText().toString();
                String others_reason_not_presented3 = edt_others_reason_not_presented3.getText().toString();
                String others_reason_not_presented4 = edt_others_reason_not_presented4.getText().toString();
                String others_reason_not_presented5 = edt_others_reason_not_presented5.getText().toString();
                String reason_unclaimed1 = aat_reason_unclaimed1.getText().toString();
                String reason_unclaimed2 = aat_reason_unclaimed2.getText().toString();
                String reason_unclaimed3 = aat_reason_unclaimed3.getText().toString();
                String reason_unclaimed4 = aat_reason_unclaimed4.getText().toString();
                String reason_unclaimed5 = aat_reason_unclaimed5.getText().toString();
                String others_reason_unclaimed1 = edt_others_reason_unclaimed1.getText().toString();
                String others_reason_unclaimed2 = edt_others_reason_unclaimed2.getText().toString();
                String others_reason_unclaimed3 = edt_others_reason_unclaimed3.getText().toString();
                String others_reason_unclaimed4 = edt_others_reason_unclaimed4.getText().toString();
                String others_reason_unclaimed5 = edt_others_reason_unclaimed5.getText().toString();
                String card_replacement_request1 = aat_card_replacement_request1.getText().toString();
                String card_replacement_request2 = aat_card_replacement_request2.getText().toString();
                String card_replacement_request3 = aat_card_replacement_request3.getText().toString();
                String card_replacement_request4 = aat_card_replacement_request4.getText().toString();
                String card_replacement_request5 = aat_card_replacement_request5.getText().toString();
                String card_replacement_request_submitted_details1 = edt_card_replacement_request_submitted_details1.getText().toString();
                String card_replacement_request_submitted_details2 = edt_card_replacement_request_submitted_details2.getText().toString();
                String card_replacement_request_submitted_details3 = edt_card_replacement_request_submitted_details3.getText().toString();
                String card_replacement_request_submitted_details4 = edt_card_replacement_request_submitted_details4.getText().toString();
                String card_replacement_request_submitted_details5 = edt_card_replacement_request_submitted_details5.getText().toString();
                String card_number_inputted1 = edt_card_number_inputted1.getText().toString();
                String card_number_inputted2 = edt_card_number_inputted2.getText().toString();
                String card_number_inputted3 = edt_card_number_inputted3.getText().toString();
                String card_number_inputted4 = edt_card_number_inputted4.getText().toString();
                String card_number_inputted5 = edt_card_number_inputted5.getText().toString();
                String card_number_series1 = edt_card_number_series1.getText().toString();
                String card_number_series2 = edt_card_number_series2.getText().toString();
                String card_number_series3 = edt_card_number_series3.getText().toString();
                String card_number_series4 = edt_card_number_series4.getText().toString();
                String card_number_series5 = edt_card_number_series5.getText().toString();
                String pawning_remarks1 = edt_pawning_remarks1.getText().toString();
                String pawning_remarks2 = edt_pawning_remarks1.getText().toString();
                String pawning_remarks3 = edt_pawning_remarks1.getText().toString();
                String pawning_remarks4 = edt_pawning_remarks1.getText().toString();
                String pawning_remarks5 = edt_pawning_remarks1.getText().toString();

                myEdit.putString("card_number_prefilled", card_number_prefilled);
                myEdit.putString("distribution_status", distribution_status);
                myEdit.putString("release_date", release_date);
                myEdit.putString("release_by", release_by);
                myEdit.putString("release_place", release_place);
                myEdit.putString("card_physically_presented", card_physically_presented);
                myEdit.putString("card_pin_is_attached", card_pin_is_attached);
                myEdit.putString("reason_not_presented", reason_not_presented);
                myEdit.putString("others_reason_not_presented", others_reason_not_presented);
                myEdit.putString("reason_unclaimed", reason_unclaimed);
                myEdit.putString("others_reason_unclaimed", others_reason_unclaimed);
                myEdit.putString("card_replacement_request", card_replacement_request);
                myEdit.putString("card_replacement_request_submitted_details", card_replacement_request_submitted_details);
                myEdit.putString("card_number_inputted", card_number_inputted);
                myEdit.putString("card_number_series", card_number_series);
                myEdit.putString("id_exists", id_exists);
                myEdit.putString("lender_name", lender_name);
                myEdit.putString("date_pawned", date_pawned);
                myEdit.putString("loan_amount", loan_amount);
                myEdit.putString("lender_address", lender_address);
                myEdit.putString("date_retrieved", date_retrieved);
                myEdit.putString("interest", interest);
                myEdit.putString("status", status);
                myEdit.putString("reason", reason);
                myEdit.putString("offense_history", offense_history);
                myEdit.putString("offense_date", offense_date);
                myEdit.putString("remarks", remarks);
                myEdit.putString("staff_intervention", staff_intervention);
                myEdit.putString("other_details", other_details);
                myEdit.putString("card_holder_name1", card_holder_name1);
                myEdit.putString("card_holder_name2", card_holder_name2);
                myEdit.putString("card_holder_name3", card_holder_name3);
                myEdit.putString("card_holder_name4", card_holder_name4);
                myEdit.putString("card_holder_name5", card_holder_name5);
                myEdit.putString("distribution_status1", distribution_status1);
                myEdit.putString("distribution_status2", distribution_status2);
                myEdit.putString("distribution_status3", distribution_status3);
                myEdit.putString("distribution_status4", distribution_status4);
                myEdit.putString("distribution_status5", distribution_status5);
                myEdit.putString("release_date1", release_date1);
                myEdit.putString("release_date2", release_date2);
                myEdit.putString("release_date3", release_date3);
                myEdit.putString("release_date4", release_date4);
                myEdit.putString("release_date5", release_date5);
                myEdit.putString("release_by1", release_by1);
                myEdit.putString("release_by2", release_by2);
                myEdit.putString("release_by3", release_by3);
                myEdit.putString("release_by4", release_by4);
                myEdit.putString("release_by5", release_by5);
                myEdit.putString("release_place1", release_place1);
                myEdit.putString("release_place2", release_place2);
                myEdit.putString("release_place3", release_place3);
                myEdit.putString("release_place4", release_place4);
                myEdit.putString("release_place5", release_place5);
                myEdit.putString("card_physically_presented1", card_physically_presented1);
                myEdit.putString("card_physically_presented2", card_physically_presented2);
                myEdit.putString("card_physically_presented3", card_physically_presented3);
                myEdit.putString("card_physically_presented4", card_physically_presented4);
                myEdit.putString("card_physically_presented5", card_physically_presented5);
                myEdit.putString("card_pin_is_attached1", card_pin_is_attached1);
                myEdit.putString("card_pin_is_attached2", card_pin_is_attached2);
                myEdit.putString("card_pin_is_attached3", card_pin_is_attached3);
                myEdit.putString("card_pin_is_attached4", card_pin_is_attached4);
                myEdit.putString("card_pin_is_attached5", card_pin_is_attached5);
                myEdit.putString("reason_not_presented1", reason_not_presented1);
                myEdit.putString("reason_not_presented2", reason_not_presented2);
                myEdit.putString("reason_not_presented3", reason_not_presented3);
                myEdit.putString("reason_not_presented4", reason_not_presented4);
                myEdit.putString("reason_not_presented5", reason_not_presented5);
                myEdit.putString("others_reason_not_presented1", others_reason_not_presented1);
                myEdit.putString("others_reason_not_presented2", others_reason_not_presented2);
                myEdit.putString("others_reason_not_presented3", others_reason_not_presented3);
                myEdit.putString("others_reason_not_presented4", others_reason_not_presented4);
                myEdit.putString("others_reason_not_presented5", others_reason_not_presented5);
                myEdit.putString("reason_unclaimed1", reason_unclaimed1);
                myEdit.putString("reason_unclaimed2", reason_unclaimed2);
                myEdit.putString("reason_unclaimed3", reason_unclaimed3);
                myEdit.putString("reason_unclaimed4", reason_unclaimed4);
                myEdit.putString("reason_unclaimed5", reason_unclaimed5);
                myEdit.putString("others_reason_unclaimed1", others_reason_unclaimed1);
                myEdit.putString("others_reason_unclaimed2", others_reason_unclaimed2);
                myEdit.putString("others_reason_unclaimed3", others_reason_unclaimed3);
                myEdit.putString("others_reason_unclaimed4", others_reason_unclaimed4);
                myEdit.putString("others_reason_unclaimed5", others_reason_unclaimed5);
                myEdit.putString("card_replacement_request1", card_replacement_request1);
                myEdit.putString("card_replacement_request2", card_replacement_request2);
                myEdit.putString("card_replacement_request3", card_replacement_request3);
                myEdit.putString("card_replacement_request4", card_replacement_request4);
                myEdit.putString("card_replacement_request5", card_replacement_request5);
                myEdit.putString("card_replacement_request_submitted_details1", card_replacement_request_submitted_details1);
                myEdit.putString("card_replacement_request_submitted_details2", card_replacement_request_submitted_details2);
                myEdit.putString("card_replacement_request_submitted_details3", card_replacement_request_submitted_details3);
                myEdit.putString("card_replacement_request_submitted_details4", card_replacement_request_submitted_details4);
                myEdit.putString("card_replacement_request_submitted_details5", card_replacement_request_submitted_details5);
                myEdit.putString("card_number_inputted1", card_number_inputted1);
                myEdit.putString("card_number_inputted2", card_number_inputted2);
                myEdit.putString("card_number_inputted3", card_number_inputted3);
                myEdit.putString("card_number_inputted4", card_number_inputted4);
                myEdit.putString("card_number_inputted5", card_number_inputted5);
                myEdit.putString("card_number_series1", card_number_series1);
                myEdit.putString("card_number_series2", card_number_series2);
                myEdit.putString("card_number_series3", card_number_series3);
                myEdit.putString("card_number_series4", card_number_series4);
                myEdit.putString("card_number_series5", card_number_series5);
                myEdit.putString("pawning_remarks1", pawning_remarks1);
                myEdit.putString("pawning_remarks2", pawning_remarks2);
                myEdit.putString("pawning_remarks3", pawning_remarks3);
                myEdit.putString("pawning_remarks4", pawning_remarks4);
                myEdit.putString("pawning_remarks5", pawning_remarks5);
                myEdit.commit();
                break;
            case 3:
                String nma_amount = edt_nma_amount.getText().toString();
                String nma_reason = aat_nma_reason.getText().toString();
                String nma_others_reason = edt_nma_others_reason.getText().toString();
                String nma_date_claimed = edt_nma_date_claimed.getText().toString();
                String nma_remarks = edt_nma_remarks.getText().toString();

                myEdit.putString("nma_amount", nma_amount);
                myEdit.putString("nma_reason", nma_reason);
                myEdit.putString("nma_others_reason", nma_others_reason);
                myEdit.putString("nma_date_claimed", nma_date_claimed);
                myEdit.putString("nma_remarks", nma_remarks);
                myEdit.commit();
                break;
            case 4:
                String overall_remarks = edt_overall_remarks.getText().toString();

                myEdit.putString("overall_remarks", overall_remarks);
                myEdit.commit();
                break;
            default:
                break;
        }
    }


    public void search_household(){
        String household_no ="";
        household_no = edt_hh_id.getText().toString();
        if (!household_no.matches("")){
           Cursor search = MainActivity.sqLiteHelper.getData("SELECT id,first_name,last_name,middle_name,ext_name,hh_id,hh_status,province,municipality,barangay,sex,hh_set_group,nma_amount,grantee_card_number,grantee_distribution_status,grantee_card_release_date,other_card_number_1,other_card_holder_name_1,other_card_distribution_status_1,other_card_release_date_1,other_card_number_2,other_card_holder_name_2,other_card_distribution_status_2,other_card_release_date_2,other_card_number_3,other_card_holder_name_3,other_card_distribution_status_3,other_card_release_date_3,other_card_number_4,other_card_holder_name_4,other_card_distribution_status_4,other_card_release_date_4,other_card_number_5,other_card_holder_name_5,other_card_distribution_status_5,other_card_release_date_5,upload_history_id,record_counter,created_at,updated_at,validated_at, nma_non_emv, nma_card_name FROM emv_validations WHERE hh_id='"+household_no+"'");
            while (search.moveToNext()) {
                emv_monitoring_id = search.getInt(0);
                first_name = search.getString(1);
                last_name = search.getString(2);
                middle_name = search.getString(3);
                ext_name = search.getString(4);
                hh_id = search.getString(5);
                hh_status = search.getString(6);
                province = search.getString(7);
                municipality = search.getString(8);
                barangay = search.getString(9);
                sex = search.getString(10);
                hh_set_group = search.getString(11);
                nma_amount = search.getString(12);
                grantee_card_number = search.getString(13);
                grantee_distribution_status = search.getString(14);
                grantee_distribution_status_record = search.getString(14);
                grantee_card_release_date = search.getString(15);
                other_card_number_1 = search.getString(16);
                other_card_holder_1 = search.getString(17);
                other_card_distribution_status_1 = search.getString(18);
                grantee_distribution_status_record1 = search.getString(18);
                other_card_release_date_1 = search.getString(19);
                grantee_card_release_date1 = search.getString(19);
                other_card_number_2 = search.getString(20);
                other_card_holder_2 = search.getString(21);
                other_card_distribution_status_2 = search.getString(22);
                grantee_distribution_status_record2 = search.getString(22);
                other_card_release_date_2 = search.getString(23);
                grantee_card_release_date2 = search.getString(23);
                other_card_number_3 = search.getString(24);
                other_card_holder_3 = search.getString(25);
                other_card_distribution_status_3 = search.getString(26);
                grantee_distribution_status_record3 = search.getString(26);
                other_card_release_date_3 = search.getString(27);
                grantee_card_release_date3 = search.getString(27);
                other_card_number_4 = search.getString(28);
                other_card_holder_4 = search.getString(29);
                other_card_distribution_status_4 = search.getString(30);
                grantee_distribution_status_record4 = search.getString(30);
                other_card_release_date_4 = search.getString(31);
                grantee_card_release_date4 = search.getString(31);
                other_card_number_5 = search.getString(32);
                other_card_holder_5 = search.getString(33);
                other_card_distribution_status_5 = search.getString(34);
                grantee_distribution_status_record5 = search.getString(34);
                other_card_release_date_5 = search.getString(35);
                grantee_card_release_date5 = search.getString(35);
                upload_history_id = search.getString(36);
                record_counter = search.getInt(37);
                created_at = search.getString(38);
                updated_at = search.getString(39);
                validated_at = search.getString(40);
                nma_non_emv = search.getString(41);
                nma_card_name = search.getString(42);
            }
            
             String other_card1 = String.valueOf(other_card_number_1);
             String other_card2 = String.valueOf(other_card_number_2);
             String other_card3 = String.valueOf(other_card_number_3);
             String other_card4 = String.valueOf(other_card_number_4);
             String other_card5 = String.valueOf(other_card_number_5);
             
             if (other_card1.matches("null")){
                 other_card_number_1 = "";
             }
             if (other_card2.matches("null")){
                 other_card_number_2 = "";
             }
             if (other_card3.matches("null")){
                 other_card_number_3 = "";
             }
             if (other_card4.matches("null")){
                 other_card_number_4 = "";
             }
             if (other_card5.matches("null")){
                 other_card_number_5 = "";
             }

            clear_preferences();
            aat_set.setText(null, false);
            edt_last_name.setText(null);
            edt_first_name.setText(null);
            edt_middle_name.setText(null);
            aat_ext_name.setText(null, false);
            edt_other_ext_name.setText(null);
            aat_hh_status.setText(null, false);
            aat_province_code.setText(null, false);
            aat_municipality_code.setText(null, false);
            aat_barangay_code.setText(null, false);
            aat_sex.setText(null, false);
            aat_is_grantee.setText(null, false);
            aat_relationship_to_grantee.setText(null, false);
            edt_contact_no.setText(null);
            aat_contact_no_of.setText(null, false);
            edt_contact_no_of_others.setText(null);
            edt_assigned_staff.setText(null);
            aat_is_minor.setText(null);

            if (search == null || search.getCount() == 0){
                edt_last_name.setText("");
                aat_hh_status.setText("");
                aat_set.setText("");
                aat_sex.setText("");
                Toasty.error(getApplicationContext(),"Household number not found", Toasty.LENGTH_SHORT).show();
            } else {
                if  (validated_at.matches("null")){
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();

                    card_count = 0;

                    if (!other_card_number_1.matches("") && !other_card_number_1.matches("null")) {
                        card_count++;
                    }

                    if (!other_card_number_2.matches("") && !other_card_number_2.matches("null")) {
                        card_count++;
                    }

                    if (!other_card_number_3.matches("") && !other_card_number_3.matches("null")) {
                        card_count++;
                    }

                    if (!other_card_number_4.matches("") && !other_card_number_4.matches("null")) {
                        card_count++;
                    }

                    if (!other_card_number_5.matches("") && !other_card_number_5.matches("null")) {
                        card_count++;
                    }

                    myEdit.putInt("card_count", card_count);
                    myEdit.putInt("emv_monitoring_id", emv_monitoring_id);
                    myEdit.putString("hh_id", hh_id);
                    myEdit.putString("first_name", first_name);
                    myEdit.putString("last_name", last_name);
                    myEdit.putString("middle_name", middle_name);
                    myEdit.putString("ext_name", ext_name);
                    myEdit.putString("hh_status", hh_status);
                    myEdit.putString("province", province);
                    myEdit.putString("municipality", municipality);
                    myEdit.putString("barangay", barangay);
                    myEdit.putString("sex", sex);
                    myEdit.putString("hh_set", hh_set_group);

                    myEdit.putString("nma_non_emv", nma_non_emv);
                    myEdit.putString("nma_card_name", nma_card_name);
                    myEdit.putString("nma_amount", nma_amount);
                    myEdit.putString("pressBtn_search", "true");
                    myEdit.putString("card_number_prefilled", grantee_card_number);

                    myEdit.putString("distribution_status_record", grantee_distribution_status);
                    myEdit.putString("distribution_status_record1", grantee_distribution_status_record1);
                    myEdit.putString("distribution_status_record2", grantee_distribution_status_record2);
                    myEdit.putString("distribution_status_record3", grantee_distribution_status_record3);
                    myEdit.putString("distribution_status_record4", grantee_distribution_status_record4);
                    myEdit.putString("distribution_status_record5", grantee_distribution_status_record5);

                    myEdit.putString("release_date_record", grantee_card_release_date);
                    myEdit.putString("release_date_record1", grantee_card_release_date1);
                    myEdit.putString("release_date_record2", grantee_card_release_date2);
                    myEdit.putString("release_date_record3", grantee_card_release_date3);
                    myEdit.putString("release_date_record4", grantee_card_release_date4);
                    myEdit.putString("release_date_record5", grantee_card_release_date5);


                    myEdit.putString("card_number_prefilled1", other_card_number_1);
                    myEdit.putString("card_holder_name1", other_card_holder_1);
                    myEdit.putString("distribution_status1", other_card_distribution_status_1);
                    myEdit.putString("release_date1", other_card_release_date_1);
                    myEdit.putString("card_number_prefilled2", other_card_number_2);
                    myEdit.putString("card_holder_name2", other_card_holder_2);
                    myEdit.putString("distribution_status2", other_card_distribution_status_2);
                    myEdit.putString("release_date2", other_card_release_date_2);
                    myEdit.putString("card_number_prefilled3", other_card_number_3);
                    myEdit.putString("card_holder_name3", other_card_holder_3);
                    myEdit.putString("distribution_status3", other_card_distribution_status_3);
                    myEdit.putString("release_date3", other_card_release_date_3);
                    myEdit.putString("card_number_prefilled4", other_card_number_4);
                    myEdit.putString("card_holder_name4", other_card_holder_4);
                    myEdit.putString("distribution_status4", other_card_distribution_status_4);
                    myEdit.putString("release_date4", other_card_release_date_4);
                    myEdit.putString("card_number_prefilled5", other_card_number_5);
                    myEdit.putString("card_holder_name5", other_card_holder_5);
                    myEdit.putString("distribution_status5", other_card_distribution_status_5);
                    myEdit.putString("release_date5", other_card_release_date_5);
                    myEdit.commit();

                    aat_set.setText(hh_set_group);
                    edt_last_name.setText(last_name);
                    edt_first_name.setText(first_name);
                    edt_middle_name.setText(middle_name);
                    aat_ext_name.setText(ext_name);
                    aat_hh_status.setText(hh_status,false);
                    aat_province_code.setText(province,false);

                    if (!province.matches("")) {
                        province_event();
                    }
                    aat_municipality_code.setText(municipality,false);
                    if (!municipality.matches("")) {
                        municipality_event();
                    }
                    aat_barangay_code.setText(barangay,false);
                    if (!barangay.matches("")) {
                        barangay_event();
                    }
                    aat_sex.setText(sex,false);

                    Toasty.success(getApplicationContext(),"Household Found", Toasty.LENGTH_SHORT).show();

//                    otherCardVisibility();
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
    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void load_loading_bar(){
        loadingBar.Show_loading_bar();
    }
    public void hide_loading_bar(){
        loadingBar.Hide_loading_bar();
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
                    til_hh_id.setError("Not enough length");
                }
                else{
                    til_hh_id.setError(null);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void initialize_layout(int pos) {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();

        Cursor get_db_prov = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE geographic_level='province'");

        while(get_db_prov.moveToNext()) {
            province_list.add(get_db_prov.getString(0).toUpperCase());
        }

        String[] prov_list = new String[province_list.size()];
        prov_list = province_list.toArray(prov_list);

        switch (pos) {
            case 0:
                btn_search_hh = (Button) findViewById(R.id.btnSearchHh);

                xml_initialization(1);
                til_representative_name.setVisibility(View.GONE);

                ArrayAdapter<String> adapterSex = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Sex);
                ArrayAdapter<String> adapterAns = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterClientStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ClientStatus);
                ArrayAdapter<String> adapterContact_no_of = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Contact_no_of);
                ArrayAdapter<String> adapterInterviewee = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Interviewee);
                ArrayAdapter<String> adapterRelationshipToGrantee = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, RelationshipToGrantee);
                ArrayAdapter<String> adapterHouseholdSet = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, HouseholdSet);
                ArrayAdapter<String> adapterExtensionName = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ExtensionName);
                ArrayAdapter<String> adapterProvince = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, prov_list);

                adapterSex.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterAns.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterClientStatus.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterContact_no_of.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterInterviewee.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterRelationshipToGrantee.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterHouseholdSet.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterExtensionName.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterProvince.setDropDownViewResource(simple_spinner_dropdown_item);

                aat_sex.setAdapter(adapterSex);
                aat_is_minor.setAdapter(adapterAns);
                aat_hh_status.setAdapter(adapterClientStatus);
                aat_contact_no_of.setAdapter(adapterContact_no_of);
                aat_is_grantee.setAdapter(adapterInterviewee);
                aat_relationship_to_grantee.setAdapter(adapterRelationshipToGrantee);
                aat_set.setAdapter(adapterHouseholdSet);
                aat_ext_name.setAdapter(adapterExtensionName);
                aat_province_code.setAdapter(adapterProvince);

                String hh_id = sh.getString("hh_id","");
                String hh_set = sh.getString("hh_set","");
                String last_name = sh.getString("last_name","");
                String first_name = sh.getString("first_name","");
                String middle_name = sh.getString("middle_name","");
                String ext_name = sh.getString("ext_name","");
                String other_ext_name = sh.getString("other_ext_name","");
                String hh_status = sh.getString("hh_status","");
                String province = sh.getString("province","");
                String municipality = sh.getString("municipality","");
                String barangay = sh.getString("barangay","");
                String sex = sh.getString("sex","");
                String is_grantee = sh.getString("is_grantee","");
                String relationship_to_grantee = sh.getString("relationship_to_grantee","");
                String contact_no = sh.getString("contact_no","");
                String contact_no_of = sh.getString("contact_no_of","");
                String contact_no_of_others = sh.getString("contact_no_of_others","");
                String assigned_staff = sh.getString("assigned_staff","");
                String is_minor = sh.getString("is_minor","");
                String representative_name = sh.getString("representative_name", "");

                edt_hh_id.setText(hh_id);
                aat_set.setText(hh_set);
                aat_set.setEnabled(false);
                edt_last_name.setText(last_name);
                edt_first_name.setText(first_name);
                edt_middle_name.setText(middle_name);
                aat_ext_name.setText(ext_name, false);
                edt_other_ext_name.setText(other_ext_name);
                aat_hh_status.setText(hh_status, false);
                aat_province_code.setText(province, false);
                if (!province.matches("")) {
                    province_event();
                }
                aat_municipality_code.setText(municipality, false);
                if (!municipality.matches("")) {
                    municipality_event();
                }
                aat_barangay_code.setText(barangay, false);
                if (!barangay.matches("")) {
                    barangay_event();
                }
                aat_sex.setText(sex, false);
                aat_is_grantee.setText(is_grantee, false);
                aat_relationship_to_grantee.setText(relationship_to_grantee, false);
                edt_contact_no.setText(contact_no);
                aat_contact_no_of.setText(contact_no_of, false);
                edt_contact_no_of_others.setText(contact_no_of_others);
                aat_is_minor.setText(assigned_staff, false);
                edt_assigned_staff.setText(is_minor);
                edt_representative_name.setText(representative_name);

                if (ext_name.matches("Others")) {
                    til_other_ext_name.setVisibility(View.VISIBLE);
                } else {
                    til_other_ext_name.setVisibility(View.GONE);
                }

                if (relationship_to_grantee.matches("")) {
                    til_relationship_to_grantee.setVisibility(View.GONE);
                }

                if (contact_no.length() != 10) {
                    til_contact_no_of.setVisibility(View.GONE);
                    aat_contact_no_of.setText(null, false);
                    til_contact_no_of_others.setVisibility(View.GONE);
                    edt_contact_no_of_others.setText(null);
                } else if (contact_no.length() == 10) {
                    if (contact_no_of.matches("Others")) {
                        til_contact_no_of_others.setVisibility(View.VISIBLE);
                    } else {
                        til_contact_no_of_others.setVisibility(View.GONE);
                    }
                }

                if (aat_is_grantee.getText().toString().matches("Representative")) {
                    til_relationship_to_grantee.setVisibility(View.VISIBLE);
                    til_representative_name.setVisibility(View.VISIBLE);
                } else {
                    til_relationship_to_grantee.setVisibility(View.GONE);
                    til_representative_name.setVisibility(View.GONE);
                    aat_relationship_to_grantee.setText(null, false);
                }


                til_assigned_staff.setVisibility(View.GONE);
                til_is_minor.setVisibility(View.GONE);

                btn_search_hh.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        search_household();
                    }
                });

                aat_ext_name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (aat_ext_name.getText().toString().matches("Others")) {
                            til_other_ext_name.setVisibility(View.VISIBLE);
                        } else {
                            til_other_ext_name.setVisibility(View.GONE);
                        }
                    }
                });

                aat_province_code.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        province_event();
                    }
                });

                aat_municipality_code.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        municipality_event();
                    }
                });

                aat_barangay_code.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        barangay_event();
                    }
                });

                aat_contact_no_of.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (aat_contact_no_of.getText().toString().matches("Others") && !edt_contact_no.getText().toString().matches("")) {
                            til_contact_no_of_others.setVisibility(View.VISIBLE);
                        } else {
                            til_contact_no_of_others.setVisibility(View.GONE);
                        }
                    }
                });

                edt_contact_no.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        if(s.toString().length() != 10){
                            til_contact_no.setError(required_length);
                            til_contact_no_of.setVisibility(View.GONE);
                            til_contact_no_of_others.setVisibility(View.GONE);
                            aat_contact_no_of.setText(null,null);
                            edt_contact_no_of_others.setText(null);
                        }
                        else{
                            til_contact_no.setError(null);
                            til_contact_no_of.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

                aat_contact_no_of.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_contact_no_of.getText().toString().matches("Others")) {
                            til_contact_no_of_others.setVisibility(View.VISIBLE);
                        } else {
                            edt_contact_no_of_others.setText("");
                            til_contact_no_of_others.setError(null);
                            til_contact_no_of_others.setVisibility(View.GONE);
                            edt_contact_no_of_others.setText(null);
                        }
                    }
                });

                aat_is_grantee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        edt_representative_name.setText("");
                        aat_relationship_to_grantee.setText("");
                        if (aat_is_grantee.getText().toString().matches("Representative")) {
                            til_relationship_to_grantee.setVisibility(View.VISIBLE);
                            til_representative_name.setVisibility(View.VISIBLE);
                        } else {

                            til_relationship_to_grantee.setVisibility(View.GONE);
                            til_representative_name.setVisibility(View.GONE);
                            aat_relationship_to_grantee.setText(null, false);
                        }
                    }
                });
                break;
            case 1:
                xml_initialization(2);

                rlOtherCardScanningField1 = findViewById(R.id.rlOtherCardScanning1);
                rlOtherCardScanningField2 = findViewById(R.id.rlOtherCardScanning2);
                rlOtherCardScanningField3 = findViewById(R.id.rlOtherCardScanning3);
                rlOtherCardScanningField4 = findViewById(R.id.rlOtherCardScanning4);
                rlOtherCardScanningField5 = findViewById(R.id.rlOtherCardScanning5);


                til_release_date.setVisibility(View.GONE);
                til_release_by.setVisibility(View.GONE);
                til_release_place.setVisibility(View.GONE);
                til_card_physically_presented.setVisibility(View.GONE);//new
                til_card_pin_is_attached.setVisibility(View.GONE);
                til_reason_not_presented.setVisibility(View.GONE);
                til_others_reason_not_presented.setVisibility(View.GONE);
                til_reason_unclaimed.setVisibility(View.GONE);
                til_others_reason_unclaimed.setVisibility(View.GONE);
                til_card_replacement_request.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details.setVisibility(View.GONE);
                mcvPawning.setVisibility(View.GONE);
                ll_additional_id_layout.setVisibility(View.GONE);
                imgUri.setVisibility(View.INVISIBLE);

                ivOtherScannedImageUrl1.setVisibility(View.INVISIBLE);
                ivOtherScannedImageUrl2.setVisibility(View.INVISIBLE);
                ivOtherScannedImageUrl3.setVisibility(View.INVISIBLE);
                ivOtherScannedImageUrl4.setVisibility(View.INVISIBLE);
                ivOtherScannedImageUrl5.setVisibility(View.INVISIBLE);

                til_release_date1.setVisibility(View.GONE);
                til_release_date2.setVisibility(View.GONE);
                til_release_date3.setVisibility(View.GONE);
                til_release_date4.setVisibility(View.GONE);
                til_release_date5.setVisibility(View.GONE);
                til_release_by1.setVisibility(View.GONE);
                til_release_by2.setVisibility(View.GONE);
                til_release_by3.setVisibility(View.GONE);
                til_release_by4.setVisibility(View.GONE);
                til_release_by5.setVisibility(View.GONE);
                til_release_place1.setVisibility(View.GONE);
                til_release_place2.setVisibility(View.GONE);
                til_release_place3.setVisibility(View.GONE);
                til_release_place4.setVisibility(View.GONE);
                til_release_place5.setVisibility(View.GONE);
                til_card_number_inputted1.setVisibility(View.GONE);
                til_card_number_inputted2.setVisibility(View.GONE);
                til_card_number_inputted3.setVisibility(View.GONE);
                til_card_number_inputted4.setVisibility(View.GONE);
                til_card_number_inputted5.setVisibility(View.GONE);
                til_card_number_series1.setVisibility(View.GONE);
                til_card_number_series2.setVisibility(View.GONE);
                til_card_number_series3.setVisibility(View.GONE);
                til_card_number_series4.setVisibility(View.GONE);
                til_card_number_series5.setVisibility(View.GONE);

                til_card_physically_presented1.setVisibility(View.GONE);
                til_card_physically_presented2.setVisibility(View.GONE);
                til_card_physically_presented3.setVisibility(View.GONE);
                til_card_physically_presented4.setVisibility(View.GONE);
                til_card_physically_presented5.setVisibility(View.GONE);



                til_card_pin_is_attached1.setVisibility(View.GONE);
                til_card_pin_is_attached2.setVisibility(View.GONE);
                til_card_pin_is_attached3.setVisibility(View.GONE);
                til_card_pin_is_attached4.setVisibility(View.GONE);
                til_card_pin_is_attached5.setVisibility(View.GONE);
                til_reason_not_presented1.setVisibility(View.GONE);
                til_reason_not_presented2.setVisibility(View.GONE);
                til_reason_not_presented3.setVisibility(View.GONE);
                til_reason_not_presented4.setVisibility(View.GONE);
                til_reason_not_presented5.setVisibility(View.GONE);
                til_others_reason_not_presented1.setVisibility(View.GONE);
                til_others_reason_not_presented2.setVisibility(View.GONE);
                til_others_reason_not_presented3.setVisibility(View.GONE);
                til_others_reason_not_presented4.setVisibility(View.GONE);
                til_others_reason_not_presented5.setVisibility(View.GONE);
                til_reason_unclaimed1.setVisibility(View.GONE);
                til_reason_unclaimed2.setVisibility(View.GONE);
                til_reason_unclaimed3.setVisibility(View.GONE);
                til_reason_unclaimed4.setVisibility(View.GONE);
                til_reason_unclaimed5.setVisibility(View.GONE);
                til_others_reason_unclaimed1.setVisibility(View.GONE);
                til_others_reason_unclaimed2.setVisibility(View.GONE);
                til_others_reason_unclaimed3.setVisibility(View.GONE);
                til_others_reason_unclaimed4.setVisibility(View.GONE);
                til_others_reason_unclaimed5.setVisibility(View.GONE);
                til_card_replacement_request1.setVisibility(View.GONE);
                til_card_replacement_request2.setVisibility(View.GONE);
                til_card_replacement_request3.setVisibility(View.GONE);
                til_card_replacement_request4.setVisibility(View.GONE);
                til_card_replacement_request5.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details1.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details2.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details3.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details4.setVisibility(View.GONE);
                til_card_replacement_request_submitted_details5.setVisibility(View.GONE);
                til_pawning_remarks1.setVisibility(View.GONE);
                til_pawning_remarks2.setVisibility(View.GONE);
                til_pawning_remarks3.setVisibility(View.GONE);
                til_pawning_remarks4.setVisibility(View.GONE);
                til_pawning_remarks5.setVisibility(View.GONE);
                rlOtherCardScanningField1.setVisibility(View.GONE);
                rlOtherCardScanningField2.setVisibility(View.GONE);
                rlOtherCardScanningField3.setVisibility(View.GONE);
                rlOtherCardScanningField4.setVisibility(View.GONE);
                rlOtherCardScanningField5.setVisibility(View.GONE);

                ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Status);
                ArrayAdapter<String> adapterOffenseHistory = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Offense);
                ArrayAdapter<String> adapterIsAvail = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, CardRequired);
                ArrayAdapter<String> adapterYesNoBlank = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterIsID = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, CardRequired);
                ArrayAdapter<String> adapterIsDistribution = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, distribution);
                ArrayAdapter<String> adapterIsCashCardUnclaimed = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ReasonCashCardUnclaimed);
                ArrayAdapter<String> adapterYesNo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);

                adapterStatus.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterOffenseHistory.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvail.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterYesNoBlank.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsID.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsDistribution.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsCashCardUnclaimed.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterYesNo.setDropDownViewResource(simple_spinner_dropdown_item);

                aat_card_physically_presented.setAdapter(adapterIsAvail);
                aat_card_physically_presented1.setAdapter(adapterYesNoBlank);
                aat_card_physically_presented2.setAdapter(adapterYesNoBlank);
                aat_card_physically_presented3.setAdapter(adapterYesNoBlank);
                aat_card_physically_presented4.setAdapter(adapterYesNoBlank);
                aat_card_physically_presented5.setAdapter(adapterYesNoBlank);
                aat_id_exists.setAdapter(adapterIsID);
                aat_reason_not_presented.setAdapter(adapterYesNo);
                aat_reason_not_presented1.setAdapter(adapterYesNo);
                aat_reason_not_presented2.setAdapter(adapterYesNo);
                aat_reason_not_presented3.setAdapter(adapterYesNo);
                aat_reason_not_presented4.setAdapter(adapterYesNo);
                aat_reason_not_presented5.setAdapter(adapterYesNo);
                aat_distribution_status.setAdapter(adapterIsDistribution);
                aat_distribution_status1.setAdapter(adapterIsDistribution);
                aat_distribution_status2.setAdapter(adapterIsDistribution);
                aat_distribution_status3.setAdapter(adapterIsDistribution);
                aat_distribution_status4.setAdapter(adapterIsDistribution);
                aat_distribution_status5.setAdapter(adapterIsDistribution);
                aat_reason_unclaimed.setAdapter(adapterIsCashCardUnclaimed);
                aat_reason_unclaimed1.setAdapter(adapterIsCashCardUnclaimed);
                aat_reason_unclaimed2.setAdapter(adapterIsCashCardUnclaimed);
                aat_reason_unclaimed3.setAdapter(adapterIsCashCardUnclaimed);
                aat_reason_unclaimed4.setAdapter(adapterIsCashCardUnclaimed);
                aat_reason_unclaimed5.setAdapter(adapterIsCashCardUnclaimed);
                aat_card_replacement_request.setAdapter(adapterIsAvail);
                aat_card_replacement_request1.setAdapter(adapterIsAvail);
                aat_card_replacement_request2.setAdapter(adapterIsAvail);
                aat_card_replacement_request3.setAdapter(adapterIsAvail);
                aat_card_replacement_request4.setAdapter(adapterIsAvail);
                aat_card_replacement_request5.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached1.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached2.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached3.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached4.setAdapter(adapterIsAvail);
                aat_card_pin_is_attached5.setAdapter(adapterIsAvail);
                aat_status.setAdapter(adapterStatus);
                aat_offense_history.setAdapter(adapterOffenseHistory);

                edt_date_pawned.setFocusable(false);
                edt_date_pawned.setClickable(true);
                edt_date_retrieved.setFocusable(false);
                edt_date_retrieved.setClickable(true);
                edt_offense_date.setFocusable(false);
                edt_offense_date.setClickable(true);
                edt_release_date.setFocusable(false);
                edt_release_date.setClickable(true);
                edt_release_date1.setFocusable(false);
                edt_release_date1.setClickable(true);
                edt_release_date2.setFocusable(false);
                edt_release_date2.setClickable(true);
                edt_release_date3.setFocusable(false);
                edt_release_date3.setClickable(true);
                edt_release_date4.setFocusable(false);
                edt_release_date4.setClickable(true);
                edt_release_date5.setFocusable(false);
                edt_release_date5.setClickable(true);
                edt_card_number_prefilled.setEnabled(false);
                edt_card_number_prefilled1.setEnabled(false);
                edt_card_number_prefilled2.setEnabled(false);
                edt_card_number_prefilled3.setEnabled(false);
                edt_card_number_prefilled4.setEnabled(false);
                edt_card_number_prefilled5.setEnabled(false);

                aat_distribution_status_record1.setEnabled(false);
                aat_distribution_status_record2.setEnabled(false);
                aat_distribution_status_record3.setEnabled(false);
                aat_distribution_status_record4.setEnabled(false);
                aat_distribution_status_record5.setEnabled(false);


                btnAddCard = findViewById(R.id.btnAddCard);

                otherCardVisibility();


                scannedCardNumber(edt_card_number_inputted,til_card_number_inputted);
                scannedCardNumber(edt_card_number_inputted1,til_card_number_inputted1);
                scannedCardNumber(edt_card_number_inputted2,til_card_number_inputted2);
                scannedCardNumber(edt_card_number_inputted3,til_card_number_inputted3);
                scannedCardNumber(edt_card_number_inputted4,til_card_number_inputted4);
                scannedCardNumber(edt_card_number_inputted5,til_card_number_inputted5);


                btnAddCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        card_count++;
                        myEdit.putInt("card_count", card_count);
                        myEdit.commit();
                        otherCardVisibility();
                    }
                });

                edt_release_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date);
                    }
                });

                edt_release_date1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date1);
                    }
                });

                edt_release_date2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date2);
                    }
                });

                edt_release_date3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date3);
                    }
                });

                edt_release_date4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date4);
                    }
                });

                edt_release_date5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_release_date5);
                    }
                });

                edt_date_pawned.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_date_pawned);
                    }
                });

                edt_date_retrieved.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDateDialog(edt_date_retrieved);
                    }
                });

                edt_offense_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDateDialog(edt_offense_date);
                    }
                });

                aat_id_exists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ll_additional_id_layout.setVisibility(View.GONE);
                        if (aat_id_exists.getText().toString().matches("Yes")) {
                            ll_additional_id_layout.setVisibility(View.VISIBLE);
                            getImage();
                        } else {
                            sqLiteHelper.queryData("UPDATE tmp_blob SET additional_id_image=NULL WHERE id=1");
                            imgAdditionalId.setImageResource(R.drawable.ic_image);
                        }
                    }
                });

                aat_distribution_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_distribution_status.getText().toString().matches("Unclaimed")||aat_distribution_status.getText().toString().matches("")) {
                            edt_release_date.setText("");
                            edt_release_by.setText("");
                            edt_release_place.setText("");
                            aat_reason_not_presented.setText("");
                            aat_card_physically_presented.setText("");
                            aat_card_replacement_request.setText("");
                            aat_reason_unclaimed.setText(""); //new
                            aat_card_pin_is_attached.setText("");//new
                            edt_card_replacement_request_submitted_details.setText("");
                            edt_others_reason_not_presented.setText("");
                            til_card_pin_is_attached.setVisibility(View.GONE);//new
                            til_reason_not_presented.setVisibility(View.GONE);
                            til_others_reason_not_presented.setVisibility(View.GONE);//new
                            til_card_replacement_request.setVisibility(View.GONE); //new
                            til_release_date.setVisibility(View.GONE);
                            til_release_place.setVisibility(View.GONE);
                            til_release_by.setVisibility(View.GONE);
                            til_reason_unclaimed.setVisibility(View.VISIBLE);
                            til_card_physically_presented.setVisibility(View.GONE);

//                            Reasons = Arrays.copyOfRange(Reasons, 1, Reasons.length);
//                            ArrayAdapter<String> adapterYesNo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);
//                            adapterYesNo.setDropDownViewResource(simple_spinner_dropdown_item);
//                            aat_reason_not_presented.setAdapter(adapterYesNo);
                        } else {
//                            til_reason_not_presented.setVisibility(View.VISIBLE);
                            til_release_date.setVisibility(View.VISIBLE);
                            til_release_place.setVisibility(View.VISIBLE);
                            til_release_by.setVisibility(View.VISIBLE);
                            til_reason_unclaimed.setVisibility(View.GONE);
                            til_card_physically_presented.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_distribution_status1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {

                        if (aat_distribution_status1.getText().toString().matches("Unclaimed")||aat_distribution_status1.getText().toString().matches("")) {
                            edt_release_date1.setText("");
                            edt_release_by1.setText("");
                            edt_release_place1.setText("");
                            aat_card_physically_presented1.setText("");
                            aat_reason_not_presented1.setText("");//start
                            aat_card_replacement_request1.setText("");//start
                            edt_card_replacement_request_submitted_details1.setText("");//start
                            edt_pawning_remarks1.setText("");
                            aat_card_pin_is_attached1.setText("");
                            edt_others_reason_not_presented1.setText("");

                            til_reason_not_presented1.setVisibility(View.GONE);//start
                            til_card_replacement_request1.setVisibility(View.GONE);//start
                            til_card_replacement_request_submitted_details1.setVisibility(View.GONE);//start
                            til_pawning_remarks1.setVisibility(View.GONE);//start
                            til_release_date1.setVisibility(View.GONE);
                            til_release_place1.setVisibility(View.GONE);
                            til_release_by1.setVisibility(View.GONE);
                            til_card_physically_presented1.setVisibility(View.GONE);
                            til_card_pin_is_attached1.setVisibility(View.GONE);
                            til_others_reason_not_presented1.setVisibility(View.GONE);
                            aat_reason_unclaimed1.setText("");//newother
                            til_reason_unclaimed1.setVisibility(View.VISIBLE); //newother
                        } else {

                            til_release_date1.setVisibility(View.VISIBLE);
                            til_release_place1.setVisibility(View.VISIBLE);
                            til_release_by1.setVisibility(View.VISIBLE);
                            til_card_physically_presented1.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed1.setText("");//newother
                            til_reason_unclaimed1.setVisibility(View.GONE); //newother
                            til_reason_not_presented1.setVisibility(View.GONE); //start
                        }
                    }
                });

                aat_distribution_status2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_distribution_status2.getText().toString().matches("Unclaimed")||aat_distribution_status2.getText().toString().matches("")) {
                            edt_release_date2.setText("");
                            edt_release_by2.setText("");
                            edt_release_place2.setText("");
                            aat_card_physically_presented2.setText("");
                            aat_reason_not_presented2.setText("");
                            aat_card_replacement_request2.setText("");
                            edt_card_replacement_request_submitted_details2.setText("");
                            edt_pawning_remarks2.setText("");
                            aat_card_pin_is_attached2.setText("");
                            edt_others_reason_not_presented2.setText("");

                            til_reason_not_presented2.setVisibility(View.GONE);
                            til_card_replacement_request2.setVisibility(View.GONE);
                            til_card_replacement_request_submitted_details2.setVisibility(View.GONE);
                            til_pawning_remarks2.setVisibility(View.GONE);
                            til_release_date2.setVisibility(View.GONE);
                            til_release_place2.setVisibility(View.GONE);
                            til_release_by2.setVisibility(View.GONE);
                            til_card_physically_presented2.setVisibility(View.GONE);
                            til_card_pin_is_attached2.setVisibility(View.GONE);
                            til_others_reason_not_presented2.setVisibility(View.GONE);
                            aat_reason_unclaimed2.setText("");
                            til_reason_unclaimed2.setVisibility(View.VISIBLE);
//                            edt_release_date2.setText("");
//                            edt_release_by2.setText("");
//                            edt_release_place2.setText("");
//                            til_release_date2.setVisibility(View.GONE);
//                            til_release_place2.setVisibility(View.GONE);
//                            til_release_by2.setVisibility(View.GONE);
//                            til_card_physically_presented2.setVisibility(View.GONE);
//                            aat_reason_unclaimed2.setText("");//newother
//                            til_reason_unclaimed2.setVisibility(View.VISIBLE); //newother
                        } else {

                            til_release_date2.setVisibility(View.VISIBLE);
                            til_release_place2.setVisibility(View.VISIBLE);
                            til_release_by2.setVisibility(View.VISIBLE);
                            til_card_physically_presented2.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed2.setText("");//newother
                            til_reason_unclaimed2.setVisibility(View.GONE); //newother
                            til_reason_not_presented2.setVisibility(View.GONE); //start

//                            til_release_date2.setVisibility(View.VISIBLE);
//                            til_release_place2.setVisibility(View.VISIBLE);
//                            til_release_by2.setVisibility(View.VISIBLE);
//                            til_card_physically_presented2.setVisibility(View.VISIBLE);
//                            aat_reason_unclaimed2.setText("");//newother
//                            til_reason_unclaimed2.setVisibility(View.GONE); //newother
                        }
                    }
                });

                aat_distribution_status3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_distribution_status3.getText().toString().matches("Unclaimed")||aat_distribution_status3.getText().toString().matches("")) {

                            edt_release_date3.setText("");
                            edt_release_by3.setText("");
                            edt_release_place3.setText("");
                            aat_card_physically_presented3.setText("");
                            aat_reason_not_presented3.setText("");
                            aat_card_replacement_request3.setText("");
                            edt_card_replacement_request_submitted_details3.setText("");
                            edt_pawning_remarks3.setText("");
                            aat_card_pin_is_attached3.setText("");
                            edt_others_reason_not_presented3.setText("");
                            til_reason_not_presented3.setVisibility(View.GONE);
                            til_card_replacement_request3.setVisibility(View.GONE);
                            til_card_replacement_request_submitted_details3.setVisibility(View.GONE);
                            til_pawning_remarks3.setVisibility(View.GONE);
                            til_release_date3.setVisibility(View.GONE);
                            til_release_place3.setVisibility(View.GONE);
                            til_release_by3.setVisibility(View.GONE);
                            til_card_physically_presented3.setVisibility(View.GONE);
                            til_card_pin_is_attached3.setVisibility(View.GONE);
                            til_others_reason_not_presented3.setVisibility(View.GONE);
                            aat_reason_unclaimed3.setText("");
                            til_reason_unclaimed3.setVisibility(View.VISIBLE);


//                            edt_release_date3.setText("");
//                            edt_release_by3.setText("");
//                            edt_release_place3.setText("");
//                            til_release_date3.setVisibility(View.GONE);
//                            til_release_place3.setVisibility(View.GONE);
//                            til_release_by3.setVisibility(View.GONE);
//                            til_card_physically_presented3.setVisibility(View.GONE);
//                            aat_reason_unclaimed3.setText("");//newother
//                            til_reason_unclaimed3.setVisibility(View.VISIBLE); //newother
                        } else {
                            til_release_date3.setVisibility(View.VISIBLE);
                            til_release_place3.setVisibility(View.VISIBLE);
                            til_release_by3.setVisibility(View.VISIBLE);
                            til_card_physically_presented3.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed3.setText("");//newother
                            til_reason_unclaimed3.setVisibility(View.GONE); //newother
                            til_reason_not_presented3.setVisibility(View.GONE); //start


//                            til_release_date3.setVisibility(View.VISIBLE);
//                            til_release_place3.setVisibility(View.VISIBLE);
//                            til_release_by3.setVisibility(View.VISIBLE);
//                            til_card_physically_presented3.setVisibility(View.VISIBLE);
//                            aat_reason_unclaimed3.setText("");//newother
//                            til_reason_unclaimed3.setVisibility(View.GONE); //newother
                        }
                    }
                });

                aat_distribution_status4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_distribution_status4.getText().toString().matches("Unclaimed")||aat_distribution_status4.getText().toString().matches("")) {

                            edt_release_date4.setText("");
                            edt_release_by4.setText("");
                            edt_release_place4.setText("");
                            aat_card_physically_presented4.setText("");
                            aat_reason_not_presented4.setText("");
                            aat_card_replacement_request4.setText("");
                            edt_card_replacement_request_submitted_details4.setText("");
                            edt_pawning_remarks4.setText("");
                            aat_card_pin_is_attached4.setText("");
                            edt_others_reason_not_presented4.setText("");

                            til_reason_not_presented4.setVisibility(View.GONE);
                            til_card_replacement_request4.setVisibility(View.GONE);
                            til_card_replacement_request_submitted_details4.setVisibility(View.GONE);
                            til_pawning_remarks4.setVisibility(View.GONE);
                            til_release_date4.setVisibility(View.GONE);
                            til_release_place4.setVisibility(View.GONE);
                            til_release_by4.setVisibility(View.GONE);
                            til_card_physically_presented4.setVisibility(View.GONE);
                            til_card_pin_is_attached4.setVisibility(View.GONE);
                            til_others_reason_not_presented4.setVisibility(View.GONE);
                            aat_reason_unclaimed4.setText("");
                            til_reason_unclaimed4.setVisibility(View.VISIBLE);


//                            edt_release_date4.setText("");
//                            edt_release_by4.setText("");
//                            edt_release_place4.setText("");
//                            til_release_date4.setVisibility(View.GONE);
//                            til_release_place4.setVisibility(View.GONE);
//                            til_release_by4.setVisibility(View.GONE);
//                            til_card_physically_presented4.setVisibility(View.GONE);
//                            aat_reason_unclaimed4.setText("");//newother
//                            til_reason_unclaimed4.setVisibility(View.VISIBLE); //newother
                        } else {
                            til_release_date4.setVisibility(View.VISIBLE);
                            til_release_place4.setVisibility(View.VISIBLE);
                            til_release_by4.setVisibility(View.VISIBLE);
                            til_card_physically_presented4.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed4.setText("");//newother
                            til_reason_unclaimed4.setVisibility(View.GONE); //newother
                            til_reason_not_presented4.setVisibility(View.GONE); //start


/*                            til_release_date4.setVisibility(View.VISIBLE);
                            til_release_place4.setVisibility(View.VISIBLE);
                            til_release_by4.setVisibility(View.VISIBLE);
                            til_card_physically_presented4.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed4.setText("");//newother
                            til_reason_unclaimed4.setVisibility(View.GONE); //newother*/
                        }
                    }
                });

                aat_distribution_status5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (aat_distribution_status5.getText().toString().matches("Unclaimed")||aat_distribution_status5.getText().toString().matches("")) {

                            edt_release_date5.setText("");
                            edt_release_by5.setText("");
                            edt_release_place5.setText("");
                            aat_card_physically_presented5.setText("");
                            aat_reason_not_presented5.setText("");
                            aat_card_replacement_request5.setText("");
                            edt_card_replacement_request_submitted_details5.setText("");
                            edt_pawning_remarks5.setText("");
                            aat_card_pin_is_attached5.setText("");
                            edt_others_reason_not_presented5.setText("");

                            til_reason_not_presented5.setVisibility(View.GONE);
                            til_card_replacement_request5.setVisibility(View.GONE);
                            til_card_replacement_request_submitted_details5.setVisibility(View.GONE);
                            til_pawning_remarks5.setVisibility(View.GONE);
                            til_release_date5.setVisibility(View.GONE);
                            til_release_place5.setVisibility(View.GONE);
                            til_release_by5.setVisibility(View.GONE);
                            til_card_physically_presented5.setVisibility(View.GONE);
                            til_card_pin_is_attached5.setVisibility(View.GONE);
                            til_others_reason_not_presented5.setVisibility(View.GONE);
                            aat_reason_unclaimed5.setText("");
                            til_reason_unclaimed5.setVisibility(View.VISIBLE);


//                            edt_release_date5.setText("");
//                            edt_release_by5.setText("");
//                            edt_release_place5.setText("");
//                            aat_reason_unclaimed5.setText("");//newother
//                            til_reason_unclaimed5.setVisibility(View.VISIBLE); //newother
//
//                            til_release_date5.setVisibility(View.GONE);
//                            til_release_place5.setVisibility(View.GONE);
//                            til_release_by5.setVisibility(View.GONE);
//                            til_card_physically_presented5.setVisibility(View.GONE);

                        } else {
                            til_release_date5.setVisibility(View.VISIBLE);
                            til_release_place5.setVisibility(View.VISIBLE);
                            til_release_by5.setVisibility(View.VISIBLE);
                            til_card_physically_presented5.setVisibility(View.VISIBLE);
                            aat_reason_unclaimed5.setText("");//newother
                            til_reason_unclaimed5.setVisibility(View.GONE); //newother
                            til_reason_not_presented5.setVisibility(View.GONE); //start

//                            til_release_date5.setVisibility(View.VISIBLE);
//                            til_release_place5.setVisibility(View.VISIBLE);
//                            til_release_by5.setVisibility(View.VISIBLE);
//                            til_card_physically_presented5.setVisibility(View.VISIBLE);
//                            aat_reason_unclaimed5.setText("");//newother
//                            til_reason_unclaimed5.setVisibility(View.GONE); //newother
                        }
                    }
                });

                aat_card_physically_presented.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_reason_not_presented.setVisibility(View.GONE);
                        mcvPawning.setVisibility(View.GONE);
                        til_reason_unclaimed.setVisibility(View.GONE);
                        til_card_replacement_request.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details.setVisibility(View.GONE);
                        til_card_pin_is_attached.setVisibility(View.GONE);

                        til_others_reason_not_presented.setVisibility(View.GONE);//new
                        edt_others_reason_not_presented.setText("");//new

                        aat_card_pin_is_attached.setText(null, false);
                        aat_reason_not_presented.setText(null, false);
                        edt_others_reason_not_presented.setText(null);
                        aat_reason_unclaimed.setText(null, false);
                        edt_others_reason_unclaimed.setText(null);
                        edt_card_replacement_request_submitted_details.setText(null);

                        edt_lender_name.setText(null);
                        edt_date_pawned.setText(null);
                        edt_loan_amount.setText(null);
                        edt_lender_address.setText(null);
                        edt_date_retrieved.setText(null);
                        edt_interest.setText(null);
                        aat_status.setText(null, false);
                        edt_reason.setText(null);
                        aat_offense_history.setText(null, false);
                        edt_offense_date.setText(null);
                        edt_remarks.setText(null);
                        edt_staff_intervention.setText(null);
                        edt_other_details.setText(null);

                        if (aat_card_physically_presented.getText().toString().matches("Yes")) {
                            til_card_pin_is_attached.setVisibility(View.VISIBLE);
                            if (aat_distribution_status.getText().toString().matches("Unclaimed")){
                                til_distribution_status.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                        }
                        else {
                            aat_card_pin_is_attached.setText("");
                            til_card_pin_is_attached.setVisibility(View.GONE);
                            til_distribution_status.setError(null);
                            til_reason_not_presented.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_physically_presented1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_pawning_remarks1.setVisibility(View.GONE);
                        til_reason_unclaimed1.setVisibility(View.GONE);
                        til_card_replacement_request1.setVisibility(View.GONE);
                        til_reason_not_presented1.setVisibility(View.GONE);
                        til_card_pin_is_attached1.setVisibility(View.GONE);
                        til_card_number_inputted1.setVisibility(View.GONE); //newother
                        til_card_number_series1.setVisibility(View.GONE); //newother


                        aat_card_pin_is_attached1.setText(null, false);
                        aat_reason_not_presented1.setText(null, false);
                        edt_others_reason_not_presented1.setText(null);
                        aat_reason_unclaimed1.setText(null, false);
                        edt_others_reason_unclaimed1.setText(null);
                        edt_card_replacement_request_submitted_details1.setText(null);
                        edt_pawning_remarks1.setText(null);

                        if (aat_card_physically_presented1.getText().toString().matches("Yes")) {
                            til_card_replacement_request1.setVisibility(View.GONE);
                            til_pawning_remarks1.setVisibility(View.GONE);
                            til_others_reason_not_presented1.setVisibility(View.GONE);

                            til_card_pin_is_attached1.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField1.setVisibility(View.VISIBLE);
                            til_card_number_inputted1.setVisibility(View.VISIBLE); //newother
                            til_card_number_series1.setVisibility(View.VISIBLE); //newother
                            if (aat_distribution_status1.getText().toString().matches("Unclaimed")){
                                til_distribution_status1.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                            else {
                                til_distribution_status1.setError(null);
                            }

                        } else {
                            til_distribution_status1.setError(null);
                            til_reason_not_presented1.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField1.setVisibility(View.GONE);
                            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_1=NULL WHERE id=1");
                            ivOtherScannedImage1.setImageResource(R.drawable.ic_image);
                        }
                    }
                });

                aat_card_physically_presented2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_pawning_remarks2.setVisibility(View.GONE);
                        til_reason_unclaimed2.setVisibility(View.GONE);
                        til_card_replacement_request2.setVisibility(View.GONE);
                        til_reason_not_presented2.setVisibility(View.GONE);
                        til_card_pin_is_attached2.setVisibility(View.GONE);
                        til_card_number_inputted2.setVisibility(View.GONE); //newother
                        til_card_number_series2.setVisibility(View.GONE); //newother

                        aat_card_pin_is_attached2.setText(null, false);
                        aat_reason_not_presented2.setText(null, false);
                        edt_others_reason_not_presented2.setText(null);
                        aat_reason_unclaimed2.setText(null, false);
                        edt_others_reason_unclaimed2.setText(null);
                        edt_card_replacement_request_submitted_details2.setText(null);
                        edt_pawning_remarks2.setText(null);

                        if (aat_card_physically_presented2.getText().toString().matches("Yes")) {
                            til_card_replacement_request2.setVisibility(View.GONE);
                            til_pawning_remarks2.setVisibility(View.GONE);
                            til_others_reason_not_presented2.setVisibility(View.GONE);

                            til_card_pin_is_attached2.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField2.setVisibility(View.VISIBLE);
                            til_card_number_inputted2.setVisibility(View.VISIBLE); //newother
                            til_card_number_series2.setVisibility(View.VISIBLE); //newother
                            if (aat_distribution_status2.getText().toString().matches("Unclaimed")){
                                til_distribution_status2.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                            else {
                                til_distribution_status2.setError(null);
                            }

                        } else {
                            til_distribution_status2.setError(null);
                            til_reason_not_presented2.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField2.setVisibility(View.GONE);
                            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_2=NULL WHERE id=1");
                            ivOtherScannedImage2.setImageResource(R.drawable.ic_image);
                        }

                    }
                });

                aat_card_physically_presented3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_pawning_remarks3.setVisibility(View.GONE);
                        til_reason_unclaimed3.setVisibility(View.GONE);
                        til_card_replacement_request3.setVisibility(View.GONE);
                        til_reason_not_presented3.setVisibility(View.GONE);
                        til_card_pin_is_attached3.setVisibility(View.GONE);
                        til_card_number_inputted3.setVisibility(View.GONE); //newother
                        til_card_number_series3.setVisibility(View.GONE); //newother

                        aat_card_pin_is_attached3.setText(null, false);
                        aat_reason_not_presented3.setText(null, false);
                        edt_others_reason_not_presented3.setText(null);
                        aat_reason_unclaimed3.setText(null, false);
                        edt_others_reason_unclaimed3.setText(null);
                        edt_card_replacement_request_submitted_details3.setText(null);
                        edt_pawning_remarks3.setText(null);

                        if (aat_card_physically_presented3.getText().toString().matches("Yes")) {
                            til_card_replacement_request3.setVisibility(View.GONE);
                            til_pawning_remarks3.setVisibility(View.GONE);
                            til_others_reason_not_presented3.setVisibility(View.GONE);

                            til_card_pin_is_attached3.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField3.setVisibility(View.VISIBLE);
                            til_card_number_inputted3.setVisibility(View.VISIBLE); //newother
                            til_card_number_series3.setVisibility(View.VISIBLE); //newother
                            if (aat_distribution_status3.getText().toString().matches("Unclaimed")){
                                til_distribution_status3.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                            else {
                                til_distribution_status3.setError(null);
                            }

                        } else {
                            til_distribution_status3.setError(null);
                            til_reason_not_presented3.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField3.setVisibility(View.GONE);
                            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_3=NULL WHERE id=1");
                            ivOtherScannedImage3.setImageResource(R.drawable.ic_image);
                        }

                    }
                });

                aat_card_physically_presented4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_pawning_remarks4.setVisibility(View.GONE);
                        til_reason_unclaimed4.setVisibility(View.GONE);
                        til_card_replacement_request4.setVisibility(View.GONE);
                        til_reason_not_presented4.setVisibility(View.GONE);
                        til_card_pin_is_attached4.setVisibility(View.GONE);
                        til_card_number_inputted4.setVisibility(View.GONE); //newother
                        til_card_number_series4.setVisibility(View.GONE); //newother


                        aat_card_pin_is_attached4.setText(null, false);
                        aat_reason_not_presented4.setText(null, false);
                        edt_others_reason_not_presented4.setText(null);
                        aat_reason_unclaimed4.setText(null, false);
                        edt_others_reason_unclaimed4.setText(null);
                        edt_card_replacement_request_submitted_details4.setText(null);
                        edt_pawning_remarks4.setText(null);

                        if (aat_card_physically_presented4.getText().toString().matches("Yes")) {
                            til_card_replacement_request4.setVisibility(View.GONE);
                            til_pawning_remarks4.setVisibility(View.GONE);
                            til_others_reason_not_presented4.setVisibility(View.GONE);

                            til_card_pin_is_attached4.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField4.setVisibility(View.VISIBLE);
                            til_card_number_inputted4.setVisibility(View.VISIBLE); //newother
                            til_card_number_series4.setVisibility(View.VISIBLE); //newother
                            if (aat_distribution_status4.getText().toString().matches("Unclaimed")){
                                til_distribution_status4.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                            else {
                                til_distribution_status4.setError(null);
                            }

                        } else {
                            til_distribution_status4.setError(null);
                            til_reason_not_presented4.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField4.setVisibility(View.GONE);
                            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_4=NULL WHERE id=1");
                            ivOtherScannedImage4.setImageResource(R.drawable.ic_image);
                        }

                    }
                });

                aat_card_physically_presented5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        til_pawning_remarks5.setVisibility(View.GONE);
                        til_reason_unclaimed5.setVisibility(View.GONE);
                        til_card_replacement_request5.setVisibility(View.GONE);
                        til_reason_not_presented5.setVisibility(View.GONE);
                        til_card_pin_is_attached5.setVisibility(View.GONE);
                        til_card_number_inputted5.setVisibility(View.GONE); //newother
                        til_card_number_series5.setVisibility(View.GONE); //newother

                        aat_card_pin_is_attached5.setText(null, false);
                        aat_reason_not_presented5.setText(null, false);
                        edt_others_reason_not_presented5.setText(null);
                        aat_reason_unclaimed5.setText(null, false);
                        edt_others_reason_unclaimed5.setText(null);
                        edt_card_replacement_request_submitted_details5.setText(null);
                        edt_pawning_remarks5.setText(null);

                        if (aat_card_physically_presented5.getText().toString().matches("Yes")) {
                            til_card_replacement_request5.setVisibility(View.GONE);
                            til_pawning_remarks5.setVisibility(View.GONE);
                            til_others_reason_not_presented5.setVisibility(View.GONE);

                            til_card_pin_is_attached5.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField5.setVisibility(View.VISIBLE);
                            til_card_number_inputted5.setVisibility(View.VISIBLE); //newother
                            til_card_number_series5.setVisibility(View.VISIBLE); //newother
                            if (aat_distribution_status5.getText().toString().matches("Unclaimed")){
                                til_distribution_status5.setError("Must be Claimed if physical cash card presented");
                                isValidationError++;
                            }
                            else {
                                til_distribution_status5.setError(null);
                            }

                        } else {
                            til_distribution_status5.setError(null);
                            til_reason_not_presented5.setVisibility(View.VISIBLE);
                            rlOtherCardScanningField5.setVisibility(View.GONE);
                            sqLiteHelper.queryData("UPDATE tmp_blob SET other_card_e_image_5=NULL WHERE id=1");
                            ivOtherScannedImage5.setImageResource(R.drawable.ic_image);
                        }
                    }
                });

                aat_reason_not_presented.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_reason_unclaimed.setVisibility(View.GONE);
                        til_card_replacement_request.setVisibility(View.GONE);
                        mcvPawning.setVisibility(View.GONE);
                        til_others_reason_not_presented.setVisibility(View.GONE);
                        til_others_reason_unclaimed.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details.setVisibility(View.GONE);

                        edt_others_reason_not_presented.setText(null);
                        aat_reason_unclaimed.setText(null, false);
                        edt_others_reason_unclaimed.setText(null);
                        edt_card_replacement_request_submitted_details.setText(null);

                        edt_lender_name.setText(null);
                        edt_date_pawned.setText(null);
                        edt_loan_amount.setText(null);
                        edt_lender_address.setText(null);
                        edt_date_retrieved.setText(null);
                        edt_interest.setText(null);
                        aat_status.setText(null, false);
                        edt_reason.setText(null);
                        aat_offense_history.setText(null, false);
                        edt_offense_date.setText(null);
                        edt_remarks.setText(null);
                        edt_staff_intervention.setText(null);
                        edt_other_details.setText(null);
//                        if (aat_reason_not_presented.getText().toString().matches("Unclaimed")) {
//                            til_reason_unclaimed.setVisibility(View.VISIBLE);
//                        }
                        if (aat_reason_not_presented.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented.getText().toString().matches("Damaged/Defective")) {
                             aat_card_replacement_request.setText("");
                             til_card_replacement_request.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented.getText().toString().matches("Pawned")) {
                            mcvPawning.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented.getText().toString().matches("Others")) {
                            til_others_reason_not_presented.setVisibility(View.VISIBLE);
                        }
                        else {
                            aat_card_replacement_request.setText("");//new
                            til_card_replacement_request.setVisibility(View.GONE);
                            til_others_reason_not_presented.setVisibility(View.GONE);//new
                            edt_others_reason_not_presented.setText("");//new
                        }

                    }
                });

                aat_reason_not_presented1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        til_reason_unclaimed1.setVisibility(View.GONE);
                        til_card_replacement_request1.setVisibility(View.GONE);
                        til_pawning_remarks1.setVisibility(View.GONE);
                        til_others_reason_not_presented1.setVisibility(View.GONE);
                        til_others_reason_unclaimed1.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details1.setVisibility(View.GONE);

                        edt_others_reason_not_presented1.setText(null);
                        aat_reason_unclaimed1.setText(null, false);
                        edt_others_reason_unclaimed1.setText(null);
                        edt_card_replacement_request_submitted_details1.setText(null);
                        edt_pawning_remarks1.setText(null);
                        aat_card_replacement_request1.setText(null);


                        if (aat_reason_not_presented1.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented1.getText().toString().matches("Damaged/Defective")) {
                            til_card_replacement_request1.setVisibility(View.VISIBLE);

                        } else if (aat_reason_not_presented1.getText().toString().matches("Pawned")) {
                            til_pawning_remarks1.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented1.getText().toString().matches("Others")) {
                            til_others_reason_not_presented1.setVisibility(View.VISIBLE);
                        }
                        else{
                            til_card_replacement_request1.setVisibility(View.GONE);
                            til_pawning_remarks1.setVisibility(View.GONE);
                            til_others_reason_not_presented1.setVisibility(View.GONE);
                        }
                    }
                });

                aat_reason_not_presented2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_reason_unclaimed2.setVisibility(View.GONE);
                        til_card_replacement_request2.setVisibility(View.GONE);
                        til_pawning_remarks2.setVisibility(View.GONE);
                        til_others_reason_not_presented2.setVisibility(View.GONE);
                        til_others_reason_unclaimed2.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details2.setVisibility(View.GONE);

                        edt_others_reason_not_presented2.setText(null);
                        aat_reason_unclaimed2.setText(null, false);
                        edt_others_reason_unclaimed2.setText(null);
                        edt_card_replacement_request_submitted_details2.setText(null);
                        edt_pawning_remarks2.setText(null);
                        aat_card_replacement_request2.setText(null);

                        if (aat_reason_not_presented2.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented2.getText().toString().matches("Damaged/Defective")) {
                            til_card_replacement_request2.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented2.getText().toString().matches("Pawned")) {
                            til_pawning_remarks2.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented2.getText().toString().matches("Others")) {
                            til_others_reason_not_presented2.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_not_presented3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_reason_unclaimed3.setVisibility(View.GONE);
                        til_card_replacement_request3.setVisibility(View.GONE);
                        til_pawning_remarks3.setVisibility(View.GONE);
                        til_others_reason_not_presented3.setVisibility(View.GONE);
                        til_others_reason_unclaimed3.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details3.setVisibility(View.GONE);

                        edt_others_reason_not_presented3.setText(null);
                        aat_reason_unclaimed3.setText(null, false);
                        edt_others_reason_unclaimed3.setText(null);
                        edt_card_replacement_request_submitted_details3.setText(null);
                        edt_pawning_remarks3.setText(null);
                        aat_card_replacement_request3.setText(null);

                        if (aat_reason_not_presented3.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented3.getText().toString().matches("Damaged/Defective")) {
                            til_card_replacement_request3.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented3.getText().toString().matches("Pawned")) {
                            til_pawning_remarks3.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented3.getText().toString().matches("Others")) {
                            til_others_reason_not_presented3.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_not_presented4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_reason_unclaimed4.setVisibility(View.GONE);
                        til_card_replacement_request4.setVisibility(View.GONE);
                        til_pawning_remarks4.setVisibility(View.GONE);
                        til_others_reason_not_presented4.setVisibility(View.GONE);
                        til_others_reason_unclaimed4.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details4.setVisibility(View.GONE);

                        edt_others_reason_not_presented4.setText(null);
                        aat_reason_unclaimed4.setText(null, false);
                        edt_others_reason_unclaimed4.setText(null);
                        edt_card_replacement_request_submitted_details4.setText(null);
                        edt_pawning_remarks4.setText(null);
                        aat_card_replacement_request4.setText(null);

                        if (aat_reason_not_presented4.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented4.getText().toString().matches("Damaged/Defective")) {
                            til_card_replacement_request4.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented4.getText().toString().matches("Pawned")) {
                            til_pawning_remarks4.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented4.getText().toString().matches("Others")) {
                            til_others_reason_not_presented4.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_not_presented5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_reason_unclaimed5.setVisibility(View.GONE);
                        til_card_replacement_request5.setVisibility(View.GONE);
                        til_pawning_remarks5.setVisibility(View.GONE);
                        til_others_reason_not_presented5.setVisibility(View.GONE);
                        til_others_reason_unclaimed5.setVisibility(View.GONE);
                        til_card_replacement_request_submitted_details5.setVisibility(View.GONE);

                        edt_others_reason_not_presented5.setText(null);
                        aat_reason_unclaimed5.setText(null, false);
                        edt_others_reason_unclaimed5.setText(null);
                        edt_card_replacement_request_submitted_details5.setText(null);
                        edt_pawning_remarks5.setText(null);
                        aat_card_replacement_request5.setText(null);

                        if (aat_reason_not_presented5.getText().toString().matches("Lost/Stolen") || aat_reason_not_presented5.getText().toString().matches("Damaged/Defective")) {
                            til_card_replacement_request5.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented5.getText().toString().matches("Pawned")) {
                            til_pawning_remarks5.setVisibility(View.VISIBLE);
                        } else if (aat_reason_not_presented5.getText().toString().matches("Others")) {
                            til_others_reason_not_presented5.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed.setVisibility(View.GONE);
                        edt_others_reason_unclaimed.setText(null);
                        if (aat_reason_unclaimed.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed1.setVisibility(View.GONE);
                        edt_others_reason_unclaimed1.setText(null);
                        if (aat_reason_unclaimed1.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed1.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed2.setVisibility(View.GONE);
                        edt_others_reason_unclaimed2.setText(null);
                        if (aat_reason_unclaimed2.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed2.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed3.setVisibility(View.GONE);
                        edt_others_reason_unclaimed3.setText(null);
                        if (aat_reason_unclaimed3.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed3.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed4.setVisibility(View.GONE);
                        edt_others_reason_unclaimed4.setText(null);
                        if (aat_reason_unclaimed4.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed4.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_reason_unclaimed5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_others_reason_unclaimed5.setVisibility(View.GONE);
                        edt_others_reason_unclaimed5.setText(null);
                        if (aat_reason_unclaimed5.getText().toString().matches("Others")) {
                            til_others_reason_unclaimed5.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details.setText(null);
                        if (aat_card_replacement_request.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details1.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details1.setText(null);
                        if (aat_card_replacement_request1.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details1.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details2.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details2.setText(null);
                        if (aat_card_replacement_request2.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details2.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details3.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details3.setText(null);
                        if (aat_card_replacement_request3.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details3.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details4.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details4.setText(null);
                        if (aat_card_replacement_request4.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details4.setVisibility(View.VISIBLE);
                        }
                    }
                });

                aat_card_replacement_request5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_card_replacement_request_submitted_details5.setVisibility(View.GONE);
                        edt_card_replacement_request_submitted_details5.setText(null);
                        if (aat_card_replacement_request5.getText().toString().matches("Yes")) {
                            til_card_replacement_request_submitted_details5.setVisibility(View.VISIBLE);
                        }
                    }
                });

                ScannedImage.setClipToOutline(true);
                imgUri.setClipToOutline(true);
                imgAdditionalId.setClipToOutline(true);
                mGrantee.setClipToOutline(true);
                ivOtherScannedImage1.setClipToOutline(true);
                ivOtherScannedImageUrl1.setClipToOutline(true);
                ivOtherScannedImage2.setClipToOutline(true);
                ivOtherScannedImageUrl2.setClipToOutline(true);
                ivOtherScannedImage3.setClipToOutline(true);
                ivOtherScannedImageUrl3.setClipToOutline(true);
                ivOtherScannedImage4.setClipToOutline(true);
                ivOtherScannedImageUrl4.setClipToOutline(true);
                ivOtherScannedImage5.setClipToOutline(true);
                ivOtherScannedImageUrl5.setClipToOutline(true);

                getImage();

                btn_scanCashCard.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScanImagePos = 0;
                        showImageImportDialog();
                    }
                });

                btnOtherScanned1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanImagePos = 1;
                        showImageImportDialog();
                    }
                });

                btnOtherScanned2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanImagePos = 2;
                        showImageImportDialog();
                    }
                });

                btnOtherScanned3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanImagePos = 3;
                        showImageImportDialog();
                    }
                });

                btnOtherScanned4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanImagePos = 4;
                        showImageImportDialog();
                    }
                });

                btnOtherScanned5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ScanImagePos = 5;
                        showImageImportDialog();
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

                String card_number_prefilled = sh.getString("card_number_prefilled", "");

                String distribution_status_record = sh.getString("distribution_status_record", "");
                String release_date_record = sh.getString("release_date_record", "");

                String distribution_status_record1 = sh.getString("distribution_status_record1", "");
                String release_date_record1 = sh.getString("release_date_record1", "");
                String distribution_status_record2 = sh.getString("distribution_status_record2", "");
                String release_date_record2 = sh.getString("release_date_record2", "");
                String distribution_status_record3 = sh.getString("distribution_status_record3", "");
                String release_date_record3 = sh.getString("release_date_record3", "");
                String distribution_status_record4 = sh.getString("distribution_status_record4", "");
                String release_date_record4 = sh.getString("release_date_record4", "");
                String distribution_status_record5 = sh.getString("distribution_status_record5", "");
                String release_date_record5 = sh.getString("release_date_record5", "");

                String distribution_status = sh.getString("distribution_status", "");
                String release_date = sh.getString("release_date", "");
                String release_by = sh.getString("release_by", "");
                String release_place = sh.getString("release_place", "");
                String card_physically_presented = sh.getString("card_physically_presented", "");
                String card_pin_is_attached = sh.getString("card_pin_is_attached", "");
                String reason_not_presented = sh.getString("reason_not_presented", "");
                String others_reason_not_presented = sh.getString("others_reason_not_presented", "");
                String reason_unclaimed = sh.getString("reason_unclaimed", "");
                String others_reason_unclaimed = sh.getString("others_reason_unclaimed", "");
                String card_replacement_request = sh.getString("card_replacement_request", "");
                String card_replacement_request_submitted_details = sh.getString("card_replacement_request_submitted_details", "");
                String card_number_inputted = sh.getString("card_number_inputted", "");
                String card_number_series = sh.getString("card_number_series", "");
                String id_exists = sh.getString("id_exists", "");
                String lender_name = sh.getString("lender_name", "");
                String date_pawned = sh.getString("date_pawned", "");
                String loan_amount = sh.getString("loan_amount", "");
                String lender_address = sh.getString("lender_address", "");
                String date_retrieved = sh.getString("date_retrieved", "");
                String interest = sh.getString("interest", "");
                String status = sh.getString("status", "");
                String reason = sh.getString("reason", "");
                String offense_history = sh.getString("offense_history", "");
                String offense_date = sh.getString("offense_date", "");
                String remarks = sh.getString("remarks", "");
                String staff_intervention = sh.getString("staff_intervention", "");
                String other_details = sh.getString("other_details", "");
                String distribution_status1 = sh.getString("distribution_status1", "");
                String distribution_status2 = sh.getString("distribution_status2", "");
                String distribution_status3 = sh.getString("distribution_status3", "");
                String distribution_status4 = sh.getString("distribution_status4", "");
                String distribution_status5 = sh.getString("distribution_status5", "");
                String card_number_prefilled1 = sh.getString("card_number_prefilled1", "");
                String card_number_prefilled2 = sh.getString("card_number_prefilled2", "");
                String card_number_prefilled3 = sh.getString("card_number_prefilled3", "");
                String card_number_prefilled4 = sh.getString("card_number_prefilled4", "");
                String card_number_prefilled5 = sh.getString("card_number_prefilled5", "");
                String card_holder_name1 = sh.getString("card_holder_name1", "");
                String card_holder_name2 = sh.getString("card_holder_name2", "");
                String card_holder_name3 = sh.getString("card_holder_name3", "");
                String card_holder_name4 = sh.getString("card_holder_name4", "");
                String card_holder_name5 = sh.getString("card_holder_name5", "");
                String release_date1 = sh.getString("release_date1", "");
                String release_date2 = sh.getString("release_date2", "");
                String release_date3 = sh.getString("release_date3", "");
                String release_date4 = sh.getString("release_date4", "");
                String release_date5 = sh.getString("release_date5", "");
                String release_by1 = sh.getString("release_by1", "");
                String release_by2 = sh.getString("release_by2", "");
                String release_by3 = sh.getString("release_by3", "");
                String release_by4 = sh.getString("release_by4", "");
                String release_by5 = sh.getString("release_by5", "");
                String release_place1 = sh.getString("release_place1", "");
                String release_place2 = sh.getString("release_place2", "");
                String release_place3 = sh.getString("release_place3", "");
                String release_place4 = sh.getString("release_place4", "");
                String release_place5 = sh.getString("release_place5", "");
                String card_physically_presented1 = sh.getString("card_physically_presented1", "");
                String card_physically_presented2 = sh.getString("card_physically_presented2", "");
                String card_physically_presented3 = sh.getString("card_physically_presented3", "");
                String card_physically_presented4 = sh.getString("card_physically_presented4", "");
                String card_physically_presented5 = sh.getString("card_physically_presented5", "");
                String card_pin_is_attached1 = sh.getString("card_pin_is_attached1", "");
                String card_pin_is_attached2 = sh.getString("card_pin_is_attached2", "");
                String card_pin_is_attached3 = sh.getString("card_pin_is_attached3", "");
                String card_pin_is_attached4 = sh.getString("card_pin_is_attached4", "");
                String card_pin_is_attached5 = sh.getString("card_pin_is_attached5", "");
                String reason_not_presented1 = sh.getString("reason_not_presented1", "");
                String reason_not_presented2 = sh.getString("reason_not_presented2", "");
                String reason_not_presented3 = sh.getString("reason_not_presented3", "");
                String reason_not_presented4 = sh.getString("reason_not_presented4", "");
                String reason_not_presented5 = sh.getString("reason_not_presented5", "");
                String others_reason_not_presented1 = sh.getString("others_reason_not_presented1", "");
                String others_reason_not_presented2 = sh.getString("others_reason_not_presented2", "");
                String others_reason_not_presented3 = sh.getString("others_reason_not_presented3", "");
                String others_reason_not_presented4 = sh.getString("others_reason_not_presented4", "");
                String others_reason_not_presented5 = sh.getString("others_reason_not_presented5", "");
                String reason_unclaimed1 = sh.getString("reason_unclaimed1", "");
                String reason_unclaimed2 = sh.getString("reason_unclaimed2", "");
                String reason_unclaimed3 = sh.getString("reason_unclaimed3", "");
                String reason_unclaimed4 = sh.getString("reason_unclaimed4", "");
                String reason_unclaimed5 = sh.getString("reason_unclaimed5", "");
                String others_reason_unclaimed1 = sh.getString("others_reason_unclaimed1", "");
                String others_reason_unclaimed2 = sh.getString("others_reason_unclaimed2", "");
                String others_reason_unclaimed3 = sh.getString("others_reason_unclaimed3", "");
                String others_reason_unclaimed4 = sh.getString("others_reason_unclaimed4", "");
                String others_reason_unclaimed5 = sh.getString("others_reason_unclaimed5", "");
                String card_replacement_request1 = sh.getString("card_replacement_request1", "");
                String card_replacement_request2 = sh.getString("card_replacement_request2", "");
                String card_replacement_request3 = sh.getString("card_replacement_request3", "");
                String card_replacement_request4 = sh.getString("card_replacement_request4", "");
                String card_replacement_request5 = sh.getString("card_replacement_request5", "");
                String card_replacement_request_submitted_details1 = sh.getString("card_replacement_request_submitted_details1", "");
                String card_replacement_request_submitted_details2 = sh.getString("card_replacement_request_submitted_details2", "");
                String card_replacement_request_submitted_details3 = sh.getString("card_replacement_request_submitted_details3", "");
                String card_replacement_request_submitted_details4 = sh.getString("card_replacement_request_submitted_details4", "");
                String card_replacement_request_submitted_details5 = sh.getString("card_replacement_request_submitted_details5", "");
                String card_number_inputted1 = sh.getString("card_number_inputted1", "");
                String card_number_inputted2 = sh.getString("card_number_inputted2", "");
                String card_number_inputted3 = sh.getString("card_number_inputted3", "");
                String card_number_inputted4 = sh.getString("card_number_inputted4", "");
                String card_number_inputted5 = sh.getString("card_number_inputted5", "");
                String card_number_series1 = sh.getString("card_number_series1", "");
                String card_number_series2 = sh.getString("card_number_series2", "");
                String card_number_series3 = sh.getString("card_number_series3", "");
                String card_number_series4 = sh.getString("card_number_series4", "");
                String card_number_series5 = sh.getString("card_number_series5", "");
                String pawning_remarks1 = sh.getString("pawning_remarks1", "");
                String pawning_remarks2 = sh.getString("pawning_remarks2", "");
                String pawning_remarks3 = sh.getString("pawning_remarks3", "");
                String pawning_remarks4 = sh.getString("pawning_remarks4", "");
                String pawning_remarks5 = sh.getString("pawning_remarks5", "");
                String distribution_status_actual = aat_distribution_status.getText().toString();

                edt_card_number_prefilled = findViewById(R.id.edt_card_number_prefilled);

                aat_distribution_status_record.setText(distribution_status_record);
                edt_release_date_record.setText(release_date_record);

                aat_distribution_status_record1.setText(distribution_status_record1);
                edt_release_date_record1.setText(release_date_record1);
                aat_distribution_status_record2.setText(distribution_status_record2);
                edt_release_date_record2.setText(release_date_record2);
                aat_distribution_status_record3.setText(distribution_status_record3);
                edt_release_date_record3.setText(release_date_record3);
                aat_distribution_status_record4.setText(distribution_status_record4);
                edt_release_date_record4.setText(release_date_record4);
                aat_distribution_status_record5.setText(distribution_status_record5);
                edt_release_date_record5.setText(release_date_record5);


                aat_distribution_status_record.setEnabled(false);
                edt_release_date_record.setEnabled(false);
                edt_release_date_record1.setEnabled(false);
                edt_release_date_record2.setEnabled(false);
                edt_release_date_record3.setEnabled(false);
                edt_release_date_record4.setEnabled(false);
                edt_release_date_record5.setEnabled(false);

                aat_distribution_status.setText(distribution_status, false);
                edt_release_date.setText(release_date);
                edt_release_by.setText(release_by);
                edt_release_place.setText(release_place);
                aat_card_physically_presented.setText(card_physically_presented, false);
                aat_card_pin_is_attached.setText(card_pin_is_attached, false);
                aat_reason_not_presented.setText(reason_not_presented, false);
                edt_others_reason_not_presented.setText(others_reason_not_presented);
                aat_reason_unclaimed.setText(reason_unclaimed, false);
                edt_others_reason_unclaimed.setText(others_reason_unclaimed);
                aat_card_replacement_request.setText(card_replacement_request, false);
                edt_card_replacement_request_submitted_details.setText(card_replacement_request_submitted_details);
                edt_card_number_inputted.setText(card_number_inputted);
                edt_card_number_series.setText(card_number_series);
                aat_id_exists.setText(id_exists, false);
                edt_lender_name.setText(lender_name);
                edt_date_pawned.setText(date_pawned);
                edt_loan_amount.setText(loan_amount);
                edt_lender_address.setText(lender_address);
                edt_date_retrieved.setText(date_retrieved);
                edt_interest.setText(interest);
                aat_status.setText(status, false);
                edt_reason.setText(reason);
                aat_offense_history.setText(offense_history, false);
                edt_offense_date.setText(offense_date);
                edt_remarks.setText(remarks);
                edt_staff_intervention.setText(staff_intervention);
                edt_other_details.setText(other_details);
                edt_card_number_prefilled1.setText(card_number_prefilled1);
                edt_card_number_prefilled2.setText(card_number_prefilled2);
                edt_card_number_prefilled3.setText(card_number_prefilled3);
                edt_card_number_prefilled4.setText(card_number_prefilled4);
                edt_card_number_prefilled5.setText(card_number_prefilled5);
                edt_card_holder_name1.setText(card_holder_name1);
                edt_card_holder_name2.setText(card_holder_name2);
                edt_card_holder_name3.setText(card_holder_name3);
                edt_card_holder_name4.setText(card_holder_name4);
                edt_card_holder_name5.setText(card_holder_name5);
                aat_distribution_status1.setText(distribution_status1, false);
                aat_distribution_status2.setText(distribution_status2, false);
                aat_distribution_status3.setText(distribution_status3, false);
                aat_distribution_status4.setText(distribution_status4, false);
                aat_distribution_status5.setText(distribution_status5, false);
                edt_release_date1.setText(release_date1);
                edt_release_date2.setText(release_date2);
                edt_release_date3.setText(release_date3);
                edt_release_date4.setText(release_date4);
                edt_release_date5.setText(release_date5);
                edt_release_by1.setText(release_by1);
                edt_release_by2.setText(release_by2);
                edt_release_by3.setText(release_by3);
                edt_release_by4.setText(release_by4);
                edt_release_by5.setText(release_by5);
                edt_release_place1.setText(release_place1);
                edt_release_place2.setText(release_place2);
                edt_release_place3.setText(release_place3);
                edt_release_place4.setText(release_place4);
                edt_release_place5.setText(release_place5);
                aat_card_physically_presented1.setText(card_physically_presented1, false);
                aat_card_physically_presented2.setText(card_physically_presented2, false);
                aat_card_physically_presented3.setText(card_physically_presented3, false);
                aat_card_physically_presented4.setText(card_physically_presented4, false);
                aat_card_physically_presented5.setText(card_physically_presented5, false);
                aat_card_pin_is_attached1.setText(card_pin_is_attached1, false);
                aat_card_pin_is_attached2.setText(card_pin_is_attached2, false);
                aat_card_pin_is_attached3.setText(card_pin_is_attached3, false);
                aat_card_pin_is_attached4.setText(card_pin_is_attached4, false);
                aat_card_pin_is_attached5.setText(card_pin_is_attached5, false);
                aat_reason_not_presented1.setText(reason_not_presented1, false);
                aat_reason_not_presented2.setText(reason_not_presented2, false);
                aat_reason_not_presented3.setText(reason_not_presented3, false);
                aat_reason_not_presented4.setText(reason_not_presented4, false);
                aat_reason_not_presented5.setText(reason_not_presented5, false);
                edt_others_reason_not_presented1.setText(others_reason_not_presented1);
                edt_others_reason_not_presented2.setText(others_reason_not_presented2);
                edt_others_reason_not_presented3.setText(others_reason_not_presented3);
                edt_others_reason_not_presented4.setText(others_reason_not_presented4);
                edt_others_reason_not_presented5.setText(others_reason_not_presented5);
                aat_reason_unclaimed1.setText(reason_unclaimed1, false);
                aat_reason_unclaimed2.setText(reason_unclaimed2, false);
                aat_reason_unclaimed3.setText(reason_unclaimed3, false);
                aat_reason_unclaimed4.setText(reason_unclaimed4, false);
                aat_reason_unclaimed5.setText(reason_unclaimed5, false);
                edt_others_reason_unclaimed1.setText(others_reason_unclaimed1);
                edt_others_reason_unclaimed2.setText(others_reason_unclaimed2);
                edt_others_reason_unclaimed3.setText(others_reason_unclaimed3);
                edt_others_reason_unclaimed4.setText(others_reason_unclaimed4);
                edt_others_reason_unclaimed5.setText(others_reason_unclaimed5);
                aat_card_replacement_request1.setText(card_replacement_request1, false);
                aat_card_replacement_request2.setText(card_replacement_request2, false);
                aat_card_replacement_request3.setText(card_replacement_request3, false);
                aat_card_replacement_request4.setText(card_replacement_request4, false);
                aat_card_replacement_request5.setText(card_replacement_request5, false);
                edt_card_replacement_request_submitted_details1.setText(card_replacement_request_submitted_details1);
                edt_card_replacement_request_submitted_details2.setText(card_replacement_request_submitted_details2);
                edt_card_replacement_request_submitted_details3.setText(card_replacement_request_submitted_details3);
                edt_card_replacement_request_submitted_details4.setText(card_replacement_request_submitted_details4);
                edt_card_replacement_request_submitted_details5.setText(card_replacement_request_submitted_details5);
                edt_card_number_inputted1.setText(card_number_inputted1);
                edt_card_number_inputted2.setText(card_number_inputted2);
                edt_card_number_inputted3.setText(card_number_inputted3);
                edt_card_number_inputted4.setText(card_number_inputted4);
                edt_card_number_inputted5.setText(card_number_inputted5);
                edt_card_number_series1.setText(card_number_series1);
                edt_card_number_series2.setText(card_number_series2);
                edt_card_number_series3.setText(card_number_series3);
                edt_card_number_series4.setText(card_number_series4);
                edt_card_number_series5.setText(card_number_series5);
                edt_pawning_remarks1.setText(pawning_remarks1);
                edt_pawning_remarks2.setText(pawning_remarks2);
                edt_pawning_remarks3.setText(pawning_remarks3);
                edt_pawning_remarks4.setText(pawning_remarks4);
                edt_pawning_remarks5.setText(pawning_remarks5);
                edt_card_number_prefilled.setText(card_number_prefilled);

                if (distribution_status.matches("Claimed")) {
                    til_release_date.setVisibility(View.VISIBLE);
                    til_release_by.setVisibility(View.VISIBLE);
                    til_release_place.setVisibility(View.VISIBLE);
                    til_card_physically_presented.setVisibility(View.VISIBLE);//new

                }
                else if (distribution_status.matches("Unclaimed")){
                    til_reason_unclaimed.setVisibility(View.VISIBLE);
                }
                else{
                    til_reason_unclaimed.setVisibility(View.GONE);
                    til_release_date.setVisibility(View.GONE);
                    til_release_by.setVisibility(View.GONE);
                    til_release_place.setVisibility(View.GONE);
                    til_card_physically_presented.setVisibility(View.GONE); //new
                }

                if (card_physically_presented.matches("Yes")) {
                    til_card_pin_is_attached.setVisibility(View.VISIBLE);
                } else if (card_physically_presented.matches("No")) {
                    til_reason_not_presented.setVisibility(View.VISIBLE);
                    if (reason_not_presented.matches("Unclaimed")) {
                        til_reason_unclaimed.setVisibility(View.VISIBLE);
                        if (reason_unclaimed.matches("Others")) {
                            til_others_reason_unclaimed.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented.matches("Lost/Stolen") || reason_not_presented.matches("Damaged/Defective")) {
                        til_card_replacement_request.setVisibility(View.VISIBLE);
                        if (card_replacement_request.matches("Yes")) {
                            til_card_replacement_request_submitted_details.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented.matches("Pawned")) {
                        mcvPawning.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented.matches("Others")) {
                        til_others_reason_not_presented.setVisibility(View.VISIBLE);
                    }
                }

                if (id_exists.matches("Yes")) {
                    ll_additional_id_layout.setVisibility(View.VISIBLE);
                }


//                Other Card Availability 1 - 5

                if (distribution_status1.matches("Claimed")) {
                    til_release_date1.setVisibility(View.VISIBLE);
                    til_release_by1.setVisibility(View.VISIBLE);
                    til_release_place1.setVisibility(View.VISIBLE);
                    til_card_physically_presented1.setVisibility(View.VISIBLE);//new
                }
                else if (distribution_status1.matches("Unclaimed")){
                    Log.v(TAG,"Unclaimmmeed");
                    aat_card_physically_presented1.setText("");
                    aat_reason_not_presented1.setText("");
                    til_reason_unclaimed1.setVisibility(View.VISIBLE);
                    til_reason_not_presented1.setVisibility(View.GONE);
                }
                else {
                    til_reason_unclaimed1.setVisibility(View.GONE);
                    til_release_date1.setVisibility(View.GONE);
                    til_release_by1.setVisibility(View.GONE);
                    til_release_place1.setVisibility(View.GONE);
                    til_card_physically_presented1.setVisibility(View.GONE); //new
                }

                if (card_physically_presented1.matches("Yes")) {
                    til_card_pin_is_attached1.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField1.setVisibility(View.VISIBLE);
                    til_card_number_inputted1.setVisibility(View.VISIBLE);
                    til_card_number_series1.setVisibility(View.VISIBLE);
                } else if (card_physically_presented1.matches("No")) {
                    til_reason_not_presented1.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField1.setVisibility(View.GONE);
                    if (reason_not_presented1.matches("Unclaimed")) {
                        til_reason_unclaimed1.setVisibility(View.VISIBLE);
                        if (reason_unclaimed1.matches("Others")) {
                            til_others_reason_unclaimed1.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented1.matches("Lost/Stolen") || reason_not_presented1.matches("Damaged/Defective")) {
                        til_card_replacement_request1.setVisibility(View.VISIBLE);
                        if (card_replacement_request1.matches("Yes")) {
                            til_card_replacement_request_submitted_details1.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented1.matches("Pawned")) {
                        til_pawning_remarks1.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented1.matches("Others")) {
                        til_others_reason_not_presented1.setVisibility(View.VISIBLE);
                    }
                }

                // other card 2

                if (distribution_status2.matches("Claimed")) {
                    til_release_date2.setVisibility(View.VISIBLE);
                    til_release_by2.setVisibility(View.VISIBLE);
                    til_release_place2.setVisibility(View.VISIBLE);
                    til_card_physically_presented2.setVisibility(View.VISIBLE);//new
                }

                else if (distribution_status2.matches("Unclaimed")){
                    Log.v(TAG,"Unclaimmmeed");
                    aat_card_physically_presented2.setText("");
                    aat_reason_not_presented2.setText("");
                    til_reason_unclaimed2.setVisibility(View.VISIBLE);
                    til_reason_not_presented2.setVisibility(View.GONE);
                }
                else {
                    til_reason_unclaimed2.setVisibility(View.GONE);
                    til_release_date2.setVisibility(View.GONE);
                    til_release_by2.setVisibility(View.GONE);
                    til_release_place2.setVisibility(View.GONE);
                    til_card_physically_presented2.setVisibility(View.GONE); //new
                }

                if (card_physically_presented2.matches("Yes")) {
                    til_card_pin_is_attached2.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField2.setVisibility(View.VISIBLE);
                    til_card_number_inputted2.setVisibility(View.VISIBLE);
                    til_card_number_series2.setVisibility(View.VISIBLE);
                } else if (card_physically_presented2.matches("No")) {
                    til_reason_not_presented2.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField2.setVisibility(View.GONE);
                    if (reason_not_presented2.matches("Unclaimed")) {
                        til_reason_unclaimed2.setVisibility(View.VISIBLE);
                        if (reason_unclaimed2.matches("Others")) {
                            til_others_reason_unclaimed2.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented2.matches("Lost/Stolen") || reason_not_presented2.matches("Damaged/Defective")) {
                        til_card_replacement_request2.setVisibility(View.VISIBLE);
                        if (card_replacement_request2.matches("Yes")) {
                            til_card_replacement_request_submitted_details2.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented2.matches("Pawned")) {
                        til_pawning_remarks2.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented2.matches("Others")) {
                        til_others_reason_not_presented2.setVisibility(View.VISIBLE);
                    }
                }

                //other card 3

                if (distribution_status3.matches("Claimed")) {
                    til_release_date3.setVisibility(View.VISIBLE);
                    til_release_by3.setVisibility(View.VISIBLE);
                    til_release_place3.setVisibility(View.VISIBLE);
                    til_card_physically_presented3.setVisibility(View.VISIBLE);//new
                }
                else if (distribution_status3.matches("Unclaimed")){
                    Log.v(TAG,"Unclaimmmeed");
                    aat_card_physically_presented3.setText("");
                    aat_reason_not_presented3.setText("");
                    til_reason_unclaimed3.setVisibility(View.VISIBLE);
                    til_reason_not_presented3.setVisibility(View.GONE);
                }
                else {
                    til_reason_unclaimed3.setVisibility(View.GONE);
                    til_release_date3.setVisibility(View.GONE);
                    til_release_by3.setVisibility(View.GONE);
                    til_release_place3.setVisibility(View.GONE);
                    til_card_physically_presented3.setVisibility(View.GONE); //new
                }

                if (card_physically_presented3.matches("Yes")) {
                    til_card_pin_is_attached3.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField3.setVisibility(View.VISIBLE);
                    til_card_number_inputted3.setVisibility(View.VISIBLE);
                    til_card_number_series3.setVisibility(View.VISIBLE);
                } else if (card_physically_presented3.matches("No")) {
                    til_reason_not_presented3.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField3.setVisibility(View.GONE);
                    if (reason_not_presented3.matches("Unclaimed")) {
                        til_reason_unclaimed3.setVisibility(View.VISIBLE);
                        if (reason_unclaimed3.matches("Others")) {
                            til_others_reason_unclaimed3.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented3.matches("Lost/Stolen") || reason_not_presented3.matches("Damaged/Defective")) {
                        til_card_replacement_request3.setVisibility(View.VISIBLE);
                        if (card_replacement_request3.matches("Yes")) {
                            til_card_replacement_request_submitted_details3.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented3.matches("Pawned")) {
                        til_pawning_remarks3.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented3.matches("Others")) {
                        til_others_reason_not_presented3.setVisibility(View.VISIBLE);
                    }
                }

                //other card 4
                if (distribution_status4.matches("Claimed")) {
                    til_release_date4.setVisibility(View.VISIBLE);
                    til_release_by4.setVisibility(View.VISIBLE);
                    til_release_place4.setVisibility(View.VISIBLE);
                    til_card_physically_presented4.setVisibility(View.VISIBLE);//new
                }
                else if (distribution_status4.matches("Unclaimed")){
                    Log.v(TAG,"Unclaimmmeed");
                    aat_card_physically_presented4.setText("");
                    aat_reason_not_presented4.setText("");
                    til_reason_unclaimed4.setVisibility(View.VISIBLE);
                    til_reason_not_presented4.setVisibility(View.GONE);
                }
                else {
                    til_reason_unclaimed4.setVisibility(View.GONE);
                    til_release_date4.setVisibility(View.GONE);
                    til_release_by4.setVisibility(View.GONE);
                    til_release_place4.setVisibility(View.GONE);
                    til_card_physically_presented4.setVisibility(View.GONE); //new
                }

                if (card_physically_presented4.matches("Yes")) {
                    til_card_pin_is_attached4.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField4.setVisibility(View.VISIBLE);
                    til_card_number_inputted4.setVisibility(View.VISIBLE);
                    til_card_number_series4.setVisibility(View.VISIBLE);
                } else if (card_physically_presented4.matches("No")) {
                    til_reason_not_presented4.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField4.setVisibility(View.GONE);
                    if (reason_not_presented4.matches("Unclaimed")) {
                        til_reason_unclaimed4.setVisibility(View.VISIBLE);
                        if (reason_unclaimed4.matches("Others")) {
                            til_others_reason_unclaimed4.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented4.matches("Lost/Stolen") || reason_not_presented4.matches("Damaged/Defective")) {
                        til_card_replacement_request4.setVisibility(View.VISIBLE);
                        if (card_replacement_request4.matches("Yes")) {
                            til_card_replacement_request_submitted_details4.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented4.matches("Pawned")) {
                        til_pawning_remarks4.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented4.matches("Others")) {
                        til_others_reason_not_presented4.setVisibility(View.VISIBLE);
                    }
                }

                //other card 5
                if (distribution_status5.matches("Claimed")) {
                    til_release_date5.setVisibility(View.VISIBLE);
                    til_release_by5.setVisibility(View.VISIBLE);
                    til_release_place5.setVisibility(View.VISIBLE);
                    til_card_physically_presented5.setVisibility(View.VISIBLE);//new
                }
                else if (distribution_status5.matches("Unclaimed")){
                    Log.v(TAG,"Unclaimmmeed");
                    aat_card_physically_presented5.setText("");
                    aat_reason_not_presented5.setText("");
                    til_reason_unclaimed5.setVisibility(View.VISIBLE);
                    til_reason_not_presented5.setVisibility(View.GONE);
                }
                else {
                    til_reason_unclaimed5.setVisibility(View.GONE);
                    til_release_date5.setVisibility(View.GONE);
                    til_release_by5.setVisibility(View.GONE);
                    til_release_place5.setVisibility(View.GONE);
                    til_card_physically_presented5.setVisibility(View.GONE); //new
                }

                if (card_physically_presented5.matches("Yes")) {
                    til_card_pin_is_attached5.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField5.setVisibility(View.VISIBLE);
                    til_card_number_inputted5.setVisibility(View.VISIBLE);
                    til_card_number_series5.setVisibility(View.VISIBLE);
                } else if (card_physically_presented5.matches("No")) {
                    til_reason_not_presented5.setVisibility(View.VISIBLE);
                    rlOtherCardScanningField5.setVisibility(View.GONE);
                    if (reason_not_presented5.matches("Unclaimed")) {
                        til_reason_unclaimed5.setVisibility(View.VISIBLE);
                        if (reason_unclaimed5.matches("Others")) {
                            til_others_reason_unclaimed5.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented5.matches("Lost/Stolen") || reason_not_presented5.matches("Damaged/Defective")) {
                        til_card_replacement_request5.setVisibility(View.VISIBLE);
                        if (card_replacement_request5.matches("Yes")) {
                            til_card_replacement_request_submitted_details5.setVisibility(View.VISIBLE);
                        }
                    } else if (reason_not_presented5.matches("Pawned")) {
                        til_pawning_remarks5.setVisibility(View.VISIBLE);
                    } else if (reason_not_presented5.matches("Others")) {
                        til_others_reason_not_presented5.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case 2:
                xml_initialization(3);

                til_nma_date_claimed.setVisibility(View.GONE);
                til_nma_reason.setVisibility(View.GONE);
                til_nma_others_reason.setVisibility(View.GONE);

                ArrayAdapter<String> adapterReasonNMA = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ReasonNMA);
                adapterReasonNMA.setDropDownViewResource(simple_spinner_dropdown_item);
                aat_nma_reason.setAdapter(adapterReasonNMA);
                edt_nma_amount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                    @Override
                    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                        float amount = 0;

                        if (!edt_nma_amount.getText().toString().matches("")) {
                            amount = Float.parseFloat(edt_nma_amount.getText().toString());
                        }

                        if (amount >= 100){
                            til_nma_reason.setVisibility(View.VISIBLE);
                        } else {
                            aat_nma_reason.setText("", false);
                            til_nma_reason.setVisibility(View.GONE);
                            til_nma_others_reason.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void afterTextChanged(Editable editable) {}
                });

                aat_nma_reason.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        til_nma_others_reason.setVisibility(View.GONE);
                        if (aat_nma_reason.getText().toString().matches("Others")) {
                            til_nma_others_reason.setVisibility(View.VISIBLE);
                        }
                    }
                });

                String nma_non_emv = sh.getString("nma_non_emv", "");
                String nma_card_name = sh.getString("nma_card_name", "");

                String nma_amount = sh.getString("nma_amount", "0");
                String nma_reason = sh.getString("nma_reason", "");
                String nma_others_reason = sh.getString("nma_others_reason", "");
                String nma_date_claimed = sh.getString("nma_date_claimed", "");
                String nma_remarks = sh.getString("nma_remarks", "");

                edt_non_emv.setText(nma_non_emv);
                edt_card_name.setText(nma_card_name);

                edt_nma_amount.setText(nma_amount);
                aat_nma_reason.setText(nma_reason, false);
                edt_nma_others_reason.setText(nma_others_reason);
                edt_nma_date_claimed.setText(nma_date_claimed);
                edt_nma_remarks.setText(nma_remarks);

                if (!TextUtils.isEmpty(nma_amount)) {
                    if (Float.parseFloat(nma_amount) >= 100) {
                        til_nma_reason.setVisibility(View.VISIBLE);
                        if (nma_reason.matches("Others")) {
                            til_nma_others_reason.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case 3:
                xml_initialization(4);
                String overall_remarks = sh.getString("overall_remarks", "");
                edt_overall_remarks.setText(overall_remarks);
                break;
        }
    }

    public void clear_preferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        //1
        myEdit.putString("hh_id","");
        myEdit.putString("hh_set","");
        myEdit.putString("last_name","");
        myEdit.putString("first_name","");
        myEdit.putString("middle_name","");
        myEdit.putString("ext_name","");
        myEdit.putString("other_ext_name","");
        myEdit.putString("hh_status","");
        myEdit.putString("province","");
        myEdit.putString("municipality","");
        myEdit.putString("barangay","");
        myEdit.putString("sex","");
        myEdit.putString("is_grantee","");
        myEdit.putString("relationship_to_grantee","");
        myEdit.putString("contact_no","");
        myEdit.putString("contact_no_of","");
        myEdit.putString("contact_no_of_others","");
        myEdit.putString("assigned_staff","");
        myEdit.putString("is_minor","");

        //2
        myEdit.putString("card_number_prefilled", "");
        myEdit.putString("distribution_status", "");
        myEdit.putString("distribution_status_record", "");
        myEdit.putString("distribution_status_record1", "");
        myEdit.putString("distribution_status_record2", "");
        myEdit.putString("distribution_status_record3", "");
        myEdit.putString("distribution_status_record4", "");
        myEdit.putString("distribution_status_record5", "");
        myEdit.putString("release_date", "");
        myEdit.putString("release_by", "");
        myEdit.putString("release_place", "");
        myEdit.putString("card_physically_presented", "");
        myEdit.putString("card_pin_is_attached", "");
        myEdit.putString("reason_not_presented", "");
        myEdit.putString("others_reason_not_presented", "");
        myEdit.putString("reason_unclaimed", "");
        myEdit.putString("others_reason_unclaimed", "");
        myEdit.putString("card_replacement_request", "");
        myEdit.putString("card_replacement_request_submitted_details", "");
        myEdit.putString("card_number_inputted", "");
        myEdit.putString("card_number_series", "");
        myEdit.putString("id_exists", "");
        myEdit.putString("representative_name", "");
        myEdit.putString("lender_name", "");
        myEdit.putString("date_pawned", "");
        myEdit.putString("loan_amount", "");
        myEdit.putString("lender_address", "");
        myEdit.putString("date_retrieved", "");
        myEdit.putString("interest", "");
        myEdit.putString("status", "");
        myEdit.putString("reason", "");
        myEdit.putString("offense_history", "");
        myEdit.putString("offense_date", "");
        myEdit.putString("remarks", "");
        myEdit.putString("staff_intervention", "");
        myEdit.putString("other_details", "");
        myEdit.putString("card_holder_name1", "");
        myEdit.putString("card_holder_name2", "");
        myEdit.putString("card_holder_name3", "");
        myEdit.putString("card_holder_name4", "");
        myEdit.putString("card_holder_name5", "");
        myEdit.putString("distribution_status1", "");
        myEdit.putString("distribution_status2", "");
        myEdit.putString("distribution_status3", "");
        myEdit.putString("distribution_status4", "");
        myEdit.putString("distribution_status5", "");
        myEdit.putString("release_date1", "");
        myEdit.putString("release_date2", "");
        myEdit.putString("release_date3", "");
        myEdit.putString("release_date4", "");
        myEdit.putString("release_date5", "");
        myEdit.putString("release_by1", "");
        myEdit.putString("release_by2", "");
        myEdit.putString("release_by3", "");
        myEdit.putString("release_by4", "");
        myEdit.putString("release_by5", "");
        myEdit.putString("release_place1", "");
        myEdit.putString("release_place2", "");
        myEdit.putString("release_place3", "");
        myEdit.putString("release_place4", "");
        myEdit.putString("release_place5", "");
        myEdit.putString("card_physically_presented1", "");
        myEdit.putString("card_physically_presented2", "");
        myEdit.putString("card_physically_presented3", "");
        myEdit.putString("card_physically_presented4", "");
        myEdit.putString("card_physically_presented5", "");
        myEdit.putString("card_pin_is_attached1", "");
        myEdit.putString("card_pin_is_attached2", "");
        myEdit.putString("card_pin_is_attached3", "");
        myEdit.putString("card_pin_is_attached4", "");
        myEdit.putString("card_pin_is_attached5", "");
        myEdit.putString("reason_not_presented1", "");
        myEdit.putString("reason_not_presented2", "");
        myEdit.putString("reason_not_presented3", "");
        myEdit.putString("reason_not_presented4", "");
        myEdit.putString("reason_not_presented5", "");
        myEdit.putString("others_reason_not_presented1", "");
        myEdit.putString("others_reason_not_presented2", "");
        myEdit.putString("others_reason_not_presented3", "");
        myEdit.putString("others_reason_not_presented4", "");
        myEdit.putString("others_reason_not_presented5", "");
        myEdit.putString("reason_unclaimed1", "");
        myEdit.putString("reason_unclaimed2", "");
        myEdit.putString("reason_unclaimed3", "");
        myEdit.putString("reason_unclaimed4", "");
        myEdit.putString("reason_unclaimed5", "");
        myEdit.putString("others_reason_unclaimed1", "");
        myEdit.putString("others_reason_unclaimed2", "");
        myEdit.putString("others_reason_unclaimed3", "");
        myEdit.putString("others_reason_unclaimed4", "");
        myEdit.putString("others_reason_unclaimed5", "");
        myEdit.putString("card_replacement_request1", "");
        myEdit.putString("card_replacement_request2", "");
        myEdit.putString("card_replacement_request3", "");
        myEdit.putString("card_replacement_request4", "");
        myEdit.putString("card_replacement_request5", "");
        myEdit.putString("card_replacement_request_submitted_details1", "");
        myEdit.putString("card_replacement_request_submitted_details2", "");
        myEdit.putString("card_replacement_request_submitted_details3", "");
        myEdit.putString("card_replacement_request_submitted_details4", "");
        myEdit.putString("card_replacement_request_submitted_details5", "");
        myEdit.putString("card_number_inputted1", "");
        myEdit.putString("card_number_inputted2", "");
        myEdit.putString("card_number_inputted3", "");
        myEdit.putString("card_number_inputted4", "");
        myEdit.putString("card_number_inputted5", "");
        myEdit.putString("card_number_series1", "");
        myEdit.putString("card_number_series2", "");
        myEdit.putString("card_number_series3", "");
        myEdit.putString("card_number_series4", "");
        myEdit.putString("card_number_series5", "");
        myEdit.putString("pawning_remarks1", "");
        myEdit.putString("pawning_remarks2", "");
        myEdit.putString("pawning_remarks3", "");
        myEdit.putString("pawning_remarks4", "");
        myEdit.putString("pawning_remarks5", "");

        //3
        myEdit.putString("nma_non_emv", "0");
        myEdit.putString("nma_card_name", "0");
        myEdit.putString("nma_amount", "0");
        myEdit.putString("nma_reason", "");
        myEdit.putString("nma_others_reason", "");
        myEdit.putString("nma_date_claimed", "");
        myEdit.putString("nma_remarks", "");

        //4
        myEdit.putString("overall_remarks", "");
        myEdit.commit();
    }
}