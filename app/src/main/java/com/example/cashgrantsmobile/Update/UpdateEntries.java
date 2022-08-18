package com.example.cashgrantsmobile.Update;




import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;
import com.example.cashgrantsmobile.Signatories.Informant;
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


public class UpdateEntries extends AppCompatActivity {

    ImageView mPreviewIv;
    private static final int CAMERA_REQUEST_CODE = 200;
    public static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String StoragePermission[];
    public static boolean scanned = true;
    public static boolean pressBtn_search = false;
    Uri image_uri;
    TextView ScannedCount;
    String full_name,hh_id,client_status,address,sex,hh_set_group,current_grantee_card_number,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_cardholder_name_3,upload_history_id,created_at,updated_at,validated_at;
    Integer emv_id;
    String full_name_get,hh_id_get,client_status_get,address_get,sex_get,contact_get,hh_set_group_get,assigned_staff_get,minor_grantee_get,current_grantee_release_date_get,current_grantee_release_place_get,current_grantee_release_by_get,current_grantee_is_available_get,current_grantee_reason_get,current_grantee_card_number_get,other_card_number_1_get,other_card_holder_name_1_get,other_card_number_21_get,other_card_holder_name_21_get,other_card_number_31_get,other_card_holder_name_31_get,other_card_is_available1_get,other_card_reason1_get,nma_amount1_get,nma_date_claimed1_get,nma_reason_get,nma_remarks_get,pawn_name_of_lender_get,pawn_date_get,pawn_retrieved_date_get,pawn_status_get,pawn_reason_get,pawn_offense_history_get,pawn_offense_date_get,pawn_remarks_get,pawn_intervention_staff_get,pawn_other_details_get,informant_full_name_get,accomplish_by_full_name_get,cash_card_scanned_no_get,attested_by_full_name_get,other_card_number_series_1_get,other_card_number_series_2_get,other_card_number_series_3_get,emv_database_monitoring_id_get,current_grantee_card_number_series_get,other_card_is_available_2_get,other_card_is_available_3_get,other_card_reason_2_get,other_card_reason_3_get,pawn_loaned_amount_get,pawn_lender_address_get,pawn_interest_get;
    int entries;

    //onboard

    Cursor search;

    private TextView tvNext, tvPrev;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private UpdatePref updatePref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;
    TextInputLayout tilHhId, tilFullname, tilClientStatus, tilAddress, tilSex, tilSet, tilContactNo, tilAssigned, tilMinorGrantee;
    TextInputLayout tilCardReleased, tilWhoReleased, tilPlaceReleased, tilIsAvailable, tilCurrentGranteeNumber, tilIsAvailableReason, tilOtherCardNumber1, tilOtherCardHolderName1, tilOtherIsAvailable1, tilOtherIsAvailableReason1, tilOtherCardNumber2, tilOtherCardHolderName2, tilOtherIsAvailable2, tilOtherIsAvailableReason2, tilOtherCardNumber3, tilOtherCardHolderName3, tilOtherIsAvailable3, tilOtherIsAvailableReason3, tilOtherCardNumberSeries1, tilOtherCardNumberSeries2, tilOtherCardNumberSeries3;
    TextInputLayout tilNmaAmount, tilNmaReason, tilDateWithdrawn, tilRemarks;
    TextInputLayout tilLenderName, tilPawningDate, tilLoanedAmount, tilLenderAddress, tilDateRetrieved, tilInterest, tilStatus, tilPawningReason, tilOffenseHistory, tilOffenseHistoryDate, tilPdRemarks, tilIntervention, tilOtherDetails;
    EditText edt_hh, edt_fullname, edt_address, edt_set, edt_contact_no, edt_assigned;
    EditText edt_card_released, edt_who_released, edt_place_released, edt_current_grantee_number, edt_other_card_number_1, edt_other_card_holder_name_1, edt_other_card_number_2, edt_other_card_holder_name_2, edt_other_card_number_3, edt_other_card_holder_name_3, edt_other_card_number_series_1, edt_other_card_number_series_2, edt_other_card_number_series_3;
    EditText edt_nma_amount, edt_nma_reason,  edt_date_withdrawn, edt_remarks;
    EditText edt_lender_name, edt_pawning_date, edt_loaned_amount, edt_lender_address, edt_date_retrieved, edt_interest, edt_pawning_reason, edt_offense_history_date, edt_pd_remarks, edt_intervention, edt_other_details;
    AutoCompleteTextView spinSex, spinAnswer, spinIsAvail, spinIsAvail1, spinIsAvail2, spinIsAvail3, spinIsAvailReason, spinIsAvailReason1, spinIsAvailReason2, spinIsAvailReason3, spinClientStatus, spinStatus, spinOffenseHistory;

    String[] Ans = new String[]{"Yes", "No"};
    String[] Sex = new String[]{"MALE", "FEMALE"};
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


    public Integer getUserId() {
        Integer userID = 0;
        Cursor lastEmvDatabaseID = MainActivity.sqLiteHelper.getData("SELECT user_id FROM Api LIMIT 1");
        while (lastEmvDatabaseID.moveToNext()) {
            userID = lastEmvDatabaseID.getInt(0);
        }
        return userID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cash_card_scanner_entries);
        mPreviewIv = findViewById(R.id.imageIv);
        mPreviewIv .setVisibility(View.INVISIBLE);
//        ScannedCount = (TextView) findViewById(R.id.ScannedCount);

//        TotalScanned();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("toast");
            Toasty.success(this,""+value, Toasty.LENGTH_SHORT).show();
            extras.clear();
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

        updatePref = new UpdatePref(this);
        if (!updatePref.isFirstTimeLaunch()) {
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
                R.layout.update1_one,
                R.layout.update2_two,
                R.layout.update3_three,
                R.layout.update4_four
        };
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                String hh_id = sh.getString("hh_id", "");
                if (hh_id.length() > 0) {
                    pressBtn_search=true;
                }
                if (pressBtn_search==true){
                    nextValidation();
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

                SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
                String hh_id = sh.getString("hh_id", "");
                if (hh_id.length() > 0) {
                    pressBtn_search=true;
                }

                store_preferences(current+1);

                if (current > 0) {
                    current = current - 1;
                    viewPager.setCurrentItem(current);
                }
                if (current == 0){
                    tvPrev.setVisibility(View.INVISIBLE);
//                    pressBtn_search=true;
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
            Intent intent = new Intent(UpdateEntries.this, MainActivity.class);
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
                    myEdit.putString("granteeBtn", "false");
                    myEdit.putInt("updateMoriah", 0);
                    myEdit.commit();

                    SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

                    int emv_id = sh.getInt("emv_id", 0);
                    String full_name = sh.getString("full_name", "");
                    String household = sh.getString("hh_id", "");
                    String client_status = sh.getString("client_status", "");
                    String address = sh.getString("address", "");
                    String sex = sh.getString("sex", "");
                    String hh_set_group = sh.getString("hh_set_group", "");
                    String contact_no = sh.getString("contact_no", "");
                    String assigned = sh.getString("accomplish_by_name", "");
                    String minor_grantee = sh.getString("minor_grantee", "");

                    String card_released = sh.getString("card_released", "");
                    String who_released = sh.getString("who_released", "");
                    String place_released = sh.getString("place_released", "");
                    String is_available = sh.getString("is_available", "");
                    String is_available_reason = sh.getString("is_available_reason", "");

                    String other_card_number_1 = sh.getString("other_card_number_1", "");
                    String other_card_holder_name_1 = sh.getString("other_card_holder_name_1", "");
                    String other_is_available_1 = sh.getString("other_is_available_1", "");
                    String other_is_available_reason_1 = sh.getString("other_is_available_reason_1", "");


                    String other_card_number_2 = sh.getString("other_card_number_2", "");
                    String other_card_holder_name_2 = sh.getString("other_card_holder_name_2", "");
                    String other_is_available_2 = sh.getString("other_is_available_2", "");

                    String other_is_available_reason_2 = sh.getString("other_is_available_reason_2", "");
                    String other_card_number_3 = sh.getString("other_card_number_3", "");
                    String other_card_holder_name_3 = sh.getString("other_card_holder_name_3", "");
                    String other_is_available_3 = sh.getString("other_is_available_3", "");
                    String other_is_available_reason_3 = sh.getString("other_is_available_reason_3", "");

                    String other_card_number_series_1 = sh.getString("other_card_number_series_1", "");
                    String other_card_number_series_2 = sh.getString("other_card_number_series_2", "");
                    String other_card_number_series_3 = sh.getString("other_card_number_series_3", "");

                    String nma_amount = sh.getString("nma_amount", "");
                    String nma_reason = sh.getString("nma_reason", "");
                    String date_withdrawn = sh.getString("date_withdrawn", "");
                    String remarks = sh.getString("remarks", "");

                    String lender_name = sh.getString("lender_name", "");
                    String pawning_date = sh.getString("pawning_date", "");
                    String date_retrieved = sh.getString("date_retrieved", "");
                    String spin_status = sh.getString("spin_status", "");
                    String pawning_reason = sh.getString("pawning_reason", "");

                    String offense_history = sh.getString("offense_history", "");
                    String offense_history_date = sh.getString("offense_history_date", "");
                    String pd_remarks = sh.getString("pd_remarks", "");
                    String intervention = sh.getString("intervention", "");
                    String other_details = sh.getString("other_details", "");

                    String pawn_loaned_amount = sh.getString("loaned_amount", "");
                    String pawn_lender_address = sh.getString("lender_address", "");
                    String pawn_interest = sh.getString("interest", "");
                    String hh_no_1 = sh.getString("hh_id", "");

                    Intent i = new Intent(UpdateEntries.this, MainActivity.class);

                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
                        mPreviewIv.setImageBitmap(Bitmap.createScaledBitmap(bm, 187, 250, false));
                        if (sTextFromET.length() >23){
                            String limitString = sTextFromET.substring(0,23);
                            i.putExtra("cashCardNumber",limitString);
//                            sqLiteHelper.insertEmvDatabase(full_name,household,client_status,address,sex,hh_set_group,contact_no,assigned,minor_grantee,card_released,who_released,place_released,is_available,is_available_reason,other_card_number_1,other_card_holder_name_1,other_is_available_1,other_is_available_reason_1,other_card_number_2,other_card_holder_name_2,other_is_available_2,other_is_available_reason_2,other_card_number_3,other_card_holder_name_3,other_is_available_3,other_is_available_reason_3,nma_amount,nma_reason,date_withdrawn,remarks, lender_name,pawning_date,date_retrieved,spin_status,pawning_reason,offense_history,offense_history_date,pd_remarks,intervention,other_details,limitString,imageViewToByte(mPreviewIv), pawn_loaned_amount,pawn_lender_address,pawn_interest, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, getUserId(), emv_id);
                        }
                        else{
                            i.putExtra("cashCardNumber",sTextFromET);
//                            sqLiteHelper.insertEmvDatabase(full_name,household,client_status,address,sex,hh_set_group,contact_no,assigned,minor_grantee,card_released,who_released,place_released,is_available,is_available_reason,other_card_number_1,other_card_holder_name_1,other_is_available_1,other_is_available_reason_1,other_card_number_2,other_card_holder_name_2,other_is_available_2,other_is_available_reason_2,other_card_number_3,other_card_holder_name_3,other_is_available_3,other_is_available_reason_3,nma_amount,nma_reason,date_withdrawn,remarks, lender_name,pawning_date,date_retrieved,spin_status,pawning_reason,offense_history,offense_history_date,pd_remarks,intervention,other_details,sTextFromET,imageViewToByte(mPreviewIv), pawn_loaned_amount,pawn_lender_address,pawn_interest, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3, getUserId(), emv_id);
                        }
                        sqLiteHelper.update_emv_monitoring(
                                hh_no_1
                        );

                        clearSharedPref();

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
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                tvNext.setText("SAVE");
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

            Intent in = getIntent();
            entries = in.getIntExtra("UpdateId_entries", 0);
            getData();

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

            //intro_one.xml
            if (position == 0) {
                edt_hh = findViewById(R.id.edtHhId);
                edt_fullname = findViewById(R.id.edtFullname);
                spinClientStatus = findViewById(R.id.spinnerClientStatus);
                edt_address = findViewById(R.id.edtAddress);
                spinSex = findViewById(R.id.spinnerSex);
                edt_set = findViewById(R.id.edtSet);
                edt_contact_no = findViewById(R.id.edtContactNo);
                edt_assigned = findViewById(R.id.edtAssigned);
                spinAnswer = findViewById(R.id.spinnerMinorGrantee);

                ArrayAdapter<String> adapterSex = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Sex);
                ArrayAdapter<String> adapterAns = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, Ans);
                ArrayAdapter<String> adapterClientStatus = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, ClientStatus);

                adapterSex.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterAns.setDropDownViewResource(simple_spinner_dropdown_item);
                adapterClientStatus.setDropDownViewResource(simple_spinner_dropdown_item);

                spinSex.setAdapter(adapterSex);
                spinAnswer.setAdapter(adapterAns);
                spinClientStatus.setAdapter(adapterClientStatus);

                String hh_id = sh.getString("hh_id", "160310001-");
                String full_name = sh.getString("full_name", "");
                String client_status = sh.getString("client_status", "");
                String address = sh.getString("address", "");
                String sex = sh.getString("sex", "");
                String contact_no = sh.getString("contact_no", "");
                String hh_set_group = sh.getString("hh_set_group", "");
                String assigned = sh.getString("assigned", "");
                String minor_grantee = sh.getString("minor_grantee", "");

                edt_hh.setText(hh_id);
                edt_fullname.setText(full_name);
                spinClientStatus.setText(client_status);
                edt_address.setText(address);
                spinSex.setText(sex);
                edt_set.setText(contact_no);
                edt_contact_no.setText(hh_set_group);
                edt_assigned.setText(assigned);
                spinAnswer.setText(minor_grantee);

            } else if (position == 1) {
                //intro_two.xml
                pressBtn_search = false;

                edt_card_released = findViewById(R.id.edtCardReleased);
                edt_who_released = findViewById(R.id.edtWhoReleased);
                edt_place_released = findViewById(R.id.edtPlaceReleased);
                edt_current_grantee_number = findViewById(R.id.edtCurrentGranteeNumber);
                spinIsAvail = findViewById(R.id.spinnerIsAvailable);
                spinIsAvailReason = findViewById(R.id.spinnerIsAvailableReason);
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
                edt_current_grantee_number.setEnabled(false);

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
                            spinIsAvailReason.setText(null);
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
                            spinIsAvailReason1.setText(null);
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
                            spinIsAvailReason2.setText(null);
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
                            spinIsAvailReason3.setText(null);
                            spinIsAvailReason3.setEnabled(false);
                        } else {
                            spinIsAvailReason3.setEnabled(true);
                        }
                    }
                });

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

                edt_card_released.setText(card_released);
                edt_who_released.setText(who_released);
                edt_place_released.setText(place_released);
                edt_current_grantee_number.setText(temp_current_grantee_number);
                spinIsAvail.setText(is_available);
                spinIsAvailReason.setText(is_available_reason);
                edt_other_card_number_1.setText(other_card_number_1);
                edt_other_card_number_series_1.setText(other_card_number_series_1);
                edt_other_card_holder_name_1.setText(other_card_holder_name_1);
                spinIsAvail1.setText(other_is_available_1);
                spinIsAvailReason1.setText(other_is_available_reason_1);
                edt_other_card_number_2.setText(other_card_number_2);
                edt_other_card_number_series_2.setText(other_card_number_series_2);
                edt_other_card_holder_name_2.setText(other_card_holder_name_2);
                spinIsAvail2.setText(other_is_available_2);
                spinIsAvailReason2.setText(other_is_available_reason_2);
                edt_other_card_number_3.setText(other_card_number_3);
                edt_other_card_number_series_3.setText(other_card_number_series_3);
                edt_other_card_holder_name_3.setText(other_card_holder_name_3);
                spinIsAvail3.setText(other_is_available_3);
                spinIsAvailReason3.setText(other_is_available_reason_3);

            } else if (position == 2) {
                //intro_three.xml

                edt_nma_amount = findViewById(R.id.edtNmaAmount);
                edt_nma_reason = findViewById(R.id.edtNmaReason);
                edt_date_withdrawn = findViewById(R.id.edtDateWithdrawn);
                edt_remarks = findViewById(R.id.edtRemarks);

                edt_date_withdrawn.setFocusable(false);
                edt_date_withdrawn.setClickable(true);

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

                edt_lender_name.setText(lender_name);
                edt_pawning_date.setText(pawning_date);
                edt_loaned_amount.setText(loaned_amount);
                edt_lender_address.setText(lender_address);
                edt_date_retrieved.setText(date_retrieved);
                edt_interest.setText(interest);
                spinStatus.setText(spin_status);
                edt_pawning_reason.setText(pawning_reason);
                spinOffenseHistory.setText(offense_history);
                edt_offense_history_date.setText(offense_history_date);
                edt_pd_remarks.setText(pd_remarks);
                edt_intervention.setText(intervention);
                edt_other_details.setText(other_details);
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
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);
        int emv_id = sh.getInt("emv_id", 0);
        String full_name = sh.getString("full_name", "");
        String client_status = sh.getString("client_status", "");
        String address = sh.getString("address", "");
        String sex = sh.getString("sex", "");
        String hh_set_group = sh.getString("hh_set_group", "");
        String contact_no = sh.getString("contact_no", "");
        String assigned = sh.getString("accomplish_by_name", "");
        String minor_grantee = sh.getString("minor_grantee", "");

        String card_released = sh.getString("card_released", "");
        String who_released = sh.getString("who_released", "");
        String place_released = sh.getString("place_released", "");
        String is_available = sh.getString("is_available", "");
        String is_available_reason = sh.getString("is_available_reason", "");

        String other_card_number_1 = sh.getString("other_card_number_1", "");
        String other_card_holder_name_1 = sh.getString("other_card_holder_name_1", "");
        String other_is_available_1 = sh.getString("other_is_available_1", "");
        String other_is_available_reason_1 = sh.getString("other_is_available_reason_1", "");


        String other_card_number_2 = sh.getString("other_card_number_2", "");
        String other_card_holder_name_2 = sh.getString("other_card_holder_name_2", "");
        String other_is_available_2 = sh.getString("other_is_available_2", "");

        String other_is_available_reason_2 = sh.getString("other_is_available_reason_2", "");
        String other_card_number_3 = sh.getString("other_card_number_3", "");
        String other_card_holder_name_3 = sh.getString("other_card_holder_name_3", "");
        String other_is_available_3 = sh.getString("other_is_available_3", "");
        String other_is_available_reason_3 = sh.getString("other_is_available_reason_3", "");

        String other_card_number_series_1 = sh.getString("other_card_number_series_1", "");
        String other_card_number_series_2 = sh.getString("other_card_number_series_2", "");
        String other_card_number_series_3 = sh.getString("other_card_number_series_3", "");

        String nma_amount = sh.getString("nma_amount", "");
        String nma_reason = sh.getString("nma_reason", "");
        String date_withdrawn = sh.getString("date_withdrawn", "");
        String remarks = sh.getString("remarks", "");

        String lender_name = sh.getString("lender_name", "");
        String pawning_date = sh.getString("pawning_date", "");
        String date_retrieved = sh.getString("date_retrieved", "");
        String spin_status = sh.getString("spin_status", "");
        String pawning_reason = sh.getString("pawning_reason", "");

        String offense_history = sh.getString("offense_history", "");
        String offense_history_date = sh.getString("offense_history_date", "");
        String pd_remarks = sh.getString("pd_remarks", "");
        String intervention = sh.getString("intervention", "");
        String other_details = sh.getString("other_details", "");
        String pawn_loaned_amount = sh.getString("loaned_amount", "");
        String pawn_lender_address = sh.getString("lender_address", "");
        String pawn_interest = sh.getString("interest", "");

        sqLiteHelper.updateDetailsEmvDatabase(full_name,client_status,address,sex,hh_set_group,contact_no,assigned,minor_grantee,card_released,who_released,place_released,is_available,is_available_reason,other_card_number_1,other_card_holder_name_1,other_is_available_1,other_is_available_reason_1,other_card_number_2,other_card_holder_name_2,other_is_available_2,other_is_available_reason_2,other_card_number_3,other_card_holder_name_3,other_is_available_3,other_is_available_reason_3,nma_amount,nma_reason,date_withdrawn,remarks, lender_name,pawning_date,date_retrieved,spin_status,pawning_reason,offense_history,offense_history_date,pd_remarks,intervention,other_details, pawn_loaned_amount,pawn_lender_address,pawn_interest, other_card_number_series_1, other_card_number_series_2, other_card_number_series_3,emv_id);
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

        new DatePickerDialog(UpdateEntries.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void nextValidation(){
        Integer isValidationError = 0;
        String required_field = "This field is required!";

        int current = getItem(1);

        edt_lender_name = findViewById(R.id.edtLenderName);

        if (current == 1) {
            edt_hh = findViewById(R.id.edtHhId);
            edt_fullname = findViewById(R.id.edtFullname);
            spinClientStatus = findViewById(R.id.spinnerClientStatus);
            edt_address = findViewById(R.id.edtAddress);
            spinSex = findViewById(R.id.spinnerSex);
            edt_set = findViewById(R.id.edtSet);
            edt_contact_no = findViewById(R.id.edtContactNo);
            edt_assigned = findViewById(R.id.edtAssigned);
            spinAnswer = findViewById(R.id.spinnerMinorGrantee);
            edt_contact_no = findViewById(R.id.edtContactNo);
            edt_assigned = findViewById(R.id.edtAssigned);
            spinAnswer = findViewById(R.id.spinnerMinorGrantee);

            String household = edt_hh.getText().toString();
            String full_name = edt_fullname.getText().toString();
            String client_status = spinClientStatus.getText().toString();
            String address = edt_address.getText().toString();
            String sex = spinSex.getText().toString();
            String hh_set = edt_set.getText().toString();
            String contact_no = edt_contact_no.getText().toString();

            tilHhId = findViewById(R.id.til_hhid);
            tilFullname = findViewById(R.id.til_fullname);
            tilClientStatus = findViewById(R.id.til_clientstatus);
            tilAddress = findViewById(R.id.til_address);
            tilSex = findViewById(R.id.til_sex);
            tilSet = findViewById(R.id.til_set);
            tilContactNo = findViewById(R.id.til_contact_no);

            if (household.matches("")){
                tilHhId.setError(required_field);
                isValidationError++;
            } else {
                tilHhId.setError(null);
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
            } else {
                tilContactNo.setError(null);
            }

            store_preferences(1);

        } else if (current == 2) {
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

            String card_released = edt_card_released.getText().toString();
            String who_released = edt_who_released.getText().toString();
            String place_released = edt_place_released.getText().toString();
            String is_available = spinIsAvail.getText().toString();


            tilCardReleased = findViewById(R.id.til_cardreleased);
            tilWhoReleased = findViewById(R.id.til_whoreleased);
            tilPlaceReleased = findViewById(R.id.til_placereleased);
            tilCurrentGranteeNumber = findViewById(R.id.til_currentgranteenumber);
            tilIsAvailable = findViewById(R.id.til_isavailable);

            if (is_available.matches("")) {
                tilIsAvailable.setError(required_field);
                isValidationError++;
            } else {
                tilIsAvailable.setError(null);
            }

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

            store_preferences(2);

        }
        else if (current == 3) {
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
                edt_contact_no = findViewById(R.id.edtContactNo);
                edt_assigned = findViewById(R.id.edtAssigned);
                spinAnswer = findViewById(R.id.spinnerMinorGrantee);

                String household = edt_hh.getText().toString();
                String full_name = edt_fullname.getText().toString();
                String client_status = spinClientStatus.getText().toString();
                String address = edt_address.getText().toString();
                String sex = spinSex.getText().toString();
                String hh_set = edt_set.getText().toString();
                String contact_no = edt_contact_no.getText().toString();
                String assigned = edt_assigned.getText().toString();
                String minor_grantee = spinAnswer.getText().toString();

                myEdit.putString("hh_id", household);
                myEdit.putString("full_name", full_name);
                myEdit.putString("client_status", client_status);
                myEdit.putString("address", address);
                myEdit.putString("sex", sex);
                myEdit.putString("hh_set_group", hh_set);
                myEdit.putString("contact_no", contact_no);
                myEdit.putString("assigned", assigned);
                myEdit.putString("minor_grantee", minor_grantee);
                myEdit.commit();
                break;
            case 2:
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
            case 4:
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
            default:
                Toasty.warning(getApplicationContext(),"Store preferences out of bounds!", Toasty.LENGTH_SHORT).show();
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
                        myEdit.commit();
                        pressBtn_search = true;
                        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                                new Runnable() {
                                    public void run() {
//                                                    edt_hh.setText(hh_id);
                                        edt_fullname.setText(full_name);
                                        spinClientStatus.setText(client_status);
                                        spinClientStatus.setSelection(client_status.length());
                                        edt_address.setText(address);
                                        edt_set.setText(hh_set_group);
                                        spinSex.setText(sex);
                                        Toasty.success(getApplicationContext(),"Household Found", Toasty.LENGTH_SHORT).show();
                                    }
                                },
                                300);

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
                        if (edt_other_card_number_3!=null || edt_other_card_number_3.length()!=4){
                            edt_other_card_number_3.setText(other_card_number_3);
                        }

                        if (edt_other_card_holder_name_3!=null || other_cardholder_name_3.length()!=4){
                            edt_other_card_holder_name_3.setText(other_cardholder_name_3);
                        }
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
            Toasty.error(getApplicationContext(),"Household not found", Toasty.LENGTH_SHORT).show();
        }
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
        myEdit.commit();
    }


    public void getData(){
        entries = entries+1;

        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,full_name,hh_id,client_status,address,sex,contact,hh_set_group,assigned_staff,minor_grantee,current_grantee_card_release_date,current_grantee_card_release_place,current_grantee_card_release_by,current_grantee_is_available,current_grantee_reason,current_grantee_card_number,other_card_number_1,other_card_holder_name_1,other_card_number_2,other_card_holder_name_2,other_card_number_3,other_card_holder_name_3,other_card_is_available ,other_card_reason,nma_amount,nma_date_claimed,nma_reason,nma_remarks,pawn_name_of_lender,pawn_date,pawn_retrieved_date,pawn_status,pawn_reason,pawn_offense_history,pawn_offense_date,pawn_remarks,pawn_intervention_staff,pawn_other_details,informant_full_name, accomplish_by_full_name,cash_card_scanned_no, attested_by_full_name,other_card_number_series_1,other_card_number_series_2,other_card_number_series_3,emv_database_monitoring_id,current_grantee_card_number_series,other_card_is_available_2,other_card_is_available_3,other_card_reason_2,other_card_reason_3,pawn_loaned_amount,pawn_lender_address,pawn_interest FROM emv_database_monitoring_details WHERE id='"+entries+"'");
        while (cursor.moveToNext()) {

            full_name_get = cursor.getString(1);
            hh_id_get = cursor.getString(2);
            client_status_get = cursor.getString(3);
            address_get = cursor.getString(4);
            sex_get = cursor.getString(5);
            contact_get = cursor.getString(6);
            hh_set_group_get = cursor.getString(7);
            assigned_staff_get = cursor.getString(8);
            minor_grantee_get = cursor.getString(9);
            current_grantee_release_date_get = cursor.getString(10);
            current_grantee_release_place_get = cursor.getString(11);
            current_grantee_release_by_get = cursor.getString(12);
            current_grantee_is_available_get = cursor.getString(13);
            current_grantee_reason_get  = cursor.getString(14);
            current_grantee_card_number_get = cursor.getString(15);
            other_card_number_1_get = cursor.getString(16);
            other_card_holder_name_1_get = cursor.getString(17);
            other_card_number_21_get = cursor.getString(18);
            other_card_holder_name_21_get = cursor.getString(19);
            other_card_number_31_get = cursor.getString(20);
            other_card_holder_name_31_get = cursor.getString(21);
            other_card_is_available1_get = cursor.getString(22);
            other_card_reason1_get = cursor.getString(23);
            nma_amount1_get = cursor.getString(24);
            nma_date_claimed1_get = cursor.getString(25);
            nma_reason_get = cursor.getString(26);
            nma_remarks_get = cursor.getString(27);
            pawn_name_of_lender_get = cursor.getString(28);
            pawn_date_get = cursor.getString(29);
            pawn_retrieved_date_get = cursor.getString(30);
            pawn_status_get = cursor.getString(31);
            pawn_reason_get = cursor.getString(32);
            pawn_offense_history_get = cursor.getString(33);
            pawn_offense_date_get = cursor.getString(34);
            pawn_remarks_get = cursor.getString(35);
            pawn_intervention_staff_get = cursor.getString(36);
            pawn_other_details_get = cursor.getString(37);
            informant_full_name_get = cursor.getString(38);
            accomplish_by_full_name_get = cursor.getString(39);
            cash_card_scanned_no_get = cursor.getString(40);
            attested_by_full_name_get = cursor.getString(41);
            other_card_number_series_1_get = cursor.getString(42);
            other_card_number_series_2_get = cursor.getString(43);
            other_card_number_series_3_get = cursor.getString(44);
            emv_database_monitoring_id_get = cursor.getString(45);
            current_grantee_card_number_series_get = cursor.getString(46);
            other_card_is_available_2_get = cursor.getString(47);
            other_card_is_available_3_get = cursor.getString(48);
            other_card_reason_2_get = cursor.getString(49);
            other_card_reason_3_get = cursor.getString(50);
            pawn_loaned_amount_get = cursor.getString(51);
            pawn_lender_address_get = cursor.getString(52);
            pawn_interest_get = cursor.getString(53);



        }



    }



}