package com.example.excalendar2;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//13월 기준 객체
public class DayInfo {
    private String date;
    public boolean inMonth = false;
    int y, m, d;

    public void setDate(String stringDate, boolean inMonth){
        String dayArray[] = stringDate.split("-");
        y = Integer.parseInt(dayArray[0]);
        m = Integer.parseInt(dayArray[1]);
        d = Integer.parseInt(dayArray[2]);
        this.date = stringDate;
        this.inMonth = inMonth;
    }

    public String getDay(){
        return Integer.toString(d);
    }

    public String get12Day(int stringType){
        @SuppressLint("SimpleDateFormat")
        CalendarConverter calendarConverter = new CalendarConverter();
        Calendar cal12 = calendarConverter.convert13to12(y,m,d);
        int year, mon, day;
        year = cal12.get(Calendar.YEAR)-2000;
        mon = cal12.get(Calendar.MONTH) + 1;
        day = cal12.get(Calendar.DATE);
        String date = "";
        if(stringType == 0){
            date =year+"-"+mon+"-"+day;
        } else if (stringType == 1){
            date = mon+"/"+day;
        }
        return date;
    }

    public String getDate(){
        return date;
    }

}
