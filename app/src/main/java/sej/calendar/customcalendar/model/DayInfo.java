package sej.calendar.customcalendar.model;

import java.util.Calendar;

import sej.calendar.customcalendar.CalendarConverter;


//커스텀 달력 기준

//12월 달력도 가지고 
public class DayInfo {
    private String date;
    int y, m, d;
    public boolean inMonth = false; //비어있는 날짜인지 판단

    public void setDate(int year, int month, int day, boolean inMonth){
        y = year; m = month; d = day;
        this.date = year + "-" +month + "-" + day;
        this.inMonth = inMonth;
    }

    public String getDay(){
        return Integer.toString(d);
    }

    public String getDate(){
        return this.date;
    }

    // 같은 날짜면 true 다른 날짜면 false 반환
    public boolean isSameDay(String date1){
        boolean sameDay = date1.equals(this.date);
        return sameDay;
    }

    /*
    public void setDate12(String date12) {
        this.date12 = date12
    }
    * */

}
