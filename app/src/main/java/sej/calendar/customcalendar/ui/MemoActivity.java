package sej.calendar.customcalendar.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;
import sej.calendar.customcalendar.GoogleCalendar;
import sej.calendar.customcalendar.R;
import sej.calendar.customcalendar.model.Memo;

public class MemoActivity extends AppCompatActivity{

    private Realm realm;

    private EditText memoTitle;
    private EditText editMemo;
    private Button saveBtn;
    private Button deleteBtn;

    private String date12; //12월

    private Memo exitMemo;

    private GoogleAccountCredential mCredential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Collections.singleton(CalendarScopes.CALENDAR))
                .setBackOff(new ExponentialBackOff());

        System.out.println(mCredential.getSelectedAccountName() + " 계정으로 로그인 됨");

        Intent intent = getIntent();
        date12 = intent.getStringExtra("date12");
        String customDate = intent.getStringExtra("customDate");
        String gTitle = intent.getStringExtra("title");
        String gContent = intent.getStringExtra("content");

        editMemo = findViewById(R.id.edit_memo);
        memoTitle = findViewById(R.id.memo_title);
        saveBtn = (Button) findViewById(R.id.save_memo);
        deleteBtn = (Button) findViewById(R.id.delete_memo);

        //백 화살표
        ActionBar mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);
        mActionbar.setTitle(customDate + "(" + date12 + ")");

        realm = Realm.getDefaultInstance();

        exitMemo = realm.where(Memo.class).equalTo("date", date12).findFirst();

        if (exitMemo != null) {
            editMemo.setText(exitMemo.getContent());
            memoTitle.setText(exitMemo.getTitle());
        }

        memoTitle.setText(gTitle);
        editMemo.setText(gContent);

        saveBtn.setOnClickListener(v -> {
            editMemo(date12);
            setResult(1001);
            finish();
        });


        deleteBtn.setOnClickListener(v -> {
            realm.beginTransaction();
            RealmResults<Memo> exitMemo = realm.where(Memo.class).equalTo("date", date12).findAll();
            exitMemo.deleteAllFromRealm();
            realm.commitTransaction();
            setResult(1002);
            finish();
        });
    }


    //백 화살표 눌렸을 때 닫힘
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //메모 입력
    public void editMemo(String date12) {
        String title = (memoTitle.getText().length() > 0) ? memoTitle.getText().toString():"no title";
        String content = (editMemo.getText().length() > 0 ) ? editMemo.getText().toString():"no content";

        Memo memo;

        realm.beginTransaction();
        if (exitMemo == null) { // 메모 없었던 경우 저장
            memo = realm.createObject(Memo.class);
            memo.setDate(date12);
            memo.setTitle(title);
            memo.setContent(content);
        } else{ //메모가 원래 있었던 경우 입력된 값으로 새로 저장
            exitMemo.setTitle(title);
            exitMemo.setContent(content);
        }
        realm.commitTransaction();
    }


    public void addEvent() {
        GoogleCalendar googleCalendar = GoogleCalendar.build(mCredential);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}