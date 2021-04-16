package sej.calendar.customcalendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.realm.Realm;
import sej.calendar.customcalendar.model.DayView;
import sej.calendar.customcalendar.model.Memo;
import sej.calendar.customcalendar.ui.MainActivity;

public class CalendarViewModel extends ViewModel {
    private CalendarConverter converter;
    private ArrayList<DayView> calList;
    private Realm realm;
    private int curYear;
    private int curMonth;

    public MutableLiveData<String> calendarHeader = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DayView>> calendarList = new MutableLiveData<>();


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

    public ArrayList<DayView> getCalList() {
        return calList;
    }


    public void setCalendarList() {
        // 메모 불러오는 기능 어댑터에서 분리시키기

        //12월 기준으로 변환해서 데이터 불러오기(따로 함수로 만들기. 커스텀 달력 기준 년, 월 주면 12월 기준으로 변환하여
        // 구글 캘린더 api로 이벤트를 리스트로 얻어오기)
        // 불러온 메모 숫자셀에만 넣기
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
                DayView day = new DayView(true);
                //DayInfo day = new DayInfo();
                calList.add(day);
            }
        } else { //월화수목금토
            for (int i = 0; i < dayOfWeek-2; i++) {
                DayView day = new DayView(true);
                //DayInfo day = new DayInfo();
                calList.add(day);
            }
        }
    }


    // 숫자 쉘 채우기
    private void setNumberDate() {
        int dayOfMonth;

        System.out.println("===========이제부터 " + curMonth + "월 데이터를 불러오겠습니다============");
        //마지막 달 아님
        if (curMonth != converter.MONTH_PER_YEAR) {
            dayOfMonth = converter.DAY_PER_MONTH;
        } else { // 마지막 달
            dayOfMonth = converter.LAST_MONTH_DAY;
        }
        Calendar start = converter.cToN(curYear + "-" + curMonth + "-" + 1);
        Calendar end = converter.cToN(curYear + "-" + curMonth + "-" + dayOfMonth);

        ArrayList<Memo> memoList  = getMemoList(start, end);
        for (int i = 0; i < dayOfMonth; i++) {
            DayView day = new DayView(false);
            day.setCustomDate(curYear, curMonth, i+1);
            String[] date = memoList.get(i).getDate().split("-");
            day.setNormalDate(date[1] +"/" + date[2], memoList.get(i));
            System.out.println(memoList.get(i).getDate() + ":" + memoList.get(i).getContent());
            calList.add(day);
        }

        System.out.println("===================메모 리스트 불러오기 끗===================");
    }


    //달력 뒤 빈칸 채우기
    private void setBackEmptyDate() {
        int lastWeek = 7 - calList.size() % 7;
        for(int i=0; i<lastWeek; i++) {
            DayView day = new DayView(true);
            //DayInfo day = new DayInfo();
            calList.add(day);
        }
    }

    //그리는 월의 메모 리스트 불러오기
    private ArrayList<Memo> getMemoList(Calendar start, Calendar end) {
        ArrayList<Memo> normalDateList = new ArrayList<>();
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        //SimpleDateFormat md = new SimpleDateFormat("MM/dd", Locale.getDefault());
        while (start.getTimeInMillis() <= end.getTimeInMillis()) {
            // 커스텀 캘린더에서 메모 불러오기
            String date = ymd.format(start.getTime());
            Memo result = realm.where(Memo.class).equalTo("date", date).findFirst();
            System.out.println(date  + "불러온 메모: " + result.getContent());

            if (result == null) { //없으면 만들기
                realm.beginTransaction();
                Memo memo = realm.createObject(Memo.class);
                memo.setDate(date);
                normalDateList.add(memo);
                realm.commitTransaction();
            } else { // 있으면 기존 메모 넣기
                normalDateList.add(result);
            }
            start.add(Calendar.DATE,1);
        }
        return normalDateList;
    }



}
