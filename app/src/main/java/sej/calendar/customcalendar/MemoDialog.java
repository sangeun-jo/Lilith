package sej.calendar.customcalendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.Realm;
import sej.calendar.customcalendar.model.Memo;

public class MemoDialog extends Dialog implements View.OnClickListener{

    private TextView memoDate;
    private EditText editMemo;
    private Button saveBtn;
    private Button deleteBtn;

    private Context context;
    private String customDate; //커스텀
    private String date12; //12월
    private EditText title;
    private EditText category;

    private myListener myListener;

    private Realm realm;

    public MemoDialog(Context context, String customDate, String date12) {
        super(context);
        this.context = context;
        this.customDate = customDate;
        this.date12 = date12;
    }


    //인터페이스 설정
    public interface myListener{
        void onSaveClicked(String category, String title, String content);
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

        //Realm.init(context);
        realm = Realm.getDefaultInstance();

        //init
        memoDate = findViewById(R.id.memo_date);
        memoDate.setText(customDate + "(" + date12 + ")");
        editMemo = findViewById(R.id.edit_memo);
        title = findViewById(R.id.memo_title);
        category = findViewById(R.id.memo_category);
        saveBtn = (Button) findViewById(R.id.save_memo);
        deleteBtn = (Button) findViewById(R.id.delete_memo);


        realm.executeTransaction(new Realm.Transaction(){
            @Override
            public void execute(Realm realm) {
                final Memo results = realm.where(Memo.class).equalTo("date", date12).findFirst();
                if (results != null) {
                    editMemo.setText(results.getContent());
                    title.setText(results.getTitle());
                    category.setText(results.getCategory());
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
                String category;
                String title;
                String content;
                if(editMemo.getText().length() > 0){
                    content = editMemo.getText().toString();
                } else{
                    content = "no content";
                }

                if(this.title.getText().length() > 0){
                    title = this.title.getText().toString();
                } else{
                    title = "no title";
                }

                if(this.title.getText().length() > 0){
                    category = this.title.getText().toString();
                } else{
                    category = "no category";
                }

                //인터페이스의 함수를 호출하여 변수에 저장된 값들을 Activity로 전달
                myListener.onSaveClicked(category, title, content);
                break;
            case R.id.delete_memo:
                myListener.onDeleteClicked();
                break;
        }
    }
}
