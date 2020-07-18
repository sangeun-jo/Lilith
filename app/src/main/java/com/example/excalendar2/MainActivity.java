package com.example.excalendar2;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;;
import java.util.Locale;


//날짜 -> 스트링
//스트링 -> 날짜 변환해 주는 도구 하나 있어야 겠다.

public class MainActivity extends AppCompatActivity {

    public int curYear; //13월 달력 기준 년
    public int curMonth; //13월 달력 기준 월
    public GridView gridView;
    public TextView tvCalendarTitle;
    private TextView tvSelectedDate;

    private ArrayList<DayInfo> arrayListDayInfo = new ArrayList<>();

    CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    CalendarConverter calConverter = new CalendarConverter();
    String selectedDate; // 선택된 날짜. 13월 기준
    String today;
    Calendar mThisMonthCalendar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목
        tvSelectedDate = findViewById(R.id.iv_selected);
        gridView = findViewById(R.id.gv_calendar);  //그리드 뷰

        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼
        ImageButton btnPreviousCalendar2 = findViewById(R.id.btn_previous_calendar2); //이전년 버튼
        ImageButton btnNextCalendar2 = findViewById(R.id.btn_next_calendar2); // 다음년 버튼

        //초기세팅
        onResume();
        selectedDate = today;

        //월 이동 버튼
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

        //년 이동 버튼
        btnPreviousCalendar2.setOnClickListener(new View.OnClickListener(){ // 이전달 버튼이 눌리면
            @Override
            public void onClick(View view) {
                curYear -= 1;
                drawCalendar(curYear, curMonth);
            }
        });
        btnNextCalendar2.setOnClickListener(new View.OnClickListener(){ //다음달 버튼 눌리면
            @Override
            public void onClick(View view) {
                curYear += 1;
                drawCalendar(curYear, curMonth);
            }
        });

        // 날짜 클릭 시
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 날짜 중에 아무거나 선택하면
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DayInfo clickedDay = (DayInfo)view.getTag();
                if(clickedDay.inMonth){
                    setSelectedDate(clickedDay.getDate());
                } else{
                    return;
                }
                calendarAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setSelectedDate(String date){
        selectedDate = date;
        if(calendarAdapter != null){
            calendarAdapter.selectedDate = date;
        }
    }

    // 새로 고침 함수 오늘날짜 세팅해줌
    @Override
    public void onResume() {
        super.onResume();
        mThisMonthCalendar = Calendar.getInstance(Locale.KOREA);  //달력 객체
        today = calConverter.convert12to13(mThisMonthCalendar); //현재 날짜로 변환

        String[] Today = today.split("-");

        curYear = Integer.parseInt(Today[0]);
        curMonth = Integer.parseInt(Today[1]);

        drawCalendar(curYear, curMonth);  //오늘 날짜
    }

    //13월 기준으로 13. 12 캘린더 그리는 함수
    @SuppressLint("SetTextI18n")
    public void drawCalendar(int year, int month) {

        int dayOfWeek;
        arrayListDayInfo.clear();

        Calendar jen = Calendar.getInstance(Locale.KOREA);
        jen.set(year, 0, 1);
        dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);

        DayInfo day;

        // 달력 앞 빈 쉘 처리
        if (dayOfWeek == 1) {
            for (int i = 0; i < 6; i++) {
               day = new DayInfo();
               arrayListDayInfo.add(day);
            }
        } else {
            for (int i = 0; i < dayOfWeek-2; i++) {
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        }

        // 숫자 쉘 그리기
        if (month != 13) { // 1~12월
            for (int i = 1; i <= 28; i++) {
                day = new DayInfo();
                day.setDate(year, month ,i,true);
                arrayListDayInfo.add(day);
            }
        } else { // 13월
            if (year % 4 == 0) { //윤년
                for (int i = 1; i <= 30; i++) { //13월이 30일
                    day = new DayInfo();
                    day.setDate(year, month ,i,true);
                    arrayListDayInfo.add(day);
                }
            } else {
                for (int i = 1; i <= 29; i++) {
                    day = new DayInfo();
                    day.setDate(year, month ,i,true);
                    arrayListDayInfo.add(day);
                }
            }
        }

        // 달력 뒷부분 그리기
        if (dayOfWeek == 1) {
            day = new DayInfo();
            arrayListDayInfo.add(day);
        } else {
            for (int i = 0; i < 9-dayOfWeek; i++) {
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        }

        // 연, 월 그리기
        tvCalendarTitle.setText(year + "년 " + month + "월");

        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(selectedDate, arrayListDayInfo);
        gridView.setAdapter(calendarAdapter);
    }
}