package sej.calendar.customcalendar.model;

public class DayView {
    private String normalDate;
    private String customDate;
    private boolean isEmpty;
    private Memo memo;

    public DayView(Boolean isEmpty){
        this.isEmpty = isEmpty;
    }

    public void setNormalDate(String normalDate, Memo memo) {
        this.normalDate = normalDate;
        this.memo = memo;
    }

    public void setCustomDate(String customDate) {
        this.customDate = customDate;
    }
}
