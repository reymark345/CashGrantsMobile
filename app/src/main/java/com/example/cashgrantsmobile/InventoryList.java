package com.example.cashgrantsmobile;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class InventoryList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Inventory> list;
    InventoryListAdapter adapter = null;
    String cashCardNumber;
    private Toolbar mToolbars;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);
        gridView = (GridView) findViewById(R.id.gridView);
        mToolbars = findViewById(R.id.mainToolbar);
        setSupportActionBar(mToolbars);
        getSupportActionBar().setTitle("Inventory List");
        
        list = new ArrayList<>();
        adapter = new InventoryListAdapter(this, R.layout.activity_inventory_items, list);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
//                v.setBackgroundColor(Color.YELLOW);
                new SweetAlertDialog(InventoryList.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Please choose corresponding action")
                        .setCancelText("Update")
                        .setConfirmText("Exclude")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                Long l= new Long(id);
                                int i=l.intValue();
                                ScannedDetails.scanned = false;
                                Intent in = new Intent(getApplicationContext(), ScannedDetails.class);
                                in.putExtra("updateData", i);
                                startActivity(in);
                            }
                        })
                        .show();
            }
        });


        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,cc_image, id_image, cash_card_scanned_no FROM CgList");
            list.clear();
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
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
                list.add(new Inventory(cashCardNumber, hhNumber,seriesNumber, CashCardImage, idImage, id));
            }
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
//        try {
//
//
//            // get all data from sqlite
//            Cursor cursor = ScannedDetails.sqLiteHelper.getData("SELECT id,cash_card_actual_no,hh_number,series_number,id_image FROM CgList limit 500");
//            list.clear();
//            while (cursor.moveToNext()) {
//                int id = cursor.getInt(0);
//                String cashCardNumber = cursor.getString(1);
//                String hhNumber = cursor.getString(2);
//                String seriesNumber = cursor.getString(3);
//                byte[] CashCardImage = cursor.getBlob(4);
//                list.add(new Inventory(cashCardNumber, hhNumber,seriesNumber, CashCardImage, id));
//            }
//            adapter.notifyDataSetChanged();
//
//        } catch (Exception e) {
////            e.printStackTrace();
//            Toast.makeText(InventoryList.this, "Data is Empty", Toast.LENGTH_SHORT).show();
//        }


//
//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                CharSequence[] items = {"Update", "Delete"};
//                AlertDialog.Builder dialog = new AlertDialog.Builder(InventoryList.this);
//
//                dialog.setTitle("Choose an action");
//                dialog.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (item == 0) {
//                            // update
//                            Cursor c = ScannedDetails.sqLiteHelper.getData("SELECT id FROM FOOD");
//                            ArrayList<Integer> arrID = new ArrayList<Integer>();
//                            while (c.moveToNext()){
//                                arrID.add(c.getInt(0));
//                            }
//                            // show dialog update at here
//                            showDialogUpdate(InventoryList.this, arrID.get(position));
//
//                        } else {
//                            // delete
//                            Cursor c = ScannedDetails.sqLiteHelper.getData("SELECT id FROM FOOD");
//                            ArrayList<Integer> arrID = new ArrayList<Integer>();
//                            while (c.moveToNext()){
//                                arrID.add(c.getInt(0));
//                            }
//                            showDialogDelete(arrID.get(position));
//                        }
//                    }
//                });
//                dialog.show();
//                return true;
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

//    ImageView imageViewFood;
//    private void showDialogUpdate(Activity activity, final int position){
//
//        final Dialog dialog = new Dialog(activity);
//        dialog.setContentView(R.layout.update_food_activity);
//        dialog.setTitle("Update");
//
//        imageViewFood = (ImageView) dialog.findViewById(R.id.imageViewFood);
//        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
//        final EditText edtPrice = (EditText) dialog.findViewById(R.id.edtPrice);
//        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);
//
//        // set width for dialog
//        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
//        // set height for dialog
//        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
//        dialog.getWindow().setLayout(width, height);
//        dialog.show();
//
//        imageViewFood.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // request photo library
//                ActivityCompat.requestPermissions(
//                        InventoryList.this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        888
//                );
//            }
//        });
//
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    ScannedDetails.sqLiteHelper.updateData(
//                            edtName.getText().toString().trim(),
//                            edtPrice.getText().toString().trim(),
//                            ScannedDetails.imageViewToByte(imageViewFood),
//                            position
//                    );
//                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Update successfully!!!",Toast.LENGTH_SHORT).show();
//                }
//                catch (Exception error) {
//                    Log.e("Update error", error.getMessage());
//                }
//                updateFoodList();
//            }
//        });
//    }
//
//    private void showDialogDelete(final int idFood){
//        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(InventoryList.this);
//
//        dialogDelete.setTitle("Warning!!");
//        dialogDelete.setMessage("Are you sure you want to this delete?");
//        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    ScannedDetails.sqLiteHelper.deleteData(idFood);
//                    Toast.makeText(getApplicationContext(), "Delete successfully!!!",Toast.LENGTH_SHORT).show();
//                } catch (Exception e){
//                    Log.e("error", e.getMessage());
//                }
//                updateFoodList();
//            }
//        });
//
//        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        dialogDelete.show();
//    }
//
//    private void updateFoodList(){
//        // get all data from sqlite
//        Cursor cursor = ScannedDetails.sqLiteHelper.getData("SELECT * FROM FOOD");
//        list.clear();
//        while (cursor.moveToNext()) {
//            int id = cursor.getInt(0);
//            String name = cursor.getString(1);
//            String price = cursor.getString(2);
//            byte[] image = cursor.getBlob(3);
//
//            list.add(new Inventory(name, price, image, id));
//        }
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        if(requestCode == 888){
//            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 888);
//            }
//            else {
//                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
//            Uri uri = data.getData();
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                imageViewFood.setImageBitmap(bitmap);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}