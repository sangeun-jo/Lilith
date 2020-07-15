package com.example.excalendar2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
// 날 것의 배열을 화면에 뿌려줄 수 있도록 가공해주는 애

public class CalendarAdapter extends BaseAdapter {

    public Date selectedData;
    private ArrayList<String> _13dayList; //뿌려줄 데이터
    private ArrayList<String> _12dayList; //뿌려줄 데이터

    public CalendarAdapter(ArrayList<String> _13dayList, ArrayList<String> _12dayList, Date date){ //생성자
        this._13dayList = _13dayList;
        this._12dayList = _12dayList;
        this.selectedData = date;
    }

    @Override
    public int getCount() {
        return _13dayList.size();
    }

    @Override
    public Object getItem(int position) {
        return _13dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //커스텀 하기
        String _13day = _13dayList.get(position);
        String _12day = _12dayList.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day, parent, false);
        }

        if(_13day != null){
            TextView _13tvDay = convertView.findViewById(R.id.day_cell_tv_day);  // day 의 숫자 부분
            TextView _12tvDay = convertView.findViewById(R.id._12cal_day);  // day 의 숫자 부분
            _13tvDay.setText(_13day);  // 해당 위치의 날짜를 그려줌
            _12tvDay.setText(_12day);

            return convertView; //데이터를 뿌린 뷰를 반환함
        }
        return null;
    }
}


