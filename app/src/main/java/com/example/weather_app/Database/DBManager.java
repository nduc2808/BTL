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
     * L???y ra c??c d??? li???u t??? database ????? add v??o locationModal
     * rawQuery : d??ng ????? truy v???n c?? s??? d??? li???u
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

    // Th???c hi???n x??a th??nh ph???
    public int deleteCity (String city)
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        return DB.delete("databaseCity", "cityALM = ?", new String[]{city});
    }

    // Ki???m tra tr??ng l???p
    public boolean checkAlready(String value) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT EXISTS (SELECT * FROM databaseCity WHERE cityALM='"+value+"' LIMIT 1)";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        // cursor.getInt (0) l?? 1 n???u c???t c?? gi?? tr??? t???n t???i
        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

}