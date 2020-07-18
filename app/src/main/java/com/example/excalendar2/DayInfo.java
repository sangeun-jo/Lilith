package com.example.excalendar2;

import android.annotation.SuppressLint;

import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

//13월 기준 객체
public class DayInfo {
    private String date;
    int y, m, d;
    public boolean inMonth = false;

    public void setDate(int year, int month, int day, boolean inMonth){
        y = year; m = month; d = day;
        this.date = year + "-" +month + "-" + day;
        this.inMonth = inMonth;
    }

    public String getDay(){
        return Integer.toString(d);
    }

    //12월 날짜로 변환해서 Calendar 객체로 반환
    public Calendar get12DayCal(){
        CalendarConverter calendarConverter = new CalendarConverter();
        Calendar cal12 = calendarConverter.convert13to12(y,m,d);
        return cal12;
    }

    public String getDate(){
        return this.date;
    }

    // 같은 날짜면 true 다른 날짜면 false 반환
    public boolean isSameDay(String date1){
        boolean sameDay = date1.equals(this.date);
        return sameDay;
    }


}
