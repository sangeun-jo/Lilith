package sej.calendar.customcalendar;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017-05-26.
 */

public class GoogleCalendar {

    private com.google.api.services.calendar.Calendar mService = null;

    private GoogleCalendar(com.google.api.services.calendar.Calendar mService) {
        this.mService = mService;
    }

    public static GoogleCalendar build(GoogleAccountCredential mCredential) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        com.google.api.services.calendar.Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, mCredential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();

        return new GoogleCalendar(mService);
    }

    public String getCalendarID(String calendarTitle) throws IOException {
        String id = null;

        // Iterate through entries in calendar list
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();
            for (CalendarListEntry calendarListEntry : items) {
                if ( calendarListEntry.getSummary().equals(calendarTitle)) {
                    id = calendarListEntry.getId();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        return id;
    }


    public List<String> getCalendarList() throws IOException, UserRecoverableAuthIOException{
        List<String> result = new ArrayList<>();
        String pageToken = null;
        do {
            CalendarList calendarList = null;
            calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();
            for(int i =0; i < items.size(); i++) {
                result.add(items.get(i).getSummary()) ;
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        return result;
    }


    private String createCalendar() throws IOException {
        String ids = getCalendarID("Custom Calendar");
        if ( ids != null ){
            return "Calendar already exist";
        }
        // 새로운 캘린더 생성
        com.google.api.services.calendar.model.Calendar calendar = new Calendar();
        // 캘린더의 제목 설정
        calendar.setSummary("Custom Calendar");
        // 캘린더의 시간대 설정
        //calendar.setTimeZone("Asia/Seoul");
        // 구글 캘린더에 새로 만든 캘린더를 추가
        Calendar createdCalendar = mService.calendars().insert(calendar).execute();
        // 추가한 캘린더의 ID를 가져옴.
        String calendarId = createdCalendar.getId();
        // 구글 캘린더의 캘린더 목록에서 새로 만든 캘린더를 검색
        CalendarListEntry calendarListEntry = mService.calendarList().get(calendarId).execute();
        // 캘린더의 배경색을 파란색으로 표시  RGB
        calendarListEntry.setBackgroundColor("#0000ff");
        // 변경한 내용을 구글 캘린더에 반영
        CalendarListEntry updatedCalendarListEntry =
                mService.calendarList()
                        .update(calendarListEntry.getId(), calendarListEntry)
                        .setColorRgbFormat(true)
                        .execute();
        // 새로 추가한 캘린더의 ID를 리턴
        return "Custom Calendar Created";
    }



}