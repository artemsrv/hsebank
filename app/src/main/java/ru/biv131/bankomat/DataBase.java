package ru.biv131.bankomat;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class DataBase extends SQLiteOpenHelper {

    public DataBase(Context context) {
        super(context, "DataBase7", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("databaselog", "Database created");
        db.execSQL("CREATE TABLE Cards("
                + "id integer primary key autoincrement,"
                + "number text,"
                + "password text,"
                + "balance real" + ");");
        db.execSQL("CREATE TABLE History("
                + "_id integer primary key autoincrement,"
                + "idcard integer,"
                + "action text,"
                + "time text" + ");");

        db.execSQL("INSERT INTO Cards values" +
                "('1','0000000000000000','1234','25700.74')," +
                "('2','0000000000000001','2016','98421');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    static public String getTime() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }
}