package com.example.excalendar2;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomCalendar {
	
	public static int YEAR; 
	public static int MONTH;
	public static int DATE;
	
	public static int MONTH_PER_YEAR;  // 1년에 몇달인지
	public static int LAST_MONTH_DAY; //마지막 달 일 수
	public static int DAY_PER_YEAR; // 1년에 몇일인지 
	public static int DAY_PER_WEEK; //1주일에 몇일인지
	
	
	private static int SHARE; //몫
	private static int REMAIN_DATE; //나머지(자투리 날짜)
	
	
	private static boolean CARRY; //이월 여부 
	
	//사용자에게 입력받아서 shared프러퍼런스에 넣을 요소들 
	private static double RATIO = 0.3; // 마지막 달이 기본 달과 30퍼 이상 길어지면 이월. 1 입력시 이월 안됨. 0 입력 시 무조건 이월
	public static int FIRST_WEEK = 2; //한주의 시작 요일 
	public static String week[] = {"월", "화", "수", "목", "금", "토", "일"};  //맵으로 변경하기
	public static int DAY_PER_MONTH = 42; // 1달에 몇일인지

	private ArrayList<DayInfo> arrayListDayInfo;
	
	//생성자 
	public CustomCalendar() {
		Calendar cal = Calendar.getInstance(); 
		this.YEAR = cal.YEAR;  
		this.MONTH = cal.MONTH; 
		this.DATE = cal.DATE;
		DAY_PER_WEEK = week.length; 
		
		//윤년 
		if(YEAR % 4 == 0) {
			DAY_PER_YEAR = 366;
		} else {
			DAY_PER_YEAR = 365; 
		}
		
		
		SHARE = (int) DAY_PER_YEAR / DAY_PER_MONTH; //몫
		REMAIN_DATE = DAY_PER_YEAR % DAY_PER_MONTH; //나머지 

		// 마지막 달의 일수와 이월 여부를 정하는 코드 
		double ratio = (double) REMAIN_DATE / DAY_PER_MONTH;  
		if (ratio > RATIO) {  
			MONTH_PER_YEAR = DAY_PER_YEAR / DAY_PER_MONTH + 1; 
			LAST_MONTH_DAY = REMAIN_DATE; // 자투리 날짜가 마지막 달 
			CARRY = true; 
		} else {
			MONTH_PER_YEAR = DAY_PER_YEAR / DAY_PER_MONTH; 
			LAST_MONTH_DAY = DAY_PER_MONTH + REMAIN_DATE; // DAY_PER_MONTH에 자투리 일수 더하기 
			CARRY = false; 
		}
		
	}

	public void setArrayListDayInfo(ArrayList<DayInfo> arrayListDayInfo){
		this.arrayListDayInfo = arrayListDayInfo;
	}
	
	//날짜 세팅
	
	public void setYear(int year) {
		this.YEAR = year; 
	}
	
	public void setMonth(int month) {
		this.MONTH = month; 
	}
	
	public void setDate(int date) {
		this.DATE = date;
	}
	
	public void set(int year, int month, int date) {
		this.YEAR = year;
		this.MONTH = month; 
		this.DATE = date; 
		
	}
	
	//커스텀 달력을 12 달력으로 바꾸기
	public Calendar cToN(int year, int month, int date) { 
		int stackedDays = DAY_PER_MONTH * (month-1) + (date - 1); ; 
        System.out.println(stackedDays);
        Calendar cal = Calendar.getInstance();
        cal.set(year, 0, 1);
        cal.add(Calendar.DATE, stackedDays);
        return cal;
	}
	
	//12 달력을 커스텀 달력으로 바꾸기
	public CustomCalendar nToC(int year, int month, int date) { 
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, date);
    	
		int stackedDays = cal.get(Calendar.DAY_OF_YEAR);

		int c_mon; 
		if(month == MONTH_PER_YEAR && CARRY) {
			c_mon = stackedDays / DAY_PER_MONTH + 1; 
		} else {
			c_mon = (int) stackedDays / DAY_PER_MONTH; 
		}

		int c_date = stackedDays % DAY_PER_MONTH;  
		
		CustomCalendar ccal = new CustomCalendar();
		ccal.set(year, c_mon + 1, c_date);
	
		return ccal; 
	}

	public String nToC(Calendar cal) {

		int stackedDays = cal.get(Calendar.DAY_OF_YEAR);
		int c_mon;
		if(cal.MONTH-1 == MONTH_PER_YEAR && CARRY) {
			c_mon = stackedDays / DAY_PER_MONTH + 1;
		} else {
			c_mon = (int) stackedDays / DAY_PER_MONTH;
		}

		int c_date = stackedDays % DAY_PER_MONTH;

		String ccal = cal.get(Calendar.YEAR) + "-" + (c_mon+1) + "-" + c_date;
		return ccal;
	}
	
	
	//1: 일요일
	//2: 월요일
	//3: 화요일
	//4: 수요일
	//5: 목요일
	//6: 금요일
	//7: 토요일

	//달 입력하면 프린트, 월요일이 일주일 시작 
	public void drawCalendar(int year, int month) {

		Calendar jen = Calendar.getInstance(Locale.KOREA);
        jen.set(year, 0, 1);
        int dayOfWeek = jen.get(Calendar.DAY_OF_WEEK);

		DayInfo day;

		// 달력 앞 빈 칸 
        if (dayOfWeek == 1) { 
            for (int i = 0; i < DAY_PER_WEEK - (FIRST_WEEK-1); i++) {
				day = new DayInfo();
				arrayListDayInfo.add(day);
            }
        } else { //월화수목금토 
            for (int i = 0; i < dayOfWeek-FIRST_WEEK; i++) { //7-2
				day = new DayInfo();
				arrayListDayInfo.add(day);
            }
        }
		
		// 숫자 쉘 
        if (month != MONTH_PER_YEAR) { 
            for (int i = 1; i <= DAY_PER_MONTH; i++) {
				day = new DayInfo();
				day.setDate(year, month ,i,true);
				arrayListDayInfo.add(day);
            }
        } else { // 마지막 달 
        	for (int i = 1; i <= LAST_MONTH_DAY; i++) {
				day = new DayInfo();
				day.setDate(year, month ,i,true);
				arrayListDayInfo.add(day);
            }
        }
        
        // 달력 뒷부분 빈칸 
        int lastWeek = DAY_PER_WEEK - arrayListDayInfo.size() % DAY_PER_WEEK;
        for(int i=0; i<lastWeek; i++) {
			day = new DayInfo();
			arrayListDayInfo.add(day);
        }



	}
}