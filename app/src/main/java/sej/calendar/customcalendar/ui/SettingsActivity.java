package sej.calendar.customcalendar.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import sej.calendar.customcalendar.R;


public class SettingsActivity extends AppCompatActivity {

    private int dayPerMonth; //한달 일수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        //백 화살표
        ActionBar mActionbar = getSupportActionBar();
        mActionbar.setDisplayHomeAsUpEnabled(true);

        // 메뉴 리스트
        String [] str = {
                "Google calendar syn",
                "Change day per month",
                "information"
        };

        ArrayAdapter adt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, str);

        ListView listView = findViewById(R.id.settings_list);
        listView.setAdapter(adt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    //구글 캘린더 연동 페이지 띄우기
                    Intent intent = new Intent(SettingsActivity.this, AuthActivity.class);
                    startActivity(intent);
                } else if(i == 1){
                    //한달 일수 변경
                    changeDayPerMonth();
                } else {
                    //설명
                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Information")
                            .setMessage("The days of the last month is less than N or greater than N. If the remainder that divide 365(or 366) into N is more than 0.3 times of N, the days of the last month is equal to the remainder , if it is less than 0.3 times, the days of the last month is equal to remainder + N.\n" +
                                    "ex) If N = 28, 365% 28 = 1 <= 0.3 * 28, so the last month is 28+1 days")
                            .setPositiveButton("close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

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

    //일수 변경 다이얼로그
    public void changeDayPerMonth(){
        final EditText et = new EditText(getApplicationContext());
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Change day per month")
                .setMessage("Please enter a day between 14 and 180")
                .setView(et)
                .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setNegativeButton("confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (et.getText().length() > 0){
                            dayPerMonth = Integer.parseInt(et.getText().toString()); //숫자 패드만 보이게 하기
                            SharedPreferences pref = getSharedPreferences("Pref", MODE_PRIVATE);
                            if (14 <= dayPerMonth && dayPerMonth <= 180){
                                Toast.makeText(SettingsActivity.this,"Please restart", Toast.LENGTH_LONG).show();
                                pref.edit().putInt("dayPerMonth", dayPerMonth).apply();
                            } else{
                                Toast.makeText(SettingsActivity.this,"Wrong value", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /* 바뀐 다이얼로구 사용법 블로그포스팅
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    final EditText et = new EditText(getApplicationContext());
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(et);
                    builder.setMessage("Please enter a day between 14 and 180");


                    builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (et.getText().length() > 0){
                                dayPerMonth = Integer.parseInt(et.getText().toString()); //숫자 패드만 보이게 하기
                                if (14 <= dayPerMonth && dayPerMonth <= 180){
                                    Toast.makeText(getApplicationContext(),"Please restart", Toast.LENGTH_LONG).show();
                                    pref.edit().putInt("dayPerMonth", dayPerMonth).apply();
                                } else{
                                    Toast.makeText(getApplicationContext(),"Wrong value", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Change day per month"); // dialog  Title
                    alert.show();

                     */

}