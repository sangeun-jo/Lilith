package sej.calendar.customcalendar.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import sej.calendar.customcalendar.GoogleCalendar;
import sej.calendar.customcalendar.R;
import sej.calendar.customcalendar.model.Memo;


//뷰모델로 바꾸기

public class AuthActivity extends GoogleCalendarActivity implements GoogleCalendarActivity.CalendarTaskListener{

    Button selectAccount;
    ImageButton selectCalendar;

    TextView googleAccount;
    TextView googleCalendar;

    String savedAccount;
    String savedCalendar;

    Handler handler;

    Realm realm;

    ArrayList<Memo> eventList = new ArrayList<>();
    List<String> calList = new ArrayList<>();

    GoogleCalendar googleTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        selectAccount = findViewById(R.id.btn_select_account);
        googleAccount = findViewById(R.id.google_account);
        selectCalendar = findViewById(R.id.btn_select_calendar);
        googleCalendar = findViewById(R.id.google_calendar);

        setCalendarTaskListener(this);

        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());

        String selectedCalendar = getSharedPreferences("pref", MODE_PRIVATE).getString("savedCalendar", "Please choose calendar");
        googleCalendar.setText(selectedCalendar);

        googleTask = GoogleCalendar.build(mCredential);

        GoogleAuthThread googleAuthThread = new GoogleAuthThread();
        googleAuthThread.start();

        selectAccount.setOnClickListener(v -> {
            setViewId(v.getId());
            chooseAccount();
        });

        selectCalendar.setOnClickListener(v -> {
            isCalendarTaskAvailable(v.getId());
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0:
                        showCalendarList(calList);
                }

            }
        };

    }

    @Override
    public void onAvailableCalendarTask(int viewId) {
        switch (viewId) {
            case R.id.btn_select_account:
                SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("savedAccount", mCredential.getSelectedAccountName());
                editor.putString("savedCalendar", "Please Choose Calendar");
                editor.apply();
                googleAccount.setText(mCredential.getSelectedAccountName()); //뷰 모델로 바꿔보자
                googleCalendar.setText("Please choose calendar");
                break;
            case R.id.btn_select_calendar:
                CalendarListThread calendarListThread = new CalendarListThread();
                calendarListThread.start();
                break;

        }
    }


    //달력 목록 보여주는 다이얼로그
    public void showCalendarList(List<String> calendarList) {
        if (calendarList.size() == 0) {
            Toast.makeText(this, "Calendar is not exist in this account", Toast.LENGTH_LONG);
            return ;
        }
        String[] calList = calendarList.stream().toArray(String[]::new);
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        oDialog.setTitle("Choose calendar")
                .setItems(calList, (dialog, which) -> {
                    SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("savedCalendar", calList[which]);
                    editor.apply();
                    googleCalendar.setText(calList[which]);
                    Toast.makeText(this, calList[which] + " selected", Toast.LENGTH_LONG).show();
                    //CalendarEventThread calendarEventThread = new CalendarEventThread(googleCalendar.getText().toString());
                    //calendarEventThread.start();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onErrorCalendarTask(String message) {
       //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    //이벤트 가져오는 핸들러
    class CalendarEventThread extends Thread {
        private String calTitle;
        public CalendarEventThread(String calTitle) {
            this.calTitle = calTitle;
        }
        @Override
        public void run() {
            try {
                String calendarId = googleTask.getCalendarID(calTitle);
                googleTask.getEventByDate(calendarId);
                setResult(2000);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } catch (UserRecoverableAuthException e) {

            }

        }
    }



    //달력 가져오는 핸들러
    class CalendarListThread extends Thread{
        @Override
        public void run() {
            try {
                calList = googleTask.getCalendarList();
            } catch (Exception e) {
                handleCommonThrowable(e);
            }
            handler.sendEmptyMessage(0);
        }
    }

    class GoogleAuthThread extends Thread {
        @Override
        public void run() {
            savedAccount = getSharedPreferences("pref", MODE_PRIVATE).getString("savedAccount", null);
            savedCalendar = getSharedPreferences("pref", MODE_PRIVATE).getString("savedCalendar", null);
            mCredential.setSelectedAccountName(savedAccount);

            if(savedAccount != null) {
                googleAccount.setText(savedAccount);
            } else {
                googleAccount.setText("Please choose account");
            }

            if(selectCalendar != null) {
                googleCalendar.setText(savedCalendar);
            } else {
                googleCalendar.setText("Please choose calendar");
            }
        }
    }




}

