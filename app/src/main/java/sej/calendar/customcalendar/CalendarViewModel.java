package sej.calendar.customcalendar;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import sej.calendar.customcalendar.model.DayInfo;
import sej.calendar.customcalendar.model.Memo;
import sej.calendar.customcalendar.ui.MainActivity;

public class CalendarViewModel extends ViewModel {
    Calendar calendar;
    CalendarConverter converter;
    ArrayList<DayInfo> calList;
    int curYear;
    int curMonth;

    public MutableLiveData<String> calendarHeader = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DayInfo>> calendarList = new MutableLiveData<>();
    public MutableLiveData<String> selectedDate = new MutableLiveData<>();

    public CalendarViewModel() {
        calendar = new GregorianCalendar();
        converter = new CalendarConverter(MainActivity.dayPerMonth);
        String today = converter.nToC(calendar);
        String[] Today = today.split("-");
        curYear = Integer.parseInt(Today[0]);
        curMonth = Integer.parseInt(Today[1]);
        setCalendarList(curYear, curMonth);
    }

    public void nextMonth() {
        if(curMonth == converter.MONTH_PER_YEAR){
            curYear += 1; curMonth = 1;
        } else { curMonth += 1; }
        setCalendarList(curYear, curMonth);
    }

    public void preMonth() {
        if(curMonth <= 1){
            curYear -= 1; curMonth = converter.MONTH_PER_YEAR;
        } else { curMonth -= 1; }
        setCalendarList(curYear, curMonth);
    }

    public void nextYear() {
        curYear += 1;
        setCalendarList(curYear, curMonth);
    }

    public void preYear() {
        curYear -= 1;
        setCalendarList(curYear, curMonth);
    }

    public ArrayList<DayInfo> getCalList() {
        return calList;
    }

    public void setCalendarList(int year, int month) {


        // 메모 불러오는 기능 어댑터에서 분리시키기

        //12월 기준으로 변환해서 데이터 불러오기(따로 함수로 만들기. 커스텀 달력 기준 년, 월 주면 12월 기준으로 변환하여
        // 구글 캘린더 api로 이벤트를 리스트로 얻어오기)
        // 불러온 메모 숫자셀에만 넣기


        calendarHeader.setValue(year + "." + month);

        calList = new ArrayList<>();
        calList.clear();

        converter.setYear(year);
        Calendar jen = Calendar.getInstance(Locale.getDefault());
        jen.set(year, 0, 1);
        int dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);

        DayInfo day;

        // 달력 앞 빈 칸
        if (dayOfWeek == 1) { //일
            for (int i = 0; i < 6; i++) {
                day = new DayInfo();
                calList.add(day);
            }
        } else { //월화수목금토
            for (int i = 0; i < dayOfWeek-2; i++) {
                day = new DayInfo();
                calList.add(day);
            }
        }
        // 숫자 쉘
        if (month != converter.MONTH_PER_YEAR) {
            for (int i = 1; i <= converter.DAY_PER_MONTH; i++) {
                day = new DayInfo();
                day.setDate(year, month ,i,true);
                calList.add(day);
            }
        } else { // 마지막 달
            for (int i = 1; i <= converter.LAST_MONTH_DAY; i++) {
                day = new DayInfo();
                day.setDate(year, month ,i,true);
                calList.add(day);
            }
        }


        // 달력 뒷부분 빈칸
        int lastWeek = 7 - calList.size() % 7;
        for(int i=0; i<lastWeek; i++) {
            day = new DayInfo();
            calList.add(day);
        }

        calendarList.setValue(calList);
    }
}
