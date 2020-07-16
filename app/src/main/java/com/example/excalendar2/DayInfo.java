package com.example.excalendar2;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Locale;

public class DayInfo {
    private LocalDate date12;
    private LocalDate date13;

    public String get12Day(){
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        return sdf.format(date12);
    }

    public String get13Day(){
        SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
        return sdf.format(date13);
    }

    public LocalDate getDate12(){
        return date12;
    }

    public LocalDate getDate13(){
        return date13;
    }

    public void setDate(LocalDate date12, LocalDate date13){
        this.date12 = date12;
        this.date13 = date13;
    }

    public boolean isSameDay(LocalDate date12){ //같은 날짜인지 다른 날짜인지 판별
        boolean sameDay;
        sameDay = date12 == this.date12; // 같은 날짜면 true, 다른 날짜면 false
        return sameDay;
    }
}
