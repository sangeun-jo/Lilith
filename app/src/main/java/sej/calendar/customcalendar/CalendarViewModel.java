package sej.calendar.customcalendar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.realm.Realm;
import sej.calendar.customcalendar.model.DayView;
import sej.calendar.customcalendar.model.Memo;

public class CalendarViewModel extends ViewModel {
    private CalendarConverter converter;
    private ArrayList<DayView> calList;
    private HashMap<String, Memo> eventList;
    private Realm realm;
    private int curYear;
    private int curMonth;
    int dayOfMonth;

    private GoogleAccountCredential credential;
    private String savedAccount;
    private String savedCalendar;
    private GoogleCalendar googleTask;

    public MutableLiveData<String> calendarHeader = new MutableLiveData<>();
    public MutableLiveData<ArrayList<DayView>> calendarList = new MutableLiveData<>();


    public CalendarViewModel() { //뷰모델 초기화 시 값 넘겨주는 거 제대로 배우기!
    }

    public void setBefore(
            GoogleAccountCredential credential,
            String savedAccount, String savedCalendar,
            CalendarConverter converter) {
        this.credential = credential;
        this.savedAccount = savedAccount;
        this.savedCalendar = savedCalendar;
        this.converter = converter;
        credential.setSelectedAccountName(savedAccount);
        googleTask = GoogleCalendar.build(credential);
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

    public HashMap<String, Memo> getEventList() {
        return eventList;
    }


    public void setCalendarList() {
        calendarHeader.setValue(curYear + "." +curMonth);

        calList = new ArrayList<>();
        calList.clear();

        setFrontEmptyDate();

        //한달 일수 얻어오기(함수화하기)
        //마지막 달 아님
        if (curMonth != converter.MONTH_PER_YEAR) {
            dayOfMonth = converter.DAY_PER_MONTH;
        } else { // 마지막 달
            dayOfMonth = converter.LAST_MONTH_DAY;
        }

        Calendar start = converter.cToN(curYear, curMonth, 1);
        Calendar end = converter.cToN(curYear, curMonth, dayOfMonth);

        getCalListThread getCalListThread = new getCalListThread(start, end);
        getCalListThread.start();

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
                calList.add(day);
            }
        }
    }


    //달력 뒤 빈칸 채우기
    private void setBackEmptyDate() {
        int lastWeek = 7 - calList.size() % 7;
        for(int i=0; i<lastWeek; i++) {
            DayView day = new DayView(true);
            calList.add(day);
        }
    }

    
    class getCalListThread extends Thread {
        Calendar start;
        Calendar end;

        public getCalListThread(Calendar start, Calendar end){
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            eventList = new HashMap<>();
            ArrayList<Memo> normalDateList = new ArrayList<>();
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());

            if(savedCalendar != null ){
                String calendarId = null;
                try {
                    calendarId = googleTask.getCalendarID(savedCalendar);
                    eventList = googleTask.getEventByDate(calendarId, new DateTime(start.getTime()), new DateTime(end.getTime()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UserRecoverableAuthException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            System.out.println("이벤트 리스트 불러옴!!! " + eventList.size());

            while (start.getTimeInMillis() <= end.getTimeInMillis()) {
                realm = Realm.getDefaultInstance();
                // 커스텀 캘린더에서 메모 불러오기
                String date = ymd.format(start.getTime());
                Memo memo = new Memo();
                memo.setDate(date);

                // 이벤트 있으면 추가
                if (eventList.size() > 0) {
                    if (eventList.get(date) != null) {
                        System.out.println(eventList.get(date).getTitle() + "/" + date);
                        System.out.println("이벤트 있음!!! ");
                        System.out.print(eventList.get(date).getTitle());
                        System.out.print(eventList.get(date).getContent());
                        memo.setTitle(eventList.get(date).getTitle());
                    }
                }
                //리얼님 있으면 추가
                Memo result = realm.where(Memo.class).equalTo("date", date).findFirst();
                if (result != null) { // 리얼님 메모 있으면 추가
                    memo.setTitle("혼합됨!!!");
                    memo.setContent(result.getContent()  + "\n" + eventList.get(date).getContent());
                }
                normalDateList.add(memo);
                start.add(Calendar.DATE,1);
            }


            for (int i = 0; i < dayOfMonth; i++) {
                DayView day = new DayView(false);
                day.setCustomDate(curYear, curMonth, i+1);
                String[] date = normalDateList.get(i).getDate().split("-");
                day.setNormalDate(date[1] +"/" + date[2], normalDateList.get(i));
                calList.add(day);
            }

            setBackEmptyDate();
            calendarList.postValue(calList);
        }
    }
}
