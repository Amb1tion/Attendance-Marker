package com.example.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class attendance_activity extends AppCompatActivity {
    private static final String TAG = "attendance activity";
    private AppDatabase appDatabase;
    private String course;
    private ListView mListView;
    private FloatingActionButton fab;
    private ArrayAdapter<String> adapter;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "com.example.android.attendance";
    private ArrayList<String> sections;
    private ArrayList<String> empty;
    private Boolean flag=false;

    private ArrayAdapter<String> section_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_act_layout);
        appDatabase = AppDatabase.getInstance(attendance_activity.this);
        mPreferences = attendance_activity.this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
        try {
            sections = (ArrayList<String>) ObjectSerializer.deserialize(mPreferences.getString("Sections", ObjectSerializer.serialize(new ArrayList<String>())));
            if (sections == null || sections.size() == 0){
                empty = new ArrayList<String>();
                this.empty.add("No Sections Added");
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        course = getIntent().getStringExtra("course");
        View fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                LayoutInflater li = LayoutInflater.from(attendance_activity.this);
                View promptsView = li.inflate(R.layout.section_prompt,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(attendance_activity.this);
                alertDialogBuilder.setView(promptsView);

                alertDialogBuilder.setTitle("Select Section");
                final AlertDialog alertDialog = alertDialogBuilder.create();
                final Spinner mSpinner = (Spinner) promptsView.findViewById(R.id.spinner);
                final Button mButton =  (Button) promptsView.findViewById((R.id.button2));
                if (flag){section_adapter = new ArrayAdapter<String>(attendance_activity.this,android.R.layout.simple_spinner_item,empty);}
                else{section_adapter = new ArrayAdapter<String>(attendance_activity.this,android.R.layout.simple_spinner_item,sections);}
                section_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                mSpinner.setAdapter(section_adapter);
                alertDialog.show();
                mButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.d(TAG,"i'm stuff");
                    }
                });

            }
        });
    }
}
