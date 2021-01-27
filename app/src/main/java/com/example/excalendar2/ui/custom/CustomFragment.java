package com.example.excalendar2.ui.custom;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.excalendar2.CalendarAdapter;
import com.example.excalendar2.CustomCalendar;
import com.example.excalendar2.DayInfo;
import com.example.excalendar2.R;
import com.example.excalendar2.ui.normal.NormalViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomFragment extends Fragment {

    public int curYear; //13월 달력 기준 년
    public int curMonth; //13월 달력 기준 월
    public GridView gridView;
    public TextView tvCalendarTitle;
    private TextView tvSelectedDate;
    private int weekMode = 2;

    private ArrayList<DayInfo> arrayListDayInfo = new ArrayList<>();

    CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    CustomCalendar cCal;
    String selectedDate; // 선택된 날짜. 13월 기준

    SharedPreferences prefs;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_normal, container, false);

        prefs = getActivity().getSharedPreferences("Pref", getActivity().MODE_PRIVATE);

        tvCalendarTitle = root.findViewById(R.id.tv_calendar_title); //제목
        tvSelectedDate = root.findViewById(R.id.iv_selected);
        gridView = root.findViewById(R.id.gv_calendar);  //그리드 뷰
        gridView.setNumColumns(cCal.DAY_PER_WEEK);

        ImageButton btnPreviousCalendar = root.findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = root.findViewById(R.id.btn_next_calendar); // 다음달 버튼
        ImageButton btnPreviousCalendar2 = root.findViewById(R.id.btn_previous_calendar2); //이전년 버튼
        ImageButton btnNextCalendar2 = root.findViewById(R.id.btn_next_calendar2); // 다음년 버튼
        //ImageButton btnToday = findViewById(R.id.btn_today);


        cCal = new CustomCalendar();

        Calendar cal = Calendar.getInstance(Locale.KOREA);  //달력 객체
        selectedDate = cCal.nToC(cal); //현재 날짜로 변환

        String[] Today = selectedDate.split("-");
        curYear = Integer.parseInt(Today[0]);
        curMonth = Integer.parseInt(Today[1]);

        drawCalendar(curYear, curMonth);  //오늘 날짜

        //월 이동 버튼
        btnPreviousCalendar.setOnClickListener(new View.OnClickListener(){ // 이전달 버튼이 눌리면
            @Override
            public void onClick(View view) {
                if(curMonth <= 1){
                    curYear -= 1; curMonth = cCal.MONTH_PER_YEAR;
                } else { curMonth -= 1; }
                changeYM(curYear, curMonth);
                drawCalendar(curYear, curMonth);
            }
        });

        btnNextCalendar.setOnClickListener(new View.OnClickListener(){ //다음달 버튼 눌리면
            @Override
            public void onClick(View view) {
                if(curMonth == cCal.MONTH_PER_YEAR){
                    curYear += 1; curMonth = 1;
                } else { curMonth += 1; }
                changeYM(curYear, curMonth);
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

        // 날짜 쉘 클릭 시
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

        return root;
    }


    // 점 세개
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.select_date:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeYM(int year, int month){
        tvCalendarTitle.setText(year + "년 " + month + "월");
    }


    public void setSelectedDate(String date){
        selectedDate = date;
        if(calendarAdapter != null){
            calendarAdapter.selectedDate = date;
        }
    }


    //13월 기준으로 13. 12 캘린더 그리는 함수
    public void drawCustomCalendar(int year, int month) {
        arrayListDayInfo.clear();

        Calendar jen = Calendar.getInstance(Locale.KOREA);
        jen.set(year, 0, 1);
        int dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);

        DayInfo day;

        // 달력 앞 빈 칸
        if (dayOfWeek == 1) { //일
            for (int i = 0; i < 7 - (weekMode-1); i++) {
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        } else { //월화수목금토
            for (int i = 0; i < dayOfWeek-weekMode; i++) { //7-2
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        }

        // 숫자 쉘
        if (month != cCal.MONTH_PER_YEAR) {
            for (int i = 1; i <= cCal.DAY_PER_MONTH; i++) {
                day = new DayInfo();
                day.setDate(year, month ,i,true);
                arrayListDayInfo.add(day);
            }
        } else { // 마지막 달
            for (int i = 1; i <= cCal.LAST_MONTH_DAY; i++) {
                day = new DayInfo();
                day.setDate(year, month ,i,true);
                arrayListDayInfo.add(day);
            }
        }

        // 달력 뒷부분 빈칸
        int lastWeek = 7 - arrayListDayInfo.size() % 7;
        for(int i=0; i<lastWeek; i++) {
            day = new DayInfo();
            arrayListDayInfo.add(day);
        }

        // 연, 월 그리기
        tvCalendarTitle.setText(year + "년 " + month + "월");

        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(selectedDate, arrayListDayInfo);
        gridView.setAdapter(calendarAdapter);
    }



    //12월 기준으로 12.13 캘린더 그리는 함수
    public void drawCalendar(int year, int month) {
        arrayListDayInfo.clear();

        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.set(year, month-1, 1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); //요일
        int end = cal.getActualMaximum(Calendar.DATE);

        DayInfo day;

        // 달력 앞 빈 칸
        if(weekMode == 1){ //일요일부터 한 주 시작
            for(int i = 1; i<dayOfWeek;i++){
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        } else{ //월요일부터 한 주 시작
            if(dayOfWeek == 1){
                for(int i = 0; i<6;i++){
                    day = new DayInfo();
                    arrayListDayInfo.add(day);
                }
            } else{
                for(int i = 0; i<dayOfWeek-weekMode;i++){
                    day = new DayInfo();
                    arrayListDayInfo.add(day);
                }
            }
        }

        // 숫자 쉘
        for(int i =1; i<=end; i++){
            day = new DayInfo();
            day.setDate(year, month ,i,true);
            arrayListDayInfo.add(day);
        }

        // 달력 뒷부분 빈칸

        for(int i=0; i<7 -end%7 ; i++) {
            day = new DayInfo();
            arrayListDayInfo.add(day);
        }
        // 연, 월 그리기
        tvCalendarTitle.setText(year + "년 " + month + "월");

        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(selectedDate, arrayListDayInfo);
        gridView.setAdapter(calendarAdapter);
    }
}
