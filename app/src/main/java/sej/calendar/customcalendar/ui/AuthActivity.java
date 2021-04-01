package sej.calendar.customcalendar.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import sej.calendar.customcalendar.R;

public class AuthActivity extends Activity implements EasyPermissions.PermissionCallbacks {

    protected GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());

        Button addCalendar = findViewById(R.id.btn_calendar_add);
        addCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run(){
                        if (mCredential.getSelectedAccountName() == null) {
                            chooseAccount();
                        } else {
                            createCalendar();
                        }
                    }
                }.start();

            }
        });

    }


    //권한 필요 시 호출
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            /*String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);*/
            /*if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }*/

            startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER); //계정 선택
        } else { //구글계정 접근 권한 묻기
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    // 권한 선택 후 결과보여주기
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES: //구글 플레이 서비스 필요 시
                if (resultCode != RESULT_OK) { //구글 플레이 스토어 설치 안됨
                    System.out.println("구글 플레이 스토어 설치 안됨!");
                } else {
                    System.out.println("구글 플레이스토어 설치 됨!");
                }
                break;
            case REQUEST_ACCOUNT_PICKER: //계정 선택하게 보여주기
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        /*SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();*/


                        mCredential.setSelectedAccountName(accountName);
                        System.out.println("계정 얻음!!! ");
                        //할일
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    //권한 얻음
                    System.out.println("사용자가 앱에 권한 주는거 동의함");
                }
                else {
                    System.out.println("사용자가 동의 안함");
                }
                break;
        }
    }


    public void createCalendar(){
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        // Initialize Calendar service with valid OAuth credentials
        Calendar service = new Calendar.Builder(httpTransport, jsonFactory, mCredential).setApplicationName("applicationName").build();

        // Create a new calendar
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("calendarSummary");
        calendar.setTimeZone("America/Los_Angeles");

        // Insert the new calendar
        com.google.api.services.calendar.model.Calendar createdCalendar = null; //model
        try {
            createdCalendar = service.calendars().insert(calendar).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(createdCalendar.getId());
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}