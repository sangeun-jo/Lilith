package sej.calendar.customcalendar.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Collections;

import sej.calendar.customcalendar.R;

public class AuthActivity extends GoogleCalendarActivity implements GoogleCalendarActivity.CalendarTaskListener{

    Button selectAccount;
    Button addCalendar;
    Button createCalendar;

    ListView calendarListView;

    TextView googleAccount;
    String savedAccount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        selectAccount = findViewById(R.id.btn_select_account);
        googleAccount = findViewById(R.id.google_account);
        addCalendar = findViewById(R.id.add_calendar);
        createCalendar = findViewById(R.id.create_calendar);
        calendarListView = findViewById(R.id.calendar_list);


        setCalendarTaskListener(this);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());

        //선택된 구글 계정
        savedAccount = getPreferences(Context.MODE_PRIVATE).getString("savedAccount", null);
        mCredential.setSelectedAccountName(savedAccount);
        googleAccount.setText(savedAccount);


        selectAccount.setOnClickListener(v -> {
            setViewId(v.getId());
            chooseAccount();
        });

        createCalendar.setOnClickListener(v -> {
            isCalendarTaskAvailable(v.getId());
        });

    }


    @Override
    public void onAvailableCalendarTask(int viewId) {
        setCredential(mCredential);

        switch (viewId) {
            case R.id.btn_select_account:
                googleAccount.setText(mCredential.getSelectedAccountName());
                break;
            case R.id.create_calendar:
                //다이얼로그 띄우기
                break;

        }
    }

    @Override
    public void onErrorCalendarTask(String message) {

    }
}