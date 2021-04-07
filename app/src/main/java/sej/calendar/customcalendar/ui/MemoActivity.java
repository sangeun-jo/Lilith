package sej.calendar.customcalendar.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import sej.calendar.customcalendar.R;
import sej.calendar.customcalendar.model.Memo;

public class MemoActivity extends AppCompatActivity {

    private Realm realm;

    private TextView memoDate;
    private EditText memoTitle;
    private EditText calendar;
    private EditText editMemo;
    private Button saveBtn;
    private Button deleteBtn;

    private String date12; //12월


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        //백 화살표
        ActionBar mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        date12 = intent.getStringExtra("date12");
        String customDate = intent.getStringExtra("customDate");

        memoDate = findViewById(R.id.memo_date);
        memoDate.setText(customDate + "(" + date12 + ")");
        editMemo = findViewById(R.id.edit_memo);
        memoTitle = findViewById(R.id.memo_title);
        calendar = findViewById(R.id.google_calendar);
        saveBtn = (Button) findViewById(R.id.save_memo);
        deleteBtn = (Button) findViewById(R.id.delete_memo);

        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                final Memo results = realm.where(Memo.class).equalTo("date", date12).findFirst();
                if (results != null) {
                    editMemo.setText(results.getContent());
                    memoTitle.setText(results.getTitle());
                    calendar.setText(results.getCalendar());
                }
            }

        });
        realm.close();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMemo(date12);
                setResult(1001);
                finish();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Memo> exitMemo = realm.where(Memo.class).equalTo("date", date12).findAll();
                        if (!exitMemo.isEmpty()) {
                            exitMemo.deleteAllFromRealm();
                        }
                    }
                });
                realm.close();

                setResult(1002);
                finish();
            }
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

        String cal;
        String title;
        String content;

        if(editMemo.getText().length() > 0){
            content = editMemo.getText().toString();
        } else{
            content = "no content";
        }

        if(memoTitle.getText().length() > 0){
            title = memoTitle.getText().toString();
        } else{
            title = "no title";
        }

        if(calendar.getText().length() > 0){
            cal = calendar.getText().toString();
        } else{
            cal = "custom calendar";
        }

        realm = Realm.getDefaultInstance();
        Memo results = realm.where(Memo.class).equalTo("date", date12).findFirst();
        Memo memo;

        realm.beginTransaction();
        if (results == null) { // 메모 없었던 경우 저장
            memo = realm.createObject(Memo.class);
            memo.setDate(date12);
            memo.setCalendar(cal);
            memo.setTitle(title);
            memo.setContent(content);
        } else{ //메모가 원래 있었던 경우
            results.setCalendar(results.getCalendar());
            results.setTitle(results.getTitle());
            results.setContent(results.getContent());
        }
        realm.commitTransaction();
        realm.close();
    }
}