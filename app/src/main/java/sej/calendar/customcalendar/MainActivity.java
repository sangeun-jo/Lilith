package sej.calendar.customcalendar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import sej.calendar.customcalendar.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

;


// 12월 달력을 기준으로 그리고, 밑에 조그맣게 정혈 주기 별 커스텀 달력으로 보이게 해야겠다.
// 옵션으로 정혈달력을 기준으로 사용할 수 있음
// 하단 바 있는 프레그먼트 레이아웃으로 만들기
// 메뉴-오늘, 정혈 달력, 통계, 설정
// 요일 레이블 추가, 시작 요일 선택에 따라 바뀌게 하기

public class MainActivity extends AppCompatActivity {

    private int curYear; //13월 달력 기준 년
    private int curMonth; //13월 달력 기준 월
    private int dayPerMonth; //한달 일수
    private int currCell = -1;

    private String today;
    private String selectedDate; // 선택된 날짜. 13월 기준

    private GridView gridView;
    private TextView tvCalendarTitle;
    private TextView dateTitle;
    //private TextView tvSelectedDate;

    private CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    private CustomCalendar cCal;

    private SharedPreferences pref;
    private ArrayList<DayInfo> arrayListDayInfo = new ArrayList<>();

    private Realm realm;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tvCalendarTitle = findViewById(R.id.tv_calendar_title); //제목
        dateTitle = findViewById(R.id.memo_date);
        //tvSelectedDate = findViewById(R.id.iv_selected);
        gridView = findViewById(R.id.gv_calendar);  //그리드 뷰
        gridView.setNumColumns(7);

        ImageButton btnPreviousCalendar = findViewById(R.id.btn_previous_calendar); //이전달 버튼
        ImageButton btnNextCalendar = findViewById(R.id.btn_next_calendar); // 다음달 버튼
        ImageButton btnPreviousCalendar2 = findViewById(R.id.btn_previous_calendar2); //이전년 버튼
        ImageButton btnNextCalendar2 = findViewById(R.id.btn_next_calendar2); // 다음년 버튼


        Realm.init(this);

        Calendar cal = Calendar.getInstance(Locale.getDefault());  //달력 객체

        pref = getSharedPreferences("Pref", MODE_PRIVATE);
        dayPerMonth = pref.getInt("dayPerMonth", 28);

        cCal = new CustomCalendar(dayPerMonth);
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
                currCell = i;
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
            case R.id.select_date:
                setTodayDate();
                break;
            case R.id.change_n:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(et);
                builder.setMessage("Please enter a day between 14 and 180");
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et.getText().length() > 0){
                            dayPerMonth = Integer.parseInt(et.getText().toString()); //숫자 패드만 보이게 하기
                            if (14 <= dayPerMonth && dayPerMonth <= 180){
                                Toast.makeText(getApplicationContext(),"Please restart", Toast.LENGTH_LONG).show();
                                pref.edit().putInt("dayPerMonth", dayPerMonth).apply();
                            } else{
                                Toast.makeText(getApplicationContext(),"Wrong value", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                });
                AlertDialog alert = builder.create();
                alert.setTitle("Change day per month"); // dialog  Title
                alert.show();
                break;
            case R.id.add_memo:
                String c_date = today;
                if (currCell != -1) {
                    c_date = calendarAdapter.arrayListDayInfo.get(currCell).getDate();
                }
                showMemoDialog(c_date);
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
        cCal = new CustomCalendar(dayPerMonth);
        today = cCal.nToC(cal);
        setSelectedDate(today);
        String Today[] = today.split("-");
        drawCalendar(Integer.parseInt(Today[0]), Integer.parseInt(Today[1]));  //오늘 날짜
        calendarAdapter.notifyDataSetChanged();
    }

    public void changeYM(int year, int month){
        tvCalendarTitle.setText(year + "." + month);
    }

    public void showMemoDialog(final String memoTitle) {
        final String date = cCal.cToN(memoTitle);
        final MemoDialog dialog = new MemoDialog(this, memoTitle, date);
        dialog.setContentView(R.layout.memo_dialog);

        //에딧창 크기 조절
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams)params);
        realm = Realm.getDefaultInstance();
        // 기존 메모 있으면 불러오기
        dialog.setDialogListener(new MemoDialog.myListener() {
            @Override
            public void onSaveClicked(final String memoData) {
                Memo results = realm.where(Memo.class).equalTo("date", date).findFirst();
                realm.beginTransaction();
                if (results == null) {
                    if(memoData != null){
                        memo = realm.createObject(Memo.class);
                        memo.setDate(cCal.cToN(memoTitle));
                        memo.setContent(memoData);
                    } else{
                        Toast.makeText(getApplicationContext(), "empty value", Toast.LENGTH_LONG).show();
                    }
                } else{
                    results.setContent(memoData);
                    Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_LONG).show();
                }
                realm.commitTransaction();
                //setSelectedDate(date);
                calendarAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onDeleteClicked() {
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm) {
                        final RealmResults<Memo> results = realm.where(Memo.class).equalTo("date", date).findAll();
                        if (!results.isEmpty()) {
                            results.deleteAllFromRealm();
                        }
                        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();
                    }

                });
                drawCalendar(curYear, curMonth);
                dialog.dismiss();
            }
        });
        realm.close();
        dialog.show();
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
        calendarAdapter = new CalendarAdapter(this, selectedDate, arrayListDayInfo, dayPerMonth);
        gridView.setAdapter(calendarAdapter);
    }
}