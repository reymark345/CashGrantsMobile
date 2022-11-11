package com.example.cashgrantsmobile.Inventory;


import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;


public class InventoryList extends AppCompatActivity {

    GridView gridView;
    ArrayList<Inventory> list;
    InventoryListAdapter adapter = null;
    String cashCardNumber;
    private TextView tvResultHh;
    private TextView tvIdentifier;
    EditText  filterDate, filterHousehold, filterFname, filterMname, filterLname;
    AutoCompleteTextView filterProvince, filterMunicipality, filterBarangay;
    String ftype, fhhid, fdate;
    String[] typeOptions = new String[]{"", "complete", "incomplete"};

    final ArrayList<String> province_list = new ArrayList<>();
    final ArrayList<String> municipality_list = new ArrayList<>();
    final ArrayList<String> barangay_list = new ArrayList<>();

    String psgc_province = "";
    String psgc_municipality = "";
    String psgc_barangay = "";

    String filter_fname = "";
    String filter_mname = "";
    String filter_lname = "";
    String filter_province = "";
    String filter_province_name = "";
    String filter_municipality = "";
    String filter_municipality_name = "";
    String filter_barangay = "";
    String filter_barangay_name = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_list);
        gridView = (GridView) findViewById(R.id.gridView);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("ExcludeInclude");
            Toasty.success(this,value+"d", Toasty.LENGTH_SHORT).show();
        }
        TextView tvSearch = findViewById(R.id.tvSearch);
        tvResultHh = findViewById(R.id.tvResultHousehold);
        tvIdentifier = findViewById(R.id.tvIdentifier);
        tvResultHh.setVisibility(View.INVISIBLE);
        tvIdentifier.setVisibility(View.INVISIBLE);

        Cursor get_db_prov = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE geographic_level='province'");

        while(get_db_prov.moveToNext()) {
            province_list.add(get_db_prov.getString(0).toUpperCase());
        }

        tvSearch.setOnClickListener(v -> searchItem());
        list = new ArrayList<>();
        adapter = new InventoryListAdapter(this, R.layout.activity_inventory_items, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            Inventory data = list.get(position);
            Integer emv_id = data.getEmvId();
            String householdNumber = data.gethhNumber();

            new SweetAlertDialog(InventoryList.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Update household?")
                    .setContentText(householdNumber)
                    .setConfirmText("Proceed")
                    .showCancelButton(false)
                    .setConfirmClickListener(sDialog -> {
                        Intent in = new Intent(getApplicationContext(), UpdateData.class);
                        in.putExtra("emv_id", emv_id);
                        startActivity(in);
                        finish();
                    }).show();
            });
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            get_data(list, adapter, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void searchItem() {
        String[] prov_list = new String[province_list.size()];
        prov_list = province_list.toArray(prov_list);

        final Dialog dialog = new Dialog(InventoryList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_inventory_list);
        filterHousehold = dialog.findViewById(R.id.edtFilterHHID);
        filterDate = dialog.findViewById(R.id.edtFilterDate);
        filterFname = dialog.findViewById(R.id.edtFilterFname);
        filterMname = dialog.findViewById(R.id.edtFilterMname);
        filterLname = dialog.findViewById(R.id.edtFilterLname);
        filterProvince = dialog.findViewById(R.id.aatFilterProvince);
        filterMunicipality = dialog.findViewById(R.id.aatFilterMunicipality);
        filterBarangay = dialog.findViewById(R.id.aatFilterBarangay);

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, prov_list);
        adapterProvince.setDropDownViewResource(simple_spinner_dropdown_item);
        filterProvince.setAdapter(adapterProvince);

        filterProvince.setOnItemClickListener((adapterView, view, i, l) -> province_event());

        filterMunicipality.setOnItemClickListener((adapterView, view, i, l) -> municipality_event());

        filterBarangay.setOnItemClickListener((adapterView, view, i, l) -> barangay_event());

        filterHousehold.setText(fhhid);
        filterDate.setText(fdate);
        filterFname.setText(filter_fname);
        filterMname.setText(filter_mname);
        filterLname.setText(filter_lname);
        filterProvince.setText(filter_province_name, false);
        if (!filter_province_name.matches("")) {
            province_event();
        }
        filterMunicipality.setText(filter_municipality_name, false);
        if (!filter_municipality_name.matches("")) {
            municipality_event();
        }
        filterBarangay.setText(filter_barangay_name, false);
        if (!filter_barangay_name.matches("")) {
            barangay_event();
        }


        tvIdentifier = findViewById(R.id.tvIdentifier);

        Button submitButton = dialog.findViewById(R.id.btn_search);
        Button btnRefresh = dialog.findViewById(R.id.btnRefresh);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, typeOptions);
        adapterType.setDropDownViewResource(simple_spinner_dropdown_item);

        filterDate.setOnClickListener(v -> showDateDialog(filterDate));

        submitButton.setOnClickListener(v -> {
            JSONObject filterData = new JSONObject();

            fhhid = filterHousehold.getText().toString();
            fdate = filterDate.getText().toString();
            filter_fname = filterFname.getText().toString();
            filter_mname = filterMname.getText().toString();
            filter_lname = filterLname.getText().toString();
            filter_province_name = filterProvince.getText().toString();
            filter_municipality_name = filterMunicipality.getText().toString();
            filter_barangay_name = filterBarangay.getText().toString();

            try {
                filterData.put("hh_id", fhhid);
                filterData.put("f_date", fdate);
                filterData.put("firstname", filter_fname);
                filterData.put("middlename", filter_mname);
                filterData.put("lastname", filter_lname);
                filterData.put("province", psgc_province);
                filterData.put("municipality", psgc_municipality);
                filterData.put("barangay", psgc_barangay);

                get_data(list, adapter, filterData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        });

        btnRefresh.setOnClickListener(v -> {
            ftype = null;
            fhhid = null;
            fdate = null;
            tvIdentifier.setVisibility(View.INVISIBLE);
            tvResultHh.setVisibility(View.INVISIBLE);
            filter_fname = "";
            filter_mname = "";
            filter_lname = "";
            filter_province = "";
            filter_municipality = "";
            filter_barangay = "";
            filter_province_name = "";
            filter_municipality_name = "";
            filter_barangay_name = "";
            filterHousehold.setText(null);
            filterFname.setText(null);
            filterMname.setText(null);
            filterLname.setText(null);
            filterProvince.setText(null, false);
            filterMunicipality.setText(null, false);
            filterBarangay.setText(null, false);
            try {
                get_data(list, adapter, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            date_in.setText(simpleDateFormat.format(calendar.getTime()));

        };

        new DatePickerDialog(InventoryList.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void get_data(ArrayList<Inventory> list, InventoryListAdapter adapter, JSONObject filterData) throws JSONException {
        list.clear();

        String f_hh_id = null, f_date = null, f_fname = null, f_mname = null, f_lname = null, f_province = null, f_municipality = null, f_barangay = null;

        if (filterData != null) {
            f_hh_id = filterData.getString("hh_id");
            f_date = filterData.getString("f_date");
            f_fname = filterData.getString("firstname");
            f_mname = filterData.getString("middlename");
            f_lname = filterData.getString("lastname");
            f_province = filterData.getString("province");
            f_municipality = filterData.getString("municipality");
            f_barangay = filterData.getString("barangay");
        }

        try {
            Cursor cursor;
            if (filterData != null) {
                cursor  = sqLiteHelper.getData("SELECT evd.id, gv.hh_id, cvd.card_number_inputted, cvd.card_number_system_generated, cvd.card_number_series,cvd.card_image, gv.grantee_image FROM emv_validation_details AS evd LEFT JOIN card_validation_details AS cvd ON cvd.id = evd.card_validation_detail_id LEFT JOIN grantee_validations AS gv ON gv.id = evd.grantee_validation_id WHERE gv.hh_id LIKE'%"+f_hh_id+"%' AND DATE(evd.created_at) LIKE'%"+f_date+"%' AND gv.first_name LIKE '%"+f_fname+"%' AND gv.middle_name LIKE '%"+f_mname+"%' AND gv.last_name LIKE '%"+f_lname+"%' AND gv.province_code LIKE '%"+f_province+"%' AND gv.municipality_code LIKE '%"+f_municipality+"%' AND gv.barangay_code LIKE '%"+f_barangay+"%' order by evd.id DESC");
            } else {
                cursor  = sqLiteHelper.getData("SELECT evd.id, gv.hh_id, cvd.card_number_inputted, cvd.card_number_system_generated, cvd.card_number_series,cvd.card_image, gv.grantee_image FROM emv_validation_details AS evd LEFT JOIN card_validation_details AS cvd ON cvd.id = evd.card_validation_detail_id LEFT JOIN grantee_validations AS gv ON gv.id = evd.grantee_validation_id order by evd.id DESC");
            }

            if (cursor.getCount()==0){
                tvIdentifier.setText("No Scanned available");
                tvIdentifier.setVisibility(View.VISIBLE);
            } else {
                tvIdentifier.setText("");
                tvIdentifier.setVisibility(View.GONE);
            }

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String hhNumber = cursor.getString(1);
                if (cursor.getString(2).matches("")){
                    cashCardNumber = cursor.getString(3);
                }
                else{
                    cashCardNumber = cursor.getString(2);
                }
                String grantee_number = cursor.getString(3);
                String seriesNumber = cursor.getString(4);
                byte[] CashCardImage = cursor.getBlob(5);
                byte[] idImage = cursor.getBlob(6);
                list.add(new Inventory(cashCardNumber, grantee_number,seriesNumber, CashCardImage, idImage, id,hhNumber));
            }

            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
    }

    public void province_event() {
        Cursor get_prov_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='"+filterProvince.getText().toString()+"' AND geographic_level='province' LIMIT 1");
        String split_prov_psgc = null;

        filterMunicipality.setText(null, false);
        filterBarangay.setText(null, false);
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

        try (Cursor muni_data = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE correspondence_code LIKE '%" + split_prov_psgc + "%' AND geographic_level='municipality'")) {
            municipality_list.clear();
            while (muni_data.moveToNext()) {
                municipality_list.add(muni_data.getString(0).toUpperCase());
            }
        }

        String[] muni_list = new String[municipality_list.size()];
        muni_list = municipality_list.toArray(muni_list);

        ArrayAdapter<String> adapterMunicipality = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, muni_list);
        adapterMunicipality.setDropDownViewResource(simple_spinner_dropdown_item);
        filterMunicipality.setAdapter(adapterMunicipality);
    }

    public void municipality_event() {
        String split_prov_code = psgc_province.substring(0, 4);
        Cursor get_muni_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='"+filterMunicipality.getText().toString()+"' AND geographic_level='municipality' AND correspondence_code LIKE '%"+split_prov_code+"%' LIMIT 1");
        String split_muni_psgc = null;

        filterBarangay.setText(null, false);
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

        try (Cursor brgy_data = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE correspondence_code LIKE '%" + split_muni_psgc + "%' AND geographic_level='barangay'")) {
            barangay_list.clear();
            while (brgy_data.moveToNext()) {
                barangay_list.add(brgy_data.getString(0).toUpperCase());
            }
        }

        String[] brgy_list = new String[barangay_list.size()];
        brgy_list = barangay_list.toArray(brgy_list);

        ArrayAdapter<String> adapterBarangay = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, brgy_list);
        adapterBarangay.setDropDownViewResource(simple_spinner_dropdown_item);
        filterBarangay.setAdapter(adapterBarangay);
    }

    public void barangay_event() {
        String split_muni_code = psgc_municipality.substring(0, 6);
        try (Cursor get_brgy_psgc = sqLiteHelper.getData("SELECT correspondence_code FROM psgc WHERE upper(name_new)='" + filterBarangay.getText().toString() + "' AND geographic_level='barangay' AND correspondence_code LIKE '%" + split_muni_code + "%' LIMIT 1")) {
            while (get_brgy_psgc.moveToNext()) {
                psgc_barangay = get_brgy_psgc.getString(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            Intent intent = new Intent(InventoryList.this, MainActivity.class);
//            startActivity(intent);
//            finish();
            super.onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }
}