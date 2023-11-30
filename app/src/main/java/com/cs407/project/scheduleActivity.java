package com.cs407.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class scheduleActivity extends AppCompatActivity {
    private int noteId=-1;
    EditText editText;
    EditText editTextt;
    Button back;
    static ArrayList<schedule> notes1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        editText=(EditText) findViewById(R.id.editTextText3);
        editTextt=(EditText) findViewById(R.id.term);
        Intent intent = getIntent();
        Integer noteId = intent.getIntExtra("noteId",-1);
        if(noteId!=-1){
            schedule note=scheduleViewActivity.notes1.get(noteId);
            String noteContent=note.getContent();
            String noteTerm=note.getTerm();
            editText.setText(noteContent);
            editTextt.setText(noteTerm);
        }
        Button saveButton=findViewById(R.id.button2);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String con=editText.getText().toString();
                String term =editTextt.getText().toString();
                if (term.equals("Term")){
                    term="Undecided";
                }
                Context context=getApplicationContext();
                SQLiteDatabase sqLiteDatabase=context.openOrCreateDatabase("notes",Context.MODE_PRIVATE,null);
                DBHelper dbHelper=new DBHelper(sqLiteDatabase);
                String title;
                DateFormat dateFormat=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
                String date=dateFormat.format(new Date());
                if (noteId==-1){
                    title="Note_"+(scheduleViewActivity.notes1.size()+1);
                    dbHelper.saveNotes(term,title,date,con);
                }else{
                    title="Note_"+(noteId+1);
                    dbHelper.updateNotes(con,date,title,term);
                }
                Intent intent=new Intent(getApplicationContext(),scheduleViewActivity.class);
                startActivity(intent);
            }
        });

    }
    public void deleteFunction(View view){
        Context context=getApplicationContext();
        SQLiteDatabase sqLiteDatabase=context.openOrCreateDatabase("notes",Context.MODE_PRIVATE,null);
        DBHelper dbHelper=new DBHelper(sqLiteDatabase);
        String title="Note_"+(noteId+1);
        String con=editText.getText().toString();
        dbHelper.deleteNotes(con,title);
        Intent intent = new Intent(this, scheduleViewActivity.class);
        startActivity(intent);
    }


}