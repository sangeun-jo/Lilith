package com.example.excalendar2;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;


//어플이름은 릴리스
//메인화면 만들기
//날짜 선택 기능 만들기
//건강 정보 페이지 만들기

public class MainActivity extends AppCompatActivity {

    public int curYear;
    public int curMonth;
    public GridView gv;
    public TextView tvCalendarTitle;
    private TextView tvSelectedDate;

    private ArrayList<DayInfo> arrayListDayInfo = new ArrayList<>();

    CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    CalendarConverter calConverter = new CalendarConverter();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd(EEE)", Locale.getDefault()); //날짜 포맷 설정
    Calendar selectedDate; // 선택된 날짜



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
        int temp = calendar.get(Calendar.MONTH)+1;

        selectedDate = calConverter.convert12to13( //오늘날자로 세팅
                calendar.get(Calendar.YEAR), temp, calendar.get(Calendar.DATE));

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

        /*
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 날짜 중에 아무거나 선택하면
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSelectedDate(((String)view.getTag()));
                CalendarAdapter.notifyDataSetChanged();
            }
        });
         */
    }

    public void setSelectedDate(Calendar date){
        selectedDate = date;

        if(calendarAdapter != null){
            calendarAdapter.selectedDate = date;
        }
    }

    //캘린더 그리는 함수
    @SuppressLint("SetTextI18n")
    public void drawCalendar(int year, int month) {

        int dayOfWeek;

        arrayListDayInfo.clear();

        //입력 받은 년도 1월 1일 요일 구하기
        Calendar jen = new GregorianCalendar(year, 0, 1);
        dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);


        DayInfo day;
        String date;

        // 달력 앞 빈 쉘 처리(day 객체에는 null 값 들어감)
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
        if (month != 13) {
            for (int i = 1; i <= 28; i++) {
                day = new DayInfo();
                date = year+"-"+month+"-"+i;
                day.setDate(date,true);
                arrayListDayInfo.add(day);
            }
        } else {

            if (year % 4 == 0) { //윤년이면
                for (int i = 1; i <= 30; i++) { //13월이 30일
                    day = new DayInfo();
                    date = year+"-"+month+"-"+i;
                    day.setDate(date, true);
                    arrayListDayInfo.add(day);
                }
            } else {
                for (int i = 1; i <= 29; i++) { //13월이 29일
                    day = new DayInfo();
                    date = year+"-"+month+"-"+i;
                    day.setDate(date, true);
                    arrayListDayInfo.add(day);
                }
            }
        }

/*
        // 12월 달력 그리기
        for (int i = 1; i <= _13dayList.size(); i++) {
            Calendar calendar = calConverter.convert13to12(year, month, i);

            //나중에 포멧 관련 정리 좀 하기
            String string12;
            int tempMonth = calendar.get(Calendar.MONTH)+1;
            string12 = tempMonth + "." + calendar.get(Calendar.DATE);
            _12dayList.add(string12);
        }
*/
        // 연, 월 그리기
        tvCalendarTitle.setText(year + "년 " + month + "월");

        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(arrayListDayInfo);
        gv.setAdapter(calendarAdapter); //그리드 뷰에 어댑터 연결


        /* 테스트
        TextView testView12 = findViewById(R.id.test12);
        TextView testView13 = findViewById(R.id.test13);
        CalendarConverter calendarConverter = new CalendarConverter();
        Calendar cal = calendarConverter.convert12to13(2020, 9, 9);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //String result = df.format(cal);
        int tMonth = cal.get(Calendar.MONTH) + 1;
        String result = cal.get(Calendar.YEAR) + "-" +  tMonth + "-" + cal.get(Calendar.DATE);
        testView12.setText("2020-09-09");
        testView13.setText(result);
        */
    }
}