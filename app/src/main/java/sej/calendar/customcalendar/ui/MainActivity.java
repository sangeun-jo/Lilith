package sej.calendar.customcalendar.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import sej.calendar.customcalendar.CalendarAdapter;
import sej.calendar.customcalendar.CalendarConverter;
import sej.calendar.customcalendar.CalendarViewModel;
import sej.calendar.customcalendar.databinding.ActivityMainBinding;
import sej.calendar.customcalendar.model.DayInfo;
import sej.calendar.customcalendar.R;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


// 12월 달력을 기준으로 그리고, 밑에 조그맣게 정혈 주기 별 커스텀 달력으로 보이게 해야겠다.
// 옵션으로 정혈달력을 기준으로 사용할 수 있음
// 하단 바 있는 프레그먼트 레이아웃으로 만들기
// 메뉴-오늘, 정혈 달력, 통계, 설정
// 요일 레이블 추가, 시작 요일 선택에 따라 바뀌게 하기

public class MainActivity extends AppCompatActivity {

    public static int dayPerMonth; //한달 일수

    private String today;
    private CalendarAdapter calendarAdapter; //캘린더 어댑터 객체
    private CalendarConverter converter;
    private ActivityMainBinding binding;
    private CalendarViewModel calendarViewModel;

    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);

        binding.gvCalendar.setNumColumns(7);

        realmConfig();

        SharedPreferences pref = getSharedPreferences("Pref", MODE_PRIVATE);
        dayPerMonth = pref.getInt("dayPerMonth", 28);

        converter = new CalendarConverter(dayPerMonth);

        today = converter.nToC(Calendar.getInstance(Locale.getDefault()));

        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);
        binding.setCalendarViewModel(calendarViewModel);

        calendarAdapter = new CalendarAdapter(binding.getCalendarViewModel().getCalList(), today, converter);
        binding.gvCalendar.setAdapter(calendarAdapter);

        binding.gvCalendar.setOnItemClickListener((adapterView, view, i, l) -> {
            DayInfo clickedDay = (DayInfo)view.getTag();
            String customDate = clickedDay.getDate();
            if(clickedDay.inMonth){
                if(customDate.equals(calendarAdapter.getSelectedDate())) {
                    Intent intent = new Intent(getApplicationContext(), MemoActivity.class);
                    intent.putExtra("customDate", customDate);
                    intent.putExtra("date12", ymd.format(converter.cToN(customDate).getTime()));
                    startActivityForResult(intent, 1000);
                }
                calendarAdapter.setSelectedDate(customDate);
                calendarAdapter.notifyDataSetChanged();
            }
        });
        observe();

    }

    private void observe() {
        binding.getCalendarViewModel().calendarList.observe(this, new Observer<ArrayList<DayInfo>>() {
            @Override
            public void onChanged(ArrayList<DayInfo> objects) {
                calendarAdapter.setCalList(binding.getCalendarViewModel().getCalList());
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
                calendarAdapter.setSelectedDate(today);
                calendarAdapter.notifyDataSetChanged();
                break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if (resultCode == 1001) {
                Toast.makeText(this, "saved", Toast.LENGTH_LONG).show();
            } else if (resultCode == 1002) {
                Toast.makeText(this, "deleted", Toast.LENGTH_LONG).show();
            }
            calendarAdapter.notifyDataSetChanged();
        }
    }
}