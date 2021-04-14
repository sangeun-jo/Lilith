package sej.calendar.customcalendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import sej.calendar.customcalendar.model.DayInfo;
import sej.calendar.customcalendar.ui.MainActivity;

public class CalendarViewModel extends ViewModel {
    private CalendarConverter converter;
    private ArrayList<DayInfo> calList;
    private Realm realm;
    private int curYear;
    private int curMonth;

    public MutableLiveData<String> calendarHeader = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DayInfo>> calendarList = new MutableLiveData<>();


    public CalendarViewModel() {
        realm = Realm.getDefaultInstance();
        converter = new CalendarConverter(MainActivity.dayPerMonth);
        setToday();
    }


    public void nextMonth() {
        if(curMonth == converter.MONTH_PER_YEAR){
            curYear += 1; curMonth = 1;
        } else { curMonth += 1; }
        setCalendarList();
    }

    public void preMonth() {
        if(curMonth <= 1){
            curYear -= 1; curMonth = converter.MONTH_PER_YEAR;
        } else { curMonth -= 1; }
        setCalendarList();
    }

    public void nextYear() {
        curYear += 1;
        setCalendarList();
    }

    public void preYear() {
        curYear -= 1;
        setCalendarList();
    }


    public void setToday() {
        String today = converter.nToC(Calendar.getInstance());
        String[] Today = today.split("-");
        curYear = Integer.parseInt(Today[0]);
        curMonth = Integer.parseInt(Today[1]);
        setCalendarList();
    }

    public ArrayList<DayInfo> getCalList() {
        return calList;
    }

    /*
    public List<String> getNormalDateList(){

    }
     */

    public void setCalendarList() {
        // 메모 불러오는 기능 어댑터에서 분리시키기

        //12월 기준으로 변환해서 데이터 불러오기(따로 함수로 만들기. 커스텀 달력 기준 년, 월 주면 12월 기준으로 변환하여
        // 구글 캘린더 api로 이벤트를 리스트로 얻어오기)
        // 불러온 메모 숫자셀에만 넣기

        String start;
        String end;
        //RealmResults<Memo> result = realm.where(Memo.class).between("date", start, end).findAll();

        calendarHeader.setValue(curYear + "." +curMonth);

        calList = new ArrayList<>();
        calList.clear();

        setFrontEmptyDate();
        setNumberDate();
        setBackEmptyDate();

        calendarList.setValue(calList);
    }


    // 달력 앞 빈 칸
    private void setFrontEmptyDate() {
        Calendar jen = Calendar.getInstance();
        jen.set(curYear, 0, 1);
        int dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) { //일
            for (int i = 0; i < 6; i++) {
                DayInfo day = new DayInfo();
                calList.add(day);
            }
        } else { //월화수목금토
            for (int i = 0; i < dayOfWeek-2; i++) {
                DayInfo day = new DayInfo();
                calList.add(day);
            }
        }
    }

    // 숫자 쉘 채우기
    private void setNumberDate() {
        if (curMonth != converter.MONTH_PER_YEAR) {
            for (int i = 1; i <= converter.DAY_PER_MONTH; i++) {
                DayInfo day = new DayInfo();
                day.setDate(curYear, curMonth, i,true);
                calList.add(day);
            }
        } else { // 마지막 달
            for (int i = 1; i <= converter.LAST_MONTH_DAY; i++) {
                DayInfo day = new DayInfo();
                day.setDate(curYear, curMonth ,i,true);
                calList.add(day);
            }
        }
    }

    //달력 뒤 빈칸 채우기
    private void setBackEmptyDate() {
        int lastWeek = 7 - calList.size() % 7;
        for(int i=0; i<lastWeek; i++) {
            DayInfo day = new DayInfo();
            calList.add(day);
        }
    }
}
