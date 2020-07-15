package com.example.excalendar2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DayInfo {
    private Date date;

    public String getDay(){
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        return sdf.format(date);
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public boolean isSameDay(Date date1){ //같은 날짜인지 다른 날짜인지 판별
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(this.date);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR); //같은 날짜면 true, 다른 날짜면 false

        return sameDay;
    }
}
