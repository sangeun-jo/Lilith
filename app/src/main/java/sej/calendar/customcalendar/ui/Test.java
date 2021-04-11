package sej.calendar.customcalendar.ui;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import sej.calendar.customcalendar.R;

public class Test extends AppCompatActivity {

    Button googleLogin = findViewById(R.id.google_login);
    Button googleLogout = findViewById(R.id.google_logout);
    Button googleCalendar = findViewById(R.id.google_calendar_list);

    GoogleAccountCredential mCredential;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
