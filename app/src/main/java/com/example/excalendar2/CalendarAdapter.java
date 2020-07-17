package com.example.excalendar2;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;

// 날 것의 배열을 화면에 뿌려줄 수 있도록 가공해주는 애

public class CalendarAdapter extends BaseAdapter {

    public Calendar selectedDate;
    public ArrayList<DayInfo> arrayListDayInfo;

    public CalendarAdapter(Calendar selectedDate, ArrayList<DayInfo> arrayListDayInfo){ //생성자
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //커스텀 하기
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
                System.out.println();
            }

            if(day.inMonth){
                tvDay12.setText(day.get12Day(1));
                tvDay13.setText(day.getDay());
            }

            return convertView; //데이터를 뿌린 뷰를 반환함
        }

        return null;
    }
}