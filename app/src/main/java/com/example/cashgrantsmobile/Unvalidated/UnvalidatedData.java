package com.example.cashgrantsmobile.Unvalidated;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.content.ContentValues.TAG;
import static com.example.cashgrantsmobile.MainActivity.sqLiteHelper;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cashgrantsmobile.MainActivity;
import com.example.cashgrantsmobile.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UnvalidatedData extends AppCompatActivity {

    GridView gridView;
    ArrayList<UnvalidatedItem> list;
    UnvalidatedAdapter adapter = null;
    TextView tvIdentifier;
    TextView tv_result;
    TextView tvSearch;
    TextView tv_household_id;
    TextView tv_name;
    TextView tv_address;
    EditText filterHousehold, filterFname, filterMname, filterLname;
    AutoCompleteTextView filterProvince, filterMunicipality, filterBarangay;

    final ArrayList<String> province_list = new ArrayList<>();
    final ArrayList<String> municipality_list = new ArrayList<>();
    final ArrayList<String> barangay_list = new ArrayList<>();

    String psgc_province = "";
    String psgc_municipality = "";
    String psgc_barangay = "";

    String filter_hh_id = "";
    String filter_fname = "";
    String filter_mname = "";
    String filter_lname = "";
    String filter_province = "";
    String filter_municipality = "";
    String filter_barangay = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unvalidated_list);

        gridView = findViewById(R.id.gridView);
        tvSearch = findViewById(R.id.unvalidatedSearch);

        list = new ArrayList<>();
        adapter = new UnvalidatedAdapter(this, R.layout.unvalidated_list_item, list);
        gridView.setAdapter(adapter);

        Cursor get_db_prov = sqLiteHelper.getData("SELECT name_new FROM psgc WHERE geographic_level='province'");

        while(get_db_prov.moveToNext()) {
            province_list.add(get_db_prov.getString(0).toUpperCase());
        }

        try {
            getData(list,adapter, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvSearch.setOnClickListener(v -> searchItem());
    }

    @SuppressLint("SetTextI18n")
    public void getData(ArrayList<UnvalidatedItem> list, UnvalidatedAdapter adapter, JSONObject filterData) throws JSONException {
        tvIdentifier = findViewById(R.id.tvIdentifier);
        tv_result = findViewById(R.id.tv_result);
        String f_hh_id = null, f_fname = null, f_mname = null, f_lname = null, f_province = null, f_municipality = null, f_barangay = null;

        if (filterData != null) {
            f_hh_id = filterData.getString("hh_id");
            f_fname = filterData.getString("firstname");
            f_mname = filterData.getString("middlename");
            f_lname = filterData.getString("lastname");
            f_province = filterData.getString("province");
            f_municipality = filterData.getString("municipality");
            f_barangay = filterData.getString("barangay");
        }

        try {
            Cursor unvalidatedData;

            if (filterData != null) {
                unvalidatedData = sqLiteHelper.getData("SELECT id, first_name, last_name, middle_name, ext_name, hh_id, province, municipality, barangay FROM emv_validations WHERE first_name LIKE '%"+ f_fname +"%' AND middle_name LIKE '%"+f_mname+"%' AND last_name LIKE '%"+f_lname+"%' AND hh_id LIKE '%"+f_hh_id+"%' AND province LIKE '%"+f_province+"%' AND municipality LIKE '%"+f_municipality+"%' AND barangay LIKE '%"+f_barangay+"%' AND validated_at='null'");
            } else {
                unvalidatedData = sqLiteHelper.getData("SELECT id, first_name, last_name, middle_name, ext_name, hh_id, province, municipality, barangay FROM emv_validations WHERE validated_at='null'");
            }

            list.clear();

            while (unvalidatedData.moveToNext()) {
                int id = unvalidatedData.getInt(0);
                String fullname = unvalidatedData.getString(1) + " " + unvalidatedData.getString(3) + " " + unvalidatedData.getString(2) + " " + unvalidatedData.getString(4);
                String hh_id = unvalidatedData.getString(5);
                String Address = unvalidatedData.getString(8) + ", " + unvalidatedData.getString(7) + ", " + unvalidatedData.getString(6);

                list.add(new UnvalidatedItem(id, fullname, Address, hh_id));
            }

            if (unvalidatedData.getCount() > 0) {
                tvIdentifier.setText("");
                tv_result.setText(String.valueOf(unvalidatedData.getCount()));
            } else {
                tv_result.setText("0");
                tvIdentifier.setText("No Data Found!");
            }
            unvalidatedData.close();
            adapter.notifyDataSetChanged();
        }
        catch (Exception e){
            Log.d(TAG, "Error: "+ e);
        }
    }

    public void searchItem() {

        String[] prov_list = new String[province_list.size()];
        prov_list = province_list.toArray(prov_list);

        final Dialog dialog = new Dialog(UnvalidatedData.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.search_unvalidated_list);

        Button btnSearch = dialog.findViewById(R.id.btn_search);
        Button btnReset = dialog.findViewById(R.id.btnReset);

        filterHousehold = dialog.findViewById(R.id.edtFilterHHID);
        filterFname = dialog.findViewById(R.id.edtFilterFname);
        filterMname = dialog.findViewById(R.id.edtFilterMname);
        filterLname = dialog.findViewById(R.id.edtFilterLname);
        filterProvince = dialog.findViewById(R.id.aatFilterProvince);
        filterMunicipality = dialog.findViewById(R.id.aatFilterMunicipality);
        filterBarangay = dialog.findViewById(R.id.aatFilterBarangay);

        tv_household_id = findViewById(R.id.tv_household_id);
        tv_name = findViewById(R.id.tv_name);
        tv_address = findViewById(R.id.tv_address);

        ArrayAdapter<String> adapterProvince = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, prov_list);
        adapterProvince.setDropDownViewResource(simple_spinner_dropdown_item);
        filterProvince.setAdapter(adapterProvince);

        filterProvince.setOnItemClickListener((adapterView, view, i, l) -> province_event());

        filterMunicipality.setOnItemClickListener((adapterView, view, i, l) -> municipality_event());

        filterBarangay.setOnItemClickListener((adapterView, view, i, l) -> barangay_event());


        filterHousehold.setText(filter_hh_id);
        filterFname.setText(filter_fname);
        filterMname.setText(filter_mname);
        filterLname.setText(filter_lname);
        filterProvince.setText(filter_province, false);
        if (!filter_province.matches("")) {
            province_event();
        }
        filterMunicipality.setText(filter_municipality, false);
        if (!filter_municipality.matches("")) {
            municipality_event();
        }
        filterBarangay.setText(filter_barangay, false);
        if (!filter_barangay.matches("")) {
            barangay_event();
        }

        btnSearch.setOnClickListener(v -> {

            JSONObject filterData = new JSONObject();
            try {
                filter_hh_id = filterHousehold.getText().toString();
                filter_fname = filterFname.getText().toString();
                filter_mname = filterMname.getText().toString();
                filter_lname = filterLname.getText().toString();
                filter_province = filterProvince.getText().toString();
                filter_municipality = filterMunicipality.getText().toString();
                filter_barangay = filterBarangay.getText().toString();

                filterData.put("hh_id", filter_hh_id);
                filterData.put("firstname", filter_fname);
                filterData.put("middlename", filter_mname);
                filterData.put("lastname", filter_lname);
                filterData.put("province", filter_province);
                filterData.put("municipality", filter_municipality);
                filterData.put("barangay", filter_barangay);

                getData(list,adapter,filterData);

                tv_household_id.setText(filter_hh_id);
                tv_name.setText(String.format("%s %s %s", filter_fname, filter_mname, filter_lname));
                tv_address.setText(String.format("%s, %s, %s", filter_barangay, filter_municipality, filter_province));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        });

        btnReset.setOnClickListener(v -> {
            try {
                filter_hh_id = "";
                filter_fname = "";
                filter_mname = "";
                filter_lname = "";
                filter_province = "";
                filter_municipality = "";
                filter_barangay = "";
                filterHousehold.setText(null);
                filterFname.setText(null);
                filterMname.setText(null);
                filterLname.setText(null);
                filterProvince.setText(null, false);
                filterMunicipality.setText(null, false);
                filterBarangay.setText(null, false);
                getData(list,adapter,null);
                tv_household_id.setText(null);
                tv_name.setText(null);
                tv_address.setText(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });

        dialog.show();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(UnvalidatedData.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
