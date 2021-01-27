package com.example.excalendar2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NormalAdapter extends BaseAdapter {

    public String selectedDate;
    public ArrayList<DayInfo> arrayListDayInfo;
    private CustomCalendar cCal = new CustomCalendar();
    private int firstWeek = 2 ;
    public NormalAdapter(String selectedDate, ArrayList<DayInfo> arrayListDayInfo){ //생성자
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
        SimpleDateFormat sdf = new SimpleDateFormat("M/d", Locale.KOREA);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day, parent, false);
        }

        if (day != null) {
            TextView tvDay12 = convertView.findViewById(R.id._12cal_day);
            TextView tvDay13 = convertView.findViewById(R.id.day_cell_tv_day);
            //ImageView ivSelected = convertView.findViewById(R.id.iv_selected);
            RelativeLayout bg = convertView.findViewById(R.id.day_cell_ll_background);

            if(day.isSameDay(selectedDate)){
                bg.setBackgroundColor(Color.rgb(241, 241, 241));
                //tvDay13.setBackgroundColor(Color.rgb(255, 193, 7));
                //ivSelected.setVisibility(View.VISIBLE); //선택된 날짜와 같은 날짜면 보이게
            } else{
                bg.setBackgroundColor(Color.rgb(255, 255, 255));
                //tvDay13.setBackgroundColor(Color.rgb(255, 255, 255));
                //ivSelected.setVisibility(View.INVISIBLE); //선택된 날짜와 다른 날짜면 보이지 않게
            }


            if(day.inMonth){
                tvDay12.setText(sdf.format(day.get12DayCal().getTime()));
                tvDay13.setText(day.getDay());
                if(firstWeek == 1){ //일요일이 첫주
                    if((position % 7 ) == 0){   //일요일이면
                        tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                    } else if((position % 7) == 6){ //토요일이면
                        //tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                    }else{ //나머지 날은 검정색
                        tvDay13.setTextColor(Color.BLACK);
                    }
                } else{
                    if((position % 7) == 6){   //일요일이면
                        tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                    } else if((position % 7) == 5){ //토요일이면
                        //tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                    }else{ //나머지 날은 검정색
                        tvDay13.setTextColor(Color.BLACK);
                    }
                }


                tvDay12.setTextColor(Color.GRAY); //12월


            }

            convertView.setTag(day);

            return convertView; //데이터를 뿌린 뷰를 반환함
        }

        return null;
    }
}
