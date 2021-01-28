package com.example.excalendar2;

public class Memo {
    private String date;
    private String memo;

    public Memo(String date, String memo){
        this.date = date;
        this.memo = memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo(){
        return memo;
    }

    public String getDate(){
        return date;
    }
}
