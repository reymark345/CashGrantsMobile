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


    public void insertEmvDatabase(String full_name,String household,String client_status,String address,String sex,String hh_set_group,String contact_no,String assigned,String minor_grantee, String card_released,String who_released,String place_released,String is_available,String is_available_reason,String other_card_number_1,String other_card_holder_name_1,String other_is_available_1,String other_is_available_reason_1,String other_card_number_2,String other_card_holder_name_2,String other_card_is_available_2,String other_card_reason_2,String other_card_number_3,String other_card_holder_name_3,String other_card_is_available_3,String other_card_reason_3,String nma_amount,String nma_reason,String date_withdrawn,String remarks,String lender_name,String pawning_date,String date_retrieved,String spin_status,String pawning_reason,String offense_history,String offense_history_date,String pd_remarks,String intervention,String other_details,String current_grantee_card,byte[] cc_image, String pawn_loaned_amount, String pawn_lender_address, String pawn_interest, String other_card_number_series_1, String other_card_number_series_2, String other_card_number_series_3, Integer user_id, Integer emv_id, String username){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO emv_database_monitoring_details VALUES (NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, full_name);
            statement.bindString(2, household);
            statement.bindString(3, client_status);
            statement.bindString(4, address);
            statement.bindString(5, sex);
            statement.bindString(6, hh_set_group);
            statement.bindString(7, assigned);
            statement.bindString(8, minor_grantee);
            statement.bindString(9, contact_no);
            statement.bindString(10, card_released);
            statement.bindString(11, place_released);
            statement.bindString(12, who_released);
            statement.bindString(13, is_available);
            statement.bindString(14, is_available_reason);
            statement.bindString(15, current_grantee_card);
            statement.bindString(16, other_card_number_1);
            statement.bindString(17, other_card_holder_name_1);
            statement.bindString(18, other_card_number_2);
            statement.bindString(19, other_card_holder_name_2);
            statement.bindString(20, other_card_number_3);
            statement.bindString(21, other_card_holder_name_3);
            statement.bindString(22, other_is_available_1);
            statement.bindString(23, other_is_available_reason_1);
            statement.bindString(24, nma_amount);
            statement.bindString(25, date_withdrawn);
            statement.bindString(26, nma_reason);
            statement.bindString(27, remarks);
            statement.bindString(28, lender_name);
            statement.bindString(29, pawning_date);
            statement.bindString(30, date_retrieved);
            statement.bindString(31, spin_status);
            statement.bindString(32, pawning_reason);
            statement.bindString(33, offense_history);
            statement.bindString(34, offense_history_date);
            statement.bindString(35, pd_remarks);
            statement.bindString(36, intervention);
            statement.bindString(37, other_details);
            statement.bindString(39, username);
            statement.bindBlob(43, cc_image);
            statement.bindLong(50, emv_id);
            statement.bindString(54, strDate);
            statement.bindString(57, other_card_is_available_2);
            statement.bindString(58, other_card_is_available_3);
            statement.bindString(59, other_card_reason_2);
            statement.bindString(60, other_card_reason_3);
            statement.bindString(61, pawn_loaned_amount);
            statement.bindString(62, pawn_lender_address);
            statement.bindString(63, pawn_interest);
            statement.bindString(47, other_card_number_series_1);
            statement.bindString(48, other_card_number_series_2);
            statement.bindString(49, other_card_number_series_3);
            statement.bindLong(52, user_id);
            statement.bindLong(56, 0);
            statement.executeInsert();
        }
        catch(Exception e){
            Log.v(TAG,e.toString());
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
                    String sql = "INSERT INTO emv_database_monitoring (id, full_name, hh_id, client_status, address, sex, hh_set_group, current_grantee_card_number, other_card_number_1, other_card_holder_name_1, other_card_number_2, other_card_holder_name_2, other_card_number_3, other_card_holder_name_3, upload_history_id, created_at, updated_at, validated_at) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    SQLiteStatement statement = database.compileStatement(sql);
                    statement.clearBindings();
                    statement.bindLong(1, extractedData.getInt("id"));
                    statement.bindString(2, extractedData.getString("full_name"));
                    statement.bindString(3, extractedData.getString("hh_id"));
                    statement.bindString(4, extractedData.getString("client_status"));
                    statement.bindString(5, extractedData.getString("address"));
                    statement.bindString(6, extractedData.getString("sex"));
                    statement.bindString(7, extractedData.getString("hh_set_group"));
                    statement.bindString(8, extractedData.getString("current_grantee_card_number"));
                    statement.bindString(9, extractedData.getString("other_card_number_1"));
                    statement.bindString(10, extractedData.getString("other_card_holder_name_1"));
                    statement.bindString(11, extractedData.getString("other_card_number_2"));
                    statement.bindString(12, extractedData.getString("other_card_holder_name_2"));
                    statement.bindString(13, extractedData.getString("other_card_number_3"));
                    statement.bindString(14, extractedData.getString("other_card_holder_name_3"));
                    statement.bindLong(15, extractedData.getInt("upload_history_id"));
                    statement.bindString(16, extractedData.getString("created_at"));
                    statement.bindString(17, extractedData.getString("updated_at"));
                    statement.bindString(18, extractedData.getString("validated_at"));
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