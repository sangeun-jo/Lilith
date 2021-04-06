package sej.calendar.customcalendar.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Memo extends RealmObject{

    @Required
    private String date; //날짜
    private String category; //카테고리
    private String title; //제목
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
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
