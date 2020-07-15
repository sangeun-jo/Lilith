package com.example.excalendar2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


//어플이름은 릴리스
//일요일 맨 마지막으로 바꾸고 로직 수정
//다이얼 방식으로 바꾸기
//메인화면 만들기
//날짜 선택 기능 만들기
//건강 정보 페이지 만들기

public class MainActivity extends AppCompatActivity {

    public int curYear;
    public int curMonth;
    public GridView gv;
    public TextView tvCalendarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목
        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼
        gv = findViewById(R.id.gv_calendar);  //그리드 뷰

        //초기세팅
        Calendar calendar = Calendar.getInstance(); //현재 연월일시
        curYear = calendar.get(Calendar.YEAR); //현재 년도

        drawCalendar(curYear, 1);  //현재 년도 1월

        btnPreviousCalendar.setOnClickListener(new View.OnClickListener(){ // 이전달 버튼이 눌리면
            @Override
            public void onClick(View view) {
                if(curMonth <= 1){
                    curYear -= 1; curMonth = 13;
                } else { curMonth -= 1; }
                drawCalendar(curYear, curMonth);
            }
        });

        btnNextCalendar.setOnClickListener(new View.OnClickListener(){ //다음달 버튼 눌리면
            @Override
            public void onClick(View view) {
                if(curMonth == 13){
                    curYear += 1; curMonth = 1;
                } else { curMonth += 1; }
                drawCalendar(curYear, curMonth);
            }
        });

    }

    //캘린더 그리는 함수
    public void drawCalendar(int year, int month){

        ArrayList<String> _13dayList = new ArrayList<>();
        ArrayList<String> _12dayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        int dayOfWeek;

        _13dayList.clear();
        _12dayList.clear();

        calendar.set(Calendar.YEAR, year); //입력받은 연도로 설정

        //1월 1일로 설정
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 0);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); //1월 1일 요일 구하기


        //13월 달력 배열 만들기

        // 빈칸
        if(dayOfWeek == 1){ //만약 1월 1일이 일요일이면
            for(int i=1;i<=6;i++){
                _13dayList.add(" ");
                _12dayList.add(" ");
            }

        } else{
            for(int i=1; i < dayOfWeek;i++){
                _13dayList.add(" ");
                _12dayList.add(" ");
            }
        }

        // 본 날짜
        if(month != 13){
            for(int i = 1; i<=28;i++){
                _13dayList.add(i+"");
            }

        } else {

            if(year % 4 == 0){ //윤년이면
                for(int i = 1; i<=30;i++){ //13월이 30일
                    _13dayList.add(i+"");
                }
            } else {
                for(int i = 1; i<=29;i++){ //13월이 29일
                    _13dayList.add(i+"");
                }
            }
        }


        // 12월 달력 그리기
        for(int i = 1; i <= _13dayList.size(); i++){
            String string12;
            calendar = convert13to12(year, month, i);
            int myMonth = calendar.get(Calendar.MONTH) + 1;
            string12 = myMonth  + "."+ calendar.get(Calendar.DATE);
            _12dayList.add(string12);
        }

        // 연, 월 그리기
        tvCalendarTitle.setText(year+"년 "+month+"월");

        //그리드 뷰에 그리기
        CalendarAdapter calendarAdapter = new CalendarAdapter(_13dayList, _12dayList);
        gv.setAdapter(calendarAdapter); //그리드 뷰에 어댑터 연결
    }


    //다시 해야함
    public Calendar convert13to12(int year, int month, int day){

        Calendar calendar = Calendar.getInstance();
        ArrayList<Integer> dlist = getD(year);

        int d;

        if(month == 13){
            // 현재 날짜를 내년 1월 day 로 설정
            calendar.set(Calendar.YEAR, year + 1);
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DATE, day);

            d = dlist.get(12);

        } else { // 1~12월달

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month-1);
            calendar.set(Calendar.DATE, day);

            d = dlist.get(month-1);
        }
        calendar.add(Calendar.DATE, -d);

        return calendar;
    }

    public ArrayList<Integer> getD(int year){
        int d = 0;
        int monthMax;
        ArrayList<Integer> dlist;
        Calendar calendar = Calendar.getInstance();;

        calendar.set(Calendar.YEAR, year); //입력 받은 년도로 설정하기

        dlist = new ArrayList<>();
        dlist.add(0);

        for(int i = 2; i <= 13; i++){
            calendar.set(Calendar.MONTH, i-2);
            monthMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); //이전 달 마지막 날짜수
            if(monthMax == 28) d = 0;
            else if(monthMax == 29) d = d + 1;
            else if(monthMax == 30) d = d + 2;
            else if(monthMax == 31) d = d + 3;
            dlist.add(d);
        }
        return dlist;
    }


}