package sej.calendar.customcalendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import sej.calendar.customcalendar.model.DayView;

// 날 것의 배열을 화면에 뿌려줄 수 있도록 가공해주는 애



public class CalendarAdapter extends BaseAdapter {

    private String selectedDate;
    private ArrayList<DayView> arrayListDayInfo;
    private CalendarConverter converter;

    SimpleDateFormat md = new SimpleDateFormat("M/d", Locale.getDefault());
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public CalendarAdapter(ArrayList<DayView> arrayListDayInfo, String selectedDate, CalendarConverter converter){ //생성자
        this.arrayListDayInfo = arrayListDayInfo;
        this.selectedDate = selectedDate;
        this.converter = converter;
    }

    public void setCalList(ArrayList<DayView> arrayListDayInfo) {
        this.arrayListDayInfo = arrayListDayInfo;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public String getSelectedDate() {
        return selectedDate;
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
        DayView day = arrayListDayInfo.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day, parent, false);
        }

        TextView tvDay12 = convertView.findViewById(R.id.day_12cal);
        TextView tvDay13 = convertView.findViewById(R.id.day_cell_tv_day);
        RelativeLayout bg = convertView.findViewById(R.id.day_cell_ll_background);
        TextView mark = convertView.findViewById(R.id.memo_mark);

        if (day != null) {
            if(day.isSameDay(selectedDate)){
                bg.setBackgroundColor(Color.rgb(241, 241, 241));
                mark.setTextColor(Color.rgb(241, 241, 241));
            } else{
                bg.setBackgroundColor(Color.rgb(255, 255, 255));
                mark.setText("");
            }

            if(!day.isEmpty){
                String date12 = day.getNormalDate();

                if(day.getMemo() != null) {
                    System.out.println(day.getMemo().getTitle());
                    mark.setText(day.getMemo().getTitle());
                    mark.setTextColor(Color.BLACK);
                }

                tvDay12.setText(date12);
                tvDay13.setText(day.getCustomD());

                //한주의 시작=월요일
                if((position % 7) == 6){   //일요일이면
                    tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                } else if((position % 7) == 5){ //토요일이면
                    tvDay13.setTextColor(Color.GRAY); //
                }else{ //나머지 날은 검정색
                    tvDay13.setTextColor(Color.BLACK);
                }

                /* 한주의 시작=일요일
                if((position % cCal.DAY_PER_WEEK) == 0){   //일요일이면
                    tvDay13.setTextColor(Color.rgb(233, 30, 99)); //빨간색
                } else if((position % cCal.DAY_PER_WEEK) == 6){ //토요일이면
                    //tvDay13.setTextColor(Color.rgb(33, 150, 245)); //파란색
                }else{ //나머지 날은 검정색
                    tvDay13.setTextColor(Color.BLACK);
                }

                 */
                tvDay12.setTextColor(Color.GRAY); //12월
            } else{
                tvDay12.setText("");
                tvDay13.setText("");
            }
            convertView.setTag(day);
            return convertView; //데이터를 뿌린 뷰를 반환함
        }

        return null;
    }



}