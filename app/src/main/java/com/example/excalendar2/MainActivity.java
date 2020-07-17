package com.example.excalendar2;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;;
import java.util.Locale;


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
    Calendar selectedDate; // 선택된 날짜
    Calendar mThisMonthCalendar;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목
        tvSelectedDate = findViewById(R.id.iv_selected);
        gv = findViewById(R.id.gv_calendar);  //그리드 뷰
        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼


        //초기세팅
        Calendar calendar = Calendar.getInstance(Locale.KOREA); //현재 연월일시
        curYear = calendar.get(Calendar.YEAR); //현재 년도
        curMonth = calendar.get(Calendar.MONTH)+1;


        selectedDate = calConverter.convert12to13( //오늘날짜를 13월로 바꾸어서 세팅
                curYear, curMonth, calendar.get(Calendar.DATE));


        drawCalendar(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH)+1);  //현재 년도 1월

        //버튼

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

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 날짜 중에 아무거나 선택하면
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DayInfo clickedDay = (DayInfo)view.getTag();
                if(clickedDay.inMonth){
                    setSelectedDate(clickedDay.get13DayCal());
                } else{
                    return;
                }
                calendarAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"그리드 뷰가 눌렸습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setSelectedDate(Calendar date){
        selectedDate = date;

        if(calendarAdapter != null){
            calendarAdapter.selectedDate = date;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mThisMonthCalendar = Calendar.getInstance(Locale.KOREA);  //달력 객체
        Calendar cal = calConverter.convert12to13(mThisMonthCalendar.get(Calendar.YEAR), mThisMonthCalendar.get(Calendar.MONTH)+1, mThisMonthCalendar.get(Calendar.DATE));
        drawCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1);  //오늘 날짜
        //drawCalendar(mThisMonthCalendar.get(Calendar.YEAR), mThisMonthCalendar.get(Calendar.MONTH)+1);
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
        String date;

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
                date = year+"-"+month+"-"+i;
                day.setDate(date,true);
                arrayListDayInfo.add(day);
            }
        } else { // 13월
            if (year % 4 == 0) { //윤년
                for (int i = 1; i <= 30; i++) { //13월이 30일
                    day = new DayInfo();
                    date = year+"-"+month+"-"+i;
                    day.setDate(date, true);
                    arrayListDayInfo.add(day);
                }
            } else {
                for (int i = 1; i <= 29; i++) {
                    day = new DayInfo();
                    date = year+"-"+month+"-"+i;
                    day.setDate(date, true);
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
        gv.setAdapter(calendarAdapter); //그리드 뷰에 어댑터 연결

    }
}