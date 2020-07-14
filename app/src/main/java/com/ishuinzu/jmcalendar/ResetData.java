package com.ishuinzu.jmcalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ResetData extends AppCompatActivity {
    private Spinner IslamicDatespin;
    private Spinner IslamicMonthspin;
    private Spinner IslamicYearspin;
    private Spinner IslamicHourspin;
    private Spinner IslamicMinuteSpin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_data);

        String[] islamicDate = new String[30];
        for(int i=0;i<30;i++){
            islamicDate[i]=String.valueOf(i+1);
        }
        IslamicDatespin =  findViewById(R.id.Islamic_date_reset);
        ArrayAdapter<String> DateAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, islamicDate);
        DateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IslamicDatespin.setAdapter(DateAdapter);
        String[] islamicMonth = new String[12];
        for(int i=0;i<12;i++){
            islamicMonth[i]=String.valueOf(i+1);
        }
        IslamicMonthspin =  findViewById(R.id.Islamic_month_reset);
        ArrayAdapter<String> MonthAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,islamicMonth);
        MonthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IslamicMonthspin.setAdapter(MonthAdapter);
        String[] islamicYear = new String[12];
        for(int i=0;i<12;i++){
            islamicYear[i]=String.valueOf(1440+i);
        }
        IslamicYearspin =  findViewById(R.id.Islamic_year_reset);
        ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,islamicYear);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IslamicYearspin.setAdapter(YearAdapter);
        String[] last_update_Hour = new String[24];
        for(int i=0;i<24;i++){
            last_update_Hour[i]=String.valueOf(i+1);
        }
        IslamicHourspin =  findViewById(R.id.last_updated_hour);
        ArrayAdapter<String> HourAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,last_update_Hour);
        HourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IslamicHourspin.setAdapter(HourAdapter);
        String[] last_update_minutes = new String[60];
        for(int i=0;i<60;i++){
            last_update_minutes[i]=String.valueOf(i+1);
        }
        IslamicMinuteSpin =  findViewById(R.id.last_updated_minute);
        ArrayAdapter<String> MinuteAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item,last_update_minutes);
        MinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IslamicMinuteSpin.setAdapter(MinuteAdapter);
    }

    public void onClickUpdateButton(View view){
        try {
            Calendar calendar = Calendar.getInstance();
            int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            SimpleDateFormat df = new SimpleDateFormat("dd");
            String date = df.format(new Date());
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            String yesterdayAsString = df.format(calendar.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("MM");
            String month = sdf.format(new Date());
            SQLiteOpenHelper jmDatabaseHelper=new JmDatabaseHelper(this);
            SQLiteDatabase dbs = jmDatabaseHelper.getWritableDatabase();
            ContentValues CalendarValues = new ContentValues();
            CalendarValues.put("ISLAMIC_DATE",String.valueOf(IslamicDatespin.getSelectedItem()) );
            CalendarValues.put("ISLAMIC_MONTH", String.valueOf(IslamicMonthspin.getSelectedItem()));
            CalendarValues.put("ISLAMIC_YEAR", String.valueOf(IslamicYearspin.getSelectedItem()));
            if((Integer.parseInt(String.valueOf(IslamicHourspin.getSelectedItem()))==hour24hrs && minutes<Integer.parseInt(String.valueOf(IslamicMinuteSpin.getSelectedItem())))  || Integer.parseInt(String.valueOf(IslamicMinuteSpin.getSelectedItem()))<hour24hrs){
                CalendarValues.put("DATE",yesterdayAsString);
            }
            else{
                CalendarValues.put("DATE", date);
            }
            CalendarValues.put("MONTH", month);
            CalendarValues.put("HOURS",String.valueOf(IslamicHourspin.getSelectedItem()));
            CalendarValues.put("MINUTES", String.valueOf(IslamicMinuteSpin.getSelectedItem()));
            dbs.update("CALENDAR_TABLE",
                    CalendarValues,
                    "_ID=?",
                    new String[]{Integer.toString(1)});
            dbs.close();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Toast toast = Toast.makeText(ResetData.this, "Datebase Unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}