package com.example.cashgrantsmobile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }
    public void insertData(String cash_card, String hh_number,String series_number ,byte[] cc_image,byte[] id_image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO CgList VALUES (NULL,?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, cash_card);
        statement.bindString(2, hh_number);
        statement.bindString(3, series_number);
        statement.bindBlob(4, cc_image);
        statement.bindBlob(5, id_image);
        statement.executeInsert();
    }

    public void insertDarkModeStatus(String status){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO DarkMode VALUES (NULL,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindString(1, status);
        statement.executeInsert();
    }

    public void updateData(String cash_card, String hh_number,String series_number, byte[] cc_image, byte[] id_image, int id) {
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
    public void updateDarkmodeStatus(String status, int id) {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "UPDATE DarkMode SET status = ? WHERE id = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1, status);
        statement.bindDouble(2, (double)id);
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

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}