package sej.calendar.customcalendar;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Memo extends RealmObject{

    @Required
    private String date; //날짜
    private String content; //내용

    public Memo(){

    }

    public Memo(String date, String content){
        this.date = date;
        this.content = content;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


}
