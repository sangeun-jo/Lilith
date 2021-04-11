package sej.calendar.customcalendar.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sej.calendar.customcalendar.GoogleCalendar;
import sej.calendar.customcalendar.R;

public class AuthActivity extends GoogleCalendarActivity implements GoogleCalendarActivity.CalendarTaskListener{

    Button selectAccount;
    ImageButton selectCalendar;

    TextView googleAccount;
    TextView googleCalendar;

    String savedAccount;
    String savedCalendar;


    List<String> calList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        selectAccount = findViewById(R.id.btn_select_account);
        googleAccount = findViewById(R.id.google_account);
        selectCalendar = findViewById(R.id.btn_select_calendar);
        googleCalendar = findViewById(R.id.google_calendar);

        setCalendarTaskListener(this);

        //구글 로그인
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());
        savedAccount = getPreferences(Context.MODE_PRIVATE).getString("savedAccount", null);
        savedCalendar = getPreferences(Context.MODE_PRIVATE).getString("savedCalendar", null);
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



        selectAccount.setOnClickListener(v -> {
            setViewId(v.getId());
            chooseAccount();
        });

        selectCalendar.setOnClickListener(v -> {
            isCalendarTaskAvailable(v.getId());
        });

    }


    //달력 가져오는 핸들러
    class CalendarListThread extends Thread{
        @Override
        public void run() {
            GoogleCalendar googleCalendar = GoogleCalendar.build(mCredential);
            try {
                calList = googleCalendar.getCalendarList();
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            }catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("달력 가져오기 완료!");
            // 메인에서 생성된 Handler 객체의 sendEmpryMessage 를 통해 Message 전달
            handler.sendEmptyMessage(0);
        }
    }

    //메인 스레드 핸들러
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    System.out.println("다중선택창 보여주기!");
                    showCalendarList(calList);

            }

        }
    };


    @Override
    public void onAvailableCalendarTask(int viewId) {
        System.out.println("현재 계정" +  mCredential.getSelectedAccountName());
        switch (viewId) {
            case R.id.btn_select_account:
                googleAccount.setText(mCredential.getSelectedAccountName());
                break;
            case R.id.btn_select_calendar:
                CalendarListThread calendarListThread = new CalendarListThread();
                calendarListThread.start();
                break;

        }
    }

    public void showCalendarList(List<String> calendarList) {
        if (calendarList.size() == 0) {
            Toast.makeText(this, "Calendar is not exist in this account", Toast.LENGTH_LONG);
            return ;
        }
        String[] calList = calendarList.stream().toArray(String[]::new);
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

        oDialog.setTitle("Choose calendar")
                .setItems(calList, (dialog, which) -> Toast.makeText(getApplicationContext(),
                        calList[which], Toast.LENGTH_LONG).show())
                .setCancelable(false)
                .show();
    }

    @Override
    public void onErrorCalendarTask(String message) {

    }

}

