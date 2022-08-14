package com.example.cashgrantsmobile.Scanner;




import static android.R.layout.simple_spinner_dropdown_item;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class ScanCashCard extends AppCompatActivity {

    ImageView mPreviewIv;
    private static final int CAMERA_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String StoragePermission[];
    Button btn_scan;
    public static boolean scanned = true;
    Uri image_uri;
    TextView ScannedCount;


    //onboard

    private TextView tvNext, tvPrev;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;
    TextInputLayout tilHhId, tilFullname, tilClientStatus, tilAddress, tilSex, tilSet, tilContactNo, tilAssigned, tilMinorGrantee;
    TextInputLayout tilCardReleased, tilWhoReleased, tilPlaceReleased, tilCurrentGranteeNumber, tilIsAvailable, tilIsAvailableReason, tilOtherCardNumber1, tilOtherCardHolderName1, tilOtherIsAvailable1, tilOtherIsAvailableReason1, tilOtherCardNumber2, tilOtherCardHolderName2, tilOtherIsAvailable2, tilOtherIsAvailableReason2, tilOtherCardNumber3, tilOtherCardHolderName3, tilOtherIsAvailable3, tilOtherIsAvailableReason3;
    TextInputLayout tilNmaAmount, tilNmaReason, tilDateWithdrawn, tilRemarks;
    TextInputLayout tilLenderName, tilPawningDate, tilLoanedAmount, tilLenderAddress, tilDateRetrieved, tilInterest, tilStatus, tilPawningReason, tilOffenseHistory, tilOffenseHistoryDate, tilPdRemarks, tilIntervention, tilOtherDetails;
    EditText edt_hh, edt_fullname, edt_address, edt_set, edt_contact_no, edt_assigned;
    EditText edt_card_released, edt_who_released, edt_place_released, edt_current_grantee_number, edt_other_card_number_1, edt_other_card_holder_name_1, edt_other_card_number_2, edt_other_card_holder_name_2, edt_other_card_number_3, edt_other_card_holder_name_3;
    EditText edt_nma_amount, edt_nma_reason,  edt_date_withdrawn, edt_remarks;
    EditText edt_lender_name, edt_pawning_date, edt_loaned_amount, edt_lender_address, edt_date_retrieved, edt_interest, edt_pawning_reason, edt_offense_history_date, edt_pd_remarks, edt_intervention, edt_other_details;
    AutoCompleteTextView spinSex, spinAnswer, spinIsAvail, spinIsAvail1, spinIsAvail2, spinIsAvail3, spinIsAvailReason, spinIsAvailReason1, spinIsAvailReason2, spinIsAvailReason3, spinClientStatus, spinStatus, spinOffenseHistory;

    String[] Ans = new String[]{"Yes", "No"};
    String[] Sex = new String[]{"Male", "Female"};
    String[] Reasons = new String[]{"Unclaimed", "Lost/Stolen", "Damaged/Defective", "Pawned", "Not Turned Over", "Others"};
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
    String[] Status = new String[]{"Ongoing (card as collateral)", "Ongoing (card is on-hand)", "Retrieved"};
    String[] Offense = new String[]{"1st Offense", "2nd Offense", "3rd Offense"};

    //end onboard


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_card_scanner_entries);
        mPreviewIv = findViewById(R.id.imageIv);
        mPreviewIv .setVisibility(View.INVISIBLE);

//        btn_scan = (Button) findViewById(R.id.btnScan);
//        ScannedCount = (TextView) findViewById(R.id.ScannedCount);

//        TotalScanned();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("toast");
            Toasty.success(this,""+value, Toasty.LENGTH_SHORT).show();
        }

        cameraPermission = new String[]{Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        StoragePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

//        btn_scan.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showImageImportDialog();
//            }
//        });

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
        viewPager = findViewById(R.id.viewPager);
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

                boolean isValidationError = false;
                String required_field = "This field is required!";

                int current = getItem(1);

                if (current == 1) {
                    String household = "";
                    String fullname = "";
                    String client_status = "";
                    String address = "";
                    String sex = "";
                    String hh_set = "";
                    String contact_no = "";
                    String assigned = "";
                    String minor_grantee = "";

                    isValidationError = false;

                    edt_hh = findViewById(R.id.edtHhId);
                    edt_fullname = findViewById(R.id.edtFullname);
                    spinClientStatus = findViewById(R.id.spinnerClientStatus);
                    edt_address = findViewById(R.id.edtAddress);
                    spinSex = findViewById(R.id.spinnerSex);
                    edt_set = findViewById(R.id.edtSet);
                    edt_contact_no = findViewById(R.id.edtContactNo);
                    edt_assigned = findViewById(R.id.edtAssigned);
                    spinAnswer = findViewById(R.id.spinnerMinorGrantee);

                    household = edt_hh.getText().toString();
                    fullname = edt_fullname.getText().toString();
                    client_status = spinClientStatus.getText().toString();
                    address = edt_address.getText().toString();
                    sex = spinSex.getText().toString();
                    hh_set = edt_set.getText().toString();
                    contact_no = edt_contact_no.getText().toString();
                    assigned = edt_assigned.getText().toString();
                    minor_grantee = spinAnswer.getText().toString();

                    tilHhId = findViewById(R.id.til_hhid);
                    tilFullname = findViewById(R.id.til_fullname);
                    tilClientStatus = findViewById(R.id.til_clientstatus);
                    tilAddress = findViewById(R.id.til_address);
                    tilSex = findViewById(R.id.til_sex);
                    tilSet = findViewById(R.id.til_set);
                    tilContactNo = findViewById(R.id.til_contact_no);


                    if (household.matches("")){
                        tilHhId.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilHhId.setError(null);
                        isValidationError = false;
                    }

                    if (fullname.matches("")){
                        tilFullname.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilFullname.setError(null);
                        isValidationError = false;
                    }

                    if (client_status.matches("")){
                        tilClientStatus.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilClientStatus.setError(null);
                        isValidationError = false;
                    }

                    if (address.matches("")){
                        tilAddress.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilAddress.setError(null);
                        isValidationError = false;
                    }

                    if (sex.matches("")){
                        tilSex.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilSex.setError(null);
                        isValidationError = false;
                    }

                    if (hh_set.matches("")){
                        tilSet.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilSet.setError(null);
                        isValidationError = false;
                    }

                    if (contact_no.matches("")){
                        tilContactNo.setError(required_field);
                        isValidationError = true;
                    } else {
                        tilContactNo.setError(null);
                        isValidationError = false;
                    }

                } else if (current == 2) {
                    String card_released = "";
                    String who_released = "";
                    String place_released = "";
                    String current_grantee_number = "";
                    String is_available = "";
                    String is_available_reason = "";
                    String other_card_number_1 = "";
                    String other_card_holder_name_1 = "";
                    String other_is_available_1 = "";
                    String other_is_available_reason_1 = "";
                    String other_card_number_2 = "";
                    String other_card_holder_name_2 = "";
                    String other_is_available_2 = "";
                    String other_is_available_reason_2 = "";
                    String other_card_number_3 = "";
                    String other_card_holder_name_3 = "";
                    String other_is_available_3 = "";
                    String other_is_available_reason_3 = "";

                    isValidationError = false;

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

                    card_released = edt_card_released.getText().toString();
                    who_released = edt_who_released.getText().toString();
                    place_released = edt_place_released.getText().toString();
                    current_grantee_number = edt_current_grantee_number.getText().toString();
                    is_available = spinIsAvail.getText().toString();
                    is_available_reason = spinIsAvailReason.getText().toString();
                    other_card_number_1 = edt_other_card_number_1.getText().toString();
                    other_card_holder_name_1 = edt_other_card_holder_name_1.getText().toString();
                    other_is_available_1 = spinIsAvail1.getText().toString();
                    other_is_available_reason_1 = spinIsAvailReason1.getText().toString();
                    other_card_number_2 = edt_other_card_number_2.getText().toString();
                    other_card_holder_name_2 = edt_other_card_holder_name_2.getText().toString();
                    other_is_available_2 = spinIsAvail2.getText().toString();
                    other_is_available_reason_2 = spinIsAvailReason2.getText().toString();
                    other_card_number_3 = edt_other_card_number_3.getText().toString();
                    other_card_holder_name_3 = edt_other_card_holder_name_3.getText().toString();
                    other_is_available_3 = spinIsAvail3.getText().toString();
                    other_is_available_reason_3 = spinIsAvailReason3.getText().toString();

                    tilCardReleased = findViewById(R.id.til_cardreleased);
                    tilWhoReleased = findViewById(R.id.til_whoreleased);
                    tilPlaceReleased = findViewById(R.id.til_placereleased);
                    tilCurrentGranteeNumber = findViewById(R.id.til_currentgranteenumber);
                    tilIsAvailable = findViewById(R.id.til_isavailable);

                    if (card_released.matches("")) {
                        tilCardReleased.setError(required_field);
                        isValidationError = true;
                    } else {
                        isValidationError = false;
                    }

                    if (who_released.matches("")) {
                        tilWhoReleased.setError(required_field);
                        isValidationError = true;
                    } else {
                        isValidationError = false;
                    }

                    if (place_released.matches("")) {
                        tilPlaceReleased.setError(required_field);
                        isValidationError = true;
                    } else {
                        isValidationError = false;
                    }

                    if (current_grantee_number.matches("")) {
                        tilCurrentGranteeNumber.setError(required_field);
                        isValidationError = true;
                    } else {
                        isValidationError = false;
                    }

                    if (is_available.matches("")) {
                        tilIsAvailable.setError(required_field);
                        isValidationError = true;
                    } else {
                        isValidationError = false;
                    }

                }

                if (isValidationError) {
                    Toasty.warning(getApplicationContext(), "Please fill-in all required fields!", Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.success(getApplicationContext(), "All fields are valid!", Toast.LENGTH_SHORT).show();
                    if (current < layouts.length) {
                        tvPrev.setVisibility(View.VISIBLE);
                        viewPager.setCurrentItem(current);
                    }
                    else {
                        launchHomeScreen();
                    }
                }
            }
        });
        tvPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = viewPager.getCurrentItem();

                if (current > 0) {
                    current = current - 1;
                    viewPager.setCurrentItem(current);
                }
                if (current == 0){
                    tvPrev.setVisibility(View.INVISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_CAMERA_CODE){

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

                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("signatureAccomplishment", "false");
                    myEdit.putString("identifier", "false");
                    myEdit.putInt("updateMoriah", 0);
                    myEdit.commit();


                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                    String falses = sh.getString("identifier", "");



                    ScannedDetails.scanned = true;

                    Intent i = new Intent(ScanCashCard.this, ScannedDetails.class);
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                        mPreviewIv.setImageBitmap(Bitmap.createScaledBitmap(bm, 187, 250, false));
                        if (sTextFromET.length() >23){
                            String limitString = sTextFromET.substring(0,23);
                            i.putExtra("cashCardNumber",limitString);
                            sqLiteHelper.insertScannedCashCard(limitString,imageViewToByte(mPreviewIv));
                        }
                        else{
                            i.putExtra("cashCardNumber",sTextFromET);
                            sqLiteHelper.insertScannedCashCard(sTextFromET,imageViewToByte(mPreviewIv));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //camera
                    i.putExtra("CashCardImage",image_uri.toString());
                    startActivity(i);
                    finish();
                }
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toasty.error(this,""+error, Toasty.LENGTH_SHORT).show();
            }
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        image.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 120, 120, false));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
    public void TotalScanned(){
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,accomplish_by,informant,id_image,cash_card_scanned_no, card_scanning_status FROM CgList");
        int z = cursor.getCount();
//        ScannedCount.setText("Total Scanned: " + String.valueOf(z));
    }

    //onboard

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
//            edt_hh = findViewById(R.id.edt_hh_no);
//            household = edt_hh.getText().toString();
//
//            if (household.matches("")){
//                Toasty.error(getApplicationContext(),"Required", Toasty.LENGTH_SHORT).show();
//            }

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

            if (position == 0) {
                spinSex = findViewById(R.id.spinnerSex);
                spinAnswer = findViewById(R.id.spinnerMinorGrantee);
                spinClientStatus = findViewById(R.id.spinnerClientStatus);

                ArrayAdapter<String> adapterSex = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Sex);
                ArrayAdapter<String> adapterAns = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterClientStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ClientStatus);

                adapterSex.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterAns.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterClientStatus.setDropDownViewResource(simple_spinner_dropdown_item);

                spinSex.setAdapter(adapterSex);
                spinAnswer.setAdapter(adapterAns);
                spinClientStatus.setAdapter(adapterClientStatus);

            } else if (position == 1) {

                spinIsAvail = findViewById(R.id.spinnerIsAvailable);
                spinIsAvail1 = findViewById(R.id.spinnerOtherIsAvailable1);
                spinIsAvail2 = findViewById(R.id.spinnerOtherIsAvailable2);
                spinIsAvail3 = findViewById(R.id.spinnerOtherIsAvailable3);
                spinIsAvailReason = findViewById(R.id.spinnerIsAvailableReason);
                spinIsAvailReason1 = findViewById(R.id.spinnerOtherIsAvailableReason1);
                spinIsAvailReason2 = findViewById(R.id.spinnerOtherIsAvailableReason2);
                spinIsAvailReason3 = findViewById(R.id.spinnerOtherIsAvailableReason3);
                edt_card_released = findViewById(R.id.edtCardReleased);

                ArrayAdapter<String> adapterIsAvail = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterIsAvail1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterIsAvail2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterIsAvail3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);

                ArrayAdapter<String> adapterIsAvailReason = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);
                ArrayAdapter<String> adapterIsAvailReason1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);
                ArrayAdapter<String> adapterIsAvailReason2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);
                ArrayAdapter<String> adapterIsAvailReason3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Reasons);

                adapterIsAvail.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvail1.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvail2.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvail3.setDropDownViewResource(simple_spinner_dropdown_item);

                adapterIsAvailReason.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvailReason1.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvailReason2.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterIsAvailReason3.setDropDownViewResource(simple_spinner_dropdown_item);

                spinIsAvail.setAdapter(adapterIsAvail);
                spinIsAvail1.setAdapter(adapterIsAvail1);
                spinIsAvail2.setAdapter(adapterIsAvail2);
                spinIsAvail3.setAdapter(adapterIsAvail3);

                spinIsAvailReason.setAdapter(adapterIsAvailReason);
                spinIsAvailReason1.setAdapter(adapterIsAvailReason1);
                spinIsAvailReason2.setAdapter(adapterIsAvailReason2);
                spinIsAvailReason3.setAdapter(adapterIsAvailReason3);

                edt_card_released.setFocusable(false);
                edt_card_released.setClickable(true);

                edt_card_released.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_card_released);
                    }
                });
                spinIsAvailReason.setEnabled(false);
                spinIsAvail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail.getText().toString().matches("Yes")) {
                            spinIsAvailReason.setEnabled(false);
                        } else {
                            spinIsAvailReason.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason1.setEnabled(false);
                spinIsAvail1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail1.getText().toString().matches("Yes")) {
                            spinIsAvailReason1.setEnabled(false);
                        } else {
                            spinIsAvailReason1.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason2.setEnabled(false);
                spinIsAvail2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail2.getText().toString().matches("Yes")) {
                            spinIsAvailReason2.setEnabled(false);
                        } else {
                            spinIsAvailReason2.setEnabled(true);
                        }
                    }
                });

                spinIsAvailReason3.setEnabled(false);
                spinIsAvail3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                        if (spinIsAvail3.getText().toString().matches("Yes")) {
                            spinIsAvailReason3.setEnabled(false);
                        } else {
                            spinIsAvailReason3.setEnabled(true);
                        }
                    }
                });
            } else if (position == 2) {
                edt_date_withdrawn = findViewById(R.id.edtDateWithdrawn);

                edt_date_withdrawn.setFocusable(false);
                edt_date_withdrawn.setClickable(true);

                edt_date_withdrawn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDateDialog(edt_date_withdrawn);
                    }
                });
            } else if (position == 3) {
                edt_pawning_date = findViewById(R.id.edtPawningDate);
                edt_date_retrieved = findViewById(R.id.edtDateRetrieved);
                edt_offense_history_date = findViewById(R.id.edtOffenseHistoryDate);
                spinStatus = findViewById(R.id.spinnerStatus);
                spinOffenseHistory = findViewById(R.id.spinnerOffenseHistory);

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


    //end onboard

}
