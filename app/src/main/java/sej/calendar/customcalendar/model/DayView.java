package sej.calendar.customcalendar.model;

public class DayView {
    private String normalDate;
    private String customDate;
    public boolean isEmpty;
    private int year;
    private int month;
    private int date;
    private Memo memo;

    public DayView(Boolean isEmpty){
        this.isEmpty = isEmpty;
    }

    public void setNormalDate(String normalDate, Memo memo) {
        this.normalDate = normalDate;
        this.memo = memo;
    }

    public void setCustomDate(int y, int m, int d) {
        year = y;
        month = m;
        date = d;
        customDate = y+ "-" + m +"-" + d;
    }

    public String getCustomD() {
        return Integer.toString(date);
    }

    public String getCustomYMD(){
        return customDate;
    }

    public String getNormalDate() {
        return normalDate;
    }

    public Memo getMemo() {
        return memo;
    }

    /*
    public boolean isMemo() {
        if (memo.getTitle() != null){
            return true;
        } else {
            return false;
        }
    }

     */

    public boolean isSameDay(String date1){
        boolean sameDay = date1.equals(this.customDate);
        return sameDay;
    }
}
