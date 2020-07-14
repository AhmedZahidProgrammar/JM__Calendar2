package com.ishuinzu.jmcalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
public class JmDatabaseHelper extends SQLiteOpenHelper {
    private static  final String  DB_NAME = "JmDatabase";
    private static int VERSION = 1;

    public JmDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE CALENDAR_TABLE("+"_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"ISLAMIC_DATE INTEGER, "
                +"ISLAMIC_MONTH INTEGER, "
                +"ISLAMIC_YEAR INTEGER, "
                +"DATE INTEGER, "
                +"MONTH INTEGER, "
                +"HOURS INTEGER, "
                +"MINUTES INTEGER);");
        ContentValues CalendarValues = new ContentValues();
        CalendarValues.put("ISLAMIC_DATE", 19);
        CalendarValues.put("ISLAMIC_MONTH", 11);
        CalendarValues.put("ISLAMIC_YEAR", 1441);
        CalendarValues.put("DATE", 11);
        CalendarValues.put("MONTH", 7);
        CalendarValues.put("HOURS", 19);
        CalendarValues.put("MINUTES", 30);
        db.insert("CALENDAR_TABLE", null, CalendarValues);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}