package com.example.excalendar2;

import android.annotation.SuppressLint;
import java.util.Calendar;
import java.util.Date;

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

    //12월 날짜로 변환해서 반환
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
            date =year+"-"+mon+"-"+day;  // yy/mm/dd 형태
        } else if (stringType == 1){
            date = mon+"/"+day; // mm/dd 형태
        }
        return date;
    }

    public String getDate(){
        return this.date;
    }

    // 같은 날짜면 true 다른 날짜면 false 반환
    public boolean isSameDay(Calendar date1){


        boolean sameDay = date1.get(Calendar.YEAR) == y &&
                date1.get(Calendar.MONTH)+1 == m &&
                date1.get(Calendar.DATE) == d;
        return sameDay;
    }


}
