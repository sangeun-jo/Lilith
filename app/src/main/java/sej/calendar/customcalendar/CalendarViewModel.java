package sej.calendar.customcalendar;

import android.content.Context;
import android.content.res.Resources;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import io.realm.Realm;
import sej.calendar.customcalendar.model.DayView;
import sej.calendar.customcalendar.model.Memo;
import sej.calendar.customcalendar.ui.GoogleCalendarActivity;
import sej.calendar.customcalendar.ui.MainActivity;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class CalendarViewModel extends ViewModel {
    private CalendarConverter converter;
    private ArrayList<DayView> calList;
    private ArrayList<Memo> eventList;
    private Realm realm;
    private int curYear;
    private int curMonth;

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
        realm = Realm.getDefaultInstance();
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
            calList.add(day);
        }
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



    class CalendarEventThread extends Thread {
        private Calendar start;
        private Calendar end;
        public CalendarEventThread(){
            //this.start = start;
            //this.end = end;
        }
        @Override
        public void run() {
            try {
                String calendarId = googleTask.getCalendarID(savedCalendar);
                eventList = googleTask.getEventByDate(calendarId);
                for(Memo m:eventList) {
                    System.out.print(m.getDate());
                    System.out.print(m.getTitle());
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } catch (UserRecoverableAuthException e) {

            }
        }
    }

    //그리는 월의 커스텀 캘린더 메모 리스트 불러오기
    private ArrayList<Memo> getMemoList(Calendar start, Calendar end)  {
        ArrayList<Memo> normalDateList = new ArrayList<>();
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
        System.out.println("선택된 달력: " + savedCalendar);
        if(savedCalendar != null ){
            //ayns 어쩌구 써보기
            CalendarEventThread c = new CalendarEventThread();
            c.start();
        }
            while (start.getTimeInMillis() <= end.getTimeInMillis()) {
                // 커스텀 캘린더에서 메모 불러오기
                String date = ymd.format(start.getTime());
                Memo result = realm.where(Memo.class).equalTo("date", date).findFirst();
                if (result == null) { //없으면 날짜만 넣기
                    Memo memo = new Memo();
                    memo.setDate(date);
                    normalDateList.add(memo);
                } else { // 있으면 기존 메모 넣기
                    normalDateList.add(result);
                }
                start.add(Calendar.DATE,1);
            }


        return normalDateList;
    }
    // 지금 연동된 계정이 있다면, 연동된 계정의 이벤트 리스트 가져오기
}
