package com.example.excalendar2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


// 캘린더 먼스 햇갈릴때
// 넣을 때 -1, 출력할 때 + 1

public class CalendarConverter {


    public CalendarConverter(){
    }

    //13월에서 12월로 바꾸기
    public Calendar convert13to12(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();
        ArrayList<Integer> dList = getDifference(year);

        int d;

        if(month == 13){ // 13월
            // 현재 날짜를 내년 1월 day 로 설정
            calendar.set(Calendar.YEAR, year+1);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DATE, day);
            d = dList.get(12);

        } else { // 2~12월달

            calendar.set(Calendar.YEAR, year); //2021
            calendar.set(Calendar.MONTH, month-1); //들어오는 값이 2일때 , 1이 되므로 2월달로 세팅이 됩
            calendar.set(Calendar.DATE, day); //1
            d = dList.get(month-1); //첫번째 값을 받음
        }
        calendar.add(Calendar.DATE, -d); //현재 날짜에서 3일 뻄

        return calendar;
    }

    public ArrayList<Integer> getDifference(int year){

        int d = 0;
        int monthMax;
        ArrayList<Integer> dList;
        Calendar calendar = Calendar.getInstance();;

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


