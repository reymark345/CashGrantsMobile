package com.example.cashgrantsmobile.Database;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;

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
            String sql = "INSERT INTO CgList VALUES (NULL,?,?,?,?,?,?,0,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, "");
            statement.bindString(2, "");
            statement.bindString(3, "");
            statement.bindBlob(4, cc_image);
            statement.bindString(5,"");
            statement.bindString(6, scannedCashCard);
            statement.bindString(7, strDate);
            statement.bindString(8, "");
            statement.bindString(9, "");
            statement.bindString(10, "");
            statement.bindString(11, "");
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

    public void updateSubmitData(String cash_card_actual_no, String accomplishBy,String informant, byte[] cc_image, byte[] id_image, String attested) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE CgList SET cash_card_actual_no = ?, accomplish_by = ?, informant = ?, cc_image=?, id_image =?, attested =?, card_scanning_status = 1  WHERE id = (SELECT max(id) FROM CGList)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, accomplishBy);
        statement.bindString(3, informant);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.bindString(6, attested);
        statement.execute();
        database.close();
    }
    public void updateAccomplishSignature(int current_idd,String cash_card ,String accomplish,String informant,String attested ,byte[] signature) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE CgList SET  cash_card_actual_no =?,accomplish_img =?,accomplish_by = ?,informant = ?,attested = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindBlob(2, signature);
            statement.bindString(3, accomplish);
            statement.bindString(4, informant);
            statement.bindString(5, attested);
            statement.bindLong(6, current_idd);
            statement.execute();
            database.close();
            Log.v(TAG,"ni updates");
        }
        catch (Exception e){
            Log.v(TAG,"wala ni update "+e);
        }
    }
    public void updateInformantSignature(int current_idd,String cash_card,String accomplish,String informant,String attested ,byte[] signature) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE CgList SET cash_card_actual_no = ?,informant_image =?,accomplish_by = ?,informant = ?,attested = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindBlob(2, signature);
            statement.bindString(3, accomplish);
            statement.bindString(4, informant);
            statement.bindString(5, attested);
            statement.bindLong(6, current_idd);
            statement.execute();
            database.close();
        }
        catch (Exception e){
            Log.v(TAG,"Error "+e);
        }
    }

    public void updateAttestedSignature(int current_idd,String cash_card,String accomplish,String informant,String attested ,byte[] signature) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE CgList SET cash_card_actual_no =?,attested_img =?,accomplish_by = ?,informant = ?,attested = ? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindString(1, cash_card);
            statement.bindBlob(2, signature);
            statement.bindString(3, accomplish);
            statement.bindString(4, informant);
            statement.bindString(5, attested);
            statement.bindLong(6, current_idd);
            statement.execute();
            database.close();
            Log.v(TAG,"ni updates");
        }
        catch (Exception e){
            Log.v(TAG,"wala ni update "+e);
        }
    }
    public void updateGrantee(int current_idd,byte[] grantee) {
        try {

            SQLiteDatabase database = getWritableDatabase();
            String sql = "UPDATE CgList SET id_image =? WHERE id = ?";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.bindBlob(1, grantee);
            statement.bindLong(2, current_idd);
            statement.execute();
            database.close();
            Log.v(TAG,"ni update ang grantee");
        }
        catch (Exception e){
            Log.v(TAG,"wala ni update grantee "+e);
        }
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
    public void updateInventoryList(String cash_card_actual_no, String accomplishBy,String informant, byte[] cc_image, byte[] id_image, int id, String attested) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE CgList SET cash_card_actual_no = ?, accomplish_by = ?, informant = ?, cc_image=?, id_image =?, attested=? , card_scanning_status = 1  WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, cash_card_actual_no);
        statement.bindString(2, accomplishBy);
        statement.bindString(3, informant);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.bindString(6, attested);
        statement.bindDouble(7, id);
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

    public  void deleteAccess() {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "DELETE FROM Api";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.execute();
        database.close();
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

    public void updateInventoryData(String cash_card, String accomplishBy,String series_number, byte[] cc_image, byte[] id_image, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE CgList SET name = ?, price = ?, image = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, cash_card);
        statement.bindString(2, accomplishBy);
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