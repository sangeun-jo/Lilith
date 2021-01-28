package com.example.excalendar2;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MemoDialog extends Dialog implements View.OnClickListener{

    private TextView memoTitle;
    private EditText inputMemo;
    private Button confirmBtn;
    private Button cancelBtn;

    private Context context;
    private String title;
    private myListener myListener;

    public MemoDialog(Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;
    }

    //인터페이스 설정
    public interface myListener{
        void onPositiveClicked(String memoData);
        void onNegativeClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(myListener myListener){
        this.myListener = myListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_dialog);

        //init
        memoTitle = findViewById(R.id.memo_date);
        memoTitle.setText(title);
        inputMemo = findViewById(R.id.memo);
        confirmBtn = (Button) findViewById(R.id.confirm);
        cancelBtn = (Button) findViewById(R.id.cancel);

        //버튼 클릭 리스너 등록
        confirmBtn.setOnClickListener(this); //각 액티비티에서 리스너 생성시 기본 함수로 등록
        cancelBtn.setOnClickListener(this);
        //액티비티에서 Override 하여 액티비티마다 다르게 동작시키게 할 수 있음

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirm:
                String memoData;
                if(inputMemo.getText().length() > 0){
                    memoData = inputMemo.getText().toString();
                } else{
                    memoData = null;
                }

                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                myListener.onPositiveClicked(memoData);
                break;

            case R.id.cancel:
                myListener.onNegativeClicked();
                break;
        }
    }




}
