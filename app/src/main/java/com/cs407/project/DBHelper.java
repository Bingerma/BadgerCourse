package com.cs407.project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;


    public class DBHelper {
        static SQLiteDatabase sqLiteDatabase;
        public DBHelper(SQLiteDatabase sqLiteDatabase){
            this.sqLiteDatabase=sqLiteDatabase;
        }
        public static void createTable(){
            sqLiteDatabase.execSQL("Create TABLE IF NOT EXISTS notes" +
                    "(id INTEGER PRIMARY KEY, noteId INTEGER, term TEXT, date TEXT,content TEXT, title TEXT)");
        }
        public ArrayList<schedule> readNotes(){
            createTable();
            Cursor c=sqLiteDatabase.rawQuery("SELECT * FROM notes",null);
            int dateIndex=c.getColumnIndex("date");
            int titleIndex=c.getColumnIndex("title");
            int contentIndex=c.getColumnIndex("content");
            int termIndex=c.getColumnIndex("term");
            c.moveToFirst();
            ArrayList<schedule>notesList=new ArrayList<>();
            while(!c.isAfterLast()){
                String title=c.getString(titleIndex);
                String date=c.getString(dateIndex);
                String content=c.getString(contentIndex);
                String term=c.getString(termIndex);

                schedule notes=new schedule(date,term,title,content);
                notesList.add(notes);
                c.moveToNext();
            }
            c.close();
            sqLiteDatabase.close();
            return notesList;
        }
        public void saveNotes(String term, String title,String date,String content){
            createTable();
            sqLiteDatabase.execSQL("INSERT INTO notes (term,date,title,content) VALUES (?,?,?,?)", new String[]{term,date,title,content});
        }
        public void updateNotes(String content,String date,String title,String term){
            createTable();
            schedule note=new schedule(date,term,title,content);
            sqLiteDatabase.execSQL("UPDATE notes set content=?, date=? where title=? and term=?",new String[]{content,date,title,term});
        }
        public void deleteNotes(String content, String title){
            createTable();
            String date="";
            Cursor cursor=sqLiteDatabase.rawQuery("SELECT date FROM notes WHERE content=?",new String[]{content});
            if (cursor.moveToNext()){
                date=cursor.getString(0);
            }
            sqLiteDatabase.execSQL("DELETE FROM notes WHERE content=? AND date=?",new String[]{content, date});
            cursor.close();
        }
    }

