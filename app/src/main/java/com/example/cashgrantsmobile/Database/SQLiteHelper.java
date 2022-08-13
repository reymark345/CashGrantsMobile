package com.example.cashgrantsmobile.Database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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

    public void insertScannedCashCard(String scannedCashCard,byte[] cc_image){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());

        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO CgList VALUES (NULL,?,?,?,?,?,?,0,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, "");
            statement.bindString(2, "");
            statement.bindString(3, "");
            statement.bindBlob(4, cc_image);
            statement.bindString(5,"");
            statement.bindString(6, scannedCashCard);
            statement.bindString(7, strDate);
            statement.executeInsert();
        }
        catch(Exception e){
            Log.v(TAG,e.toString());
        }
    }

    public void insertDefaultUser(String token, String user_id, String email, String mobile, String name, String username){
        try {
            SQLiteDatabase database = getWritableDatabase();
            String sql = "INSERT INTO Api VALUES (1,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, token);
            statement.bindString(2, user_id);
            statement.bindString(3, email);
            statement.bindString(4, mobile);
            statement.bindString(5, name);
            statement.bindString(6, username);
            statement.executeInsert();

            Log.v(TAG,"insertDefault");

        }
        catch(Exception e){
            Log.v(TAG,"insertDefault na error ");
        }
    }
    public void updateUser(String token, String user_id, String email, String mobile, String name, String username){
        try {
            SQLiteDatabase database = getWritableDatabase();

            String sql = "UPDATE Api SET token = ?,user_id=?, email=?,mobile=?,name=?,username=? WHERE id = 1";

            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, token);
            statement.bindString(2, user_id);
            statement.bindString(3, email);
            statement.bindString(4, mobile);
            statement.bindString(5, name);
            statement.bindString(6, username);
            statement.execute();
            database.close();
            Log.v(TAG,"success Token");
        }
        catch(Exception e){
            Log.v(TAG,"error Token");
            Log.v(TAG,e.toString());
        }
    }

    public void updateScannedCashCard(String scannedCashCard,byte[] cc_image){

        try {
            SQLiteDatabase database = getWritableDatabase();
//            String sql = "INSERT INTO CgList VALUES (NULL,?,?,?,?,?,?,0)";
            String sql = "UPDATE CgList SET cash_card_actual_no = ?,cc_image=? WHERE id = (SELECT max(id) FROM CGList) ";
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

    public void updateSubmitData(String cash_card_actual_no, String hh_number,String series_number, byte[] cc_image, byte[] id_image) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE CgList SET cash_card_actual_no = ?, hh_number = ?, series_number = ?, cc_image=?, id_image =?, card_scanning_status = 1  WHERE id = (SELECT max(id) FROM CGList)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, hh_number);
        statement.bindString(3, series_number);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.execute();
        database.close();
    }
    public void excludeData(int i, int status) {
        if (status == 0){status =1;}
        else {status =0;}
        int idd = i+1;
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE CgList SET card_scanning_status = ?,card_scanning_status = ? WHERE id = ? ";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindLong(1, 0);
        statement.bindLong(2, status);
        statement.bindLong(3, idd);
        statement.execute();
        database.close();
    }
    public void updateInventoryList(String cash_card_actual_no, String hh_number,String series_number, byte[] cc_image, byte[] id_image, int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE CgList SET cash_card_actual_no = ?, hh_number = ?, series_number = ?, cc_image=?, id_image =?, card_scanning_status = 1  WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, hh_number);
        statement.bindString(3, series_number);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.bindDouble(6, id);
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

                    Log.v(TAG,"Successfully inserted data!");

                }
                catch(Exception e){
                    Log.v(TAG,"Failed to insert Emv Data! = " + e );
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public  void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM CgList WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);

        statement.execute();
        database.close();
    }

    public void updateInventoryData(String cash_card, String hh_number,String series_number, byte[] cc_image, byte[] id_image, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CgList SET name = ?, price = ?, image = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, cash_card);
        statement.bindString(2, hh_number);
        statement.bindString(3, series_number);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.bindDouble(6, (double)id);
        statement.execute();
        database.close();
    }


    public void insertData(String cash_card_actual_no, String hh_number,String series_number ,byte[] cc_image,byte[] id_image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO CgList VALUES (1,?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, hh_number);
        statement.bindString(3, series_number);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.executeInsert();
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}