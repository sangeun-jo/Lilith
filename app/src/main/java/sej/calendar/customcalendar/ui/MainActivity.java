package sej.calendar.customcalendar.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import sej.calendar.customcalendar.CalendarAdapter;
import sej.calendar.customcalendar.CalendarConverter;
import sej.calendar.customcalendar.model.DayInfo;
import sej.calendar.customcalendar.model.Memo;
import sej.calendar.customcalendar.R;

;


// 12월 달력을 기준으로 그리고, 밑에 조그맣게 정혈 주기 별 커스텀 달력으로 보이게 해야겠다.
// 옵션으로 정혈달력을 기준으로 사용할 수 있음
// 하단 바 있는 프레그먼트 레이아웃으로 만들기
// 메뉴-오늘, 정혈 달력, 통계, 설정
// 요일 레이블 추가, 시작 요일 선택에 따라 바뀌게 하기

//구

public class MainActivity extends AppCompatActivity {

    private int curYear; //13월 달력 기준 년
    private int curMonth; //13월 달력 기준 월
    private int dayPerMonth; //한달 일수
    private int currCell = -1;

    private String today;
    private String selectedDate; // 선택된 날짜. 13월 기준

    private GridView gridView;
    private TextView tvCalendarTitle;
    //private TextView tvSelectedDate;

    private CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    private CalendarConverter cCal;

    private SharedPreferences pref;
    private ArrayList<DayInfo> arrayListDayInfo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목
        //tvSelectedDate = findViewById(R.id.iv_selected);
        gridView = findViewById(R.id.gv_calendar);  //그리드 뷰
        gridView.setNumColumns(7);

        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼
        ImageButton btnPreviousCalendar2 = findViewById(R.id.btn_previous_calendar2); //이전년 버튼
        ImageButton btnNextCalendar2 = findViewById(R.id.btn_next_calendar2); // 다음년 버튼

        realmConfig();

        Calendar cal = Calendar.getInstance(Locale.getDefault());  //달력 객체

        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        dayPerMonth = pref.getInt("dayPerMonth", 28);

        cCal = new CalendarConverter(dayPerMonth);
        today = cCal.nToC(cal);
        selectedDate = today;  //현재 날짜로 변환

        setSelectedDate(selectedDate);

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
                String customDate = clickedDay.getDate();
                currCell = i;

                if(clickedDay.inMonth){
                    if(customDate.equals(selectedDate)) {
                        Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                        intent.putExtra("customDate", customDate);
                        intent.putExtra("date12", cCal.cToN(customDate));
                        startActivityForResult(intent, 1000);
                    }
                    setSelectedDate(customDate);
                }
                calendarAdapter.notifyDataSetChanged();
            }
        });

    }

    public void realmConfig(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                //.schemaVersion(1)
                //.migration(new Migration())
                .build();
        Realm.setDefaultConfiguration(config);
    }


    //액션바 옵션 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //오늘 날짜 클릭
        switch(item.getItemId()) {
            case R.id.select_date: //오늘 날짜버튼 클릭
                setTodayDate();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void setSelectedDate(String date){
        selectedDate = date;
        if(calendarAdapter != null){
            calendarAdapter.selectedDate = date;
        }
    }

    public void setTodayDate(){
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cCal = new CalendarConverter(dayPerMonth);
        today = cCal.nToC(cal);
        setSelectedDate(today);
        String Today[] = today.split("-");
        drawCalendar(Integer.parseInt(Today[0]), Integer.parseInt(Today[1]));  //오늘 날짜
        calendarAdapter.notifyDataSetChanged();
    }

    public void changeYM(int year, int month){
        tvCalendarTitle.setText(year + "." + month);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if (resultCode == 1001) {
                //Toast.makeText(this, "saved", Toast.LENGTH_LONG);
                calendarAdapter.notifyDataSetChanged();

            } else {
                //Toast.makeText(this, "deleted", Toast.LENGTH_LONG);
                drawCalendar(curYear, curMonth);
            }

        }
    }


    //13월 기준으로 13. 12 캘린더 그리는 함수
    public void drawCalendar(int year, int month) {
        arrayListDayInfo.clear();

        cCal.setYear(year);
        Calendar jen = Calendar.getInstance(Locale.getDefault());
        jen.set(year, 0, 1);
        int dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);

        DayInfo day;


        // 달력 앞 빈 칸
        if (dayOfWeek == 1) { //일
            for (int i = 0; i < 6; i++) {
                day = new DayInfo();
                arrayListDayInfo.add(day);
            }
        } else { //월화수목금토
            for (int i = 0; i < dayOfWeek-2; i++) {
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
        tvCalendarTitle.setText(year + "." + month);

        //그리드 뷰에 그리기
        calendarAdapter = new CalendarAdapter(selectedDate, arrayListDayInfo, dayPerMonth);
        gridView.setAdapter(calendarAdapter);
    }
}