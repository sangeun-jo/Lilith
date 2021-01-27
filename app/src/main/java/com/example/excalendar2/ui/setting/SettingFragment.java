package com.example.excalendar2.ui.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.excalendar2.R;

public class SettingFragment extends Fragment{

    private SettingViewModel settingViewModel;
    SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        prefs  = getActivity().getSharedPreferences("Pref", getActivity().MODE_PRIVATE);
        String [] str = {
                "한달 일수 변경",
                "이월 비율 변경",
                "사용법"
        };
        ArrayAdapter adt = new ArrayAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, str);
        ListView listView = (ListView) root.findViewById(R.id.setting_list);
        listView.setAdapter(adt);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final EditText et = new EditText(getActivity());
                    builder.setView(et);
                    builder.setMessage("한달 일수를 입력하세요(14~180)");
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int dayPerMonth = Integer.parseInt(et.getText().toString());
                            if(14<= dayPerMonth && dayPerMonth <= 180) {
                                prefs.edit().putInt("dayPerMonth", dayPerMonth).apply();
                            } else{
                                //토스트 메시지 띄우기
                            }
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("한달 일수 변경"); // dialog  Title
                    alert.show();
                } else if(i == 1){ //벌금 바꾸기
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    final EditText et = new EditText(getActivity());
                    builder.setView(et);
                    builder.setMessage("이월 계수를 입력하세요(0~1 사이)"); //한달 최대 날짜로 바꾸기
                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int ratio = Integer.parseInt(et.getText().toString());
                            if(0<= ratio && ratio <= 1) {
                                prefs.edit().putInt("ratio", ratio).apply();
                            } else{
                                //토스트 메시지 띄우기
                            }
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("이월 계수"); // dialog  Title
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(
                            "\n어쩌구 저쩌구\n" +
                                    "   회색: 출결 상태\n" +
                                    "   파란색: 틀린 단어 개수\n" +
                                    "   빨간색: 해당 날짜의 벌금\n" +
                                    "\n2. 다른 날짜 출결 관리\n" +
                                    "   우측 상단 달력 아이콘을 선택하여 이동하세요.\n" +
                                    "\n3. 멤버 관리\n" +
                                    "   멤버 추가: 멤버 메뉴로 이동 > 우측 상단 점 세개 클릭 > 멤버 추가\n"  +
                                    "   멤버 삭제: 멤버 메뉴로 이동 > 우측 상단 점 세개 클릭 > 멤버 선택 > 삭제 모드 진입\n" +
                                    "   ※멤버 이름 옆 회색 날짜는 등록일입니다\n" +
                                    "\n4. 벌금 액수 변경\n" +
                                    "   초기 벌금은 단어 1개 100원, 지각 1분 100원, 무단 결석 10000원, 예고 결석 0원으로 설정되어 있습니다. 변경 시점 이전의 내역에 대해서는 적용되지 않습니다.\n" +
                                    "\n5. 데이터 초기화에 관하여\n" +
                                    "   출결 데이터는 사용자의 폰에 저장되므로, 어플 삭제, 데이터 초기화 시 복구할 수 없습니다.\n" );

                    builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    });

                    AlertDialog alert = builder.create();
                    alert.setTitle("사용법"); // dialog  Title
                    alert.show();
                }
            }
        });

        return root;
    }
}
