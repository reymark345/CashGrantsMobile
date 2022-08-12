package com.example.cashgrantsmobile.Scanner;




import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
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
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private TextView tvNext, tvSkip;
    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private IntroPref introPref;
    private int[] layouts;
    private TextView[] dots;
    private MyViewPagerAdapter viewPagerAdapter;
    TextInputLayout tilHhId, tilIdNo;
    EditText edt_hh, edt_id_no;

    //end onboard


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_card_scanner_entries);
        mPreviewIv = findViewById(R.id.imageIv);
        mPreviewIv .setVisibility(View.INVISIBLE);

//        btn_scan = (Button) findViewById(R.id.btnScan);
//        ScannedCount = (TextView) findViewById(R.id.ScannedCount);




        TotalScanned();
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
        tvSkip = findViewById(R.id.tvSkip);
        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);

        layouts = new int[]{
                R.layout.intro_one,
                R.layout.intro_two,
                R.layout.intro_three,
                R.layout.intro_four

        };

        int current = getItem(+1);
        if (current ==1) {
            tvSkip.setVisibility(View.GONE);
            // move to next screen
//            Toasty.success(getApplicationContext(),"dsaf", Toasty.LENGTH_SHORT).show();
        }

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvSkip.setVisibility(View.VISIBLE);
                edt_hh = findViewById(R.id.edt_hh_no);
                edt_id_no = findViewById(R.id.edtIdno);

                int current = getItem(+1);
                String required_field = "This field is required!";

                if (current == 1) {
                    String household = "";

                    household = edt_hh.getText().toString();
                    if (household.matches("")){
                        tilHhId = findViewById(R.id.til_hhid);
                        tilHhId.setError(required_field);
                    } else if (current < layouts.length) {
                        tilHhId.setError(null);
                        viewPager.setCurrentItem(current);
                    }
                    else {
                        launchHomeScreen();
                    }
                } else if (current == 2) {
                    String id_no = "";

                    id_no = edt_id_no.getText().toString();
                    if (id_no.matches("")){
                        tilIdNo = findViewById(R.id.til_Idno);
                        tilIdNo.setError(required_field);
                    } else if (current < layouts.length) {
                        tilIdNo.setError(null);
                        viewPager.setCurrentItem(current);
                    }
                    else {
                        launchHomeScreen();
                    }
                }
                if (current < layouts.length) {
//                    tilIdNo.setError(null);
                    viewPager.setCurrentItem(current);
                }
                else {
                    launchHomeScreen();
                }

            }
        });
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current ==2){
                    viewPager.setCurrentItem(current-2);
                    tvSkip.setVisibility(View.GONE);
                }
                else if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current-2);
                }
                else {
                    viewPager.setCurrentItem(current-2);
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
                    myEdit.commit();

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
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,id_image,cash_card_scanned_no, card_scanning_status FROM CgList");
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
        return viewPager.getCurrentItem() + 1;
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


    //end onboard

}
