package com.example.cashgrantsmobile.Database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import com.example.cashgrantsmobile.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
        database.close();
    }

    public void insertDarkModeStatus(String status){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO DarkMode VALUES (NULL,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, status);
        statement.executeInsert();
        database.close();
    }

    public String convertDateFormat(String date_format) {
        String result = "";
        if (!date_format.matches("")) {
            if (date_format.contains("-")) {
                result = date_format;
            } else {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    Date convertedDate = dateFormat.parse(date_format);
                    SimpleDateFormat sdfnewformat = new SimpleDateFormat("yyyy-MM-dd");
                    result = sdfnewformat.format(convertedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public void insertDatabase(String hh_id,String first_name,String last_name,String middle_name,String ext_name,String sex,String province,String municipality, String barangay, String set,
                               String lender_name,String lender_address,String date_pawned,String date_retrieved,String loaned_amount,String status,String reason,String interest,String offense_history,String offense_date,String remarks,String staff_intervention,String other_details,
                               String amount,String date_claimed,String nma_reason,String nma_remarks,
                               String hh_status,String contact_no,String contact_no_of,String is_grantee,String is_minor,String relationship_to_grantee,String assigned_staff,String representative_name, String sync_at, int user_id, int emv_validation_id,
                               String card_number_prefilled, String card_number_system_generated, String card_number_unputted, String card_number_series, String distribution_status, String release_date, String release_by, String release_place, String card_physically_presented, String card_pin_is_attached, String reason_not_presented,String reason_unclaimed, String card_replacement_request, String card_replacement_submitted_details, int emv_monitoring_id,
                               String card_holder_name1,String card_number_system_generated1,String card_number_inputted1,String card_number_series1,String distribution_status1,String release_date1,String release_by1,String release_place1,String card_physically_presented1,String card_pin_is_attached1,String reason_not_presented1,String reason_unclaimed1,String card_replacement_request1,String card_replacement_request_submitted_details1,String pawning_remarks1,
                               String card_holder_name2,String card_number_system_generated2,String card_number_inputted2,String card_number_series2,String distribution_status2, String release_date2,String release_by2,String release_place2,String card_physically_presented2,String card_pin_is_attached2,String reason_not_presented2,String reason_unclaimed2,String card_replacement_request2,String card_replacement_request_submitted_details2,String pawning_remarks2,
                               String card_holder_name3,String card_number_system_generated3,String card_number_inputted3,String card_number_series3,String distribution_status3,String release_date3, String release_by3,String release_place3,String card_physically_presented3,String card_pin_is_attached3,String reason_not_presented3,String reason_unclaimed3,String card_replacement_request3,String card_replacement_request_submitted_details3,String pawning_remarks3,
                               String card_holder_name4,String card_number_system_generated4,String card_number_inputted4,String card_number_series4,String distribution_status4,String release_date4,String release_by4,String release_place4,String card_physically_presented4,String card_pin_is_attached4,String reason_not_presented4,String reason_unclaimed4,String card_replacement_request4,String card_replacement_request_submitted_details4,String pawning_remarks4,
                               String card_holder_name5,String card_number_system_generated5,String card_number_inputted5,String card_number_series5,String distribution_status5,String release_date5,String release_by5,String release_place5,String card_physically_presented5,String card_pin_is_attached5,String reason_not_presented5,String reason_unclaimed5,String card_replacement_request5,String card_replacement_request_submitted_details5,String pawning_remarks5, int card_count,
                               byte[] card_image,byte[] grantee_image,byte[] additional_image,byte[] other_scanned_image1,byte[] other_scanned_image2,byte[] other_scanned_image3,byte[] other_scanned_image4,byte[] other_scanned_image5, String overall_remarks, String other_ext_name, String contact_no_of_others, String others_reason_not_presented, String others_reason_not_presented1, String others_reason_not_presented2, String others_reason_not_presented3, String others_reason_not_presented4, String others_reason_not_presented5, String others_reason_unclaimed, String others_reason_unclaimed1, String others_reason_unclaimed2, String others_reason_unclaimed3, String others_reason_unclaimed4, String others_reason_unclaimed5, String nma_others_reason) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());

        SQLiteDatabase database = getWritableDatabase();
        String sql1 = "INSERT INTO grantee_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql2 = "INSERT INTO pawning_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql3 = "INSERT INTO nma_validations VALUES (NULL,?,?,?,?,?,?)";
        String sql4 = "INSERT INTO card_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql5,sql6,sql7,sql8,sql9,sql10;


        if (additional_image!=null){
            sql5 = "INSERT INTO emv_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,(SELECT max(id) FROM grantee_validations),(SELECT max(id) FROM pawning_validation_details),(SELECT max(id) FROM nma_validations),(SELECT max(id) FROM card_validation_details),?,?,(SELECT user_id FROM api),?,?,?,?,?)";
        } else {
            sql5 = "INSERT INTO emv_validation_details VALUES (NULL,?,?,?,?,?,?,?,?,(SELECT max(id) FROM grantee_validations),(SELECT max(id) FROM pawning_validation_details),(SELECT max(id) FROM nma_validations),(SELECT max(id) FROM card_validation_details),?,?,(SELECT user_id FROM api),null,?,?,?,?)";
        }

        if (other_scanned_image1!=null){sql6 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?,?,?,?)";}
        else {sql6 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),null,?,?,?)";}
        if (other_scanned_image2!=null){sql7 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?,?,?,?)";}
        else {sql7 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),null,?,?,?)";}
        if (other_scanned_image3!=null){sql8 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?,?,?,?)";}
        else {sql8 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),null,?,?,?)";}
        if (other_scanned_image4!=null){sql9 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?,?,?,?)";}
        else {sql9 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),null,?,?,?)";}
        if (other_scanned_image5!=null){sql10 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),?,?,?,?)";}
        else {sql10 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(SELECT max(id) FROM emv_validation_details),null,?,?,?)";}


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
        grantee_validations.bindBlob(11, grantee_image);
        grantee_validations.bindString(12, strDate);
        grantee_validations.bindString(13, other_ext_name);
        grantee_validations.executeInsert();

        if (reason_not_presented.matches("Pawned")){
            SQLiteStatement pawning_validations_details = database.compileStatement(sql2);
            pawning_validations_details.clearBindings();
            pawning_validations_details.bindString(1, lender_name);
            pawning_validations_details.bindString(2, lender_address);
            pawning_validations_details.bindString(3, convertDateFormat(date_pawned));
            pawning_validations_details.bindString(4, convertDateFormat(date_retrieved));
            pawning_validations_details.bindString(5, loaned_amount);
            pawning_validations_details.bindString(6, status);
            pawning_validations_details.bindString(7, reason);
            pawning_validations_details.bindString(8, interest);
            pawning_validations_details.bindString(9, offense_history);
            pawning_validations_details.bindString(10, convertDateFormat(offense_date));
            pawning_validations_details.bindString(11, remarks);
            pawning_validations_details.bindString(12, staff_intervention);
            pawning_validations_details.bindString(13, other_details);
            pawning_validations_details.bindString(14, strDate);
            pawning_validations_details.executeInsert();
        }
        
        SQLiteStatement nma_validations = database.compileStatement(sql3);
        nma_validations.clearBindings();
        nma_validations.bindString(1, amount);
        nma_validations.bindString(2, convertDateFormat(date_claimed));
        nma_validations.bindString(3, nma_reason);
        nma_validations.bindString(4, nma_remarks);
        nma_validations.bindString(5, strDate);
        nma_validations.bindString(6, nma_others_reason);
        nma_validations.executeInsert();

        SQLiteStatement card_validation_details = database.compileStatement(sql4);
        card_validation_details.clearBindings();
        card_validation_details.bindString(1, card_number_prefilled);
        card_validation_details.bindString(2, card_number_system_generated);
        card_validation_details.bindString(3, card_number_unputted);
        card_validation_details.bindString(4, card_number_series);
        card_validation_details.bindString(5, distribution_status);
        card_validation_details.bindString(6, convertDateFormat(release_date));
        card_validation_details.bindString(7, release_by);
        card_validation_details.bindString(8, release_place);
        card_validation_details.bindString(9, card_physically_presented);
        card_validation_details.bindString(10, card_pin_is_attached);
        card_validation_details.bindString(11, reason_not_presented);
        card_validation_details.bindString(12, reason_unclaimed);
        card_validation_details.bindString(13, card_replacement_request);
        card_validation_details.bindString(14, card_replacement_submitted_details);
        card_validation_details.bindBlob(15, card_image);
        card_validation_details.bindString(16, strDate);
        card_validation_details.bindString(17, others_reason_not_presented);
        card_validation_details.bindString(18, others_reason_unclaimed);
        card_validation_details.executeInsert();

        SQLiteStatement emv_validation_details = database.compileStatement(sql5);
        emv_validation_details.clearBindings();
        emv_validation_details.bindString(1, hh_status);
        emv_validation_details.bindString(2, contact_no);
        emv_validation_details.bindString(3, contact_no_of);
        emv_validation_details.bindString(4, is_grantee);
        emv_validation_details.bindString(5, is_minor);
        emv_validation_details.bindString(6, relationship_to_grantee);
        emv_validation_details.bindString(7, assigned_staff);
        emv_validation_details.bindString(8, representative_name);
        emv_validation_details.bindString(9, sync_at);
        emv_validation_details.bindLong(10, user_id);
        if (additional_image !=null){
            emv_validation_details.bindBlob(11,additional_image);
            emv_validation_details.bindString(12, overall_remarks);
            emv_validation_details.bindString(13, strDate);
            emv_validation_details.bindLong(14, emv_monitoring_id);
            emv_validation_details.bindString(15, contact_no_of_others);
        }
        else {
            emv_validation_details.bindString(11, overall_remarks);
            emv_validation_details.bindString(12, strDate);
            emv_validation_details.bindLong(13, emv_monitoring_id);
            emv_validation_details.bindString(14, contact_no_of_others);
        }
        emv_validation_details.executeInsert();

        if (card_count >=1) {
            SQLiteStatement other_card_validations1 = database.compileStatement(sql6);
            other_card_validations1.clearBindings();
            other_card_validations1.bindString(1, card_holder_name1);
            other_card_validations1.bindString(2, card_number_system_generated1);
            if (!card_number_inputted1.matches("")) {
                card_number_inputted1 = card_number_inputted1;
            }
            other_card_validations1.bindString(3, card_number_inputted1);
            other_card_validations1.bindString(4, card_number_series1);
            other_card_validations1.bindString(5, distribution_status1);
            other_card_validations1.bindString(6, convertDateFormat(release_date1));
            other_card_validations1.bindString(7, release_by1);
            other_card_validations1.bindString(8, release_place1);
            other_card_validations1.bindString(9, card_physically_presented1);
            other_card_validations1.bindString(10, card_pin_is_attached1);
            other_card_validations1.bindString(11, reason_not_presented1);
            other_card_validations1.bindString(12, reason_unclaimed1);
            other_card_validations1.bindString(13, card_replacement_request1);
            other_card_validations1.bindString(14, card_replacement_request_submitted_details1);
            other_card_validations1.bindString(15, pawning_remarks1);
            if (other_scanned_image1 !=null){
                other_card_validations1.bindBlob(16, other_scanned_image1);
                other_card_validations1.bindString(17, strDate);
                other_card_validations1.bindString(18, others_reason_not_presented);
                other_card_validations1.bindString(19, others_reason_unclaimed);
            }
            else {
                other_card_validations1.bindString(16, strDate);
                other_card_validations1.bindString(17, others_reason_not_presented);
                other_card_validations1.bindString(18, others_reason_unclaimed);
            }
            other_card_validations1.executeInsert();
        }

        if (card_count >=2) {
            SQLiteStatement other_card_validations2 = database.compileStatement(sql7);
            other_card_validations2.clearBindings();
            other_card_validations2.bindString(1, card_holder_name2);
            other_card_validations2.bindString(2, card_number_system_generated2);
            if (!card_number_inputted2.matches("")) {
                card_number_inputted2 = card_number_inputted2;
            }
            other_card_validations2.bindString(3, card_number_inputted2);
            other_card_validations2.bindString(4, card_number_series2);
            other_card_validations2.bindString(5, distribution_status2);
            other_card_validations2.bindString(6, convertDateFormat(release_date2));
            other_card_validations2.bindString(7, release_by2);
            other_card_validations2.bindString(8, release_place2);
            other_card_validations2.bindString(9, card_physically_presented2);
            other_card_validations2.bindString(10, card_pin_is_attached2);
            other_card_validations2.bindString(11, reason_not_presented2);
            other_card_validations2.bindString(12, reason_unclaimed2);
            other_card_validations2.bindString(13, card_replacement_request2);
            other_card_validations2.bindString(14, card_replacement_request_submitted_details2);
            other_card_validations2.bindString(15, pawning_remarks2);

            if (other_scanned_image2 !=null){
                other_card_validations2.bindBlob(16, other_scanned_image2);
                other_card_validations2.bindString(17, strDate);
            }
            else {
                other_card_validations2.bindString(16, strDate);
            }
            other_card_validations2.executeInsert();

        }

        if (card_count >=3) {
            SQLiteStatement other_card_validations3 = database.compileStatement(sql8);
            other_card_validations3.clearBindings();
            other_card_validations3.bindString(1, card_holder_name3);
            other_card_validations3.bindString(2, card_number_system_generated3);
            if (!card_number_inputted3.matches("")) {
                card_number_inputted3 = card_number_inputted3;
            }
            other_card_validations3.bindString(3, card_number_inputted3);
            other_card_validations3.bindString(4, card_number_series3);
            other_card_validations3.bindString(5, distribution_status3);
            other_card_validations3.bindString(6, convertDateFormat(release_date3));
            other_card_validations3.bindString(7, release_by3);
            other_card_validations3.bindString(8, release_place3);
            other_card_validations3.bindString(9, card_physically_presented3);
            other_card_validations3.bindString(10, card_pin_is_attached3);
            other_card_validations3.bindString(11, reason_not_presented3);
            other_card_validations3.bindString(12, reason_unclaimed3);
            other_card_validations3.bindString(13, card_replacement_request3);
            other_card_validations3.bindString(14, card_replacement_request_submitted_details3);
            other_card_validations3.bindString(15, pawning_remarks3);

            if (other_scanned_image3 !=null){
                other_card_validations3.bindBlob(16, other_scanned_image3);
                other_card_validations3.bindString(17, strDate);
            }
            else {
                other_card_validations3.bindString(16, strDate);
            }
            other_card_validations3.executeInsert();
        }

        if (card_count >=4) {
            SQLiteStatement other_card_validations4 = database.compileStatement(sql9);
            other_card_validations4.clearBindings();
            other_card_validations4.bindString(1, card_holder_name4);
            other_card_validations4.bindString(2, card_number_system_generated4);
            if (!card_number_inputted4.matches("")) {
                card_number_inputted4 = card_number_inputted4;
            }
            other_card_validations4.bindString(3, card_number_inputted4);
            other_card_validations4.bindString(4, card_number_series4);
            other_card_validations4.bindString(5, distribution_status4);
            other_card_validations4.bindString(6, convertDateFormat(release_date4));
            other_card_validations4.bindString(7, release_by4);
            other_card_validations4.bindString(8, release_place4);
            other_card_validations4.bindString(9, card_physically_presented4);
            other_card_validations4.bindString(10, card_pin_is_attached4);
            other_card_validations4.bindString(11, reason_not_presented4);
            other_card_validations4.bindString(12, reason_unclaimed4);
            other_card_validations4.bindString(13, card_replacement_request4);
            other_card_validations4.bindString(14, card_replacement_request_submitted_details4);
            other_card_validations4.bindString(15, pawning_remarks4);
            if (other_scanned_image4 !=null){
                other_card_validations4.bindBlob(16, other_scanned_image4);
                other_card_validations4.bindString(17, strDate);
            }
            else {
                other_card_validations4.bindString(16, strDate);
            }
            other_card_validations4.executeInsert();
        }


        if (card_count >=5) {
            SQLiteStatement other_card_validations5 = database.compileStatement(sql10);
            other_card_validations5.clearBindings();
            other_card_validations5.bindString(1, card_holder_name5);
            other_card_validations5.bindString(2, card_number_system_generated5);
            if (!card_number_inputted5.matches("")) {
                card_number_inputted5 = card_number_inputted5;
            }
            other_card_validations5.bindString(3, card_number_inputted5);
            other_card_validations5.bindString(4, card_number_series5);
            other_card_validations5.bindString(5, distribution_status5);
            other_card_validations5.bindString(6, convertDateFormat(release_date5));
            other_card_validations5.bindString(7, release_by5);
            other_card_validations5.bindString(8, release_place5);
            other_card_validations5.bindString(9, card_physically_presented5);
            other_card_validations5.bindString(10, card_pin_is_attached5);
            other_card_validations5.bindString(11, reason_not_presented5);
            other_card_validations5.bindString(12, reason_unclaimed5);
            other_card_validations5.bindString(13, card_replacement_request5);
            other_card_validations5.bindString(14, card_replacement_request_submitted_details5);
            other_card_validations5.bindString(15, pawning_remarks5);
            if (other_scanned_image5 !=null){
                other_card_validations5.bindBlob(16, other_scanned_image5);
                other_card_validations5.bindString(17, strDate);
            }
            else {
                other_card_validations5.bindString(16, strDate);
            }
            other_card_validations5.executeInsert();
        }

        String sql_emv_validations = "UPDATE emv_validations SET validated_at = ? WHERE hh_id = ?";
        SQLiteStatement statement_emv_validations = database.compileStatement(sql_emv_validations);
        statement_emv_validations.bindString(1, strDate);
        statement_emv_validations.bindString(2, hh_id);
        statement_emv_validations.execute();
        database.close();

        deleteTmpBlob(1);

    }

    public void deleteTmpBlob(Integer id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql_tmp_blob = "DELETE FROM tmp_blob WHERE id=" + id;
        SQLiteStatement statement_tmp_blob = database.compileStatement(sql_tmp_blob);
        statement_tmp_blob.clearBindings();
        statement_tmp_blob.execute();
        database.close();
    }

    public void updateDatabase(String hh_id,String first_name,String last_name,String middle_name,String ext_name,String sex,String province,String municipality, String barangay, String set,
                               String lender_name,String lender_address,String date_pawned,String date_retrieved,String loaned_amount,String status,String reason,String interest,String offense_history,String offense_date,String remarks,String staff_intervention,String other_details,
                               String amount,String date_claimed,String nma_reason,String nma_remarks,
                               String hh_status,String contact_no,String contact_no_of,String is_grantee,String is_minor,String relationship_to_grantee,String assigned_staff,String representative_name,
                               String card_number_prefilled, String card_number_system_generated, String card_number_unputted, String card_number_series, String distribution_status, String release_date, String release_by, String release_place, String card_physically_presented, String card_pin_is_attached, String reason_not_presented,String reason_unclaimed, String card_replacement_request, String card_replacement_submitted_details, int emv_monitoring_id,
                               String card_holder_name1,String card_number_system_generated1,String card_number_inputted1,String card_number_series1,String distribution_status1,String release_date1,String release_by1,String release_place1,String card_physically_presented1,String card_pin_is_attached1,String reason_not_presented1,String reason_unclaimed1,String card_replacement_request1,String card_replacement_request_submitted_details1,String pawning_remarks1,
                               String card_holder_name2,String card_number_system_generated2,String card_number_inputted2,String card_number_series2,String distribution_status2, String release_date2,String release_by2,String release_place2,String card_physically_presented2,String card_pin_is_attached2,String reason_not_presented2,String reason_unclaimed2,String card_replacement_request2,String card_replacement_request_submitted_details2,String pawning_remarks2,
                               String card_holder_name3,String card_number_system_generated3,String card_number_inputted3,String card_number_series3,String distribution_status3,String release_date3, String release_by3,String release_place3,String card_physically_presented3,String card_pin_is_attached3,String reason_not_presented3,String reason_unclaimed3,String card_replacement_request3,String card_replacement_request_submitted_details3,String pawning_remarks3,
                               String card_holder_name4,String card_number_system_generated4,String card_number_inputted4,String card_number_series4,String distribution_status4,String release_date4,String release_by4,String release_place4,String card_physically_presented4,String card_pin_is_attached4,String reason_not_presented4,String reason_unclaimed4,String card_replacement_request4,String card_replacement_request_submitted_details4,String pawning_remarks4,
                               String card_holder_name5,String card_number_system_generated5,String card_number_inputted5,String card_number_series5,String distribution_status5,String release_date5,String release_by5,String release_place5,String card_physically_presented5,String card_pin_is_attached5,String reason_not_presented5,String reason_unclaimed5,String card_replacement_request5,String card_replacement_request_submitted_details5,String pawning_remarks5, int card_count,
                               byte[] card_image,byte[] grantee_image,byte[] additional_image,byte[] other_scanned_image1,byte[] other_scanned_image2,byte[] other_scanned_image3,byte[] other_scanned_image4,byte[] other_scanned_image5, String overall_remarks, String other_ext_name, String contact_no_of_others, String others_reason_not_presented, String others_reason_not_presented1, String others_reason_not_presented2, String others_reason_not_presented3, String others_reason_not_presented4, String others_reason_not_presented5, String others_reason_unclaimed, String others_reason_unclaimed1, String others_reason_unclaimed2, String others_reason_unclaimed3, String others_reason_unclaimed4, String others_reason_unclaimed5, String nma_others_reason, Integer evd_id, Integer gv_id, Integer cvd_id, Integer pvd_id, Integer nv_id, Integer ocv_id1, Integer ocv_id2, Integer ocv_id3, Integer ocv_id4, Integer ocv_id5) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());

        SQLiteDatabase database = getWritableDatabase();
        String sql1 = "UPDATE grantee_validations SET hh_id = ?, first_name = ?, last_name = ?, middle_name = ?, ext_name = ?, sex = ?, province_code = ?, municipality_code = ?, barangay_code = ?, hh_set = ?, grantee_image = ?, other_ext_name = ? WHERE id = ?";
        String sql2 = "UPDATE pawning_validation_details SET lender_name = ?, lender_address = ?, date_pawned = ?, date_retrieved = ?, loan_amount = ?, status = ?, reason = ?, interest = ?, offense_history = ?, offense_date = ?, remarks = ?, staff_intervention = ?, other_details = ? WHERE id = ?";
        String sql3 = "UPDATE nma_validations SET amount = ?, date_claimed = ?, reason = ?, remarks = ?, nma_others_reason = ? WHERE id = ?";
        String sql4 = "UPDATE card_validation_details SET card_number_prefilled = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_submitted_details = ?, card_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        String sql5,sql6,sql7,sql8,sql9,sql10,sql11,sql12,sql13,sql14,sql15;


        if (additional_image!=null){
            sql5 = "UPDATE emv_validation_details SET hh_status = ?, contact_no = ?, contact_no_of = ?, is_grantee = ?, is_minor = ?, relationship_to_grantee = ?, assigned_staff = ?, representative_name = ?, additional_image = ?, overall_remarks = ?, created_at = ?, contact_no_of_others = ? WHERE id = ?";
        } else {
            sql5 = "UPDATE emv_validation_details SET hh_status = ?, contact_no = ?, contact_no_of = ?, is_grantee = ?, is_minor = ?, relationship_to_grantee = ?, assigned_staff = ?, representative_name = ?, additional_image = NULL, overall_remarks = ?, created_at = ?, contact_no_of_others = ? WHERE id = ?";
        }

        if (other_scanned_image1!=null) {
            sql6 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        } else {
            sql6 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = NULL, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        }
        if (other_scanned_image2!=null) {
            sql7 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        } else {
            sql7 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = NULL, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        }
        if (other_scanned_image3!=null) {
            sql8 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        } else {
            sql8 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = NULL, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        }
        if (other_scanned_image4!=null) {
            sql9 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        } else {
            sql9 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = NULL, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        }
        if (other_scanned_image5!=null) {
            sql10 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = ?, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        } else {
            sql10 = "UPDATE other_card_validations SET card_holder_name = ?, card_number_system_generated = ?, card_number_inputted = ?, card_number_series = ?, distribution_status = ?, release_date = ?, release_by = ?, release_place = ?, card_physically_presented = ?, card_pin_is_attached = ?, reason_not_presented = ?, reason_unclaimed = ?, card_replacement_requests = ?, card_replacement_request_submitted_details = ?, pawning_remarks = ?, other_image = NULL, others_reason_not_presented = ?, others_reason_unclaimed = ? WHERE id = ?";
        }


        if (other_scanned_image1!=null){sql11 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,?)";}
        else {sql11 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL,?,?)";}
        if (other_scanned_image2!=null){sql12 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,?)";}
        else {sql12 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL,?,?)";}
        if (other_scanned_image3!=null){sql13 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,?)";}
        else {sql13 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL,?,?)";}
        if (other_scanned_image4!=null){sql14 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,?)";}
        else {sql14 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL,?,?)";}
        if (other_scanned_image5!=null){sql15 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,?,?)";}
        else {sql15 = "INSERT INTO other_card_validations VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NULL,NULL,?,?)";}

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
        grantee_validations.bindBlob(11, grantee_image);
        grantee_validations.bindString(12, other_ext_name);
        grantee_validations.bindLong(13, gv_id);
        grantee_validations.execute();

        if (reason_not_presented.matches("Pawned")){
            SQLiteStatement pawning_validations_details = database.compileStatement(sql2);
            pawning_validations_details.clearBindings();
            pawning_validations_details.bindString(1, lender_name);
            pawning_validations_details.bindString(2, lender_address);
            pawning_validations_details.bindString(3, convertDateFormat(date_pawned));
            pawning_validations_details.bindString(4, convertDateFormat(date_retrieved));
            pawning_validations_details.bindString(5, loaned_amount);
            pawning_validations_details.bindString(6, status);
            pawning_validations_details.bindString(7, reason);
            pawning_validations_details.bindString(8, interest);
            pawning_validations_details.bindString(9, offense_history);
            pawning_validations_details.bindString(10, convertDateFormat(offense_date));
            pawning_validations_details.bindString(11, remarks);
            pawning_validations_details.bindString(12, staff_intervention);
            pawning_validations_details.bindString(13, other_details);
            pawning_validations_details.bindLong(14, pvd_id);
            pawning_validations_details.execute();
        }

        SQLiteStatement nma_validations = database.compileStatement(sql3);
        nma_validations.clearBindings();
        nma_validations.bindString(1, amount);
        nma_validations.bindString(2, convertDateFormat(date_claimed));
        nma_validations.bindString(3, nma_reason);
        nma_validations.bindString(4, nma_remarks);
        nma_validations.bindString(5, nma_others_reason);
        nma_validations.bindLong(6, nv_id);
        nma_validations.execute();

        SQLiteStatement card_validation_details = database.compileStatement(sql4);
        card_validation_details.clearBindings();
        card_validation_details.bindString(1, card_number_prefilled);
        card_validation_details.bindString(2, card_number_system_generated);
        card_validation_details.bindString(3, card_number_unputted);
        card_validation_details.bindString(4, card_number_series);
        card_validation_details.bindString(5, distribution_status);
        card_validation_details.bindString(6, convertDateFormat(release_date));
        card_validation_details.bindString(7, release_by);
        card_validation_details.bindString(8, release_place);
        card_validation_details.bindString(9, card_physically_presented);
        card_validation_details.bindString(10, card_pin_is_attached);
        card_validation_details.bindString(11, reason_not_presented);
        card_validation_details.bindString(12, reason_unclaimed);
        card_validation_details.bindString(13, card_replacement_request);
        card_validation_details.bindString(14, card_replacement_submitted_details);
        card_validation_details.bindBlob(15, card_image);
        card_validation_details.bindString(16, others_reason_not_presented);
        card_validation_details.bindString(17, others_reason_unclaimed);
        card_validation_details.bindLong(18, cvd_id);
        card_validation_details.execute();

        SQLiteStatement emv_validation_details = database.compileStatement(sql5);
        emv_validation_details.clearBindings();
        emv_validation_details.bindString(1, hh_status);
        emv_validation_details.bindString(2, contact_no);
        emv_validation_details.bindString(3, contact_no_of);
        emv_validation_details.bindString(4, is_grantee);
        emv_validation_details.bindString(5, is_minor);
        emv_validation_details.bindString(6, relationship_to_grantee);
        emv_validation_details.bindString(7, assigned_staff);
        emv_validation_details.bindString(8, representative_name);
        if (additional_image !=null){
            emv_validation_details.bindBlob(9,additional_image);
            emv_validation_details.bindString(10, overall_remarks);
            emv_validation_details.bindString(11, strDate);
            emv_validation_details.bindString(12, contact_no_of_others);
            emv_validation_details.bindLong(13, evd_id);
        }
        else {
            emv_validation_details.bindString(9, overall_remarks);
            emv_validation_details.bindString(10, strDate);
            emv_validation_details.bindString(11, contact_no_of_others);
            emv_validation_details.bindLong(12, evd_id);
        }
        emv_validation_details.execute();
        if (card_count >=1) {
            SQLiteStatement other_card_validations1 = database.compileStatement((ocv_id1 == 0) ? sql11 : sql6);
            other_card_validations1.clearBindings();
            other_card_validations1.bindString(1, card_holder_name1);
            other_card_validations1.bindString(2, card_number_system_generated1);
            other_card_validations1.bindString(3, card_number_inputted1);
            other_card_validations1.bindString(4, card_number_series1);
            other_card_validations1.bindString(5, distribution_status1);
            other_card_validations1.bindString(6, convertDateFormat(release_date1));
            other_card_validations1.bindString(7, release_by1);
            other_card_validations1.bindString(8, release_place1);
            other_card_validations1.bindString(9, card_physically_presented1);
            other_card_validations1.bindString(10, card_pin_is_attached1);
            other_card_validations1.bindString(11, reason_not_presented1);
            other_card_validations1.bindString(12, reason_unclaimed1);
            other_card_validations1.bindString(13, card_replacement_request1);
            other_card_validations1.bindString(14, card_replacement_request_submitted_details1);
            other_card_validations1.bindString(15, pawning_remarks1);
            if (ocv_id1 == 0) {
                if (other_scanned_image1 !=null){
                    other_card_validations1.bindLong(16, evd_id);
                    other_card_validations1.bindBlob(17, other_scanned_image1);
                    other_card_validations1.bindString(18, others_reason_not_presented1);
                    other_card_validations1.bindString(19, others_reason_unclaimed1);
                } else {
                    other_card_validations1.bindLong(16, evd_id);
                    other_card_validations1.bindString(17, others_reason_not_presented1);
                    other_card_validations1.bindString(18, others_reason_unclaimed1);
                }
            } else {
                if (other_scanned_image1 !=null){
                    other_card_validations1.bindBlob(16, other_scanned_image1);
                    other_card_validations1.bindString(17, others_reason_not_presented1);
                    other_card_validations1.bindString(18, others_reason_unclaimed1);
                    other_card_validations1.bindLong(19, ocv_id1);
                } else {
                    other_card_validations1.bindString(16, others_reason_not_presented1);
                    other_card_validations1.bindString(17, others_reason_unclaimed1);
                    other_card_validations1.bindLong(18, ocv_id1);
                }
                ocv_id1 = 0;
            }
            other_card_validations1.execute();
        }

        if (card_count >=2) {
            SQLiteStatement other_card_validations2 = database.compileStatement((ocv_id2 == 0) ? sql12 : sql7);
            other_card_validations2.clearBindings();
            other_card_validations2.bindString(1, card_holder_name2);
            other_card_validations2.bindString(2, card_number_system_generated2);
            other_card_validations2.bindString(3, card_number_inputted2);
            other_card_validations2.bindString(4, card_number_series2);
            other_card_validations2.bindString(5, distribution_status2);
            other_card_validations2.bindString(6, convertDateFormat(release_date2));
            other_card_validations2.bindString(7, release_by2);
            other_card_validations2.bindString(8, release_place2);
            other_card_validations2.bindString(9, card_physically_presented2);
            other_card_validations2.bindString(10, card_pin_is_attached2);
            other_card_validations2.bindString(11, reason_not_presented2);
            other_card_validations2.bindString(12, reason_unclaimed2);
            other_card_validations2.bindString(13, card_replacement_request2);
            other_card_validations2.bindString(14, card_replacement_request_submitted_details2);
            other_card_validations2.bindString(15, pawning_remarks2);
            if (ocv_id2 == 0) {
                if (other_scanned_image2 != null){
                    other_card_validations2.bindLong(16, evd_id);
                    other_card_validations2.bindBlob(17, other_scanned_image2);
                    other_card_validations2.bindString(18, others_reason_not_presented2);
                    other_card_validations2.bindString(19, others_reason_unclaimed2);
                } else {
                    other_card_validations2.bindLong(16, evd_id);
                    other_card_validations2.bindString(17, others_reason_not_presented2);
                    other_card_validations2.bindString(18, others_reason_unclaimed2);
                }
            } else {
                if (other_scanned_image2 != null){
                    other_card_validations2.bindBlob(16, other_scanned_image2);
                    other_card_validations2.bindString(17, others_reason_not_presented2);
                    other_card_validations2.bindString(18, others_reason_unclaimed2);
                    other_card_validations2.bindLong(19, ocv_id2);
                } else {
                    other_card_validations2.bindString(16, others_reason_not_presented2);
                    other_card_validations2.bindString(17, others_reason_unclaimed2);
                    other_card_validations2.bindLong(18, ocv_id2);
                }
                ocv_id2 = 0;
            }
            other_card_validations2.execute();
        }

        if (card_count >=3) {
            SQLiteStatement other_card_validations3 = database.compileStatement((ocv_id3 == 0) ? sql13 : sql8);
            other_card_validations3.clearBindings();
            other_card_validations3.bindString(1, card_holder_name3);
            other_card_validations3.bindString(2, card_number_system_generated3);
            other_card_validations3.bindString(3, card_number_inputted3);
            other_card_validations3.bindString(4, card_number_series3);
            other_card_validations3.bindString(5, distribution_status3);
            other_card_validations3.bindString(6, convertDateFormat(release_date3));
            other_card_validations3.bindString(7, release_by3);
            other_card_validations3.bindString(8, release_place3);
            other_card_validations3.bindString(9, card_physically_presented3);
            other_card_validations3.bindString(10, card_pin_is_attached3);
            other_card_validations3.bindString(11, reason_not_presented3);
            other_card_validations3.bindString(12, reason_unclaimed3);
            other_card_validations3.bindString(13, card_replacement_request3);
            other_card_validations3.bindString(14, card_replacement_request_submitted_details3);
            other_card_validations3.bindString(15, pawning_remarks3);
            if (ocv_id3 == 0) {
                if (other_scanned_image3 != null){
                    other_card_validations3.bindLong(16, evd_id);
                    other_card_validations3.bindBlob(17, other_scanned_image3);
                    other_card_validations3.bindString(18, others_reason_not_presented3);
                    other_card_validations3.bindString(19, others_reason_unclaimed3);
                }
                else {
                    other_card_validations3.bindLong(16, evd_id);
                    other_card_validations3.bindString(17, others_reason_not_presented3);
                    other_card_validations3.bindString(18, others_reason_unclaimed3);
                }
            } else {
                if (other_scanned_image3 != null){
                    other_card_validations3.bindBlob(16, other_scanned_image3);
                    other_card_validations3.bindString(17, others_reason_not_presented3);
                    other_card_validations3.bindString(18, others_reason_unclaimed3);
                    other_card_validations3.bindLong(19, ocv_id3);
                }
                else {
                    other_card_validations3.bindString(16, others_reason_not_presented3);
                    other_card_validations3.bindString(17, others_reason_unclaimed3);
                    other_card_validations3.bindLong(18, ocv_id3);
                }
                ocv_id3 = 0;
            }

            other_card_validations3.execute();
        }

        if (card_count >=4) {
            SQLiteStatement other_card_validations4 = database.compileStatement((ocv_id4 == 0) ? sql14 : sql9);
            other_card_validations4.clearBindings();
            other_card_validations4.bindString(1, card_holder_name4);
            other_card_validations4.bindString(2, card_number_system_generated4);
            other_card_validations4.bindString(3, card_number_inputted4);
            other_card_validations4.bindString(4, card_number_series4);
            other_card_validations4.bindString(5, distribution_status4);
            other_card_validations4.bindString(6, convertDateFormat(release_date4));
            other_card_validations4.bindString(7, release_by4);
            other_card_validations4.bindString(8, release_place4);
            other_card_validations4.bindString(9, card_physically_presented4);
            other_card_validations4.bindString(10, card_pin_is_attached4);
            other_card_validations4.bindString(11, reason_not_presented4);
            other_card_validations4.bindString(12, reason_unclaimed4);
            other_card_validations4.bindString(13, card_replacement_request4);
            other_card_validations4.bindString(14, card_replacement_request_submitted_details4);
            other_card_validations4.bindString(15, pawning_remarks4);
            if (ocv_id4 == 0) {
                if (other_scanned_image4 != null){
                    other_card_validations4.bindLong(16, evd_id);
                    other_card_validations4.bindBlob(17, other_scanned_image4);
                    other_card_validations4.bindString(18, others_reason_not_presented4);
                    other_card_validations4.bindString(19, others_reason_unclaimed4);
                }
                else {
                    other_card_validations4.bindLong(16, evd_id);
                    other_card_validations4.bindString(17, others_reason_not_presented4);
                    other_card_validations4.bindString(18, others_reason_unclaimed4);
                }
            } else {
                if (other_scanned_image4 != null){
                    other_card_validations4.bindBlob(16, other_scanned_image4);
                    other_card_validations4.bindString(17, others_reason_not_presented4);
                    other_card_validations4.bindString(18, others_reason_unclaimed4);
                    other_card_validations4.bindLong(19, ocv_id4);
                }
                else {
                    other_card_validations4.bindString(16, others_reason_not_presented4);
                    other_card_validations4.bindString(17, others_reason_unclaimed4);
                    other_card_validations4.bindLong(15, ocv_id4);
                }
                ocv_id4 = 0;
            }
            other_card_validations4.execute();
        }


        if (card_count >=5) {
            SQLiteStatement other_card_validations5 = database.compileStatement((ocv_id5 == 0) ? sql15 : sql10);
            other_card_validations5.clearBindings();
            other_card_validations5.bindString(1, card_holder_name5);
            other_card_validations5.bindString(2, card_number_system_generated5);
            other_card_validations5.bindString(3, card_number_inputted5);
            other_card_validations5.bindString(4, card_number_series5);
            other_card_validations5.bindString(5, distribution_status5);
            other_card_validations5.bindString(6, convertDateFormat(release_date5));
            other_card_validations5.bindString(7, release_by5);
            other_card_validations5.bindString(8, release_place5);
            other_card_validations5.bindString(9, card_physically_presented5);
            other_card_validations5.bindString(10, card_pin_is_attached5);
            other_card_validations5.bindString(11, reason_not_presented5);
            other_card_validations5.bindString(12, reason_unclaimed5);
            other_card_validations5.bindString(13, card_replacement_request5);
            other_card_validations5.bindString(14, card_replacement_request_submitted_details5);
            other_card_validations5.bindString(15, pawning_remarks5);
            if (ocv_id5 == 0) {
                if (other_scanned_image5 !=null){
                    other_card_validations5.bindLong(16, evd_id);
                    other_card_validations5.bindBlob(17, other_scanned_image5);
                    other_card_validations5.bindString(18, others_reason_not_presented5);
                    other_card_validations5.bindString(19, others_reason_unclaimed5);
                }
                else {
                    other_card_validations5.bindLong(16, ocv_id5);
                    other_card_validations5.bindString(17, others_reason_not_presented5);
                    other_card_validations5.bindString(18, others_reason_unclaimed5);
                }
            } else {
                if (other_scanned_image5 !=null){
                    other_card_validations5.bindBlob(16, other_scanned_image5);
                    other_card_validations5.bindString(17, others_reason_not_presented5);
                    other_card_validations5.bindString(18, others_reason_unclaimed5);
                    other_card_validations5.bindLong(19, ocv_id5);
                }
                else {
                    other_card_validations5.bindString(16, others_reason_not_presented5);
                    other_card_validations5.bindString(17, others_reason_unclaimed5);
                    other_card_validations5.bindLong(18, ocv_id5);
                }
                ocv_id5 = 0;
            }
            other_card_validations5.execute();
        }

        database.close();

        deleteTmpBlob(2);

        deleteOtherCardValidations(ocv_id1);
        deleteOtherCardValidations(ocv_id2);
        deleteOtherCardValidations(ocv_id3);
        deleteOtherCardValidations(ocv_id4);
        deleteOtherCardValidations(ocv_id5);

    }

    public void deleteOtherCardValidations(int ocv_id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql_ocv = "DELETE FROM other_card_validations WHERE id=" + ocv_id;
        SQLiteStatement statement_sql_ocv = database.compileStatement(sql_ocv);
        statement_sql_ocv.clearBindings();
        statement_sql_ocv.execute();
        database.close();
    }

    public void deleteScannedData(Integer evd_id, Integer gv_id, Integer cvd_id, Integer nv_id, Integer pvd_id, JSONArray arr_ocv_id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql_evd = "DELETE FROM emv_validation_details WHERE id=" + evd_id;
        SQLiteStatement statement_sql_evd = database.compileStatement(sql_evd);
        statement_sql_evd.clearBindings();
        statement_sql_evd.execute();

        String sql_gv = "DELETE FROM grantee_validations WHERE id=" + gv_id;
        SQLiteStatement statement_sql_gv = database.compileStatement(sql_gv);
        statement_sql_gv.clearBindings();
        statement_sql_gv.execute();

        String sql_cvd = "DELETE FROM card_validation_details WHERE id=" + cvd_id;
        SQLiteStatement statement_sql_cvd = database.compileStatement(sql_cvd);
        statement_sql_cvd.clearBindings();
        statement_sql_cvd.execute();

        String sql_nv = "DELETE FROM nma_validations WHERE id=" + nv_id;
        SQLiteStatement statement_sql_nv = database.compileStatement(sql_nv);
        statement_sql_nv.clearBindings();
        statement_sql_nv.execute();

        String sql_pvd = "DELETE FROM pawning_validation_details WHERE id=" + pvd_id;
        SQLiteStatement statement_sql_pvd = database.compileStatement(sql_pvd);
        statement_sql_pvd.clearBindings();
        statement_sql_pvd.execute();

        for (int i = 0; i < arr_ocv_id.length(); i++) {
            String sql_ocv = null;
            try {
                sql_ocv = "DELETE FROM other_card_validations WHERE id=" + arr_ocv_id.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SQLiteStatement statement_sql_ocv = database.compileStatement(sql_ocv);
            statement_sql_ocv.clearBindings();
            statement_sql_ocv.execute();
        }

        database.close();
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
            database.close();
        }
        catch(Exception e){
            Log.v(TAG,"error ");
        }
    }

    public void insertImageTmp(String col_name, byte[] cc_image, Integer true_id){
        if (cc_image != null) {
            Cursor check_tmp_blob = MainActivity.sqLiteHelper.getData("SELECT * FROM tmp_blob WHERE id="+true_id);
            String sql = null;
            SQLiteDatabase database = getWritableDatabase();
            if (check_tmp_blob.getCount() > 0) {
                sql = "UPDATE tmp_blob SET "+col_name+" = ? WHERE id = ?";
            } else {
                sql = "INSERT INTO tmp_blob ("+col_name+", id) VALUES (?,?)";
            }
            check_tmp_blob.close();
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindBlob(1, cc_image);
            statement.bindLong(2, true_id);
            statement.execute();
            database.close();
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

    public void deletePSGC() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM psgc";
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
                            "other_card_number_1,other_card_holder_name_1,other_card_distribution_status_1,other_card_release_date_1," +
                            "other_card_number_2,other_card_holder_name_2,other_card_distribution_status_2,other_card_release_date_2," +
                            "other_card_number_3,other_card_holder_name_3,other_card_distribution_status_3,other_card_release_date_3," +
                            "other_card_number_4,other_card_holder_name_4,other_card_distribution_status_4,other_card_release_date_4," +
                            "other_card_number_5,other_card_holder_name_5,other_card_distribution_status_5,other_card_release_date_5," +
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
                    statement.bindString(18, extractedData.getString("other_card_holder_name_1"));
                    statement.bindString(19, extractedData.getString("other_card_distribution_status_1"));
                    statement.bindString(20, extractedData.getString("other_card_release_date_1"));
                    statement.bindString(21, extractedData.getString("other_card_number_2"));
                    statement.bindString(22, extractedData.getString("other_card_holder_name_2"));
                    statement.bindString(23, extractedData.getString("other_card_distribution_status_2"));
                    statement.bindString(24, extractedData.getString("other_card_release_date_2"));
                    statement.bindString(25, extractedData.getString("other_card_number_3"));
                    statement.bindString(26, extractedData.getString("other_card_holder_name_3"));
                    statement.bindString(27, extractedData.getString("other_card_distribution_status_3"));
                    statement.bindString(28, extractedData.getString("other_card_release_date_3"));
                    statement.bindString(29, extractedData.getString("other_card_number_4"));
                    statement.bindString(30, extractedData.getString("other_card_holder_name_4"));
                    statement.bindString(31, extractedData.getString("other_card_distribution_status_4"));
                    statement.bindString(32, extractedData.getString("other_card_release_date_4"));
                    statement.bindString(33, extractedData.getString("other_card_number_5"));
                    statement.bindString(34, extractedData.getString("other_card_holder_name_5"));
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public void insertPsgcData(JSONArray remoteData) {
        for (int i=0; i < remoteData.length(); i++) {
            try {

                JSONObject extractedData = remoteData.getJSONObject(i);
                try {
                    SQLiteDatabase database = getWritableDatabase();
                    String sql = "INSERT INTO psgc (id,name_new, name_old, code, correspondence_code, geographic_level, created_at, updated_at) " +
                            "VALUES (?,?,?,?,?,?,?,?)";

                    SQLiteStatement statement = database.compileStatement(sql);
                    statement.clearBindings();
                    statement.bindLong(1, extractedData.getInt("id"));
                    statement.bindString(2, extractedData.getString("name_new"));
                    statement.bindString(3, extractedData.getString("name_old"));
                    statement.bindString(4, extractedData.getString("code"));
                    statement.bindString(5, extractedData.getString("correspondence_code"));
                    statement.bindString(6, extractedData.getString("geographic_level"));
                    statement.bindString(7, extractedData.getString("created_at"));
                    statement.bindString(8, extractedData.getString("updated_at"));
                    statement.executeInsert();
                    database.close();
                }
                catch(Exception e){
                    Log.v(TAG,"Error "+e );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateEmvValidations(String validated, String id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE emv_validations SET validated_at = ? WHERE id = ?";
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