package com.example.excalendar2;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import io.realm.Realm;

public class MemoDialog extends Dialog implements View.OnClickListener{

    private TextView memoTitle;
    private EditText editMemo;
    private Button saveBtn;
    private Button deleteBtn;

    private Context context;
    private String title; //커스텀
    private String date; //12월
    private myListener myListener;

    private Realm realm;

    public MemoDialog(Context context, String title, String date) {
        super(context);
        this.context = context;
        this.title = title;
        this.date = date;
    }


    //인터페이스 설정
    public interface myListener{
        void onSaveClicked(String memoData);
        void onDeleteClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(myListener myListener){
        this.myListener = myListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_dialog);

        Realm.init(context);
        realm = Realm.getDefaultInstance();

        //init
        memoTitle = findViewById(R.id.memo_date);
        memoTitle.setText(title + "(" + date + ")");
        editMemo = findViewById(R.id.edit_memo);
        saveBtn = (Button) findViewById(R.id.save_memo);
        deleteBtn = (Button) findViewById(R.id.delete_memo);


        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                final Memo results = realm.where(Memo.class).equalTo("date", date).findFirst();
                if (results != null) {
                    String content = results.getContent();
                    editMemo.setText(content);
                }
            }

        });
        realm.close();

        //버튼 클릭 리스너 등록
        saveBtn.setOnClickListener(this); //각 액티비티에서 리스너 생성시 기본 함수로 등록
        deleteBtn.setOnClickListener(this);
        //액티비티에서 Override 하여 액티비티마다 다르게 동작시키게 할 수 있음
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.save_memo:
                String memoData;
                if(editMemo.getText().length() > 0){
                    memoData = editMemo.getText().toString();
                } else{
                    memoData = null;
                }
                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                myListener.onSaveClicked(memoData);
                break;
            case R.id.delete_memo:
                myListener.onDeleteClicked();
                break;
        }
    }
}
