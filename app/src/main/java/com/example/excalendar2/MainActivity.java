package com.example.excalendar2;



import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


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

    Calendar calendar;
    CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd(EEE)", Locale.getDefault()); //날짜 포맷 설정
    Date selectedDate; // 선택된 날짜

    private ArrayList<String> _13dayList = new ArrayList<>();
    private ArrayList<String> _12dayList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목

        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼

        gv = findViewById(R.id.gv_calendar);  //그리드 뷰



        //초기세팅
        calendar = Calendar.getInstance(); //현재 연월일시
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

    public void setSelectedDate(Date date){
        selectedDate = date;

        if(calendarAdapter != null){
            calendarAdapter.selectedData = date;
        }
    }

    //캘린더 그리는 함수
    @SuppressLint("SetTextI18n")
    public void drawCalendar(int year, int month) {

        ArrayList<String> _13dayList = new ArrayList<>();
        ArrayList<String> _12dayList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        int dayOfWeek;

        _13dayList.clear();
        _12dayList.clear();

        calendar.set(Calendar.YEAR, year); //입력받은 연도로 설정

        //1월 1일로 설정

        //올해부터 현재까지의 누적 날짜  % 28 로 세팅하자
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DATE, 0);
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); //1월 1일 요일 구하기

        //13월 달력 배열 만들기

        // 빈칸
        if (dayOfWeek == 1) { //만약 1월 1일이 일요일이면
            for (int i = 1; i <= 6; i++) {
                _13dayList.add(" ");
                _12dayList.add(" ");
            }

        } else {
            for (int i = 1; i < dayOfWeek; i++) {
                _13dayList.add(" ");
                _12dayList.add(" ");
            }
        }

        // 본 날짜
        if (month != 13) {
            for (int i = 1; i <= 28; i++) {
                _13dayList.add(i + "");
            }

        } else {

            if (year % 4 == 0) { //윤년이면
                for (int i = 1; i <= 30; i++) { //13월이 30일
                    _13dayList.add(i + "");
                }
            } else {
                for (int i = 1; i <= 29; i++) { //13월이 29일
                    _13dayList.add(i + "");
                }
            }
        }


        // 12월 달력 그리기
        for (int i = 1; i <= _13dayList.size(); i++) {

            CalendarConverter calConverter = new CalendarConverter();
            calendar = calConverter.convert13to12(year, month, i);

            //나중에 포멧 관련 정리 좀 하기
            String string12;
            int tempMonth = calendar.get(Calendar.MONTH)+1;
            string12 = tempMonth + "." + calendar.get(Calendar.DATE);
            _12dayList.add(string12);
        }


        // 연, 월 그리기
        tvCalendarTitle.setText(year + "년 " + month + "월");


        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(_13dayList, _12dayList, selectedDate);
        gv.setAdapter(calendarAdapter); //그리드 뷰에 어댑터 연결

        // 테스트
        TextView testView12 = findViewById(R.id.test12);
        TextView testView13 = findViewById(R.id.test13);

    }

    /*
    // 두가지 달력을 사전 형태로 만듦
    public Map getBothCalList(){
        Map <String,String> Cal25 =  new HashMap<String,String>();

        for (int i = 0; i <= _13dayList.size();i++){
            Cal25.put(_13dayList.get(i), _12dayList.get(i));
        }
        return Cal25;
    }
     */
}