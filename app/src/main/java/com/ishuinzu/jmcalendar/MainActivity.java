package com.ishuinzu.jmcalendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.ishuinzu.jmcalendar.JmDatabaseHelper;

public class MainActivity extends AppCompatActivity {
    private static int flag = 0;
    private static int updateFlag = 0;
    private TextView islamicDateView;
    private TextView solarDateView;
    Calendar calendar;
    private String date;
    private String month;
    private String year;
    private int hour24hrs;
    private int minutes;
    private int islamicDate;
    private int islamicMonth;
    private int islamicYear;
    private int lastUpdatedDate = 0;
    private int lastUpdatedMonth = 0;
    private int databaseHour = 0;
    private int databaseMinutes = 0;
    private int maximuimDaysOfMonth = 0;
    private SQLiteOpenHelper jmDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "onCreate()", Toast.LENGTH_SHORT).show();
        new MyAsyncTask().execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast toast = Toast.makeText(MainActivity.this, "onStart", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast = Toast.makeText(MainActivity.this, "onResume", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast toast = Toast.makeText(MainActivity.this, "onPause", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast toast = Toast.makeText(MainActivity.this, "onStop", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast toast = Toast.makeText(MainActivity.this, "onRestart", Toast.LENGTH_LONG);
        toast.show();
        new MyAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast toast = Toast.makeText(MainActivity.this, "onDestroy", Toast.LENGTH_LONG);
        toast.show();
    }

    public void updateIslamicCalendar(){
        SimpleDateFormat df = new SimpleDateFormat("dd");
        date = df.format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        month = sdf.format(new Date());
        SimpleDateFormat f = new SimpleDateFormat("YYYY");
        year = f.format(new Date());
        hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        while (lastUpdatedDate < (Integer.parseInt(date) - 1)) {
            nextIslamicDate();
            nextDate();
            updateFlag = 1;
        }
        if (updateFlag == 1){
            updateDatabase();
            updateFlag = 0;
        }
        if(lastUpdatedDate > Integer.parseInt(date) && lastUpdatedMonth <= Integer.parseInt(month)) {

            if (maximuimDaysOfMonth == lastUpdatedDate && Integer.parseInt(date) == 1) {
                if ((databaseHour == hour24hrs && databaseMinutes <= minutes) || (databaseHour < hour24hrs)) {
                    nextIslamicDate();
                    nextDate();
                    updateDatabase();
                }
            } else {
                nextIslamicDate();
                nextDate();
                updateDatabase();
            }
        } else if (lastUpdatedDate == (Integer.parseInt(date) - 1)) {
            if ((databaseHour == hour24hrs && databaseMinutes <= minutes) || (databaseHour < hour24hrs)) {
                nextIslamicDate();
                nextDate();
                updateDatabase();
            }
        }
    }

    public void updateViews() {
        islamicDateView.setText(String.format("%s - %s - %s", String.valueOf(islamicDate), getIslamicMonthName(islamicMonth), String.valueOf(islamicYear)));
        solarDateView.setText(String.format("%s - %s - %s", date, month, year));
    }

    public void readDatabaseData() {
        try {
            jmDatabaseHelper = new JmDatabaseHelper(MainActivity.this);
            SQLiteDatabase db = jmDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("CALENDAR_TABLE",
                    new String[]{"ISLAMIC_DATE", "ISLAMIC_MONTH", "ISLAMIC_YEAR", "DATE", "MONTH", "HOURS", "MINUTES"},
                    "_ID=?",
                    new String[]{Integer.toString(1)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                islamicDate = cursor.getInt(0);
                islamicMonth = cursor.getInt(1);
                islamicYear = cursor.getInt(2);
                lastUpdatedDate = cursor.getInt(3);
                lastUpdatedMonth = cursor.getInt(4);
                databaseHour = cursor.getInt(5);
                databaseMinutes = cursor.getInt(6);
                cursor.close();
                db.close();
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(MainActivity.this, "Datebase Unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void nextIslamicDate() {
        if (islamicDate < 29) {
            islamicDate++;
        } else if (islamicDate == 29) {
            if (hour24hrs >= 22 && flag==0 ) {
                islamicDate++;
            } else if (islamicDate == 30) {
                nextIslamicMonth();
            } else if (flag == 1 && ((databaseHour == hour24hrs && databaseMinutes <= minutes) || (databaseHour < hour24hrs))) {
                nextIslamicMonth();
                flag = 0;
            }
        }
    }

    public void nextDate() {
        maximuimDaysOfMonth = getMaximuimDaysOfMonth(lastUpdatedMonth, Integer.parseInt(year));
        if (lastUpdatedDate < maximuimDaysOfMonth) {
            lastUpdatedDate++;
        } else if (lastUpdatedDate == maximuimDaysOfMonth) {
            lastUpdatedDate = 1;
            if (lastUpdatedMonth == 12) {
                lastUpdatedMonth = 1;
            } else if (lastUpdatedMonth < 12) {
                lastUpdatedMonth++;
            }
        }
    }

    public void nextIslamicMonth() {
        islamicDate = 1;
        if (islamicMonth == 12) {
            islamicMonth = 1;
            islamicYear++;
        } else if (islamicMonth < 12) {
            islamicMonth++;
        }
    }

    public void updateDatabase() {
        try {
            SQLiteDatabase dbs = jmDatabaseHelper.getWritableDatabase();
            ContentValues CalendarValues = new ContentValues();
            CalendarValues.put("ISLAMIC_DATE", islamicDate);
            CalendarValues.put("ISLAMIC_MONTH", islamicMonth);
            CalendarValues.put("ISLAMIC_YEAR", islamicYear);
            CalendarValues.put("DATE", lastUpdatedDate);
            CalendarValues.put("MONTH", lastUpdatedMonth);
            CalendarValues.put("HOURS", 19);
            CalendarValues.put("MINUTES", 30);
            dbs.update("CALENDAR_TABLE",
                    CalendarValues,
                    "_ID=?",
                    new String[]{Integer.toString(1)});
            dbs.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MainActivity.this, "Datebase Unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public String getIslamicMonthName(int monthNumber) {
        switch (monthNumber) {
            case 1:
                return "Muḥarram";
            case 2:
                return "Safar";
            case 3:
                return "Rabīʿ al-Awwal";
            case 4:
                return "Rabi-us-Sani";
            case 5:
                return "Jamadi-ul-Awwal";
            case 6:
                return "Jammadi_us-Sani";
            case 7:
                return "Rajjab";
            case 8:
                return "Shaban";
            case 9:
                return "Ramzan";
            case 10:
                return "Shawwal";
            case 11:
                return "Zilquad";
            case 12:
                return "Zilhajj";
            default:
                return "invalid month";
        }
    }

    public int getMaximuimDaysOfMonth(int month, int currentYear) {
        switch (month) {
            case 1:
                return 31;
            case 2:
                if (currentYear % 4 == 0)
                    return 29;
                else
                    return 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                return 0;
        }
    }

    public void showChoiceDialog(View v) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("ARE YOU SURE!");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onClickDateChangeTo1Button();
            }
        });
        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing
            }
        });
        alertDialogBuilder.show();
    }

    public void onClickDateChangeTo1Button() {
        flag = 1;
        new MyCreateTask().execute();
    }

    public void onClickRefresfButton(View v) {
        new MyCreateTask().execute();
    }

    public void onClickResetButton(View view) {
        Intent intent = new Intent(this, ResetData.class);
        startActivity(intent);
    }

    class MyAsyncTask extends AsyncTask<Void,Void,Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            islamicDateView = findViewById(R.id.Islamic_date);
            solarDateView = findViewById(R.id.solar_date);
            calendar = Calendar.getInstance();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            SimpleDateFormat df = new SimpleDateFormat("dd");
            date = df.format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            month = sdf.format(new Date());
            SimpleDateFormat f = new SimpleDateFormat("YYYY");
            year = f.format(new Date());
            readDatabaseData();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            updateViews();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
    class MyCreateTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            updateIslamicCalendar();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            updateViews();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}