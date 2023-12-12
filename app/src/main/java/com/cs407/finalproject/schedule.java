package com.cs407.finalproject;

public class schedule {
    private String date;
    private String term;
    private String title;
    private String content;

    public schedule(String date,String term,String title,String content){
        this.date=date;
        this.term=term;
        this.title=title;
        this.content=content;
    }
    public String getDate(){
        return date;
    }
    public String getTerm(){
        return term;
    }
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
}