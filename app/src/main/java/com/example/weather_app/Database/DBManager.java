package com.example.weather_app.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.weather_app.Add_Location.AddLocationModal;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "databaseCity.db";
    private static final int 	DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE_TABLE_CITY = "create table databaseCity "
            + "(id integer primary key autoincrement,"
            + "cityALM text not null,"
            + "tempMaxALM text not null,"
            + "tempMinALM text not null,"
            + "tempCurrentALM text not null);";


    public DBManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_TABLE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBManager.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS databaseCity");
        onCreate(db);
    }

    public long addCity(String city, String tMax, String tMin, String tCurrent) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (checkAlready(city)) {
            return -1;
        } else {
            values.put("cityALM", city);
            values.put("tempMaxALM", tMax);
            values.put("tempMinALM", tMin);
            values.put("tempCurrentALM", tCurrent);
            return database.insert("databaseCity", null, values);
        }
    }

    /**
     * Lấy ra các dữ liệu từ database để add vào locationModal
     * rawQuery : dùng để truy vấn cơ sở dữ liệu
     * @return
     */
    public ArrayList<AddLocationModal> getAll() {
        SQLiteDatabase database = this.getWritableDatabase();
        ArrayList<AddLocationModal> databaseCity = new ArrayList<AddLocationModal>();
        Cursor cursor = database.rawQuery("SELECT * FROM databaseCity", null);

        if (cursor.moveToFirst()) {
            do {
                String city = cursor.getString(1);
                String tMax = cursor.getString(2);
                String tMin = cursor.getString(3);
                String curr = cursor.getString(4);

                AddLocationModal modal = new AddLocationModal(city, tMax, tMin, curr);
                databaseCity.add(modal);
            } while (cursor.moveToNext());
        }
        return databaseCity;
    }

    // Thực hiện xóa thành phố
    public int deleteCity (String city)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.delete("databaseCity", "cityALM = ?", new String[]{city});
    }

    // Kiểm tra trùng lặp
    public boolean checkAlready(String value) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT EXISTS (SELECT * FROM databaseCity WHERE cityALM='"+value+"' LIMIT 1)";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        // cursor.getInt (0) là 1 nếu cột có giá trị tồn tại
        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

}