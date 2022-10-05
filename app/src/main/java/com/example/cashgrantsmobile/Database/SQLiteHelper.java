package com.example.cashgrantsmobile.Database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.cashgrantsmobile.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public void insertDarkModeStatus(String status){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO DarkMode VALUES (NULL,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, status);
        statement.executeInsert();
    }


//    public void insertEmvDatabase(String full_name,String household,String client_status,String address,String sex,String hh_set_group,String contact_no,String assigned,String minor_grantee, String card_released,String who_released,String place_released,String is_available,String is_available_reason,String other_card_number_1,String other_card_holder_name_1,String other_is_available_1,String other_is_available_reason_1,String other_card_number_2,String other_card_holder_name_2,String other_card_is_available_2,String other_card_reason_2,String other_card_number_3,String other_card_holder_name_3,String other_card_is_available_3,String other_card_reason_3,String nma_amount,String nma_reason,String date_withdrawn,String remarks,String lender_name,String pawning_date,String date_retrieved,String spin_status,String pawning_reason,String offense_history,String offense_history_date,String pd_remarks,String intervention,String other_details,String current_grantee_card,byte[] cc_image, String pawn_loaned_amount, String pawn_lender_address, String pawn_interest, String other_card_number_series_1, String other_card_number_series_2, String other_card_number_series_3, Integer user_id, Integer emv_id, String username){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String strDate = sdf.format(new Date());
//        try {
//            SQLiteDatabase database = getWritableDatabase();
//            String sql = "INSERT INTO emv_database_monitoring_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//            SQLiteStatement statement = database.compileStatement(sql);
//            statement.clearBindings();
//            statement.bindString(1, full_name);
//            statement.bindString(2, household);
//            statement.bindString(3, client_status);
//            statement.bindString(4, address);
//            statement.bindString(5, sex);
//            statement.bindString(6, hh_set_group);
//            statement.bindString(7, assigned);
//            statement.bindString(8, minor_grantee);
//            statement.bindString(9, contact_no);
//            statement.bindString(10, card_released);
//            statement.bindString(11, place_released);
//            statement.bindString(12, who_released);
//            statement.bindString(13, is_available);
//            statement.bindString(14, is_available_reason);
//            statement.bindString(15, current_grantee_card);
//            statement.bindString(16, other_card_number_1);
//            statement.bindString(17, other_card_holder_name_1);
//            statement.bindString(18, other_card_number_2);
//            statement.bindString(19, other_card_holder_name_2);
//            statement.bindString(20, other_card_number_3);
//            statement.bindString(21, other_card_holder_name_3);
//            statement.bindString(22, other_is_available_1);
//            statement.bindString(23, other_is_available_reason_1);
//            statement.bindString(24, nma_amount);
//            statement.bindString(25, date_withdrawn);
//            statement.bindString(26, nma_reason);
//            statement.bindString(27, remarks);
//            statement.bindString(28, lender_name);
//            statement.bindString(29, pawning_date);
//            statement.bindString(30, date_retrieved);
//            statement.bindString(31, spin_status);
//            statement.bindString(32, pawning_reason);
//            statement.bindString(33, offense_history);
//            statement.bindString(34, offense_history_date);
//            statement.bindString(35, pd_remarks);
//            statement.bindString(36, intervention);
//            statement.bindString(37, other_details);
//            statement.bindString(39, username);
//            statement.bindBlob(43, cc_image);
//            statement.bindLong(50, emv_id);
//            statement.bindString(54, strDate);
//            statement.bindString(57, other_card_is_available_2);
//            statement.bindString(58, other_card_is_available_3);
//            statement.bindString(59, other_card_reason_2);
//            statement.bindString(60, other_card_reason_3);
//            statement.bindString(61, pawn_loaned_amount);
//            statement.bindString(62, pawn_lender_address);
//            statement.bindString(63, pawn_interest);
//            statement.bindString(47, other_card_number_series_1);
//            statement.bindString(48, other_card_number_series_2);
//            statement.bindString(49, other_card_number_series_3);
//            statement.bindLong(52, user_id);
//            statement.bindLong(56, 0);
//            statement.executeInsert();
//        }
//        catch(Exception e){
//            Log.v(TAG,e.toString());
//        }
//    }

    public void insertDatabase(String hh_id,String first_name,String last_name,String middle_name,String ext_name,String sex,String province,String municipality, String barangay, String set,
                               String lender_name,String lender_address,String date_pawned,String date_retrieved,String loaned_amount,String status,String reason,String interest,String offense_history,String offense_date,String remarks,String staff_intervention,String other_details,
                               String amount,String date_claimed,String nma_reason,String nma_remarks,
                               String hh_status,String contact_no,String contact_no_of,String is_grantee,String is_minor,String relationship_to_grantee,String assigned_staff,String representative_name, String sync_at, int user_id,
                               String card_number_prefilled, String card_number_system_generated, String card_number_unputted, String card_number_series, String distribution_status, String release_date, String release_by, String release_place, String card_physically_presented, String card_pin_is_attached, String reason_not_presented,String reason_unclaimed, String card_replacement_request, String card_replacement_submitted_details, String emv_monitoring_id,
                               String card_holder_name1,String card_number_system_generated1,String card_number_inputted1,String card_number_series1,String distribution_status1,String release_date1,String release_by1,String release_place1,String card_physically_presented1,String card_pin_is_attached1,String reason_not_presented1,String reason_unclaimed1,String card_replacement_request1,String card_replacement_request_submitted_details1,String pawning_remarks1,
                               String card_holder_name2,String card_number_system_generated2,String card_number_inputted2,String card_number_series2,String distribution_status2, String release_date2,String release_by2,String release_place2,String card_physically_presented2,String card_pin_is_attached2,String reason_not_presented2,String reason_unclaimed2,String card_replacement_request2,String card_replacement_request_submitted_details2,String pawning_remarks2,
                               String card_holder_name3,String card_number_system_generated3,String card_number_inputted3,String card_number_series3,String distribution_status3,String release_date3, String release_by3,String release_place3,String card_physically_presented3,String card_pin_is_attached3,String reason_not_presented3,String reason_unclaimed3,String card_replacement_request3,String card_replacement_request_submitted_details3,String pawning_remarks3,
                               String card_holder_name4,String card_number_system_generated4,String card_number_inputted4,String card_number_series4,String distribution_status4,String release_date4,String release_by4,String release_place4,String card_physically_presented4,String card_pin_is_attached4,String reason_not_presented4,String reason_unclaimed4,String card_replacement_request4,String card_replacement_request_submitted_details4,String pawning_remarks4,
                               String card_holder_name5,String card_number_system_generated5,String card_number_inputted5,String card_number_series5,String distribution_status5,String release_date5,String release_by5,String release_place5,String card_physically_presented5,String card_pin_is_attached5,String reason_not_presented5,String reason_unclaimed5,String card_replacement_request5,String card_replacement_request_submitted_details5,String pawning_remarks5){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        try {
            SQLiteDatabase database = getWritableDatabase();

            String sql1 = "INSERT INTO grantee_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?)";
            String sql2 = "INSERT INTO pawning_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String sql3 = "INSERT INTO nma_validations VALUES (NULL,?,?,?,?,?)";
            String sql4 = "INSERT INTO emv_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,(SELECT max(id) FROM grantee_validations),(SELECT max(id) FROM pawning_validation_details),(SELECT max(id) FROM nma_validations),?,?,?)";
            String sql5 = "INSERT INTO card_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?)";
            String sql6 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?)";

            SQLiteStatement grantee_validations = database.compileStatement(sql1);
            grantee_validations.clearBindings();
            grantee_validations.bindString(1, hh_id);
            grantee_validations.bindString(2, first_name);
            grantee_validations.bindString(3, last_name);
            grantee_validations.bindString(4, middle_name);
            grantee_validations.bindString(5, ext_name);
            grantee_validations.bindString(6, sex);
            grantee_validations.bindString(7, province);
            grantee_validations.bindString(8, municipality);
            grantee_validations.bindString(9, barangay);
            grantee_validations.bindString(10, set);
            grantee_validations.bindString(11, strDate);
            grantee_validations.executeInsert();

            SQLiteStatement pawning_validations_details = database.compileStatement(sql2);
            pawning_validations_details.clearBindings();
            pawning_validations_details.bindString(1, lender_name);
            pawning_validations_details.bindString(2, lender_address);
            pawning_validations_details.bindString(3, date_pawned);
            pawning_validations_details.bindString(4, date_retrieved);
            pawning_validations_details.bindString(5, loaned_amount);
            pawning_validations_details.bindString(6, status);
            pawning_validations_details.bindString(7, reason);
            pawning_validations_details.bindString(8, interest);
            pawning_validations_details.bindString(9, offense_history);
            pawning_validations_details.bindString(10, offense_date);
            pawning_validations_details.bindString(11, remarks);
            pawning_validations_details.bindString(12, staff_intervention);
            pawning_validations_details.bindString(13, other_details);
            pawning_validations_details.bindString(14, strDate);
            pawning_validations_details.executeInsert();

            SQLiteStatement nma_validations = database.compileStatement(sql3);
            nma_validations.clearBindings();
            nma_validations.bindString(1, amount);
            nma_validations.bindString(2, date_claimed);
            nma_validations.bindString(3, nma_reason);
            nma_validations.bindString(4, nma_remarks);
            nma_validations.bindString(5, strDate);
            nma_validations.executeInsert();


            SQLiteStatement emv_validation_details = database.compileStatement(sql4);
            emv_validation_details.clearBindings();
            emv_validation_details.bindString(1, hh_status);
            emv_validation_details.bindString(2, contact_no);
            emv_validation_details.bindString(3, contact_no_of);
            emv_validation_details.bindString(4, is_grantee);
            emv_validation_details.bindString(5, is_minor);
            emv_validation_details.bindString(6, relationship_to_grantee);
            emv_validation_details.bindString(7, assigned_staff);
            emv_validation_details.bindString(8, representative_name);
            emv_validation_details.bindString(12, sync_at);
            emv_validation_details.bindLong(13, user_id);
            emv_validation_details.bindString(14, strDate);
            emv_validation_details.executeInsert();


            SQLiteStatement card_validation_details = database.compileStatement(sql5);
            card_validation_details.clearBindings();
            card_validation_details.bindString(1, card_number_prefilled);
            card_validation_details.bindString(2, card_number_system_generated);
            card_validation_details.bindString(3, card_number_unputted);
            card_validation_details.bindString(4, card_number_series);
            card_validation_details.bindString(5, distribution_status);
            card_validation_details.bindString(6, release_date);
            card_validation_details.bindString(7, release_by);
            card_validation_details.bindString(8, release_place);
            card_validation_details.bindString(9, card_physically_presented);
            card_validation_details.bindString(10, card_pin_is_attached);
            card_validation_details.bindString(11, reason_not_presented);
            card_validation_details.bindString(12, reason_unclaimed);
            card_validation_details.bindString(13, card_replacement_request);
            card_validation_details.bindString(14, card_replacement_submitted_details);
            card_validation_details.bindString(15, strDate);
            card_validation_details.executeInsert();


            SQLiteStatement other_card_validations1 = database.compileStatement(sql6);
            other_card_validations1.clearBindings();
            other_card_validations1.bindString(1, card_holder_name1);
            other_card_validations1.bindString(2, card_number_system_generated1);
            other_card_validations1.bindString(3, card_number_inputted1);
            other_card_validations1.bindString(4, card_number_series1);
            other_card_validations1.bindString(5, distribution_status1);
            other_card_validations1.bindString(6, release_date1);
            other_card_validations1.bindString(7, release_by1);
            other_card_validations1.bindString(8, release_place1);
            other_card_validations1.bindString(9, card_physically_presented1);
            other_card_validations1.bindString(10, card_pin_is_attached1);
            other_card_validations1.bindString(11, reason_not_presented1);
            other_card_validations1.bindString(12, reason_unclaimed1);
            other_card_validations1.bindString(13, card_replacement_request1);
            other_card_validations1.bindString(14, card_replacement_request_submitted_details1);
            other_card_validations1.bindString(15, pawning_remarks1);
            other_card_validations1.bindString(16, strDate);
//            other_card_validations_1.bindString(15, emv_monitoring_id_1);
            other_card_validations1.executeInsert();

            SQLiteStatement other_card_validations2 = database.compileStatement(sql6);
            other_card_validations2.clearBindings();
            other_card_validations2.bindString(1, card_holder_name2);
            other_card_validations2.bindString(2, card_number_system_generated2);
            other_card_validations2.bindString(3, card_number_inputted2);
            other_card_validations2.bindString(4, card_number_series2);
            other_card_validations2.bindString(5, distribution_status2);
            other_card_validations2.bindString(6, release_date2);
            other_card_validations2.bindString(7, release_by2);
            other_card_validations2.bindString(8, release_place2);
            other_card_validations2.bindString(9, card_physically_presented2);
            other_card_validations2.bindString(10, card_pin_is_attached2);
            other_card_validations2.bindString(11, reason_not_presented2);
            other_card_validations2.bindString(12, reason_unclaimed2);
            other_card_validations2.bindString(13, card_replacement_request2);
            other_card_validations2.bindString(14, card_replacement_request_submitted_details2);
            other_card_validations2.bindString(15, pawning_remarks2);
            other_card_validations2.bindString(16, strDate);
//            other_card_validatios_2.bindString(15, emv_monitoring_id_2);
            other_card_validations2.executeInsert();

            SQLiteStatement other_card_validations3 = database.compileStatement(sql6);
            other_card_validations3.clearBindings();
            other_card_validations3.bindString(1, card_holder_name3);
            other_card_validations3.bindString(2, card_number_system_generated3);
            other_card_validations3.bindString(3, card_number_inputted3);
            other_card_validations3.bindString(4, card_number_series3);
            other_card_validations3.bindString(5, distribution_status3);
            other_card_validations3.bindString(6, release_date3);
            other_card_validations3.bindString(7, release_by3);
            other_card_validations3.bindString(8, release_place3);
            other_card_validations3.bindString(9, card_physically_presented3);
            other_card_validations3.bindString(10, card_pin_is_attached3);
            other_card_validations3.bindString(11, reason_not_presented3);
            other_card_validations3.bindString(12, reason_unclaimed3);
            other_card_validations3.bindString(13, card_replacement_request3);
            other_card_validations3.bindString(14, card_replacement_request_submitted_details3);
            other_card_validations3.bindString(15, pawning_remarks3);
            other_card_validations3.bindString(16, strDate);
//            other_card_validations_3.bindString(15, emv_monitoring_id_3);
            other_card_validations3.executeInsert();

            SQLiteStatement other_card_validations4 = database.compileStatement(sql6);
            other_card_validations4.clearBindings();
            other_card_validations4.bindString(1, card_holder_name4);
            other_card_validations4.bindString(2, card_number_system_generated4);
            other_card_validations4.bindString(3, card_number_inputted4);
            other_card_validations4.bindString(4, card_number_series4);
            other_card_validations4.bindString(5, distribution_status4);
            other_card_validations4.bindString(6, release_date4);
            other_card_validations4.bindString(7, release_by4);
            other_card_validations4.bindString(8, release_place4);
            other_card_validations4.bindString(9, card_physically_presented4);
            other_card_validations4.bindString(10, card_pin_is_attached4);
            other_card_validations4.bindString(11, reason_not_presented4);
            other_card_validations4.bindString(12, reason_unclaimed4);
            other_card_validations4.bindString(13, card_replacement_request4);
            other_card_validations4.bindString(14, card_replacement_request_submitted_details4);
            other_card_validations4.bindString(15, pawning_remarks4);
            other_card_validations4.bindString(16, strDate);
//            other_card_validations_4.bindString(15, emv_monitoring_id_4);
            other_card_validations4.executeInsert();

            SQLiteStatement other_card_validations5 = database.compileStatement(sql6);
            other_card_validations5.clearBindings();
            other_card_validations5.bindString(1, card_holder_name5);
            other_card_validations5.bindString(2, card_number_system_generated5);
            other_card_validations5.bindString(3, card_number_inputted5);
            other_card_validations5.bindString(4, card_number_series5);
            other_card_validations5.bindString(5, distribution_status5);
            other_card_validations5.bindString(6, release_date5);
            other_card_validations5.bindString(7, release_by5);
            other_card_validations5.bindString(8, release_place5);
            other_card_validations5.bindString(9, card_physically_presented5);
            other_card_validations5.bindString(10, card_pin_is_attached5);
            other_card_validations5.bindString(11, reason_not_presented5);
            other_card_validations5.bindString(12, reason_unclaimed5);
            other_card_validations5.bindString(13, card_replacement_request5);
            other_card_validations5.bindString(14, card_replacement_request_submitted_details5);
            other_card_validations5.bindString(15, pawning_remarks5);
            other_card_validations5.bindString(16, strDate);
//            other_card_validations_5.bindString(15, emv_monitoring_id_5);
            other_card_validations5.executeInsert();
        }
        catch(Exception e){
            Log.v(TAG,"hala naay errors"+ e);
        }
    }

    public void updateDetailsEmvDatabase(String full_name, String client_status,String address, String sex,String hh_set_group, String contact_no, String assigned, String minor_grantee, String card_released, String who_released, String place_released, String is_available,String is_available_reason,String other_card_number_1,String other_card_holder_name_1,String other_is_available_1,String other_is_available_reason_1,String other_card_number_2,String other_card_holder_name_2,String other_is_available_2,String other_is_available_reason_2,String other_card_number_3,String other_card_holder_name_3,String other_is_available_3,String other_is_available_reason_3,String nma_amount,String nma_reason,String date_withdrawn,String remarks, String lender_name,String pawning_date,String date_retrieved,String spin_status,String pawning_reason,String offense_history,String offense_history_date,String pd_remarks,String intervention,String other_details, String pawn_loaned_amount,String pawn_lender_address,String pawn_interest, String other_card_number_series_1, String other_card_number_series_2, String other_card_number_series_3,int emv_id){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET full_name = ?,client_status=?, address=?,sex=?,hh_set_group=?,assigned_staff=? ,minor_grantee=? ,contact=?,current_grantee_card_release_date=?,current_grantee_card_release_place=?,current_grantee_card_release_by=?,current_grantee_is_available=?,current_grantee_reason=?,other_card_number_1=?,other_card_holder_name_1=?,other_card_number_2=?,other_card_holder_name_2=?,other_card_number_3=?,other_card_holder_name_3=?, other_card_is_available=?,other_card_reason=?,nma_amount=?,nma_date_claimed=?,nma_reason=?,nma_remarks=?, pawn_name_of_lender=?,pawn_date=?, pawn_retrieved_date=?,pawn_status=?,pawn_reason=?,pawn_offense_history=?,pawn_offense_date=?,pawn_remarks=?,pawn_intervention_staff=?,pawn_other_details=?,other_card_number_series_1=?,other_card_number_series_2=?,other_card_number_series_3=?,updated_at=?,other_card_is_available_2=?,other_card_is_available_3=?,other_card_reason_2=?,other_card_reason_3=?,pawn_loaned_amount=?,pawn_lender_address=?,pawn_interest=? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, full_name);
            statement.bindString(2, client_status);
            statement.bindString(3, address);
            statement.bindString(4, sex);
            statement.bindString(5, hh_set_group);
            statement.bindString(6, assigned);
            statement.bindString(7, minor_grantee);
            statement.bindString(8, contact_no);
            statement.bindString(9, card_released);
            statement.bindString(10, place_released);
            statement.bindString(11, who_released);
            statement.bindString(12, is_available);
            statement.bindString(13, is_available_reason);
            statement.bindString(14, other_card_number_1);
            statement.bindString(15, other_card_holder_name_1);
            statement.bindString(16, other_card_number_2);
            statement.bindString(17, other_card_holder_name_2);
            statement.bindString(18, other_card_number_3);
            statement.bindString(19, other_card_holder_name_3);
            statement.bindString(20, other_is_available_1);
            statement.bindString(21, other_is_available_reason_1);
            statement.bindString(22, nma_amount);
            statement.bindString(23, date_withdrawn);
            statement.bindString(24, nma_reason);
            statement.bindString(25, remarks);
            statement.bindString(26, lender_name);
            statement.bindString(27, pawning_date);
            statement.bindString(28, date_retrieved);
            statement.bindString(29, spin_status);
            statement.bindString(30, pawning_reason);
            statement.bindString(31, offense_history);
            statement.bindString(32, offense_history_date);
            statement.bindString(33, pd_remarks);
            statement.bindString(34, intervention);
            statement.bindString(35, other_details);
            statement.bindString(36, other_card_number_series_1);
            statement.bindString(37, other_card_number_series_2);
            statement.bindString(38, other_card_number_series_3);
            statement.bindString(39, strDate);
            statement.bindString(40, other_is_available_2);
            statement.bindString(41, other_is_available_3);
            statement.bindString(42, other_is_available_reason_2);
            statement.bindString(43, other_is_available_reason_3);
            statement.bindString(44, pawn_loaned_amount);
            statement.bindString(45, pawn_lender_address);
            statement.bindString(46, pawn_interest);
            statement.bindLong(47, emv_id);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG,e.toString());
        }
    }

    public void insertDefaultUser(String token, String user_id, String email, String mobile, String name, String username){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO Api VALUES (1,?,?,?,?,?,?,null)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, token);
            statement.bindString(2, user_id);
            statement.bindString(3, email);
            statement.bindString(4, mobile);
            statement.bindString(5, name);
            statement.bindString(6, username);
            statement.executeInsert();
        }
        catch(Exception e){
            Log.v(TAG,"error ");
        }
    }

    public void insertDefaultScannedTmp(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO tmp_blob VALUES (1,?,null,null,null,null,null,null,null)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindBlob(1, cc_image);
            statement.executeInsert();
            Log.v(TAG,"ni insert" + cc_image);
        }
        catch(Exception e){
            Log.v(TAG,"nag error do " + e);
        }
    }

    public void insertDefaultAdditionalTmp(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO tmp_blob VALUES (1,null,?,null,null,null,null,null,null)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindBlob(1, cc_image);
            statement.executeInsert();
            Log.v(TAG,"ni insert" + cc_image);
        }
        catch(Exception e){
            Log.v(TAG,"nag error do " + e);
        }
    }

    public void insertDefaultGranteeTmp(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO tmp_blob VALUES (1,null,null,?,null,null,null,null,null)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindBlob(1, cc_image);
            statement.executeInsert();
            Log.v(TAG,"wala ni insert" + cc_image);
        }
        catch(Exception e){
            Log.v(TAG,"nag error do " + e);
        }
    }


    public void updateUser(String token, String user_id, String email, String mobile, String name, String username){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE Api SET token = ?,user_id=?, email=?,mobile=?,name=?,username=?, accomplish_e_signature=null WHERE id = 1";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, token);
            statement.bindString(2, user_id);
            statement.bindString(3, email);
            statement.bindString(4, mobile);
            statement.bindString(5, name);
            statement.bindString(6, username);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG,"error Token");
            Log.v(TAG,e.toString());
        }
    }


    public void updateTmpScannedCC(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE tmp_blob SET scanned_e_image = ? WHERE id = 1";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, cc_image);
            statement.execute();
            database.close();
            Log.v(TAG, "update ni insert ");
        }
        catch(Exception e){
            Log.v(TAG, "update ni error "+e);
        }
    }


    public void updateTmpAdditional(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE tmp_blob SET additional_id_image = ? WHERE id = 1";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, cc_image);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG, "error "+e);
        }
    }

    public void updateTmpGrantee(byte[] cc_image){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE tmp_blob SET grantee_e_image = ? WHERE id = 1";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, cc_image);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG, "error "+e);
        }
    }

    public void storeLogs(String type, String household, String description) {
        Cursor user_data = MainActivity.sqLiteHelper.getData("SELECT username FROM Api");
        String username = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());

        while (user_data.moveToNext()) {
            username = user_data.getString(0);
        }

        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO logs VALUES (NULL, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, username);
        statement.bindString(2, type);
        statement.bindString(3, household);
        statement.bindString(4, description);
        statement.bindString(5, strDate);
        statement.executeInsert();
    }



    public void updateScannedCashCard_emv(String scannedCashCard,byte[] cc_image){

        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET cash_card_actual_no = ?,current_cash_card_picture =? WHERE id = (SELECT max(id) FROM emv_database_monitoring_details) ";
            SQLiteStatement statement = database.compileStatement(sql);
//            statement.clearBindings();
            statement.bindString(1, scannedCashCard);
            statement.bindBlob(2, cc_image);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG,e.toString());
        }
    }
    public void updateSubmitData_emv(String cash_card_actual_no, String accomplishBy,String informant, byte[] cc_image, byte[] id_image, String attested, String series_number) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE emv_database_monitoring_details SET current_grantee_card_number  = ?,current_grantee_card_number_series =?, accomplish_by_full_name = ?, informant_full_name = ?, current_cash_card_picture =?, beneficiary_picture =?, attested_by_full_name =?, card_scanning_status = 1  WHERE id = (SELECT max(id) FROM emv_database_monitoring_details)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, series_number);
        statement.bindString(3, accomplishBy);
        statement.bindString(4, informant);
        statement.bindBlob(5, cc_image);
        statement.bindBlob(6, id_image);
        statement.bindString(7, attested);
        statement.execute();
        database.close();
    }
    public void update_emv_monitoring(String household_id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE emv_database_monitoring SET validated_at = ? WHERE hh_id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, strDate);
        statement.bindString(2, household_id);
        statement.execute();
        database.close();
    }
    public void updateAccomplishSignature_emv(int current_idd,String cash_card ,String accomplish,String informant,String attested ,byte[] signature, String series_number) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET  current_grantee_card_number  =?,current_grantee_card_number_series=?, accomplish_e_signature =?,accomplish_by_full_name = ?,informant_full_name = ?,attested_by_full_name = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindString(2, series_number);
            statement.bindBlob(3, signature);
            statement.bindString(4, accomplish);
            statement.bindString(5, informant);
            statement.bindString(6, attested);
            statement.bindLong(7, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,e.toString());
        }
    }

    public void updateAccomplishSignature(byte[] signature){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE Api SET accomplish_e_signature = ? WHERE id = 1";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, signature);
            statement.execute();
            database.close();
        }
        catch(Exception e){
            Log.v(TAG,e.toString());
        }
    }


    public void updateInformantSignature_emv(int current_idd,String cash_card,String accomplish,String informant,String attested ,byte[] signature,String series_number) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET current_grantee_card_number  = ?,current_grantee_card_number_series =?,informant_e_signature =?,accomplish_by_full_name = ?,informant_full_name = ?,attested_by_full_name = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindString(2, series_number);
            statement.bindBlob(3, signature);
            statement.bindString(4, accomplish);
            statement.bindString(5, informant);
            statement.bindString(6, attested);
            statement.bindLong(7, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error "+e);
        }
    }

    public void updateAttestedSignature_emv(int current_idd,String cash_card,String accomplish,String informant,String attested ,byte[] signature, String series_number) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET current_grantee_card_number  =?,current_grantee_card_number_series =?, attested_by_e_signature =?,accomplish_by_full_name = ?,informant_full_name = ?,attested_by_full_name = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindString(2, series_number);
            statement.bindBlob(3, signature);
            statement.bindString(4, accomplish);
            statement.bindString(5, informant);
            statement.bindString(6, attested);
            statement.bindLong(7, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error"+e);
        }
    }
    public void updateCashCardEmv(int current_idd,byte[] cash_card) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET current_cash_card_picture  =? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, cash_card);
            statement.bindLong(2, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error"+e);
        }
    }

    public void updateGranteeEmv(int current_idd,byte[] grantee) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET beneficiary_picture =? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, grantee);
            statement.bindLong(2, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error");
        }
    }
    public void updateAccomplishSign(byte[] accomplish_sign) {
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE emv_database_monitoring_details SET accomplish_e_signature =? WHERE id = (SELECT max(id) FROM emv_database_monitoring_details)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, accomplish_sign);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error");
        }
    }
    public void updateInventoryList_emv(String cash_card_actual_no, String accomplishBy,String informant, byte[] cc_image, byte[] id_image, int id, String attested, String series_number, int completed) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE emv_database_monitoring_details SET current_grantee_card_number  = ?, current_grantee_card_number_series = ?, accomplish_by_full_name = ?, informant_full_name = ?, current_cash_card_picture =?, beneficiary_picture =?, attested_by_full_name=? , card_scanning_status = ?  WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, series_number);
        statement.bindString(3, accomplishBy);
        statement.bindString(4, informant);
        statement.bindBlob(5, cc_image);
        statement.bindBlob(6, id_image);
        statement.bindString(7, attested);
        statement.bindDouble(8, completed);
        statement.bindDouble(9, id);
        statement.execute();
        database.close();
    }
    public void updateDarkmodeStatus(String status, int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE DarkMode SET status = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, status);
        statement.bindDouble(2, (double)id);
        statement.execute();
        database.close();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }
    public void deleteAccess() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM Api";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.execute();
        database.close();
    }
    public void insertEmvData(JSONArray remoteData) {
        for (int i=0; i < remoteData.length(); i++) {
            try {

                JSONObject extractedData = remoteData.getJSONObject(i);
                try {
                    SQLiteDatabase database = getWritableDatabase();
                    String sql = "INSERT INTO emv_validations (id,first_name, last_name, middle_name, ext_name, hh_id, hh_status, province, municipality, barangay, " +
                            "sex, hh_set_group, nma_amount, grantee_card_number, grantee_distribution_status,grantee_card_release_date, " +
                            "other_card_number_1,other_card_holder_1,other_card_distribution_status_1,other_card_release_date_1," +
                            "other_card_number_2,other_card_holder_2,other_card_distribution_status_2,other_card_release_date_2," +
                            "other_card_number_3,other_card_holder_3,other_card_distribution_status_3,other_card_release_date_3," +
                            "other_card_number_4,other_card_holder_4,other_card_distribution_status_4,other_card_release_date_4," +
                            "other_card_number_5,other_card_holder_5,other_card_distribution_status_5,other_card_release_date_5," +
                            "upload_history_id,record_counter,created_at, updated_at, validated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    SQLiteStatement statement = database.compileStatement(sql);
                    statement.clearBindings();
                    statement.bindLong(1, extractedData.getInt("id"));
                    statement.bindString(2, extractedData.getString("first_name"));
                    statement.bindString(3, extractedData.getString("last_name"));
                    statement.bindString(4, extractedData.getString("middle_name"));
                    statement.bindString(5, extractedData.getString("ext_name"));
                    statement.bindString(6, extractedData.getString("hh_id"));
                    statement.bindString(7, extractedData.getString("hh_status"));
                    statement.bindString(8, extractedData.getString("province"));
                    statement.bindString(9, extractedData.getString("municipality"));
                    statement.bindString(10, extractedData.getString("barangay"));
                    statement.bindString(11, extractedData.getString("sex"));
                    statement.bindString(12, extractedData.getString("hh_set_group"));
                    statement.bindString(13, extractedData.getString("nma_amount"));
                    statement.bindString(14, extractedData.getString("grantee_card_number"));
                    statement.bindString(15, extractedData.getString("grantee_distribution_status"));
                    statement.bindString(16, extractedData.getString("grantee_card_release_date"));
                    statement.bindString(17, extractedData.getString("other_card_number_1"));
                    statement.bindString(18, extractedData.getString("other_card_holder_1"));
                    statement.bindString(19, extractedData.getString("other_card_distribution_status_1"));
                    statement.bindString(20, extractedData.getString("other_card_release_date_1"));
                    statement.bindString(21, extractedData.getString("other_card_number_2"));
                    statement.bindString(22, extractedData.getString("other_card_holder_2"));
                    statement.bindString(23, extractedData.getString("other_card_distribution_status_2"));
                    statement.bindString(24, extractedData.getString("other_card_release_date_2"));
                    statement.bindString(25, extractedData.getString("other_card_number_3"));
                    statement.bindString(26, extractedData.getString("other_card_holder_3"));
                    statement.bindString(27, extractedData.getString("other_card_distribution_status_3"));
                    statement.bindString(28, extractedData.getString("other_card_release_date_3"));
                    statement.bindString(29, extractedData.getString("other_card_number_4"));
                    statement.bindString(30, extractedData.getString("other_card_holder_4"));
                    statement.bindString(31, extractedData.getString("other_card_distribution_status_4"));
                    statement.bindString(32, extractedData.getString("other_card_release_date_4"));
                    statement.bindString(33, extractedData.getString("other_card_number_5"));
                    statement.bindString(34, extractedData.getString("other_card_holder_5"));
                    statement.bindString(35, extractedData.getString("other_card_distribution_status_5"));
                    statement.bindString(36, extractedData.getString("other_card_release_date_5"));
                    statement.bindString(37, extractedData.getString("upload_history_id"));
                    statement.bindString(38, extractedData.getString("record_counter"));
                    statement.bindString(39, extractedData.getString("created_at"));
                    statement.bindString(40, extractedData.getString("updated_at"));
                    statement.bindString(41, extractedData.getString("validated_at"));
                    statement.executeInsert();
                }
                catch(Exception e){
                    Log.v(TAG,"Error "+e );
                }




//                try {
//                    SQLiteDatabase database = getWritableDatabase();
//                    String sql = "INSERT INTO emv_database_monitoring (id, full_name, hh_id, client_status, address, sex, hh_set_group, current_grantee_card_number, other_card_number_1, other_card_holder_name_1, other_card_number_2, other_card_holder_name_2, other_card_number_3, other_card_holder_name_3, upload_history_id, created_at, updated_at, validated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//                    SQLiteStatement statement = database.compileStatement(sql);
//                    statement.clearBindings();
//                    statement.bindLong(1, extractedData.getInt("id"));
//                    statement.bindString(2, extractedData.getString("full_name"));
//                    statement.bindString(3, extractedData.getString("hh_id"));
//                    statement.bindString(4, extractedData.getString("client_status"));
//                    statement.bindString(5, extractedData.getString("address"));
//                    statement.bindString(6, extractedData.getString("sex"));
//                    statement.bindString(7, extractedData.getString("hh_set_group"));
//                    statement.bindString(8, extractedData.getString("current_grantee_card_number"));
//                    statement.bindString(9, extractedData.getString("other_card_number_1"));
//                    statement.bindString(10, extractedData.getString("other_card_holder_name_1"));
//                    statement.bindString(11, extractedData.getString("other_card_number_2"));
//                    statement.bindString(12, extractedData.getString("other_card_holder_name_2"));
//                    statement.bindString(13, extractedData.getString("other_card_number_3"));
//                    statement.bindString(14, extractedData.getString("other_card_holder_name_3"));
//                    statement.bindLong(15, extractedData.getInt("upload_history_id"));
//                    statement.bindString(16, extractedData.getString("created_at"));
//                    statement.bindString(17, extractedData.getString("updated_at"));
//                    statement.bindString(18, extractedData.getString("validated_at"));
//                    statement.executeInsert();
//                }
//                catch(Exception e){
//                    Log.v(TAG,"Error "+e );
//                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public  void deleteEmvMonitoringDetails(int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM emv_database_monitoring_details WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);
        statement.execute();
        database.close();
    }

    public void updateEmvMonitoring(String validated, String id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE emv_database_monitoring SET validated_at = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, validated);
        statement.bindString(2, id);
        statement.execute();
        database.close();
    }

    public void deleteLogs() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM logs";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.execute();
        database.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}