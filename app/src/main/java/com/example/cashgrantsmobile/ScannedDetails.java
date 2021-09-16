package com.example.cashgrantsmobile;



import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScannedDetails extends AppCompatActivity {

    EditText edtCashCard, edtHhnumber, edtSeriesno;
    ImageView mPreview4PsId, mPreviewCashCard;
    Button btnSubmit, btnrescanCashCard, btnrescanBeneId;
    TextInputLayout tilCashCard, tilHousehold, tilSeriesNo;

    private int prevCount = 0;
    private boolean isAtSpaceDelimiter(int currCount) {
        return currCount == 4 || currCount == 9 || currCount == 14 || currCount == 19;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_details);

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

        //layoutMaterial
        tilCashCard = findViewById(R.id.til_cashCard);
        tilHousehold = findViewById(R.id.til_household);
        tilSeriesNo = findViewById(R.id.til_seriesno);

        CashCardOnChange();
        HouseholdOnChanged();
        SeriesOnChanged();
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
                Toast.makeText(getApplicationContext(), "No function yet to be update using Inheritance", Toast.LENGTH_SHORT).show();
//                ScanCashCard action = new ScanCashCard();
//                action.pickCamera();
            }
        });

        // save to database
        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Cardresult = edtCashCard.getText().toString();
                String household = edtHhnumber.getText().toString();
                String seriesno = edtSeriesno.getText().toString();
                String idCard = btnrescanBeneId.getText().toString();
                int length = Cardresult.length();

                if (Cardresult.matches("[0-9 ]+") && !household.matches("") && !seriesno.matches("") && idCard.equals("RE-SCAN") && length==23 ){
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
                }
                else if (!household.matches("") && !seriesno.matches("") ){
                    tilHousehold.setError(null);
                    tilSeriesNo.setError(null);

                }

                if (!seriesno.matches("")){
                    tilSeriesNo.setError(null);

                }
                if (seriesno.matches("")){
                    tilSeriesNo.setError("Please filled this blank");

                }
                if (household.matches("")){
                    tilHousehold.setError("Please filled this blank");

                }
                if (!household.matches("")){
                    tilHousehold.setError(null);
                }
                if (!Cardresult.matches("[0-9 ]+")){
                    tilCashCard.setError("Invalid format");
                }
                if (length!=23 ){
                    tilCashCard.setError("Not enough length");
                }

                if (idCard.equals("Scan")){
                    Toast.makeText(getApplicationContext(), "Please Scan 4P's Id", Toast.LENGTH_SHORT).show();
                }
                if (!Cardresult.matches("[0-9 ]+")){
                    tilCashCard.setError("Cash Card contains a character");
                }

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

    public void CashCardOnChange(){
        edtCashCard.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';
            private boolean isDelete;
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
        edtHhnumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilHousehold.setError("Please filled this blank");
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
        edtSeriesno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if(s.toString().length() == 0){
                    tilSeriesNo.setError("Please filled this blank");
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



}