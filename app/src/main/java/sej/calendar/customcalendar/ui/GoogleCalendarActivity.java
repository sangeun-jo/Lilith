package sej.calendar.customcalendar.ui;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class GoogleCalendarActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private int viewId;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    CalendarTaskListener calendarTaskListener = null;

    public interface CalendarTaskListener {
        void onAvailableCalendarTask(int viewId);
        void onErrorCalendarTask(String message);
    }

    public void setCalendarTaskListener(CalendarTaskListener calendarTaskListener) {
        this.calendarTaskListener = calendarTaskListener;
    }

    //이 액티비티를 상속해서 사용하는 액티비티는 인증정보를 생성해서 써야함
    GoogleAccountCredential mCredential = null;

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }

    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    if(calendarTaskListener != null)
                        calendarTaskListener.onErrorCalendarTask("This app requires Google Play Services. Please install " +
                                "Google Play Services on your device and relaunch this app.");
                } else {
                    if(calendarTaskListener != null)
                        isCalendarTaskAvailable(viewId);
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        mCredential.setSelectedAccountName(accountName);
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("savedAccount", accountName);
                        editor.apply();
                        if (calendarTaskListener != null) {
                            isCalendarTaskAvailable(viewId);
                        }
                    }


                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    if(calendarTaskListener != null)
                        isCalendarTaskAvailable(viewId);
                }
                else {
                    if(calendarTaskListener != null)
                        calendarTaskListener.onErrorCalendarTask("권한 설정을 허용해야 이용 가능합니다!");
                }
                break;
        }
    }


    //캘린더 작업이 가능한지 조건 따지는 애
    protected final void isCalendarTaskAvailable(int viewId) {
        this.viewId = viewId;
        if (!isGooglePlayServicesAvailable()) { //1. 구글 플레이 서비스 사용가능한지 확인
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) { //2. 계정 선택 안됨
            chooseAccount();
        } else if (!isDeviceOnline()) { //3. 온라인 상태 확인
            if(calendarTaskListener != null)
                calendarTaskListener.onErrorCalendarTask("No network connection available.");
        } else { // 캘린더 작업 가능!!
            if(calendarTaskListener != null)
                calendarTaskListener.onAvailableCalendarTask(viewId);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    void handleCommonThrowable(Throwable throwable) {
        if (throwable != null) {
            if (throwable instanceof GooglePlayServicesAvailabilityIOException) {
                showGooglePlayServicesAvailabilityErrorDialog(
                        ((GooglePlayServicesAvailabilityIOException) throwable)
                                .getConnectionStatusCode());
            } else if (throwable instanceof UserRecoverableAuthIOException) {
                startActivityForResult(
                        ((UserRecoverableAuthIOException) throwable).getIntent(), REQUEST_AUTHORIZATION);
            } else {
                if(calendarTaskListener != null)
                    calendarTaskListener.onErrorCalendarTask("The following error occurred:\n" + throwable.getMessage());
            }
        } else {
            if(calendarTaskListener != null)
                calendarTaskListener.onErrorCalendarTask("Request cancelled.");
        }
    }
}
