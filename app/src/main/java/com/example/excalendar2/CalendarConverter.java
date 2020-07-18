package com.example.excalendar2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


// 캘린더 먼스 햇갈릴때
// 넣을 때 -1, 출력할 때 + 1

public class CalendarConverter {

    public CalendarConverter(){}

    // 12월에서 13월로 바꾸기
    public String convert12to13(Calendar today){

        //1월부터 누적날짜 구하기
        Calendar jen = Calendar.getInstance(Locale.KOREA);
        jen.set(today.get(Calendar.YEAR), 0, 1);

        long diffSec = ( today.getTimeInMillis() -jen.getTimeInMillis())/1000;
        long diffDays = diffSec / (24*60*60) + 1;

        //변환하기
        int m = (int) diffDays / 28 + 1;
        int d = (int) diffDays % 28 + 1;

        String converted = today.get(Calendar.YEAR)+"-" + m + "-" + d;

        return converted;
    }


    //13월에서 12월로 바꾸기
    public Calendar convert13to12(int year, int month, int day){

        Calendar calendar;
        ArrayList<Integer> dList = getDifference(year);

        int d;

        if(month == 13){ // 13월
            calendar = Calendar.getInstance(Locale.KOREA);
            calendar.set(year+1,0,day);
            d = dList.get(12);

        } else { // 1~12월
            calendar = Calendar.getInstance(Locale.KOREA);
            calendar.set(year, month-1, day);
            d = dList.get(month-1);
        }
        calendar.add(Calendar.DATE, -d);

        return calendar;
    }

    public ArrayList<Integer> getDifference(int year){

        int d = 0;
        int monthMax;

        ArrayList<Integer> dList;
        Calendar calendar = Calendar.getInstance(Locale.KOREA);;

        calendar.set(Calendar.YEAR, year); //입력 받은 년도로 설정하기

        dList = new ArrayList<>();
        dList.add(0);

        for(int i = 2; i <= 13; i++){
            calendar.set(Calendar.MONTH, i-2);
            monthMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            if(monthMax == 28) d = 3;
            else if(monthMax == 29) d = d + 1;
            else if(monthMax == 30) d = d + 2;
            else if(monthMax == 31) d = d + 3;
            dList.add(d);
        }
        return dList;
    }

}


