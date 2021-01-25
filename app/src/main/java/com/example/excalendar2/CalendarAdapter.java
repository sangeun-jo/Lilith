package com.example.excalendar2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// 날 것의 배열을 화면에 뿌려줄 수 있도록 가공해주는 애

public class CalendarAdapter extends BaseAdapter {

    public String selectedDate;
    public ArrayList<DayInfo> arrayListDayInfo;
    private CustomCalendar cCal = new CustomCalendar();
    //int first_week = 1; //월요일2, 일요일 1. 쉐어드 프로퍼런스 불러오기

    public CalendarAdapter(String selectedDate, ArrayList<DayInfo> arrayListDayInfo){ //생성자
        this.selectedDate = selectedDate;
        this.arrayListDayInfo = arrayListDayInfo;
    }

    @Override
    public int getCount() {
        return arrayListDayInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListDayInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DayInfo day = arrayListDayInfo.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day, parent, false);
        }

        if (day != null) {
            TextView tvDay12 = convertView.findViewById(R.id._12cal_day);
            TextView tvDay13 = convertView.findViewById(R.id.day_cell_tv_day);

            ImageView ivSelected = convertView.findViewById(R.id.iv_selected);

            if(day.isSameDay(selectedDate)){

                ivSelected.setVisibility(View.VISIBLE); //선택된 날짜와 같은 날짜면 보이게
            }else{
                ivSelected.setVisibility(View.INVISIBLE); //선택된 날짜와 다른 날짜면 보이지 않게
            }

            if(day.inMonth){
                SimpleDateFormat sdf = new SimpleDateFormat("M/d", Locale.KOREA);
                tvDay12.setText(sdf.format(day.get12DayCal().getTime()));
                tvDay13.setText(day.getDay());

                if(cCal.FIRST_WEEK == 1){
                    if((position % cCal.DAY_PER_WEEK) == 0){   //일요일이면
                        tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                    } else if((position % cCal.DAY_PER_WEEK) == 6){ //토요일이면
                        tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                    }else{ //나머지 날은 검정색
                        tvDay13.setTextColor(Color.BLACK);
                    }
                } else{
                    if((position % cCal.DAY_PER_WEEK) == 6){   //일요일이면
                        tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                    } else if((position % cCal.DAY_PER_WEEK) == 5){ //토요일이면
                        tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                    }else{ //나머지 날은 검정색
                        tvDay13.setTextColor(Color.BLACK);
                    }
                }

                /*
                if((position % cCal.DAY_PER_WEEK) == 6){   //일요일이면
                    tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                } else if((position % cCal.DAY_PER_WEEK) == 5){ //토요일이면
                    tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                }else{ //나머지 날은 검정색
                    tvDay13.setTextColor(Color.BLACK);
                }
                */
                tvDay12.setTextColor(Color.GRAY); //12월


            }

            convertView.setTag(day);

            return convertView; //데이터를 뿌린 뷰를 반환함
        }

        return null;
    }
}